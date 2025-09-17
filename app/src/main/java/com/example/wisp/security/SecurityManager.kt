package com.example.wisp.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Security manager for handling encrypted storage and API key protection
 */
@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "wisp_encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    /**
     * Store sensitive data securely
     */
    fun storeSecureData(key: String, value: String) {
        encryptedPrefs.edit()
            .putString(key, value)
            .apply()
    }
    
    /**
     * Retrieve sensitive data securely
     */
    fun getSecureData(key: String): String? {
        return encryptedPrefs.getString(key, null)
    }
    
    /**
     * Remove sensitive data
     */
    fun removeSecureData(key: String) {
        encryptedPrefs.edit()
            .remove(key)
            .apply()
    }
    
    /**
     * Clear all secure data
     */
    fun clearAllSecureData() {
        encryptedPrefs.edit()
            .clear()
            .apply()
    }
    
    companion object {
        // Keys for secure storage
        const val KEY_USER_PREFERENCES = "user_preferences"
        const val KEY_ANALYTICS_ID = "analytics_id"
        const val KEY_LAST_SYNC_TIME = "last_sync_time"
        const val KEY_APP_FIRST_LAUNCH = "app_first_launch"
    }
}
