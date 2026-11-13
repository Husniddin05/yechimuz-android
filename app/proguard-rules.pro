# YechimUZ Production Proguard Rules

# Retrofit & OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson models
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !static !transient <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class uz.yechim.app.data.model.** { *; }
-keep class uz.yechim.app.domain.model.** { *; }

# Jetpack Compose
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.ui.** { *; }
