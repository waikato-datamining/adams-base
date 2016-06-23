/*
 * ExternalActorHandler.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

/**
 * Interface for actors that wrap an actor loaded from an external file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9036 $
 */
public interface ExternalActorHandler
  extends ExternalActorFileHandler {

  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor();

  /**
   * Returns the external actor.
   *
   * @return		the actor, null if not available
   */
  public Actor getExternalActor();

  /**
   * Cleans up the external actor.
   */
  public void cleanUpExternalActor();
}
