# =========================================================
# Custom ProGuard / R8 Rules
# ACTION: Create standard obfuscation exceptions
# =========================================================

# 1. Preserve JNI components in temp_aar (JLibTorrent / TorrentStream)
# ---------------------------------------------------------
-keep class com.frostwire.jlibtorrent.** { *; }
-keep class com.github.se_bastiaan.** { *; }
-keep class org.libtorrent.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    native <methods>;
}

# 2. Preserve Reflection for JSON Parsers (Gson models, APIs, and Room DB)
# ---------------------------------------------------------
-keep class com.epicgera.vtrae.api.** { *; }
-keep class com.epicgera.vtrae.data.** { *; }
-keep class com.epicgera.vtrae.db.** { *; }

-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }

# Gson specific rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

# Workarounds for OkHttp and Retrofit
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**

