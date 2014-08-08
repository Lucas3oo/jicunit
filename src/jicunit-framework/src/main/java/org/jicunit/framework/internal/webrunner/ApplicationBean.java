package org.jicunit.framework.internal.webrunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.jicunit.framework.internal.model.TestDescription;
import org.junit.runner.Description;
import org.junit.runner.Request;


/**
 * Main class for the web GUI
 * 
 * @author lucas
 *
 */
//@ManagedBean(name = "applicationBean", eager=true)
//@ApplicationScoped
@Named("applicationBean")
@ApplicationScoped
public class ApplicationBean implements Serializable  {

  private static final long serialVersionUID = 1L;

  private transient TestDescription mTestDescription;
  
  public ApplicationBean() {

  }
  
  @PostConstruct
  public void init() throws IOException {
    List<String> testSuiteNames = loadTestSuitesFromFile();
    loadTestSuites(testSuiteNames);
  }
  
  protected List<String> loadTestSuitesFromFile() throws IOException {
    List<String> testSuiteNames = new ArrayList<String>();
    final FacesContext facesContext = FacesContext.getCurrentInstance();
    final ExternalContext externalContext = facesContext.getExternalContext();
    try(InputStream stream = externalContext.getResourceAsStream("/WEB-INF/test-suites.txt");
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader)) {
      String testSuitesName;
      while ((testSuitesName = reader.readLine()) != null) {
        testSuiteNames.add(testSuitesName);
      }      
    }
    return testSuiteNames;
  }

  protected void loadTestSuites(List<String> testSuiteNames) {
    // hack to get around that getRowsByDepth doesn't work on treeTable
    // create a dummy root that will not be displayed
    Description rootDesc = Description.createSuiteDescription("root", (Annotation[])null);

    for (String testSuiteName : testSuiteNames) {
      try {
        Description desc = createDescription(testSuiteName);
        rootDesc.addChild(desc);
      } catch (ClassNotFoundException e) {
        // TODO log that the class was not found
      }
    }
    
    mTestDescription = createTestDescriptionFromDescription(rootDesc);
  }

  /**
   * Creates our own model for a test and its status from the JUnit Description
   * @return testDescription
   */
  protected TestDescription createTestDescriptionFromDescription(Description description) {
    String name = description.getDisplayName();
    TestDescription testDescription = new TestDescription(name, description.getClassName());
    
    for (Description child : description.getChildren()) {
      TestDescription testDescriptionChild = createTestDescriptionFromDescription(child);
      testDescription.addTestDescription(testDescriptionChild);
    }
    return testDescription;
  }
  
  
  protected Description createDescription(String testSuiteName) throws ClassNotFoundException {
    ClassLoader classLoader = getClassLoader();
    Class<?> clazz = classLoader.loadClass(testSuiteName);
    Request request = Request.aClass(clazz);
    Description description = request.getRunner().getDescription();
    return description;
  }
  
  
  /**
   * 
   * @return the class loader that will be used to load the tests classes with
   */
  protected ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
  
  
  public TestDescription getTestDescription() {
    return mTestDescription;
  }
  
  
}
