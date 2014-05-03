/*
 * InternalActorHandler.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Interface for actors that wrap an internal actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InternalActorHandler
  extends Actor {

  /**
   * Returns the internal actor.
   *
   * @return		the actor, null if not available
   */
  public Actor getInternalActor();
}
