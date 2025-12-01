package com.proyecto.straightupapp.ui.components.devicecontrol


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.proyecto.straightupapp.bluetooth.BleManager

@Composable
fun ConnectedControlsCard(
    bleManager: BleManager,
    onShutdownClicked: () -> Unit,
    onRestartClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Control Remoto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Botón de apagar ESP32
            Button(
                onClick = onShutdownClicked, // Usa el callback
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apagar Dispositivo ESP32")
            }

            // Botón de reiniciar ESP32
            OutlinedButton(
                onClick = onRestartClicked, // Usa el callback
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar Dispositivo")
            }

            // Botón de desconectar
            OutlinedButton(
                onClick = { bleManager.disconnect() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.BluetoothDisabled, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Desconectar")
            }
        }
    }
}