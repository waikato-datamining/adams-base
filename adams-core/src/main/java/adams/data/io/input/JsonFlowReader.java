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
 * JsonFlowReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.File;

import adams.core.option.JsonConsumer;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;

/**
 * Reads flows in JSON format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonFlowReader
  extends AbstractFlowReader {

  /** for serialization. */
  private static final long serialVersionUID = 4618819455357416453L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads flows in JSON format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JSON file";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"json", "json.gz"};
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
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   */
  @Override
  protected Actor doRead(File file) {
    AbstractActor	result;
    JsonConsumer	consumer;

    consumer = new JsonConsumer();
    result   = (AbstractActor) consumer.read(file.getAbsolutePath());

    // transfer errors/warnings
    m_Errors.addAll(consumer.getErrors());
    m_Warnings.addAll(consumer.getWarnings());

    return result;
  }
}
