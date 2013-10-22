package ifesdjeen.stateless4j.triggers;

import ifesdjeen.stateless4j.delegates.Func;
import ifesdjeen.stateless4j.exceptions.TriggerIgnoredException;


public abstract class TriggerBehaviour<TState, TTrigger> {
  final TTrigger _trigger;
  final Func<Boolean> _guard;

  protected TriggerBehaviour(TTrigger trigger, Func<Boolean> guard) {
    _trigger = trigger;
    _guard = guard;
  }

  public TTrigger getTrigger() {
    return _trigger;
  }

  public Boolean isGuardConditionMet() {
    return _guard.call();
  }

  public abstract TState ResultsInTransitionFrom(TState source, Object... args) throws TriggerIgnoredException;
}
