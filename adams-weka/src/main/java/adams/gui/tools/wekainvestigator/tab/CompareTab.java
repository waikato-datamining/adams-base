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
 * CompareTab.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.MessageCollection;
import adams.core.Range;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.data.instance.InstancePoint;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.statistics.StatUtils;
import adams.data.weka.WekaAttributeRange;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseList;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTextArea;
import adams.gui.core.BaseTextField;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.evaluation.DatasetHelper;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstancePanel;
import adams.gui.visualization.report.ReportFactory;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.Remove;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * For comparing datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompareTab
  extends AbstractInvestigatorTab {

  private static final long serialVersionUID = 6828735730385124766L;

  public static final String KEY_FIRST_DATASET = "dataset1";

  public static final String KEY_SECOND_DATASET = "dataset2";

  public static final String KEY_FIRST_ATTRANGE = "attrange1";

  public static final String KEY_SECOND_ATTRANGE = "attrange2";

  /** the first dataset. */
  protected BaseComboBox<String> m_ComboBoxFirstDataset;

  /** the first dataset model. */
  protected DefaultComboBoxModel<String> m_ModelFirstDataset;

  /** the second dataset. */
  protected BaseComboBox<String> m_ComboBoxSecondDataset;

  /** the second dataset model. */
  protected DefaultComboBoxModel<String> m_ModelSecondDataset;

  /** the panel for the datasets. */
  protected JPanel m_PanelDatasets;

  /** the text field for the first attribute range. */
  protected BaseTextField m_TextFirstRange;

  /** the text field for the second attribute range. */
  protected BaseTextField m_TextSecondRange;

  /** the panel for the structure. */
  protected JPanel m_PanelStructure;

  /** the button for comparing the structure. */
  protected BaseButton m_ButtonStructure;

  /** the overall panel for comparison. */
  protected JPanel m_PanelComparison;

  /** the text area with the structure output. */
  protected BaseTextArea m_TextStructure;

  /** the panel for the data. */
  protected JPanel m_PanelData;

  /** the ID attribute in the first dataset to use for comparing the data. */
  protected BaseComboBox<String> m_ComboBoxFirstID;

  /** the first ID attribute model. */
  protected DefaultComboBoxModel<String> m_ModelFirstID;

  /** the ID attribute in the second dataset to use for comparing the data. */
  protected BaseComboBox<String> m_ComboBoxSecondID;

  /** the second ID attribute model. */
  protected DefaultComboBoxModel<String> m_ModelSecondID;

  /** the button for comparing the data. */
  protected BaseButton m_ButtonData;

  /** the tabbed pane for the data. */
  protected BaseTabbedPane m_TabbedPaneData;
  
  /** the IDs only present in the first dataset. */
  protected BaseTextArea m_TextOnlyFirst;

  /** the IDs present in both datasets. */
  protected BaseTextArea m_TextCommon;

  /** the IDs only present in the second dataset. */
  protected BaseTextArea m_TextOnlySecond;

  /** the model with the common IDs. */
  protected DefaultListModel<String> m_ModelCommonIDs;

  /** the JList with the common IDs. */
  protected BaseList m_ListCommonIDs;

  /** the panel for displaying the two instances. */
  protected InstancePanel m_PanelBoth;

  /** the panel with the difference. */
  protected InstancePanel m_PanelDifference;

  /** the table with the report. */
  protected ReportFactory.Table m_ReportTable;

  /** the first dataset. */
  protected Instances m_FirstData;

  /** the first set of attributes. */
  protected TIntList m_FirstAttributes;

  /** the second dataset. */
  protected Instances m_SecondData;

  /** the second set of attributes. */
  protected TIntList m_SecondAttributes;

  /** the file saver for storing the IDs. */
  protected TextFileChooser m_FileChooserIDs;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ModelFirstDataset  = new DefaultComboBoxModel<>();
    m_ModelSecondDataset = new DefaultComboBoxModel<>();
    m_FirstAttributes    = new TIntArrayList();
    m_SecondAttributes   = new TIntArrayList();
    m_FileChooserIDs     = new TextFileChooser();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JLabel		label;
    JPanel		panel;
    JPanel		panel2;
    BaseSplitPane	splitPane;

    super.initGUI();
    
    setLayout(new BorderLayout());

    // datasets
    m_PanelDatasets = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelDatasets, BorderLayout.NORTH);

    m_ComboBoxFirstDataset  = new BaseComboBox<>(m_ModelFirstDataset);
    m_ComboBoxFirstDataset.setPreferredSize(new Dimension(150, 25));
    label = new JLabel("First");
    label.setDisplayedMnemonic('F');
    label.setLabelFor(m_ComboBoxFirstDataset);
    m_PanelDatasets.add(label);
    m_PanelDatasets.add(m_ComboBoxFirstDataset);

    m_TextFirstRange = new BaseTextField(10);
    m_TextFirstRange.setText("");
    m_TextFirstRange.setToolTipText("The attribute range to consider");
    m_TextFirstRange.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateWidgets();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateWidgets();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateWidgets();
      }
    });
    m_PanelDatasets.add(m_TextFirstRange);

    m_ComboBoxSecondDataset = new BaseComboBox<>(m_ModelSecondDataset);
    m_ComboBoxSecondDataset.setPreferredSize(new Dimension(150, 25));
    label = new JLabel("Second");
    label.setDisplayedMnemonic('S');
    label.setLabelFor(m_ComboBoxSecondDataset);
    m_PanelDatasets.add(label);
    m_PanelDatasets.add(m_ComboBoxSecondDataset);

    m_TextSecondRange = new BaseTextField(10);
    m_TextSecondRange.setText("");
    m_TextSecondRange.setToolTipText("The attribute range to consider");
    m_TextSecondRange.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateWidgets();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateWidgets();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateWidgets();
      }
    });
    m_PanelDatasets.add(m_TextSecondRange);

    m_ButtonStructure = new BaseButton("Compare");
    m_ButtonStructure.addActionListener((ActionEvent e) -> compareStructure());
    m_PanelDatasets.add(m_ButtonStructure);

    m_PanelComparison = new JPanel(new BorderLayout());
    add(m_PanelComparison, BorderLayout.CENTER);

    // Structure
    m_PanelStructure = new JPanel(new BorderLayout());
    m_PanelStructure.setBorder(BorderFactory.createTitledBorder("Structure"));
    m_TextStructure = new BaseTextArea(3, 40);
    m_TextStructure.setEditable(false);
    m_TextStructure.setTextFont(Fonts.getMonospacedFont());
    m_PanelStructure.add(new BaseScrollPane(m_TextStructure), BorderLayout.CENTER);
    m_PanelComparison.add(m_PanelStructure, BorderLayout.NORTH);

    // Data
    m_PanelData = new JPanel(new BorderLayout());
    m_PanelData.setBorder(BorderFactory.createTitledBorder("Data"));
    m_PanelComparison.add(m_PanelData, BorderLayout.CENTER);

    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelData.add(panel, BorderLayout.NORTH);

    m_ModelFirstID    = new DefaultComboBoxModel<>();
    m_ComboBoxFirstID = new BaseComboBox<>(m_ModelFirstID);
    m_ComboBoxFirstID.setPreferredSize(new Dimension(150, 25));
    label = new JLabel("First");
    label.setLabelFor(m_ComboBoxFirstID);
    panel.add(label);
    panel.add(m_ComboBoxFirstID);

    m_ModelSecondID    = new DefaultComboBoxModel<>();
    m_ComboBoxSecondID = new BaseComboBox<>(m_ModelSecondID);
    m_ComboBoxSecondID .setPreferredSize(new Dimension(150, 25));
    label = new JLabel("Second");
    label.setLabelFor(m_ComboBoxSecondID);
    panel.add(label);
    panel.add(m_ComboBoxSecondID);
    
    m_ButtonData = new BaseButton("Compare");
    m_ButtonData.addActionListener((ActionEvent e) -> compareData());
    panel.add(m_ButtonData);

    // tabbed pane
    m_TabbedPaneData = new BaseTabbedPane();
    m_PanelData.add(m_TabbedPaneData, BorderLayout.CENTER);

    // IDs
    panel = new JPanel(new GridLayout(1, 3));
    m_TabbedPaneData.addTab("IDs", panel);
    
    m_TextOnlyFirst = new BaseTextArea(40, 20);
    m_TextOnlyFirst.setEditable(false);
    m_TextOnlyFirst.setTextFont(Fonts.getMonospacedFont());
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createTitledBorder("Only first"));
    panel2.add(new BaseScrollPane(m_TextOnlyFirst), BorderLayout.CENTER);
    panel2.add(createIDInfoPanel(m_TextOnlyFirst), BorderLayout.SOUTH);
    panel.add(panel2);
    
    m_TextCommon = new BaseTextArea(40, 20);
    m_TextCommon.setEditable(false);
    m_TextCommon.setTextFont(Fonts.getMonospacedFont());
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createTitledBorder("Common"));
    panel2.add(new BaseScrollPane(m_TextCommon), BorderLayout.CENTER);
    panel2.add(createIDInfoPanel(m_TextCommon), BorderLayout.SOUTH);
    panel.add(panel2);
    
    m_TextOnlySecond = new BaseTextArea(40, 20);
    m_TextOnlySecond.setEditable(false);
    m_TextOnlySecond.setTextFont(Fonts.getMonospacedFont());
    panel2 = new JPanel(new BorderLayout());
    panel2.setBorder(BorderFactory.createTitledBorder("Only second"));
    panel2.add(new BaseScrollPane(m_TextOnlySecond), BorderLayout.CENTER);
    panel2.add(createIDInfoPanel(m_TextOnlySecond), BorderLayout.SOUTH);
    panel.add(panel2);

    // Values
    splitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(1.0);
    splitPane.setDividerLocation(600);
    splitPane.setUISettingsParameters(getClass(), "Divider");
    m_TabbedPaneData.addTab("Values", splitPane);

    panel2 = new JPanel(new GridLayout(2, 1));
    splitPane.setLeftComponent(panel2);

    m_PanelBoth = new InstancePanel("Compare");
    m_PanelBoth.setSidePanelVisible(false);
    m_PanelBoth.getPlot().getAxis(Axis.BOTTOM).setAxisName("Attribute index (in selected range)");
    m_PanelDifference = new InstancePanel("Difference");
    m_PanelDifference.setSidePanelVisible(false);
    m_PanelDifference.getPlot().getAxis(Axis.BOTTOM).setAxisName("Attribute index (in selected range)");
    panel2.add(m_PanelBoth);
    panel2.add(m_PanelDifference);

    panel2 = new JPanel(new GridLayout(2, 1));
    splitPane.setRightComponent(panel2);

    m_ModelCommonIDs = new DefaultListModel<>();
    m_ListCommonIDs  = new BaseList(m_ModelCommonIDs);
    m_ListCommonIDs.addListSelectionListener((ListSelectionEvent e) -> compareInstances());
    panel2.add(new BaseScrollPane(m_ListCommonIDs));

    m_ReportTable = ReportFactory.getTable(null);
    m_ReportTable.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_ReportTable.setNumDecimals(3);
    panel2.add(new BaseScrollPane(m_ReportTable));
  }

  /**
   * Generates an info panel for an ID text area.
   *
   * @param textArea	the area for which to generate the panel
   * @return		the generated panel
   */
  protected JPanel createIDInfoPanel(final BaseTextArea textArea) {
    JPanel		result;
    JPanel		panelButtons;
    final JLabel	labelNumItems;
    final BaseButton	buttonCopy;
    final BaseButton	buttonSave;

    result = new JPanel(new BorderLayout());
    labelNumItems = new JLabel("0 IDs");
    result.add(labelNumItems, BorderLayout.WEST);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    result.add(panelButtons, BorderLayout.EAST);
    buttonCopy = new BaseButton(GUIHelper.getIcon("copy.gif"));
    buttonCopy.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(textArea.getText()));
    panelButtons.add(buttonCopy);
    buttonSave = new BaseButton(GUIHelper.getIcon("save.gif"));
    buttonSave.addActionListener((ActionEvent e) -> {
      int retVal = m_FileChooserIDs.showSaveDialog(getOwner());
      if (retVal != TextFileChooser.APPROVE_OPTION)
        return;
      String msg = FileUtils.writeToFileMsg(
        m_FileChooserIDs.getSelectedFile().getAbsolutePath(), textArea.getText(), false, null);
      if (msg != null)
        GUIHelper.showErrorMessage(getOwner(), "Failed to write IDs to:\n" + m_FileChooserIDs.getSelectedFile() + "\n" + msg);
      else
        logMessage("IDs written to: " + m_FileChooserIDs.getSelectedFile());
    });
    panelButtons.add(buttonSave);

    textArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	update();
      }
      protected void update() {
        String text = textArea.getText().trim();
        int lines = 0;
        if (text.length() > 0)
          lines = text.split("\n").length;
        labelNumItems.setText(lines + " IDs");
        buttonCopy.setEnabled(lines > 0);
        buttonSave.setEnabled(lines > 0);
      }
    });

    return result;
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    m_TextFirstRange.setText(Range.ALL);
    m_TextSecondRange.setText(Range.ALL);
    m_TextOnlyFirst.setText("");
    m_TextCommon.setText("");
    m_TextOnlySecond.setText("");
    updateWidgets();
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Compare";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "diff.png";
  }

  /**
   * Returns the dataset structure.
   *
   * @param data	the dataset
   * @param rangeStr	the range string
   * @return		the structure
   */
  protected Instances getStructure(Instances data, String rangeStr, TIntList indices) {
    Instances		result;
    WekaAttributeRange	range;
    Remove 		remove;

    result = new Instances(data, 0);
    if ((rangeStr == null) || rangeStr.isEmpty())
      rangeStr = WekaAttributeRange.ALL;
    range  = new WekaAttributeRange(rangeStr);
    range.setData(data);
    indices.clear();
    indices.addAll(range.getIntIndices());
    if (indices.size() == 0)
      return result;
    remove = new Remove();
    remove.setAttributeIndicesArray(indices.toArray());
    remove.setInvertSelection(true);
    try {
      remove.setInputFormat(result);
      result = remove.getOutputFormat();
    }
    catch (Exception e) {
      logError("Failed to filter instances structure, using: " + rangeStr, e, "Structure filter failure");
    }
    return result;
  }

  /**
   * Compares the structure.
   */
  protected void compareStructure() {
    String	structure;
    Instances	firstStructure;
    Instances	secondStructure;
    String	oldFirst;
    String	oldSecond;
    boolean	match;

    m_FirstData     = getOwner().getData().get(m_ComboBoxFirstDataset.getSelectedIndex()).getData();
    m_SecondData    = getOwner().getData().get(m_ComboBoxSecondDataset.getSelectedIndex()).getData();
    firstStructure  = getStructure(m_FirstData, m_TextFirstRange.getText(), m_FirstAttributes);
    secondStructure = getStructure(m_SecondData, m_TextSecondRange.getText(), m_SecondAttributes);

    structure = firstStructure.equalHeadersMsg(secondStructure);
    if (structure == null)
      structure = "Same structure";
    m_TextStructure.setText(structure);

    // data comboboxes
    oldFirst = m_ComboBoxFirstDataset.getSelectedItem();
    m_ModelFirstID.removeAllElements();
    match = false;
    for (int i: m_FirstAttributes.toArray()) {
      m_ModelFirstID.addElement(m_FirstData.attribute(i).name());
      if ((oldFirst != null) && m_FirstData.attribute(i).name().equals(oldFirst))
        match = true;
    }
    if (match)
      m_ComboBoxFirstID.setSelectedItem(oldFirst);
    
    oldSecond = m_ComboBoxSecondDataset.getSelectedItem();
    m_ModelSecondID.removeAllElements();
    match = false;
    for (int i: m_SecondAttributes.toArray()) {
      m_ModelSecondID.addElement(m_SecondData.attribute(i).name());
      if ((oldSecond != null) && m_SecondData.attribute(i).name().equals(oldSecond))
        match = true;
    }
    if (match)
      m_ComboBoxSecondID.setSelectedItem(oldSecond);

    // ID text areas
    m_TextOnlyFirst.setText("");
    m_TextCommon.setText("");
    m_TextOnlySecond.setText("");

    updateWidgets();
  }

  /**
   * Retrieves the IDs from the dataset.
   *
   * @param dataset	the dataset to use
   * @param index	the attribute index
   * @return		the generated list
   */
  protected List<String> getIDs(Instances dataset, int index) {
    List<String>	result;
    int			i;
    boolean		nominal;

    result = new ArrayList<>();
    nominal = dataset.attribute(index).isNominal() || dataset.attribute(index).isString();
    for (i = 0; i < dataset.numInstances(); i++) {
      if (nominal)
        result.add(dataset.instance(i).stringValue(index));
      else
        result.add("" + dataset.instance(i).value(index));
    }

    return result;
  }

  /**
   * Returns the instance from the dataset with the specified ID.
   *
   * @param dataset	the dataset to get the instance from
   * @param index	the ID attribute index
   * @param id		the ID
   * @param attributes 	the attribute indices to use
   * @return		the instance, null if not found
   */
  protected adams.data.instance.Instance getInstance(Instances dataset, int index, String id, TIntList attributes) {
    adams.data.instance.Instance	result;
    Instance 				inst;
    int					i;
    int					instIndex;
    boolean				nominal;
    Range				range;
    TIntList				actualAtts;

    result    = null;
    inst      = null;
    instIndex = -1;
    nominal = dataset.attribute(index).isNominal() || dataset.attribute(index).isString();
    for (i = 0; i < dataset.numInstances(); i++) {
      if (nominal) {
        if (id.equals(dataset.instance(i).stringValue(index))) {
          inst      = dataset.instance(i);
          instIndex = i;
          break;
	}
      }
      else {
        if (id.equals("" + dataset.instance(i).value(index))) {
          inst      = dataset.instance(i);
          instIndex = i;
          break;
	}
      }
    }

    if (inst != null) {
      actualAtts = new TIntArrayList(attributes);
      actualAtts.remove(index);
      range = new Range();
      range.setIndices(actualAtts.toArray());
      result = new adams.data.instance.Instance();
      result.set(inst, instIndex, new int[]{index}, range, null);
      result.setID(id);
    }

    return result;
  }

  /**
   * Performs the comparison between the rows from the two datasets.
   */
  protected void compareInstances() {
    adams.data.instance.Instance 	inst1;
    adams.data.instance.Instance	inst2;
    adams.data.instance.Instance	instDiff;
    InstancePoint 			point1;
    InstancePoint			point2;
    InstancePoint			pointDiff;
    Iterator<InstancePoint> 		iter1;
    Iterator<InstancePoint>		iter2;
    InstanceContainer 			cont;
    InstanceContainerManager 		managerComp;
    InstanceContainerManager		managerDiff;
    Report 				reportDiff;
    Field 				field;
    double[]				values1;
    double[]				values2;
    String				id;

    managerComp = m_PanelBoth.getContainerManager();
    managerDiff = m_PanelDifference.getContainerManager();

    managerComp.clear();
    managerDiff.clear();
    m_ReportTable.setModel(ReportFactory.getModel(null));
    m_ReportTable.setOptimalColumnWidth();

    if (m_ListCommonIDs.getSelectedIndex() < 1)
      return;

    id    = "" + m_ListCommonIDs.getSelectedValue();
    inst1 = getInstance(m_FirstData,  m_ComboBoxFirstID.getSelectedIndex(), id, m_FirstAttributes);
    inst2 = getInstance(m_SecondData, m_ComboBoxSecondID.getSelectedIndex(), id, m_SecondAttributes);

    // comparison
    managerComp.startUpdate();
    if (inst1 != null) {
      cont = managerComp.newContainer(inst1);
      managerComp.add(cont);
    }
    if (inst2 != null) {
      cont = managerComp.newContainer(inst2);
      managerComp.add(cont);
    }
    managerComp.finishUpdate();

    // difference
    if ((inst1 == null) || (inst2 == null)) {
      showStatus("Instance only available in " + (inst1 != null ? "first" : "second") + " dataset!");
    }
    else if (inst1.size() != inst2.size()) {
      showStatus("Instances differ in size: " + inst1.size() + " != " + inst2.size());
    }
    else {
      showStatus("");

      // create instance
      instDiff = new adams.data.instance.Instance();
      iter1    = inst1.iterator();
      iter2    = inst2.iterator();
      while (iter1.hasNext() && iter2.hasNext()) {
	point1    = iter1.next();
	point2    = iter2.next();
	pointDiff = new InstancePoint(point1.getX(), (double) (point1.getY() - point2.getY()));
	instDiff.add(pointDiff);
      }

      // extend report
      reportDiff = instDiff.getReport();
      values1    = inst1.toDoubleArray();
      values2    = inst2.toDoubleArray();
      // 1. correlation coefficient
      field = new Field("Correlation coefficient", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.correlationCoefficient(values1, values2));
      // 2. RMSE
      field = new Field("Root mean squared error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.rmse(values1, values2));
      // 3. RRSE
      field = new Field("Root relative squared error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.rrse(values1, values2));
      // 4. MAE
      field = new Field("Mean absolute error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.mae(values1, values2));
      // 5. RAE
      field = new Field("Relative absolute error", DataType.NUMERIC);
      reportDiff.addField(field);
      reportDiff.setValue(field, StatUtils.rae(values1, values2));
      // TODO: further statistics, etc.

      managerDiff.startUpdate();
      cont = managerComp.newContainer(instDiff);
      cont.setID((String) m_ListCommonIDs.getSelectedValue());
      managerDiff.add(cont);
      managerDiff.finishUpdate();
      m_ReportTable.setModel(ReportFactory.getModel(reportDiff));
      m_ReportTable.setOptimalColumnWidth();
    }
  }

  /**
   * Compares the IDs.
   */
  protected void compareIDs() {
    Set<String> 	setFirst;
    Set<String> 	setSecond;
    Set<String>		setCommon;
    List<String>	onlyFirst;
    List<String>	common;
    List<String>	onlySecond;

    setFirst  = new HashSet<>(getIDs(m_FirstData,  m_ComboBoxFirstID.getSelectedIndex()));
    setSecond = new HashSet<>(getIDs(m_SecondData, m_ComboBoxSecondID.getSelectedIndex()));
    setCommon = new HashSet<>(setFirst);
    setCommon.retainAll(setSecond);
    setFirst.removeAll(setCommon);
    setSecond.removeAll(setCommon);

    onlyFirst  = new ArrayList<>(setFirst);
    common     = new ArrayList<>(setCommon);
    onlySecond = new ArrayList<>(setSecond);
    Collections.sort(onlyFirst);
    Collections.sort(common);
    Collections.sort(onlySecond);

    m_TextOnlyFirst.setText(Utils.flatten(onlyFirst, "\n"));
    m_TextCommon.setText(Utils.flatten(common, "\n"));
    m_TextOnlySecond.setText(Utils.flatten(onlySecond, "\n"));
    m_TextOnlyFirst.setCaretPosition(0);
    m_TextCommon.setCaretPosition(0);
    m_TextOnlySecond.setCaretPosition(0);

    m_ModelCommonIDs.removeAllElements();
    for (String com: common)
      m_ModelCommonIDs.addElement(com);
  }

  /**
   * Compares the data
   */
  protected void compareData() {
    compareIDs();
    compareInstances();
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  @Override
  public void dataChanged(WekaInvestigatorDataEvent e) {
    List<String> 	datasets;
    int 		indexFirst;
    int 		indexSecond;
    boolean		changed;

    datasets   = DatasetHelper.generateDatasetList(getOwner().getData());
    changed    = false;
    indexFirst = DatasetHelper.indexOfDataset(getOwner().getData(), m_ComboBoxFirstDataset.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelFirstDataset)) {
      m_ModelFirstDataset = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
      m_ComboBoxFirstDataset.setModel(m_ModelFirstDataset);
      if ((indexFirst == -1) && (m_ModelFirstDataset.getSize() > 0)) {
	m_ComboBoxFirstDataset.setSelectedIndex(0);
	changed = true;
      }
      else if (indexFirst > -1) {
	m_ComboBoxFirstDataset.setSelectedIndex(indexFirst);
      }
    }
    indexSecond = DatasetHelper.indexOfDataset(getOwner().getData(), m_ComboBoxSecondDataset.getSelectedItem());
    if (DatasetHelper.hasDataChanged(datasets, m_ModelSecondDataset)) {
      m_ModelSecondDataset = new DefaultComboBoxModel<>(datasets.toArray(new String[datasets.size()]));
      m_ComboBoxSecondDataset.setModel(m_ModelSecondDataset);
      if ((indexSecond == -1) && (m_ModelSecondDataset.getSize() > 0)) {
	m_ComboBoxSecondDataset.setSelectedIndex(0);
	changed = true;
      }
      else if (indexSecond > -1) {
	m_ComboBoxSecondDataset.setSelectedIndex(indexSecond);
      }
    }

    if (changed)
      clear();
    updateWidgets();
  }

  /**
   * Clears the content.
   */
  protected void clear() {
    m_TextStructure.setText("");

    m_FirstData  = null;
    m_FirstAttributes.clear();

    m_SecondData = null;
    m_SecondAttributes.clear();

    m_ModelFirstID.removeAllElements();
    m_ModelSecondID.removeAllElements();

    m_TextOnlyFirst.setText("");
    m_TextCommon.setText("");
    m_TextOnlySecond.setText("");

    m_ModelCommonIDs.removeAllElements();
  }

  /**
   * Updates the state of the widgets.
   */
  protected  void updateWidgets() {
    boolean	hasData;
    boolean	hasIDs;

    hasData = (m_ComboBoxFirstDataset.getSelectedIndex() > -1)
      && (m_ComboBoxSecondDataset.getSelectedIndex() > -1);

    m_ButtonStructure.setEnabled(hasData);
    m_TextStructure.setEnabled(hasData);

    hasIDs = hasData
      && (m_ComboBoxFirstID.getSelectedIndex() > -1)
      && (m_ComboBoxSecondID.getSelectedIndex() > -1);
    m_ButtonData.setEnabled(hasIDs);
    m_TextOnlyFirst.setEnabled(hasIDs);
    m_TextCommon.setEnabled(hasIDs);
    m_TextOnlySecond.setEnabled(hasIDs);
  }

  /**
   * Returns the objects for serialization.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  @Override
  protected Map<String,Object> doSerialize(Set<SerializationOption> options) {
    Map<String,Object>	result;

    result = super.doSerialize(options);
    if (options.contains(SerializationOption.GUI)) {
      result.put(KEY_FIRST_DATASET, m_ComboBoxFirstDataset.getSelectedIndex());
      result.put(KEY_SECOND_DATASET, m_ComboBoxSecondDataset.getSelectedIndex());
    }
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_FIRST_ATTRANGE, m_TextFirstRange.getText());
      result.put(KEY_SECOND_ATTRANGE, m_TextSecondRange.getText());
    }

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  @Override
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_FIRST_DATASET))
      m_ComboBoxFirstDataset.setSelectedIndex((int) data.get(KEY_FIRST_DATASET));
    if (data.containsKey(KEY_SECOND_DATASET))
      m_ComboBoxSecondDataset.setSelectedIndex((int) data.get(KEY_SECOND_DATASET));
    if (data.containsKey(KEY_FIRST_ATTRANGE))
      m_TextFirstRange.setText((String) data.get(KEY_FIRST_ATTRANGE));
    if (data.containsKey(KEY_SECOND_ATTRANGE))
      m_TextSecondRange.setText((String) data.get(KEY_SECOND_ATTRANGE));
  }
}
