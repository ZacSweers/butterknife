package butterknife.plugin

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import java.util.Locale

private const val ANNOTATION_PACKAGE = "androidx/annotation"
internal val SUPPORTED_TYPES = setOf(
    "anim", "array", "attr", "bool", "color", "dimen",
    "drawable", "id", "integer", "layout", "menu", "plurals", "string", "style", "styleable"
)

/**
 * Generates a class that contains all supported field names in an R file as final values.
 * Also enables adding support annotations to indicate the type of resource for every field.
 */
class FinalRClassBuilder(
    packageName: String,
    private val className: String
) {

  private val rClassInternalName = "${packageName.replace(".", "/")}/$className"

  private var resourceTypes = mutableMapOf<String, ClassWriter>()

  fun build(): Map<String, ClassWriter> {
    val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES).apply {
      visit(
          Opcodes.V1_6,
          Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
          rClassInternalName,
          null,
          "java/lang/Object",
          null
      )
      visitPrivateConstructor()
      visitSource("R.java", null)
    }

    return mapOf(className to cw) + resourceTypes
  }

  fun addResourceField(type: String, fieldName: String, fieldInitializer: Int) {
    if (type !in SUPPORTED_TYPES) {
      return
    }

    val simpleName = "$className\$$type"
    val resourceType =
        resourceTypes.getOrPut(simpleName) {
          val name = "$rClassInternalName\$$type"
          ClassWriter(ClassWriter.COMPUTE_FRAMES).apply {
            visit(
                Opcodes.V1_6,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER,
                name,
                null,
                "java/lang/Object",
                null
            )
            visitPrivateConstructor()
            visitSource("R.java", null)
            visitInnerClass(name, rClassInternalName, type, Opcodes.ACC_STATIC)
          }
        }

    resourceType.visitField(
        Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC,
        fieldName,
        Type.getDescriptor(Int::class.javaPrimitiveType),
        "I",
        fieldInitializer
    ).apply {
      visitAnnotation(getSupportAnnotationClass(type), true)
    }
  }

  @OptIn(ExperimentalStdlibApi::class)
  private fun getSupportAnnotationClass(type: String): String {
    return "$ANNOTATION_PACKAGE/${type.capitalize(Locale.US)}Res"
  }

  private fun ClassWriter.visitPrivateConstructor() {
    // Private empty default constructor
    visitMethod(Opcodes.ACC_PRIVATE, "<init>", "()V", null, null).apply {
      visitCode()
      visitVarInsn(Opcodes.ALOAD, 0) // load "this"
      visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
      visitInsn(Opcodes.RETURN)

      // Note that MAXS are computed, but we still have to call this at the end with throwaway values
      visitMaxs(-1, -1)
      visitEnd()
    }
  }
}
