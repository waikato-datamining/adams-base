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
 * Child.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.application;

import java.util.logging.Logger;

import adams.core.CleanUpHandler;

/**
 * The interface for the child frames/windows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Child
  extends CleanUpHandler {

  /**
   * returns the parent frame, can be null.
   *
   * @return		the parent frame
   */
  public AbstractApplicationFrame getParentFrame();

  /**
   * Returns the current title.
   *
   * @return		the title
   */
  public String getTitle();

  /**
   * Sets the new title.
   *
   * @param title	the new title
   */
  public void setTitle(String title);

  /**
   * Calls the cleanUp() method if the first component is a CleanUpHandler.
   */
  public void cleanUp();

  /**
   * de-registers the child frame/window with the parent first.
   */
  public void dispose();

  /**
   * Returns whether a new window, containing the same panel, can be created.
   *
   * @return		true if a new window can be created
   */
  public boolean canCreateNewWindow();

  /**
   * Creates a new window of itself.
   *
   * @return		the new window, or null if not possible
   */
  public Child getNewWindow();

  /**
   * Adds a window listener to dispose the frame/window.
   */
  public void addDisposeWindowListener();

  /**
   * Brings the child to the front.
   */
  public void toFront();

  /**
   * Requests the focus for the child.
   */
  public void requestFocus();

  /**
   * Returns the logger.
   *
   * @return		the logger
   */
  public Logger getLogger();
}