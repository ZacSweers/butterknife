plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
  kotlin("jvm")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  compileOnly(gradleApi())

  implementation("com.android.tools.build:gradle:3.6.3")
  implementation("com.squareup:javapoet:1.12.1")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")
  implementation("org.ow2.asm:asm:8.0.1")
  implementation("org.ow2.asm:asm-util:8.0.1")

  testImplementation("junit:junit:4.13")
  testImplementation("com.google.truth:truth:1.0.1")
  testImplementation("androidx.annotation:annotation:1.1.0")
  testImplementation("com.google.testing.compile:compile-testing:0.18")
}

tasks.withType<Test> {
  dependsOn(":butterknife:installLocally")
  dependsOn(":butterknife-annotations:installLocally")
  dependsOn(":butterknife-compiler:installLocally")
  dependsOn(":butterknife-runtime:installLocally")

  systemProperty("butterknife.version", version)
}

//apply from: rootProject.file('gradle/gradle-mvn-push.gradle')
