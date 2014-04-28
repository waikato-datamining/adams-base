/*
 * SortSetupListener.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;

import adams.gui.tools.spreadsheetviewer.SortPanel;

/**
 * Interface for listeners that react to changes in a sort setup of a
 * {@link SortPanel}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SortSetupListener {

  /**
   * Gets triggered whenever the sort setup changes.
   *
   * @param e		the event
   */
  public void sortSetupChanged(SortSetupEvent e);
}
