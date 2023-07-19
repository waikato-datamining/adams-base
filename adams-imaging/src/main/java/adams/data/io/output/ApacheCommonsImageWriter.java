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
 * ApacheCommonsImageWriter.java
 * Copyright (C) 2019-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.ApacheCommonsImageReader;
import adams.data.io.input.ImageReader;
import org.apache.commons.imaging.ImageFormat;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;

import java.io.OutputStream;
import java.util.HashMap;

/**
 <!-- globalinfo-start -->
 * Apache Commons Imaging image writer for: bmp, gif, ico, pbm, pgm, png, pnm, ppm, tif, tiff
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ApacheCommonsImageWriter
  extends AbstractImageWriter<BufferedImageContainer>
  implements OutputStreamImageWriter<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;

  /** the image format to use (uses file extension to determine it if UNKNOWN). */
  protected ImageFormats m_ImageFormat;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Apache Commons Imaging writer for: " + Utils.flatten(getFormatExtensions(), ", ");
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-format", "imageFormat",
      ImageFormats.UNKNOWN);
  }

  /**
   * Sets the image format to use.
   * Requires an explicit format (other than {@link ImageFormats#UNKNOWN}) when writing to a stream.
   *
   * @param value 	the format
   */
  public void setImageFormat(ImageFormats value) {
    m_ImageFormat = value;
    reset();
  }

  /**
   * Returns the image format to use.
   * Requires an explicit format (other than {@link ImageFormats#UNKNOWN}) when writing to a stream.
   *
   * @return 		the format
   */
  public ImageFormats getImageFormat() {
    return m_ImageFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageFormatTipText() {
    return "The image format to use; if set to UNKNOWN it will use the file extension to determine the format automatically; when writing to an output stream requires explicit format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Apache Commons Imaging writer";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"bmp", "dcx", "gif", "hdr", "icns", "ico", "jpg", "pcx", "png", "pnm", "psd", "tif", "tiff", "wbmp", "xbm", "xpm"};
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public ImageReader getCorrespondingReader() {
    return new ApacheCommonsImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   *
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    String	result;
    String	ext;
    ImageFormat format;

    result = null;

    // determine file format if necessary
    format = m_ImageFormat;
    if (m_ImageFormat == ImageFormats.UNKNOWN) {
      ext = FileUtils.getExtension(file);
      switch (ext) {
	case "bmp":
	  format = ImageFormats.BMP;
	  break;
	case "gif":
	  format = ImageFormats.GIF;
	  break;
	case "hdr":
	  format = ImageFormats.RGBE;
	  break;
	case "ico":
	  format = ImageFormats.ICO;
	  break;
	case "icns":
	  format = ImageFormats.ICNS;
	  break;
	case "jpg":
	case "jpeg":
	  format = ImageFormats.JPEG;
	  break;
	case "pcx":
	  format = ImageFormats.PCX;
	  break;
	case "png":
	  format = ImageFormats.PNG;
	  break;
	case "pnm":
	  format = ImageFormats.PNM;
	  break;
	case "psd":
	  format = ImageFormats.PSD;
	  break;
	case "tif":
	case "tiff":
	  format = ImageFormats.TIFF;
	  break;
	case "wbmp":
	  format = ImageFormats.WBMP;
	  break;
	case "xbm":
	  format = ImageFormats.XBM;
	  break;
	case "xpm":
	  format = ImageFormats.XPM;
	  break;
	default:
	  format = null;
      }
      if (format == null)
	result = "Unhandled file extension: " + ext;
    }

    if (result == null) {
      try {
	Imaging.writeImage(cont.toBufferedImage(), file.getAbsoluteFile(), format, new HashMap<>());
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to write image to: " + file, e);
      }
    }

    return result;
  }

  /**
   * Writes the image to the stream. Callers must close the stream.
   *
   * @param stream the stream to write to
   * @param cont   the image container to write
   * @return null if successfully written, otherwise error message
   */
  @Override
  public String write(OutputStream stream, BufferedImageContainer cont) {
    String	result;

    result = null;

    if (m_ImageFormat == ImageFormats.UNKNOWN)
      result = "Writing to a stream requires an explicit image format!";

    if (result == null) {
      try {
	Imaging.writeImage(cont.toBufferedImage(), stream, m_ImageFormat, new HashMap<>());
      }
      catch (Exception e) {
	result = LoggingHelper.handleException(this, "Failed to write image to stream!", e);
      }
    }

    return result;
  }
}
