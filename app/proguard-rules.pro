# Add project specific ProGuard rules here.

# Preserve Room Database implementation
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Preserve ViewModel constructors
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

