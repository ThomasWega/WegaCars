plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.10"
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("com.gradleup.shadow") version "8.3.3"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.inventivetalent.org/repository/public/")
    maven("https://repo.momirealms.net/releases/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.11.1")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.11.2") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1")
    // don't updte cuz 1.7.2 imports spigot API as well
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.8")
}

group = "com.himerarp"
version = "0.3-SNAPSHOT"
description = "toolkit"
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = rootProject.name
            version = version
            from(components["java"])
        }
    }
}

tasks {
    shadowJar {
        relocate("com.jeff_media.morepersistentdatatypes", "com.himerarp.morepersistentdatatypes")
        relocate("dev.jorel.commandapi", "com.himerarp.commandapi")
        relocate("com.github.stefvanschie.inventoryframework", "com.himerarp.IF")
    }

    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
