
package org.jicunit.maven


import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.apache.maven.project.MavenProjectHelper

import java.util.List





/**
 * Generate additional files that must be included in the test WAR.
 * Files that will be generated into the WAR: 
 * WEB-INF/test-suites.txt 
 *
 * //requiresDependencyResolution compile
 * 
 * @goal jicunit-war
 * @phase process-test-classes 
 */
class JicUnitWarMojo
extends GroovyMojoSupport {

  /**
   * A list of &lt;include&gt; elements specifying the tests (by pattern) that should be included in testing. 
   * When not specified the default includes will be <code>
   * <br/>
   * &lt;includes&gt;<br/>
   * &nbsp;&lt;include&gt;**&#47;Test*.class&lt;/include&gt;<br/>
   * &nbsp;&lt;include&gt;**&#47;*Test.class&lt;/include&gt;<br/>
   * &nbsp;&lt;include&gt;**&#47;*TestCase.class&lt;/include&gt;<br/>
   * &lt;/includes&gt;<br/>
   * </code>
   * <p/>
   *
   * @parameter 
   */
  List<String> includes
  
  /**
   * A list of &lt;exclude&gt; elements specifying the tests (by pattern) that should be excluded in testing.
   * When not specified, the default excludes will be <code>
   * <br/>
   * &lt;excludes&gt;<br/>
   * &nbsp;&lt;exclude&gt;**&#47;*$*&lt;/exclude><br/>
   * &lt;/excludes&gt;<br/>
   * </code>
   * <p/>
   * (which excludes all inner classes).
   *
   * @parameter
   */
  List<String> excludes

  /**
   * Output directory. Should match the maven-war-plugin's webappDirectory. The directory where the webapp is built.
   *
   * @parameter default-value="${project.build.directory}/${project.build.finalName}"
   */
  String outputDirectory

  /**
   * Project
   *
   * @parameter default-value="${project}" 
   * @required
   * @readonly
   */
  MavenProject project

  void execute() {
    Log log = getLog()
    if (includes == null || includes.size() == 0) {
      includes = Arrays.asList(getDefaultIncludes())
    }
    if (excludes == null || excludes.size() == 0) {
      excludes = Arrays.asList(getDefaultExcludes())
    }
    String webInfDirecotry = outputDirectory + File.separator + "WEB-INF"
    ant.mkdir(dir:"$webInfDirecotry")
    
    log.debug("outputDirectory = $outputDirectory")
    log.debug("includes = $includes")
    log.debug("excludes = $excludes")
    log.debug("TestClassesDirectory = " + getTestClassesDirectory())
    
    
    GenerateTestSuitesList generator = new GenerateTestSuitesList(log, webInfDirecotry, 
      getTestClassesDirectory(), includes, excludes)
    generator.generate()

  }
  
  String[] getDefaultIncludes() {
    return [ "**/Test*.class", "**/*Test.class", "**/*TestCase.class" ]
  }
  
  String[] getDefaultExcludes() {
    return [ "**/*\$*" ]
  } 

  
  String getTestClassesDirectory() {
    return project.getBuild().getTestOutputDirectory()
  }
  
}