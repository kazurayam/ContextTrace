# [Katalon Studio] ContextTrace

- date Oct 2025
- by [kazurayam](https://forum.katalon.com/u/kazurayam/summary)

In Katalon Studio, the `com.kazurayam.ks.ContextTree` class enables a Test Case script to get its own "Test Case ID" and other useful identifications programatically at runtime.

## Prerequisites

- You need Katalon Studio of version 10.0.0 or newer as this library was developed on v10 which restored the "Custom Keyword" feature.
- No Katalon Enterprise license is required to use this library

## Where to download the jar

You can download the `ks-context-trace-*.jar` file from the Releases page of the GitHub project at

- https://github.com/kazurayam/ContextTrace/releases

You need to manually put the jar into the `Drivers` folder of your Katalon project and reopen your project. See the doc ["Library management in Katalon Studio"](https://docs.katalon.com/katalon-studio/manage-projects/project-settings/library-management-in-katalon-studio#copy-and-paste-a-library-jar-file-to-the-drivers-folder)

## Usage

### create a TestListener

You need to create a [TestListener](https://docs.katalon.com/katalon-studio/create-test-cases/test-fixtures-and-test-listeners-test-hooks-in-katalon-studio#test-listeners-test-hooks) in your Katalon project. The file could be named any. E.g, `Test Listeners/SetupContextTrace.groovy`.

You can copy and page the following code (no change will be required):

```
import com.kazurayam.ks.ContextTrace
import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

class SetupContextTrace {

	@BeforeTestSuite
	def beforeTestSuite(TestSuiteContext context) {
		ContextTrace.setTestSuiteContext(context)
	}

	@BeforeTestCase
	def beforeTestCase(TestCaseContext context) {
		ContextTrace.setTestCaseContext(context)
	}
}
```

This TestListener is essential for the `com.kazurayam.ks.ContextTrace` class to work properly.
Katalon Studio fires the `beforeTestCase(TestCaseContext)` and `beforeTestSuite(TestSuiteContext)` methods in the TestListener, which pass the context objects to the `ContextTrace` object. The `ContextTrace` object will memorize the context objects so that user's TestCase scripts can referer to later when necessary.

### write a Test Case that uses TestContextTrace

Now you can use the `TestContextTrace` anywhere in your Test Case scripts. For example, see
[Test Cases/main/demo](https://github.com/kazurayam/TestContextTrace/blob/master/lib/Scripts/main/demo/Script1759362677840.groovy) in the `app` project:

```
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase

import com.kazurayam.ks.ContextTrace
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

/**
 * main/demo
 */

println "ContextTrace.getTestSuiteId(): " + ContextTrace.getTestSuiteId()
println "ContextTrace.escapedTestSuiteId(): " + ContextTrace.escapedTestSuiteId()
println "ContextTrace.getCurrentTestCaseId(): " + ContextTrace.getCurrentTestCaseId()
println "ContextTrace.getEscapedCurrentTestCaseId(): " + ContextTrace.getEscapedCurrentTestCaseId()
println "ContextTrace.getTestCaseIdStack(): " + ContextTrace.getTestCaseIdStack()
println "ContextTrace.escapedTestCaseIdChain(): " + ContextTrace.escapedTestCaseIdChain()

WebUI.callTestCase(findTestCase("main/demo_sub"), [:])
```

The `Test Cases/main/demo` calls `Test Cases/main/demo_sub`, which looks like this:

```
import com.kazurayam.ks.ContextTrace

/**
 * main/demo_sub
 */
ContextTrace.status()
```

When you run the `Test Cases/main/demo`, you will see the following messages in the Console tab of Katalon Studio:

![lib-demo](https://kazurayam.github.io/ContextTrace/images/lib-demo.png)

1. The statement `ContextTrace.getTestCaseIdStack()` returned an instace of `java.util.Stack`, which was stringified into
```[Test Cases/main/demo, Test Cases/main/demo_sub]```
. This reveals that Katalon Studio launched `Test Cases/main/demo`, which then called the `Test Cases/main/demo_sub` via `WebUI.callTestCase(...)` keword.
2. The statement `ContextTrace.escapedTestCaseIdChain()` returned a string
```main-demo,main-demo_sub```
. This string was derived from the Stack instance that `ContextTrace.getTestCaseIdStack()`.
3. Please note that the returned value `main-demo,main-demo_sub` was derived from the Test Case ID strings while slightly translated.
  - The leading prefix `Test Cases/` was chomped off
  - Forward slash character / was translated into a hypen -
  - Whitespace character was translated into a under bar _
  - A series of Test Cases chained by the `callTestCase()` keyword is converted into a flat string delimited by comma ,
4. The string returned by `ContextTrace.escapedTestCaseIdChain()` is shorter and more compact than the original TestCaseIds.
5. The string returned by `ContextTrace.escapedTestCaseIdChain()` is suitable for a file name, as it contains no special characters (`<>:"/\|?*`) which are prohibited as a part of file name in Windows.

## API documentation

The API document of `com.kazurayam.ks.ContextTrace` is found [here](https://kazurayam.github.io/ContextTrace/api/)

## What is the ContextTrace class useful for?

For example, when a test case in Katalon Studio takes a screenshot of browser, it is a good practice to save the PNG into a file named with the string returned by `ContextTrace.escapedTestCaseIdChain()`. The following document describes this idea with sample code:

- [the doc](https://kazurayam.github.io/ContextTrace/)

Many other usecases you would be able to find. You would insert a string of `escapedTestCaseIdChain` into your RDB tables or No-SQL databases to record the test's activities, that would make the records self-describing well.

## History

This project succeeds my old project [TestCaseStack](https://github.com/kazurayam/TestCaseStack). I have encapsulated my hack into a jar and published it.

## Developer Guide

- [here](https://kazurayam.github.io/ContextTrace/developer-guide.adoc)
