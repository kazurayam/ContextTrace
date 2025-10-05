# It is a good practice to name screenshot files with the Test Case ID

Katalon Studio provides a built-in keyword [`WebUI.takeScreenshot`](https://docs.katalon.com/katalon-studio/keywords/keyword-description-in-katalon-studio/web-ui-keywords/webui-take-screenshot). This feature brings an itchy problem to me. Let me describe the problem and what I have done.

I have developed a Katalon project <https://github.com/kazurayam/ContextTrace/tree/master/app>. Let’s go through this sample project, which will propose a untility class `com.kazurayam.ks.ContextTrace`.

## Original sample code

As the document tells, the `WebUI.takeScreenshot` keyword takes an argument `fileName`, which is optional. What value will the `fileName` take as default? So let me check it hands-on. I created a testcase script

- [app/Test Cases/Main Test Cases/TC2\_Verify Successful Appointment](https://github.com/kazurayam/ContextTrace/blob/develop/app/Scripts/Main%20Test%20Cases/TC2_Verify%20Successful%20Appointment/Script1759213297502.groovy)

Also I made a test suite

- [app/Test Suites/TS](https://github.com/kazurayam/ContextTrace/blob/master/app/Test%20Suites/TS.ts)

The test suite calls the aforementioned testcase. When I ran the test suite, I got the result in the `Reports` folder.

    $ tree Reports/20251004_204203
    Reports/20251004_204203
    └── TS
        └── 20251004_204203
            ├── 1759578150377.png
            ├── 1759578157116.png
            ├── 1759578166156.png
            ├── 1759578172001.png
            ├── 20251004_204203.csv
            ├── 20251004_204203.html
            ├── console0.log
            ├── execution.properties
            ├── execution.uuid
            ├── execution0.log
            ├── JUnit_Report.xml
            ├── testCaseBinding
            └── tsc_id.txt

    3 directories, 13 files

As you see, the `WebUI.takeScreenshot()` generated a PNG file named with 13 digits appended with postfix `.png`, like `1759578150377.png`.

> What is `1759578150377`? --- It is the value of `new java.util.Date().getTime()`; the [Current Epoch Unix Timestamp](https://www.unixtimestamp.com/).

This file name tells me nothing what the PNG image presents. Therefore I can not re-use the PNG files named in this format. These PNG files are just useless for me.

## Awkward solution

How can I give screenshot files better names? --- It’s easy. As the doc [`WebUI.takeScreenshot`](https://docs.katalon.com/katalon-studio/keywords/keyword-description-in-katalon-studio/web-ui-keywords/webui-take-screenshot) tells, I can specify `fileName` as the 1st argument.

It is a good idea to name the screenshot files with the TestCaseId. I can do it easily. See the test cases source code:

- [app/Test Cases/Main Test Cases/TC2\_Verify Successful Appointment - awkward](https://github.com/kazurayam/ContextTrace/blob/master/app/Scripts/Main%20Test%20Cases/TC2_Verify%20Successful%20Appointment%20-%20awkward/Script1759213297502.groovy)

<!-- -->

    ...
    WebUI.openBrowser(GlobalVariable.G_SiteURL)

    WebUI.takeScreenshot('1-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png')
    ...

When I ran the [app/Test Suites/TS - awkward](https://github.com/kazurayam/TestContextTrace/blob/master/app/Test%20Suites/TS%20-%20awkward.ts), I got the following output in the Console:

    $ tree Reports/20251004_212933
    Reports/20251004_212933
    └── TS - awkward
        └── 20251004_212933
            ├── 1-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png
            ├── 2-Main_Test_Cases-TC_Verify_Successful_Appointment_-_improved,Common_Test_Cases-Login_-_awkward.png
            ├── 20251004_212933.csv
            ├── 20251004_212933.html
            ├── 3-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png
            ├── 4-Main_Test_Cases-TC_Verify_Successful_Appointment_-_awkward.png
            ├── console0.log
            ├── execution.properties
            ├── execution.uuid
            ├── execution0.log
            ├── JUnit_Report.xml
            ├── testCaseBinding
            └── tsc_id.txt

    3 directories, 13 files

The PNG files are named better than the original code --- the sequence number 1,2,3,4 followed by Test Case ID. Even in the case where 2 or more Test Cases are chained by `WebUI.callTestCase()` keyword, the file name contains the full information of the chained test cases.

But I hate the test case source code. It is a terrible idea to hard-code a string like `"2-Main_Test_Cases-TC2_Verify_Successful_Appointment_-improved,Common_Test_Cases-Login-_awkward.png"` in the script. Anyone can imagin, such string literals will make the maintenance job a nightmare.

## Improved solution

The `com.kazurayam.ks.ContextTrace` class can improve my code. See the [README](https://github.com/kazurayam/ContextTrace) how to introduce it. See the test cases source code:

- [app/Test Cases/Main Test Cases/TC2\_Verify Successful Appointment - improved](https://github.com/kazurayam/ContextTrace/blob/develop/app/Scripts/Main%20Test%20Cases/TC2_Verify%20Successful%20Appointment%20-%20improved/Script1759213297502.groovy)

<!-- -->

    ....
    WebUI.openBrowser(GlobalVariable.G_SiteURL)

    WebUI.takeScreenshot('1-' + ContextTrace.escapedTestCaseIdChain() + '.png')
    ....

As you see, **the test case source code now contains no lenthy string literal, which is replaced with a call to `ContextTrace.escapedTestCaseIdChain()`**. This style makes the code much more maintainable.

When I ran the [app/Test Suites/TS - improved](https://github.com/kazurayam/TestContextTrace/blob/master/app/Test%20Suites/TS%20-%20improved.ts), I got the following output in the Console:

    $ tree Reports/20251004_213229
    Reports/20251004_213229
    └── TS - improved
        └── 20251004_213229
            ├── 1-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_improved.png
            ├── 2-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_improved,Common_Test_Cases-Login_-_improved.png
            ├── 20251004_213229.csv
            ├── 20251004_213229.html
            ├── 3-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_improved.png
            ├── 4-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_improved.png
            ├── console0.log
            ├── execution.properties
            ├── execution.uuid
            ├── execution0.log
            ├── JUnit_Report.xml
            ├── testCaseBinding
            └── tsc_id.txt

    3 directories, 13 files

This output is just similar to the "awkward solution".

## Modularized solution

The "improved solution" generates 4 screenshot files. This means, the test case scripts repeats the following code 4 times:

    WebUI.takeScreenshot('1-' + ContextTrace.escapedTestCaseIdChain() + '.png')

I want to make the code even simpler. I want to have a custom keyword that encapsulates the statements of calling `WebUI.takeScreenshot` and `ContextTrace.escapedTestCaseIdChain()`. So I made one more code set.

- [app/Test Cases/Main Test Cases/TC2\_Verify Successful Appointment - modularized](https://github.com/kazurayam/ContextTrace/blob/develop/app/Scripts/Main%20Test%20Cases/TC2_Verify%20Successful%20Appointment%20-%20modularized/Script1759068159344.groovy)

<!-- -->

    ....
    WebUI.openBrowser(GlobalVariable.G_SiteURL)

    CustomKeywords.'my.Screenshooter.takeScreenshot'()
    ....

The `my.Screenshooter` class is a short Groovy class, as follows:

- [my.Screenshooter](https://github.com/kazurayam/ContextTrace/blob/master/app/Keywords/my/Screenshooter.groovy)

When I ran the [app/Test Suites/TS - modularized](https://github.com/kazurayam/TestContextTrace/blob/master/app/Test%20Suites/TS%20-%20modularized.ts), I got the following output in the Console:

    $ tree Reports/20251004_213431
    Reports/20251004_213431
    └── TS - modularized
        └── 20251004_213431
            ├── 20251004_213431.csv
            ├── 20251004_213431.html
            ├── console0.log
            ├── execution.properties
            ├── execution.uuid
            ├── execution0.log
            ├── JUnit_Report.xml
            ├── snapshots
            │   ├── 1-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_modularized.png
            │   ├── 2-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_modularized,Common_Test_Cases-Login_-_modularized.png
            │   ├── 3-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_modularized.png
            │   └── 4-Main_Test_Cases-TC2_Verify_Successful_Appointment_-_modularized.png
            ├── testCaseBinding
            └── tsc_id.txt

    4 directories, 13 files

This is the final result. I like it.

## Conclusion

The `com.kazurayam.ks.ContextTrace` class enables Katalon Test Case scripts to get its own TestCaseId programatically. You can use value returned by `ContextTrace.escapedTestCaseIdChain()` as screenshot file name. You would be able to find more usecases of it.
