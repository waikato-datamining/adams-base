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
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.flattener;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.image.BufferedImageHelper;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageFloat64;
import boofcv.struct.image.ImageInteger;
import boofcv.struct.image.ImageSInt64;

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
  extends AbstractBoofCVFlattener {

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
  public Instances createHeader(BoofCVImageContainer img) {
    Instances			result;
    ArrayList<Attribute>	atts;
    int				i;
    int				numPixels;

    numPixels = img.getWidth() * img.getHeight();
    atts      = new ArrayList<Attribute>();
    for (i = 0; i < numPixels; i++)
      atts.add(new Attribute("att_" + (i+1)));
    result = new Instances(getClass().getName(), atts, 0);

    return result;
  }

  /**
   * Performs the actual flattening of the image.
   *
   * @param img		the image to process
   * @return		the generated array
   */
  @Override
  public Instance[] doFlatten(BoofCVImageContainer img) {
    Instance[]		result;
    double[]		values;
    int[]		pixels;
    int			i;
    int			n;
    ImageInteger	integer;
    ImageFloat32	float32;
    ImageFloat64	float64;
    ImageSInt64		sInt64;
    BufferedImage	buff;
    int			height;
    int			width;

    result = null;
    
    values = newArray(m_Header.numAttributes());
    height = img.getHeight();
    width  = img.getWidth();
    
    if (img.getImage() instanceof ImageInteger) {
      integer = (ImageInteger) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  values[n*height + i] = integer.get(i, n);
      }
    }
    else if (img.getImage() instanceof ImageFloat32) {
      float32 = (ImageFloat32) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  values[n*height + i] = float32.get(i, n);
      }
    }
    else if (img.getImage() instanceof ImageFloat64) {
      float64 = (ImageFloat64) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  values[n*height + i] = float64.get(i, n);
      }
    }
    else if (img.getImage() instanceof ImageSInt64) {
      sInt64 = (ImageSInt64) img.getImage();
      for (n = 0; n < height; n++) {
	for (i = 0; i < width; i++)
	  values[n*height + i] = sInt64.get(i, n);
      }
    }
    else {
      buff   = img.toBufferedImage();
      pixels = BufferedImageHelper.getPixels(buff);
      for (i = 0; i < pixels.length; i++)
	values[i] = pixels[i];
    }

    result = new Instance[]{new DenseInstance(1.0, values)};
    result[0].setDataset(m_Header);

    return result;
  }
}
