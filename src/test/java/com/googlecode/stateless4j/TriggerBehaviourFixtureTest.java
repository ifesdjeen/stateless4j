package com.googlecode.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import com.googlecode.stateless4j.transitions.TransitioningTriggerBehaviour;


public class TriggerBehaviourFixtureTest {
  @Test
  public void ExposesCorrectUnderlyingTrigger() {
    TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(Trigger.X, State.C, IgnoredTriggerBehaviourFixtureTest.returnTrue);

    Assert.assertEquals(Trigger.X, transtioning.getTrigger());
  }

  @Test
  public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
    TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(Trigger.X, State.C, IgnoredTriggerBehaviourFixtureTest.returnFalse);

    Assert.assertFalse(transtioning.isGuardConditionMet());
  }

  @Test
  public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
    TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(Trigger.X, State.C, IgnoredTriggerBehaviourFixtureTest.returnTrue);

    Assert.assertTrue(transtioning.isGuardConditionMet());
  }
}
