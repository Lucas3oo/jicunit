package com.example.jicunit.sample;

import static org.junit.Assert.assertEquals;

import org.jicunit.framework.JicUnitRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;


/**
 * Sample test used in testing the framework
 * 
 * @author lucas
 *
 */
@RunWith(JicUnitRunner.class)
public class TestSampleJunit4 {
  
  @Rule
  public TestName mName = new TestName();
    
  public String getName() {
    return mName.getMethodName();
  }
  
  @BeforeClass
  public static void setUpClass() {
    System.out.println("in setUpClass " + TestSampleJunit4.class.getSimpleName() + " using thread " + Thread.currentThread().getName());
  }  

  @AfterClass
  public static void tearDownClass() {
    System.out.println("in tearDownClass " + TestSampleJunit4.class.getSimpleName() + " using thread " + Thread.currentThread().getName());
  }
  
  @Before
  public void setUp() throws Exception {
    // when run in container the setUp must not be called in the "local" run, only in the container run.
    System.out.println("in setUp for " + getName() + " using thread " + Thread.currentThread().getName());
  }
  
  @After
  public void tearDown() throws Exception {
    // when run in container the tearDown must not be called in the "local" run, only in the container run.
    System.out.println("in tearDown for " + getName() + " using thread " + Thread.currentThread().getName());
  }
  
  @Category(PositiveTestCategory.class)
  @Test
  public void testDoSomething() throws Exception {
    System.out.println("hello world JUnit4"  + " using thread " + Thread.currentThread().getName());
  }

  @Category(PositiveTestCategory.class)
  @Test
  public void testDoSomethingLongRunning() throws Exception {
    System.out.println("hello world JUnit4"  + " using thread " + Thread.currentThread().getName());
    Thread.sleep(2 * 1000);
    System.out.println("hello world JUnit4"  + " using thread " + Thread.currentThread().getName() + " Done!");
  }

  @Category(PositiveTestCategory.class)
  @Test
  public void testDoSomethingLongerRunning() throws Exception {
    System.out.println("hello world JUnit4"  + " using thread " + Thread.currentThread().getName());
    Thread.sleep(4 * 1000);
    System.out.println("hello world JUnit4"  + " using thread " + Thread.currentThread().getName() + " Done!");
  }

  
  @Category(NegativeTestCategory.class)
  @Test
  public void testDoSomethingThatFailes() throws Exception {
    assertEquals("Expected success status", 200, 500);
  }

  @Test
  @Ignore
  public void testDoSomethingIgnore() throws Exception {
    System.out.println("hello world");
  }
  
  @Category(NegativeTestCategory.class)
  @Test
  public void testDoSomethingThatErrors() throws Exception {
    throw new IllegalArgumentException("The value is illegal");
  }
    
}
