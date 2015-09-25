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
 * BufferedImageToOpenIMAJ.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.data.image.AbstractImageContainer;
import adams.data.image.BufferedImageContainer;
import adams.data.openimaj.OpenIMAJImageContainer;
import adams.data.openimaj.OpenIMAJImageType;
import org.openimaj.image.ImageUtilities;

/**
 <!-- globalinfo-start -->
 * Turns a BufferedImage container into an OpenIMAJ one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-image-type &lt;FIMAGE|MBFIMAGE&gt; (property: imageType)
 * &nbsp;&nbsp;&nbsp;The OpenIMAJ image type to use.
 * &nbsp;&nbsp;&nbsp;default: MBFIMAGE
 * </pre>
 * 
 * <pre>-alpha &lt;boolean&gt; (property: alpha)
 * &nbsp;&nbsp;&nbsp;Whether to include an alpha channel in case of multi-band images.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 11709 $
 */
public class BufferedImageToOpenIMAJ
  extends AbstractConversion
  implements BufferedImageToOtherFormatConversion {

  /** for serialization. */
  private static final long serialVersionUID = 267299130050379610L;

  /** the image type to generate. */
  protected OpenIMAJImageType m_ImageType;

  /** whether to add an alpha channel for multi-band images. */
  protected boolean m_Alpha;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a BufferedImage container into an OpenIMAJ one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-type", "imageType",
      OpenIMAJImageType.MBFIMAGE);

    m_OptionManager.add(
      "alpha", "alpha",
      false);
  }

  /**
   * Sets the image type to use.
   *
   * @param value	the type
   */
  public void setImageType(OpenIMAJImageType value) {
    m_ImageType = value;
    reset();
  }

  /**
   * Returns the image type to use.
   *
   * @return		the type
   */
  public OpenIMAJImageType getImageType() {
    return m_ImageType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageTypeTipText() {
    return "The OpenIMAJ image type to use.";
  }

  /**
   * Sets whether to use an alpha channel in case of multi-band images.
   *
   * @param value	true if to use alpha channel
   */
  public void setAlpha(boolean value) {
    m_Alpha = value;
    reset();
  }

  /**
   * Returns whether to use an alpha channel in case of multi-band images.
   *
   * @return		true if to use alpha channel
   */
  public boolean getAlpha() {
    return m_Alpha;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alphaTipText() {
    return "Whether to include an alpha channel in case of multi-band images.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return AbstractImageContainer.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return OpenIMAJImageContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    OpenIMAJImageContainer	result;
    BufferedImageContainer	input;

    input  = (BufferedImageContainer) m_Input;
    result = new OpenIMAJImageContainer();
    result.setReport(input.getReport().getClone());
    result.setNotes(input.getNotes().getClone());

    switch (m_ImageType) {
      case FIMAGE:
	result.setImage(ImageUtilities.createFImage(input.toBufferedImage()));
	break;

      case MBFIMAGE:
	result.setImage(ImageUtilities.createMBFImage(input.toBufferedImage(), m_Alpha));
	break;

      default:
	throw new IllegalStateException("Unhandled image type: " + m_ImageType);
    }

    return result;
  }
}
