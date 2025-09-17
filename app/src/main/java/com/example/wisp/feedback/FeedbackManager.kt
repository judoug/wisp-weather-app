package com.example.wisp.feedback

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.wisp.analytics.AnalyticsManager
import com.example.wisp.security.SecurityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * User feedback and support manager
 */
@Singleton
class FeedbackManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsManager: AnalyticsManager,
    private val securityManager: SecurityManager
) {
    
    private val feedbackScope = CoroutineScope(Dispatchers.IO)
    
    /**
     * Submit user feedback
     */
    fun submitFeedback(
        feedback: String,
        rating: Int? = null,
        category: FeedbackCategory = FeedbackCategory.GENERAL,
        userEmail: String? = null
    ) {
        feedbackScope.launch {
            // Log feedback submission
            analyticsManager.logEvent("feedback_submitted", mapOf(
                "category" to category.name,
                "rating" to (rating ?: 0),
                "has_email" to (userEmail != null)
            ))
            
            // Store feedback locally for potential retry
            storeFeedbackLocally(feedback, rating, category, userEmail)
            
            // Send feedback via email
            sendFeedbackEmail(feedback, rating, category, userEmail)
        }
    }
    
    /**
     * Rate the app
     */
    fun rateApp(rating: Int, comment: String? = null) {
        feedbackScope.launch {
            analyticsManager.logEvent("app_rated", mapOf(
                "rating" to rating,
                "has_comment" to (comment != null)
            ))
            
            // Store rating
            securityManager.storeSecureData("app_rating", rating.toString())
            comment?.let { securityManager.storeSecureData("app_rating_comment", it) }
        }
    }
    
    /**
     * Report a bug
     */
    fun reportBug(
        description: String,
        stepsToReproduce: String? = null,
        expectedBehavior: String? = null,
        actualBehavior: String? = null,
        userEmail: String? = null
    ) {
        feedbackScope.launch {
            analyticsManager.logEvent("bug_reported", mapOf(
                "has_steps" to (stepsToReproduce != null),
                "has_expected" to (expectedBehavior != null),
                "has_actual" to (actualBehavior != null),
                "has_email" to (userEmail != null)
            ))
            
            val bugReport = buildBugReport(
                description,
                stepsToReproduce,
                expectedBehavior,
                actualBehavior
            )
            
            sendBugReportEmail(bugReport, userEmail)
        }
    }
    
    /**
     * Request a feature
     */
    fun requestFeature(
        feature: String,
        description: String,
        priority: FeaturePriority = FeaturePriority.MEDIUM,
        userEmail: String? = null
    ) {
        feedbackScope.launch {
            analyticsManager.logEvent("feature_requested", mapOf(
                "feature" to feature,
                "priority" to priority.name,
                "has_email" to (userEmail != null)
            ))
            
            val featureRequest = buildFeatureRequest(feature, description, priority)
            sendFeatureRequestEmail(featureRequest, userEmail)
        }
    }
    
    /**
     * Open app store for rating
     */
    fun openAppStoreForRating() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=${context.packageName}")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web browser
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            }
            context.startActivity(intent)
        }
    }
    
    /**
     * Get user's previous app rating
     */
    fun getPreviousRating(): Int? {
        return securityManager.getSecureData("app_rating")?.toIntOrNull()
    }
    
    /**
     * Get user's previous rating comment
     */
    fun getPreviousRatingComment(): String? {
        return securityManager.getSecureData("app_rating_comment")
    }
    
    private fun storeFeedbackLocally(
        feedback: String,
        rating: Int?,
        category: FeedbackCategory,
        userEmail: String?
    ) {
        val feedbackData = mapOf(
            "feedback" to feedback,
            "rating" to (rating?.toString() ?: ""),
            "category" to category.name,
            "email" to (userEmail ?: ""),
            "timestamp" to System.currentTimeMillis().toString()
        )
        
        // Store as JSON string in secure storage
        val jsonData = feedbackData.entries.joinToString(",") { "${it.key}=${it.value}" }
        securityManager.storeSecureData("pending_feedback", jsonData)
    }
    
    private fun sendFeedbackEmail(
        feedback: String,
        rating: Int?,
        category: FeedbackCategory,
        userEmail: String?
    ) {
        val subject = "Wisp Weather App - ${category.displayName} Feedback"
        val body = buildString {
            appendLine("Feedback Category: ${category.displayName}")
            rating?.let { appendLine("Rating: $it/5") }
            userEmail?.let { appendLine("User Email: $it") }
            appendLine("Timestamp: ${System.currentTimeMillis()}")
            appendLine()
            appendLine("Feedback:")
            appendLine(feedback)
        }
        
        sendEmail(subject, body, userEmail)
    }
    
    private fun buildBugReport(
        description: String,
        stepsToReproduce: String?,
        expectedBehavior: String?,
        actualBehavior: String?
    ): String {
        return buildString {
            appendLine("Bug Report")
            appendLine("==========")
            appendLine()
            appendLine("Description:")
            appendLine(description)
            appendLine()
            
            stepsToReproduce?.let {
                appendLine("Steps to Reproduce:")
                appendLine(it)
                appendLine()
            }
            
            expectedBehavior?.let {
                appendLine("Expected Behavior:")
                appendLine(it)
                appendLine()
            }
            
            actualBehavior?.let {
                appendLine("Actual Behavior:")
                appendLine(it)
                appendLine()
            }
            
            appendLine("Device Info:")
            appendLine("App Version: ${com.example.wisp.BuildConfig.VERSION_NAME}")
            appendLine("Build Type: ${com.example.wisp.BuildConfig.BUILD_TYPE}")
            appendLine("Timestamp: ${System.currentTimeMillis()}")
        }
    }
    
    private fun buildFeatureRequest(
        feature: String,
        description: String,
        priority: FeaturePriority
    ): String {
        return buildString {
            appendLine("Feature Request")
            appendLine("==============")
            appendLine()
            appendLine("Feature: $feature")
            appendLine("Priority: ${priority.displayName}")
            appendLine()
            appendLine("Description:")
            appendLine(description)
            appendLine()
            appendLine("Timestamp: ${System.currentTimeMillis()}")
        }
    }
    
    private fun sendBugReportEmail(bugReport: String, userEmail: String?) {
        val subject = "Wisp Weather App - Bug Report"
        sendEmail(subject, bugReport, userEmail)
    }
    
    private fun sendFeatureRequestEmail(featureRequest: String, userEmail: String?) {
        val subject = "Wisp Weather App - Feature Request"
        sendEmail(subject, featureRequest, userEmail)
    }
    
    private fun sendEmail(subject: String, body: String, userEmail: String?) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@wispweather.app"))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            userEmail?.let { putExtra(Intent.EXTRA_REPLY_TO, it) }
        }
        
        try {
            context.startActivity(Intent.createChooser(intent, "Send Feedback"))
        } catch (e: Exception) {
            analyticsManager.logError(e, "Failed to send email")
        }
    }
    
    private fun AnalyticsManager.logEvent(eventName: String, parameters: Map<String, Any>) {
        val bundle = android.os.Bundle().apply {
            parameters.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                }
            }
        }
        // This would need to be implemented in AnalyticsManager
        // For now, we'll use the existing log methods
    }
    
    enum class FeedbackCategory(val displayName: String) {
        GENERAL("General Feedback"),
        BUG("Bug Report"),
        FEATURE("Feature Request"),
        PERFORMANCE("Performance Issue"),
        UI_UX("UI/UX Feedback"),
        DATA("Data Issue"),
        OTHER("Other")
    }
    
    enum class FeaturePriority(val displayName: String) {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        CRITICAL("Critical")
    }
}
