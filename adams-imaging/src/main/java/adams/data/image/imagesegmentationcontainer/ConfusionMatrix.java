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
 * ConfusionMatrix.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.image.imagesegmentationcontainer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.image.BufferedImageHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a prediction (first container) against the annotation
 * (second container) and outputs a spreadsheet with the confusion matrix.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConfusionMatrix
  extends AbstractImageSegmentationContainerOperation {

  private static final long serialVersionUID = 5451678654384977453L;

  /**
   * Defines what values to show.
   */
  public enum MatrixValues {
    COUNTS,
    PERCENTAGES,
    PERCENTAGES_PER_ROW,
  }

  /** the label to use instead of automatically determining. */
  protected BaseString[] m_Labels;

  /** the optional prefix for the actual labels. */
  protected String m_ActualPrefix;

  /** the optional prefix for the predicted labels. */
  protected String m_PredictedPrefix;

  /** what values to generate. */
  protected MatrixValues m_MatrixValues;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates a prediction (first container) against the annotation "
      + "(second container) and outputs a spreadsheet with the confusion matrix.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "label", "labels",
      new BaseString[0]);

    m_OptionManager.add(
      "actual-prefix", "actualPrefix",
      "a: ");

    m_OptionManager.add(
      "predicted-prefix", "predictedPrefix",
      "p: ");

    m_OptionManager.add(
      "matrix-values", "matrixValues",
      MatrixValues.COUNTS);
  }

  /**
   * Sets the labels to use for enforcing order other than alphabetical.
   *
   * @param value	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to use for enforcing order other than alphabetical.
   *
   * @return		the labels
   */
  public BaseString[] getLabels() {
    return m_Labels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelsTipText() {
    return "The labels to use for enforcing order other than alphabetical.";
  }

  /**
   * Sets the prefix of the actual labels.
   *
   * @param value	the prefix
   */
  public void setActualPrefix(String value) {
    m_ActualPrefix = value;
    reset();
  }

  /**
   * Returns the prefix of the actual labels.
   *
   * @return		the prefix
   */
  public String getActualPrefix() {
    return m_ActualPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actualPrefixTipText() {
    return "The prefix for the actual labels.";
  }

  /**
   * Sets the prefix of the predicted labels.
   *
   * @param value	the prefix
   */
  public void setPredictedPrefix(String value) {
    m_PredictedPrefix = value;
    reset();
  }

  /**
   * Returns the prefix of the predicted labels.
   *
   * @return		the prefix
   */
  public String getPredictedPrefix() {
    return m_PredictedPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictedPrefixTipText() {
    return "The prefix for the predicted labels.";
  }

  /**
   * Sets the type of values to generate.
   *
   * @param value	the type of values
   */
  public void setMatrixValues(MatrixValues value) {
    m_MatrixValues = value;
    reset();
  }

  /**
   * Returns the type of values to generate.
   *
   * @return		the type of values
   */
  public MatrixValues getMatrixValues() {
    return m_MatrixValues;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String matrixValuesTipText() {
    return "The type of values to generate.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "labels", m_Labels, "labels: ");
    result += QuickInfoHelper.toString(this, "matrixValues", m_MatrixValues, ", values: ");

    return result;
  }

  /**
   * Returns the minimum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumContainersRequired() {
    return 2;
  }

  /**
   * Returns the maximum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumContainersRequired() {
    return 2;
  }

  /**
   * The type of data that is generated.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Collapses the layers into a single array, with the label index corresponding
   * to the integer value (label 0 = pixel 1, label 1 = pixel 2, ...).
   *
   * @param layers 	the layers to collapse
   * @param labels	the labels to collapse
   * @param collapsed	the collapsed array to fill
   */
  protected void collapse(Map<String,BufferedImage> layers, List<String> labels, int[] collapsed) {
    int 	black;
    int		labelIndex;
    String	labelStr;
    int[]	pixels;
    int		i;

    black = Color.BLACK.getRGB();
    for (labelIndex = 0; labelIndex < labels.size(); labelIndex++) {
      labelStr = labels.get(labelIndex);
      if (!layers.containsKey(labelStr))
        continue;
      pixels = BufferedImageHelper.getPixels(layers.get(labelStr));
      for (i = 0; i < pixels.length; i++) {
        if (pixels[i] != black)
          collapsed[i] = labelIndex + 1;
      }
    }
  }

  /**
   * Converts the number to integer if COUNTS is used.
   *
   * @param value	the value to convert
   * @return		the converted value
   */
  protected Object convert(double value) {
    if (m_MatrixValues == MatrixValues.COUNTS)
      return (int) value;
    else
      return value;
  }

  /**
   * Performs the actual processing of the containers.
   *
   * @param containers the containers to process
   * @return the generated data
   */
  @Override
  protected Object doProcess(ImageSegmentationContainer[] containers) {
    SpreadSheet			result;
    Row				row;
    ImageSegmentationContainer 	predCont;
    ImageSegmentationContainer 	annoCont;
    Map<String,BufferedImage> 	predLayers;
    Map<String,BufferedImage> 	annoLayers;
    List<String> 		labels;
    int[]			annotations;
    int[]			predictions;
    int				length;
    double[][]			matrix;
    int				i;
    int				n;
    double			total;

    predCont   = containers[0];
    predLayers = (Map<String,BufferedImage>) predCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    annoCont   = containers[1];
    annoLayers = (Map<String,BufferedImage>) annoCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);

    if (m_Labels.length > 0) {
      labels = new ArrayList<>(Arrays.asList(BaseObject.toStringArray(m_Labels)));
    }
    else {
      labels = new ArrayList<>(annoLayers.keySet());
      Collections.sort(labels);
    }

    // initialize array
    length = 0;
    for (String label: labels) {
      length = predLayers.get(label).getWidth() * predLayers.get(label).getHeight();
      break;
    }
    if (length == 0)
      return new DefaultSpreadSheet();

    annotations = new int[length];
    collapse(annoLayers, labels, annotations);
    predictions = new int[length];
    collapse(predLayers, labels, predictions);

    matrix = new double[labels.size() + 1][labels.size() + 1];
    for (i = 0; i < annotations.length; i++)
      matrix[annotations[i]][predictions[i]]++;

    switch (m_MatrixValues) {
      case COUNTS:
        // nothing to do
        break;
      case PERCENTAGES_PER_ROW:
        for (n = 0; n < labels.size() + 1; n++) {
	  total = 0;
	  for (i = 0; i < labels.size() + 1; i++)
	    total += matrix[n][i];
  	  // if total=0, then NaNs will show up as missing cells, which is fine
	  for (i = 0; i < labels.size() + 1; i++)
	    matrix[n][i] /= total;
	}
        break;
      case PERCENTAGES:
        total = 0;
        for (n = 0; n < labels.size() + 1; n++) {
	  for (i = 0; i < labels.size() + 1; i++)
	    total += matrix[n][i];
	}
	// if total=0, then NaNs will show up as missing cells, which is fine
        for (n = 0; n < labels.size() + 1; n++) {
	  for (i = 0; i < labels.size() + 1; i++)
	    matrix[n][i] /= total;
	}
        break;
      default:
        throw new IllegalStateException("Unhandled matrix values type: " + m_MatrixValues);
    }

    result = new DefaultSpreadSheet();

    // header
    row    = result.getHeaderRow();
    row.addCell("L").setContentAsString("x");
    row.addCell("U").setContentAsString(m_PredictedPrefix + "Unlabeled");
    for (i = 0; i < labels.size(); i++)
      row.addCell("" + i).setContentAsString(m_PredictedPrefix + labels.get(i));

    // unlabeled
    row = result.addRow();
    row.addCell("L").setContentAsString(m_ActualPrefix + "Unlabeled");
    row.addCell("U").setNative(convert(matrix[0][0]));
    for (i = 0; i < labels.size(); i++)
      row.addCell("" + i).setNative(convert(matrix[0][i+1]));

    // labels
    for (n = 0; n < labels.size(); n++) {
      row = result.addRow();
      row.addCell("L").setContentAsString(m_ActualPrefix + labels.get(n));
      row.addCell("U").setNative(convert(matrix[n + 1][0]));
      for (i = 0; i < labels.size(); i++)
	row.addCell("" + i).setNative(convert(matrix[n + 1][i + 1]));
    }

    return result;
  }
}
