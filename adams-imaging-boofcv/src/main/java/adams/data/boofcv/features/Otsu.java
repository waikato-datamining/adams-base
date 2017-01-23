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
 * Otsu.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.features;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Computes the variance based threshold using Otsu's method from an input image (gray scale; boofcv.struct.image.GrayU8).<br>
 * <br>
 * For more information see:<br>
 * WikiPedia. Otsu's method.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Otsu's method},
 *    HTTP = {https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Otsu%27s_method}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheet -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
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
 * <pre>-min &lt;int&gt; (property: min)
 * &nbsp;&nbsp;&nbsp;The minimum value to use in the computation (included).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-max &lt;int&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;The maximum value to use in the computation (excluded).
 * &nbsp;&nbsp;&nbsp;default: 256
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9598 $
 */
public class Otsu
  extends AbstractBoofCVFeatureGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the min value. */
  protected int m_Min;

  /** the max value. */
  protected int m_Max;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Computes the variance based threshold using Otsu's method from "
	+ "an input image (gray scale; " + GrayU8.class.getName() + ").\n\n"
	+ "For more information see:\n"
	+ getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Otsu's method");
    result.setValue(Field.HTTP, "https://en.wikipedia.org/wiki/Otsu%27s_method");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min", "min",
	    0, 0, null);

    m_OptionManager.add(
	    "max", "max",
	    256, 1, null);
  }

  /**
   * Sets the minimum value for the computation.
   *
   * @param value	the minimum (incl)
   */
  public void setMin(int value) {
    m_Min = value;
    reset();
  }

  /**
   * Returns the minimum value for the computation.
   *
   * @return		the minimum (incl)
   */
  public int getMin() {
    return m_Min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minTipText() {
    return "The minimum value to use in the computation (included).";
  }

  /**
   * Sets the maximum value for the computation.
   *
   * @param value	the maximum (excl)
   */
  public void setMax(int value) {
    m_Max = value;
    reset();
  }

  /**
   * Returns the maximum value for the computation.
   *
   * @return		the maximum (excl)
   */
  public int getMax() {
    return m_Max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String maxTipText() {
    return "The maximum value to use in the computation (excluded).";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BoofCVImageContainer img) {
    HeaderDefinition		result;

    result = new HeaderDefinition();
    result.add("Otsu", DataType.NUMERIC);

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
    ImageBase		gray;

    result    = new List[1];
    result[0] = new ArrayList<Object>();

    gray = BoofCVHelper.toBoofCVImage(img.getImage(), BoofCVImageType.UNSIGNED_INT_8);
    result[0].add(GThresholdImageOps.computeOtsu((GrayU8) gray, m_Min, m_Max));

    return result;
  }
}
