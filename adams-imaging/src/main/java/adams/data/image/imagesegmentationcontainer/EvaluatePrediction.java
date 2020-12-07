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
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.image.imagesegmentationcontainer;

import adams.data.image.BufferedImageHelper;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.ImageSegmentationContainer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Evaluates a prediction (first container) against the annotation "
      + "(second container) and outputs a spreadsheet with the results.";
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
   * Compares the two images.
   *
   * @param prediction	the prediction image
   * @param annotation	the annotation image
   * @return		the statistics
   */
  protected Map<String,Integer> compare(BufferedImage prediction, BufferedImage annotation) {
    Map<String,Integer>	result;
    int[]		predP;
    int[]		annoP;
    int			i;
    int			black;
    int 		annotationCount;
    int 		missedCount;
    int 		additionalCount;
    int 		overlapCount;

    if (prediction == null)
      prediction = new BufferedImage(annotation.getWidth(), annotation.getHeight(), annotation.getType());

    predP           = BufferedImageHelper.getPixels(prediction);
    annoP           = BufferedImageHelper.getPixels(annotation);
    black           = Color.BLACK.getRGB();
    annotationCount = 0;
    additionalCount = 0;
    missedCount     = 0;
    overlapCount    = 0;
    for (i = 0; i < predP.length; i++) {
      if (annoP[i] != black)
        annotationCount++;
      if (predP[i] == annoP[i]) {
	if (predP[i] != black)
	  overlapCount++;
      }
      else {
	if (predP[i] != black)
	  additionalCount++;
	else if (predP[i] == black)
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
   * Performs the actual processing of the containers.
   *
   * @param containers the containers to process
   * @return the generated image(s)
   */
  @Override
  protected Object doProcess(ImageSegmentationContainer[] containers) {
    SpreadSheet			result;
    Row				row;
    ImageSegmentationContainer	pred;
    ImageSegmentationContainer	anno;
    Map<String,BufferedImage>	predL;
    Map<String,BufferedImage>	annoL;
    List<String> 		labels;
    Map<String,Integer>		stats;
    int				total;
    BufferedImage		predI;
    BufferedImage		annoI;

    pred  = containers[0];
    predL = (Map<String,BufferedImage>) pred.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    anno  = containers[1];
    annoL = (Map<String,BufferedImage>) anno.getValue(ImageSegmentationContainer.VALUE_LAYERS);

    labels = new ArrayList<>(annoL.keySet());
    Collections.sort(labels);

    result = new DefaultSpreadSheet();
    result.addComment("Name: " + pred.getValue(ImageSegmentationContainer.VALUE_NAME));

    // header
    row    = result.getHeaderRow();
    row.addCell("L").setContentAsString("Label");
    row.addCell("O").setContentAsString("Overlap");
    row.addCell("OP").setContentAsString("Overlap (perc)");
    row.addCell("M").setContentAsString("Missed");
    row.addCell("MP").setContentAsString("Missed (perc)");
    row.addCell("A").setContentAsString("Additional");
    row.addCell("AP").setContentAsString("Additional (perc)");

    // data
    for (String label: labels) {
      row = result.addRow();
      row.getCell("L").setContentAsString(label);

      predI = predL.get(label);
      annoI = annoL.get(label);
      stats = compare(predI, annoI);
      total = stats.get(KEY_ANNOTATION);

      row.getCell("O").setContent(stats.get(KEY_OVERLAP));
      row.getCell("OP").setContent((double) stats.get(KEY_OVERLAP) / total);
      row.getCell("M").setContent(stats.get(KEY_MISSED));
      row.getCell("MP").setContent((double) stats.get(KEY_MISSED) / total);
      row.getCell("A").setContent(stats.get(KEY_ADDITIONAL));
      row.getCell("AP").setContent((double) stats.get(KEY_ADDITIONAL) / total);
    }

    return result;
  }
}
