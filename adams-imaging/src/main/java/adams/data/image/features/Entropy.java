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
 * Entropy.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image.features;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adams.core.License;
import adams.core.annotation.MixedCopyright;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Calculates the Shannon entropy of an image.<br>
 * <br>
 * Original code taken from here:<br>
 * http:&#47;&#47;stackoverflow.com&#47;a&#47;22280200
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
 * <pre>-step-size &lt;int&gt; (property: stepSize)
 * &nbsp;&nbsp;&nbsp;The step size to use (ie every n-th column&#47;row).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision: 9196 $
 */
@MixedCopyright(
    license = License.CC_BY_SA_3,
    author = "chathan - http://stackoverflow.com/users/2556447/chathan",
    url = "http://stackoverflow.com/a/22280200"
)
public class Entropy
  extends AbstractBufferedImageFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /** the step size to use (every n-th column/row). */
  protected int m_StepSize;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Calculates the Shannon entropy of an image.\n\n"
	+ "Original code taken from here:\n"
	+ "http://stackoverflow.com/a/22280200";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "step-size", "stepSize",
	    1, 1, null);
  }

  /**
   * Sets the step size (every n-th column/row).
   *
   * @param value	the step size
   */
  public void setStepSize(int value) {
    m_StepSize = value;
    reset();
  }

  /**
   * Returns the step size (every n-th column/row).
   *
   * @return		the step size
   */
  public int getStepSize() {
    return m_StepSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String stepSizeTipText() {
    return "The step size to use (ie every n-th column/row).";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(BufferedImageContainer img) {
    HeaderDefinition	result;

    result = new HeaderDefinition();
    result.add("Entropy", DataType.NUMERIC);

    return result;
  }
  
  /**
   * Calculates the Shannon entropy.
   * 
   * @param img		the image to calculate the entropy for
   * @return		the entropy
   */
  protected double calculateShannonEntropy(BufferedImage img){
    double 			result;
    List<String> 		values;
    int 			n;
    Map<Integer, Integer> 	occ;
    int				i;
    int				j;
    int 			pixel;
    int 			red;
    int 			green;
    int 			blue;
    int 			d;
    double 			p;
    
    values = new ArrayList<String>();
    n      = 0;
    occ    = new HashMap<Integer, Integer>();
    for (i = 0; i < img.getHeight(); i += m_StepSize) {
      for (j = 0; j < img.getWidth(); j += m_StepSize) {
	pixel = img.getRGB(j, i);
	red   = (pixel >> 16) & 0xff;
	green = (pixel >> 8) & 0xff;
	blue  = (pixel) & 0xff;
	//0.2989 * R + 0.5870 * G + 0.1140 * B greyscale conversion
	d = (int) Math.round(0.2989 * red + 0.5870 * green + 0.1140 * blue);
	if (!values.contains(String.valueOf(d)))
	  values.add(String.valueOf(d));
	if (occ.containsKey(d))
	  occ.put(d, occ.get(d) + 1);
	else
	  occ.put(d, 1);
	++n;
      }
    }
    
    result = 0.0;
    for (Map.Entry<Integer, Integer> entry: occ.entrySet()) {
      p       = (double) entry.getValue() / n;
      result += p * ((double) (Math.log(p) / Math.log(2.0)));
    }
    
    return -result;
  }

  /**
   * Performs the actual feature generation.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(BufferedImageContainer img) {
    List<Object>[]		result;
    BufferedImage		image;

    result    = null;
    image     = BufferedImageHelper.convert(img.getImage(), BufferedImage.TYPE_4BYTE_ABGR);
    result    = new List[1];
    result[0] = new ArrayList<Object>();
    result[0].add(calculateShannonEntropy(image));

    return result;
  }
}
