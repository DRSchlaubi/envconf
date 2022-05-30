**Project has moved to https://github.com/DRSchlaubi/stdx.kt**

# envconf

envconf is a small Kotlin MPP (currently JVM, JS) library, which helps you to use environment variables for your config

# Example

```kotlin

// this gets the env variable "HELLO"
val HELLO by environment

// this gets the env variable "HELLO_COUNT" and converts it into an int
val HELLO_COUNT by getEnv { it.toInt() }

// this does the same as the one above
val COUNT by getEnv("HELLO_") { it.toInt() }

// this does the same as the one above, but it uses 1 if the variable is missing
val COUNT by getEnv("HELLO_", 1) { it.toInt() }

// this does the same as the one above, but it uses null if the variable is missing
val COUNT by getEnv("HELLO_", String::toInt).optional()

class Konfig : Config("HELLO_") {
    // this outsources the prefix to the constructor
    val COUNT by getEnv("HELLO_", 1) { it.toInt() }
}

```

# Download

<details open>
<summary>Gradle (Kotlin)</summary>

```kotlin

repositories {
    maven("https://schlaubi.jfrog.io/artifactory/envconf/")
}

dependencies {
    implementation("dev.schlaubi", "envconf", "1.1")
}
```
</details>

<details>
<summary>Gradle (Groovy)</summary>

```groovy
repositories {
    maven { url "https://schlaubi.jfrog.io/artifactory/envconf/" }
}

dependencies {
    implementation 'dev.schlaubi:envconf:1.1'
}
```

</details>

<details>
<summary>Maven</summary>

```xml

<project>
  <repositories>
    <repository>
      <url>https://schlaubi.jfrog.io/artifactory/envconf/</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>dev.schlaubi</groupId>
      <artifactId>envconf-jvm</artifactId>
      <version>1.1</version>
    </dependency>
  </dependencies>
</project>
```

</details>

# Why is there no .env support?
Because it's easier to [use this great plugin](https://plugins.jetbrains.com/plugin/7861-envfile).
