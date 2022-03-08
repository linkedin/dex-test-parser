# Dex Test Parser
[![Build Status](https://img.shields.io/github/workflow/status/linkedin/dex-test-parser/Merge%20checks)](https://img.shields.io/github/workflow/status/linkedin/dex-test-parser/Merge%20checks)

## Motivation

dex-test-parser was inspired by the Google presentation "[Going Green: Cleaning up the Toxic Mobile Environment](https://www.youtube.com/watch?v=aHcmsK9jfGU)".

## What does it do?

Given an Android instrumentation apk, dex-test-parser will parse the apk's dex files and return the fully qualified method names of all JUnit 3 and JUnit 4 test methods.

Of course, you could also collect this list of method names from inside your test code by scanning the apk internally and using reflection. However, there are several reasons you may not want to do this:

 * Scanning the app's classpath for test methods at runtime causes any static initializers in the classes to be run immediately, which can lead to tests that behave differently than production code.
 * You might want to run one invocation of the `adb shell am instrument` command for each test to avoid shared state between tests and so that if one test crashes, other tests are still run.

## Download

Download the latest .jar via Maven:
```xml
    <dependency>
      <groupId>com.linkedin.dextestparser</groupId>
      <artifactId>parser</artifactId>
      <version>2.3.4</version>
      <type>pom</type>
    </dependency>
```

or Gradle:
```
    compile 'com.linkedin.dextestparser:parser:2.3.4'
```

or you can manually download the jar from [Bintray](https://bintray.com/linkedin/maven/parser).

## Getting Started

dex-test-parser provides a single public method that you can call from Java to get all test method names.
```java
List<String> customAnnotations = new ArrayList<>();
List<String> testMethodNames = DexParser.findTestNames(apkPath, customAnnotations);
```
Variable customAnnotations is a list of custom tags that marks tests if you are using custom test runner for your tests.

You can also use the jar directly from the command line if you prefer. This will create a file called `AllTests.txt` in the specified output directory.

```

java -jar parser.jar path/to/apk path/for/output

```
If "path/for/output" is omitted, the output will be printed into stdout.


If you have custom test runner (com.company.testing.uitest.screenshot.ScreenshotTest in this example) and custom tag to annotate tests:
```

java -jar parser.jar path/to/apk path/for/output -A com.company.testing.uitest.screenshot.ScreenshotTest

```

## Snapshots

You can use snapshot builds to test the latest unreleased changes. A new snapshot is published
after every merge to the main branch by the [Deploy Snapshot Github Action workflow](.github/workflows/deploy-snapshot.yml).

Just add the Sonatype snapshot repository to your Gradle scripts:
```gradle
repositories {
    maven {
        url "https://oss.sonatype.org/content/repositories/snapshots/"
    }
}
```

You can find the latest snapshot version to use in the [gradle.properties](gradle.properties) file.
