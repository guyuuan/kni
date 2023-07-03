import com.android.build.gradle.internal.cxx.io.writeTextIfDifferent
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.KonanTarget
import org.jetbrains.kotlin.konan.target.KonanTarget.ANDROID_ARM32
import org.jetbrains.kotlin.konan.target.KonanTarget.ANDROID_ARM64
import org.jetbrains.kotlin.konan.target.KonanTarget.ANDROID_X64
import org.jetbrains.kotlin.konan.target.KonanTarget.ANDROID_X86

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.org.jetbrains.kotlin.multiplatform)
    alias(libs.plugins.androidLibrary)
}
val jniLibDir = File(project.buildDir, listOf("generated", "jniLibs").joinToString(File.separator))


kotlin {
    android()
    val nativeConfigure: KotlinNativeTarget.() -> Unit = {
        compilations {
            val main by getting {
                cinterops {

                    val libhello by creating {
                        val defPath = defFile.absolutePath
                        val file =
                            File(defPath.replace(".def", "_${abiDirName(target.konanTarget)}.def"))
//                        val file = File(defPath)
                        val templateFile = File(defPath.removeSuffix(".def"))
                        val text = templateFile.readText()
                        println("def path = ${file.absolutePath}")
                        if (!file.exists()) {
                            file.writeText(
                                text.replace(
                                    "{LINK_PATH}", "-L${
                                        project.file(
                                            listOf(
                                                "..",
                                                "libs",
                                                "jni",
                                                "debug",
                                                abiDirName(target.konanTarget)
                                            ).joinToString(File.separator)
                                        ).absolutePath
                                    }"
                                )
                            )

                        } else {
                            file.writeTextIfDifferent(
                                text.replace(
                                    "{LINK_PATH}", "-L ${
                                        project.file(
                                            listOf(
                                                "..", "libs", "jni","debug", abiDirName(target.konanTarget)
                                            ).joinToString(File.separator)
                                        ).absolutePath
                                    }"
                                )
                            )
                        }
                        defFile = file
                        packageName = "hello.${abiDirName(target.konanTarget).replace("-", "_")}"
                        compilerOpts += "-I${
                            project.file(
                                listOf(
                                    "..", "hello", "src", "main", "cpp"
                                ).joinToString(File.separator)
                            ).absolutePath
                        }"
                        extraOpts("-verbose")
                    }
                }
            }
        }
        binaries {
            sharedLib("knlib") {
                println(linkTask)
                linkTask.doLast {
                    copy {
                        from(outputFile)
                        val typeName =
                            if (buildType == NativeBuildType.DEBUG) "Debug" else "Release"

                        into(
                            file(
                                listOf(
                                    jniLibDir, typeName, abiDirName(target.konanTarget)
                                ).joinToString(File.separator)
                            )
                        )
                    }
                }

                afterEvaluate {
                    val preBuild by tasks.getting
                    preBuild.dependsOn(linkTask)
                }
            }
        }

    }
    androidNativeArm32(configure = nativeConfigure)
    androidNativeArm64(configure = nativeConfigure)
    androidNativeX86(configure = nativeConfigure)
    androidNativeX64(configure = nativeConfigure)

    sourceSets {
        val androidNativeArm32Main by getting
        val androidNativeArm64Main by getting
        val androidNativeX86Main by getting
        val androidNativeX64Main by getting

        val nativeMain by creating {
            androidNativeArm32Main.dependsOn(this)
            androidNativeArm64Main.dependsOn(this)
            androidNativeX86Main.dependsOn(this)
            androidNativeX64Main.dependsOn(this)
        }
    }
}

fun abiDirName(target: KonanTarget) = when (target) {
    ANDROID_ARM32 -> "armeabi-v7a"
    ANDROID_ARM64 -> "arm64-v8a"
    ANDROID_X86 -> "x86"
    ANDROID_X64 -> "x86_64"
    else -> "unknown"
}

android {
    namespace = "com.example.knative"
    defaultConfig {
        compileSdk = 33
    }

    sourceSets {

        getByName("main") {
            kotlin {
                srcDirs("src/androidMain/kotlin")
            }
        }
        getByName("debug") {
            jniLibs.srcDirs(
                "$jniLibDir/Debug", project.file(
                    listOf(
                        "..", "libs", "jni", "debug"
                    ).joinToString(File.separator)
                ).absolutePath
            )
        }
        getByName("release") {
            jniLibs.srcDirs(
                "$jniLibDir/Release", project.file(
                    listOf(
                        "..", "libs", "jni", "release"
                    ).joinToString(File.separator)
                ).absolutePath
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
