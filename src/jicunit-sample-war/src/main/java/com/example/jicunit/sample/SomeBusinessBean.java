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
  
  public int parseValRuntimeExeption(String value) {
    try {
      int result = Integer.parseInt(value);
      return result;
    } catch (Exception e) {
      throw new RuntimeException("The value could not be parsed", e);
    }
  }
  
}
