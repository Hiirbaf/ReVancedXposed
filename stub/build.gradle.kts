plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "stub"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets  {
        getByName("main") {
            java {
                srcDirs(
                    "../revanced-patches/patches/stub/src/main/java",
                    "../revanced-patches/extensions/spotify/stub/src/main/java"
                )
            }
        }
    }
}
