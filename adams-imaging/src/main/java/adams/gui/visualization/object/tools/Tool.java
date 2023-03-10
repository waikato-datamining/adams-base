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
 * Tool.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.core.CleanUpHandler;
import adams.core.GlobalInfoSupporter;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;
import adams.gui.core.BasePanel;
import adams.gui.visualization.object.CanvasPanel;

import javax.swing.Icon;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Interface for tools.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface Tool
  extends Serializable, GlobalInfoSupporter, CleanUpHandler, LoggingSupporter, LoggingLevelHandler {

  /**
   * Sets the panel to use.
   *
   * @param value 	the panel
   */
  public void setCanvas(CanvasPanel value);

  /**
   * Returns the currently set canvas panel.
   *
   * @return		the panel, null if none set
   */
  public CanvasPanel getCanvas();

  /**
   * Checks whether an image is currently available.
   *
   * @return		true if available
   */
  public boolean hasImage();

  /**
   * Returns the current image.
   *
   * @return		the image or null if none available
   */
  public BufferedImage getImage();

  /**
   * Returns the current zoom.
   *
   * @return		the zoom (1.0 = 100%)
   */
  public double getZoom();

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  public String getName();

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  public Icon getIcon();

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  public Cursor getCursor();

  /**
   * Returns the mouse listener to use.
   *
   * @return		the listener
   */
  public ToolMouseAdapter getMouseListener();

  /**
   * Returns the mouse motion listener to use.
   *
   * @return		the listener
   */
  public ToolMouseMotionAdapter getMouseMotionListener();

  /**
   * Returns the mouse listener to use.
   *
   * @return		the listener
   */
  public ToolKeyAdapter getKeyListener();

  /**
   * Returns whether the settings are currently modified.
   *
   * @return		true if modified
   */
  public boolean isModified();

  /**
   * Returns the panel for setting the options.
   *
   * @return		the options panel
   */
  public BasePanel getOptionPanel();

  /**
   * Gets called to activate the tool.
   */
  public void activate();

  /**
   * Gets called to deactivate the tool.
   */
  public void deactivate();

  /**
   * Called when image or annotations change.
   */
  public void update();
}
