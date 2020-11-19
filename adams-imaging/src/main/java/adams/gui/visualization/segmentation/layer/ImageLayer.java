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
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
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

  public final static String LAYER_NAME = "Image";

  /** the label for the layer name. */
  protected JLabel m_LabelName;

  /** the button for copying the label name. */
  protected BaseFlatButton m_ButtonName;

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
    m_ButtonName = new BaseFlatButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonName.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(m_LabelName.getText()));
    m_ButtonName.setToolTipText("Copy name to clipboard");
    panelRow.add(m_ButtonName);

    panelRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panelRow);
    panelRow.add(Fonts.usePlain(new JLabel("Brightness")));
    m_TextBrightness = new NumberTextField(Type.DOUBLE, "100");
    m_TextBrightness.setColumns(5);
    m_TextBrightness.setToolTipText("100 = original brightness");
    m_TextBrightness.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 1.0, null));
    m_TextBrightness.addAnyChangeListener((ChangeEvent e) -> setApplyButtonState(m_ButtonApply, true));
    panelRow.add(m_TextBrightness);
    m_ButtonApply = createApplyButton();
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
   * Sets the image to display.
   *
   * @param value	the image, null to clear
   */
  public void setImage(BufferedImage value) {
    super.setImage(value);
    m_BrightImage = null;
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
   * Clears the image.
   */
  public void clear() {
    setImage(null);
  }

  /**
   * Performs the drawing.
   *
   * @param g2d		the graphics context
   */
  @Override
  protected void doDraw(Graphics2D g2d) {
    float		brightness;
    RescaleOp 		op;
    BufferedImage	image;

    brightness = m_TextBrightness.getValue().floatValue();
    if ((m_BrightImage == null) || (m_LastBrightness == null) || (m_LastBrightness != brightness)) {
      op = new RescaleOp(brightness / 100.0f, 0, null);
      m_BrightImage = new BufferedImage(m_Image.getWidth(), m_Image.getHeight(), m_Image.getType());
      image         = m_Image;
      if (m_Image.getType() == BufferedImage.TYPE_BYTE_INDEXED)
        image = BufferedImageHelper.convert(m_Image, BufferedImage.TYPE_INT_ARGB);
      m_BrightImage = op.filter(image, m_BrightImage);
      m_LastBrightness = brightness;
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

    result       = (ImageLayerState) getSettings();
    result.image = BufferedImageHelper.deepCopy(getImage());

    return result;
  }

  /**
   * Restores the state of the layer.
   *
   * @param state	the state
   */
  public void setState(AbstractLayerState state) {
    setSettings(state);

    if (state instanceof ImageLayerState)
      setImage(((ImageLayerState) state).image);
  }

  /**
   * Returns the current settings.
   *
   * @return		the settings
   */
  public AbstractLayerState getSettings() {
    ImageLayerState	result;

    result         = new ImageLayerState();
    result.name    = getName();
    result.enabled = isEnabled();

    return result;
  }

  /**
   * Restores the settings of the layer.
   *
   * @param settings	the settings
   */
  public void setSettings(AbstractLayerState settings) {
    setName(settings.name);
    setEnabled(settings.enabled);
  }

  /**
   * Notifies the change listeners.
   */
  protected void update() {
    setApplyButtonState(m_ButtonApply, false);
    super.update();
  }
}
