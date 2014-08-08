package com.example.jicunit.sample;

import org.jicunit.framework.JicUnitTestCase;
public class TestMinimalSampleJunit3 extends JicUnitTestCase {
  public void testDoSomething() throws Exception {
    System.out.println("hello world JUnit3" + " using thread " + Thread.currentThread().getName());
  }
}
