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
 * ParameterPanel.java
 * Copyright (C) 2010-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.goe.PropertyPanel;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A panel that lists one parameter (label and component or just
 * AbstractChooserPanel) per row. The sizes of the labels get automatically
 * adjusted. Optionally, a checkbox can be displayed per parameter.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ParameterPanel
  extends BasePanel
  implements ChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 7164103981772081436L;

  /** the check boxes. */
  protected List<BaseCheckBox> m_CheckBoxes;

  /** the labels. */
  protected List<JLabel> m_Labels;

  /** the parameters. */
  protected List<Component> m_Parameters;

  /** the actual parameters. */
  protected List<Component> m_ActualParameters;

  /** the horizontal gap. */
  protected int m_GapHorizontal;

  /** the vertical gap. */
  protected int m_GapVertical;

  /** whether to use checkboxes. */
  protected boolean m_UseCheckBoxes;

  /** whether to use mnemonic indicators (_ precedes the mnemonic letter). */
  protected boolean m_UseMnemonicIndicators;

  /** the preferred dimensions for JSpinner components. */
  protected Dimension m_PreferredDimensionJSpinner;

  /** the minimum dimensions for BaseComboBox components. */
  protected Dimension m_MinDimensionJComboBox;

  /** the change listeners. */
  protected Set<ChangeListener> m_ChangeListeners;

  /** the document listener. */
  protected DocumentListener m_DocumentListener;

  /** the action listener. */
  protected ActionListener m_ActionListener;

  /** the property change listener. */
  protected PropertyChangeListener m_PropertyChangeListener;

  /**
   * Initializes the panel.
   */
  public ParameterPanel() {
    this(false);
  }

  /**
   * Initializes the panel.
   *
   * @param useCheckBoxes	whether to use checkboxes
   */
  public ParameterPanel(boolean useCheckBoxes) {
    this(5, 2, useCheckBoxes);
  }

  /**
   * Initializes the panel.
   *
   * @param hgap	the horizontal gap in pixel
   * @param vgap	the vertical gap in pixel
   */
  public ParameterPanel(int hgap, int vgap) {
    this(hgap, vgap, false);
  }

  /**
   * Initializes the panel.
   *
   * @param hgap		the horizontal gap in pixel
   * @param vgap		the vertical gap in pixel
   * @param useCheckBoxes	whether to use checkboxes
   */
  public ParameterPanel(int hgap, int vgap, boolean useCheckBoxes) {
    super();

    m_GapHorizontal = hgap;
    m_GapVertical   = vgap;
    m_UseCheckBoxes = useCheckBoxes;

    update();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CheckBoxes                 = new ArrayList<>();
    m_Labels                     = new ArrayList<>();
    m_Parameters                 = new ArrayList<>();
    m_ActualParameters           = new ArrayList<>();
    m_PreferredDimensionJSpinner = new Dimension(100, GUIHelper.getInteger("GOESpinnerHeight", 20));
    m_MinDimensionJComboBox      = new Dimension(50, GUIHelper.getInteger("GOEComboBoxHeight", 20));
    m_ChangeListeners            = new HashSet<>();
    m_UseMnemonicIndicators      = true;
    m_DocumentListener = new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	stateChanged(new ChangeEvent(e));
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	stateChanged(new ChangeEvent(e));
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	stateChanged(new ChangeEvent(e));
      }
    };
    m_ActionListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	stateChanged(new ChangeEvent(this));
      }
    };
    m_PropertyChangeListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
	stateChanged(new ChangeEvent(this));
      }
    };
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
    m_CheckBoxes.clear();
    m_Labels.clear();
    m_Parameters.clear();
    m_ActualParameters.clear();
    update();
  }

  /**
   * Returns whether checkboxes are used.
   *
   * @return		true if checkboxes are used
   */
  public boolean useCheckBoxes() {
    return m_UseCheckBoxes;
  }

  /**
   * Sets whether to interpret "_" in the name as a mnemonic indicator
   * (the next letter is to be used as mnemonic indicator).
   *
   * @param value	true if to use mnemonic indicators
   */
  public void setUseMnemonicIndicators(boolean value) {
    m_UseMnemonicIndicators = value;
    update();
  }

  /**
   * Returns whether to interpret "_" in the name as a mnemonic indicator
   * (the next letter is to be used as mnemonic indicator).
   *
   * @return		true if to use mnemonic indicators
   */
  public boolean getUseMnemonicIndicators() {
    return m_UseMnemonicIndicators;
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
   * Sets the minimum dimension for BaseComboBox and derived classes.
   *
   * @param value	the minimum dimensions (do not use 0 for height!)
   */
  public void setMinDimensionJComboBox(Dimension value) {
    m_MinDimensionJComboBox = (Dimension) value.clone();
    update();
  }

  /**
   * Returns the minimum dimension for BaseComboBox and derived classes.
   *
   * @return		the minimum dimensions
   */
  public Dimension getMinDimensionJComboBox() {
    return m_MinDimensionJComboBox;
  }

  /**
   * Adds the label and component as new row at the end.
   *
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @return		the index of the parameter
   */
  public int addParameter(String label, Component comp) {
    return addParameter(false, label, comp);
  }

  /**
   * Adds the label and component as new row at the end.
   *
   * @param checked	whether the checkbox is checked
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @return		the index of the parameter
   */
  public int addParameter(boolean checked, String label, Component comp) {
    return addParameter(-1, checked, label, comp);
  }

  /**
   * Adds the chooser panel at the end. Cannot be used if checkboxes used.
   *
   * @param chooser	the chooser panel to add
   * @return		the index of the parameter
   * @see		#useCheckBoxes()
   */
  public int addParameter(AbstractChooserPanel chooser) {
    return addParameter(false, chooser);
  }

  /**
   * Adds the chooser panel at the end. Cannot be used if checkboxes used.
   *
   * @param chooser	the chooser panel to add
   * @return		the index of the parameter
   * @see		#useCheckBoxes()
   */
  public int addParameter(boolean checked, AbstractChooserPanel chooser) {
    return addParameter(-1, checked, chooser);
  }

  /**
   * Inserts the label and component as new row at the specified row.
   *
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the component at the end
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @return		the index of the parameter
   */
  public int addParameter(int index, String label, Component comp) {
    return addParameter(index, false, label, comp);
  }

  /**
   * Inserts the label and component as new row at the specified row.
   *
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the component at the end
   * @param checked	whether the checkbox is checked
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param comp	the component to add
   * @return		the index of the parameter
   */
  public int addParameter(int index, boolean checked, String label, Component comp) {
    JLabel		lbl;
    BaseCheckBox	check;
    JPanel		panel;
    GridBagConstraints	con;
    GridBagLayout	layout;
    Component 		actual;

    actual = comp;

    if (m_UseMnemonicIndicators) {
      lbl = new JLabel(label.replace("" + GUIHelper.MNEMONIC_INDICATOR, ""));
      lbl.setDisplayedMnemonic(GUIHelper.getMnemonic(label));
      lbl.setLabelFor(comp);
    }
    else {
      lbl = new JLabel(label);
      lbl.setLabelFor(comp);
    }

    if (m_UseCheckBoxes)
      check = new BaseCheckBox("", checked);
    else
      check = null;

    if (comp instanceof JTextArea)
      comp = new BaseScrollPane(comp);
    else if (comp instanceof JTextPane)
      comp = new BaseScrollPane(comp);

    layout = new GridBagLayout();
    panel  = new JPanel(layout);

    if (m_UseCheckBoxes) {
      con        = new GridBagConstraints();
      con.anchor = GridBagConstraints.WEST;
      con.gridy  = 0;
      con.gridx  = 0;
      con.insets = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
      layout.setConstraints(check, con);
      panel.add(check);
    }

    con        = new GridBagConstraints();
    con.anchor = GridBagConstraints.WEST;
    con.gridy  = 0;
    con.gridx  = 0;
    if (m_UseCheckBoxes)
      con.gridx++;
    con.ipadx  = 20;
    con.insets = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
    layout.setConstraints(lbl, con);
    panel.add(lbl);

    con = new GridBagConstraints();
    con.anchor = GridBagConstraints.WEST;
    con.fill   = GridBagConstraints.HORIZONTAL;
    con.gridy  = 0;
    con.gridx  = 1;
    if (m_UseCheckBoxes)
      con.gridx++;
    con.weightx = 100;
    con.ipadx   = 20;
    con.insets  = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
    layout.setConstraints(comp, con);
    panel.add(comp);

    if (index == -1) {
      if (m_UseCheckBoxes)
	m_CheckBoxes.add(check);
      m_Labels.add(lbl);
      index = m_Parameters.size();
      m_Parameters.add(comp);
      m_ActualParameters.add(actual);
    }
    else {
      if (m_UseCheckBoxes)
	m_CheckBoxes.add(index, check);
      m_Labels.add(index, lbl);
      m_Parameters.add(index, comp);
      m_ActualParameters.add(index, actual);
    }

    addChangeListenerTo(actual);

    update();

    return index;
  }

  /**
   * Inserts the label and component as new row at the end.
   *
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param actual	the actual, non-wrapped component to add
   * @param wrapper 	the wrapped component
   * @return		the index of the parameter
   */
  public int addParameter(String label, Component actual, JPanel wrapper) {
    return addParameter(-1, label, actual, wrapper);
  }

  /**
   * Inserts the label and component as new row at the specified row.
   *
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the component at the end
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param actual	the actual, non-wrapped component to add
   * @param wrapper 	the wrapped component
   * @return		the index of the parameter
   */
  public int addParameter(int index, String label, Component actual, JPanel wrapper) {
    return addParameter(index, false, label, actual, wrapper);
  }

  /**
   * Inserts the label and component as new row at the specified row.
   *
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the component at the end
   * @param checked	whether the checkbox is checked
   * @param label	the label to add, the mnemonic to use is preceded by "_"
   * @param actual	the actual, non-wrapped component to add
   * @param wrapper 	the wrapped component
   * @return		the index of the parameter
   */
  public int addParameter(int index, boolean checked, String label, Component actual, JPanel wrapper) {
    JLabel		lbl;
    BaseCheckBox	check;
    JPanel		panel;
    GridBagConstraints	con;
    GridBagLayout	layout;

    if (m_UseMnemonicIndicators) {
      lbl = new JLabel(label.replace("" + GUIHelper.MNEMONIC_INDICATOR, ""));
      lbl.setDisplayedMnemonic(GUIHelper.getMnemonic(label));
      lbl.setLabelFor(actual);
    }
    else {
      lbl = new JLabel(label);
      lbl.setLabelFor(actual);
    }

    if (m_UseCheckBoxes)
      check = new BaseCheckBox("", checked);
    else
      check = null;

    layout = new GridBagLayout();
    panel  = new JPanel(layout);

    if (m_UseCheckBoxes) {
      con        = new GridBagConstraints();
      con.anchor = GridBagConstraints.WEST;
      con.gridy  = 0;
      con.gridx  = 0;
      con.insets = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
      layout.setConstraints(check, con);
      panel.add(check);
    }

    con        = new GridBagConstraints();
    con.anchor = GridBagConstraints.WEST;
    con.gridy  = 0;
    con.gridx  = 0;
    if (m_UseCheckBoxes)
      con.gridx++;
    con.ipadx  = 20;
    con.insets = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
    layout.setConstraints(lbl, con);
    panel.add(lbl);

    con = new GridBagConstraints();
    con.anchor = GridBagConstraints.WEST;
    con.fill   = GridBagConstraints.HORIZONTAL;
    con.gridy  = 0;
    con.gridx  = 1;
    if (m_UseCheckBoxes)
      con.gridx++;
    con.weightx = 100;
    con.ipadx   = 20;
    con.insets  = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
    layout.setConstraints(wrapper, con);
    panel.add(wrapper);

    if (index == -1) {
      if (m_UseCheckBoxes)
	m_CheckBoxes.add(check);
      m_Labels.add(lbl);
      index = m_Parameters.size();
      m_Parameters.add(wrapper);
      m_ActualParameters.add(actual);
    }
    else {
      if (m_UseCheckBoxes)
	m_CheckBoxes.add(index, check);
      m_Labels.add(index, lbl);
      m_Parameters.add(index, wrapper);
      m_ActualParameters.add(index, actual);
    }

    addChangeListenerTo(actual);

    update();

    return index;
  }

  /**
   * Inserts the chooser panel as new row at the specified row.
   *
   * @param chooser	the chooser panel to insert
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the chooser at the end
   */
  public void addParameter(int index, AbstractChooserPanel chooser) {
    addParameter(index, false, chooser);
  }

  /**
   * Inserts the chooser panel as new row at the specified row.
   *
   * @param index	the row index to insert the label/editfield at, -1 will
   * 			add the chooser at the end
   * @param checked	whether the checkbox is checked
   * @param chooser	the chooser panel to insert
   * @return		the index of the parameter
   */
  public int addParameter(int index, boolean checked, AbstractChooserPanel chooser) {
    JPanel		panel;
    BaseCheckBox		check;
    GridBagConstraints	con;
    GridBagLayout	layout;

    layout = new GridBagLayout();
    panel  = new JPanel(layout);

    if (m_UseCheckBoxes)
      check = new BaseCheckBox("", checked);
    else
      check = null;

    if (m_UseCheckBoxes) {
      con           = new GridBagConstraints();
      con.anchor    = GridBagConstraints.WEST;
      con.gridy     = 0;
      con.gridx     = 0;
      con.insets    = new Insets(m_GapVertical + 1, m_GapHorizontal, m_GapVertical + 1, m_GapHorizontal);
      layout.setConstraints(check, con);
      add(check);
    }

    con   = new GridBagConstraints();
    con.anchor  = GridBagConstraints.WEST;
    con.fill    = GridBagConstraints.HORIZONTAL;
    con.gridy   = 0;
    con.gridx   = 0;
    con.weightx = 100;
    con.ipadx   = 20;
    con.insets  = new Insets(m_GapVertical, m_GapHorizontal, m_GapVertical, m_GapHorizontal);
    layout.setConstraints(chooser, con);
    panel.add(chooser);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(chooser);

    if (index == -1) {
      if (m_UseCheckBoxes)
	m_CheckBoxes.add(check);
      m_Labels.add(chooser.getPrefixLabel());
      index = m_Parameters.size();
      m_Parameters.add(chooser);
      m_ActualParameters.add(chooser);
    }
    else {
      if (m_UseCheckBoxes)
	m_CheckBoxes.add(index, check);
      m_Labels.add(index, chooser.getPrefixLabel());
      m_Parameters.add(index, chooser);
      m_ActualParameters.add(index, chooser);
    }

    addChangeListenerTo(chooser);

    update();

    return index;
  }

  /**
   * Removes the parameter at the specified location.
   *
   * @param index	the row index
   */
  public void removeParameter(int index) {
    Component	actual;

    m_Labels.remove(index);
    m_Parameters.remove(index);
    actual = m_ActualParameters.remove(index);
    removeChangeListenerFrom(actual);
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
   * Returns the actual parameter component (without wrapping scroll pane etc)
   * at the specified location.
   *
   * @param index	the row index
   * @return		the requested component
   */
  public Component getActualParameter(int index) {
    return m_ActualParameters.get(index);
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
   * Returns the checkbof for the parameter at the specified location.
   *
   * @param index	the row index
   * @return		the requested checkbox, null if not used
   * @see		#useCheckBoxes()
   */
  public BaseCheckBox getCheckBox(int index) {
    if (m_UseCheckBoxes)
      return m_CheckBoxes.get(index);
    else
      return null;
  }

  /**
   * Returns whether the checkbox is ticked.
   *
   * @param index	the row index
   * @return		true if checked
   */
  public boolean isChecked(int index) {
    return m_UseCheckBoxes && m_CheckBoxes.get(index).isSelected();
  }

  /**
   * Sets the tiptext to display.
   *
   * @param index	the index of the component
   * @param text	the tiptext to use, null to turn off
   * @param label	whether to set the tiptext for the label
   * @param comp	whether to set the tiptext for the component
   */
  public void setToolTipText(int index, String text, boolean label, boolean comp) {
    setToolTipText(index, text, false, label, comp);
  }

  /**
   * Sets the tiptext to display.
   *
   * @param index	the index of the component
   * @param text	the tiptext to use, null to turn off
   * @param check	whether to set the tiptext for the checkbox
   * @param label	whether to set the tiptext for the label
   * @param comp	whether to set the tiptext for the component (must be {@link JComponent})
   */
  public void setToolTipText(int index, String text, boolean check, boolean label, boolean comp) {
    if (m_UseCheckBoxes && check)
      getCheckBox(index).setToolTipText(text);
    if (label)
      getLabel(index).setToolTipText(text);
    if (comp && (getActualParameter(index) instanceof JComponent))
      ((JComponent) getActualParameter(index)).setToolTipText(text);
  }

  /**
   * Fixes the dimensions for various components.
   *
   * @see	#m_PreferredDimensionJSpinner
   * @see	#m_MinDimensionJComboBox
   */
  protected void fixDimensions() {
    int			i;

    for (i = 0; i < m_ActualParameters.size(); i++) {
      if (m_ActualParameters.get(i) instanceof JSpinner)
	m_ActualParameters.get(i).setPreferredSize((Dimension) m_PreferredDimensionJSpinner.clone());
      if (m_ActualParameters.get(i) instanceof JComboBox)
	m_ActualParameters.get(i).setMinimumSize((Dimension) m_MinDimensionJComboBox.clone());
    }
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

    // set min/preferred dimensions
    fixDimensions();

    for (i = 0; i < m_Labels.size(); i++) {
      if (m_UseCheckBoxes) {
	con           = new GridBagConstraints();
	con.anchor    = GridBagConstraints.WEST;
	con.gridy     = i;
	con.gridx     = 0;
	con.insets    = new Insets(m_GapVertical + 1, m_GapHorizontal, m_GapVertical + 1, m_GapHorizontal);
	layout.setConstraints(m_CheckBoxes.get(i), con);
	add(m_CheckBoxes.get(i));
      }

      con        = new GridBagConstraints();
      con.anchor = GridBagConstraints.WEST;
      con.gridy  = i;
      con.gridx  = 0;
      if (m_UseCheckBoxes)
	con.gridx++;
      con.ipadx  = 20;
      con.insets = new Insets(m_GapVertical + 1, 5, m_GapVertical + 1, 5);
      layout.setConstraints(m_Labels.get(i), con);
      add(m_Labels.get(i));

      con           = new GridBagConstraints();
      con.anchor    = GridBagConstraints.WEST;
      con.fill      = GridBagConstraints.HORIZONTAL;
      con.gridy     = i;
      con.gridx     = 1;
      if (m_UseCheckBoxes)
	con.gridx++;
      con.weightx   = 100;
      con.ipadx     = 20;
      con.gridwidth = GridBagConstraints.REMAINDER;
      con.insets    = new Insets(m_GapVertical + 1, 5, m_GapVertical + 1, 5);
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

    for (i = 0; i < m_Parameters.size(); i++) {
      if (m_UseCheckBoxes)
	m_CheckBoxes.get(i).setEnabled(enabled);
      m_Parameters.get(i).setEnabled(enabled);
      m_ActualParameters.get(i).setEnabled(enabled);
    }

    super.setEnabled(enabled);
  }

  /**
   * Adds the change listener.
   *
   * @param l		the change listener
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the change listener.
   *
   * @param l		the change listener
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all the change listeners.
   */
  protected void notifyChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_ChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Invoked when the target of the listener has changed its state.
   *
   * @param e  a ChangeEvent object
   */
  public void stateChanged(ChangeEvent e) {
    notifyChangeListeners();
  }

  /**
   * Adds a change listener to the specified component.
   *
   * @param comp	the component to add the listener to
   */
  protected void addChangeListenerTo(Component comp) {
    if (comp instanceof AbstractChooserPanel)
      ((AbstractChooserPanel) comp).addChangeListener(this);
    else if (comp instanceof JTextComponent)
      ((JTextComponent) comp).getDocument().addDocumentListener(m_DocumentListener);
    else if (comp instanceof AbstractButton)
      ((AbstractButton) comp).addActionListener(m_ActionListener);
    else if (comp instanceof JComboBox)
      ((JComboBox) comp).addActionListener(m_ActionListener);
    else if (comp instanceof JSpinner)
      ((JSpinner) comp).addChangeListener(this);
    else if (comp instanceof PropertyEditor)
      ((PropertyEditor) comp).addPropertyChangeListener(m_PropertyChangeListener);
    else if (comp instanceof PropertyPanel)
      ((PropertyPanel) comp).getPropertyEditor().addPropertyChangeListener(m_PropertyChangeListener);
    //else
    //  System.err.println("Failed to add change listener to component type: " + Utils.classToString(comp));
  }

  /**
   * Removes a change listener from the specified component.
   *
   * @param comp	the component to remove the listener from
   */
  protected void removeChangeListenerFrom(Component comp) {
    if (comp instanceof AbstractChooserPanel)
      ((AbstractChooserPanel) comp).removeChangeListener(this);
    else if (comp instanceof JTextComponent)
      ((JTextComponent) comp).getDocument().removeDocumentListener(m_DocumentListener);
    else if (comp instanceof AbstractButton)
      ((AbstractButton) comp).removeActionListener(m_ActionListener);
    else if (comp instanceof JComboBox)
      ((JComboBox) comp).removeActionListener(m_ActionListener);
    else if (comp instanceof JSpinner)
      ((JSpinner) comp).removeChangeListener(this);
    else if (comp instanceof PropertyEditor)
      ((PropertyEditor) comp).removePropertyChangeListener(m_PropertyChangeListener);
    else if (comp instanceof PropertyPanel)
      ((PropertyPanel) comp).getPropertyEditor().removePropertyChangeListener(m_PropertyChangeListener);
    //else
    //  System.err.println("Failed to remove change listener from component type: " + Utils.classToString(comp));
  }
}
