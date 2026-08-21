package com.Tvman4.QuestFiles.injector

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object SoInjector {

    private const val TAG = "SoInjector"

    /**
     * Injects a .so that starts with "lib" into the APK's lib/arm64-v8a/ folder.
     * Also writes the provided constString into a small marker file inside the APK.
     *
     * @return path to the new patched APK or null on failure
     */
    fun inject(
        apkFile: File,
        soFile: File,
        constString: String,
        outputDir: File
    ): File? {
        if (!soFile.name.startsWith("lib") || !soFile.name.endsWith(".so")) {
            Log.e(TAG, "SO must start with 'lib' and end with '.so'  (got: ${soFile.name})")
            return null
        }

        if (!apkFile.exists() || !soFile.exists()) {
            Log.e(TAG, "APK or SO file does not exist")
            return null
        }

        outputDir.mkdirs()
        val outApk = File(outputDir, apkFile.nameWithoutExtension + "_injected.apk")

        try {
            ZipFile(apkFile).use { zipIn ->
                ZipOutputStream(BufferedOutputStream(FileOutputStream(outApk))).use { zipOut ->

                    // 1. Copy everything except we will overwrite / add the lib later
                    val entries = zipIn.entries()
                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        // Skip existing same-name SO if present (we will put the new one)
                        if (entry.name == "lib/arm64-v8a/${soFile.name}" ||
                            entry.name == "lib/armeabi-v7a/${soFile.name}") {
                            continue
                        }
                        zipOut.putNextEntry(ZipEntry(entry.name))
                        if (!entry.isDirectory) {
                            zipIn.getInputStream(entry).use { input ->
                                input.copyTo(zipOut)
                            }
                        }
                        zipOut.closeEntry()
                    }

                    // 2. Add the .so into lib/arm64-v8a/
                    val soEntryName = "lib/arm64-v8a/${soFile.name}"
                    zipOut.putNextEntry(ZipEntry(soEntryName))
                    FileInputStream(soFile).use { input ->
                        input.copyTo(zipOut)
                    }
                    zipOut.closeEntry()

                    // Also put a copy in armeabi-v7a for older devices (optional but safe)
                    val soEntryName32 = "lib/armeabi-v7a/${soFile.name}"
                    zipOut.putNextEntry(ZipEntry(soEntryName32))
                    FileInputStream(soFile).use { input ->
                        input.copyTo(zipOut)
                    }
                    zipOut.closeEntry()

                    // 3. Put the const string as a marker file inside the APK
                    //    (assets/questfiles_const.txt) so it is easy to verify later
                    val markerEntry = ZipEntry("assets/questfiles_const.txt")
                    zipOut.putNextEntry(markerEntry)
                    zipOut.write(constString.toByteArray(Charsets.UTF_8))
                    zipOut.closeEntry()

                    // Also put a tiny binary-friendly marker next to the SO
                    val markerEntry2 = ZipEntry("lib/arm64-v8a/questfiles_marker.txt")
                    zipOut.putNextEntry(markerEntry2)
                    zipOut.write("QuestFiles injected\nconst=$constString\n".toByteArray())
                    zipOut.closeEntry()
                }
            }

            Log.i(TAG, "Injection successful → ${outApk.absolutePath}")
            return outApk
        } catch (e: Exception) {
            Log.e(TAG, "Injection failed", e)
            outApk.delete()
            return null
        }
    }
}
