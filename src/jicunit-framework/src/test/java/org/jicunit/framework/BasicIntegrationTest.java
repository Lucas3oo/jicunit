package org.jicunit.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.jicunit.framework.samples.TestSampleJunit3;
import org.jicunit.framework.samples.TestSampleJunit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test both Junit3 and Junit4 support end to end using Jetty web container
 * 
 * @author lucas
 *
 */
@RunWith(Parameterized.class)
public class BasicIntegrationTest extends IntegrationTestBase {

  @Parameter(0)
  public Class<?> mTestClass;

  @Parameters(name = "{index}: Class {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(
        new Object[][] { 
            { TestSampleJunit3.class },
            { TestSampleJunit4.class } });
  }

  
  
  @Test
  public void testDoSomething() {
    String methodName = "testDoSomething";
    try {
      runTest(mTestClass, methodName);
    } catch (Throwable e) {
      e.printStackTrace();
      fail("There should not be an exception but was " + e);
    }
  }

  @Test
  public void testThatFailes() {
    String methodName = "testDoSomethingThatFailes";
    try {
      runTest(mTestClass, methodName);
      fail("There should have been an exception");
    } catch (Throwable e) {
      assertTrue("The exception must be of AssertionError but was " + e, e instanceof AssertionError);
    }
  }

  @Test
  public void testThatErrors() throws Throwable {
    String methodName = "testDoSomethingThatErrors";
    try {
      runTest(mTestClass, methodName);
      fail("There should have been an exception");
    } catch (Throwable e) {
      assertEquals("The exception message should be 'The value is illegal'", "The value is illegal", e.getMessage());
    }
  }
  
  
}
