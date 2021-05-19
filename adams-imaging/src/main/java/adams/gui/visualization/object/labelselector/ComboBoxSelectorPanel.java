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
 * ButtonSelectorPanel.java
 * Copyright (C) 2020-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseScrollPane;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Panel with labels in combobox and unset buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ComboBoxSelectorPanel
  extends AbstractLabelSelectorPanel {

  private static final long serialVersionUID = -5878687744017979355L;

  /** the labels. */
  protected BaseString[] m_Labels;

  /** the labels. */
  protected BaseComboBox<String> m_ComboBoxLabels;

  /** the unset button. */
  protected BaseButton m_ButtonUnset;

  /**
   * Initializes the panel.
   *
   * @param owner 	the owning panel
   * @param labels	the labels to use
   */
  public ComboBoxSelectorPanel(ObjectAnnotationPanel owner, BaseString[] labels) {
    super(owner);

    m_Labels = labels;

    initialize();
    initGUI();
    finishInit();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    if (m_Labels == null)
      return;

    super.initialize();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    int			i;
    JPanel		panelButtons;
    GridBagLayout 	layout;
    GridBagConstraints 	con;
    int 		gapVertical;
    int 		gapHorizontal;
    List<Component> 	comps;
    JPanel		panel;
    List<String>	labels;

    if (m_Labels == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout());

    comps         = new ArrayList<>();
    gapHorizontal = 5;
    gapVertical   = 2;
    layout = new GridBagLayout();
    panelButtons = new JPanel(layout);
    add(new BaseScrollPane(panelButtons), BorderLayout.CENTER);

    labels = new ArrayList<>();
    labels.add("");
    labels.addAll(Arrays.asList(BaseObject.toStringArray(m_Labels)));
    m_ComboBoxLabels = new BaseComboBox<>(labels.toArray(new String[0]));
    m_ComboBoxLabels.addActionListener((ActionEvent e) -> setCurrentLabel(m_ComboBoxLabels.getSelectedItem()));
    comps.add(m_ComboBoxLabels);

    m_ButtonUnset = new BaseButton("Unset");
    m_ButtonUnset.addActionListener((ActionEvent e) -> m_ComboBoxLabels.setSelectedIndex(0));
    comps.add(m_ButtonUnset);

    for (i = 0; i < comps.size(); i++) {
      con = new GridBagConstraints();
      con.anchor  = GridBagConstraints.WEST;
      con.fill    = GridBagConstraints.HORIZONTAL;
      con.gridy   = i;
      con.gridx   = 0;
      con.weightx = 100;
      con.ipadx   = 20;
      con.insets  = new Insets(gapVertical, gapHorizontal, gapVertical, gapHorizontal);
      layout.setConstraints(comps.get(i), con);
      panelButtons.add(comps.get(i));
    }

    // filler at bottom
    panel         = new JPanel();
    con           = new GridBagConstraints();
    con.anchor    = GridBagConstraints.WEST;
    con.fill      = GridBagConstraints.BOTH;
    con.gridy     = comps.size();
    con.gridx     = 0;
    con.weighty   = 100;
    con.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(panel, con);
    panelButtons.add(panel);
  }

  /**
   * Sets the current label to use.
   *
   * @param value	the label, null to unset
   */
  @Override
  public void setCurrentLabel(String value) {
    if (value.isEmpty())
      super.setCurrentLabel(null);
    else
      super.setCurrentLabel(value);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    if (m_Labels == null)
      return;

    super.finishInit();

    if (m_Labels.length > 0)
      m_ComboBoxLabels.setSelectedIndex(1);
    else
      m_ComboBoxLabels.setSelectedIndex(0);
  }

  /**
   * Pre-selects the label.
   *
   * @param label	the label to use
   */
  protected void doPreselectCurrentLabel(String label) {
    if (label == null)
      m_ComboBoxLabels.setSelectedIndex(0);
    else
      m_ComboBoxLabels.setSelectedItem(label);
  }

  /**
   * Shows or hides the "Unset" button.
   *
   * @param value	true if to show, false to hide
   */
  @Override
  public void setUnsetButtonVisible(boolean value) {
    m_ButtonUnset.setVisible(value);
  }

  /**
   * Returns whether the unset button is visible.
   *
   * @return		true if visible
   */
  @Override
  public boolean isUnsetButtonVisible() {
    return m_ButtonUnset.isVisible();
  }
}
