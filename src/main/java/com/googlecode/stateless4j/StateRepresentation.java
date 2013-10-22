package com.googlecode.stateless4j;

import java.util.*;

import com.googlecode.stateless4j.delegates.Action1;
import com.googlecode.stateless4j.delegates.Action2;
import com.googlecode.stateless4j.resources.StateRepresentationResources;
import com.googlecode.stateless4j.transitions.Transition;
import com.googlecode.stateless4j.triggers.TriggerBehaviour;

import javax.annotation.Nonnull;

public class StateRepresentation<TState, TTrigger> {
  final TState _state;

  final Map<TTrigger, List<TriggerBehaviour<TState, TTrigger>>> _triggerBehaviours = new HashMap<>();

  final List<Action2<Transition<TState, TTrigger>, Object[]>> _entryActions = new ArrayList<>();
  final List<Action1<Transition<TState, TTrigger>>> _exitActions = new ArrayList<>();

  StateRepresentation<TState, TTrigger> _superstate; // null

  final List<StateRepresentation<TState, TTrigger>> _substates = new ArrayList<>();

  public StateRepresentation(TState state) {
    _state = state;
  }

  public Boolean CanHandle(TTrigger trigger) {
    try {
      TryFindHandler(trigger);
      return true;
    } catch (Exception e) {
    }
    return false;
  }

  public TriggerBehaviour<TState, TTrigger> TryFindHandler(TTrigger trigger) {
    try {
      return TryFindLocalHandler(trigger);
    } catch (Exception e) {
      return getSuperstate().TryFindHandler(trigger);
    }
  }

  TriggerBehaviour<TState, TTrigger> TryFindLocalHandler(TTrigger trigger/*, out TriggerBehaviour handler*/) throws Exception {
    List<TriggerBehaviour<TState, TTrigger>> possible;
    if (!_triggerBehaviours.containsKey(trigger)) {
      throw new Exception();
    }
    possible = _triggerBehaviours.get(trigger);

    List<TriggerBehaviour<TState, TTrigger>> actual = new ArrayList<>();
    for (TriggerBehaviour<TState, TTrigger> triggerBehaviour : possible) {
      if (triggerBehaviour.isGuardConditionMet()) {
        actual.add(triggerBehaviour);
      }
    }

    if (actual.size() > 1)
      throw new Exception(String.format(StateRepresentationResources.MultipleTransitionsPermitted,
                                        trigger, _state));

    TriggerBehaviour<TState, TTrigger> handler = actual.get(0);
    if (handler == null) {
      throw new Exception();
    }
    return handler;
  }

  public void AddEntryAction(final TTrigger trigger,
                             @Nonnull final Action2<Transition<TState, TTrigger>, Object[]> action) {
    _entryActions.add(new Action2<Transition<TState, TTrigger>, Object[]>() {
      public void doIt(Transition<TState, TTrigger> t, Object[] args) {
        if (t.getTrigger().equals(trigger))
          action.doIt(t, args);
      }
    });
  }

  public void AddEntryAction(@Nonnull Action2<Transition<TState, TTrigger>, Object[]> action) {
    _entryActions.add(action);
  }

  public void AddExitAction(@Nonnull Action1<Transition<TState, TTrigger>> action) {
    _exitActions.add(action);
  }

  public void Enter(@Nonnull Transition<TState, TTrigger> transition, Object... entryArgs) {
    if (transition.isReentry()) {
      ExecuteEntryActions(transition, entryArgs);
    } else if (!Includes(transition.getSource())) {
      if (_superstate != null)
        _superstate.Enter(transition, entryArgs);

      ExecuteEntryActions(transition, entryArgs);
    }
  }

  public void Exit(@Nonnull Transition<TState, TTrigger> transition) {
    if (transition.isReentry()) {
      ExecuteExitActions(transition);
    } else if (!Includes(transition.getDestination())) {
      ExecuteExitActions(transition);
      if (_superstate != null)
        _superstate.Exit(transition);
    }
  }

  void ExecuteEntryActions(@Nonnull Transition<TState, TTrigger> transition,
                           @Nonnull Object[] entryArgs) {
    for (Action2<Transition<TState, TTrigger>, Object[]> action : _entryActions)
      action.doIt(transition, entryArgs);
  }

  void ExecuteExitActions(@Nonnull Transition<TState, TTrigger> transition) {
    for (Action1<Transition<TState, TTrigger>> action : _exitActions)
      action.doIt(transition);
  }

  public void AddTriggerBehaviour(TriggerBehaviour<TState, TTrigger> triggerBehaviour) {
    List<TriggerBehaviour<TState, TTrigger>> allowed;
    if (!_triggerBehaviours.containsKey(triggerBehaviour.getTrigger())) {
      allowed = new ArrayList<TriggerBehaviour<TState, TTrigger>>();
      _triggerBehaviours.put(triggerBehaviour.getTrigger(), allowed);
    }
    allowed = _triggerBehaviours.get(triggerBehaviour.getTrigger());
    allowed.add(triggerBehaviour);
  }

  public StateRepresentation<TState, TTrigger> getSuperstate() {
    return _superstate;
  }

  public void setSuperstate(StateRepresentation<TState, TTrigger> value) {
    _superstate = value;
  }

  public TState getUnderlyingState() {
    return _state;
  }

  public void AddSubstate(@Nonnull StateRepresentation<TState, TTrigger> substate) {
    _substates.add(substate);
  }

  public Boolean Includes(TState state) {
    Boolean isIncluded = false;
    for (StateRepresentation<TState, TTrigger> s : _substates) {
      if (s.Includes(state)) {
        isIncluded = true;
      }
    }
    return _state.equals(state) || isIncluded;
  }

  public Boolean IsIncludedIn(TState state) {
    return _state.equals(state) ||
                   (_superstate != null && _superstate.IsIncludedIn(state));
  }

  @SuppressWarnings("unchecked")
  public List<TTrigger> getPermittedTriggers() {
    Set result = new HashSet<TTrigger>();

    for (TTrigger t : _triggerBehaviours.keySet()) {
      Boolean isOk = false;
      for (TriggerBehaviour<TState, TTrigger> v : _triggerBehaviours.get(t)) {
        if (v.isGuardConditionMet()) {
          isOk = true;
        }
      }
      if (isOk) {
        result.add(t);
      }
    }

    if (getSuperstate() != null) {
      result.addAll(getSuperstate().getPermittedTriggers());
    }

    return new ArrayList<TTrigger>(result);
  }
}