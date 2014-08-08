package com.example.jicunit.sample;

import java.util.Arrays;

import org.jicunit.framework.JicUnitRunner;
import org.jicunit.framework.RunInContainerWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;


/**
 * Sample test used in testing the framework
 * 
 * @author lucas
 *
 */
@RunWith(JicUnitRunner.class)
@RunInContainerWith(Parameterized.class)
public class TestSampleJunit4Parameterized {
  
  @Rule
  public TestName mName = new TestName();
  
  @Parameter(0)
  public String mFunkyData;

  @Parameters(name = "{index}: FunkyData: {0}")
  public static Iterable<Object[]> data() {
    return Arrays.asList(
        new Object[][] { 
            { "Funky4711" },
            { "Goo Duded" }
            });
  }  
  
  public String getName() {
    return mName.getMethodName();
  }
  
  
  @Test
  public void testDoThis() throws Exception {
    System.out.println("hello world JUnit4 " + getName() + " using thread " + Thread.currentThread().getName());
  }

  @Test
  public void testDoThat() throws Exception {
    System.out.println("hello world JUnit4 " + getName() + " using thread " + Thread.currentThread().getName());
  }
  
    
}
