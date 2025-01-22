plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.openapi.generator") version "7.4.0"

}

group = "com.aneesakunju"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.apache.commons:commons-csv:1.12.0")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
	testImplementation("io.mockk:mockk-jvm:1.13.13")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("compileKotlin") {
	dependsOn("openApiGenerate")
}

openApiGenerate {
	generatorName.set("kotlin-spring")
	inputSpec.set("$projectDir/src/main/resources/openapi.yaml")
	outputDir.set("${project.layout.buildDirectory.get()}/generated-openapi")
	validateSpec.set(false)
	apiPackage.set("com.aneesakunju.fxrates.api")
	modelPackage.set("com.aneesakunju.fxrates.dto")
	generateModelTests.set(false)
	generateModelDocumentation.set(false)
	generateApiDocumentation.set(false)
	generateApiTests.set(false)
	validateSpec.set(false)

	configOptions.set(
		mapOf(
			"apiSuffix" to "ControllerApi",
			"dateLibrary" to "java8",
			"delegatePattern" to "false",
			"interfaceOnly" to "true",
			"annotationsLibrary" to "none",
			"documentationProvider" to "none",
			"enumPropertyNaming" to "UPPERCASE",
			"useSpringBoot3" to "true",
			"useBeanValidation" to "true",
			"serializationLibrary" to "jackson",
			"exceptionHandler" to "false",
			"reactive" to "false",
			"gradleBuildFile" to "false",
			"useTags" to "true",
			"generateApis" to "false"
		)
	)
	globalProperties.set(
		mapOf("skipFormModel" to "false")
	)
	typeMappings.set(
		mapOf(
			"java.time.OffsetDateTime" to "java.time.Instant",
		)
	)

	cleanupOutput.set(true)
}

project.extensions.getByType<SourceSetContainer>().getByName("main").java {
	srcDir(project.layout.buildDirectory.dir("generated-openapi/src/main/kotlin"))
}
