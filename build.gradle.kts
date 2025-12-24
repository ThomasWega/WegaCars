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
    maven("https://repo.inventivetalent.org/repository/public/")
    maven("https://repo.momirealms.net/releases/")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("org.reflections:reflections:0.10.2")
    implementation("dev.jorel:commandapi-bukkit-shade:9.7.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.11.1")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    implementation("com.jeff-media:custom-block-data:2.2.2")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.11.2") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.14.1")
    compileOnly("com.ticxo.modelengine:ModelEngine:R4.0.8")
}

group = "me.wega"
version = "0.1-SNAPSHOT"
description = "cars"
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
        relocate("com.jeff_media.morepersistentdatatypes", "me.wega.morepersistentdatatypes")
        relocate("com.jeff_media.customblockdata", "me.wega.customblockdata")
        relocate("dev.jorel.commandapi", "me.wega.commandapi")
        relocate("com.github.stefvanschie.inventoryframework", "me.wega.IF")
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
