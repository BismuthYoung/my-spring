plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.0"
}

tasks.test {
    useJUnitPlatform() // 如果你使用 JUnit 5 测试框架
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    val slf4jVersion = "2.0.7"
    val logbackVersion = "1.4.8"

    implementation(kotlin("stdlib"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")  // JUnit 5 API
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2") // JUnit 5 Engine
    testImplementation("org.mockito:mockito-core:5.0.0")  // Mockito 用于模拟对象
}

subprojects {
    // 为每个子项目应用 Kotlin 插件
    plugins.apply("kotlin")

    // 配置 Kotlin 编译选项
    kotlin {
        jvmToolchain(11)  // 使用 JDK 11 编译
    }
}