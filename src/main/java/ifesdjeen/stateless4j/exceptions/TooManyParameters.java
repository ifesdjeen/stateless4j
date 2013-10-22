package ifesdjeen.stateless4j.exceptions;

public class TooManyParameters extends StateMachineConfigurationException {

  private static String FORMAT = "Too many parametres";
  private final int expected;
  private final int actual;

  public TooManyParameters(int expected, int actual) {
    this.expected = expected;
    this.actual = actual;
  }
  public String getMessage() {
    return String.format(FORMAT, expected, actual);
  }

}
