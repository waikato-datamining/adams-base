/*
 * ExternalActorFileHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.io.FlowFile;

/**
 * Interface for actors that manage an actor loaded from an external file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9036 $
 */
public interface ExternalActorFileHandler {

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
}
