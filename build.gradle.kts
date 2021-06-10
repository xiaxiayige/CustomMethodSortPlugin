plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    java
    kotlin("jvm") version "1.4.31"
}

group = "com.xiaxiayige.plugin.methodsort"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "IC-202.7660.26"
    setPlugins("java", "android", "Kotlin")
//    localPath = "C:\\Program Files\\Android\\Android Studio"
}

tasks.runIde {
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
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

//"""
//      How to use .<br>
//      1.Add ‘sortMethod.rule’ file to your project dir. <br>
//      eg: <br>
//            aa <br>
//            bb <br>
//            cc <br>
//       <br>
//       input your method name,one method name per line.<br>
//
//      2.Code menu ---> Custom Sort Methods <br>
//
//      3.Show your code style <br>
//
//      4.Now supports Java and Kotlin method sort <br>
//
//      """
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes(
        ""
    )
}