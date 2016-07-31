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
 * PreprocessTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Preprocessing tab.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreprocessTab
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  /** the GOe with the filter. */
  protected WekaGenericObjectEditorPanel m_PanelGOE;

  /** the panel for the filter and the buttons. */
  protected JPanel m_PanelTop;

  /** the checkbox to replace the datasets. */
  protected JCheckBox m_CheckBoxReplace;

  /** the checkbox for batch-filtering. */
  protected JCheckBox m_CheckBoxBatchFilter;

  /** the button for starting the filtering. */
  protected JButton m_ButtonStart;

  /** the button for stop the filtering. */
  protected JButton m_ButtonStop;

  /** whether the evaluation is currently running. */
  protected Thread m_Worker;

  /** the current filter. */
  protected Filter m_CurrentFilter;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Worker = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 		props;
    Filter		filter;
    JPanel		panel;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_SplitPane.setBottomComponentHidden(false);

    m_PanelTop = new JPanel(new BorderLayout());
    m_PanelTop.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelData.add(m_PanelTop, BorderLayout.NORTH);

    try {
      filter = (Filter) OptionUtils.forAnyCommandLine(
	Filter.class,
	InvestigatorPanel.getProperties().getProperty(
	  "Preprocess.Filter", AllFilter.class.getName()));
    }
    catch (Exception e) {
      filter = new AllFilter();
    }
    m_PanelGOE = new WekaGenericObjectEditorPanel(Filter.class, filter, true);
    m_PanelGOE.setPrefix("Filter");
    m_PanelTop.add(m_PanelGOE, BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelTop.add(panel, BorderLayout.SOUTH);

    m_CheckBoxReplace = new JCheckBox("Replace datasets");
    m_CheckBoxReplace.setSelected(props.getBoolean("Preprocess.ReplaceDatasets", true));
    panel.add(m_CheckBoxReplace);

    m_CheckBoxBatchFilter = new JCheckBox("Batch filter");
    m_CheckBoxBatchFilter.setSelected(props.getBoolean("Preprocess.BatchFilter", true));
    panel.add(m_CheckBoxBatchFilter);

    m_ButtonStart = new JButton("Start");
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    panel.add(m_ButtonStart);

    m_ButtonStop  = new JButton("Stop");
    m_ButtonStart.addActionListener((ActionEvent e) -> stopExecution());
    panel.add(m_ButtonStop);
  }

  /**
   * Starts the filtering.
   */
  protected void startExecution() {
    final int[] 	indices;
    final boolean	batch;
    final boolean 	replace;

    if (m_Worker != null)
      return;

    m_CurrentFilter = (Filter) m_PanelGOE.getCurrent();
    batch           = m_CheckBoxBatchFilter.isSelected();
    replace         = m_CheckBoxReplace.isSelected();
    indices         = getActualSelectedRows();

    m_Worker = new Thread(() -> {
      for (int i = 0; i < indices.length; i++) {
	DataContainer cont = getData().get(i);
	logMessage("Starting filtering " + (i+1) + "/" + indices.length + " '" + cont.getSourceShort() + "' using: " + OptionUtils.getCommandLine(m_CurrentFilter));
	try {
	  if ((!batch && (i == 0)) || batch)
	    m_CurrentFilter.setInputFormat(cont.getData());
	  Instances filtered = Filter.useFilter(cont.getData(), m_CurrentFilter);
	  logMessage("Finished filtering " + (i+1) + "/" + indices.length + " '" + cont.getSourceShort() + "' using: " + OptionUtils.getCommandLine(m_CurrentFilter));
	  if (replace) {
	    cont.setData(filtered);
	  }
	  else {
	    cont = new MemoryContainer(filtered);
	    getData().add(cont);
	  }
	  fireDataChange();
	}
	catch (Exception e) {
	  logError("Failed to filter data" + (i+1) + "/" + indices.length, e, "Filter");
	  break;
	}
      }
      m_Worker = null;
      updateButtons();
    });
    m_Worker.start();
    updateButtons();
  }

  /**
   * Stops the filtering.
   */
  protected void stopExecution() {
    if (m_Worker == null)
      return;

    m_Worker.stop();
    logMessage("Stopped filtering using: " + OptionUtils.getCommandLine(m_CurrentFilter));
    updateButtons();
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    m_ButtonStart.setEnabled((m_Worker == null) && (getSelectedRows().length > 0));
    m_ButtonStop.setEnabled(m_Worker != null);
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Preprocess";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "filter.png";
  }

  /**
   * Returns whether a readonly table is used.
   *
   * @return		true if readonly
   */
  protected boolean hasReadOnlyTable() {
    return false;
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  }

  /**
   * Gets called when the used changes the selection.
   */
  protected void dataTableSelectionChanged() {
    displayData();
    updateButtons();
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    // TODO
  }
}
