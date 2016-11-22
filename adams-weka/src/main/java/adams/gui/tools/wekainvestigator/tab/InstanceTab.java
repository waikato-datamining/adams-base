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
 * InstanceTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.core.Range;
import adams.data.instance.Instance;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ParameterPanel;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SearchableBaseList;
import adams.gui.event.SearchEvent;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.visualization.core.PaintletWithMarkers;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstancePanel;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Visualizes the selected dataset like the instance explorer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = -4106630131554796889L;

  public static final String KEY_LEFTPANELWIDTH = "leftpanelwidth";

  public static final String KEY_DATASET = "dataset";

  public static final String KEY_ID = "id";

  public static final String KEY_RANGE = "range";

  public static final String KEY_ANTIALIASING = "antialiasing";

  public static final String KEY_MARKERS = "markers";

  public static final String KEY_IDS = "ids";

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the left panel. */
  protected JPanel m_PanelLeft;

  /** the right panel. */
  protected JPanel m_PanelRight;

  /** the parameter panel. */
  protected ParameterPanel m_PanelParameters;

  /** the attribute model. */
  protected DefaultComboBoxModel<String> m_ModelAttributes;

  /** the datasets model. */
  protected DefaultComboBoxModel<String> m_ModelDatasets;

  /** the datasets. */
  protected JComboBox<String> m_ComboBoxDatasets;

  /** the ID. */
  protected JComboBox<String> m_ComboBoxID;

  /** the attribute range. */
  protected JTextField m_TextAttributeRange;

  /** whether to use anti-aliasing. */
  protected JCheckBox m_CheckBoxAntiAliasing;

  /** whether to use markers. */
  protected JCheckBox m_CheckBoxMarkers;

  /** the model with the IDs. */
  protected DefaultListModel<Comparable> m_ModelIDs;

  /** the list with instance IDs to display. */
  protected SearchableBaseList m_ListIDs;

  /** the search panel for the IDs. */
  protected SearchPanel m_PanelSearchID;

  /** the button for visualizing. */
  protected JButton m_ButtonVisualize;

  /** the plot. */
  protected InstancePanel m_PanelInstance;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ModelDatasets   = new DefaultComboBoxModel<>();
    m_ModelAttributes = new DefaultComboBoxModel<>();
    m_ModelIDs        = new DefaultListModel<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties 	props;
    JPanel	panelOptions;
    JPanel	panelIDs;
    JPanel	panelButtons;

    super.initGUI();

    props = InvestigatorPanel.getProperties();

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(props.getInteger("Instance.LeftPanelWidth", 200));
    m_SplitPane.setOneTouchExpandable(true);
    m_ContentPanel.add(m_SplitPane, BorderLayout.CENTER);

    m_PanelLeft = new JPanel(new BorderLayout());
    m_PanelRight = new JPanel(new BorderLayout());
    m_SplitPane.setLeftComponent(m_PanelLeft);
    m_SplitPane.setRightComponent(m_PanelRight);

    panelOptions = new JPanel(new BorderLayout());
    m_PanelLeft.add(panelOptions, BorderLayout.NORTH);

    m_PanelParameters = new ParameterPanel();
    panelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    m_ComboBoxDatasets = new JComboBox<>(m_ModelDatasets);
    m_ComboBoxDatasets.addActionListener((ActionEvent e) -> updateAttributes());
    m_PanelParameters.addParameter("Dataset", m_ComboBoxDatasets);

    m_ComboBoxID = new JComboBox<>(m_ModelAttributes);
    m_ComboBoxID.addActionListener((ActionEvent e) -> updateIDs());
    m_PanelParameters.addParameter("ID", m_ComboBoxID);

    m_TextAttributeRange = new JTextField(20);
    m_TextAttributeRange.setText(Range.ALL);
    m_TextAttributeRange.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
    });
    m_PanelParameters.addParameter("Range", m_TextAttributeRange);

    m_CheckBoxAntiAliasing = new JCheckBox();
    m_CheckBoxAntiAliasing.setSelected(props.getBoolean("Instance.AntiAliasing", true));
    m_PanelParameters.addParameter("Use anti-aliasing", m_CheckBoxAntiAliasing);

    m_CheckBoxMarkers = new JCheckBox();
    m_CheckBoxMarkers.setSelected(props.getBoolean("Instance.Markers", true));
    m_PanelParameters.addParameter("Use makers", m_CheckBoxMarkers);

    // IDs
    panelIDs = new JPanel(new BorderLayout(5, 5));
    panelIDs.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelLeft.add(panelIDs, BorderLayout.CENTER);

    m_ListIDs = new SearchableBaseList(m_ModelIDs);

    m_PanelSearchID = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelSearchID.addSearchListener((SearchEvent e) ->
      m_ListIDs.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));

    panelIDs.add(new JLabel("Instances to display"), BorderLayout.NORTH);
    panelIDs.add(new BaseScrollPane(m_ListIDs), BorderLayout.CENTER);
    panelIDs.add(m_PanelSearchID, BorderLayout.SOUTH);

    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelLeft.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonVisualize = new JButton("Visualize");
    m_ButtonVisualize.setMnemonic('V');
    m_ButtonVisualize.addActionListener((ActionEvent e) -> visualize());
    panelButtons.add(m_ButtonVisualize);

    // the plot
    m_PanelInstance = new InstancePanel();
    m_PanelInstance.setZoomOverviewPanelVisible(true);
    m_PanelInstance.setSidePanelVisible(false);
    m_PanelRight.add(m_PanelInstance, BorderLayout.CENTER);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Instance";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "chart.gif";
  }

  /**
   * Determines the index of the old dataset name in the current dataset model.
   *
   * @param oldDataset	the old dataset to look for
   * @return		the index, -1 if not found
   */
  protected int indexOfDataset(String oldDataset) {
    int 		result;
    int			i;
    DataContainer data;

    result = -1;

    if (oldDataset != null)
      oldDataset = oldDataset.replaceAll("^[0-9]+: ", "");
    for (i = 0; i < getOwner().getData().size(); i++) {
      data = getOwner().getData().get(i);
      if ((oldDataset != null) && data.getData().relationName().equals(oldDataset)) {
        result = i;
        break;
      }
    }

    return result;
  }

  /**
   * Checks whether the data has changed and the model needs updating.
   *
   * @param newDatasets		the new list of datasets
   * @param currentModel	the current model
   * @return			true if changed
   */
  protected boolean hasDataChanged(List<String> newDatasets, ComboBoxModel<String> currentModel) {
    boolean	result;
    int		i;
    Set<String> setDatasets;
    Set<String>	setModel;
    int		index;

    setDatasets = new HashSet<>(newDatasets);
    setModel    = new HashSet<>();
    for (i = 0; i < currentModel.getSize(); i++)
      setModel.add(currentModel.getElementAt(i));

    result = (setDatasets.size() != setModel.size())
      || !(setDatasets.containsAll(setModel) && setModel.containsAll(setDatasets));

    if (!result) {
      index = indexOfDataset((String) m_ComboBoxDatasets.getSelectedItem());
      if (index > -1) {
	if (getData().get(index).getData().numAttributes() != m_ComboBoxID.getModel().getSize() - 1)
	  result = true;
      }
    }

    return result;
  }

  /**
   * Generates the list of datasets for a combobox.
   *
   * @return		the list
   */
  protected List<String> generateDatasetList() {
    List<String> 	result;
    int			i;
    DataContainer 	data;

    result = new ArrayList<>();
    for (i = 0; i < getOwner().getData().size(); i++) {
      data = getOwner().getData().get(i);
      result.add((i + 1) + ": " + data.getData().relationName());
    }

    return result;
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  @Override
  public void dataChanged(WekaInvestigatorDataEvent e) {
    List<String>	datasets;
    int			index;

    if (e.getType() == WekaInvestigatorDataEvent.ROW_ACTIVATED) {
      m_ComboBoxDatasets.setSelectedIndex(e.getRows()[0]);
      return;
    }

    datasets = generateDatasetList();
    index    = indexOfDataset((String) m_ComboBoxDatasets.getSelectedItem());
    if (hasDataChanged(datasets, m_ModelDatasets)) {
      m_ModelIDs.clear();
      m_ModelAttributes.removeAllElements();
      m_PanelInstance.getSequenceManager().clear();
      m_ModelDatasets = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
      m_ComboBoxDatasets.setModel(m_ModelDatasets);
      if ((index == -1) && (m_ModelDatasets.getSize() > 0))
	m_ComboBoxDatasets.setSelectedIndex(0);
      else if (index > -1)
	m_ComboBoxDatasets.setSelectedIndex(index);
    }
    updateButtons();
  }

  /**
   * Updates the attributes model.
   */
  protected void updateAttributes() {
    String		oldID;
    int			indexID;
    List<String>	atts;
    Instances		data;
    int			i;

    m_ModelAttributes.removeAllElements();
    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return;
    if (m_ComboBoxDatasets.getSelectedIndex() > getData().size() - 1)
      return;

    oldID = (String) m_ComboBoxID.getSelectedItem();

    data = getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    atts = new ArrayList<>();
    atts.add("-none-");
    for (i = 0; i < data.numAttributes(); i++)
      atts.add((i+1) + ": " + data.attribute(i).name());
    m_ModelAttributes = new DefaultComboBoxModel<>(atts.toArray(new String[atts.size()]));
    indexID = m_ModelAttributes.getIndexOf(oldID);

    m_ComboBoxID.setModel(m_ModelAttributes);
    if (indexID == -1)
      m_ComboBoxID.setSelectedIndex(0);
    else
      m_ComboBoxID.setSelectedIndex(indexID);
  }

  /**
   * Updates the list with IDs.
   */
  protected void updateIDs() {
    Instances		data;
    List<Comparable>	ids;
    int			indexID;
    int			i;
    weka.core.Instance	inst;

    m_ModelIDs.clear();
    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return;
    if (m_ComboBoxDatasets.getSelectedIndex() > getData().size() - 1)
      return;

    data    = getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    ids     = new ArrayList<>();
    indexID = m_ComboBoxID.getSelectedIndex() - 1;
    for (i = 0; i < data.numInstances(); i++) {
      if (indexID > -1) {
	inst = data.instance(i);
	if (inst.attribute(indexID).isNumeric())
	  ids.add(inst.value(indexID));
	else
	  ids.add(inst.stringValue(indexID));
      }
      else {
	ids.add((i+1));
      }
    }

    if (indexID > -1)
      Collections.sort(ids);

    m_ModelIDs = new DefaultListModel<>();
    for (Comparable c: ids)
      m_ModelIDs.addElement(c);

    m_ListIDs.setModel(m_ModelIDs);
  }

  /**
   * Returns whether data can be visualized.
   *
   * @return		null if can visualize, otherwise error message
   */
  protected String canVisualize() {
    String	rangeStr;
    Instances	data;

    if (m_ComboBoxDatasets.getSelectedIndex() > -1)
      data = getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    else
      data = null;
    if (data == null)
      return "No data available!";

    rangeStr = m_TextAttributeRange.getText();
    if (rangeStr.isEmpty())
      return "No attribute range defined!";
    if (!Range.isValid(rangeStr, data.numAttributes()))
      return "Invalid attribute range!";

    return null;
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    String	msg;

    msg = canVisualize();
    m_ButtonVisualize.setEnabled(msg == null);
    m_ButtonVisualize.setToolTipText(msg);
  }

  /**
   * Updates the visualization.
   */
  protected void visualize() {
    Range 			range;
    Instances			data;
    int				i;
    InstanceContainerManager	manager;
    InstanceContainer		cont;
    weka.core.Instance		winst;
    Instance			ainst;
    int				indexID;
    TIntList			additional;
    HashSet<Integer>		attTypes;
    HashSet<Comparable>		ids;
    int[]			sel;
    boolean 			numericID;
    Comparable			id;

    if (m_ComboBoxDatasets.getSelectedIndex() == -1)
      return;

    data     = getData().get(m_ComboBoxDatasets.getSelectedIndex()).getData();
    range    = new Range(m_TextAttributeRange.getText());
    indexID  = m_ComboBoxID.getSelectedIndex() - 1;
    attTypes = new HashSet<>();
    attTypes.add(Attribute.NUMERIC);
    numericID  = false;
    additional = new TIntArrayList();
    if (indexID > -1) {
      additional.add(indexID);
      numericID = data.attribute(indexID).isNumeric();
    }
    ids = null;
    sel = m_ListIDs.getSelectedIndices();
    if (sel.length > 0) {
      ids = new HashSet<>();
      for (int s: sel)
	ids.add(m_ModelIDs.get(s));
    }

    manager = m_PanelInstance.getContainerManager();
    manager.clear();

    m_PanelInstance.setAntiAliasingEnabled(m_CheckBoxAntiAliasing.isSelected());
    if (m_PanelInstance.getDataPaintlet() instanceof PaintletWithMarkers)
      ((PaintletWithMarkers) m_PanelInstance.getDataPaintlet()).setMarkersDisabled(!m_CheckBoxMarkers.isSelected());

    manager.startUpdate();
    for (i = 0; i < data.numInstances(); i++) {
      winst = data.instance(i);

      // ID selected?
      if (ids != null) {
	if (indexID > -1) {
	  if (numericID)
	    id = winst.value(indexID);
	  else
	    id = winst.stringValue(indexID);
	  if (!ids.contains(id))
	    continue;
	}
	else {
	  if (!ids.contains(i+1))
	    continue;
	}
      }

      ainst = new Instance();
      ainst.set(winst, i, additional.toArray(), range, attTypes);
      cont = manager.newContainer(ainst);
      if (indexID > -1) {
	if (numericID)
	  cont.setID("" + winst.value(indexID));
	else
	  cont.setID(winst.stringValue(indexID));
      }
      manager.add(cont);
    }
    manager.finishUpdate();
  }

  /**
   * Returns the objects for serialization.
   *
   * @return		the mapping of the objects to serialize
   */
  protected Map<String,Object> doSerialize() {
    Map<String,Object>	result;

    result = super.doSerialize();
    result.put(KEY_LEFTPANELWIDTH, m_SplitPane.getDividerLocation());
    result.put(KEY_DATASET, m_ComboBoxDatasets.getSelectedIndex());
    result.put(KEY_ID, m_ComboBoxID.getSelectedIndex());
    result.put(KEY_RANGE, m_TextAttributeRange.getText());
    result.put(KEY_ANTIALIASING, m_CheckBoxAntiAliasing.isSelected());
    result.put(KEY_MARKERS, m_CheckBoxMarkers.isSelected());
    result.put(KEY_IDS, m_ListIDs.getSelectedIndices());

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_LEFTPANELWIDTH))
      m_SplitPane.setDividerLocation((int) data.get(KEY_LEFTPANELWIDTH));
    if (data.containsKey(KEY_DATASET))
      m_ComboBoxDatasets.setSelectedIndex((int) data.get(KEY_DATASET));
    if (data.containsKey(KEY_ID))
      m_ComboBoxID.setSelectedIndex((int) data.get(KEY_ID));
    if (data.containsKey(KEY_RANGE))
      m_TextAttributeRange.setText((String) data.get(KEY_RANGE));
    if (data.containsKey(KEY_ANTIALIASING))
      m_CheckBoxAntiAliasing.setSelected((Boolean) data.get(KEY_ANTIALIASING));
    if (data.containsKey(KEY_MARKERS))
      m_CheckBoxMarkers.setSelected((Boolean) data.get(KEY_MARKERS));
    if (data.containsKey(KEY_IDS))
      m_ListIDs.setSelectedIndices((int[]) data.get(KEY_IDS));
  }
}
