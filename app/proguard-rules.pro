# By default, the flags in this file are appended to flags specified
# in /usr/share/android-studio/data/sdk/tools/proguard/proguard-android.txt

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

##---------------Begin: proguard configuration common for all Android apps ----------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-allowaccessmodification
-keepattributes *Annotation*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses "master"

-keep class com.android.vending.billing.**

-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.

# Preserve the special static methods that are required in all enumeration classes.


-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
##---------------End: proguard configuration common for all Android apps ----------

#---------------Begin: proguard configuration for support library  ----------
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class com.actionbarsherlock.** { *; }
-keep interface com.actionbarsherlock.** { *; }

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn com.google.ads.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
##---------------End: proguard configuration for Gson  ----------

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in  a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.

# For using GSON @Expose annotation

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson

# Picasso
-keepattributes SourceFile,LineNumberTable
-keep class com.parse.*{ *; }
-dontwarn com.parse.**
-dontwarn com.squareup.picasso.**
-dontwarn com.bumptech.glide.**
-keepclasseswithmembernames class * { native <methods>; }

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Libgdx stuff
#-dontwarn com.badlogic.gdx.jnigen.*
#-keep class com.badlogic.**
-keep public class android.content.Context
#-keep public interface com.badlogic.gdx.Application
#-keep public class com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
#-keep public class com.badlogic.gdx.backends.android.AndroidApplication
#-keep public class com.badlogic.gdx.backends.android.AndroidInput

-keepattributes *Annotation*

#-keepclasseswithmembers class com.badlogic.gdx.** {*;}

-dontwarn android.support.v4.**
# Remove Debug and Verbose Logs


#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

#-keep public class com.appscreat.project.editor.zlib.utils.Utils
#
-keepclasseswithmembers class de.greenrobot.event.** {*;}
#-keep interface de.greenrobot.event.**
#
#-keep

#-keep class com.alegangames.master.apps.builder.** {*;}
#-keep class com.litl.leveldb.** {*;}



## for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

##---------------End: proguard configuration for Gson  ----------

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}