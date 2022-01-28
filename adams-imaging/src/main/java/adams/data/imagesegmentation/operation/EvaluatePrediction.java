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
 * EvaluatePrediction.java
 * Copyright (C) 2020-2021 University of Waikato, Hamilton, NZ
 */

package adams.data.imagesegmentation.operation;

import adams.core.QuickInfoHelper;
import adams.data.image.BufferedImageHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.ImageSegmentationContainer;
import com.github.fracpete.javautils.struct.Struct2;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluates a prediction (first container) against the annotation
 * (second container) and outputs a spreadsheet with the results.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EvaluatePrediction
  extends AbstractImageSegmentationContainerOperation {

  private static final long serialVersionUID = 5451678654384977453L;

  /** the key for annotation count. */
  public final static String KEY_ANNOTATION = "annotation";

  /** the key for missed count. */
  public final static String KEY_MISSED = "missed";

  /** the key for overlap count. */
  public final static String KEY_OVERLAP = "overlap";

  /** the key for additional count. */
  public final static String KEY_ADDITIONAL = "additional";

  /** whether to add misclassification information. */
  protected boolean m_AddMisclassification;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates a prediction (first container) against the annotation "
      + "(second container) and outputs a spreadsheet with the results.\n"
      + "For calculating the misclassified percentage, the total pixel count "
      + "of the other label is used as denominator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "add-misclassification", "addMisclassification",
      false);
  }

  /**
   * Sets whether to add the misclassification information.
   *
   * @param value 	true if to add
   */
  public void setAddMisclassification(boolean value) {
    m_AddMisclassification = value;
    reset();
  }

  /**
   * Returns whether to add the misclassification information.
   *
   * @return 		true if to add
   */
  public boolean getAddMisclassification() {
    return m_AddMisclassification;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or for listing the options.
   */
  public String addMisclassificationTipText() {
    return "If enabled, additional columns with misclassification information are added.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "addMisclassifiication", m_AddMisclassification, "add misclassification", "");
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
   * Compares the two pixel arrays.
   *
   * @param predPixels	the prediction pixels
   * @param annoPixels	the annotation pixels
   * @return		the statistics
   */
  protected Map<String,Integer> compare(int[] predPixels, int[] annoPixels) {
    Map<String,Integer>	result;
    int			i;
    int			black;
    int 		annotationCount;
    int 		missedCount;
    int 		additionalCount;
    int 		overlapCount;

    black = Color.BLACK.getRGB();

    if (predPixels == null) {
      predPixels = new int[annoPixels.length];
      Arrays.fill(predPixels, black);
    }

    annotationCount = 0;
    additionalCount = 0;
    missedCount     = 0;
    overlapCount    = 0;
    for (i = 0; i < predPixels.length; i++) {
      if (annoPixels[i] != black)
        annotationCount++;
      if (predPixels[i] == annoPixels[i]) {
	if (predPixels[i] != black)
	  overlapCount++;
      }
      else {
	if (predPixels[i] != black)
	  additionalCount++;
	else if (predPixels[i] == black)
	  missedCount++;
      }
    }

    result = new HashMap<>();
    result.put(KEY_ANNOTATION, annotationCount);
    result.put(KEY_MISSED, missedCount);
    result.put(KEY_ADDITIONAL, additionalCount);
    result.put(KEY_OVERLAP, overlapCount);

    return result;
  }

  /**
   * Calculates the misclassification count/percentage.
   * Percentage is calculated as: miscl_pixel_count / total_pixel_count_other_label
   *
   * @param predPixels	the prediction pixels
   * @param annoOtherPixels	the annotation pixels from another layer
   * @return		the misclassified pixel count and percentage
   */
  protected Struct2<Integer,Double> calcMisclassified(int[] predPixels, int[] annoOtherPixels) {
    Struct2<Integer,Double>	result;
    int				i;
    int				black;
    int				annoTotal;

    result = new Struct2<>(0, 0.0);

    black = Color.BLACK.getRGB();

    if (predPixels == null) {
      predPixels = new int[annoOtherPixels.length];
      Arrays.fill(predPixels, black);
    }

    annoTotal = 0;
    for (i = 0; i < predPixels.length; i++) {
      if ((annoOtherPixels[i] != black) && (predPixels[i] != black))
        result.value1++;
      if (annoOtherPixels[i] != black)
        annoTotal++;
    }
    result.value2 = (double) result.value1 / annoTotal;

    return result;
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
    Map<String,int[]> 		predPixels;
    Map<String,int[]> 		annoPixels;
    List<String> 		labels;
    Map<String,Integer>		stats;
    int				total;
    Struct2<Integer,Double>	miscl;

    predCont   = containers[0];
    predLayers = (Map<String,BufferedImage>) predCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    annoCont   = containers[1];
    annoLayers = (Map<String,BufferedImage>) annoCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);

    labels = new ArrayList<>(annoLayers.keySet());
    Collections.sort(labels);

    predPixels = new HashMap<>();
    annoPixels = new HashMap<>();
    for (String label: labels) {
      if (predLayers.containsKey(label))
        predPixels.put(label, BufferedImageHelper.getPixels(predLayers.get(label)));
      if (annoLayers.containsKey(label))
        annoPixels.put(label, BufferedImageHelper.getPixels(annoLayers.get(label)));
    }

    result = new DefaultSpreadSheet();
    result.addComment("Name: " + predCont.getValue(ImageSegmentationContainer.VALUE_NAME));

    // header
    row    = result.getHeaderRow();
    row.addCell("L").setContentAsString("Layer");
    row.addCell("O").setContentAsString("Overlap");
    row.addCell("OP").setContentAsString("Overlap (perc)");
    row.addCell("M").setContentAsString("Missed");
    row.addCell("MP").setContentAsString("Missed (perc)");
    row.addCell("A").setContentAsString("Additional");
    row.addCell("AP").setContentAsString("Additional (perc)");

    if (m_AddMisclassification) {
      for (String label : labels) {
	row.addCell("ACT-" + label).setContentAsString("Actual layer " + label);
	row.addCell("ACTP-" + label).setContentAsString("Actual layer " + label + " (perc)");
      }
    }

    // data
    for (String label: labels) {
      row = result.addRow();
      row.getCell("L").setContentAsString(label);

      stats = compare(predPixels.get(label), annoPixels.get(label));
      total = stats.get(KEY_ANNOTATION);

      row.getCell("O").setContent(stats.get(KEY_OVERLAP));
      row.getCell("OP").setContent((double) stats.get(KEY_OVERLAP) / total);
      row.getCell("M").setContent(stats.get(KEY_MISSED));
      row.getCell("MP").setContent((double) stats.get(KEY_MISSED) / total);
      row.getCell("A").setContent(stats.get(KEY_ADDITIONAL));
      row.getCell("AP").setContent((double) stats.get(KEY_ADDITIONAL) / total);

      if (m_AddMisclassification) {
	for (String other : labels) {
	  if (other.equals(label))
	    continue;
	  miscl = calcMisclassified(predPixels.get(label), annoPixels.get(other));
	  row.getCell("ACT-" + other).setContent(miscl.value1);
	  row.getCell("ACTP-" + other).setContent(miscl.value2);
	}
      }
    }

    return result;
  }
}
