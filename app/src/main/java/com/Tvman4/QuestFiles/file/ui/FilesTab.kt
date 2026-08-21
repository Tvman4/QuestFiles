package com.Tvman4.QuestFiles.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier.modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Tvman4.QuestFiles.file.FileItem
import com.Tvman4.QuestFiles.file.FileUtils
import java.io.File

@Composable
fun FilesTab(modifier: Modifier = Modifier) {
    var currentPath by remember { mutableStateOf("/storage/emulated/0") }
    var showHidden by remember { mutableStateOf(true) }
    var files by remember { mutableStateOf(emptyList<FileItem>()) }
    var error by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        try {
            files = FileUtils.listFiles(currentPath, showHidden)
            error = null
        } catch (e: Exception) {
            error = e.message
            files = emptyList()
        }
    }

    LaunchedEffect(currentPath, showHidden) {
        refresh()
    }

    Column(modifier = modifier.padding(12.dp)) {

        // Quick roots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FileUtils.getCommonRoots().forEach { (label, path) ->
                FilterChip(
                    selected = currentPath == path,
                    onClick = { currentPath = path },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }

        Spacer(Modifier = Modifier.height(8.dp))

        // Path bar + controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = currentPath,
                onValueChange = { currentPath = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 13.sp),
                label = { Text("Current path") }
            )
            Spacer(Modifier = Modifier.width(8.dp))
            Button(onClick = { refresh() }) { Text("Go") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = showHidden,
                onCheckedChange = { showHidden = it }
            )
            Text("Show hidden files", modifier = Modifier.padding(start = 8.dp))
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = {
                val parent = File(currentPath).parent
                if (parent != null) currentPath = parent
            }) {
                Text("⬆ Up")
            }
        }

        if (error != null) {
            Text(
                text = "Error: $error",
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(files) { item ->
                FileRow(item = item) {
                    if (item.isDirectory) {
                        currentPath = item.file.absolutePath
                    }
                }
            }
        }
    }
}

@Composable
fun FileRow(item: FileItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (item.isDirectory) "📁" else "📄",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name + if (item.isHidden) " (hidden)" else "",
                color = if (item.isHidden) Color(0xFFAAAAAA) else Color.White,
                fontSize = 14.sp
            )
            if (!item.isDirectory) {
                Text(
                    text = FileUtils.formatSize(item.size),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
        if (item.isDirectory) {
            Text("›", color = Color.Gray, fontSize = 18.sp)
        }
    }
}
