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
 * OpenIMAJImageReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.output.AbstractImageWriter;
import adams.data.io.output.OpenIMAJImageWriter;
import adams.data.openimaj.OpenIMAJImageContainer;
import adams.data.openimaj.OpenIMAJImageType;
import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;

/**
 <!-- globalinfo-start -->
 * OpenIMAJ image reader for: png, *<br>
 * For more information see:<br>
 * http:&#47;&#47;www.openimaj.org&#47;
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
 * &nbsp;&nbsp;&nbsp;The OpenIMAJ image type to use when reading the image.
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenIMAJImageReader
  extends AbstractImageReader<OpenIMAJImageContainer> {

  private static final long serialVersionUID = -3454639551353467146L;

  /** the imagetype to read. */
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
    return
	"OpenIMAJ image reader for: " + Utils.flatten(getFormatExtensions(), ", ")
	+ "\n"
	+ "For more information see:\n"
	+ "http://www.openimaj.org/";
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
   * Sets the image type to read.
   *
   * @param value	the type
   */
  public void setImageType(OpenIMAJImageType value) {
    m_ImageType = value;
    reset();
  }

  /**
   * Returns the image type to read.
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
    return "The OpenIMAJ image type to use when reading the image.";
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
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "OpenIMAJ images";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"png", "*"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public AbstractImageWriter getCorrespondingWriter() {
    return new OpenIMAJImageWriter();
  }

  /**
   * Performs the actual reading of the image file.
   *
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  @Override
  protected OpenIMAJImageContainer doRead(PlaceholderFile file) {
    OpenIMAJImageContainer	result;
    Image			img;

    result = new OpenIMAJImageContainer();

    try {
      switch (m_ImageType) {
	case FIMAGE:
	  img = ImageUtilities.readF(file.getAbsoluteFile());
	  result.setImage(img);
	  break;

	case MBFIMAGE:
	  if (m_Alpha)
	    img = ImageUtilities.readMBFAlpha(file.getAbsoluteFile());
	  else
	    img = ImageUtilities.readMBF(file.getAbsoluteFile());
	  result.setImage(img);
	  break;

	default:
	  throw new IllegalStateException("Unhandled image type: " + m_ImageType);
      }
    }
    catch (Exception e) {
      Utils.handleException(this, "Failed to read image as " + m_ImageType + ": " + file, e);
      result = null;
    }

    return result;
  }
}
