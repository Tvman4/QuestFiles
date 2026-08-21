package com.Tvman4.QuestFiles.file

import java.io.File

data class FileItem(
    val file: File,
    val name: String,
    val isDirectory: Boolean,
    val isHidden: Boolean,
    val size: Long,
    val lastModified: Long
)

object FileUtils {

    fun listFiles(path: String, showHidden: Boolean): List<FileItem> {
        val dir = File(path)
        if (!dir.exists() || !dir.isDirectory) return emptyList()

        val files = dir.listFiles() ?: return emptyList()

        return files
            .filter { showHidden || !it.name.startsWith(".") }
            .map {
                FileItem(
                    file = it,
                    name = it.name,
                    isDirectory = it.isDirectory,
                    isHidden = it.name.startsWith("."),
                    size = if (it.isFile) it.length() else 0L,
                    lastModified = it.lastModified()
                )
            }
            .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
    }

    fun formatSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "%.1f KB".format(kb)
        val mb = kb / 1024.0
        if (mb < 1024) return "%.1f MB".format(mb)
        val gb = mb / 1024.0
        return "%.2f GB".format(gb)
    }

    fun getCommonRoots(): List<Pair<String, String>> {
        return listOf(
            "Internal" to "/storage/emulated/0",
            "Android/data" to "/storage/emulated/0/Android/data",
            "Android/obb" to "/storage/emulated/0/Android/obb",
            "Download" to "/storage/emulated/0/Download",
            "Oculus" to "/storage/emulated/0/Oculus",
            "Root (limited)" to "/"
        )
    }
}
