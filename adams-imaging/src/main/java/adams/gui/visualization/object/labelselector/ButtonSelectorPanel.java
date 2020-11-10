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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.core.base.BaseString;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseToggleButton;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Default panel with labels and unset buttons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ButtonSelectorPanel
  extends AbstractLabelSelectorPanel {

  private static final long serialVersionUID = -5878687744017979355L;

  /** the labels. */
  protected BaseString[] m_Labels;

  /** the label buttons. */
  protected BaseToggleButton[] m_ButtonLabels;

  /** the unset button. */
  protected BaseToggleButton m_ButtonUnset;

  /** the button group. */
  protected ButtonGroup m_ButtonGroup;

  /**
   * Initializes the panel.
   *
   * @param owner 	the owning panel
   * @param labels	the labels to use
   */
  public ButtonSelectorPanel(ObjectAnnotationPanel owner, BaseString[] labels) {
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

    if (m_Labels == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout());

    gapHorizontal = 5;
    gapVertical   = 2;
    layout = new GridBagLayout();
    panelButtons = new JPanel(layout);
    add(new BaseScrollPane(panelButtons), BorderLayout.CENTER);

    m_ButtonGroup = new ButtonGroup();
    comps         = new ArrayList<>();
    m_ButtonLabels = new BaseToggleButton[m_Labels.length];
    for (i = 0; i < m_Labels.length; i++) {
      final String label = m_Labels[i].getValue();
      m_ButtonLabels[i] = new BaseToggleButton(label);
      m_ButtonLabels[i].addActionListener((ActionEvent e) -> setCurrentLabel(label));
      if (i == 0)
	m_ButtonLabels[i].setSelected(true);
      m_ButtonGroup.add(m_ButtonLabels[i]);
      comps.add(m_ButtonLabels[i]);
    }

    m_ButtonUnset = new BaseToggleButton("Unset");
    m_ButtonUnset.addActionListener((ActionEvent e) -> setCurrentLabel(null));
    comps.add(m_ButtonUnset);
    m_ButtonGroup.add(m_ButtonUnset);

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
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    if (m_Labels == null)
      return;

    super.finishInit();

    if (m_ButtonLabels.length > 0)
      m_ButtonLabels[0].doClick();
  }

  /**
   * Pre-selects the label.
   *
   * @param label	the label to use
   */
  protected void doPreselectCurrentLabel(String label) {
    int   	i;

    m_ButtonGroup.clearSelection();

    if (label == null)
      return;

    for (i = 0; i < m_ButtonLabels.length; i++) {
      if (m_ButtonLabels[i].getText().equals(label)) {
        m_ButtonLabels[i].doClick();
        break;
      }
    }
  }
}
