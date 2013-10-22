package com.googlecode.stateless4j;

import com.googlecode.stateless4j.delegates.Action;
import com.googlecode.stateless4j.delegates.Action1;
import com.googlecode.stateless4j.delegates.Action2;
import com.googlecode.stateless4j.delegates.Action3;
import com.googlecode.stateless4j.delegates.Action4;
import com.googlecode.stateless4j.delegates.Func;
import com.googlecode.stateless4j.delegates.Func2;
import com.googlecode.stateless4j.delegates.Func3;
import com.googlecode.stateless4j.delegates.Func4;
import com.googlecode.stateless4j.exceptions.SelfTransitionIgnoredOrReentrant;
import com.googlecode.stateless4j.transitions.Transition;
import com.googlecode.stateless4j.transitions.TransitioningTriggerBehaviour;
import com.googlecode.stateless4j.triggers.DynamicTriggerBehaviour;
import com.googlecode.stateless4j.triggers.IgnoredTriggerBehaviour;
import com.googlecode.stateless4j.triggers.TriggerWithParameters1;
import com.googlecode.stateless4j.triggers.TriggerWithParameters2;
import com.googlecode.stateless4j.triggers.TriggerWithParameters3;

import javax.annotation.Nonnull;

public class StateConfiguration<TState, TTrigger> {
  final StateRepresentation<TState, TTrigger> representation;
  final Func2<TState, StateRepresentation<TState, TTrigger>> lookup;
  final Func<Boolean> NoGuard = new Func<Boolean>() {
    public Boolean call() {
      return true;
    }
  };

  public StateConfiguration(StateRepresentation<TState, TTrigger> representation,
                            @Nonnull Func2<TState, StateRepresentation<TState, TTrigger>> lookup) {
    this.representation = representation;
    this.lookup = lookup;
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationState">The state that the trigger will cause a
  /// transition to.</param>
  /// <returns>The reciever.</returns>
  public StateConfiguration<TState, TTrigger> Permit(TTrigger trigger,
                                                     TState destinationState) throws SelfTransitionIgnoredOrReentrant {
    enforceNotIdentityTransition(destinationState);
    return publicPermit(trigger, destinationState);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationState">The state that the trigger will cause a
  /// transition to.</param>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be accepted.</param>
  /// <returns>The reciever.</returns>
  public StateConfiguration<TState, TTrigger> PermitIf(TTrigger trigger,
                                                       TState destinationState, Func<Boolean> guard)
          throws SelfTransitionIgnoredOrReentrant {
    enforceNotIdentityTransition(destinationState);
    return publicPermitIf(trigger, destinationState, guard);
  }

  /// <summary>
  /// Accept the specified trigger, execute exit actions and re-execute entry actions.
  /// Reentry behaves as though the configured state transitions to an identical sibling state.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <returns>The reciever.</returns>
  /// <remarks>
  /// Applies to the current state only. Will not re-execute superstate actions, or
  /// cause actions to execute transitioning between super- and sub-states.
  /// </remarks>
  public StateConfiguration<TState, TTrigger> PermitReentry(TTrigger trigger) {
    return publicPermit(trigger, representation.getUnderlyingState());
  }

  /// <summary>
  /// Accept the specified trigger, execute exit actions and re-execute entry actions.
  /// Reentry behaves as though the configured state transitions to an identical sibling state.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be accepted.</param>
  /// <returns>The reciever.</returns>
  /// <remarks>
  /// Applies to the current state only. Will not re-execute superstate actions, or
  /// cause actions to execute transitioning between super- and sub-states.
  /// </remarks>
  public StateConfiguration<TState, TTrigger> PermitReentryIf(TTrigger trigger, Func<Boolean> guard) {
    return publicPermitIf(trigger, representation.getUnderlyingState(), guard);
  }

  /// <summary>
  /// Ignore the specified trigger when in the configured state.
  /// </summary>
  /// <param name="trigger">The trigger to ignore.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> Ignore(TTrigger trigger) {
    return IgnoreIf(trigger, NoGuard);
  }

  /// <summary>
  /// Ignore the specified trigger when in the configured state, if the guard
  /// returns true..
  /// </summary>
  /// <param name="trigger">The trigger to ignore.</param>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be ignored.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> IgnoreIf(@Nonnull TTrigger trigger, Func<Boolean> guard) {
    representation.AddTriggerBehaviour(new IgnoredTriggerBehaviour<TState, TTrigger>(trigger, guard));
    return this;
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <param name="entryAction">Action to execute.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> OnEntry(@Nonnull final Action entryAction) {
    return OnEntry(new Action1<Transition<TState, TTrigger>>() {
      public void doIt(Transition<TState, TTrigger> t) {
        entryAction.doIt();
      }

      ;
    });
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> OnEntry(@Nonnull final Action1<Transition<TState, TTrigger>> entryAction) {
    representation.AddEntryAction(new Action2<Transition<TState, TTrigger>, Object[]>() {
      public void doIt(Transition<TState, TTrigger> arg1, Object[] arg2) {
        entryAction.doIt(arg1);
      };
    });
    return this;
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <param name="entryAction">Action to execute.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> OnEntryFrom(TTrigger trigger,
                                                          @Nonnull final Action entryAction) {
    return OnEntryFrom(trigger, new Action1<Transition<TState, TTrigger>>() {
      public void doIt(Transition<TState, TTrigger> arg1) {
        entryAction.doIt();
      }

      ;
    });
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> OnEntryFrom(TTrigger trigger,
                                                          @Nonnull final Action1<Transition<TState, TTrigger>> entryAction) {
    representation.AddEntryAction(trigger, new Action2<Transition<TState, TTrigger>, Object[]>() {
      public void doIt(Transition<TState, TTrigger> arg1, Object[] arg2) {
        entryAction.doIt(arg1);
      }

      ;
    });
    return this;
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public <TArg0> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters1<TArg0, TState, TTrigger> trigger,
                                                                  @Nonnull final Action1<TArg0> entryAction,
                                                                  final Class<TArg0> classe0) {
    return OnEntryFrom(trigger, new Action2<TArg0, Transition<TState, TTrigger>>() {
      public void doIt(TArg0 arg1, Transition<TState, TTrigger> arg2) {
        entryAction.doIt(arg1);
      }

      ;
    }, classe0);
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public <TArg0> StateConfiguration<TState, TTrigger> OnEntryFrom(@Nonnull TriggerWithParameters1<TArg0, TState, TTrigger> trigger,
                                                                  @Nonnull final Action2<TArg0, Transition<TState, TTrigger>> entryAction,
                                                                  final Class<TArg0> classe0) {
    representation.AddEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
      @SuppressWarnings("unchecked")
      public void doIt(Transition<TState, TTrigger> t, Object[] arg2) {
        entryAction.doIt((TArg0) arg2[0], t);
      }

      ;
    });
    return this;
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public <TArg0, TArg1> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger,
                                                                         @Nonnull final Action2<TArg0, TArg1> entryAction,
                                                                         final Class<TArg0> classe0,
                                                                         final Class<TArg1> classe1) {
    return OnEntryFrom(trigger, new Action3<TArg0, TArg1, Transition<TState, TTrigger>>() {
      public void doIt(TArg0 a0, TArg1 a1, Transition<TState, TTrigger> t) {
        entryAction.doIt(a0, a1);
      };
    }, classe0, classe1);
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public <TArg0, TArg1> StateConfiguration<TState, TTrigger> OnEntryFrom(@Nonnull TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger,
                                                                         @Nonnull final Action3<TArg0, TArg1, Transition<TState, TTrigger>> entryAction,
                                                                         final Class<TArg0> classe0,
                                                                         final Class<TArg1> classe1) {
    representation.AddEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
      @SuppressWarnings("unchecked")
      public void doIt(Transition<TState, TTrigger> t, Object[] args) {
        entryAction.doIt((TArg0) args[0],
                         (TArg1) args[1], t);
      };
    });
    return this;
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  /// <typeparam name="TArg2">Type of the third trigger argument.</typeparam>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger,
                                                                                @Nonnull final Action3<TArg0, TArg1, TArg2> entryAction,
                                                                                final Class<TArg0> classe0,
                                                                                final Class<TArg1> classe1,
                                                                                final Class<TArg2> classe2) {
    return OnEntryFrom(trigger, new Action4<TArg0, TArg1, TArg2, Transition<TState, TTrigger>>() {
      public void doIt(TArg0 a0, TArg1 a1, TArg2 a2, Transition<TState, TTrigger> t) {
        entryAction.doIt(a0, a1, a2);
      }

      ;
    }, classe0, classe1, classe2);
  }

  /// <summary>
  /// Specify an action that will execute when transitioning into
  /// the configured state.
  /// </summary>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  /// <typeparam name="TArg2">Type of the third trigger argument.</typeparam>
  /// <param name="entryAction">Action to execute, providing details of the transition.</param>
  /// <param name="trigger">The trigger by which the state must be entered in order for the action to execute.</param>
  /// <returns>The receiver.</returns>
  public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> OnEntryFrom(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger,
                                                                                @Nonnull final Action4<TArg0, TArg1, TArg2, Transition<TState, TTrigger>> entryAction,
                                                                                @Nonnull final Class<TArg0> classe0, final Class<TArg1> classe1, final Class<TArg2> classe2)
  {
    representation.AddEntryAction(trigger.getTrigger(), new Action2<Transition<TState, TTrigger>, Object[]>() {
      @SuppressWarnings("unchecked")
      public void doIt(Transition<TState, TTrigger> t, Object[] args) {
        entryAction.doIt(
                                (TArg0) args[0],
                                (TArg1) args[1],
                                (TArg2) args[2], t);
      }

      ;
    });
    return this;
  }

  /// <summary>
  /// Specify an action that will execute when transitioning from
  /// the configured state.
  /// </summary>
  /// <param name="exitAction">Action to execute.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> OnExit(@Nonnull final Action exitAction) {
    return OnExit(new Action1<Transition<TState, TTrigger>>() {

      public void doIt(Transition<TState, TTrigger> arg1) {
        exitAction.doIt();
      }
    });
  }

  /// <summary>
  /// Specify an action that will execute when transitioning from
  /// the configured state.
  /// </summary>
  /// <param name="exitAction">Action to execute, providing details of the transition.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> OnExit(@Nonnull Action1<Transition<TState, TTrigger>> exitAction) {
    representation.AddExitAction(exitAction);
    return this;
  }

  /// <summary>
  /// Sets the superstate that the configured state is a substate of.
  /// </summary>
  /// <remarks>
  /// Substates inherit the allowed transitions of their superstate.
  /// When entering directly into a substate from outside of the superstate,
  /// entry actions for the superstate are executed.
  /// Likewise when leaving from the substate to outside the supserstate,
  /// exit actions for the superstate will execute.
  /// </remarks>
  /// <param name="superstate">The superstate.</param>
  /// <returns>The receiver.</returns>
  public StateConfiguration<TState, TTrigger> SubstateOf(TState superstate) {
    StateRepresentation<TState, TTrigger> superRepresentation = lookup.call(superstate);
    representation.setSuperstate(superRepresentation);
    superRepresentation.AddSubstate(representation);
    return this;
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <returns>The reciever.</returns>
  public StateConfiguration<TState, TTrigger> PermitDynamic(TTrigger trigger,
                                                            final Func<TState> destinationStateSelector) {
    return PermitDynamicIf(trigger, destinationStateSelector, NoGuard);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <returns>The reciever.</returns>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  public <TArg0> StateConfiguration<TState, TTrigger> PermitDynamic(TriggerWithParameters1<TArg0, TState, TTrigger> trigger,
                                                                    Func2<TArg0, TState> destinationStateSelector) {
    return permitDynamicIf(trigger, destinationStateSelector, NoGuard);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <returns>The reciever.</returns>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  public <TArg0, TArg1> StateConfiguration<TState, TTrigger> PermitDynamic(TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger,
                                                                           Func3<TArg0, TArg1, TState> destinationStateSelector) {
    return permitDynamicIf(trigger, destinationStateSelector, NoGuard);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <returns>The reciever.</returns>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  /// <typeparam name="TArg2">Type of the third trigger argument.</typeparam>
  public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> PermitDynamic(TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger,
                                                                                  final Func4<TArg0, TArg1, TArg2, TState> destinationStateSelector) {
    return permitDynamicIf(trigger, destinationStateSelector, NoGuard);
  }


  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be accepted.</param>
  /// <returns>The reciever.</returns>
  public StateConfiguration<TState, TTrigger> PermitDynamicIf(TTrigger trigger,
                                                              @Nonnull final Func<TState> destinationStateSelector,
                                                              Func<Boolean> guard) {
    return publicPermitDynamicIf(trigger, new Func2<Object[], TState>() {

      public TState call(Object[] arg0) {
        return destinationStateSelector.call();
      }
    }, guard);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be accepted.</param>
  /// <returns>The reciever.</returns>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  public <TArg0> StateConfiguration<TState, TTrigger> permitDynamicIf(@Nonnull TriggerWithParameters1<TArg0, TState, TTrigger> trigger,
                                                                      @Nonnull final Func2<TArg0, TState> destinationStateSelector,
                                                                      Func<Boolean> guard) {
    return publicPermitDynamicIf(trigger.getTrigger(), new Func2<Object[], TState>() {
      @SuppressWarnings("unchecked")

      public TState call(Object[] args) {
        return destinationStateSelector.call((TArg0) args[0]);
      }
    }, guard);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be accepted.</param>
  /// <returns>The reciever.</returns>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  public <TArg0, TArg1> StateConfiguration<TState, TTrigger> permitDynamicIf(@Nonnull TriggerWithParameters2<TArg0, TArg1, TState, TTrigger> trigger,
                                                                             @Nonnull final Func3<TArg0, TArg1, TState> destinationStateSelector,
                                                                             Func<Boolean> guard)
  {
    return publicPermitDynamicIf(trigger.getTrigger(), new Func2<Object[], TState>() {
      @SuppressWarnings("unchecked")
      public TState call(Object[] args) {
        return destinationStateSelector.call((TArg0) args[0],
                                             (TArg1) args[1]);
      }
    }, guard);
  }

  /// <summary>
  /// Accept the specified trigger and transition to the destination state, calculated
  /// dynamically by the supplied function.
  /// </summary>
  /// <param name="trigger">The accepted trigger.</param>
  /// <param name="destinationStateSelector">Function to calculate the state
  /// that the trigger will cause a transition to.</param>
  /// <returns>The reciever.</returns>
  /// <param name="guard">Function that must return true in order for the
  /// trigger to be accepted.</param>
  /// <typeparam name="TArg0">Type of the first trigger argument.</typeparam>
  /// <typeparam name="TArg1">Type of the second trigger argument.</typeparam>
  /// <typeparam name="TArg2">Type of the third trigger argument.</typeparam>
  public <TArg0, TArg1, TArg2> StateConfiguration<TState, TTrigger> permitDynamicIf(@Nonnull TriggerWithParameters3<TArg0, TArg1, TArg2, TState, TTrigger> trigger,
                                                                                    @Nonnull final Func4<TArg0, TArg1, TArg2, TState> destinationStateSelector,
                                                                                    Func<Boolean> guard) {
    return publicPermitDynamicIf(trigger.getTrigger(), new Func2<Object[], TState>() {
      @SuppressWarnings("unchecked")
      public TState call(Object[] args) {
        return destinationStateSelector.call((TArg0) args[0],
                                             (TArg1) args[1],
                                             (TArg2) args[2]
        );
      }
    }, guard);
  }

  StateConfiguration<TState, TTrigger> publicPermit(TTrigger trigger, TState destinationState) {
    return publicPermitIf(trigger, destinationState, new Func<Boolean>() {


      public Boolean call() {
        return true;
      }
    });
  }

  StateConfiguration<TState, TTrigger> publicPermitIf(TTrigger trigger,
                                                      TState destinationState,
                                                      @Nonnull Func<Boolean> guard) {
    representation.AddTriggerBehaviour(new TransitioningTriggerBehaviour<TState, TTrigger>(trigger, destinationState, guard));
    return this;
  }

  StateConfiguration<TState, TTrigger> publicPermitDynamic(TTrigger trigger, Func2<Object[], TState> destinationStateSelector) {
    return publicPermitDynamicIf(trigger, destinationStateSelector, NoGuard);
  }

  StateConfiguration<TState, TTrigger> publicPermitDynamicIf(TTrigger trigger,
                                                             @Nonnull Func2<Object[], TState> destinationStateSelector,
                                                             @Nonnull Func<Boolean> guard) {
    representation.AddTriggerBehaviour(new DynamicTriggerBehaviour<TState, TTrigger>(trigger, destinationStateSelector, guard));
    return this;
  }

  void enforceNotIdentityTransition(TState destination) throws SelfTransitionIgnoredOrReentrant {
    if (destination.equals(representation.getUnderlyingState())) {
      throw new SelfTransitionIgnoredOrReentrant();
    }
  }
}