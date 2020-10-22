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
 * AbstractTool.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.Cursors;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.segmentation.CanvasPanel;
import adams.gui.visualization.segmentation.layer.OverlayLayer;

import javax.swing.Icon;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.Serializable;

/**
 * Ancestor for tools.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTool
  implements Serializable {

  private static final long serialVersionUID = -6782161796343153566L;

  /** the canvas panel to operate on. */
  protected CanvasPanel m_PanelCanvas;

  /** the mouse listener. */
  protected ToolMouseAdapter m_Listener;

  /** the mouse motion listener. */
  protected ToolMouseMotionAdapter m_MotionListener;

  /** the options panel. */
  protected BasePanel m_PanelOptions;

  /**
   * Initializes the tool.
   */
  protected AbstractTool() {
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_PanelCanvas    = null;
    m_PanelOptions   = null;
    m_Listener       = null;
    m_MotionListener = null;
  }

  /**
   * Sets the canvas panel to use.
   *
   * @param value 	the panel
   */
  public void setCanvas(CanvasPanel value) {
    m_PanelCanvas = value;
  }

  /**
   * Returns the currently set canvas panel.
   *
   * @return		the panel, null if none set
   */
  public CanvasPanel getCanvas() {
    return m_PanelCanvas;
  }

  /**
   * Returns whether an active overlay layer is present.
   *
   * @return		true if available
   */
  public boolean hasActiveLayer() {
    if (m_PanelCanvas == null)
      return false;
    if (m_PanelCanvas.getOwner() == null)
      return false;
    return m_PanelCanvas.getOwner().getManager().hasActive();
  }

  /**
   * Returns the active overlay layer.
   *
   * @return		the layer, null if none available
   */
  public OverlayLayer getActiveLayer() {
    if (m_PanelCanvas == null)
      return null;
    if (m_PanelCanvas.getOwner() == null)
      return null;
    return m_PanelCanvas.getOwner().getManager().getActive();
  }

  /**
   * Returns the current zoom.
   *
   * @return		the zoom (1.0 = 100%)
   */
  public double getZoom() {
    if (m_PanelCanvas == null)
      return 1.0;
    if (m_PanelCanvas.getOwner() == null)
      return 1.0;
    return m_PanelCanvas.getOwner().getManager().getZoom();
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  public abstract Icon getIcon();

  /**
   * Creates the mouse cursor to use.
   *
   * @return		the cursor
   */
  protected abstract Cursor createCursor();

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  public Cursor getCursor() {
    if (!hasActiveLayer())
      return Cursors.disabled();
    else
      return createCursor();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  protected abstract ToolMouseAdapter createMouseListener();

  /**
   * Returns the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  public ToolMouseAdapter getMouseListener() {
    if (m_Listener == null)
      m_Listener = createMouseListener();
    return m_Listener;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  protected abstract ToolMouseMotionAdapter createMouseMotionListener();

  /**
   * Returns the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  public ToolMouseMotionAdapter getMouseMotionListener() {
    if (m_MotionListener == null)
      m_MotionListener = createMouseMotionListener();
    return m_MotionListener;
  }

  /**
   * Applies the settings.
   */
  protected abstract void doApply();

  /**
   * Applies the settings.
   *
   * @see		#doApply()
   */
  public void apply(BaseFlatButton button) {
    button.setIcon(GUIHelper.getIcon("validate.png"));
    doApply();
    getCanvas().setCursor(getCursor());
  }

  /**
   * Generates the apply button.
   *
   * @return		the button
   */
  protected BaseFlatButton createApplyButton() {
    BaseFlatButton 	result;

    result = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    result.setToolTipText("Apply current values");
    result.addActionListener((ActionEvent e) -> apply((BaseFlatButton) e.getSource()));

    return result;
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return		the options panel
   */
  protected abstract BasePanel createOptionPanel();

  /**
   * Returns the panel for setting the options.
   *
   * @return		the options panel
   */
  public BasePanel getOptionPanel() {
    if (m_PanelOptions == null)
      m_PanelOptions = createOptionPanel();
    return m_PanelOptions;
  }
}
