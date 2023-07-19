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
 * JAIImageWriter.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.input.ImageReader;
import adams.data.io.input.JAIImageReader;

import javax.imageio.ImageIO;
import java.io.OutputStream;

/**
 <!-- globalinfo-start -->
 * Java Advanced Imaging (JAI) image writer for: bmp, jpg, jpeg, wbmp, png, gif
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
public class JAIImageWriter
  extends AbstractImageWriter<BufferedImageContainer>
  implements OutputStreamImageWriter<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;

  /** the image format to use (uses file extension to determine it if empty). */
  protected String m_ImageFormat;

  /** the format extensions. */
  protected String[] m_FormatExtensions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Java Advanced Imaging (JAI) image writer for: " + Utils.flatten(getFormatExtensions(), ", ");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FormatExtensions = ImageIO.getWriterFileSuffixes();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "image-format", "imageFormat",
      "");
  }

  /**
   * Sets the image format to use.
   * Requires an explicit format (other than empty string) when writing to a stream.
   *
   * @param value 	the format
   */
  public void setImageFormat(String value) {
    m_ImageFormat = value;
    reset();
  }

  /**
   * Returns the image format to use.
   * Requires an explicit format (other than empty string) when writing to a stream.
   *
   * @return 		the format
   */
  public String getImageFormat() {
    return m_ImageFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String imageFormatTipText() {
    return "The image format to use; if empty string then it will use the file extension to determine the format automatically; when writing to an output stream requires explicit format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JAI";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_FormatExtensions;
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  @Override
  public ImageReader getCorrespondingReader() {
    return new JAIImageReader();
  }

  /**
   * Returns the reader for the default extension.
   * 
   * @return		the JAI reader
   * @see		#getDefaultFormatExtension()
   */
  public javax.imageio.ImageWriter getWriter() {
    return BufferedImageHelper.getWriterForExtension(getDefaultFormatExtension());
  }

  /**
   * Performs the actual writing of the image file.
   * 
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    return BufferedImageHelper.write(cont.getImage(), m_ImageFormat, file);
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
    if (m_ImageFormat.isEmpty())
      return "Writing to a stream requires an explicit format, like jpg or png!";
    else
      return BufferedImageHelper.write(cont.getImage(), m_ImageFormat, stream);
  }
}
