package ru.kontur.mobile.visualfsm.annotation_processor

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import ru.kontur.mobile.visualfsm.*
import java.io.File
import java.nio.file.Paths
import kotlin.reflect.KClass

@OptIn(ExperimentalCompilerApi::class)
object TestUtil {

    fun getVisualFSMSources() = listOf(
        getSource(Action::class),
        getSource(Transition::class),
        getSource(State::class),
        getSource(Feature::class),
        getSource(GenerateTransitionsFactory::class),
        getSource(Edge::class)
    )

    fun KotlinCompilation.Result.getKspCodeGeneratedSources(): List<File> {
        return getKspGeneratedSources(this, "kotlin")
    }

    fun KotlinCompilation.Result.getKspNoCodeGeneratedSources(): List<File> {
        return getKspGeneratedSources(this, "resources")
    }

    private fun getKspGeneratedSources(result: KotlinCompilation.Result, dirName: String): List<File> {
        val kspWorkingDir = result.getWorkingDir().resolve("ksp")
        val kspGeneratedDir = kspWorkingDir.resolve("sources")
        val kotlinGeneratedDir = kspGeneratedDir.resolve(dirName)
        return kotlinGeneratedDir.walkTopDown().toList() - kotlinGeneratedDir
    }

    private fun getSource(kClass: KClass<*>): SourceFile {
        val actionAnnotationRelativePath = kClass.qualifiedName!!.toCharArray().joinToString(
            separator = "",
            transform = { if (it.toString() == ".") File.separator else it.toString() }
        )

        val parentPath = Paths.get("").toAbsolutePath().parent.toAbsolutePath().toString()

        val path = StringBuilder()
            .append(parentPath)
            .append(File.separator)
            .append("visualfsm-core")
            .append(File.separator)
            .append("src")
            .append(File.separator)
            .append("commonMain")
            .append(File.separator)
            .append("kotlin")
            .append(File.separator)
            .append(actionAnnotationRelativePath)
            .append(".kt")
            .toString()

        return SourceFile.fromPath(File(path))
    }

    private fun KotlinCompilation.Result.getWorkingDir(): File = outputDirectory.parentFile!!

}