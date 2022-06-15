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
 * SwitchLabel.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.segmentation.ImageUtils;
import adams.gui.visualization.segmentation.layer.CombinedLayer;
import adams.gui.visualization.segmentation.layer.OverlayLayer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * Switch label, for changing one label to another.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SwitchLabel
    extends AbstractTool {

  private static final long serialVersionUID = -3058489939334040466L;

  /** the text field for the old label. */
  protected BaseComboBox<String> m_ComboBoxOldLabel;

  /** the text field for the new label. */
  protected BaseComboBox<String> m_ComboBoxNewLabel;

  /** the apply button. */
  protected BaseFlatButton m_ButtonApply;

  /** the available labels. */
  protected DefaultComboBoxModel<String> m_LabelsOld;

  /** the available labels. */
  protected DefaultComboBoxModel<String> m_LabelsNew;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces one label with another.";
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Switch label";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return GUIHelper.getIcon("switch_label.png");
  }

  /**
   * Returns the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  protected Cursor createCursor() {
    return Cursor.getDefaultCursor();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    return null;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    return null;
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    CombinedLayer.CombinedSubLayer 	subOld;
    CombinedLayer.CombinedSubLayer 	subNew;
    OverlayLayer			ovlOld;
    OverlayLayer			ovlNew;
    Color				colOld;
    Color				colNew;

    if (m_ComboBoxOldLabel.getSelectedIndex() == -1)
      return;
    if (m_ComboBoxNewLabel.getSelectedIndex() == -1)
      return;
    if (m_ComboBoxOldLabel.getSelectedIndex() == m_ComboBoxNewLabel.getSelectedIndex())
      return;

    if (isAutomaticUndoEnabled())
      getCanvas().getOwner().addUndoPoint();

    if (getLayerManager().getSplitLayers()) {
      ovlOld = getLayerManager().getOverlay(m_ComboBoxOldLabel.getSelectedItem());
      ovlNew = getLayerManager().getOverlay(m_ComboBoxOldLabel.getSelectedItem());
      colOld = ovlOld.getColor();
      colNew = ovlNew.getColor();
    }
    else {
      subOld = getLayerManager().getCombinedLayer().getSubLayer(m_ComboBoxOldLabel.getSelectedItem());
      subNew = getLayerManager().getCombinedLayer().getSubLayer(m_ComboBoxNewLabel.getSelectedItem());
      colOld = subOld.getColor();
      colNew = subNew.getColor();
    }

    ImageUtils.replaceColor(getActiveImage(), colOld, colNew);
    getCanvas().getOwner().getManager().update();
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return		the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel		result;
    JPanel		panel;
    JPanel		panel2;
    ButtonGroup 	group;

    result = new BasePanel();
    result.setBorder(BorderFactory.createTitledBorder(getName()));

    m_ButtonApply = createApplyButton();

    panel = new JPanel(new GridLayout(0, 1));
    result.add(panel, BorderLayout.NORTH);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(Fonts.usePlain(new JLabel("Old label")));
    m_ComboBoxOldLabel = new BaseComboBox<>();
    m_ComboBoxOldLabel.setToolTipText("The old label to replace");
    m_ComboBoxOldLabel.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    panel2.add(m_ComboBoxOldLabel);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(Fonts.usePlain(new JLabel("New label")));
    m_ComboBoxNewLabel = new BaseComboBox<>();
    m_ComboBoxNewLabel.setToolTipText("The new label to replace with");
    m_ComboBoxNewLabel.addActionListener((ActionEvent e) -> setApplyButtonState(m_ButtonApply, true));
    panel2.add(m_ComboBoxNewLabel);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(panel2);
    panel2.add(m_ButtonApply);

    m_ComboBoxOldLabel.setModel(m_LabelsOld);
    m_ComboBoxNewLabel.setModel(m_LabelsNew);
    if (m_LabelsOld.getSize() > 0) {
      m_ComboBoxOldLabel.setSelectedIndex(0);
      m_ComboBoxNewLabel.setSelectedIndex(0);
    }

    return result;
  }

  /**
   * Hook method for when new annotations have been set.
   */
  public void annotationsChanged() {
    m_LabelsOld = new DefaultComboBoxModel<>();
    m_LabelsNew = new DefaultComboBoxModel<>();
    if (getLayerManager().getSplitLayers()) {
      for (OverlayLayer layer: getLayerManager().getOverlays()) {
	m_LabelsOld.addElement(layer.getName());
	m_LabelsNew.addElement(layer.getName());
      }
    }
    else {
      for (CombinedLayer.CombinedSubLayer layer: getLayerManager().getCombinedLayer().getSubLayers()) {
	m_LabelsOld.addElement(layer.getName());
	m_LabelsNew.addElement(layer.getName());
      }
    }

    if (m_ComboBoxOldLabel != null) {
      m_ComboBoxOldLabel.setModel(m_LabelsOld);
      m_ComboBoxNewLabel.setModel(m_LabelsNew);
      if (m_LabelsOld.getSize() > 0) {
	m_ComboBoxOldLabel.setSelectedIndex(0);
	m_ComboBoxNewLabel.setSelectedIndex(0);
      }
    }
  }
}
