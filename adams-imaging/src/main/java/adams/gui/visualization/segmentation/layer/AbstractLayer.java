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
 * AbstractLayer.java
 * Copyright (C) 2020-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer;

import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BorderHelper;
import adams.gui.core.ImageManager;

import javax.swing.border.BevelBorder;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.io.Serializable;

/**
 * Ancestor for layers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLayer
  extends BasePanel {

  private static final long serialVersionUID = -3811495263799661072L;

  /**
   * For storing the state of a layer.
   */
  public static abstract class AbstractLayerState
    implements Serializable {

    private static final long serialVersionUID = -5652014216527524598L;

    /** the name. */
    protected String name;

    /** whether enabled. */
    protected boolean enabled;
  }

  /** the layer manager. */
  protected LayerManager m_Manager;

  /** Whether the layer is enabled. */
  protected BaseCheckBox m_CheckboxEnabled;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setBorder(BorderHelper.createBevelBorder(BevelBorder.RAISED));

    m_CheckboxEnabled = new BaseCheckBox();
    m_CheckboxEnabled.setSelected(true);
    m_CheckboxEnabled.setToolTipText("Toggle visibility of layer");
    m_CheckboxEnabled.addActionListener((ActionEvent e) -> update());
  }

  /**
   * Sets the layer manager.
   *
   * @param value	the manager
   */
  public void setManager(LayerManager value) {
    m_Manager = value;
  }

  /**
   * Returns the layer manager.
   *
   * @return		the manager
   */
  public LayerManager getManager() {
    return m_Manager;
  }

  /**
   * Returns the name of the layer.
   *
   * @return		the layer
   */
  public abstract String getName();

  /**
   * Sets whether the layer is enabled.
   *
   * @param value	true if enabled
   */
  public void setEnabled(boolean value) {
    m_CheckboxEnabled.setSelected(value);
  }

  /**
   * Returns whether the layer is enabled.
   *
   * @return		true if enabled
   */
  public boolean isEnabled() {
    return m_CheckboxEnabled.isSelected();
  }

  /**
   * Returns whether the layer can be removed.
   *
   * @return		true if can be removed
   */
  public abstract boolean isRemovable();

  /**
   * Returns whether actions are available.
   *
   * @return		true if available
   */
  public abstract boolean hasActionsAvailable();

  /**
   * Notifies the change listeners.
   */
  protected void update() {
    if ((m_Manager != null) && !m_Manager.ignoreUpdates())
      m_Manager.update();
  }

  /**
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  protected abstract void doDraw(Graphics2D g2d);

  /**
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  public void draw(Graphics2D g2d) {
    if (isEnabled())
      doDraw(g2d);
  }

  /**
   * Returns the current state.
   *
   * @return		the state
   */
  public abstract AbstractLayerState getState();

  /**
   * Restores the state of the layer.
   *
   * @param state	the state
   */
  public abstract void setState(AbstractLayerState state);

  /**
   * Returns the current settings.
   *
   * @return		the settings
   */
  public abstract AbstractLayerState getSettings();

  /**
   * Restores the settings of the layer.
   *
   * @param settings	the settings
   */
  public abstract void setSettings(AbstractLayerState settings);

  /**
   * Creates an apply button (icon/tooltip), but without action.
   *
   * @return		the button
   */
  public static BaseFlatButton createApplyButton() {
    BaseFlatButton	result;

    result = new BaseFlatButton(ImageManager.getIcon("validate.png"));
    result.setToolTipText("Apply current values");
    return result;
  }

  /**
   * Sets the state of the "Apply" button according to the modified flag.
   *
   * @param button	the button to update
   * @param modified	whether applying needs doing or not
   */
  public static void setApplyButtonState(BaseFlatButton button, boolean modified) {
    if (modified)
      button.setIcon(ImageManager.getIcon("validate_blue.png"));
    else
      button.setIcon(ImageManager.getIcon("validate.png"));
  }
}
