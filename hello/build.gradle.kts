@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.example.hello"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

afterEvaluate {
    val copyCmakeBuildOutputs by tasks.creating {
        val copyDebugJniLibsProjectOnly by tasks.getting {
            doLast {
                logger.error("$name: output = ${outputs.hasOutput} ${outputs.files.files}")
                copy {
                    from(outputs.files.files)
                    into(
                        project.file(
                            listOf(
                                "..",
                                "libs",
                                "jni",
                                "debug"
                            ).joinToString(File.separator)
                        )
                    )
                }
            }
        }
        val copyReleaseJniLibsProjectOnly by tasks.getting {
            doLast {
                logger.error("$name: output = ${outputs.hasOutput} ${outputs.files.files}")
                copy {
                    from(outputs.files.files)
                    into(
                        project.file(
                            listOf(
                                "..",
                                "libs",
                                "jni",
                                "release"
                            ).joinToString(File.separator)
                        )
                    )
                }
            }
        }
        dependsOn(copyDebugJniLibsProjectOnly, copyReleaseJniLibsProjectOnly)
    }
}