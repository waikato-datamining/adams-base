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
 * ImageLayer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.layer;

import adams.gui.core.BaseColorTextField;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.ColorHelper;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * The layer for the background.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BackgroundLayer
  extends AbstractLayer {

  private static final long serialVersionUID = 1680744036963757388L;

  /**
   * For storing the state of a background layer.
   */
  public static class BackgroundLayerState
    extends AbstractLayerState {

    private static final long serialVersionUID = -5652014216527524598L;

    /** the color. */
    protected Color color;
  }

  /** the label with the name. */
  protected JLabel m_LabelName;

  /** The color to use. */
  protected BaseColorTextField m_TextColor;

  /** the button for applying the values. */
  protected BaseFlatButton m_ButtonApply;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panelRow;

    super.initGUI();

    setLayout(new GridLayout(0, 1));

    panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panelRow);
    panelRow.add(m_CheckboxEnabled);
    m_LabelName = new JLabel(getName());
    panelRow.add(Fonts.usePlain(m_LabelName));

    panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panelRow);
    m_TextColor = new BaseColorTextField(Color.BLACK);
    m_TextColor.setColumns(7);
    m_TextColor.setToolTipText("The color to use for the background");
    panelRow.add(m_TextColor);
    m_ButtonApply = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonApply.setToolTipText("Apply current values");
    m_ButtonApply.addActionListener((ActionEvent e) -> update());
    panelRow.add(m_ButtonApply);
  }

  /**
   * Returns the name of the layer.
   *
   * @return		the layer
   */
  public String getName() {
    return "Background";
  }

  /**
   * Sets the color value to use for the layer.
   *
   * @param value	the color value
   */
  public void setColor(Color value) {
    m_TextColor.setText(ColorHelper.toHex(value));
  }

  /**
   * Returns the color value for the layer.
   *
   * @return		the color value
   */
  public Color getColor() {
    return m_TextColor.getObject().toColorValue();
  }

  /**
   * Returns whether the layer can be removed.
   *
   * @return		true if can be removed
   */
  @Override
  public boolean isRemovable() {
    return false;
  }

  /**
   * Returns whether actions are available.
   *
   * @return		true if available
   */
  @Override
  public boolean hasActionsAvailable() {
    return false;
  }

  /**
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  @Override
  protected void doDraw(Graphics2D g2d) {
    g2d.setColor(getColor());
    g2d.fillRect(0, 0, getManager().getWidth(), getManager().getHeight());
  }

  /**
   * Returns the current state.
   *
   * @return		the state
   */
  @Override
  public AbstractLayerState getState() {
    BackgroundLayerState	result;

    result       = new BackgroundLayerState();
    result.name  = getName();
    result.color = getColor();

    return result;
  }

  /**
   * Restores the state of the layer.
   *
   * @param state	the state
   */
  public void setState(AbstractLayerState state) {
    setName(state.name);

    if (state instanceof BackgroundLayerState)
      setColor(((BackgroundLayerState) state).color);
  }
}
