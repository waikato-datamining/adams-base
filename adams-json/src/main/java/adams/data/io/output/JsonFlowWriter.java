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
 * JsonFlowWriter.java
 * Copyright (C) 2013-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.option.JsonProducer;
import adams.data.io.input.FlowReader;
import adams.data.io.input.JsonFlowReader;
import adams.flow.core.Actor;

import java.io.File;

/**
 * Writes flows in JSON format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonFlowWriter
  extends AbstractFlowWriter {

  /** for serialization. */
  private static final long serialVersionUID = -3564589187575690183L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes flows in JSON format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new JsonFlowReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new JsonFlowReader().getFormatExtensions();
  }

  /**
   * Returns how to write the data, from a file, stream or writer.
   *
   * @return		how to write the data
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.FILE;
  }

  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(Actor content, File file) {
    boolean		result;
    JsonProducer	producer;

    producer = new JsonProducer();
    producer.setOutputFull(false);
    producer.produce(content);
    result = producer.write(file.getAbsolutePath());

    return result;
  }

  /**
   * Returns the corresponding reader, if available.
   *
   * @return		the reader, null if none available
   */
  @Override
  public FlowReader getCorrespondingReader() {
    return new JsonFlowReader();
  }
}
