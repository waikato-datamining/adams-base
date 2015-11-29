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
 * DefaultFlowReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.base.BaseCharset;
import adams.core.io.FileEncodingSupporter;
import adams.core.option.NestedConsumer;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;

import java.io.File;

/**
 * Reads flows in the default format (nested).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultFlowReader
  extends AbstractFlowReader
  implements FileEncodingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4618819455357416453L;

  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads flows in the default format (nested).";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Flow file";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"flow", "flow.gz"};
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  protected InputType getInputType() {
    return InputType.FILE;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());
  }

  /**
   * Sets the encoding to use.
   *
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }

  /**
   * Returns the encoding to use.
   *
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when reading the file, use empty string for default.";
  }

  /**
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   */
  @Override
  protected Actor doRead(File file) {
    AbstractActor	result;
    NestedConsumer	consumer;

    consumer = new NestedConsumer();
    consumer.setEncoding(m_Encoding);
    result   = (AbstractActor) consumer.read(file.getAbsolutePath());

    // transfer errors/warnings
    m_Errors.addAll(consumer.getErrors());
    m_Warnings.addAll(consumer.getWarnings());

    return result;
  }
}
