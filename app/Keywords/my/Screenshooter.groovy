package my

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.ks.ContextTrace
import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.logging.KeywordLogger

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public class Screenshooter {

	private static KeywordLogger logger = KeywordLogger.getInstance(Screenshooter.class)
	private static Path outDir
	private static int count

	static {
		Path logFolder = Paths.get(logger.getLogFolderPath())
		if ( ! logFolder.toString().contains("Reports")) {
			logFolder = Paths.get(RunConfiguration.getProjectDir()).resolve('build')   // the build directory under the project directory
		}
		outDir = logFolder.resolve('snapshots')
		if (!Files.exists(outDir)) {
			Files.createDirectories(outDir)
		}
		initialize()
	}

	static void initialize() {
		count = 0
	}

	/**
	 * Will take a screenshot of the current browser window at that time, save the PNG image into a file.
	 * Where to locate the file?
	 * If the keyword is called inside a Test Suite scope, then the file will be inside the `Reports` folder of the katalon project.
	 *
	 *     <projectDir>/Reports/yyyyMMdd_hhmmss/<TestSuiteName>/yyyyMMdd_hhmmss/snapshots/<escapedTestCaseIdChain>-N.png
	 *
	 * where N will be 1, 2, 3, ... The N identifies each image with unique name taken inside a Test Case.
	 *
	 * If the keyword is called outside a Test Suite scope (in other words, you ran a Test Case directly),
	 * then the file will be insde the `build` folder of the katalon project.
	 *
	 *     <projectDir>/build/snapshots/<escapedTestCaseIdChain>-N.png
	 *
	 * @return the Path of the file where the screenshot was saved
	 */
	@Keyword
	Path takeScreenshot() {
		count += 1
		String testCaseIdChain = ContextTrace.escapedTestCaseIdChain()
		String fileName = resolveFileName(count, testCaseIdChain)
		Path imgPath = outDir.resolve(fileName)
		Files.createDirectories(imgPath.getParent())
		//
		WebUI.takeScreenshot(imgPath.toString())
		//
		return imgPath
	}

	private String resolveFileName(int count, String name, String ext = '.png') {
		return count + '-' + name + ext
	}
}
