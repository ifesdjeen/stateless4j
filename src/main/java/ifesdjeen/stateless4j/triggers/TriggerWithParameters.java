package ifesdjeen.stateless4j.triggers;

import ifesdjeen.stateless4j.conversion.ParameterConversion;
import ifesdjeen.stateless4j.exceptions.StateMachineConfigurationException;
import ifesdjeen.stateless4j.exceptions.TooManyParameters;

import javax.annotation.Nonnull;

public abstract class TriggerWithParameters<TState, TTrigger> {
  final TTrigger underlyingTrigger;
  final Class<?>[] argumentTypes;

  /// <summary>
  /// Create a configured trigger.
  /// </summary>
  /// <param name="underlyingTrigger">Trigger represented by this trigger configuration.</param>
  /// <param name="argumentTypes">The argument types expected by the trigger.</param>
  public TriggerWithParameters(@Nonnull TTrigger underlyingTrigger, Class<?>... argumentTypes) {
    this.underlyingTrigger = underlyingTrigger;
    this.argumentTypes = argumentTypes;
  }

  /// <summary>
  /// Gets the underlying trigger value that has been configured.
  /// </summary>
  public TTrigger getTrigger() {
    return underlyingTrigger;
  }

  /// <summary>
  /// Ensure that the supplied arguments are compatible with those configured for this
  /// trigger.
  /// </summary>
  /// <param name="args"></param>
  public void ValidateParameters(@Nonnull Object[] args) throws StateMachineConfigurationException {
    ParameterConversion.Validate(args, argumentTypes);
  }
}
