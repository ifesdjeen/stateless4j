package com.googlecode.stateless4j.triggers;

import com.googlecode.stateless4j.delegates.Func;
import com.googlecode.stateless4j.delegates.Func2;

import javax.annotation.Nonnull;

public class DynamicTriggerBehaviour<TState, TTrigger> extends TriggerBehaviour<TState, TTrigger> {
  Func2<Object[], TState> destination;

  public DynamicTriggerBehaviour(TTrigger trigger,
                                 @Nonnull Func2<Object[], TState> destination,
                                 Func<Boolean> guard) {
    super(trigger, guard);
    this.destination = destination;
  }

  public TState ResultsInTransitionFrom(TState source, Object... args) {
    return destination.call(args);
  }
}