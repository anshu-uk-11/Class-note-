package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.security.FileStorageService
import com.example.data.security.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Class Notes Hub", appName)
    }

    @Test
    fun `password hashing and verification works`() {
        val rawPassword = "Admin@123"
        val hash = PasswordHasher.hashPassword(rawPassword)
        assertTrue(PasswordHasher.verifyPassword(rawPassword, hash))
        assertFalse(PasswordHasher.verifyPassword("WrongPassword", hash))
    }

    @Test
    fun `file validation accepts valid academic extensions and enforces size limit`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = FileStorageService(context)

        val validPdf = storage.validateFile("lecture1.pdf", 5 * 1024 * 1024)
        assertTrue(validPdf.isValid)
        assertEquals("PDF", validPdf.fileExtension)

        val validPpt = storage.validateFile("presentation.pptx", 10 * 1024 * 1024)
        assertTrue(validPpt.isValid)
        assertEquals("PPTX", validPpt.fileExtension)

        val oversized = storage.validateFile("giant_archive.pdf", 30 * 1024 * 1024)
        assertFalse(oversized.isValid)

        val forbiddenExt = storage.validateFile("malicious.exe", 1024)
        assertFalse(forbiddenExt.isValid)
    }
}
