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
 * FromPredictions.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.evaluation;

import adams.core.MessageCollection;
import adams.core.ObjectCopyHelper;
import adams.core.Range;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.MetaData;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.data.spreadsheet.SpreadSheetUnorderedColumnRange;
import adams.data.spreadsheet.SpreadSheetView;
import adams.flow.core.Token;
import adams.flow.transformer.WekaSpreadSheetToPredictions;
import adams.gui.chooser.SpreadSheetFileChooserPanel;
import adams.gui.core.IndexTextField;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RangeTextField;
import adams.gui.tools.wekainvestigator.tab.AbstractInvestigatorTab.SerializationOption;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Loads predictions from a spreadsheet for evaluation.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FromPredictions
  extends AbstractClassifierEvaluation {

  private static final long serialVersionUID = 1175400993991698944L;

  public static final String KEY_FILE = "file";

  public static final String KEY_READER = "reader";

  public static final String KEY_ACTUAL = "actual";

  public static final String KEY_PREDICTED = "predicted";

  public static final String KEY_WEIGHT = "weight";

  public static final String KEY_CLASSDISTRIBUTION = "class distribution";

  public static final String KEY_ADDITIONAL = "additional";

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the preditions file. */
  protected SpreadSheetFileChooserPanel m_PanelFile;

  /** the text with the actual column index. */
  protected IndexTextField m_TextActual;

  /** the text with the predicted column index. */
  protected IndexTextField m_TextPredicted;

  /** the text with the weight column index. */
  protected IndexTextField m_TextWeight;

  /** the columns with the class distribution columns range. */
  protected RangeTextField m_TextClassDistribution;

  /** the columns with the additional data to store. */
  protected RangeTextField m_TextAdditional;

  /** the fake model. */
  protected weka.classifiers.functions.FromPredictions m_Model;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Loads predictions from a spreadsheet for evaluation.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Model = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelParameters = new ParameterPanel();
    m_PanelOptions.add(m_PanelParameters, BorderLayout.CENTER);

    // predictions file
    m_PanelFile = new SpreadSheetFileChooserPanel();
    m_PanelFile.addChangeListener((ChangeEvent e) -> {
      m_Model = null;
      getOwner().updateButtons();
    });
    m_PanelParameters.addParameter("Predictions", m_PanelFile);

    // actual column
    m_TextActual = new IndexTextField("1");
    m_PanelParameters.addParameter("Actual", m_TextActual);

    // predicted column
    m_TextPredicted = new IndexTextField("2");
    m_PanelParameters.addParameter("Predicted", m_TextPredicted);

    // weight column
    m_TextWeight = new IndexTextField("");
    m_PanelParameters.addParameter("Weight", m_TextWeight);

    // class distribution
    m_TextClassDistribution = new RangeTextField("");
    m_PanelParameters.addParameter("Class distribution", m_TextClassDistribution);

    // additional attributes
    m_TextAdditional = new RangeTextField(Range.ALL);
    m_PanelParameters.addParameter("Additional", m_TextAdditional);
  }

  /**
   * Returns the name of the evaluation (displayed in combobox).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "From predictions";
  }

  /**
   * Creates the fake model.
   *
   * @return		null if successfully loaded, otherwise error message
   */
  protected String createFakeModel() {
    File					file;
    weka.classifiers.functions.FromPredictions	model;

    m_Model  = null;

    file = m_PanelFile.getCurrent();
    if (file.isDirectory())
      return "File points to a directory: " + file;
    if (!file.exists())
      return "File does not exist: " + file;

    model = new weka.classifiers.functions.FromPredictions();
    model.setPredictionsFile(new PlaceholderFile(m_PanelFile.getCurrent()));
    model.setReader(ObjectCopyHelper.copyObject(m_PanelFile.getReader()));
    model.setActual(new SpreadSheetColumnIndex(m_TextActual.getText()));
    model.setPredicted(new SpreadSheetColumnIndex(m_TextPredicted.getText()));
    model.setWeight(new SpreadSheetColumnIndex(m_TextWeight.getText()));
    model.setClassDistribution(new SpreadSheetUnorderedColumnRange(m_TextClassDistribution.getText()));
    model.setAdditional(new SpreadSheetColumnRange(m_TextAdditional.getText()));
    try {
      model.buildClassifier(null);
      m_Model = model;
    }
    catch (Exception e) {
      return e.getMessage();
    }

    return null;
  }

  /**
   * Tests whether the classifier can be evaluated.
   *
   * @return		null if successful, otherwise error message
   */
  public String canEvaluate(Classifier classifier) {
    File		file;
    String		msg;

    file = m_PanelFile.getCurrent();
    if (file.isDirectory())
      return "File points to directory: " + file;
    if (!file.exists())
      return "File does not exist: " + file;

    msg = createFakeModel();
    if (msg != null)
      return msg;

    if (m_Model == null)
      return "Failed to load predictions: " + file;

    return null;
  }

  /**
   * Initializes the result item.
   *
   * @param classifier	the current classifier
   * @return		the initialized history item
   * @throws Exception	if initialization fails
   */
  @Override
  public ResultItem init(Classifier classifier) throws Exception {
    return new ResultItem(classifier, null);
  }

  /**
   * Evaluates the classifier and updates the result item.
   *
   * @param classifier	the current classifier
   * @param item	the item to update
   * @throws Exception	if evaluation fails
   */
  @Override
  protected void doEvaluate(Classifier classifier, ResultItem item) throws Exception {
    Evaluation 				eval;
    WekaSpreadSheetToPredictions	topred;
    String				msg;
    MetaData 				runInfo;
    TIntList 				original;
    int					i;
    SpreadSheet				additional;

    classifier = (Classifier) OptionUtils.shallowCopy(m_Model);

    if ((msg = canEvaluate(classifier)) != null)
      throw new IllegalArgumentException("Failed to process predictions!\n" + msg);

    item.setTemplate(classifier);
    runInfo  = new MetaData();
    runInfo.add("Classifier", OptionUtils.getCommandLine(m_Model));
    runInfo.add("File", m_PanelFile.getCurrent());
    runInfo.add("Reader", m_PanelFile.getReader().toCommandLine());
    runInfo.add("Actual", m_TextActual.getText());
    runInfo.add("Predicted", m_TextPredicted.getText());
    runInfo.add("Weight", m_TextWeight.getText());
    runInfo.add("Class distribution", m_TextClassDistribution.getText());
    runInfo.add("Additional", m_TextAdditional.getText());

    topred = new WekaSpreadSheetToPredictions();
    topred.setActual(new SpreadSheetColumnIndex(m_TextActual.getText()));
    topred.setPredicted(new SpreadSheetColumnIndex(m_TextPredicted.getText()));
    topred.setWeight(new SpreadSheetColumnIndex(m_TextWeight.getText()));
    topred.setClassDistribution(new SpreadSheetUnorderedColumnRange(m_TextClassDistribution.getText()));
    topred.setUseColumnNamesAsClassLabels(true);
    topred.input(new Token(m_Model.getPredictions()));
    if ((msg = topred.execute()) != null)
      throw new IllegalArgumentException("Failed to generated Evaluations object from predictions:\n" + msg);
    eval = (Evaluation) topred.output().getPayload();

    original = new TIntArrayList();
    for (i = 0; i < m_Model.getPredictions().getRowCount(); i++)
      original.add(i);

    additional = null;
    if (!m_TextAdditional.getText().isEmpty())
      additional = new SpreadSheetView(m_Model.getPredictions(), null, m_Model.getAdditionalIndices());

    item.update(
      eval, m_Model, runInfo,
      original.toArray(), additional);
  }

  /**
   * Updates the settings panel.
   */
  @Override
  public void update() {
    if (getOwner() == null)
      return;
    if (getOwner().getOwner() == null)
      return;

    getOwner().updateButtons();
  }

  /**
   * Activates the specified dataset.
   *
   * @param index	the index of the dataset
   */
  public void activate(int index) {
  }

  /**
   * Returns the objects for serialization.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  public Map<String,Object> serialize(Set<SerializationOption> options) {
    Map<String,Object>	result;

    result = super.serialize(options);
    if (options.contains(SerializationOption.PARAMETERS)) {
      result.put(KEY_FILE, m_PanelFile.getCurrent().getAbsolutePath());
      result.put(KEY_READER, m_PanelFile.getReader().toCommandLine());
      result.put(KEY_ACTUAL, m_TextActual.getText());
      result.put(KEY_PREDICTED, m_TextPredicted.getText());
      result.put(KEY_WEIGHT, m_TextWeight.getText());
      result.put(KEY_CLASSDISTRIBUTION, m_TextClassDistribution.getText());
      result.put(KEY_ADDITIONAL, m_TextAdditional.getText());
    }

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  public void deserialize(Map<String,Object> data, MessageCollection errors) {
    super.deserialize(data, errors);
    if (data.containsKey(KEY_FILE))
      m_PanelFile.setCurrent(new PlaceholderFile((String) data.get(KEY_FILE)));
    if (data.containsKey(KEY_READER)) {
      try {
	m_PanelFile.setReader((SpreadSheetReader) OptionUtils.forCommandLine(OptionHandler.class, (String) data.get(KEY_READER)));
      }
      catch (Exception e) {
        errors.add("Failed to parse spreadsheet reader command-line: " + data.get(KEY_READER), e);
      }
    }
    if (data.containsKey(KEY_ACTUAL))
      m_TextActual.setText((String) data.get(KEY_ACTUAL));
    if (data.containsKey(KEY_PREDICTED))
      m_TextPredicted.setText((String) data.get(KEY_PREDICTED));
    if (data.containsKey(KEY_WEIGHT))
      m_TextWeight.setText((String) data.get(KEY_WEIGHT));
    if (data.containsKey(KEY_CLASSDISTRIBUTION))
      m_TextClassDistribution.setText((String) data.get(KEY_CLASSDISTRIBUTION));
    if (data.containsKey(KEY_ADDITIONAL))
      m_TextAdditional.setText((String) data.get(KEY_ADDITIONAL));
  }
}
