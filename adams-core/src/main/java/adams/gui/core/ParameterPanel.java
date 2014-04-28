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

/**
 * ParameterPanel.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import adams.gui.chooser.AbstractChooserPanel;

/**
 * A panel that lists one parameter (label and component or just
 * AbstractChooserPanel) per row. The sizes of the labels get automatically
 * adjusted.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ParameterPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 7164103981772081436L;

  /** the labels. */
  protected List<JLabel> m_Labels;

  /** the parameters. */
  protected List<Component> m_Parameters;

  /** the horizontal gap. */
  protected int m_GapHorizontal;

  /** the vertical gap. */
  protected int m_GapVertical;

  /** the preferred dimensions for JSpinner components. */
  protected Dimension m_PreferredDimensionJSpinner;

  /**
   * Initializes the panel.
   */
  public ParameterPanel() {
    this(0, 0);
  }

  /**
   * Initializes the panel.
   *
   * @param hgap	the horizontal gap in pixel
   * @param vgap	the vertical gap in pixel
   */
  public ParameterPanel(int hgap, int vgap) {
    super();

    m_GapHorizontal = hgap;
    m_GapVertical   = vgap;

    update();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Labels                     = new ArrayList<JLabel>();
    m_Parameters                 = new ArrayList<Component>();
    m_PreferredDimensionJSpinner = new Dimension(100, 20);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    update();
  }

  /**
   * Removes all parameters.
   */
  public void clearParameters() {
    m_Labels.clear();
    m_Parameters.clear();
    update();
  }

  /**
   * Sets the preferred dimension for JSpinner and derived classes.
   *
   * @param value	the preferred dimensions (do not use 0 for height!)
   */
  public void setPreferredDimensionJSpinner(Dimension value) {
    m_PreferredDimensionJSpinner = (Dimension) value.clone();
    update();
  }

  /**
   * Returns the preferred dimension for JSpinner and derived classes.
   *
   * @return		the preferred dimensions
   */
  public Dimension getPreferredDimensionJSpinner() {
    return m_PreferredDimensionJSpinner;
  }

  /**
   * Adds the label and component as new row at the end.
   *
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   */
  public void addParameter(String label, Component comp) {
    addParameter(-1, label, comp);
  }

  /**
   * Adds the chooser panel at the end.
   *
   * @param chooser	the chooser panel to add
   */
  public void addParameter(AbstractChooserPanel chooser) {
    addParameter(-1, chooser);
  }

  /**
   * Inserts the label and component as new row at the specified row.
   *
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the component at the end
   */
  public void addParameter(int index, String label, Component comp) {
    JLabel		lbl;
    JPanel		panel;
    GridBagConstraints	gbC;
    GridBagLayout	gbL;

    lbl = new JLabel(label.replace("" + GUIHelper.MNEMONIC_INDICATOR, ""));
    lbl.setDisplayedMnemonic(GUIHelper.getMnemonic(label));
    lbl.setLabelFor(comp);

    if (comp instanceof JTextArea)
      comp = new BaseScrollPane(comp);
    else if (comp instanceof JTextPane)
      comp = new BaseScrollPane(comp);
    
    gbL   = new GridBagLayout();
    panel = new JPanel(gbL);
    
    gbC   = new GridBagConstraints();
    gbC.anchor = GridBagConstraints.WEST;
    gbC.gridy = 0;
    gbC.gridx = 0;
    gbC.insets = new Insets(2, 5, 2, 5);
    gbL.setConstraints(lbl, gbC);
    panel.add(lbl);
    
    gbC = new GridBagConstraints();
    gbC.anchor = GridBagConstraints.WEST;
    gbC.fill = GridBagConstraints.HORIZONTAL;
    gbC.gridy = 0;
    gbC.gridx = 1;
    gbC.weightx = 100;
    gbC.ipadx = 20;
    gbC.insets = new Insets(2, 5, 2, 5);
    gbL.setConstraints(comp, gbC);
    panel.add(comp);

    if (index == -1) {
      m_Labels.add(lbl);
      m_Parameters.add(comp);
    }
    else {
      m_Labels.add(index, lbl);
      m_Parameters.add(index, comp);
    }

    update();
  }

  /**
   * Inserts the chooser panel as new row at the specified row.
   *
   * @param chooser	the chooser panel to insert
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the chooser at the end
   */
  public void addParameter(int index, AbstractChooserPanel chooser) {
    JPanel		panel;
    GridBagConstraints	gbC;
    GridBagLayout	gbL;
    
    gbL   = new GridBagLayout();
    panel = new JPanel(gbL);
    
    gbC   = new GridBagConstraints();
    gbC.anchor = GridBagConstraints.WEST;
    gbC.fill = GridBagConstraints.HORIZONTAL;
    gbC.gridy = 0;
    gbC.gridx = 0;
    gbC.weightx = 100;
    gbC.ipadx = 20;
    gbC.insets = new Insets(2, 5, 2, 5);
    gbL.setConstraints(chooser, gbC);
    panel.add(chooser);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(chooser);

    if (index == -1) {
      m_Labels.add(chooser.getPrefixLabel());
      m_Parameters.add(chooser);
    }
    else {
      m_Labels.add(index, chooser.getPrefixLabel());
      m_Parameters.add(index, chooser);
    }

    update();
  }

  /**
   * Removes the parameter at the specified location.
   *
   * @param index	the row index
   */
  public void removeParameter(int index) {
    m_Labels.remove(index);
    m_Parameters.remove(index);
    update();
  }

  /**
   * Returns the parameter component at the specified location.
   *
   * @param index	the row index
   * @return		the requested component
   */
  public Component getParameter(int index) {
    return m_Parameters.get(index);
  }

  /**
   * Returns the number of parameters currently displayed.
   *
   * @return		the number of rows
   */
  public int getParameterCount() {
    return m_Parameters.size();
  }

  /**
   * Returns the label for the parameter at the specified location.
   *
   * @param index	the row index
   * @return		the requested label
   */
  public JLabel getLabel(int index) {
    return m_Labels.get(index);
  }

  /**
   * Updates the layout.
   */
  protected void update() {
    int			i;
    GridBagLayout	layout;
    GridBagConstraints	con;
    JPanel		panel;
    
    removeAll();
    
    layout = new GridBagLayout();
    setLayout(layout);

    // set preferred dimensions for JSpinners
    for (i = 0; i < m_Parameters.size(); i++) {
      if (m_Parameters.get(i) instanceof JSpinner)
	((JSpinner) m_Parameters.get(i)).setPreferredSize((Dimension) m_PreferredDimensionJSpinner.clone());
    }

    for (i = 0; i < m_Labels.size(); i++) {
      con        = new GridBagConstraints();
      con.anchor = GridBagConstraints.WEST;
      con.gridy  = i;
      con.gridx  = 0;
      con.insets = new Insets(3, 5, 3, 5);
      layout.setConstraints(m_Labels.get(i), con);
      add(m_Labels.get(i));

      con           = new GridBagConstraints();
      con.anchor    = GridBagConstraints.WEST;
      con.fill      = GridBagConstraints.HORIZONTAL;
      con.gridy     = i;
      con.gridx     = 1;
      con.weightx   = 100;
      con.ipadx     = 20;
      con.gridwidth = GridBagConstraints.REMAINDER;
      con.insets    = new Insets(3, 5, 3, 5);
      layout.setConstraints(m_Parameters.get(i), con);
      add(m_Parameters.get(i));
    }

    // filler at bottom
    panel         = new JPanel();
    con           = new GridBagConstraints();
    con.anchor    = GridBagConstraints.WEST;
    con.fill      = GridBagConstraints.BOTH;
    con.gridy     = m_Labels.size();
    con.gridx     = 0;
    con.weighty   = 100;
    con.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(panel, con);
    add(panel);
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param enabled	if true then the parameters will be editable
   */
  @Override
  public void setEnabled(boolean enabled) {
    int		i;

    for (i = 0; i < m_Parameters.size(); i++)
      m_Parameters.get(i).setEnabled(enabled);

    super.setEnabled(enabled);
  }
}
