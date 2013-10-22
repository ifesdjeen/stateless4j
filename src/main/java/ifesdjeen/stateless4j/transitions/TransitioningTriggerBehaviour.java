package ifesdjeen.stateless4j.transitions;

import ifesdjeen.stateless4j.delegates.Func;
import ifesdjeen.stateless4j.triggers.TriggerBehaviour;

public class TransitioningTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
  final TState _destination;

  public TransitioningTriggerBehaviour(TTrigger trigger, TState destination, Func<Boolean> guard) {
    super(trigger, guard);
    _destination = destination;
  }

  public TState ResultsInTransitionFrom(TState source, Object... args) {
    return _destination;
  }
}
