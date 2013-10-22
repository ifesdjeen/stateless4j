package ifesdjeen.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import ifesdjeen.stateless4j.transitions.TransitioningTriggerBehaviour;


public class TransitioningTriggerBehaviourFixtureTest {

  @Test
  public void TransitionsToDestinationState() {
    TransitioningTriggerBehaviour<State, Trigger> transtioning = new TransitioningTriggerBehaviour<State, Trigger>(Trigger.X, State.C, IgnoredTriggerBehaviourFixtureTest.returnTrue);
    State destination = transtioning.ResultsInTransitionFrom(State.B, new Object[0]);
    Assert.assertEquals(State.C, destination);
  }

}
