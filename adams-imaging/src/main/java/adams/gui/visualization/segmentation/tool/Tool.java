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

package adams.gui.visualization.segmentation.tool;

import adams.core.CleanUpHandler;
import adams.core.GlobalInfoSupporter;
import adams.core.logging.LoggingLevelHandler;
import adams.core.logging.LoggingSupporter;
import adams.gui.core.BasePanel;
import adams.gui.visualization.segmentation.CanvasPanel;
import adams.gui.visualization.segmentation.layer.CombinedLayer;
import adams.gui.visualization.segmentation.layer.LayerManager;
import adams.gui.visualization.segmentation.layer.OverlayLayer;
import adams.gui.visualization.segmentation.paintoperation.PaintOperation;

import javax.swing.Icon;
import java.awt.Color;
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
   * Sets the canvas panel to use.
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
   * Returns the layer manager.
   *
   * @return		the layer manager, null if not available
   */
  public LayerManager getLayerManager();

  /**
   * Returns whether any active layer is present.
   *
   * @return		true if an active layer present
   * @see		#hasActiveOverlay()
   * @see		#hasActiveCombinedSubLayer()
   */
  public boolean hasAnyActive();

  /**
   * Returns whether an active overlay layer is present.
   *
   * @return		true if available
   */
  public boolean hasActiveOverlay();

  /**
   * Returns the active overlay layer.
   *
   * @return		the layer, null if none available
   */
  public OverlayLayer getActiveOverlay();

  /**
   * Returns whether an active combined sub layer is present.
   *
   * @return		true if available
   */
  public boolean hasActiveCombinedSubLayer();

  /**
   * Returns the active combined sub layer.
   *
   * @return		the layer, null if none available
   */
  public CombinedLayer.CombinedSubLayer getActiveCombinedSubLayer();

  /**
   * Returns the active image.
   *
   * @return		the image or null if none active
   */
  public BufferedImage getActiveImage();

  /**
   * Returns the active color.
   *
   * @return		the color or null if none active
   */
  public Color getActiveColor();

  /**
   * Returns the current zoom.
   *
   * @return		the zoom (1.0 = 100%)
   */
  public double getZoom();

  /**
   * Returns whether automatic undo is enabled.
   *
   * @return		true if enabled
   */
  public boolean isAutomaticUndoEnabled();

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
   * Returns the paint operation for the tool.
   *
   * @return		the paint operation
   */
  public PaintOperation getPaintOperation();

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
   * Hook method for when new annotations have been set.
   */
  public void annotationsChanged();
}
