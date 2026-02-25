pluginManagement {
    repositories {
        maven { url=uri("https://jitpack.io") }
        maven {url=uri("https://oss.sonatype.org/content/repositories/snapshots")}
        maven { url=uri("https://maven.aliyun.com/repository/releases") }
        // maven { url=uri("https://mirrors.aliyun.com/macports/distfiles/gradle/") }
        // maven { url=uri("https://mirrors.aliyun.com/gradle/") }
        maven { url=uri("https://maven.aliyun.com/repository/central")}
        maven { url=uri("https://maven.aliyun.com/repository/public")}
        maven { url=uri("https://maven.aliyun.com/repository/google")}
        maven { url=uri("https://maven.aliyun.com/repository/gradle-plugin")}
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url=uri("https://jitpack.io") }
        maven {url=uri("https://oss.sonatype.org/content/repositories/snapshots")}
        maven { url=uri("https://maven.aliyun.com/repository/releases") }
        // maven { url=uri("https://mirrors.aliyun.com/macports/distfiles/gradle/") }
        // maven { url=uri("https://mirrors.aliyun.com/gradle/") }
        maven { url=uri("https://maven.aliyun.com/repository/central")}
        maven { url=uri("https://maven.aliyun.com/repository/public")}
        maven { url=uri("https://maven.aliyun.com/repository/google")}
        maven { url=uri("https://maven.aliyun.com/repository/gradle-plugin")}
        google()
        mavenCentral()
    }
}

rootProject.name = "ToolMedia3"
include(":app")
include(":DLHPlayer")
