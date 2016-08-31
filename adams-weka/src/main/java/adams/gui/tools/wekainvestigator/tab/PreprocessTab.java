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
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeSelectionPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeSummaryPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.AttributeVisualizationPanel;
import adams.gui.tools.wekainvestigator.tab.preprocesstab.InstancesSummaryPanel;
import weka.core.Instances;
import weka.filters.AllFilter;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * Preprocessing tab.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreprocessTab
  extends AbstractInvestigatorTabWithEditableDataTable {

  private static final long serialVersionUID = -94945456385486233L;

  /** the GOe with the filter. */
  protected WekaGenericObjectEditorPanel m_PanelGOE;

  /** the panel for the filter and the buttons. */
  protected JPanel m_PanelTop;

  /** the panel for the preprocess panels. */
  protected JPanel m_PanelMain;

  /** the checkbox to replace the datasets. */
  protected JCheckBox m_CheckBoxReplace;

  /** the checkbox for batch-filtering. */
  protected JCheckBox m_CheckBoxBatchFilter;

  /** the whether to keep the relation name. */
  protected JCheckBox m_CheckBoxKeepName;

  /** the button for starting the filtering. */
  protected JButton m_ButtonStart;

  /** the button for stop the filtering. */
  protected JButton m_ButtonStop;

  /** the instances summary panel. */
  protected InstancesSummaryPanel m_PanelInstSummary;

  /** the attribute selection panel. */
  protected AttributeSelectionPanel m_PanelAttSelection;

  /** the attribute summary panel. */
  protected AttributeSummaryPanel m_PanelAttSummary;

  /** the attribute visualization panel. */
  protected AttributeVisualizationPanel m_PanelAttVisualization;

  /** button for removing checked attributes. */
  protected JButton m_ButtonRemoveChecked;

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
    JPanel		panel2;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    // top
    m_PanelTop = new JPanel(new BorderLayout());
    m_PanelTop.setBorder(BorderFactory.createTitledBorder("Filter"));
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
    m_PanelGOE.moveChooseButton(true);
    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    panel.add(m_PanelGOE, BorderLayout.CENTER);
    m_PanelTop.add(panel, BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelTop.add(panel, BorderLayout.SOUTH);

    m_ButtonStart = new JButton("Start");
    m_ButtonStart.setEnabled(false);
    m_ButtonStart.addActionListener((ActionEvent e) -> startExecution());
    panel.add(m_ButtonStart);

    m_ButtonStop  = new JButton("Stop");
    m_ButtonStop.setEnabled(false);
    m_ButtonStart.addActionListener((ActionEvent e) -> stopExecution());
    panel.add(m_ButtonStop);

    m_CheckBoxReplace = new JCheckBox("Replace datasets");
    m_CheckBoxReplace.setSelected(props.getBoolean("Preprocess.ReplaceDatasets", true));
    panel.add(m_CheckBoxReplace);

    m_CheckBoxKeepName = new JCheckBox("Keep name");
    m_CheckBoxKeepName.setSelected(props.getBoolean("Preprocess.KeepName", true));
    panel.add(m_CheckBoxKeepName);

    m_CheckBoxBatchFilter = new JCheckBox("Batch filter");
    m_CheckBoxBatchFilter.setSelected(props.getBoolean("Preprocess.BatchFilter", false));
    panel.add(m_CheckBoxBatchFilter);

    // main
    m_PanelMain = new JPanel(new GridLayout(1, 2));
    m_PanelMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelData.add(m_PanelMain, BorderLayout.CENTER);

    // main-left
    panel = new JPanel(new BorderLayout());
    m_PanelMain.add(panel);

    m_PanelInstSummary = new InstancesSummaryPanel();
    m_PanelInstSummary.setBorder(BorderFactory.createTitledBorder("Dataset summary"));
    panel.add(m_PanelInstSummary, BorderLayout.NORTH);

    panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panel2, BorderLayout.SOUTH);
    m_ButtonRemoveChecked = new JButton("Remove checked", GUIHelper.getIcon("delete.gif"));
    m_ButtonRemoveChecked.addActionListener((ActionEvent e) -> removeCheckedAttributes());
    panel2.add(m_ButtonRemoveChecked);

    m_PanelAttSelection = new AttributeSelectionPanel();
    m_PanelAttSelection.setBorder(BorderFactory.createTitledBorder("Attributes"));
    m_PanelAttSelection.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
      int[] indices = m_PanelAttSelection.getSelectedRows();
      if (indices.length == 1) {
	m_PanelAttSummary.setAttribute(indices[0]);
	m_PanelAttVisualization.setAttribute(indices[0]);
      }
      else {
	// TODO unset?
      }
    });
    panel.add(m_PanelAttSelection, BorderLayout.CENTER);

    // main-right
    panel = new JPanel(new GridLayout(2, 1));
    m_PanelMain.add(panel);

    m_PanelAttSummary = new AttributeSummaryPanel();
    m_PanelAttSummary.setBorder(BorderFactory.createTitledBorder("Attribute summary"));
    panel.add(m_PanelAttSummary);

    m_PanelAttVisualization = new AttributeVisualizationPanel();
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createTitledBorder("Attribute visualization"));
    panel2.add(m_PanelAttVisualization);
    panel.add(panel2);

    m_SplitPane.setBottomComponentHidden(false);
    m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
  }

  /**
   * Starts the filtering.
   */
  protected void startExecution() {
    final int[] 	indices;
    final boolean	batch;
    final boolean 	replace;
    final boolean	keep;

    if (m_Worker != null)
      return;

    m_CurrentFilter = (Filter) m_PanelGOE.getCurrent();
    batch           = m_CheckBoxBatchFilter.isSelected();
    replace         = m_CheckBoxReplace.isSelected();
    keep            = m_CheckBoxKeepName.isSelected();
    indices         = getSelectedRows();

    m_Worker = new Thread(() -> {
      for (int i = 0; i < indices.length; i++) {
	DataContainer cont = getData().get(indices[i]);
	logMessage("Starting filtering " + (i+1) + "/" + indices.length + " '" + cont.getSource() + "' using " + OptionUtils.getCommandLine(m_CurrentFilter));
	try {
	  String oldName = cont.getData().relationName();
	  if ((!batch && (i == 0)) || batch)
	    m_CurrentFilter.setInputFormat(cont.getData());
	  Instances filtered = Filter.useFilter(cont.getData(), m_CurrentFilter);
	  if (keep)
	    filtered.setRelationName(oldName);
	  logMessage("Finished filtering " + (i+1) + "/" + indices.length + " '" + cont.getSource() + "' using " + OptionUtils.getCommandLine(m_CurrentFilter));
	  if (replace) {
	    cont.setData(filtered);
	    fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, indices[i]));
	  }
	  else {
	    cont = new MemoryContainer(filtered);
	    getData().add(cont);
	    fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
	  }
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
    logMessage("Stopped filtering using " + OptionUtils.getCommandLine(m_CurrentFilter));
    updateButtons();
  }

  /**
   * Updates the buttons.
   */
  public void updateButtons() {
    super.updateButtons();
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
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
  }

  /**
   * Notifies all the tabs that the data has changed.
   *
   * @param e		the event to send
   */
  public void fireDataChange(WekaInvestigatorDataEvent e) {
    super.fireDataChange(e);
    displayData();
  }

  /**
   * Gets called when the user changes the selection.
   */
  protected void dataTableSelectionChanged() {
    super.dataTableSelectionChanged();
    displayData();
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    DataContainer	cont;

    if ((getData().size() > 0) && (getSelectedRows().length == 1)) {
      cont = getData().get(getSelectedRows()[0]);
      m_PanelInstSummary.setInstances(cont.getData());
      m_PanelAttSelection.setInstances(cont.getData());
      m_PanelAttSummary.setInstances(cont.getData());
      m_PanelAttVisualization.setInstances(cont.getData());
      m_ButtonRemoveChecked.setEnabled(true);
    }
    else {
      m_PanelInstSummary.setInstances(null);
      m_PanelAttSelection.setInstances(null);
      m_PanelAttSummary.setInstances(null);
      m_PanelAttVisualization.setInstances(null);
      m_ButtonRemoveChecked.setEnabled(false);
    }

    repaint();
  }

  /**
   * Removes the check attributes.
   */
  protected void removeCheckedAttributes() {
    int[]		indices;
    Remove		remove;
    int			index;
    DataContainer	cont;
    Instances		filtered;
    boolean		keep;
    String 		oldName;

    if (getSelectedRows().length != 1)
      return;
    index = getSelectedRows()[0];
    cont  = getData().get(index);

    indices = m_PanelAttSelection.getSelectedAttributes();
    if (indices.length == 0)
      return;

    keep    = m_CheckBoxKeepName.isSelected();
    oldName = cont.getData().relationName();
    remove  = new Remove();
    remove.setAttributeIndicesArray(indices);
    try {
      remove.setInputFormat(cont.getData());
      filtered = Filter.useFilter(cont.getData(), remove);
      if (keep)
	filtered.setRelationName(oldName);
      cont.setData(filtered);
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getSelectedRows()[0]));
      SwingUtilities.invokeLater(() -> {
	if (m_PanelAttSelection.getTable().getRowCount() > 0)
	  m_PanelAttSelection.getTable().setSelectedRow(0);
      });
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(this, "Failed to remove checked attributes!", e);
    }
  }
}
