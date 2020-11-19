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

import adams.core.GlobalInfoSupporter;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Cursors;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.segmentation.CanvasPanel;
import adams.gui.visualization.segmentation.layer.CombinedLayer.CombinedSubLayer;
import adams.gui.visualization.segmentation.layer.OverlayLayer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Ancestor for tools.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTool
  implements Serializable, GlobalInfoSupporter {

  private static final long serialVersionUID = -6782161796343153566L;

  /** the canvas panel to operate on. */
  protected CanvasPanel m_PanelCanvas;

  /** the mouse listener. */
  protected ToolMouseAdapter m_Listener;

  /** the mouse motion listener. */
  protected ToolMouseMotionAdapter m_MotionListener;

  /** the options panel. */
  protected BasePanel m_PanelOptions;

  /** the full option panel. */
  protected BasePanel m_PanelFullOptions;

  /**
   * Initializes the tool.
   */
  protected AbstractTool() {
    initialize();
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
   * Returns whether any active layer is present.
   *
   * @return		true if an active layer present
   * @see		#hasActiveOverlay()
   * @see		#hasActiveCombinedSubLayer()
   */
  public boolean hasAnyActive() {
    return hasActiveOverlay() || hasActiveCombinedSubLayer();
  }

  /**
   * Returns whether an active overlay layer is present.
   *
   * @return		true if available
   */
  public boolean hasActiveOverlay() {
    if (m_PanelCanvas == null)
      return false;
    if (m_PanelCanvas.getOwner() == null)
      return false;
    return m_PanelCanvas.getOwner().getManager().hasActiveOverlay();
  }

  /**
   * Returns the active overlay layer.
   *
   * @return		the layer, null if none available
   */
  public OverlayLayer getActiveOverlay() {
    if (m_PanelCanvas == null)
      return null;
    if (m_PanelCanvas.getOwner() == null)
      return null;
    return m_PanelCanvas.getOwner().getManager().getActiveOverlay();
  }

  /**
   * Returns whether an active combined sub layer is present.
   *
   * @return		true if available
   */
  public boolean hasActiveCombinedSubLayer() {
    if (m_PanelCanvas == null)
      return false;
    if (m_PanelCanvas.getOwner() == null)
      return false;
    if (m_PanelCanvas.getOwner().getManager().getCombinedLayer() == null)
      return false;
    return m_PanelCanvas.getOwner().getManager().getCombinedLayer().hasActiveSubLayer();
  }

  /**
   * Returns the active combined sub layer.
   *
   * @return		the layer, null if none available
   */
  public CombinedSubLayer getActiveCombinedSubLayer() {
    if (m_PanelCanvas == null)
      return null;
    if (m_PanelCanvas.getOwner() == null)
      return null;
    if (m_PanelCanvas.getOwner().getManager().getCombinedLayer() == null)
      return null;
    return m_PanelCanvas.getOwner().getManager().getCombinedLayer().getActiveSubLayer();
  }

  /**
   * Returns the active image.
   *
   * @return		the image or null if none active
   */
  public BufferedImage getActiveImage() {
    if (hasActiveOverlay())
      return getActiveOverlay().getImage();
    else if (hasActiveCombinedSubLayer())
      return getActiveCombinedSubLayer().getOwner().getImage();
    else
      return null;
  }

  /**
   * Returns the active color.
   *
   * @return		the color or null if none active
   */
  public Color getActiveColor() {
    if (hasActiveOverlay())
      return getActiveOverlay().getColor();
    else if (hasActiveCombinedSubLayer())
      return getActiveCombinedSubLayer().getColor();
    else
      return null;
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
   * Returns whether automatic undo is enabled.
   *
   * @return		true if enabled
   */
  public boolean isAutomaticUndoEnabled() {
    if (getCanvas() == null)
      return false;
    if (getCanvas().getOwner() == null)
      return false;
    return getCanvas().getOwner().isAutomaticUndoEnabled();
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
    if (!hasAnyActive())
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
    setApplyButtonState(button, false);
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
   * Sets the state of the "Apply" button according to the modified flag.
   *
   * @param button	the button to update
   * @param modified	whether applying needs doing or not
   */
  protected void setApplyButtonState(BaseFlatButton button, boolean modified) {
    if (modified)
      button.setIcon(GUIHelper.getIcon("validate_blue.png"));
    else
      button.setIcon(GUIHelper.getIcon("validate.png"));
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
    BaseTextArea	textArea;
    String		info;

    if (m_PanelOptions == null) {
      m_PanelOptions = createOptionPanel();
      m_PanelFullOptions = new BasePanel(new BorderLayout(5, 5));
      info = globalInfo();
      if (info != null) {
	textArea = new BaseTextArea();
	textArea.setEditable(false);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setColumns(20);
	textArea.setRows(4);
	textArea.setText(globalInfo());
	m_PanelFullOptions.add(new BaseScrollPane(textArea), BorderLayout.NORTH);
	m_PanelFullOptions.setBorder(BorderFactory.createTitledBorder("Description"));
      }
      m_PanelFullOptions.add(m_PanelOptions, BorderLayout.CENTER);
    }
    return m_PanelFullOptions;
  }
}
