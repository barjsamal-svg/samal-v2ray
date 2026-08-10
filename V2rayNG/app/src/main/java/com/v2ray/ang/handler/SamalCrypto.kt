package com.v2ray.ang.handler

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object SamalCrypto {
    private const val MASTER_SEED = "SAMAL_V2RAY_ULTIMATE_SECURITY_999@libsammal_PRO_PROTECTION"
    private const val PBKDF2_ITERATIONS = 100000
    private const val KEY_LENGTH = 256
    private const val GCM_TAG_LENGTH = 128

    /**
     * Encrypts the configuration with expiry date and custom message.
     * Uses AES-256-GCM for military-grade security.
     */
    fun encryptConfig(plainText: String, expireDate: String, customMessage: String): String {
        try {
            // Payload format: EXPIRE|MSG|DATA
            val payload = "$expireDate|$customMessage|$plainText"
            val salt = ByteArray(16)
            SecureRandom().nextBytes(salt)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(MASTER_SEED.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val iv = ByteArray(12)
            SecureRandom().nextBytes(iv)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)

            val cipherText = cipher.doFinal(payload.toByteArray(Charsets.UTF_8))
            
            // Structure: [SALT(16)][IV(12)][CIPHERTEXT]
            val combined = ByteArray(salt.size + iv.size + cipherText.size)
            System.arraycopy(salt, 0, combined, 0, salt.size)
            System.arraycopy(iv, 0, combined, salt.size, iv.size)
            System.arraycopy(cipherText, 0, combined, salt.size + iv.size, cipherText.size)

            return "SAMAL:" + Base64.encodeToString(combined, Base64.NO_WRAP)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * Decrypts the .SAMAL file content.
     */
    fun decryptConfig(encryptedData: String): SamalResult? {
        try {
            if (!encryptedData.startsWith("SAMAL:")) return null
            val base64Data = encryptedData.substring(6)
            val decoded = Base64.decode(base64Data, Base64.NO_WRAP)

            if (decoded.size < 28) return null

            val salt = ByteArray(16)
            val iv = ByteArray(12)
            val cipherText = ByteArray(decoded.size - 28)

            System.arraycopy(decoded, 0, salt, 0, 16)
            System.arraycopy(decoded, 16, iv, 0, 12)
            System.arraycopy(decoded, 28, cipherText, 0, cipherText.size)

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(MASTER_SEED.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH)
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val parameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)

            val plainBytes = cipher.doFinal(cipherText)
            val fullPayload = String(plainBytes, Charsets.UTF_8)

            val parts = fullPayload.split("|", limit = 3)
            if (parts.size < 3) return null

            return SamalResult(
                expireDate = parts[0],
                customMessage = parts[1],
                data = parts[2]
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

data class SamalResult(
    val expireDate: String,
    val customMessage: String,
    val data: String
)
