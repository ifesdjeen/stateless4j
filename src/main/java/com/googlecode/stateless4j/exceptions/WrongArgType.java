package com.googlecode.stateless4j.exceptions;

public class WrongArgType extends StateMachineConfigurationException {

  private static String FORMAT = "Argument of type `%s` required in position `%d`";
  private final Class<?> argType;
  private final int actual;

  public WrongArgType(Class<?> argType, int actual) {
    this.argType = argType;
    this.actual = actual;
  }
  public String getMessage() {
    return String.format(FORMAT, argType.toString(), actual);
  }

}
