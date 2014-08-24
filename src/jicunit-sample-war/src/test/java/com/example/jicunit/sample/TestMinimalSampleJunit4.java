package com.example.jicunit.sample;

import org.jicunit.framework.JicUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JicUnitRunner.class)
public class TestMinimalSampleJunit4 {
  @Test
  public void testDoSomething() throws Exception {
    System.out.println("hello world JUnit4"  + " using thread " + Thread.currentThread().getName());
  }
}
