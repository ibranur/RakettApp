import org.gradle.internal.impldep.bsh.commands.dir
import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.10"
}

android {
    namespace = "no.uio.ifi.in2000.team6.rakett_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team6.rakett_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
//    implementation(libs.androidx.storage)
//    implementation(libs.androidx.runtime.livedata)
//    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //ktor
    val ktorVersion = "3.0.3"
    implementation("io.ktor:ktor-client-core:$ktorVersion") //Kjernefunksjonaliteten
    implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
//    implementation("io.ktor:ktor-client-cio:$ktorVersion") //engine. kan velges istedetfor okhttp
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion") // engine
//    implementation("ch.qos.logback:logback-classic:$logbackVersion") //logging. var anbefalt på ktor nettsiden tror jeg.
    implementation("io.ktor:ktor-client-logging:$ktorVersion") //  logging
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion") // ContentNegotiation-plugin
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion") // JSON-støtte

    implementation("com.mapbox.maps:android:11.10.3") //KART
    implementation("com.mapbox.extension:maps-compose:11.10.3") //KART
    //image loading with coil
//    implementation(libs.coil.compose)
//    implementation(libs.coil.network.okhttp)
}
