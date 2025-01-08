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
 * JAIExplicitImageWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.data.image.BufferedImageContainer;
import adams.data.io.input.ImageReader;
import adams.data.io.input.JAIImageReader;
import adams.data.io.output.jaiwriter.AbstractJAIWriter;
import adams.data.io.output.jaiwriter.JPEGWriter;

import java.io.OutputStream;

/**
 <!-- globalinfo-start -->
 * Java Advanced Imaging (JAI) image writer using explicit image type writers.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-writer &lt;adams.data.io.output.jaiwriter.AbstractJAIWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.output.jaiwriter.JPEGWriter
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JAIExplicitImageWriter
  extends AbstractImageWriter<BufferedImageContainer>
  implements OutputStreamImageWriter<BufferedImageContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 6385191315392140321L;

  /** the actual writer to use. */
  protected AbstractJAIWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Java Advanced Imaging (JAI) image writer using explicit image type writers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "writer", "writer",
      new JPEGWriter());
  }

  /**
   * Sets the writer to use.
   *
   * @param value 	the writer
   */
  public void setWriter(AbstractJAIWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer to use.
   *
   * @return 		the writer
   */
  public AbstractJAIWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JAI (explicit)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return m_Writer.getFormatExtensions();
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
   * Performs the actual writing of the image file.
   *
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, BufferedImageContainer cont) {
    return m_Writer.write(file, cont.getImage());
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
    return m_Writer.write(stream, cont.getImage());
  }
}
