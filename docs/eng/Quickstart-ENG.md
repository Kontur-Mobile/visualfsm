# Quickstart

## How to setup main classes

Base classes for Android, JVM and KMM projects (Feature and AsyncWorker coroutines edition)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-core:1.1.0")
```

Support of RxJava 3 (FeatureRx, AsyncWorkerRx and dependent classes)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava3:1.1.0")
```

Support of RxJava 2 (FeatureRx, AsyncWorkerRx and dependent classes)

```kotlin
implementation("ru.kontur.mobile.visualfsm:visualfsm-rxjava2:1.1.0")
```

Tools for:

* Graph creation and analysis
* Providing generated classes

```kotlin
testImplementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
```

## How to setup and enable code generation

### How to setup code generation

#### _Kotlin App Setup_

##### In the build.gradle of the module where the annotations will be used

<details>
  <summary>Groovy</summary>

```groovy
// Use KSP plugin
plugins {
    id "com.google.devtools.ksp" version "1.6.21-1.0.6"
}

// Add generated code to source code directories
kotlin {
    sourceSets {
        main.kotlin.srcDirs += 'build/generated/ksp/main/kotlin'
        test.kotlin.srcDirs += 'build/generated/ksp/test/kotlin'
    }
}

dependencies {
    // Use AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Use tools for convenient provide of the generated code. For jvm projects only.
    implementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

// Add generated code to source code directories
kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    // Use AnnotationProcessor
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
    // Use tools for convenient provide of the generated code. For jvm projects only.
    implementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

#### _Android App Setup_

##### In the build.gradle of the module where the annotations will be used

<details>
  <summary>Groovy</summary>

```groovy
// Use KSP plugin
plugins {
    id "com.google.devtools.ksp" version "1.6.21-1.0.6"
}

dependencies {
    // Use AnnotationProcessor
    ksp "ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0"
    // Use tools for convenient provide of the generated code
    implementation "ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0"
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Use KSP plugin
plugins {
    id("com.google.devtools.ksp") version "1.6.10-1.0.6"
}

dependencies {
    // Use AnnotationProcessor
    ksp("ru.kontur.mobile.visualfsm:visualfsm-compiler:1.1.0")
    // Use tools for convenient provide of the generated code
    implementation("ru.kontur.mobile.visualfsm:visualfsm-tools:1.1.0")
}
```

</details>

##### _In build.gradle of the app module_

<details>
  <summary>Groovy</summary>

```groovy
// Add generated code to source code directories
android {
    applicationVariants.all { variant ->
        variant.sourceSets.java.each {
            it.srcDirs += "build/generated/ksp/${variant.name}/kotlin"
        }
    }
}
```

</details>
<details>
  <summary>Kotlin</summary>

```kotlin
// Add generated code to source code directories
android.applicationVariants.all {
    kotlin {
        sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
}
```

</details>

### How to enable code generation

1. Annotate the Feature class with the GenerateTransitionFactory annotation
2. Pass to TransitionFactory constructor parameters
    1. For jvm project use the provideTransitionFactory function
    2. For a non-jvm project, pass an instance of the generated class.
       The name of the generated class is formed as "Generated\*Feature\*TransitionFactory",
       where \*Feature\* is the name of the annotated Feature class.

<details>
  <summary>Example for jvm project</summary>

```kotlin
// Use Feature with Kotlin Coroutines or FeatureRx with RxJava
@GenerateTransitionFactory // annotation for enable generation of TransitionFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionFactory = provideTransitionFactory() // Get an instance of the generated TransitionFactory
)
```

</details>
<details>
  <summary>Example for a non-jvm project</summary>

```kotlin
// Use Feature with Kotlin Coroutines or FeatureRx with RxJava
@GenerateTransitionFactory // annotation for enable generation of TransitionFactory
class AuthFeature(initialState: AuthFSMState) : Feature<AuthFSMState, AuthFSMAction>(
    initialState = initialState,
    transitionFactory = GeneratedAuthFeatureTransitionFactory()
)
```

</details>