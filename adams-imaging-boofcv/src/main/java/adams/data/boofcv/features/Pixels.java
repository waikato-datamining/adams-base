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
 * Copyright (C) 2013-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.features;

import adams.data.boofcv.BoofCVImageContainer;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.image.BufferedImageHelper;
import adams.data.report.DataType;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayF64;
import boofcv.struct.image.GrayI;
import boofcv.struct.image.GrayS64;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Gets all the pixels of the image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
    return "Gets all the pixels of the image.";
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
    int				i;
    int				numPixels;

    numPixels = img.getWidth() * img.getHeight();
    result    = new HeaderDefinition();
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
  public List<Object>[] generateRows(BoofCVImageContainer img) {
    List<Object>[]	result;
    int[]		pixels;
    int			i;
    int			n;
    GrayI	integer;
    GrayF32	float32;
    GrayF64	float64;
    GrayS64		sInt64;
    BufferedImage	buff;
    int			height;
    int			width;

    result    = new List[1];
    result[0] = new ArrayList<Object>();
    height    = img.getHeight();
    width     = img.getWidth();
    
    if (img.getImage() instanceof GrayI) {
      integer = (GrayI) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  result[0].add(integer.get(i, n));
      }
    }
    else if (img.getImage() instanceof GrayF32) {
      float32 = (GrayF32) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  result[0].add(float32.get(i, n));
      }
    }
    else if (img.getImage() instanceof GrayF64) {
      float64 = (GrayF64) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  result[0].add(float64.get(i, n));
      }
    }
    else if (img.getImage() instanceof GrayS64) {
      sInt64 = (GrayS64) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  result[0].add(sInt64.get(i, n));
      }
    }
    else {
      buff   = img.toBufferedImage();
      pixels = BufferedImageHelper.getPixels(buff);
      for (i = 0; i < pixels.length; i++)
	result[0].add(pixels[i]);
    }

    return result;
  }
}
