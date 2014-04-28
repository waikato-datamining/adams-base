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
 * AbstractPixelSelectorAction.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import java.awt.Point;
import java.awt.event.ActionEvent;

import adams.core.option.OptionHandler;
import adams.core.option.OptionManager;
import adams.core.option.OptionUtils;
import adams.flow.transformer.PixelSelector;
import adams.gui.action.AbstractBaseAction;

/**
 * Ancestor for actions for the {@link PixelSelector} interactive transformer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPixelSelectorAction
  extends AbstractBaseAction 
  implements OptionHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6674327571331925321L;

  /** the mouse position key. */
  public final static String MOUSE_POSITION = "Mouse position";

  /** the pixel position key. */
  public final static String PIXEL_POSITION = "Pixel position";

  /** the panel this action is for. */
  public final static String PANEL = "Panel";

  /** for managing the available options. */
  protected OptionManager m_OptionManager;

  /**
   * Initializes the object.
   */
  public AbstractPixelSelectorAction() {
    super();
    initialize();
    defineOptions();
    getOptionManager().setDefaults();
    finishInit();
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public abstract String globalInfo();

  /**
   * Initializes the members.
   */
  protected void initialize() {
    setName(getTitle());
  }

  /**
   * Returns the title of the action (used as menu item text).
   * 
   * @return		the title
   */
  protected abstract String getTitle();
  
  /**
   * Resets the scheme.
   */
  protected void reset() {
    setMousePosition(null);
  }

  /**
   * Returns a new instance of the option manager.
   *
   * @return		the manager to use
   */
  protected OptionManager newOptionManager() {
    return new OptionManager(this);
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    m_OptionManager = newOptionManager();
  }

  /**
   * Finishes the initialization in the constructor.
   * <p/>
   * Default implementation does nothing.
   */
  protected void finishInit() {
  }

  /**
   * Returns the option manager.
   *
   * @return		the manager
   */
  public OptionManager getOptionManager() {
    if (m_OptionManager == null)
      defineOptions();

    return m_OptionManager;
  }

  /**
   * Cleans up the options.
   */
  public void cleanUpOptions() {
    if (m_OptionManager != null) {
      m_OptionManager.cleanUp();
      m_OptionManager = null;
    }
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Cleans up the options.
   *
   * @see	#cleanUpOptions()
   */
  public void destroy() {
    cleanUpOptions();
  }

  /**
   * Returns a string representation of the options.
   *
   * @return		 a string representation
   */
  public String toString() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Returns the commandline string.
   *
   * @return		 the commandline
   */
  public String toCommandLine() {
    return OptionUtils.getCommandLine(this);
  }
  
  /**
   * Stores the mouse position in the action.
   * 
   * @param value	the current mouse position, null to remove it
   */
  public void setMousePosition(Point value) {
    putValue(MOUSE_POSITION, value);
  }
  
  /**
   * Checks whether a mouse position is available.
   * 
   * @return		true if position is available
   */
  public boolean hasMousePosition() {
    return (getValue(MOUSE_POSITION) != null);
  }
  
  /**
   * Returns the mouse position, if available.
   * 
   * @return		the position, null if not availabel
   */
  public Point getMousePosition() {
    return (Point) getValue(MOUSE_POSITION);
  }
  
  /**
   * Stores the pixel position in the action.
   * 
   * @param value	the current pixel position, null to remove it
   */
  public void setPixelPosition(Point value) {
    putValue(PIXEL_POSITION, value);
  }
  
  /**
   * Checks whether a pixel position is available.
   * 
   * @return		true if position is available
   */
  public boolean hasPixelPosition() {
    return (getValue(PIXEL_POSITION) != null);
  }
  
  /**
   * Returns the pixel position, if available.
   * 
   * @return		the position, null if not availabel
   */
  public Point getPixelPosition() {
    return (Point) getValue(PIXEL_POSITION);
  }
  
  /**
   * Stores the panel in the action.
   * 
   * @param value	the panel, null to remove it
   */
  public void setPanel(PixelSelectorPanel value) {
    putValue(PANEL, value);
  }
  
  /**
   * Checks whether a panel is available.
   * 
   * @return		true if panel is available
   */
  public boolean hasPanel() {
    return (getValue(PANEL) != null);
  }
  
  /**
   * Returns the panel, if available.
   * 
   * @return		the panel, null if not availabel
   */
  public PixelSelectorPanel getPanel() {
    return (PixelSelectorPanel) getValue(PANEL);
  }

  /**
   * Reacts to the action event.
   * 
   * @param e		the event
   * @return		true if to update the report table
   */
  protected abstract boolean doProcessAction(ActionEvent e);

  /**
   * Invoked when an action occurs.
   * 
   * @param e		the action
   * @see		#doProcessAction(ActionEvent)
   */
  public void actionPerformed(ActionEvent e) {
    if (!hasPanel() || (getPanel().getImage() == null))
      return;
    
    if (doProcessAction(e)) {
      if (hasPanel())
	getPanel().update();
    }
  }
  
  /**
   * Performs a check on the setup.
   * <p/>
   * Default implementation always returns null.
   * 
   * @return		null if check passed, otherwise the error message
   */
  public String check() {
    return null;
  }
}
