# Change Log

## Version 2.3.1 (2021-02-21)

- Moved argument parsing to a new library [#60](https://github.com/linkedin/dex-test-parser/pull/60)

## Version 2.3.0 (2021-02-05)

- Moved artifact publishing from JCenter to Maven Central [#53](https://github.com/linkedin/dex-test-parser/pull/53)
- Add support for finding custom test annotations [#50](https://github.com/linkedin/dex-test-parser/pull/50)

## Version 2.2.1 (2020-06-11)

- Fix test parsing when class and superclass are in different dex files [#45](https://github.com/linkedin/dex-test-parser/issues/45)

## Version 2.2.0 (2019-11-05)

- Add support for encoded array values in annotations

## Version 2.1.1 (2019-04-23)

- Fix crash when the classpath does not contain the `Inherited` annotation [#37](https://github.com/linkedin/dex-test-parser/issues/37)

## Version 2.1.0 (2019-04-04)

- Add support for parsing enum annotation values [#28](https://github.com/linkedin/dex-test-parser/pull/28)
- Improve DexMagic checks to support newer dex file formats [#31](https://github.com/linkedin/dex-test-parser/issues/31)
- Support parsing `@Inherited` annotations [#26](https://github.com/linkedin/dex-test-parser/issues/26)
- Fix crash with `Invalid LEB128 sequence` [#34](https://github.com/linkedin/dex-test-parser/issues/34)

## Version 2.0.1 (2018-12-13)

- Fix reporting when using default interface methods and private methods [#20](https://github.com/linkedin/dex-test-parser/issues/20)
- Fix buffer overflow when parsing test annotations [#21](https://github.com/linkedin/dex-test-parser/issues/21)

## Version 2.0.0

- Support finding superclass methods annotated with @Test for JUnit4 tests. Breaking change
to the Java interface for finding JUnit4 tests - [#13](https://github.com/linkedin/dex-test-parser/issues/13)
- Added support for parsing and reading encoded values - [#9](https://github.com/linkedin/dex-test-parser/issues/9)
- Fixed crashes when building with minSdkVersion 24+ - [#12](https://github.com/linkedin/dex-test-parser/issues/12)


## Version 1.1.0 (2017-07-13)

- Fixed bug where invalid tests methods in interfaces were returned (#3)
- Added support for returning all annotations that are on a test (#1)
- Added Android app module with lots of regression tests! (#5)

## Version 1.0.0

- Initial release.
