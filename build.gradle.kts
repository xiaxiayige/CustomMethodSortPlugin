plugins {
    id("org.jetbrains.intellij") version "1.11.0"
    java
    kotlin("jvm") version "1.7.22"
}

val pluginGroup: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

group = pluginGroup
version = pluginVersion


repositories {
    maven(url = "https://plugins.gradle.org/m2/")
    mavenCentral()
    google()
    maven(url = "https://www.jetbrains.com/intellij-repository/snapshots")
    maven(url = "https://cache-redirector.jetbrains.com/intellij-dependencies")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    plugins.set(listOf("java", "android", "Kotlin"))
    version.set("2022.1.1")
    instrumentCode.set(false)
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

    patchPluginXml {
        version.set(pluginVersion)
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild)

    }
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """
          How to use .<br>
          1.Add ‘sortMethod.rule’ file to your project dir. <br>
          eg: <br>
                aa <br>
                bb <br>
                cc <br>
           <br>
           
           input your method name,one method name per line.<br>
    
          2.Code menu ---> Custom Sort Methods <br>
    
          3.Show your code style <br>
    
          4.Now supports Java and Kotlin method sort <br>
          
          ChangeNote:<br>
          
          1.0.2 support Android Studio Arctic Fox 
          
          1.0.4 support more height version
          
          1.0.7 support more height version
      """
    )
}