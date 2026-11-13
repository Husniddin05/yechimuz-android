package uz.yechim.app.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class KeystoreManager(context: Context) {

    private val prefs = context.getSharedPreferences("yechimuz_sec_prefs", Context.MODE_PRIVATE)
    private val keyAlias = "YechimUzMasterKeyAlias"
    private val androidKeyStore = "AndroidKeyStore"
    private val transformation = "AES/GCM/NoPadding"

    init {
        createKeyIfNeeded()
    }

    private fun createKeyIfNeeded() {
        val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeyStore)
            val spec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
        return (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
    }

    fun saveToken(token: String) {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(token.toByteArray(Charsets.UTF_8))

        prefs.edit()
            .putString("enc_token", Base64.encodeToString(encrypted, Base64.NO_WRAP))
            .putString("enc_iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()
    }

    fun getToken(): String? {
        val encToken = prefs.getString("enc_token", null) ?: return null
        val encIv = prefs.getString("enc_iv", null) ?: return null

        return try {
            val iv = Base64.decode(encIv, Base64.NO_WRAP)
            val cipherText = Base64.decode(encToken, Base64.NO_WRAP)
            val cipher = Cipher.getInstance(transformation)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    fun saveUserSession(userId: String, email: String, fullName: String) {
        prefs.edit()
            .putString("session_user_id", userId)
            .putString("session_email", email)
            .putString("session_name", fullName)
            .apply()
    }

    fun getUserId(): String? = prefs.getString("session_user_id", null)
    fun getUserEmail(): String? = prefs.getString("session_email", null)
    fun getUserName(): String? = prefs.getString("session_name", null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken() != null
}
