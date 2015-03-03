/*
 * AbstractDataContainerPaintlet.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import adams.gui.visualization.core.AbstractStrokePaintlet;

/**
 * A specialized paintlet for data container panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainerPaintlet
  extends AbstractStrokePaintlet {
  
  /** for serialization. */
  private static final long serialVersionUID = 7031132964890314535L;

  /**
   * Returns the data container panel currently in use.
   * 
   * @return		the panel in use
   */
  public DataContainerPanel getDataContainerPanel() {
    return (DataContainerPanel) m_Panel;
  }
}
