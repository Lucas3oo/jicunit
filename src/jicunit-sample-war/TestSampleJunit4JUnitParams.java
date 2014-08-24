package com.example.jicunit.sample;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.jicunit.framework.JicUnitRunner;
import org.jicunit.framework.RunInContainerWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * Sample test used in testing the framework using the JUnitParamsRunner runner
 * 
 * @author lucas
 *
 */
@RunWith(JicUnitRunner.class)
// @RunInContainerWith(JUnitParamsRunnerProxyRunner.class)
@RunInContainerWith(JUnitParamsRunner.class)
public class TestSampleJunit4JUnitParams {

  @Rule
  public TestName mName = new TestName();

  public String getName() {
    return mName.getMethodName();
  }



  @Test
  @Parameters(method = "parametersForIsAdult")
  public void isAdult(Person person, boolean valid) throws Exception {
    System.out.println("in isAdult " + getName() + " using thread "
        + Thread.currentThread().getName());
    assertEquals(valid, person.isAdult());
  }

  @SuppressWarnings("unused")
  private Object[] parametersForIsAdult() {
    return $(
                 $(new Person(13), false),
                 $(new Person(22), true)
            );
  }

}

class Person {
  private int age;

  public Person(int age) {
    this.age = age;
  }

  public boolean isAdult() {
    return age >= 18;
  }

  @Override
  public String toString() {
    return "Person of age: " + age;
  }

}
