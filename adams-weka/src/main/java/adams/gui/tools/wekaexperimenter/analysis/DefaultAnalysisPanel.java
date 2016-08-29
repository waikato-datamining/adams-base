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
 * DefaultAnalysisPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaexperimenter.analysis;

import adams.core.ClassLister;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.NumberTextField;
import adams.gui.core.NumberTextField.BoundedNumberCheckModel;
import adams.gui.core.NumberTextField.Type;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Range;
import weka.experiment.PairedCorrectedTTester;
import weka.experiment.ResultMatrix;
import weka.experiment.ResultMatrixPlainText;
import weka.experiment.Tester;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Default panel for analyzing results from experiments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultAnalysisPanel
  extends AbstractAnalysisPanel {

  /** for serialization. */
  private static final long serialVersionUID = 7850777725811230009L;

  public static final String PERCENT_CORRECT = "Percent_correct";

  public static final String CORRELATION_COEFFICIENT = "Correlation_coefficient";

  public static final String KEY_RUN = "Key_Run";

  public static final String KEY_FOLD = "Key_Fold";

  public static final String KEY_DATASET = "Key_Dataset";

  public static final String KEY_SCHEME = "Key_Scheme";

  public static final String KEY_SCHEME_OPTIONS = "Key_Scheme_options";

  public static final String KEY_SCHEME_VERSION_ID = "Key_Scheme_version_ID";

  /** the GOE for the tester. */
  protected WekaGenericObjectEditorPanel m_PanelTester;

  /** the GOE for the result matrix. */
  protected WekaGenericObjectEditorPanel m_PanelMatrix;

  /** the significance. */
  protected NumberTextField m_TextSignificance;

  /** the combobox with the metric to evaluate. */
  protected JComboBox<String> m_ComboBoxMetric;

  /** the model for the metric. */
  protected DefaultComboBoxModel<String> m_ModelMetric;

  /** the combobox with the results panels. */
  protected JComboBox<AbstractResultsPanel> m_ComboBoxResults;

  /** the results panels. */
  protected List<AbstractResultsPanel> m_PanelsResults;

  /** the analyze button. */
  protected JButton m_ButtonAnalyze;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    AbstractResultsPanel	panel;

    super.initialize();

    m_ModelMetric   = new DefaultComboBoxModel<>();
    m_PanelsResults = new ArrayList<>();
    for (Class cls: ClassLister.getSingleton().getClasses(AbstractResultsPanel.class)) {
      try {
	panel = (AbstractResultsPanel) cls.newInstance();
	m_PanelsResults.add(panel);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to instantiate results panel: " + cls.getName(), e);
      }
    }
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel		panelTop;
    ParameterPanel 	panelParams;
    JPanel		panelButtons;

    super.initGUI();

    setLayout(new BorderLayout());

    panelTop = new JPanel(new BorderLayout());
    add(panelTop, BorderLayout.NORTH);

    // parameters
    panelParams = new ParameterPanel();
    panelTop.add(panelParams, BorderLayout.CENTER);

    m_PanelTester = new WekaGenericObjectEditorPanel(Tester.class, new PairedCorrectedTTester(), true);  // TODO preferences
    panelParams.addParameter("Tester", m_PanelTester);

    m_PanelMatrix = new WekaGenericObjectEditorPanel(ResultMatrix.class, new ResultMatrixPlainText(), true);  // TODO preferences
    panelParams.addParameter("Output", m_PanelMatrix);

    m_TextSignificance = new NumberTextField(Type.DOUBLE);
    m_TextSignificance.setCheckModel(new BoundedNumberCheckModel(Type.DOUBLE, 0.0, 1.0, 0.05));  // TODO preferences
    m_TextSignificance.setText("0.05");  // TODO preferences
    panelParams.addParameter("Significance level", m_TextSignificance);

    m_ComboBoxMetric = new JComboBox<>(m_ModelMetric);
    panelParams.addParameter("Metric", m_ComboBoxMetric);

    m_ComboBoxResults = new JComboBox<>(
      new DefaultComboBoxModel<>(
	m_PanelsResults.toArray(new AbstractResultsPanel[m_PanelsResults.size()])));
    panelParams.addParameter("Results", m_ComboBoxResults);

    // buttons
    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelTop.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonAnalyze = new JButton("Analyze");
    m_ButtonAnalyze.addActionListener((ActionEvent e) -> analyze());
    panelButtons.add(m_ButtonAnalyze);
  }

  /**
   * Returns the name to display in the GUI.
   *
   * @return		the name
   */
  @Override
  public String getAnalysisName() {
    return "Default";
  }

  /**
   * Sets the matrix to use.
   *
   * @param value	the matrix
   */
  public void setResultMatrix(ResultMatrix value) {
    m_PanelMatrix.setCurrent(value);
  }

  /**
   * Returns the result matrix.
   *
   * @return		the matrix
   */
  public ResultMatrix getResultMatrix() {
    return (ResultMatrix) m_PanelMatrix.getCurrent();
  }

  /**
   * Sets the tester to use.
   *
   * @param value	the tester
   */
  public void setTester(Tester value) {
    m_PanelTester.setCurrent(value);
  }

  /**
   * Returns the tester.
   *
   * @return		the tester
   */
  public Tester getTester() {
    return (Tester) m_PanelTester.getCurrent();
  }

  /**
   * Checks whether the results can be handled at all.
   *
   * @param results	the results to check
   * @return		null if can handle, otherwise error message
   */
  @Override
  public String handlesResults(Instances results) {
    return null;
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public DefaultAnalysisPanel getClone() {
    DefaultAnalysisPanel	result;

    result = new DefaultAnalysisPanel();
    result.setResultMatrix(getResultMatrix());
    result.setTester(getTester());

    return result;
  }

  /**
   * Updates the GUI.
   */
  protected void update() {
    DefaultComboBoxModel<String> 	metric;
    String				oldValue;
    int					index;

    m_ButtonAnalyze.setEnabled(m_Results != null);

    // update metric
    oldValue = (String) m_ComboBoxMetric.getSelectedItem();
    metric = new DefaultComboBoxModel<>();
    m_ModelMetric = metric;
    m_ComboBoxMetric.setModel(m_ModelMetric);
    index = -1;
    if (oldValue != null)
      index = m_ModelMetric.getIndexOf(oldValue);
    if (index == -1)
      index = m_ModelMetric.getIndexOf(PERCENT_CORRECT);
    if (index == -1)
      index = m_ModelMetric.getIndexOf(CORRELATION_COEFFICIENT);
    if (index > -1)
      m_ComboBoxMetric.setSelectedIndex(index);
  }

  /**
   * Returns the attribute index for the specified attribute name.
   *
   * @param attName 	the attribute name to look up
   * @return		the index, -1 if not found
   */
  protected int getAttributeIndex(String attName) {
    Attribute	att;

    if (m_Results == null)
      return -1;
    att = m_Results.attribute(attName);
    if (att == null)
      return -1;
    else
      return att.index();
  }

  /**
   * Performs the analysis.
   */
  protected void analyze() {
    Tester			tester;
    AbstractResultsPanel	panel;

    if (m_Results == null)
      return;

    tester = getTester();
    tester.setInstances(m_Results);
    tester.setSortColumn(-1);
    tester.setRunColumn(getAttributeIndex(KEY_RUN));
    tester.setFoldColumn(getAttributeIndex(KEY_FOLD));
    tester.setDatasetKeyColumns(
      new Range(
	""
	  + (getAttributeIndex(KEY_DATASET) + 1)));
    tester.setResultsetKeyColumns(
      new Range(
	""
	  + (getAttributeIndex(KEY_SCHEME) + 1)
	  + ","
	  + (getAttributeIndex(KEY_SCHEME_OPTIONS) + 1)
	  + ","
	  + (getAttributeIndex(KEY_SCHEME_VERSION_ID) + 1)));
    tester.setResultMatrix(getResultMatrix());
    tester.setDisplayedResultsets(null);
    tester.setSignificanceLevel(Double.parseDouble(m_TextSignificance.getText()));
    try {
      if (getAttributeIndex(PERCENT_CORRECT) > -1)
	tester.multiResultsetFull(0, getAttributeIndex(PERCENT_CORRECT));
      else
	tester.multiResultsetFull(0, getAttributeIndex(CORRELATION_COEFFICIENT));
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getOwner(), "Failed to analyze experiment!", e);
      return;
    }

    // TODO history
    panel = ((AbstractResultsPanel) m_ComboBoxResults.getSelectedItem()).getClone();
    panel.display(tester.getResultMatrix());
    add(panel, BorderLayout.CENTER);
  }
}
