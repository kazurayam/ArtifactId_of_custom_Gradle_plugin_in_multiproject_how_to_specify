# How to specify ArtifactId of a custom Gradle plugin in multi-project

## Problem to solve

I wanted to learn how to develop a custom Gradle plugin.

I created a new directory where I executed the `gradle init` command to initialize a Gradle project for Plugin. I worked using Gradle v8.5.

```
$ cd ~/tmp
$ mkdir ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify
$ cd ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify
```

```
$ gradle init

Select type of project to generate:
  1: basic
  2: application
  3: library
  4: Gradle plugin
Enter selection (default: basic) [1..4] 4

Select implementation language:
  1: Groovy
  2: Java
  3: Kotlin
Enter selection (default: Java) [1..3] 1

Select build script DSL:
  1: Kotlin
  2: Groovy
Enter selection (default: Groovy) [1..2] 2
```
This was followed by a series of query prompts.

```
Project name (default: ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to 
```
To this prompt, I just stroke the ENTER key.

```
Source package (default: artifactid_of_custom_gradle_plugin_in_multiproject_how_
```
I replied to this with a string `com.kazurayam.gradleplugins`

```
Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) 
```

I just stroke the Enter key.

```
> Task :init
For more information, please refer to https://docs.gradle.org/8.5/userguide/custom_plugins.html in the Gradle documentation.

BUILD SUCCESSFUL in 6m 42s
2 actionable tasks: 2 executed
```

Gradle generated the following directory and files for me.

![01](https://kazurayam.github.io/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify/images/01_project_in_finder.png)

In the following description, I would call the `Artifact_of_custom_Gradle_plugin_in_multiproject_how_to_specify` directory as "root" for short. 

Please note that the `gradle init` command generated a Multi-project which holds a subproject named **`plugin`** where I would locate the source codes for my custom Gradle plugin.

The `settings.gradle` file under the root directory had [the following code](https://github.com/kazurayam/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify/blob/master/settings.gradle).

```
rootProject.name = 'ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify'
include('plugin')
```

The `rootProject.name` was assigned with a name derived from the root directory. It looked unsuitable. But I kept this `settings.gradle` file unchanged. 

The project's structure has been settled. So I could start creating my Gradle plugin. I searched for some tutorials in the net and found an article by Tom Gregory:

- [Introduction to writing Gradle plugins](https://tomgregory.com/gradle/introduction-to-gradle-plugins/)

I copy & pasted the sample Groovy code by Tom, that includes [Extension class](https://tomgregory.com/gradle/introduction-to-gradle-plugins/#3-extension-class), [Task class](https://tomgregory.com/gradle/introduction-to-gradle-plugins/#4-task-class), [Plugin class](https://tomgregory.com/gradle/introduction-to-gradle-plugins/#5-plugin-class) and [Test for the plugin](https://tomgregory.com/gradle/introduction-to-gradle-plugins/#6-adding-a-plugin-integration-test). I slightly modified them (the package name, etc). I could successfully compile them. The unit-test passed. I could build it. Tom's sample looked fine. 

I had the `plugin/build.gradle` files was as [this](https://github.com/kazurayam/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify/blob/master/plugin/build.gradle):

```
plugins {
    id 'java-gradle-plugin'
    id 'groovy'
    id 'maven-publish'
}

group = 'com.kazurayam'
version = '0.1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation libs.spock.core
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

gradlePlugin {
    plugins {
        fileDiff {
            id = 'com.kazurayam.file-diff'
            implementationClass = 'com.kazurayam.gradleplugins.filediff.FileDiffPlugin'
        }
    }
}
```

I tried to deploy the jar to the `~/.m2/repository` (so called Maven local repository). 

```
~/tmp/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify (master)
$ cd plugin
~/tmp/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify/plugin (master)
$ gradle publicToMavenLocal
```

I tried to deploy the jar to the `~/.m2/repository` (so called Maven local repository). 

![02](https://kazurayam.github.io/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify/images/02_artifactId_plugin.png)

Here I got a problem. The `$ gradle publishToMavenLocal` command created 2 directories

- `.m2/repository/com/kazurayam/file-diff`
- `.m2/repository/com/kazurayam/plugin`

I didn't like the name of the second directory **plugin**, which was too generic, not specific enough to identify my custom Gradle plugin. Also I wanted the second directory should have a name starting with the 1st directory `file-dife` + something. With the same prefix, 2 directories will be close to each other when I get the list of the `.m2` directory.

So I wanted to specify the second directory to be named `file-diff-plugin` or `file-diff-impl`. However, I did not know how to.

How can I specify the artifactId of my custom Gradle plugin observed in the Maven local repository?

## Solution

I inserted a few lines into the `plugin/build.gradle` file as follows

```
publishing {
    publications {
        maven(MavenPublication) {
            artifactId = 'file-diff-plugin'
        }
    }
}
```

I retried `$ gradle publishToMavenLocal`, then got the following result.

![03](https://kazurayam.github.io/ArtifactId_of_custom_Gradle_plugin_in_multiproject_how_to_specify/images/03_artifactId_file-diff-plugin.png)

Success!

## Description

The `publishToMavenLocal` task is provided by the Gradle `maven-publish` plugin. The `maven-publish` plugin determines the artifactId of the published artifacts. The [doc](https://docs.gradle.org/current/userguide/publishing_maven.html#sec:identity_values_in_the_generated_pom) writes:

>Identity values in the generated POM
> 
>The attributes of the generated POM file will contain identity values derived from the following project properties:
> - groupId - Project.getGroup()
> - artifactId - Project.getName()
> - version - Project.getVersion()

The ArtifactId is derived from the Project.getName(), which will default to the name of sub-project's directory. This is the reason why I got the artifactId `plugin` which I want to replace to `file-diff-plugin`.

The doc also writes:

>Overriding the default identity values is easy: simply specify the groupId, artifactId or version attributes when configuring the MavenPublication.

And the doc shows a sample code. 

## Conclusion

I could find the way to specify the artifactId of my Gradle custom plugin. Just I needed to read the doc of the Maven Publish plugin. That's all.

