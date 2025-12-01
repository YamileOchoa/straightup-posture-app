package com.proyecto.straightupapp.ui.components.devicecontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.proyecto.straightupapp.bluetooth.BleManager
import com.proyecto.straightupapp.bluetooth.ScanResultStatus

@Composable
fun ControlButtonsSection(
    isScanning: Boolean,
    bleManager: BleManager,
    onScanResult: (ScanResultStatus, String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                if (isScanning) {
                    bleManager.stopScan()
                    onScanResult(ScanResultStatus.ScanStarted, "")
                } else {
                    val status = bleManager.startScan()
                    val message = when (status) {
                        ScanResultStatus.BleDisabled -> "Por favor, activa el Bluetooth"
                        ScanResultStatus.NoAdapter -> "Este dispositivo no tiene Bluetooth"
                        ScanResultStatus.ScanStarted -> ""
                    }
                    onScanResult(status, message)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = if (isScanning) Icons.Default.Close else Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isScanning) "Detener Escaneo" else "Buscar Dispositivo")
        }

        if (isScanning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Buscando POSTURA-ESP32...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}