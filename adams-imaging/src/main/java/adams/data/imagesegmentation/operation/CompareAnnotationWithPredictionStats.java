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
 * CompareAnnotationWithPredictionStats.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.data.imagesegmentation.operation;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.data.image.BufferedImageHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compares a prediction (first container) with the annotation
 * (second container) and outputs statistics per label:
 * correct overlaps (count/%), the missed annotations (count/%) and additionally
 * predicted pixels (count/%), Intersect-over-Union, Dice coefficient.
 * For the percentages, the total number of annotations is used as divisor.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CompareAnnotationWithPredictionStats
  extends AbstractImageSegmentationContainerOperation {

  private static final long serialVersionUID = 5451678654384977453L;

  /** the labels to analyze (all if none specified). */
  protected BaseString[] m_Labels;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Compares a prediction (first container) with the annotation "
	     + "(second container) and outputs statistics per label: "
	     + "correct overlaps (count/%), the missed annotations (count/%) and additionally "
	     + "predicted pixels (count/%), Intersect-over-Union, Dice coefficient. "
	     + "For the percentages, the total number of annotations is used as divisor.";
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
  }

  /**
   * Sets the labels to generate the images for instead of all.
   *
   * @param value	the labels
   */
  public void setLabels(BaseString[] value) {
    m_Labels = value;
    reset();
  }

  /**
   * Returns the labels to generate the images for instead of all.
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
    return "The labels to generate the counts/percentages for instead of all.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "labels", m_Labels, "labels: ");
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
   * Compares the two pixel arrays and generates counts from it.
   *
   * @param predPixels	the prediction pixels
   * @param annoPixels	the annotation pixels
   * @return		the counts (correct, missed, additional)
   */
  protected int[] generate(int[] predPixels, int[] annoPixels) {
    int[]	result;
    int		i;
    int		black;

    result = new int[3];
    black  = Color.BLACK.getRGB();

    if (predPixels == null) {
      predPixels = new int[annoPixels.length];
      Arrays.fill(predPixels, black);
    }

    for (i = 0; i < predPixels.length; i++) {
      // binary layers, so no need to check whether the same value
      if ((annoPixels[i] != black) && (predPixels[i] != black))
	result[0]++;
      else if (annoPixels[i] != black)
	result[1]++;
      else if (predPixels[i] != black)
	result[2]++;
    }

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
    SpreadSheet					result;
    Row						row;
    ImageSegmentationContainer 			predCont;
    ImageSegmentationContainer 			annoCont;
    Map<String,BufferedImage> 			predLayers;
    Map<String,BufferedImage> 			annoLayers;
    Map<String,int[]> 				predPixels;
    Map<String,int[]> 				annoPixels;
    List<String> 				labels;
    int[]					counts;
    int						overlap;
    int						missed;
    int						additional;
    int						annoTotal;
    int						predTotal;

    predCont   = containers[0];
    predLayers = (Map<String,BufferedImage>) predCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    annoCont   = containers[1];
    annoLayers = (Map<String,BufferedImage>) annoCont.getValue(ImageSegmentationContainer.VALUE_LAYERS);

    labels = new ArrayList<>(annoLayers.keySet());
    Collections.sort(labels);
    // specific subset?
    if (m_Labels.length > 0)
      labels.retainAll(BaseObject.toStringList(m_Labels));

    predPixels = new HashMap<>();
    annoPixels = new HashMap<>();
    for (String label: labels) {
      if (predLayers.containsKey(label))
	predPixels.put(label, BufferedImageHelper.getPixels(predLayers.get(label)));
      if (annoLayers.containsKey(label))
	annoPixels.put(label, BufferedImageHelper.getPixels(annoLayers.get(label)));
    }

    result = new DefaultSpreadSheet();
    row    = result.getHeaderRow();
    row.addCell("L").setContentAsString("Label");
    row.addCell("CC").setContentAsString("Correct");
    row.addCell("CP").setContentAsString("Correct %");
    row.addCell("MC").setContentAsString("Missed");
    row.addCell("MP").setContentAsString("Missed %");
    row.addCell("AC").setContentAsString("Additional");
    row.addCell("AP").setContentAsString("Additional %");
    row.addCell("IOU").setContentAsString("IoU");
    row.addCell("DC").setContentAsString("Dice");
    for (String label: labels) {
      counts     = generate(predPixels.get(label), annoPixels.get(label));
      overlap    = counts[0];
      missed     = counts[1];
      additional = counts[2];
      annoTotal  = StatUtils.countDifferent(annoPixels.get(label), Color.BLACK.getRGB());
      if (predPixels.containsKey(label))
	predTotal = StatUtils.countDifferent(predPixels.get(label), Color.BLACK.getRGB());
      else
	predTotal = 0;
      row        = result.addRow();
      row.addCell("L").setContentAsString(label);
      row.addCell("CC").setContent(overlap);
      row.addCell("CP").setContent((double) overlap / annoTotal * 100.0);
      row.addCell("MC").setContent(missed);
      row.addCell("MP").setContent((double) missed / annoTotal * 100.0);
      row.addCell("AC").setContent(additional);
      row.addCell("AP").setContent((double) additional / annoTotal * 100.0);
      row.addCell("IOU").setContent((double) overlap / (double) (overlap + missed + additional));
      row.addCell("DC").setContent((double) (2*overlap) / (double) (annoTotal + predTotal));
    }

    return result;
  }
}
