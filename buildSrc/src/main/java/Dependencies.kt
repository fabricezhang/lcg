object Dependencies {

    // router
    const val routerApi = "com.github.wyjsonGo.GoRouter:GoRouter-Api:2.5.1"
    const val routerCompiler = "com.github.wyjsonGo.GoRouter:GoRouter-Compiler:2.5.1"

    // androidx
    const val appCompat = "androidx.appcompat:appcompat:1.2.0"
    const val coreKtx = "androidx.core:core-ktx:1.6.0"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val material = "com.google.android.material:material:1.3.0"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
    const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecycle}"

    // androidx navigation
    const val navigationFragment = "androidx.navigation:navigation-fragment:${Version.navigation}"
    const val navigationUi = "androidx.navigation:navigation-ui:${Version.navigation}"
    const val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Version.navigation}"
    const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:${Version.navigation}"
    const val navigationDynamic = "androidx.navigation:navigation-dynamic-features-fragment:${Version.navigation}"

    // media3
    const val mediaExoplayer =  "androidx.media3:media3-exoplayer:${Version.media3}"
    const val mediaExoplayerHls =  "androidx.media3:media3-exoplayer-hls:${Version.media3}"
    const val mediaUI = "androidx.media3:media3-ui:${Version.media3}"
    const val mediaCommon = "androidx.media3:media3-common:${Version.media3}"

    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10"


    // network
    const val okhttp = "com.squareup.okhttp3:okhttp:3.14.9"

    // view
    const val multiType = "com.drakeet.multitype:multitype:4.3.0"
    const val immersionBar = "com.geyifeng.immersionbar:immersionbar:3.2.2"
    const val immersionBarKtx = "com.geyifeng.immersionbar:immersionbar-ktx:3.2.2"
}

object Version {
    const val compileSdk = 34
    const val targetSdk = 33
    const val minSdk = 24
    const val buildToolsVersion = "34.0.0"
    const val kotlin = "1.9.10"

    const val navigation = "2.7.7"
    const val media3 = "1.3.1"

    const val lifecycle = "2.8.4"

    const val okhttp = "3.14.9"
    const val multiType = "4.3.0"
    const val immersionBar = "3.2.2"
}