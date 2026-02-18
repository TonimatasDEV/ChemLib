plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.140"
    id("idea")
}

val minecraftVersion: String by extra
val modVersion: String by extra
val neoVersion: String by extra
val parchmentMappingsVersion: String by extra
val parchmentMinecraftVersion: String by extra
val neoforgeVersionRange: String by extra

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
}

version = modVersion
group = "com.smashingmods.chemlib"

repositories {
    
}

base {
    archivesName = "chemlib"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }
    
    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", "chemlib")
        }

        create("data") {
            data()
            programArguments.addAll("--mod", "chemlib", "--all", "--output", file("src/generated/resources/").getAbsolutePath(), "--existing", file("src/main/resources/").getAbsolutePath())
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create("chemlib") {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

dependencies {

}


var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    var replaceProperties = mapOf("minecraftVersion" to minecraftVersion, "neoVersion" to neoVersion,
        "neoforgeVersionRange" to neoforgeVersionRange, "modVersion" to modVersion
    )
    
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets.main.get().resources.srcDir(generateModMetadata)

neoForge.ideSyncTask(generateModMetadata)
/*
publishing {
    publications {
        register("mavenJava", MavenPublication) {
            from components.java
        }
    }
    repositories {
        maven {
            url "file://${project.projectDir}/repo"
        }
    }
}
*/
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}






/*
plugins {
    id "java"
    id "idea"
    id "net.minecraftforge.gradle" version "[6.0,6.2)"
    id "org.parchmentmc.librarian.forgegradle" version "1.+"
    id "com.matthewprenger.cursegradle" version "1.4.0"
}

apply plugin: "net.minecraftforge.gradle"
apply plugin: "org.parchmentmc.librarian.forgegradle"
apply plugin: "maven-publish"

repositories {
    maven { url "https://maven.blamejared.com/" }
    maven {
        url "https://www.cursemaven.com"
        content {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    implementation fg.deobf("mezz.jei:jei-${minecraft_version}-common-api:${jei_version}")
    runtimeOnly fg.deobf("mezz.jei:jei-${minecraft_version}-forge:${jei_version}")
}

def secrets = new Properties()
file("secrets.properties").withInputStream {
    stream -> secrets.load(stream)
}

fileTree("secrets").matching {
    include "** /*.properties"
}.each {
    File file ->
        file.withInputStream {
            stream -> secrets.load(stream)
        }
}

publishing {
    publications {
        mavenJava(MavenPublication) {
            afterEvaluate {
                artifact project.jar
                artifact project.sourcesJar
                artifact project.javadocJar
            }
            setGroupId "smashingmods"
            setArtifactId "chemlib"
        }
    }
    repositories {
        maven {
            url "https://maven.tamaized.com/releases"
            credentials {
                username secrets.getProperty("maven_username")
                password secrets.getProperty("maven_password")
            }
        }
    }
}

curseforge {
    apiKey = secrets.getProperty("apiKey")
    project {
        id = "340666"
        releaseType = "release"
        changelogType = "markdown"
        changelog = file("changelog.md")
        addGameVersion "Forge"
        addGameVersion "Java 17"
        addGameVersion "$minecraft_version"
        mainArtifact(jar) {
            displayName = "ChemLib $version"
            relations {
                optionalDependency "jei"
            }
        }
    }
}

 */