# Quest-specific ProGuard rules for release builds
# Optimized for VR/AR panel applications

# Keep Quest-specific classes
-keep class com.example.wisp.ui.quest.** { *; }
-keep class com.example.wisp.data.location.IpGeoLocationProvider { *; }
-keep class com.example.wisp.data.location.ManualLocationProvider { *; }
-keep class com.example.wisp.data.location.QuestLocationProvider { *; }
-keep class com.example.wisp.performance.QuestPerformanceManager { *; }

# Keep location-related classes
-keep class com.example.wisp.domain.provider.LocationProvider { *; }
-keep class com.example.wisp.domain.model.Place { *; }

# Keep serialization classes for IP geolocation
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# Keep Retrofit interfaces
-keep interface retrofit2.Call
-keep class retrofit2.Response
-keep class okhttp3.**

# Keep Hilt/Dagger classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Quest-specific optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep Serializable classes
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Quest VR-specific optimizations
-keep class android.hardware.vr.** { *; }
-keep class com.oculus.** { *; }

# Remove unused resources for smaller APK
-dontwarn com.google.android.gms.**
-dontwarn com.google.firebase.**
