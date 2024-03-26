# introduction_to_writing_gradle_plugins

## 問題と解決

カスタムGradleプラグインを開発しようとした。`gradle init`してPlguinプロジェクトを選択するとマルチプロジェクトが生成される。その枠組みにしたがってTom Gregoryの記事のサンプルコードを打ち込んだ。`gradle publishToMavenLocal`するとjarファイルが生成されて.m2ディレクトリ配下に出力された。生成されたディレクトリの名前が `file-diff-plugin-VERSION.jar` となると期待していたのにそうならなかった。`plugin-VERSION.jar` になった。artifactIdがpluginとなった。サププロジェクトのフォルダ名がpluginだからこうなった。フォルダ名を変更したくはなかった。jarファイルの名前だけをコードで指定する方法を探した。実はこの問題は私にとって数年来未解決の問題であった。なんとかしたかった。ーーーーついに見つけた。`plugin/build.gradle` ファイルの次のコードを記述すればいい。

```
publishing {
    publications {
        maven(MavenPublication) {
            artifactId = 'file-diff-plugin'
        }
    }
}
```

## Description

I read an artile [Introduction to Gradle Plugins](https://tomgregory.com/gradle/introduction-to-gradle-plugins/) by Tom Gregory.

I made the project as a Gradle Multi-project.

The root project was named as `introduction_to_writing_gradle_plugin`

I typed the sample code as is. 

I did `$ gradle publishToMavenCentral`.

In the `.m2` directory (Maven Local repository), I got 2 directories newly added.

- $HOME/.m2/repository/com/kazurayam/file-diff/com.kazurayam.file-diff.gradle.plugin/0.1.0-SNAPSHOT/com.kazurayam.file-diff.gradle.plugin-0.1.0-SNAPSHOT.pom
- $HOME/.m2/repository/com/kazurayam/**plugin**/0.1.0-SNAPSHOT/**plugin**-0.1.0-SNAPSHOT.jar

I did not like the 2nd name **`plugin`**.
I wanted to change it to `file-diff-plugin`.

I edited the `settings.gradle` file as

```
rootProject.name = 'file-diff-plugin'
```

This did not have effect to the name of the jar file.

I struggled days and nights.

Finally found the way.

I added the following code in the `plugin/build.gradle` file:

```
publishing {
    publications {
        maven(MavenPublication) {
            artifactId = 'file-diff-plugin'
        }
    }
}
```

This code section determined the artifactId of the jar file.

It seems that the "Java Gradle Development Plugin" indirectly calls the "Maven Publish Plugin" which determines the name of jar files. The documentation of the Maven Publish Plugin clearly describes how to expclicity specify the artifactId.

- https://docs.gradle.org/current/userguide/publishing_maven.html

Things resolved.



