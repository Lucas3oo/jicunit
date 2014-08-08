package org.jicunit.framework;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jicunit.framework.internal.InContainerTestRunner;
import org.jicunit.framework.internal.JicUnitServletClient;
import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.samples.TestSampleJunit3;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Tests the JUnit3 base class of JicUnitTestCase
 * 
 * @author lucas
 *
 */
public class JicUnitTestCaseTest {

  private JicUnitServletClient mClient = new JicUnitServletClient();
  
  @Test
  public void testProcessResult() throws Throwable {

    Document document = runTest(TestSampleJunit3.class.getName(), "testDoSomething");
    
    // next should not throw any exception or error
    mClient.processResults(document);
  }

  @Test
  public void testProcessResultThatFailes() throws ClassNotFoundException {
    Document document = runTest(TestSampleJunit3.class.getName(), "testDoSomethingThatFailes");
    // next should throw exception or error
    try {
      mClient.processResults(document);
      fail("There should have been an exception");
    } catch (Throwable e) {
      assertTrue("The exception must be of AssertionError", e instanceof AssertionError);
    }
  }

  @Test
  public void testProcessResultThatErrors() throws ClassNotFoundException {
    Document document = runTest(TestSampleJunit3.class.getName(), "testDoSomethingThatErrors");
    // next should throw exception or error
    try {
      mClient.processResults(document);
      fail("There should have been an exception");
    } catch (Throwable e) {
    }
  }

 
  private Document runTest(String testClassName, String methodName) throws ClassNotFoundException {

    InContainerTestRunner testRunner = new InContainerTestRunner();
    TestDescription testDescription = new TestDescription(methodName + "(" + testClassName + ")", testClassName);

    testDescription = testRunner.runTest(getClassLoader(), testDescription);

    String xmlResult = testRunner.resultAsXml(testDescription);

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(new InputSource(new StringReader(xmlResult)));
      return document;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }

}
