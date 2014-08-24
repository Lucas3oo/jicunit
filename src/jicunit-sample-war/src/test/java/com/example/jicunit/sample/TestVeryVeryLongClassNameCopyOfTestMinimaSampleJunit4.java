package com.example.jicunit.sample;

import static org.junit.Assert.assertEquals;

import org.jicunit.framework.JicUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JicUnitRunner.class)
public class TestVeryVeryLongClassNameCopyOfTestMinimaSampleJunit4 {
  @Test
  public void testDoSomethingThatFailes() throws Exception {
    assertEquals("Very long text. bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla bla Expected success status", 200, 500);
  }
}
