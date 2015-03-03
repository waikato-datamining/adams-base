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

/*
 * AbstractMouseMovementTracker.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;


import java.awt.Point;
import java.awt.event.MouseEvent;

import adams.core.ClassLister;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;

/**
 * An abstract superclass for mouse movement trackers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMouseMovementTracker
  extends AbstractOptionHandler
  implements MouseMovementTracker, ShallowCopySupporter<MouseMovementTracker> {

  /** for serialization. */
  private static final long serialVersionUID = -5687969310967552455L;
  
  /** the panel this tracker is for. */
  protected PaintablePanel m_Panel;

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Panel = null;
  }

  /**
   * Sets the panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  public void setPanel(PaintablePanel value) {
    Point	point;
    
    if (value != m_Panel) {
      m_Panel = value;

      // send dummy event
      if (m_Panel != null) {
	point = value.getMousePosition();
	if (point == null)
	  point = new Point(0, 0);
	mouseMovementTracked(
	    new MouseEvent(
		value, 
		MouseEvent.MOUSE_MOVED,
		System.currentTimeMillis(),
		0,
		point.x,
		point.y,
		0,
		false));
      }
    }
  }

  /**
   * Returns the panel currently in use.
   *
   * @return		the panel in use
   */
  public PaintablePanel getPanel() {
    return m_Panel;
  }

  /**
   * Returns whether a panel has been set.
   *
   * @return		true if a panel is currently set
   */
  public boolean hasPanel() {
    return (m_Panel != null);
  }

  /**
   * Returns the plot panel of the panel, null if no panel present.
   *
   * @return		the plot panel
   */
  public PlotPanel getPlot() {
    PlotPanel	result;

    result = null;

    if (m_Panel != null)
      result = m_Panel.getPlot();

    return result;
  }

  /**
   * Gets triggered when the mouse moved.
   * 
   * @param e		the mouse event that triggered the event
   */
  public abstract void mouseMovementTracked(MouseEvent e);

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  @Override
  public MouseMovementTracker shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public MouseMovementTracker shallowCopy(boolean expand) {
    return (MouseMovementTracker) OptionUtils.shallowCopy(this, expand);
  }
  
  /**
   * Returns a shallow copy of the tracker. Doesn't expand variables in case
   * of {@link OptionHandler} objects.
   * 
   * @return		a shallow copy of the tracker
   */
  @Override
  public MouseMovementTracker shallowCopyTracker() {
    return shallowCopy();
  }
  
  /**
   * Returns a shallow copy of the tracker.
   * 
   * @param expand	whether to expand variables to their actual value
   * @return		a shallow copy of the tracker
   */
  @Override
  public MouseMovementTracker shallowCopyTracker(boolean expand) {
    return shallowCopy(expand);
  }

  /**
   * Returns a list with classnames of paintlets.
   *
   * @return		the filter classnames
   */
  public static String[] getMouseMovementTrackers() {
    return ClassLister.getSingleton().getClassnames(MouseMovementTracker.class);
  }

  /**
   * Instantiates the paintlet with the given options.
   *
   * @param classname	the classname of the paintlet to instantiate
   * @param options	the options for the paintlet
   * @return		the instantiated paintlet or null if an error occurred
   */
  public static MouseMovementTracker forName(String classname, String[] options) {
    MouseMovementTracker	result;

    try {
      result = (MouseMovementTracker) OptionUtils.forName(MouseMovementTracker.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the paintlet from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			paintlet to instantiate
   * @return		the instantiated paintlet
   * 			or null if an error occurred
   */
  public static MouseMovementTracker forCommandLine(String cmdline) {
    return (MouseMovementTracker) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
