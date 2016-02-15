/*
 * ExternalActorHandler.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.io.FlowFile;

/**
 * Interface for actors that wrap an actor loaded from an external file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9036 $
 */
public interface ExternalActorHandler {

  /**
   * Returns the file containing the external actor.
   *
   * @return		the actor file
   */
  public FlowFile getActorFile();

  /**
   * Sets the file containing the external actor.
   *
   * @param value	the actor file
   */
  public void setActorFile(FlowFile value);
  
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
