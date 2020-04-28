package butterknife.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@CacheableTask
abstract class R2Generator : DefaultTask() {

  init {
    group = "bb8"
  }

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  @get:InputFiles
  @get:PathSensitive(PathSensitivity.NONE)
  abstract val rFile: RegularFileProperty

  @get:Input
  abstract val packageName: Property<String>

  @get:Input
  abstract val className: Property<String>

  @TaskAction
  fun weaveBytecode() {
    weaveBytecode(rFile.asFile.get(), outputDir.asFile.get(), packageName.get(), className.get())
  }
}

internal fun weaveBytecode(
    rFile: File,
    outputDir: File,
    packageName: String,
    className: String
) {
  // Clean the output dir first
  outputDir.listFiles()!!.forEach(File::deleteRecursively)

  // Generate the files
  FinalRClassBuilder(packageName, className)
      .also { ResourceSymbolListReader(it).readSymbolTable(rFile) }
      .build()
      .forEach { (simpleName, cw) ->
        val bytes = cw.toByteArray()
        resolvePath(packageName, simpleName, outputDir.toPath()).writeBytes(bytes)
      }
}

/**
 * Writes this to [directory] using the standard directory structure.
 *
 * Returns the [Path] instance to which source is can be written to.
 */
private fun resolvePath(packageName: String, className: String, directory: Path): File {
  check(Files.notExists(directory) || Files.isDirectory(directory)) {
    "path $directory exists but is not a directory."
  }
  val outputDirectory = packageName.split(".")
      .fold(directory) { d, packageComponent ->
        d.resolve(packageComponent)
      }
  Files.createDirectories(outputDirectory)
  return outputDirectory.resolve("$className.class").toFile()
}