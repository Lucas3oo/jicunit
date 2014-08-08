package org.jicunit.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jicunit.framework.samples.Junit4CategorySuite;
import org.jicunit.framework.samples.TestSampleJunit4;
import org.jicunit.framework.samples.TestSampleJunit4Parameterized;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.Result;

/**
 * Test different runner and other advanced JUnit 4 stuff
 * 
 * @author lucas
 *
 */
public class AdvancedIntegrationTest extends IntegrationTestBase {

 
  
  @Test
  public void testParameterized() {
    // this will result in 4 tests being run. The class has two methods and two parameters in the array 2 -> 2*2=4
    Result result = runTests(TestSampleJunit4Parameterized.class);
    //System.out.println(result.getFailures());
    assertTrue("The test should succeed", result.wasSuccessful());
    assertEquals("The number of tests run", 4, result.getRunCount());
    assertEquals("The ignore count", 0, result.getIgnoreCount());
    
}
  
  @Test
  public void testDoSomethingIgnore() {
    try {
      Result result = runTest(TestSampleJunit4.class, "testDoSomethingIgnore");
      assertEquals("There should be one ignored test", 1, result.getIgnoreCount());
    } catch (Throwable e) {
      e.printStackTrace();
      fail("There should not be an exception but was " + e);
    }
  }
  

  // test both category and suite since category is a suite 
  @Test
  public void testCategorySuite() {
    Request request = Request.aClass(Junit4CategorySuite.class);
    Result result = sJUnitCore.run(request);
    assertTrue("The test should succeed", result.wasSuccessful());
    assertEquals("There should be only one positive test in the suite", 1, result.getRunCount());
  }
  
  
}
