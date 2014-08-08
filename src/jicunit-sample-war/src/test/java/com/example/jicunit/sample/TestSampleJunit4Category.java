package com.example.jicunit.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.jicunit.framework.JicUnitRunner;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * Sample test used in testing the framework
 * It uses Category annotation
 * 
 * @author lucas
 *
 */
@RunWith(JicUnitRunner.class)
public class TestSampleJunit4Category {
  
  @Rule
  public TestName mName = new TestName();
    
  public String getName() {
    return mName.getMethodName();
  }
  
  
  @Category(PositiveTestCategory.class)
  @Test
  public void testDoSomething() throws Exception {
    System.out.println("hello world JUnit4 " + getName()  + " using thread " + Thread.currentThread().getName());
  }

  @Category(NegativeTestCategory.class)
  @Test
  public void testDoSomethingThatFailes() throws Exception {
    assertEquals("Expected success status", 200, 500);
  }

  @Test
  @Ignore
  public void testDoSomethingIgnore() throws Exception {
    System.out.println("This should not be printed");
    fail("This test should not be run since it has @Ignore");
  }
  
  @Category(NegativeTestCategory.class)
  @Test
  public void testDoSomethingThatErrors() throws Exception {
    throw new IllegalArgumentException("The value is illegal");
  }
    
 
}
