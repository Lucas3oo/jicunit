package com.example.jicunit.sample;

import org.jicunit.framework.JicUnitTestCase;

/**
 * Sample test used in testing the framework
 * 
 * @author lucas
 *
 */
public class TestSampleJunit3 extends JicUnitTestCase {
  
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // when run in container the setUp must not be called in the "local" run, only in the container run.
    System.out.println("in setUp for " + getName() + " using thread " + Thread.currentThread().getName());
  }
  
  @Override
  protected void tearDown() throws Exception {
    // when run in container the tearDown must not be called in the "local" run, only in the container run.
    System.out.println("in tearDown for " + getName() + " using thread " + Thread.currentThread().getName());
    super.tearDown();
  }
  
  public void testDoSomething() throws Exception {
    System.out.println("hello world JUnit3" + " using thread " + Thread.currentThread().getName());
  }

  public void testDoSomethingThatFailes() throws Exception {
    assertEquals("Expected success status", 200, 500);
  }

  public void testDoSomethingThatErrors() throws Exception {
    throw new IllegalArgumentException("The value is illegal");
  }
  
  
}
