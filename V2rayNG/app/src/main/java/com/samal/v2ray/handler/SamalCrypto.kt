
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
    private const val ITERATIONS = 150000 // Increased iterations for fortress security
    private const val MASTER_SECRET = "SAMAL_FORTRESS_ULTRA_SECRET_KEY_2026_@libsammal_IQNET"

    data class SamalResult(
        val success: Boolean,
        val decryptedJson: String = "",
        val message: String = "",
        val expiry: String = "",
        val isLocked: Boolean = false
    )

    fun encryptConfig(jsonConfig: String, message: String, expiry: String, isLocked: Boolean): String {
        try {
            val salt = ByteArray(SALT_LENGTH_BYTE).apply {
                java.security.SecureRandom().nextBytes(this)
            }
            val iv = ByteArray(IV_LENGTH_BYTE).apply {
                java.security.SecureRandom().nextBytes(this)
            }

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(MASTER_SECRET.toCharArray(), salt, ITERATIONS, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

            val lockFlag = if (isLocked) "LOCKED:1" else "LOCKED:0"
            val rawPayload = "LOCK:$lockFlag|MSG:$message|EXP:$expiry|DATA:$jsonConfig"
            
            // Fortress Multi-layer scrambling if locked
            val payloadBytes = if (isLocked) {
                // Apply a secondary XOR scrambling layer
                val b = rawPayload.toByteArray(StandardCharsets.UTF_8)
                for (i in b.indices) {
                    b[i] = (b[i].toInt() xor (0x5A + (i % 13))).toByte()
                }
                b
            } else {
                rawPayload.toByteArray(StandardCharsets.UTF_8)
            }

            val encrypted = cipher.doFinal(payloadBytes)

            val combined = ByteArray(salt.size + iv.size + encrypted.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(encrypted, 0, combined, salt.size + iv.size, encrypted.size)

            val prefix = if (isLocked) "SAMAL_LOCK_v2:" else "SAMALv1:"
            return prefix + Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("SamalCrypto", "Encryption failed", e)
            return ""
        }
    }

    fun exportSamalFile(context: Context, jsonConfig: String, message: String, expiry: String, profileName: String, isLocked: Boolean): File? {
        try {
            val encryptedContent = encryptConfig(jsonConfig, message, expiry, isLocked)
            if (encryptedContent.isEmpty()) return null

            val sanitizedName = profileName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileName = "SAMAL_${sanitizedName}_${System.currentTimeMillis()}.samal"
            
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
            val isLockedFormat = token.startsWith("SAMAL_LOCK_v2:")
            if (!isLockedFormat && !token.startsWith("SAMALv1:")) {
                return SamalResult(true, decryptedJson = token, isLocked = false)
            }
            val raw = if (isLockedFormat) token.substring(14) else token.substring(8)
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
            val spec = PBEKeySpec(MASTER_SECRET.toCharArray(), salt, ITERATIONS, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance(ALGORITHM)
            val gcmSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

            val decryptedBytes = cipher.doFinal(encrypted)
            
            val finalBytes = if (isLockedFormat) {
                for (i in decryptedBytes.indices) {
                    decryptedBytes[i] = (decryptedBytes[i].toInt() xor (0x5A + (i % 13))).toByte()
                }
                decryptedBytes
            } else {
                decryptedBytes
            }

            val payload = String(finalBytes, StandardCharsets.UTF_8)
            
            var isLocked = isLockedFormat
            var message = ""
            var expiry = ""
            var jsonConfig = payload

            if (payload.contains("|DATA:")) {
                val parts = payload.split("|DATA:")
                val header = parts[0]
                jsonConfig = if (parts.size > 1) parts[1] else ""
                
                for (item in header.split("|")) {
                    if (item.startsWith("LOCK:1")) isLocked = true
                    if (item.startsWith("MSG:")) message = item.substring(4)
                    if (item.startsWith("EXP:")) expiry = item.substring(4)
                }
            }

            return SamalResult(
                success = true,
                decryptedJson = jsonConfig,
                message = message,
                expiry = expiry,
                isLocked = isLocked
            )
        } catch (e: Exception) {
            Log.e("SamalCrypto", "Decryption failed - File is locked or corrupted", e)
            return SamalResult(success = false, message = "الملف مقفل تشفيرياً أو تالف! لا يمكن فكه.")
        }
    }
}
