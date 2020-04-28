package butterknife.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.FeatureExtension
import com.android.build.gradle.FeaturePlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.res.GenerateLibraryRFileTask
import com.android.build.gradle.internal.res.LinkApplicationAndroidResourcesTask
import groovy.util.XmlSlurper
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class ButterKnifePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.plugins.configureEach {
      when (this) {
        is FeaturePlugin -> {
          project.extensions.getByType<FeatureExtension>().run {
            configureR2Generation(project, featureVariants)
            configureR2Generation(project, libraryVariants)
          }
        }
        is LibraryPlugin -> {
          project.extensions.getByType<LibraryExtension>().run {
            configureR2Generation(project, libraryVariants)
          }
        }
        is AppPlugin -> {
          project.extensions.getByType<AppExtension>().run {
            configureR2Generation(project, applicationVariants)
          }
        }
      }
    }
  }

  // Parse the variant's main manifest file in order to get the package id which is used to create
  // R.java in the right place.
  private fun BaseVariant.getPackageName(): String {
    val slurper = XmlSlurper(false, false)
    val list = sourceSets.map { it.manifestFile }

    // According to the documentation, the earlier files in the list are meant to be overridden by the later ones.
    // So the first file in the sourceSets list should be main.
    val result = slurper.parse(list[0])
    return result.getProperty("@package").toString()
  }

  @OptIn(ExperimentalStdlibApi::class)
  private fun configureR2Generation(project: Project, variants: DomainObjectSet<out BaseVariant>) {
    variants.configureEach {
      val variantName = name.capitalize(Locale.US)
      val outputDir = project.buildDir.resolve(
          "generated/source/r2/$dirName"
      )

      val once = AtomicBoolean()
      outputs.configureEach {
        // Though there might be multiple outputs, their R files are all the same. Thus, we only
        // need to configure the task once with the R.java input and action.
        if (once.compareAndSet(false, true)) {
          // TODO: switch to better API once exists in AGP (https://issuetracker.google.com/118668005)
          val rFile = processResourcesProvider.map { processResources ->
            val provider = when (processResources) {
              is GenerateLibraryRFileTask -> processResources.textSymbolOutputFileProperty
              is LinkApplicationAndroidResourcesTask -> processResources.textSymbolOutputFileProperty
              else -> throw RuntimeException(
                  "Minimum supported Android Gradle Plugin is 3.3.0"
              )
            }

            provider.let { project.files(it) }
                .builtBy(processResources)
                .singleFile
          }
          val generate = project.tasks.create("generate${variantName}R2", R2Generator::class.java) {
            this@create.outputDir.set(outputDir)
            this@create.rFile.set(project.layout.file(rFile))
            this@create.packageName.set(project.provider { getPackageName() })
            this@create.className.set("R2")
          }
          registerPreJavacGeneratedBytecode(project.files(outputDir).builtBy(generate))
        }
      }
    }
  }
}
