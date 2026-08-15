package com.samal.v2ray.handler

import android.content.Context
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SamalCrypto {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private const val SALT_LENGTH_BYTE = 16
    private const val ITERATIONS = 100000
    private const val KEY_LENGTH = 256
    private const val MASTER_SECRET = "SAMAL_V2RAY_ULTRA_SECRET_KEY_2026_@libsammal"

    data class SamalResult(
        val success: Boolean,
        val decryptedJson: String = "",
        val message: String = "",
        val expiry: String = ""
    )

    fun encryptConfig(jsonConfig: String, message: String, expiry: String): String {
        try {
            val salt = ByteArray(SALT_LENGTH_BYTE).apply {
                java.security.SecureRandom().nextBytes(this)
            }
            val iv = ByteArray(IV_LENGTH_BYTE).apply {
                java.security.SecureRandom().nextBytes(this)
            }

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(MASTER_SECRET.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

            val payload = "MSG:$message|EXP:$expiry|DATA:$jsonConfig"
            val encrypted = cipher.doFinal(payload.toByteArray(StandardCharsets.UTF_8))

            val combined = ByteArray(salt.size + iv.size + encrypted.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(encrypted, 0, combined, salt.size + iv.size, encrypted.size)

            return "SAMALv1:" + Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("SamalCrypto", "Encryption failed", e)
            return ""
        }
    }

    fun exportSamalFile(context: Context, jsonConfig: String, message: String, expiry: String, profileName: String): File? {
        try {
            val encryptedContent = encryptConfig(jsonConfig, message, expiry)
            if (encryptedContent.isEmpty()) return null

            val sanitizedName = profileName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileName = "SAMAL_${sanitizedName}_${System.currentTimeMillis()}.samal"
            
            // Use external files dir or cache dir safely
            val dir = File(context.getExternalFilesDir(null) ?: context.filesDir, "samal_exports")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(dir, fileName)
            FileOutputStream(file).use {
                it.write(encryptedContent.toByteArray(StandardCharsets.UTF_8))
            }
            return file
        } catch (e: Exception) {
            Log.e("SamalCrypto", "Export samal file failed", e)
            return null
        }
    }

    fun decryptConfig(token: String): SamalResult {
        try {
            if (!token.startsWith("SAMALv1:")) {
                return SamalResult(true, decryptedJson = token)
            }
            val raw = token.substring(8)
            val decoded = Base64.decode(raw, Base64.NO_WRAP)

            if (decoded.size < SALT_LENGTH_BYTE + IV_LENGTH_BYTE) {
                return SamalResult(false, message = "Invalid token size")
            }

            val salt = ByteArray(SALT_LENGTH_BYTE)
            val iv = ByteArray(IV_LENGTH_BYTE)
            val encrypted = ByteArray(decoded.size - SALT_LENGTH_BYTE - IV_LENGTH_BYTE)

            System.arraycopy(decoded, 0, salt, 0, SALT_LENGTH_BYTE)
            System.arraycopy(decoded, SALT_LENGTH_BYTE, iv, 0, IV_LENGTH_BYTE)
            System.arraycopy(decoded, SALT_LENGTH_BYTE + IV_LENGTH_BYTE, encrypted, 0, encrypted.size)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(MASTER_SECRET.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            val decryptedBytes = cipher.doFinal(encrypted)
            val payload = String(decryptedBytes, StandardCharsets.UTF_8)

            var message = ""
            var expiry = ""
            var data = payload

            if (payload.contains("MSG:") && payload.contains("|EXP:") && payload.contains("|DATA:")) {
                val msgIndex = payload.indexOf("MSG:") + 4
                val expIndex = payload.indexOf("|EXP:")
                val dataIndex = payload.indexOf("|DATA:") + 6

                if (msgIndex in 4..expIndex) {
                    message = payload.substring(msgIndex, expIndex)
                }
                if (expIndex in 0..dataIndex) {
                    expiry = payload.substring(expIndex + 5, dataIndex - 6)
                }
                if (dataIndex in 6..payload.length) {
                    data = payload.substring(dataIndex)
                }
            }

            return SamalResult(true, decryptedJson = data, message = message, expiry = expiry)
        } catch (e: Exception) {
            Log.e("SamalCrypto", "Tampering detected or decryption failed", e)
            return SamalResult(false, message = "Tampered or invalid samal config")
        }
    }
}
