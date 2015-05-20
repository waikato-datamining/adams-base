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
 * Histogram.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.features;

import java.util.ArrayList;
import java.util.List;

import adams.data.boofcv.BoofCVImageContainer;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;
import boofcv.alg.misc.ImageStatistics;
import boofcv.struct.image.ImageUInt8;

/**
 <!-- globalinfo-start -->
 * Generates a histogram from the image. Supported image types:<br>
 * boofcv.struct.image.ImageUInt8
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheetFeatureConverter -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9598 $
 */
public class Histogram
  extends AbstractBoofCVFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates a histogram from the image. Supported image types:\n" 
	+ ImageUInt8.class.getName();
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BoofCVImageContainer img) {
    HeaderDefinition	result;
    int			i;
    int			numHistogram;

    if (img.getImage() instanceof ImageUInt8) {
      numHistogram = 256;
    }
    else {
      throw new IllegalStateException("Unhandled image type: " + img.getImage().getClass().getName());
    }

    result = new HeaderDefinition();
    for (i = 0; i < numHistogram; i++)
      result.add("att_" + (i+1), DataType.NUMERIC);

    return result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BoofCVImageContainer img) {
    List<Object>[]	result;
    int[]		histogram;

    result    = new List[1];
    result[0] = new ArrayList<Object>();

    if (img.getImage() instanceof ImageUInt8) {
      histogram = new int[256];
      ImageStatistics.histogram((ImageUInt8) img.getImage(), histogram);
      for (int value: histogram)
	result[0].add(value);
    }
    else {
      throw new IllegalStateException("Unhandled image type: " + img.getImage().getClass().getName());
    }

    return result;
  }
}
