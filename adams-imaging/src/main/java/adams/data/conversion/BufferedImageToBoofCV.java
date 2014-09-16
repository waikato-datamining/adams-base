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

/**
 * BufferedImageToBoofCV.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.BoofCVHelper;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.boofcv.BoofCVImageType;
import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;

/**
 <!-- globalinfo-start -->
 * Turns a BufferedImage container into a BoofCV one.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-image-type &lt;FLOAT_32|FLOAT_64|SIGNED_INT_8|UNSIGNED_INT_8|SIGNED_INT_16|UNSIGNED_INT_16|SIGNED_INT_32|SIGNED_INT_64&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The BoofCV image type to convert to.
 * &nbsp;&nbsp;&nbsp;default: FLOAT_32
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BufferedImageToBoofCV
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 267299130050379610L;

  /** the image type to generate. */
  protected BoofCVImageType m_ImageType;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a BufferedImage container into a BoofCV one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "image-type", "imageType",
	    BoofCVImageType.FLOAT_32);
  }

  /**
   * Sets the image type to convert to.
   *
   * @param value 	the image type
   */
  public void setImageType(BoofCVImageType value) {
    m_ImageType = value;
    reset();
  }

  /**
   * Returns the image type to conver to.
   *
   * @return 		the type
   */
  public BoofCVImageType getImageType() {
    return m_ImageType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTypeTipText() {
    return "The BoofCV image type to convert to.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return BufferedImageContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return BoofCVImageContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    return BoofCVHelper.toBoofCVImageContainer((AbstractImageContainer) m_Input, m_ImageType);
  }
}
