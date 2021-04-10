-keep class com.umeng.** { *; }
-keep class com.uc.** { *; }
-keep class com.efs.** { *; }

-keepclassmembers class*{
     public<init>(org.json.JSONObject);
}
-keepclassmembers enum*{
      publicstatic**[] values();
      publicstatic** valueOf(java.lang.String);
}