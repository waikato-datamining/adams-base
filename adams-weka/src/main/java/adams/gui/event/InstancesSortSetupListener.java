/*
 * InstancesSortSetupListener.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.visualization.instances.instancestable.InstancesSortPanel;

/**
 * Interface for listeners that react to changes in a sort setup of a
 * {@link InstancesSortPanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public interface InstancesSortSetupListener {

  /**
   * Gets triggered whenever the sort setup changes.
   *
   * @param e		the event
   */
  public void sortSetupChanged(InstancesSortSetupEvent e);
}
