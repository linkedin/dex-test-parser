# Dex Test Parser
[![Build Status](https://travis-ci.org/linkedin/dex-test-parser.svg?branch=master)](https://travis-ci.org/linkedin/dex-test-parser)

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
      <version>1.0.0</version>
      <type>pom</type>
    </dependency>
```

or Gradle:
```
    compile 'com.linkedin.dextestparser:parser:1.0.0'
```

## Getting Started

dex-test-parser provides a single public method that you can call from Java to get all test method names.
```java

List<String> testMethodNames = DexParser.findTestNames(apkPath);

```

You can also use the jar directly from the command line if you prefer. This will create a file called `AllTests.txt` in the specified output directory.

```

java -jar parser.jar path/to/apk path/for/output

```