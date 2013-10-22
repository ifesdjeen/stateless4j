package ifesdjeen.stateless4j;

import org.junit.Assert;
import org.junit.Test;

import ifesdjeen.stateless4j.delegates.Func;
import ifesdjeen.stateless4j.triggers.IgnoredTriggerBehaviour;


public class IgnoredTriggerBehaviourFixtureTest {
  public static Func<Boolean> returnTrue = new Func<Boolean>() {

    public Boolean call() {
      return true;
    }
  };

  public static Func<Boolean> returnFalse = new Func<Boolean>() {

    public Boolean call() {
      return false;
    }
  };

  @Test
  public void StateRemainsUnchanged() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(Trigger.X, returnTrue);
    try {
      ignored.ResultsInTransitionFrom(State.B, new Object[0]);
      Assert.fail();
    } catch (Exception e) {

    }
  }

  @Test
  public void ExposesCorrectUnderlyingTrigger() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(
                                                                                                         Trigger.X, returnTrue);

    Assert.assertEquals(Trigger.X, ignored.getTrigger());
  }

  @Test
  public void WhenGuardConditionFalse_IsGuardConditionMetIsFalse() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(
                                                                                                         Trigger.X, returnFalse);

    Assert.assertFalse(ignored.isGuardConditionMet());
  }

  @Test
  public void WhenGuardConditionTrue_IsGuardConditionMetIsTrue() {
    IgnoredTriggerBehaviour<State, Trigger> ignored = new IgnoredTriggerBehaviour<State, Trigger>(
                                                                                                         Trigger.X, returnTrue);

    Assert.assertTrue(ignored.isGuardConditionMet());
  }
}
