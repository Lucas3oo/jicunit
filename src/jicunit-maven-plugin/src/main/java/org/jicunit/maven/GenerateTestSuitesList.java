package org.jicunit.maven;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.DirectoryScanner;


public class GenerateTestSuitesList {

  private String mOutputDirectory;
  private String mTestClassesDirectory;
  private Log mLog;
  private List<String> mIncludes;
  private List<String> mExcludes;

  /**
   * 
   * @param outputDirectory to where the test-suites.txt file shall be generated to
   * @param testClassesDirectory where the tests classes are
   */
  public GenerateTestSuitesList(Log log, String outputDirectory, String testClassesDirectory, 
      List<String> includes, List<String> excludes) {
    mLog = log;
    mOutputDirectory = outputDirectory;
    mTestClassesDirectory = testClassesDirectory;
    mIncludes = includes;
    mExcludes = excludes;
  }

  public void generate() throws IOException {
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(mTestClassesDirectory);
    scanner.addDefaultExcludes();
    
    
    String[] includeArray = mIncludes.toArray(new String[] {});
    String[] excludeArray = mExcludes.toArray(new String[] {});
    
    scanner.setIncludes(includeArray);
    scanner.setExcludes(excludeArray);
    scanner.scan();
    String[] includeFiles = scanner.getIncludedFiles();
    
    File file = new File(mOutputDirectory + File.separator + "test-suites.txt");
    FileWriter fileWriter = new FileWriter(file);
    
    
    for (String fileName : includeFiles) {
      mLog.debug(fileName);
      String className = fileName.substring(0, fileName.lastIndexOf('.'));
      className = className.replace('/', '.');
      mLog.debug(className);
      fileWriter.write(className + "\n");
    }
    
    fileWriter.flush();
    fileWriter.close();
    
    mLog.info("Generated list of test classes at " + file.getAbsolutePath());

    
  }

}
