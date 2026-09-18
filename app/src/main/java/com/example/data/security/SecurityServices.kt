package com.example.data.security

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

object PasswordHasher {
    private const val SALT = "ClassNotesHub_Secured_Academic_Salt_2026"

    fun hashPassword(password: String): String {
        val input = "$SALT:$password"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(rawPassword: String, storedHash: String): Boolean {
        return hashPassword(rawPassword) == storedHash
    }
}

data class FileValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val fileExtension: String = "",
    val formattedSize: String = ""
)

class FileStorageService(private val context: Context) {
    private val allowedExtensions = setOf(
        "pdf", "doc", "docx", "ppt", "pptx",
        "xls", "xlsx", "jpg", "jpeg", "png", "webp", "txt"
    )
    private val maxSizeBytes = 25 * 1024 * 1024L // 25 MB

    private val storageDir: File by lazy {
        val dir = File(context.filesDir, "study_materials")
        if (!dir.exists()) dir.mkdirs()
        dir
    }

    fun validateFile(fileName: String, sizeBytes: Long): FileValidationResult {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext.isEmpty() || !allowedExtensions.contains(ext)) {
            return FileValidationResult(
                isValid = false,
                errorMessage = "Unsupported file type: .$ext. Allowed: PDF, DOC, DOCX, PPT, PPTX, XLS, XLSX, JPG, PNG, WEBP, TXT",
                fileExtension = ext
            )
        }
        if (sizeBytes > maxSizeBytes) {
            return FileValidationResult(
                isValid = false,
                errorMessage = "File size exceeds 25 MB limit (${formatFileSize(sizeBytes)})",
                fileExtension = ext,
                formattedSize = formatFileSize(sizeBytes)
            )
        }
        return FileValidationResult(
            isValid = true,
            fileExtension = ext.uppercase(),
            formattedSize = formatFileSize(sizeBytes)
        )
    }

    fun saveContentToFile(fileName: String, content: ByteArray): String {
        val cleanName = fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
        val uniqueName = "${System.currentTimeMillis()}_$cleanName"
        val destination = File(storageDir, uniqueName)
        FileOutputStream(destination).use { out ->
            out.write(content)
        }
        return destination.absolutePath
    }

    fun getFile(path: String): File? {
        val file = File(path)
        return if (file.exists()) file else null
    }

    fun formatFileSize(sizeBytes: Long): String {
        val kb = sizeBytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1.0 -> "%.1f MB".format(mb)
            kb >= 1.0 -> "%.1f KB".format(kb)
            else -> "$sizeBytes B"
        }
    }
}
