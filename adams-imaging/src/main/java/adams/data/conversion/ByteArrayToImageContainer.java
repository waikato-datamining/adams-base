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
 * ByteArrayToImageContainer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.data.image.AbstractImageContainer;
import adams.data.io.input.InputStreamImageReader;
import adams.data.io.input.JAIImageReader;

import java.io.ByteArrayInputStream;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ByteArrayToImageContainer
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 7104673258581645263L;

  /** the reader to use. */
  protected InputStreamImageReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a byte array into an image container using the specified reader.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new JAIImageReader());
  }

  /**
   * Sets the image reader to use.
   *
   * @param value	the reader
   */
  public void setReader(InputStreamImageReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the image reader to use.
   *
   * @return		the reader
   */
  public InputStreamImageReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The image reader to use for reading the byte array into an image container.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "reader", m_Reader, "reader: ");
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return byte[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return the class
   */
  @Override
  public Class generates() {
    return AbstractImageContainer.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    AbstractImageContainer  result;
    ByteArrayInputStream    stream;

    stream = new ByteArrayInputStream((byte[]) m_Input);
    result = m_Reader.read(stream);
    if (result == null)
      throw new IllegalStateException("Failed to read image from byte array!");

    return result;
  }
}
