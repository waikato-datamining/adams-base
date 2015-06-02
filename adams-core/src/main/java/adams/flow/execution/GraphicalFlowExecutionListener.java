/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * GraphicalFlowExecutionListener.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.execution;

import adams.gui.core.BasePanel;

import java.awt.Dimension;

/**
 * Interface for listeners that supply graphical output in from of a panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface GraphicalFlowExecutionListener
  extends FlowExecutionListener {

  /**
   * The title of this listener.
   * 
   * @return		the title
   */
  public String getListenerTitle();
  
  /**
   * Returns the panel to use.
   * 
   * @return		the panel, null if none available
   */
  public BasePanel newListenerPanel();
  
  /**
   * Returns the default size for the frame.
   * 
   * @return		the frame size
   */
  public Dimension getDefaultFrameSize();

  /**
   * Returns whether the frame should get disposed when the flow finishes.
   *
   * @return		true if to dispose when flow finishes
   */
  public boolean getDisposeOnFinish();
}
