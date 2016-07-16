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
 * AbstractOutputPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaexperimenter;

import weka.experiment.ResultListener;
import adams.core.ClassLister;

/**
 * Ancestor for panels that allow the user to configure {@link ResultListener}s.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutputPanel
  extends AbstractSetupOptionPanel {

  /** for serialization. */
  private static final long serialVersionUID = 8147207337709278922L;
  
  /**
   * Returns the name to display in the GUI.
   * 
   * @return		the name
   */
  public abstract String getOutputName();
  
  /**
   * Returns whether this panel handles the specified {@link ResultListener}.
   * 
   * @param listener	the listener to check
   * @return		true if the panel handles this listener
   */
  public abstract boolean handlesResultListener(ResultListener listener);

  /**
   * Sets the {@link ResultListener}.
   * 
   * @param value	the listener
   */
  public abstract void setResultListener(ResultListener value);
  
  /**
   * Returns the configured {@link ResultListener}.
   * 
   * @return		the listener, null if failed to set up
   */
  public abstract ResultListener getResultListener();

  /**
   * Returns a list with classnames of panels.
   *
   * @return		the panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(AbstractOutputPanel.class);
  }
}
