package org.jicunit.framework.internal.webrunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jicunit.framework.internal.model.TestDescription;
import org.junit.runner.Description;
import org.junit.runner.Request;


/**
 * Main class for the web GUI
 * 
 * @author lucas
 *
 */
//@javax.faces.bean.ManagedBean(name = "applicationBean")
//@javax.faces.bean.ApplicationScoped
@javax.enterprise.context.ApplicationScoped
public class ApplicationBean implements Serializable  {

  private static Logger sLogger = Logger.getLogger(ApplicationBean.class.getName());
  
  private static final long serialVersionUID = 1L;

  private List<String> mTestSuiteNames;
  
  public ApplicationBean() {
  }
  
  @PostConstruct
  public void init() {
    mTestSuiteNames = loadTestSuitesFromFile();
  }
  
  protected List<String> loadTestSuitesFromFile()  {
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
    } catch (Exception e) {
      String msg = "Unable to read the test-suites.txt";
      sLogger.log(Level.SEVERE, msg, e);
      throw new RuntimeException(msg, e);
    }
    return testSuiteNames;
  }

  protected TestDescription loadTestSuites() {
    // hack to get around that getRowsByDepth doesn't work on treeTable
    // create a dummy root that will not be displayed
    Description rootDesc = Description.createSuiteDescription("all tests", (Annotation[])null);

    for (String testSuiteName : mTestSuiteNames) {
      try {
        Description desc = createDescription(testSuiteName);
        rootDesc.addChild(desc);
      } catch (ClassNotFoundException e) {
        // TODO log that the class was not found
      }
    }
    
    return createTestDescriptionFromDescription(rootDesc);
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
  
  
  /**
   * 
   * @param testSuiteName
   * @return JUnit Description
   * @throws ClassNotFoundException
   */
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
  
  
  public TestDescription createTestDescription() {
    return loadTestSuites();
  }
  
  public List<String> getTestSuiteNames() {
    return mTestSuiteNames;
  }
  
  public void setTestSuiteNames(List<String> testSuiteNames) {
    mTestSuiteNames = testSuiteNames;
  }
  
  
  
}
