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
 * ImageType.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.boofcv.transformer;

import adams.core.QuickInfoHelper;
import adams.data.boofcv.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import boofcv.struct.image.ImageBase;

/**
 <!-- globalinfo-start -->
 * Turns an image into the specified type of image.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-type &lt;FLOAT_32|FLOAT_64|SIGNED_INT_8|UNSIGNED_INT_8|SIGNED_INT_16|UNSIGNED_INT_16|SIGNED_INT_32|SIGNED_INT_64&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of image to convert to.
 * &nbsp;&nbsp;&nbsp;default: FLOAT_32
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8954 $
 */
public class ImageType
  extends AbstractBoofCVTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 2959486760492196174L;

  /** the image type. */
  protected BoofCVImageType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an image into the specified type of image.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    BoofCVImageType.FLOAT_32);
  }

  /**
   * Sets the type of image to convert to.
   *
   * @param value	the type
   */
  public void setType(BoofCVImageType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of image to convert to.
   *
   * @return		the type
   */
  public BoofCVImageType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String typeTipText() {
    return "The type of image to convert to.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }
  
  /**
   * Performs no transformation at all, just returns the input.
   *
   * @param img		the image to process (can be modified, since it is a copy)
   * @return		the copy of the image
   */
  @Override
  protected BoofCVImageContainer[] doTransform(BoofCVImageContainer img) {
    BoofCVImageContainer[]	result;
    ImageBase 			image;

    result     = new BoofCVImageContainer[1];
    image      = BoofCVHelper.toBoofCVImage(img.getImage(), m_Type);
    result[0]  = (BoofCVImageContainer) img.getHeader();
    result[0].setImage(image);

    return result;
  }
}
