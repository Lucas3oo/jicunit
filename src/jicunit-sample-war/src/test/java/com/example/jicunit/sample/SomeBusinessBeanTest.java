package com.example.jicunit.sample;

import static org.junit.Assert.assertEquals;

import org.jicunit.framework.JicUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.jicunit.sample.SomeBusinessBean;

@RunWith(JicUnitRunner.class)
public class SomeBusinessBeanTest {

  private SomeBusinessBean mSomeBusinessBean;

  @Before
  public void setUp() throws Exception {
    mSomeBusinessBean = new SomeBusinessBean();
  }

  @Test
  public void testCalculate() {
    int result = 4728;
    int val = mSomeBusinessBean.calculate(17);
    assertEquals("Must be same", result, val);
  }

  /**
   * this test will give error
   */
  @Test
  public void testParseWithError() {
    int result = 4728;
    int val = mSomeBusinessBean.parseVal("ade");
    assertEquals("Must be same", result, val);
  }
  
}
