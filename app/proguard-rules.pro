# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.ruslanlyalko.wl.data.models.** { *; }

-keep class com.chad.** {*;}
-keep class com.ruslanlyalko.wl.presentation.ui.main.my_notes.details.adapter.** {*;}

# poi
# https://github.com/centic9/poi-on-android/blob/master/poitest/proguard-rules.pro
-keeppackagenames org.apache.poi.ss.formula.function
-keep class org.apache.poi.** {*;}
-keep class org.apache.xmlbeans.** {*;}
-keep class org.openxmlformats.** {*;}
-keep class org.openxmlformats.schemas.** {*;}
-keep class org.w3c.** {*;}
-keep class org.dom4j.** {*;}
-keep class org.etsi.** {*;}
-keep class com.fasterxml.** {*;}
-keep class schemasMicrosoftComVml.** {*;}
-keep class schemasMicrosoftComOfficeExcel.** {*;}
-keep class schemasMicrosoftComOfficeOffice.** {*;}
-keep class com.microsoft.schemas.** {*;}
-keep class com.graphbuilder.** {*;}
-dontwarn org.etsi.**
-dontnote com.microsoft.schemas.**
-dontnote com.graphbuilder.**
-dontwarn org.openxmlformats.**
-dontwarn org.w3c.**
-dontwarn org.dom4j.**
-dontwarn java.rmi.**
-dontwarn java.awt.**
-dontwarn javax.**
-dontwarn schemasMicrosoftComVml.**
-dontwarn schemasMicrosoftComOfficeExcel.**
-dontwarn schemasMicrosoftComOfficeOffice.**
-dontwarn schemasMicrosoftComOfficeWord.**