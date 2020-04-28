package butterknife.plugin

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class FinalRClassBuilderTest {
  @Rule @JvmField val tempFolder = TemporaryFolder()

  @Test fun weaveBytecode() {
    val packageName = "com.butterknife.example"
    val rFile = tempFolder.newFile("R.txt").also {
      it.writeText(javaClass.getResource("/fixtures/R.txt").readText())
    }

    val outputDir = tempFolder.newFolder()
    weaveBytecode(rFile, outputDir, packageName, "R2")

    // TODO load classes, verify fields and classes
    val actual = outputDir.resolve("com/butterknife/example/R2.class").readText()
    println(actual)
//    val expected = javaClass.getResource("/fixtures/R2.class").readText()

//    assertEquals(expected.trim(), actual.trim())

  }
}
