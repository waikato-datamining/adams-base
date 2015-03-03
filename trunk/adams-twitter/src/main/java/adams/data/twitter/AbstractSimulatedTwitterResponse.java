/*
 * ManualTwitterResponse.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.twitter;

import twitter4j.RateLimitStatus;
import twitter4j.TwitterResponse;

import java.io.Serializable;

/**
 * Ancestor for simulating twitter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSimulatedTwitterResponse
  implements TwitterResponse, Serializable {

  /** for serialization. */
  private static final long serialVersionUID = -93525519472329597L;

  /**
   * Initializes the response.
   */
  protected AbstractSimulatedTwitterResponse() {
    super();
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
  }

  /**
   * Returns the current rate limit status if available.
   *
   * @return current rate limit status - dummy object
   */
  @Override
  public RateLimitStatus getRateLimitStatus() {
    return new RateLimitStatus() {
      private static final long serialVersionUID = -8963524970513688810L;
      @Override
      public int getRemaining() {
	return 0;
      }
      @Override
      public int getRemainingHits() {
	return 0;
      }
      @Override
      public int getLimit() {
	return 0;
      }
      @Override
      public int getResetTimeInSeconds() {
	return 0;
      }
      @Override
      public int getSecondsUntilReset() {
	return 0;
      }
    };
  }

  /**
   * Always {@link TwitterResponse#NONE}.
   *
   * @return application permission model
   */
  @Override
  public int getAccessLevel() {
    return TwitterResponse.NONE;
  }
}
