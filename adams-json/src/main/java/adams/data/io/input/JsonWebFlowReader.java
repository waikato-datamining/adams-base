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
 * JsonWebFlowReader.java
 * Copyright (C) 2013-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.option.JsonWebConsumer;
import adams.data.io.output.FlowWriter;
import adams.data.io.output.JsonWebFlowWriter;
import adams.flow.core.Actor;

import java.io.File;

/**
 * Reads flows in JSON web format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonWebFlowReader
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
    return "Reads flows in JSON web format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JSON web file";
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
  protected Actor doReadActor(File file) {
    Actor		result;
    JsonWebConsumer 	consumer;

    consumer = new JsonWebConsumer();
    result   = (Actor) consumer.read(file.getAbsolutePath());

    // transfer errors/warnings
    m_Errors.addAll(consumer.getErrors());
    m_Warnings.addAll(consumer.getWarnings());

    return result;
  }

  /**
   * Returns the corresponding writer, if available.
   *
   * @return		the writer, null if none available
   */
  @Override
  public FlowWriter getCorrespondingWriter() {
    return new JsonWebFlowWriter();
  }
}
