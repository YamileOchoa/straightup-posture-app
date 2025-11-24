package com.proyecto.straightupapp.bluetooth

import android.annotation.SuppressLint
import android.content.Context
import java.util.UUID
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import kotlinx.coroutines.flow.MutableStateFlow
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanFilter
import android.util.Log
import android.os.ParcelUuid
import android.bluetooth.le.ScanResult
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGattDescriptor
import java.nio.charset.Charset

sealed class ScanResultStatus {
    object ScanStarted : ScanResultStatus()
    object BleDisabled : ScanResultStatus()
    object NoAdapter : ScanResultStatus()
}

class BleManager(private val context: Context) {
    companion object {
        private const val TAG = "BleManager"

        // UUIDs que coinciden con el ESP32
        val SERVICE_UUID: UUID = UUID.fromString("12345678-1234-1234-1234-1234567890ab")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("abcd1234-5678-90ab-cdef-1234567890ab")
        val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        const val TARGET_NAME = "POSTURA-ESP32"
    }

    @SuppressLint("MissingPermission")
    private val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanCallback: ScanCallback? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var notifyCharacteristic: BluetoothGattCharacteristic? = null

    // State flows para la UI
    val isScanning = MutableStateFlow(false)
    val connectedDeviceName = MutableStateFlow<String?>(null)
    val isConnected = MutableStateFlow(false)
    val lastPayload = MutableStateFlow<String?>(null)
    val scanLog = MutableStateFlow<List<String>>(emptyList()) // Para debugging

    // Callbacks para manejo de eventos
    var onAlert: (() -> Unit)? = null
    var onOk: (() -> Unit)? = null

    private fun addToLog(message: String) {
        val currentLog = scanLog.value.toMutableList()
        currentLog.add(0, "${System.currentTimeMillis()}: $message")
        if (currentLog.size > 20) currentLog.removeAt(currentLog.size - 1)
        scanLog.value = currentLog
        Log.d(TAG, message)
    }

    fun ensureBleAvailable(): Boolean {
        @SuppressLint("MissingPermission")
        val isEnabled = bluetoothAdapter?.isEnabled ?: false
        return bluetoothAdapter != null && isEnabled
    }

    @SuppressLint("MissingPermission")
    fun startScan(): ScanResultStatus {
        if (bluetoothAdapter == null) {
            addToLog("❌ No hay adaptador Bluetooth")
            return ScanResultStatus.NoAdapter
        }

        if (!bluetoothAdapter.isEnabled) {
            addToLog("❌ Bluetooth desactivado")
            return ScanResultStatus.BleDisabled
        }

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothLeScanner == null) {
            addToLog("❌ No se pudo obtener el scanner")
            return ScanResultStatus.NoAdapter
        }

        if (scanCallback != null) {
            addToLog("⚠️ Ya hay un escaneo en curso")
            return ScanResultStatus.ScanStarted
        }

        // Configuración de escaneo MÁS agresiva
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
            .setReportDelay(0)
            .build()

        // Filtros de búsqueda
        val filters = mutableListOf<ScanFilter>()

        // Buscar por nombre Y por UUID (doble verificación)
        filters.add(
            ScanFilter.Builder()
                .setDeviceName(TARGET_NAME)
                .build()
        )

        filters.add(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(SERVICE_UUID))
                .build()
        )

        addToLog("🔍 Buscando: $TARGET_NAME con UUID: ${SERVICE_UUID}")

        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                result?.let { processScanResult(it) }
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                results?.forEach { processScanResult(it) }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                val errorMsg = when (errorCode) {
                    SCAN_FAILED_ALREADY_STARTED -> "Escaneo ya iniciado"
                    SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Fallo en registro de aplicación"
                    SCAN_FAILED_FEATURE_UNSUPPORTED -> "Característica no soportada"
                    SCAN_FAILED_INTERNAL_ERROR -> "Error interno"
                    else -> "Error desconocido: $errorCode"
                }
                addToLog("❌ Fallo en escaneo: $errorMsg")
                isScanning.value = false
            }
        }

        bluetoothLeScanner?.startScan(filters, settings, scanCallback)
        isScanning.value = true
        addToLog("🔍 Escaneo iniciado...")
        return ScanResultStatus.ScanStarted
    }

    @SuppressLint("MissingPermission")
    private fun processScanResult(result: ScanResult) {
        val device = result.device
        val scanRecord = result.scanRecord

        // Obtener el nombre del dispositivo de múltiples fuentes
        val deviceName = scanRecord?.deviceName ?: device.name ?: "Sin nombre"
        val address = device.address
        val rssi = result.rssi

        // Log BÁSICO para cada dispositivo encontrado
        addToLog("📱 ${deviceName} | ${address} | ${rssi}dBm")

        // Log DETALLADO solo para dispositivos con nombre
        if (deviceName != "Sin nombre") {
            val serviceUuids = scanRecord?.serviceUuids?.joinToString(", ") { it.uuid.toString() } ?: "Sin UUIDs"
            addToLog("   → UUIDs: $serviceUuids")
        }

        // Buscar nuestro dispositivo por NOMBRE O UUID
        val isTargetByName = deviceName.contains(TARGET_NAME, ignoreCase = true)
        val isTargetByUuid = scanRecord?.serviceUuids?.any { it.uuid == SERVICE_UUID } == true

        if (isTargetByName || isTargetByUuid) {
            addToLog("✅ ¡DISPOSITIVO OBJETIVO ENCONTRADO!")
            addToLog("   Nombre: $deviceName")
            addToLog("   MAC: $address")
            addToLog("   RSSI: $rssi dBm")
            if (isTargetByUuid) {
                addToLog("   Coincide por UUID ✓")
            }
            if (isTargetByName) {
                addToLog("   Coincide por NOMBRE ✓")
            }

            stopScan()
            connectToDevice(device)
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner?.let { scanner ->
            scanCallback?.let {
                scanner.stopScan(it)
                addToLog("⏹️ Escaneo detenido")
            }
        }
        scanCallback = null
        isScanning.value = false
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        addToLog("🔗 Conectando a ${device.address}...")
        bluetoothGatt?.close()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    addToLog("✅ Conectado al servidor GATT")
                    isConnected.value = true
                    connectedDeviceName.value = gatt?.device?.name ?: gatt?.device?.address
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    addToLog("❌ Desconectado del servidor GATT (status: $status)")
                    isConnected.value = false
                    connectedDeviceName.value = null
                    notifyCharacteristic = null
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                addToLog("🔍 Servicios descubiertos")

                // Listar todos los servicios encontrados
                gatt?.services?.forEach { service ->
                    addToLog("   Servicio: ${service.uuid}")
                }

                val service = gatt?.getService(SERVICE_UUID)
                if (service == null) {
                    addToLog("❌ Servicio no encontrado: $SERVICE_UUID")
                    return
                }

                val char = service.getCharacteristic(CHARACTERISTIC_UUID)
                if (char == null) {
                    addToLog("❌ Característica no encontrada: $CHARACTERISTIC_UUID")
                    return
                }

                notifyCharacteristic = char
                addToLog("✅ Característica encontrada")

                // Habilitar notificaciones
                val success = gatt.setCharacteristicNotification(char, true)
                addToLog("📢 Notificación local ${if (success) "habilitada" else "falló"}")

                // Escribir en el descriptor CCCD
                val descriptor = char.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                if (descriptor != null) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    val writeSuccess = gatt.writeDescriptor(descriptor)
                    addToLog("✍️ Escritura del descriptor ${if (writeSuccess) "iniciada" else "falló"}")
                } else {
                    addToLog("⚠️ Descriptor CCCD no encontrado")
                }
            } else {
                addToLog("❌ Descubrimiento de servicios falló: $status")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                addToLog("✅ Notificaciones habilitadas en el dispositivo")
            } else {
                addToLog("❌ Error al habilitar notificaciones: $status")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = characteristic?.value?.toString(Charset.forName("UTF-8")) ?: "unknown"
                addToLog("✅ Comando escrito exitosamente: $value")
            } else {
                addToLog("❌ Error al escribir comando: status=$status")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
            characteristic?.value?.let { bytes ->
                val payload = bytes.toString(Charset.forName("UTF-8")).trim()
                addToLog("📨 Dato recibido: $payload")
                lastPayload.value = payload
                handlePayload(payload)
            }
        }
    }

    private fun handlePayload(payload: String) {
        addToLog("🔔 Procesando: '$payload'")

        when {
            payload.contains("ALERTA", ignoreCase = true) -> {
                addToLog("⚠️ ALERTA DE MALA POSTURA")
                onAlert?.invoke()
            }
            payload.contains("OK", ignoreCase = true) -> {
                addToLog("✅ POSTURA CORRECTA")
                onOk?.invoke()
            }
            payload.contains("Test", ignoreCase = true) -> {
                addToLog("📊 Mensaje de prueba recibido")
            }
            else -> {
                addToLog("❓ Mensaje desconocido: '$payload'")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        addToLog("🔌 Desconectando...")

        // 1. Deshabilitar notificaciones ANTES de desconectar
        notifyCharacteristic?.let { char ->
            try {
                bluetoothGatt?.setCharacteristicNotification(char, false)

                val descriptor = char.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                descriptor?.let {
                    it.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                    bluetoothGatt?.writeDescriptor(it)
                }
            } catch (e: Exception) {
                addToLog("⚠️ Error al deshabilitar notificaciones: ${e.message}")
            }
        }

        // 2. Pequeña pausa para que se procese
        Thread.sleep(200)

        // 3. Desconectar
        bluetoothGatt?.disconnect()

        // 4. CRÍTICO: Esperar a que Android procese la desconexión
        Thread.sleep(300)

        // 5. CRÍTICO: Limpiar caché BLE (fuerza a Android a re-escanear)
        try {
            val refreshMethod = bluetoothGatt?.javaClass?.getMethod("refresh")
            refreshMethod?.invoke(bluetoothGatt)
            addToLog("🔄 Caché BLE limpiado")
        } catch (e: Exception) {
            addToLog("⚠️ No se pudo limpiar caché: ${e.message}")
        }

        // 6. Cerrar conexión GATT completamente
        bluetoothGatt?.close()
        bluetoothGatt = null
        notifyCharacteristic = null

        addToLog("✅ Desconexión completa")
    }

    @SuppressLint("MissingPermission")
    fun writeString(s: String) {
        val char = notifyCharacteristic
        if (char == null) {
            addToLog("❌ No hay característica para escribir")
            return
        }

        try {
            // Verificar que la característica tenga la propiedad WRITE
            val properties = char.properties
            if ((properties and BluetoothGattCharacteristic.PROPERTY_WRITE) == 0 &&
                (properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
                addToLog("❌ La característica no soporta escritura")
                return
            }

            // Establecer el valor
            val bytes = s.toByteArray(Charset.forName("UTF-8"))
            char.setValue(bytes)

            // IMPORTANTE: Usar WRITE_TYPE_DEFAULT para comandos importantes
            char.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

            // Escribir
            val success = bluetoothGatt?.writeCharacteristic(char) ?: false

            if (success) {
                addToLog("✍️ Comando '$s' enviado al ESP32")
            } else {
                addToLog("❌ Fallo al enviar '$s'")
            }
        } catch (e: Exception) {
            addToLog("❌ Error al escribir: ${e.message}")
        }
    }
}