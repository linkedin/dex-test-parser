# Change Log

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
