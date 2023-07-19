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
 * JAIImageReader.java
 * Copyright (C) 2014-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;
import adams.data.io.output.ImageWriter;
import adams.data.io.output.JAIImageWriter;

import javax.imageio.ImageIO;
import java.io.InputStream;

/**
 <!-- globalinfo-start -->
 * Java Advanced Imaging (JAI) image reader for: jpg, bmp, gif, png, jpeg, wbmp
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-add-meta-data &lt;boolean&gt; (property: addMetaData)
 * &nbsp;&nbsp;&nbsp;If enabled, any available meta-data gets added the image report.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JAIImageReader
  extends AbstractImageReader<BufferedImageContainer>
  implements InputStreamImageReader<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 8416312222136406140L;

  /** whether to add the meta-data. */
  protected boolean m_AddMetaData;

  /** the format extensions. */
  protected String[] m_FormatExtensions;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Java Advanced Imaging (JAI) image reader for: " + Utils.flatten(getFormatExtensions(), ", ");
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "add-meta-data", "addMetaData",
      false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FormatExtensions = ImageIO.getReaderFileSuffixes();
  }

  /**
   * Sets whether to add the meta-data.
   *
   * @param value	true if to add meta-data
   */
  public void setAddMetaData(boolean value) {
    m_AddMetaData = value;
    reset();
  }

  /**
   * Returns whether to add the meta-data.
   *
   * @return		true if to add the meta-data
   */
  public boolean getAddMetaData() {
    return m_AddMetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addMetaDataTipText() {
    return "If enabled, any available meta-data gets added the image report.";
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
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  @Override
  public ImageWriter getCorrespondingWriter() {
    return new JAIImageWriter();
  }

  /**
   * Returns the reader for the default extension.
   * 
   * @return		the JAI reader
   * @see		#getDefaultFormatExtension()
   */
  public javax.imageio.ImageReader getReader() {
    return BufferedImageHelper.getReaderForExtension(getDefaultFormatExtension());
  }

  /**
   * Performs the actual reading of the image file.
   * 
   * @param file	the file to read
   * @return		the image container, null if failed to read
   */
  @Override
  protected BufferedImageContainer doRead(PlaceholderFile file) {
    return BufferedImageHelper.read(file, m_AddMetaData);
  }

  /**
   * Reads the image from the stream. Caller must close the stream.
   *
   * @param stream the stream to read from
   * @return the image container, null if failed to read
   */
  @Override
  public BufferedImageContainer read(InputStream stream) {
    return BufferedImageHelper.read(stream, m_AddMetaData);
  }
}
