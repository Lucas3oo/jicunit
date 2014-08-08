package com.example.jicunit.sample;

public class SomeBusinessBean {

  public int calculate(int value) {
    int result = value + 4711;
    return result;
  }

  public int parseVal(String value) {
    int result = Integer.parseInt(value);
    return result;
  }
  
  
}
