package org.jicunit.framework;

import static org.junit.Assert.assertEquals;

import org.jicunit.framework.internal.InContainerTestRunner;
import org.jicunit.framework.internal.model.TestDescription;
import org.jicunit.framework.internal.model.TestDescription.Status;
import org.jicunit.framework.samples.TestSampleJunit3;
import org.jicunit.framework.samples.TestSampleJunit4;
import org.junit.Test;

public class InContainerTestRunnerTest {

  /**
   * Tests the JicUnitTestRunner.runTest method
   * @throws ClassNotFoundException 
   */
  @Test
  public void testRunTestJunit3() throws ClassNotFoundException {
    String testClassName = TestSampleJunit3.class.getName();
    executeTest(testClassName);
  }

  /**
   * Tests the JicUnitTestRunner.runTest method
   * @throws ClassNotFoundException 
   */
  @Test
  public void testRunTestJunit4() throws ClassNotFoundException {
    String testClassName = TestSampleJunit4.class.getName();
    executeTest(testClassName);
  }

  /**
   * Tests the JicUnitTestRunner.runTest method with Junit4 test that has Ignore annotation
   * @throws ClassNotFoundException 
   */
  @Test
  public void testRunTestJunit4Ignore() throws ClassNotFoundException {
    String testClassName = TestSampleJunit4.class.getName();
    
    // test ignore
    InContainerTestRunner testRunner = new InContainerTestRunner();
    TestDescription testDescription = new TestDescription( "testDoSomethingIgnore(" + testClassName + ")", testClassName);
    testDescription = testRunner.runTest(getClassLoader(),testDescription);
    assertEquals("There should be one test ignored", Status.Ignored, testDescription.getStatus());
  }
  

  
  private void executeTest(String testClassName) throws ClassNotFoundException {
    InContainerTestRunner testRunner = new InContainerTestRunner();
    TestDescription testDescription;
    testDescription = new TestDescription( "testDoSomething(" + testClassName + ")", testClassName);
    testDescription = testRunner.runTest(getClassLoader(),testDescription);
    assertEquals("There should be one test ok", Status.OK, testDescription.getStatus());
    
    testDescription = new TestDescription( "testDoSomethingThatFailes(" + testClassName + ")", testClassName);
    testDescription = testRunner.runTest(getClassLoader(), testDescription);
    assertEquals("There should be one test Failure", Status.Failure, testDescription.getStatus());

    testDescription = new TestDescription( "testDoSomethingThatErrors(" + testClassName + ")", testClassName);
    testDescription = testRunner.runTest(getClassLoader(), testDescription);
    assertEquals("There should be one test Error", Status.Error, testDescription.getStatus());
  
  }  

  private ClassLoader getClassLoader() {
    return getClass().getClassLoader();
  }
  
}
