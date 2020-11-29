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

package weka.classifiers.functions;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.classifiers.simple.AbstractSimpleClassifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Encapsulates predictions from a spreadsheet. Dummy classifier for the Investigator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FromPredictions
  extends AbstractSimpleClassifier {

  private static final long serialVersionUID = 3788758906281773914L;

  /** the predictions to use. */
  protected PlaceholderFile m_PredictionsFile;

  /** the spreadsheet reader to use. */
  protected SpreadSheetReader m_Reader;

  /** the column with the actual values. */
  protected SpreadSheetColumnIndex m_Actual;

  /** the actual column index. */
  protected int m_ActualIndex;

  /** the column with the predicted values. */
  protected SpreadSheetColumnIndex m_Predicted;

  /** the predicted column index. */
  protected int m_PredictedIndex;

  /** the column with the error values (optional). */
  protected SpreadSheetColumnIndex m_Weight;

  /** the weight column index. */
  protected int m_WeightIndex;

  /** the additional columns in the spreadsheet to add to the plot containers. */
  protected SpreadSheetColumnRange m_Additional;

  /** the additional column indices. */
  protected int[] m_AdditionalIndices;

  /** the actual predictions. */
  protected SpreadSheet m_Predictions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Encapsulates predictions from a spreadsheet. Dummy classifier for the Investigator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "predictions-file", "predictionsFile",
      new PlaceholderFile());

    m_OptionManager.add(
      "reader", "reader",
      new CsvSpreadSheetReader());

    m_OptionManager.add(
      "actual", "actual",
      new SpreadSheetColumnIndex("Actual"));

    m_OptionManager.add(
      "predicted", "predicted",
      new SpreadSheetColumnIndex("Predicted"));

    m_OptionManager.add(
      "weight", "weight",
      new SpreadSheetColumnIndex(""));

    m_OptionManager.add(
      "additional", "additional",
      new SpreadSheetColumnRange(""));
  }

  /**
   * Sets the file with the predictions.
   *
   * @param value	the file
   */
  public void setPredictionsFile(PlaceholderFile value) {
    m_PredictionsFile = value;
    reset();
  }

  /**
   * Returns the file with the predictions.
   *
   * @return		the file
   */
  public PlaceholderFile getPredictionsFile() {
    return m_PredictionsFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsFileTipText() {
    return "The spreadsheet file with the predictions.";
  }

  /**
   * Sets the spreadsheet reader to use.
   *
   * @param value	the reader
   */
  public void setReader(SpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the spreadsheet reader to use.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The spreadsheet reader to use.";
  }

  /**
   * Sets the column with the actual values.
   *
   * @param value	the column
   */
  public void setActual(SpreadSheetColumnIndex value) {
    m_Actual = value;
    reset();
  }

  /**
   * Returns the column with the actual values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getActual() {
    return m_Actual;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualTipText() {
    return "The column with the actual values.";
  }

  /**
   * Sets the column with the predicted values.
   *
   * @param value	the column
   */
  public void setPredicted(SpreadSheetColumnIndex value) {
    m_Predicted = value;
    reset();
  }

  /**
   * Returns the column with the predicted values.
   *
   * @return		the range
   */
  public SpreadSheetColumnIndex getPredicted() {
    return m_Predicted;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedTipText() {
    return "The column with the predicted values.";
  }

  /**
   * Sets the column with the weight values.
   *
   * @param value	the column
   */
  public void setWeight(SpreadSheetColumnIndex value) {
    m_Weight = value;
    reset();
  }

  /**
   * Returns the column with the weight values.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getWeight() {
    return m_Weight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String weightTipText() {
    return "The column with the weight values.";
  }

  /**
   * Sets the additional columns to add to the plot containers.
   *
   * @param value	the columns
   */
  public void setAdditional(SpreadSheetColumnRange value) {
    m_Additional = value;
    reset();
  }

  /**
   * Returns the additional columns to add to the plot containers.
   *
   * @return		the columns
   */
  public SpreadSheetColumnRange getAdditional() {
    return m_Additional;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalTipText() {
    return "The additional columns to add to the plot containers.";
  }

  /**
   * Returns the predictions that were loaded.
   *
   * @return		the predictions, null if not available
   */
  public SpreadSheet getPredictions() {
    return m_Predictions;
  }

  /**
   * Returns the actual 0-based index.
   * 
   * @return		the index, -1 if not initialized
   */
  public int getActualIndex() {
    return m_ActualIndex;
  }

  /**
   * Returns the predicted 0-based index.
   * 
   * @return		the index, -1 if not initialized
   */
  public int getPredictedIndex() {
    return m_PredictedIndex;
  }

  /**
   * Returns the weight 0-based index.
   * 
   * @return		the index, -1 if not initialized
   */
  public int getWeightIndex() {
    return m_WeightIndex;
  }

  /**
   * Returns the 0-based indices of the additional columns.
   *
   * @return		the indices, 0-length array if not initialized or not used
   */
  public int[] getAdditionalIndices() {
    return m_AdditionalIndices;
  }

  /**
   * Just loads the predictions.
   *
   * @param data	ignored
   * @throws Exception	if loading of predictions failed
   */
  @Override
  public void buildClassifier(Instances data) throws Exception {
    TIntList		additional;

    m_ActualIndex       = -1;
    m_PredictedIndex    = -1;
    m_WeightIndex       = -1;
    m_AdditionalIndices = new int[0];

    m_Predictions = m_Reader.read(m_PredictionsFile);
    if (m_Predictions == null)
      throw new IllegalStateException("Failed to load predictions from: " + m_PredictionsFile);

    m_Actual.setData(m_Predictions);
    if (m_Actual.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate actual column: " + m_Actual.getIndex());

    m_Predicted.setData(m_Predictions);
    if (m_Predicted.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate predicted column: " + m_Predicted.getIndex());

    if (!m_Weight.isEmpty()) {
      m_Weight.setData(m_Predictions);
      if (m_Weight.getIntIndex() == -1)
	throw new IllegalStateException("Failed to locate weight column: " + m_Weight.getIndex());
    }

    m_PredictedIndex = m_Predicted.getIntIndex();
    m_ActualIndex    = m_Actual.getIntIndex();
    m_WeightIndex    = m_Weight.getIntIndex();
    if ((m_WeightIndex == m_PredictedIndex) || (m_WeightIndex == m_ActualIndex))
      m_WeightIndex = -1;

    if (!m_Additional.isEmpty()) {
      m_Additional.setData(m_Predictions);
      additional = new TIntArrayList(m_Additional.getIntIndices());
      additional.remove(m_ActualIndex);
      additional.remove(m_PredictedIndex);
      additional.remove(m_WeightIndex);
      m_AdditionalIndices = additional.toArray();
    }
  }

  /**
   * Always returns 0.
   *
   * @param instance 	the instance to be classified
   * @return		always 0
   * @throws Exception	never thrown
   */
  @Override
  public double classifyInstance(Instance instance) throws Exception {
    return 0.0;
  }
}
