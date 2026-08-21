package com.Tvman4.QuestFiles.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier.modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tvman4.QuestFiles.file.FileUtils
import com.Tvman4.QuestFiles.injector.SoInjector
import java.io.File

@Composable
fun InjectorTab(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var apkPath by remember { mutableStateOf("") }
    var soPath by remember { mutableStateOf("") }
    var constString by remember { mutableStateOf("Tvman4_QuestFiles_Injected") }
    var status by remember { mutableStateOf("Ready") }
    var isWorking by remember { mutableStateOf(false) }

    // Quick list of APKs found under Android/data
    var foundApks by remember { mutableStateOf(emptyList<File>()) }

    fun scanAndroidData() {
        val dataDir = File("/storage/emulated/0/Android/data")
        if (!dataDir.exists()) {
            status = "Cannot access /Android/data – grant All Files Access"
            return
        }
        val apks = mutableListOf<File>()
        dataDir.listFiles()?.forEach { pkgDir ->
            // Look for common places people drop APKs
            listOf(
                File(pkgDir, "files"),
                File(pkgDir, "cache"),
                pkgDir
            ).forEach { dir ->
                dir.listFiles()?.filter { it.extension.equals("apk", true) }?.let {
                    apks.addAll(it)
                }
            }
        }
        // Also scan Download
        File("/storage/emulated/0/Download").listFiles()
            ?.filter { it.extension.equals("apk", true) }
            ?.let { apks.addAll(it) }

        foundApks = apks.sortedByDescending { it.lastModified() }
        status = "Found ${foundApks.size} APKs"
    }

    LaunchedEffect(Unit) {
        scanAndroidData()
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("SO Injector", style = MaterialTheme.typography.titleLarge, color = Color(0xFF00E5FF))
        Text(
            "• SO must start with \"lib\" (example: libmodmenu.so)\n" +
            "• Will be placed in lib/arm64-v8a/ and lib/armeabi-v7a/\n" +
            "• Const string is written into the APK as a marker",
            fontSize = 13.sp,
            color = Color.Gray
        )

        OutlinedTextField(
            value = apkPath,
            onValueChange = { apkPath = it },
            label = { Text("Target APK path") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
        )

        OutlinedTextField(
            value = soPath,
            onValueChange = { soPath = it },
            label = { Text("libXXX.so path (must start with lib)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp)
        )

        OutlinedTextField(
            value = constString,
            onValueChange = { constString = it },
            label = { Text("Const string (written into APK)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    isWorking = true
                    status = "Injecting..."
                    try {
                        val apk = File(apkPath)
                        val so = File(soPath)
                        val outDir = File("/storage/emulated/0/Download/QuestFiles_Injected")
                        val result = SoInjector.inject(apk, so, constString, outDir)
                        if (result != null) {
                            status = "SUCCESS → ${result.absolutePath}"
                            Toast.makeText(context, "Injected! Check Download/QuestFiles_Injected", Toast.LENGTH_LONG).show()
                        } else {
                            status = "FAILED – check logcat / SO name must start with lib"
                        }
                    } catch (e: Exception) {
                        status = "Error: ${e.message}"
                    } finally {
                        isWorking = false
                    }
                },
                enabled = !isWorking && apkPath.isNotBlank() && soPath.isNotBlank()
            ) {
                Text(if (isWorking) "Working..." else "INJECT .SO")
            }

            OutlinedButton(onClick = { scanAndroidData() }) {
                Text("Rescan Android/data")
            }
        }

        Text(status, color = if (status.startsWith("SUCCESS")) Color(0xFF00E676) else Color.White)

        Divider(color = Color.DarkGray)

        Text("APKs found under Android/data + Download:", color = Color.Gray, fontSize = 13.sp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(foundApks) { apk ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { apkPath = apk.absolutePath }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📦", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(apk.name, fontSize = 13.sp)
                        Text(
                            apk.absolutePath,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
