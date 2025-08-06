import groovy.xml.MarkupBuilder
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.NodeChild
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "io.github.chsbuffer.revancedxposed"

    defaultConfig {
        applicationId = "io.github.chsbuffer.revancedxposed"
        versionCode = 23
        versionName = "1.0.$versionCode"
        val patchVersion = Properties().apply {
            File("revanced-patches/gradle.properties").inputStream().use { load(it) }
        }["version"]
        buildConfigField("String", "PATCH_VERSION", "\"$patchVersion\"")
    }
    flavorDimensions += "abi"
    productFlavors {
        create("x86_64") {
            dimension = "abi"
            ndk {
                abiFilters.add("x86_64")
            }
        }
        create("arm64_v8a") {
            dimension = "abi"
            ndk {
                abiFilters.add("arm64-v8a")
            }
        }
        create("universal") {
            dimension = "abi"
        }
    }
    androidResources {
        additionalParameters += arrayOf("--allow-reserved-package-id", "--package-id", "0x4b")
    }
    packagingOptions.resources {
        excludes.addAll(
            arrayOf(
                "META-INF/**", "kotlin/**", "**.bin"
            )
        )
    }
    val ksFile = rootProject.file("signing.properties")
    signingConfigs {
        if (ksFile.exists()) {
            create("release") {
                val properties = Properties().apply {
                    ksFile.inputStream().use { load(it) }
                }

                storePassword = properties["KEYSTORE_PASSWORD"] as String
                keyAlias = properties["KEYSTORE_ALIAS"] as String
                keyPassword = properties["KEYSTORE_ALIAS_PASSWORD"] as String
                storeFile = file(properties["KEYSTORE_FILE"] as String)
            }
        }
    }
    buildFeatures.buildConfig = true
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            if (ksFile.exists())
                signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xno-param-assertions",
                "-Xno-receiver-assertions",
                "-Xno-call-assertions"
            )
            jvmTarget = JvmTarget.JVM_17
        }
    }
    sourceSets {
        getByName("main") {
            java {
                srcDirs(
                    "../revanced-patches/extensions/shared/library/src/main/java",
                    "../revanced-patches/extensions/spotify/src/main/java"
                )
            }
        }
    }
}

dependencies {
    implementation(libs.dexkit)
    implementation(libs.annotation)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.fuel)
    compileOnly(libs.xposed)
    compileOnly(project(":stub"))
}

abstract class GenerateStringsTask @Inject constructor(
) : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    private fun unwrapPatch(input: File, output: File) {
        val inputXml = XmlSlurper().parse(input)
        output.writer().use { writer ->
            MarkupBuilder(writer).run {
                fun writeNode(node: Any?) {
                    if (node !is NodeChild) return
                    val attributes = node.attributes()
                    withGroovyBuilder {
                        if (node.children().any()) {
                            node.name()(attributes) {
                                node.children().forEach {
                                    writeNode(it)
                                }
                            }
                        } else {
                            node.name()(attributes) { mkp.yield(node.text()) }
                        }
                    }
                }

                doubleQuotes = true
                withGroovyBuilder {
                    "resources" {
                        // resources.app.patch.*
                        inputXml.children().children().children().forEach {
                            writeNode(it)
                        }
                    }
                }
            }
        }
    }

    @TaskAction
    fun action() {
        val inputDir = inputDirectory.get().asFile
        val outputDir = outputDirectory.get().asFile

        inputDir.listFiles()?.forEach { variant ->
            val inputFile = File(variant, "strings.xml")
            val genResDir = File(outputDir, variant.name).apply { mkdirs() }
            val outputFile = File(genResDir, "strings.xml")
            unwrapPatch(inputFile, outputFile)
        }

        unwrapPatch(File(inputDir, "values/arrays.xml"), File(outputDir, "values/arrays.xml"))
    }
}

abstract class CopyResourcesTask @Inject constructor() : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun action() {
        val baseDir = inputDirectory.get().asFile
        val outputDir = outputDirectory.get().asFile

        val drawables = listOf(
            "qualitybutton/drawable",
            "settings/drawable",
            "sponsorblock/drawable",
            "swipecontrols/drawable"
        )

        for (drawable in drawables) {
            File(baseDir, drawable).copyRecursively(File(outputDir, "drawable"), overwrite = true)
        }
    }
}

androidComponents {
    onVariants { variant ->
        val variantName = variant.name.uppercaseFirstChar()
        val strTask = project.tasks.register<GenerateStringsTask>("generateStrings$variantName") {
            inputDirectory.set(project.file("../revanced-patches/patches/src/main/resources/addresources"))
        }
        variant.sources.res?.addGeneratedSourceDirectory(
            strTask, GenerateStringsTask::outputDirectory
        )

        val resTask = project.tasks.register<CopyResourcesTask>("copyResources$variantName") {
            inputDirectory.set(project.file("../revanced-patches/patches/src/main/resources"))
        }
        variant.sources.res?.addGeneratedSourceDirectory(
            resTask, CopyResourcesTask::outputDirectory
        )
    }
}
