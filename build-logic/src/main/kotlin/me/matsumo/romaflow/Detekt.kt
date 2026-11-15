package me.matsumo.romaflow

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import dev.detekt.gradle.Detekt
import dev.detekt.gradle.extensions.DetektExtension
import dev.detekt.gradle.report.ReportMergeTask
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.configureDetekt() {
    extensions.getByType<DetektExtension>().apply {
        toolVersion.set(libs.version("detekt"))
        parallel.set(true)
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        baseline.set(layout.projectDirectory.file("detekt-baseline.xml"))
        buildUponDefaultConfig.set(true)
        ignoreFailures.set(false)
        autoCorrect.set(false)
    }

    val reportMerge = if ("reportMerge" !in rootProject.tasks.names) {
        rootProject.tasks.register<ReportMergeTask>("reportMerge") {
            output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.xml"))
        }
    } else {
        rootProject.tasks.named<ReportMergeTask>("reportMerge")
    }

    val generatedDirectory = layout.buildDirectory.get().asFile
    tasks.withType<Detekt>().configureEach {
        exclude("**/resources/**", "**/build/**", "**/.gradle/**")
        exclude { it.file.startsWith(generatedDirectory) }
    }

    val lightAnalysis = tasks.named<Detekt>("detekt") {
        setSource(
            if (project == rootProject) {
                files("build.gradle.kts", "settings.gradle.kts", "build-logic")
            } else {
                files(projectDir)
            },
        )
        include("**/*.kt", "**/*.kts")
        finalizedBy(reportMerge)
    }

    reportMerge.configure {
        input.from(lightAnalysis.flatMap { it.reports.checkstyle.outputLocation })
    }

    pluginManager.withPlugin("com.android.application") { configureAndroidDetekt() }

    tasks.matching { it.name == "check" }.configureEach {
        dependsOn(
            tasks.withType<Detekt>().matching {
                it.name != "detekt" && !it.name.endsWith("SourceSet")
            },
        )
    }
}

private fun Project.configureAndroidDetekt() {
    val kotlinAndroid = extensions.getByType<KotlinAndroidExtension>()
    extensions.getByType<ApplicationAndroidComponentsExtension>().onVariants { variant ->
        val variantName = variant.name.replaceFirstChar { it.uppercase() }
        tasks.withType<Detekt>()
            .matching { it.name == "detekt$variantName" }
            .configureEach {
                // BuildConfig などの生成 Java クラスも型解決に使う。
                val compilation = kotlinAndroid.target.compilations.getByName(variant.name)
                val kotlinCompile = compilation.compileTaskProvider.map { it as KotlinJvmCompile }
                classpath.setFrom(
                    compilation.output.classesDirs,
                    kotlinCompile.map { it.libraries },
                    tasks.named<JavaCompile>("compile${variantName}JavaWithJavac")
                        .flatMap { it.destinationDirectory },
                )
            }
    }
}
