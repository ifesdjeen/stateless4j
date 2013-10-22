package ifesdjeen.stateless4j.triggers;

import ifesdjeen.stateless4j.delegates.Func;
import ifesdjeen.stateless4j.exceptions.TriggerIgnoredException;

public class IgnoredTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
  public IgnoredTriggerBehaviour(TTrigger trigger, Func<Boolean> guard) {
    super(trigger, guard);
  }

  public TState ResultsInTransitionFrom(TState source, Object... args) throws TriggerIgnoredException {
    throw new TriggerIgnoredException();
  }
}
