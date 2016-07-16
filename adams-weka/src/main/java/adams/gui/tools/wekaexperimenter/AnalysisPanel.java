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
 * AnalysisPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaexperimenter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import weka.core.Instances;
import adams.core.Utils;

/**
 * The analysis panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AnalysisPanel
  extends AbstractExperimenterPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7809897225003422111L;

  /** the current panel. */
  protected AbstractAnalysisPanel m_PanelAnalysis;

  /** the combobox with all available panels. */
  protected JComboBox m_ComboBoxPanels;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    String[]			classes;
    List<AbstractAnalysisPanel>	panels;
    int				i;
    JPanel			panel;
    JLabel			label;
    
    super.initGUI();
    
    classes = AbstractAnalysisPanel.getPanels();
    panels  = new ArrayList<AbstractAnalysisPanel>();
    for (i = 0; i < classes.length; i++) {
      try {
	if (classes[i].equals(DefaultAnalysisPanel.class.getName()))
	  continue;
	panels.add((AbstractAnalysisPanel) Class.forName(classes[i]).newInstance());
      }
      catch (Exception e) {
	logError("Failed to instantiate analysis panel: " + classes[i], "Analysis panels");
      }
    }
    panels.add(0, new DefaultAnalysisPanel());
    m_ComboBoxPanels = new JComboBox(panels.toArray(new AbstractAnalysisPanel[panels.size()]));
    m_ComboBoxPanels.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	updatePanel((AbstractAnalysisPanel) m_ComboBoxPanels.getSelectedItem());
      }
    });
    
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.NORTH);
    label = new JLabel("Type");
    label.setLabelFor(m_ComboBoxPanels);
    panel.add(label);
    panel.add(m_ComboBoxPanels);
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    m_ComboBoxPanels.setSelectedIndex(0);
  }
  
  /**
   * Gets called when the owner changes.
   */
  @Override
  protected void ownerChanged() {
    String	preferred;
    int		i;
    
    super.ownerChanged();
    
    if (getOwner() != null) {
      preferred = ExperimenterPanel.getProperties().getProperty(
	  "ResultsInitialPanel", DefaultAnalysisPanel.class.getName());
      for (i = 0; i < m_ComboBoxPanels.getItemCount(); i++) {
	if (m_ComboBoxPanels.getItemAt(i).getClass().getName().equals(preferred)) {
	  updatePanel((AbstractAnalysisPanel) m_ComboBoxPanels.getItemAt(i));
	  break;
	}
      }
    }
  }

  /**
   * Sets the panel as the new analysis panel.
   * 
   * @param panel	the panel to use
   */
  protected void updatePanel(AbstractAnalysisPanel panel) {
    Instances	results;
    
    results = null;
    if (m_PanelAnalysis != null) {
      results = m_PanelAnalysis.getResults();
      remove(m_PanelAnalysis);
    }
    m_PanelAnalysis = (AbstractAnalysisPanel) Utils.deepCopy(panel);
    add(m_PanelAnalysis, BorderLayout.CENTER);
    m_PanelAnalysis.setResults(results);
  }

  /**
   * Checks whether the results can be handled at all.
   * 
   * @param results	the results to check
   * @return		null if can handle, otherwise error message
   */
  public String handlesResults(Instances results) {
    if (m_PanelAnalysis != null)
      return m_PanelAnalysis.handlesResults(results);
    else
      return "No analysis panel available!";
  }

  /**
   * Checks whether there are any results available.
   * 
   * @return		true if results available
   */
  public boolean hasResults() {
    return (getResults() != null);
  }
  
  /**
   * Sets the results to use for analysis.
   * 
   * @param value	the results
   */
  public void setResults(Instances value) {
    if (m_PanelAnalysis != null)
      m_PanelAnalysis.setResults(value);
  }
  
  /**
   * Returns the current set results.
   * 
   * @return		the results
   */
  public Instances getResults() {
    if (m_PanelAnalysis != null)
      return m_PanelAnalysis.getResults();
    else
      return null;
  }
}
