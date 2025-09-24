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
 * DataContainerWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import adams.core.AdditionalInformationHandler;
import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;
import adams.data.container.DataContainer;

import java.util.List;

/**
 * Interface for writers that write data containers to files in various
 * formats.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to handle
 */
public interface DataContainerWriter<T extends DataContainer>
  extends OptionHandler, Comparable, CleanUpHandler, ShallowCopySupporter<DataContainerWriter>,
             FileFormatHandler, AdditionalInformationHandler {

  /**
   * Returns whether the output needs to be a file or directory.
   *
   * @return true if the output needs to be a file, a directory otherwise
   */
  public boolean isOutputFile();

  /**
   * Sets the file/directory to write to.
   *
   * @param value
   *          the file/directory to write to
   */
  public void setOutput(PlaceholderFile value);

  /**
   * The file/directory to write to.
   *
   * @return the file/directory to write to
   */
  public PlaceholderFile getOutput();

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the GUI or
   *         for listing the options.
   */
  public String outputTipText();

  /**
   * Returns whether writing of multiple containers is supported.
   * 
   * @return 		true if multiple containers are supported
   */
  public boolean canWriteMultiple();

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   * @see		#write(List)
   */
  public boolean write(T data);

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(List<T> data);
}
