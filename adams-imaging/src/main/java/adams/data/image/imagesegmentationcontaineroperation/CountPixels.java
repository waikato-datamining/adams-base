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

package adams.data.image.imagesegmentationcontaineroperation;

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
 * Counts the pixels and generates a spreadsheet with count per layer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CountPixels
  extends AbstractImageSegmentationContainerOperation {

  private static final long serialVersionUID = 5451678654384977453L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts the pixels and generates a spreadsheet with count per layer.";
  }

  /**
   * Returns the minimum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no lower limit
   */
  @Override
  public int minNumContainersRequired() {
    return 1;
  }

  /**
   * Returns the maximum number of containers that are required for the operation.
   *
   * @return the number of containers that are required, <= 0 means no upper limit
   */
  @Override
  public int maxNumContainersRequired() {
    return 1;
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
   * Counts the non-black pixels.
   *
   * @param pixels	the pixels to analyze
   * @return		the statistics
   */
  protected int count(int[] pixels) {
    int 	result;
    int		i;
    int		black;

    black  = Color.BLACK.getRGB();
    result = 0;
    for (i = 0; i < pixels.length; i++) {
      if (pixels[i] != black)
        result++;
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
    SpreadSheet			result;
    Row				row;
    ImageSegmentationContainer 	cont;
    Map<String,BufferedImage> 	layers;
    Map<String,int[]> 		pixels;
    List<String> 		labels;

    cont   = containers[0];
    layers = (Map<String,BufferedImage>) cont.getValue(ImageSegmentationContainer.VALUE_LAYERS);
    labels = new ArrayList<>(layers.keySet());
    Collections.sort(labels);

    pixels = new HashMap<>();
    for (String label: labels) {
      if (layers.containsKey(label))
        pixels.put(label, BufferedImageHelper.getPixels(layers.get(label)));
    }

    result = new DefaultSpreadSheet();
    result.addComment("Name: " + cont.getValue(ImageSegmentationContainer.VALUE_NAME));

    // header
    row    = result.getHeaderRow();
    row.addCell("L").setContentAsString("Layer");
    row.addCell("C").setContentAsString("Count");

    // data
    for (String label: labels) {
      row = result.addRow();
      row.getCell("L").setContentAsString(label);
      row.getCell("C").setContent(count(pixels.get(label)));
    }

    return result;
  }
}
