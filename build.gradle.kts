plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    java
    kotlin("jvm") version "1.3.72"
}

group = "com.xiaxiayige.plugin."
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1.1"
    setPlugins("java","android","Kotlin")
    localPath = "C:\\Program Files\\Android\\Android Studio"

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks {
    runPluginVerifier {
        ideVersions(listOf<String>("2020.1.1"))
    }
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}