package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.delegates.Func;
import com.googlecode.stateless4j.exceptions.TriggerIgnoredException;

public class IgnoredTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
  public IgnoredTriggerBehaviour(TTrigger trigger, Func<Boolean> guard) {
    super(trigger, guard);
  }

  public TState ResultsInTransitionFrom(TState source, Object... args) throws TriggerIgnoredException {
    throw new TriggerIgnoredException();
  }
}