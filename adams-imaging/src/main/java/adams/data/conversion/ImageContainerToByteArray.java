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
 * ImageContainerToByteArray.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.io.output.JAIImageWriter;
import adams.data.io.output.OutputStreamImageWriter;

import java.io.ByteArrayOutputStream;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageContainerToByteArray
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 7104673258581645263L;

  /** the writer to use. */
  protected OutputStreamImageWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns an image container into a byte array using the specified writer.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "writer", "writer",
      new JAIImageWriter());
  }

  /**
   * Sets the image writer to use.
   *
   * @param value	the writer
   */
  public void setWriter(OutputStreamImageWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the image writer to use.
   *
   * @return		the writer
   */
  public OutputStreamImageWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The image writer to use for turning the image into a byte array.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer, "writer: ");
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
   * @return the class
   */
  @Override
  public Class generates() {
    return byte[].class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    ByteArrayOutputStream   	stream;
    String			msg;

    stream = new ByteArrayOutputStream();
    msg    = m_Writer.write(stream, (AbstractImageContainer) m_Input);
    if (msg != null)
      throw new IllegalStateException(msg);

    return stream.toByteArray();
  }
}
