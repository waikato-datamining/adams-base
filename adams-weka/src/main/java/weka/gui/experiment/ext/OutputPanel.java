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
 * OutputPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import weka.experiment.ResultListener;

/**
 * Allows the user to select the output type, e.g., ARFF file or JDBC database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutputPanel
  extends AbstractSetupOptionPanel {

  /** for serialization. */
  private static final long serialVersionUID = 5858138358135989633L;

  /** the map for panel name / panel object relation. */
  protected HashMap<String,AbstractOutputPanel> m_Panels;

  /** the panel for the combobox. */
  protected JPanel m_PanelComboBox;

  /** the combobox listing all the output types. */
  protected JComboBox m_ComboBoxOutput;
  
  /** the currently output panel. */
  protected AbstractOutputPanel m_Panel;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String[]		panels;
    AbstractOutputPanel	panel;
    
    super.initialize();
    
    m_Panels = new HashMap<String,AbstractOutputPanel>();
    panels   = AbstractOutputPanel.getPanels();
    for (String cls: panels) {
      try {
	panel = (AbstractOutputPanel) Class.forName(cls).newInstance();
	m_Panels.put(panel.getOutputName(), panel);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate output panel class: " + cls);
	e.printStackTrace();
      }
    }
  }
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    String[]	names;
    
    super.initGUI();
    
    m_Panel = new ArffOutputPanel();
    add(m_Panel, BorderLayout.CENTER);
    
    names = m_Panels.keySet().toArray(new String[m_Panels.size()]);
    Arrays.sort(names);
    m_ComboBoxOutput = new JComboBox(names);
    m_ComboBoxOutput.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	AbstractOutputPanel m_Panel = m_Panels.get((String) m_ComboBoxOutput.getSelectedItem());
	removeAll();
	add(m_PanelComboBox, BorderLayout.WEST);
	add(m_Panel, BorderLayout.CENTER);
	invalidate();
	revalidate();
	repaint();
	modified();
      }
    });
    m_PanelComboBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelComboBox.add(m_ComboBoxOutput);
    add(m_PanelComboBox, BorderLayout.WEST);
    m_ComboBoxOutput.setSelectedItem(m_Panel.getOutputName());
  }

  /**
   * Sets the setup panel this option panel belongs to.
   * 
   * @param value	the owner
   */
  @Override
  public void setOwner(AbstractSetupPanel value) {
    super.setOwner(value);
    
    for (AbstractOutputPanel panel: m_Panels.values())
      panel.setOwner(value);
  }
  
  /**
   * Sets the {@link ResultListener}.
   * 
   * @param value	the listener
   */
  public void setResultListener(ResultListener value) {
    AbstractOutputPanel	match;
    String		custom;
    
    match  = null;
    custom = null;
    
    for (AbstractOutputPanel panel: m_Panels.values()) {
      // always matches, use as catch-all at the end
      if (panel instanceof CustomOutputPanel) {
	custom = panel.getOutputName();
	continue;
      }
      if (panel.handlesResultListener(value)) {
	match = panel;
	break;
      }
    }

    // use catch-all
    if ((match == null) && (custom != null))
      match = m_Panels.get(custom);
    
    if (match == null)
      throw new IllegalArgumentException("Cannot handle ResultListener: " + value.getClass().getName());
    
    m_ComboBoxOutput.setSelectedItem(match.getOutputName());
    match.setResultListener(value);
  }
  
  /**
   * Returns the configured {@link ResultListener}.
   * 
   * @return		the listener, null if failed to set up
   */
  public ResultListener getResultListener() {
    return m_Panel.getResultListener();
  }
}
