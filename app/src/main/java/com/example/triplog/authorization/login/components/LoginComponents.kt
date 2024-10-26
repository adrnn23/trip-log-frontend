package com.example.triplog.authorization.login.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.triplog.R

@Composable
fun ErrorDetails(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.onErrorContainer,
        fontSize = 12.sp
    )
}

@Composable
fun InformationDialog(
    @StringRes title: Int,
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    containerColor: @Composable () -> Color,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        title = { Text(stringResource(title)) },
        text = { text() },
        icon = { icon() },
        containerColor =  containerColor(),
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmClick() },
                shape = RoundedCornerShape(5.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.ok),
                )
            }
        }
    )
}

@Composable
fun LoginActivityButton(
    @StringRes text: Int,
    containerColor: Color,
    onClick: () -> Unit
) {
    Button(
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        onClick = {
            onClick()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = stringResource(text),
            fontSize = 18.sp,
            modifier = Modifier
                .padding(2.dp)
        )
    }
}

@Composable
fun LinearIndicator() {
    LinearProgressIndicator(
        modifier = Modifier.width(256.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Composable
fun ButtonDivider() {
    Spacer(modifier = Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(text = stringResource(R.string.or), modifier = Modifier.padding(horizontal = 8.dp))
        Divider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(10.dp))
}