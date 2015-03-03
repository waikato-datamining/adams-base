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
 * Pixels.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.data.imagej.features;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.imagej.ImagePlusContainer;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Gets all the pixels of the image.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
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
 * @version $Revision$
 */
public class Pixels
  extends AbstractImageJFeatureGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8349656592325229512L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Gets all the pixels of the image.";
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(ImagePlusContainer img) {
    HeaderDefinition		result;
    int				i;
    int				numPixels;

    result    = new HeaderDefinition();
    numPixels = img.getWidth() * img.getHeight();
    for (i = 0; i < numPixels; i++)
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
  public List<Object>[] generateRows(ImagePlusContainer img) {
    List<Object>[]	result;
    Object		pixels;
    int			i;

    result    = null;
    pixels    = img.getImage().getProcessor().getPixels();
    result    = new List[1];
    result[0] = new ArrayList<Object>();
    for (i = 0; i < Array.getLength(pixels); i++)
      result[0].add(Array.getDouble(pixels, i));

    return result;
  }
}
