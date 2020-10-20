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

import adams.data.image.BufferedImageHelper;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

/**
 * The layer for the image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ImageLayer
  extends AbstractImageLayer {

  private static final long serialVersionUID = 1680744036963757388L;

  /**
   * For storing the state of a background layer.
   */
  public static class ImageLayerState
    extends AbstractImageLayerState {

    private static final long serialVersionUID = -5652014216527524598L;
  }

  /** the label for the layer name. */
  protected JLabel m_LabelName;

  /** the brightness to use. */
  protected NumberTextField m_TextBrightness;

  /** the button for applying the values. */
  protected BaseFlatButton m_ButtonApply;

  /** the last brightness. */
  protected Float m_LastBrightness;

  /** the brightened image. */
  protected BufferedImage m_BrightImage;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_BrightImage = null;
  }

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
    m_LabelName = new JLabel("");
    panelRow.add(Fonts.usePlain(m_LabelName));

    panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panelRow);
    panelRow.add(Fonts.usePlain(new JLabel("Brightness")));
    m_TextBrightness = new NumberTextField(Type.DOUBLE, "100");
    m_TextBrightness.setColumns(5);
    m_TextBrightness.setToolTipText("100 = original brightness");
    m_TextBrightness.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    panelRow.add(m_TextBrightness);
    m_ButtonApply = new BaseFlatButton(GUIHelper.getIcon("validate.png"));
    m_ButtonApply.setToolTipText("Apply current values");
    m_ButtonApply.addActionListener((ActionEvent e) -> update());
    panelRow.add(m_ButtonApply);
  }

  /**
   * Sets the name of the layer.
   *
   * @param value	the name
   */
  public void setName(String value) {
    m_LabelName.setText(value);
  }

  /**
   * Returns the name of the layer.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return m_LabelName.getText();
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
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  @Override
  protected void doDraw(Graphics2D g2d) {
    float	brightness;
    RescaleOp 	op;

    brightness = m_TextBrightness.getValue().floatValue();
    if ((m_LastBrightness == null) || (m_LastBrightness != brightness)) {
      op = new RescaleOp(brightness / 100.0f, 0, null);
      m_BrightImage = new BufferedImage(m_Image.getWidth(), m_Image.getHeight(), m_Image.getType());
      m_BrightImage = op.filter(m_Image, m_BrightImage);
    }
    g2d.drawImage(m_BrightImage, null, 0, 0);
  }

  /**
   * Returns the current state.
   *
   * @return		the state
   */
  @Override
  public AbstractLayerState getState() {
    ImageLayerState	result;

    result       = new ImageLayerState();
    result.name  = getName();
    result.image = BufferedImageHelper.deepCopy(getImage());

    return result;
  }

  /**
   * Restores the state of the layer.
   *
   * @param state	the state
   */
  public void setState(AbstractLayerState state) {
    setName(state.name);

    if (state instanceof ImageLayerState)
      setImage(((ImageLayerState) state).image);
  }
}
