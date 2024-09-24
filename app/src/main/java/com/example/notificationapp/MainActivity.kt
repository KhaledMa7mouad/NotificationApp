package com.example.notificationapp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.notificationapp.ui.theme.NotificationAppTheme

class MainActivity : ComponentActivity() {

    private var isDialogShown = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val handeler = handelPermissionResponse()
        createNotificationChannel()

        setContent {

            if (isDialogShown.value)
                PermissionDeniedDialog {    // Body of the callback function when you want to call it
                    isDialogShown.value = false
                }

            NotificationAppTheme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                                handeler.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            else {
                                sendNotification()
                            }

                        }
                    ) {
                        Text(text = "NotifyMe!")
                    }

                }

            }
        }
    }

    private fun handelPermissionResponse(): ActivityResultLauncher<String> {
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

                if (isGranted) {
                    sendNotification()
                } else {
                    isDialogShown.value = true
                }

            }
        return launcher

    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("1", "General Notification", importance)
        channel.description = "Display general notification for the application"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

    }

    @SuppressLint("MissingPermission")
    private fun sendNotification() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.compose_quote)
        val link = "https://developer.android.com/compose".toUri()
        val i = Intent(Intent.ACTION_VIEW, link)
        val pendingIntent = PendingIntent
            .getActivity(this, 101, i, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "1")
            .setSmallIcon(R.drawable.ic_flower)
            .setContentTitle("New Notification")
            .setContentText("Smell the Rose , you using Compose!")
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(this).notify(99, notification)
    }

    @Composable
    fun PermissionDeniedDialog(OnDialogShown: () -> Unit) {    // callback function
        AlertDialog(
            onDismissRequest = { OnDialogShown() }, //cancelable = false  the app will freeze until the dialog is dismissed
            confirmButton = {
                TextButton(onClick = {
                   val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    i.data = Uri.fromParts("package", packageName, null)
                    startActivity(i)
                    OnDialogShown()
                }

                )

                {
                    Text(text = "Allow")
                }
            }, // positive button
            dismissButton = {
                TextButton(onClick = { OnDialogShown() }) {
                    Text(text = "Cancel")
                }
            }, // negative button
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = "Warning",
                    tint = LocalContentColor.current
                )
            },
            title = {
                Text(text = "We need permission to send notifications")
            },
            text = {
                Text(text = "The application relies on sending notifications. We require access to this permission to help you receive notifications about jetpack compose")
            },
        )

    }

}

