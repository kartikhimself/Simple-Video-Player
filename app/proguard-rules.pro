#
#
#-dontwarn com.google.android.material.**
#-keep class com.google.android.material.** { *; }
#
#-dontwarn androidx.**
#-keep class androidx.** { *; }
#-keep interface androidx.* { *; }
#
#-keep class android.support.v7.app.MediaRouteActionProvider {
#  *;
#}
#
#-keep class com.squareup.okhttp.** { *; }
#-keep class retrofit.** { *; }
#-keep interface com.squareup.okhttp.** { *; }
#
## support-v4
##https://stackoverflow.com/questions/18978706/obfuscate-android-support-v7-widget-gridlayout-issue
#-dontwarn android.support.v4.**
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep class android.support.v4.** { *; }
#
#
## support-v7
#-dontwarn android.support.v7.**
#-keep class android.support.v7.internal.** { *; }
#-keep interface android.support.v7.internal.** { *; }
#-keep class android.support.v7.** { *; }
#
## support design
##@link http://stackoverflow.com/a/31028536
#-dontwarn android.support.design.**
#-keep class android.support.design.** { *; }
#-keep interface android.support.design.** { *; }
#-keep public class android.support.design.R$* { *; }
#
#
#-dontwarn com.squareup.okhttp.**
#-dontwarn okio.**
#-dontwarn retrofit.**
#-dontwarn rx.**
#
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#
#-keepattributes Exceptions
#-keepattributes *Annotation*,Signature
#
##renderscript
#-keep class android.support.v8.renderscript.** { *; }
#
##searchview
#-keep class android.support.v7.widget.SearchView { *; }
#
#-dontwarn javax.jdo.**
#-keepclassmembers class * {
#  @com.google.api.client.util.Key <fields>;
#}
#
## Needed by google-http-client-android when linking against an older platform version
#
#-dontwarn com.google.api.client.extensions.android.**
#
## Needed by google-api-client-android when linking against an older platform version
#
#-dontwarn com.google.api.client.googleapis.extensions.android.**
#
## Needed by google-play-services when linking against an older platform version
#
#-dontwarn com.google.android.gms.**
#
## com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
#-dontnote java.nio.file.Files, java.nio.file.Path
#
## Suppress notes on LicensingServices
#-dontnote **.ILicensingService
#
## Suppress warnings on sun.misc.Unsafe
#-dontnote sun.misc.Unsafe
#-dontwarn sun.misc.Unsafe
#
##For JodaTime
##https://stackoverflow.com/questions/14025487/proguard-didnt-compile-with-joda-time-used-in-windows
#-dontwarn org.joda.convert.FromString
#-dontwarn org.joda.convert.ToString
#-dontnote com.google.vending.licensing.ILicensingService
#-dontnote **ILicensingService
#
#-keepclassmembers class mypackage.** { *; }
#-keep class com.google.common.** { *; }
#-dontwarn com.google.common.**
#
#-keep class com.afollestad.**{ *;}
#-dontwarn com.afollestad.**
##similarly handle other libraries you added
#-keep class org.jaudiotagger.**{ *;}
#-dontwarn org.jaudiotagger.**
#-keep class com.ogaclejapan.smarttablayout.**{ *;}
#-dontwarn com.ogaclejapan.smarttablayout.**
#-keep class com.android.vending.billing.**
## will keep line numbers and file name obfuscation
#-renamesourcefileattribute SourceFile
#-keepattributes SourceFile,LineNumberTable
#
#
#-assumenosideeffects class org.solovyev.android.checkout.Check {
#    static *;
#}
#-keep class com.github.Triggertrap.**{ *;}
#-dontwarn com.github.Triggertrap.**