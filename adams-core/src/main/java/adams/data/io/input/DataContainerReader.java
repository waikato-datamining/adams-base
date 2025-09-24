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
 * DataContainerReader.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.input;

import adams.core.AdditionalInformationHandler;
import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionHandler;
import adams.data.container.DataContainer;

import java.util.List;

/**
 * Interface for readers that read files in various formats and
 * turn them into data containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of data to read
 */
public interface DataContainerReader<T extends DataContainer>
  extends OptionHandler, Comparable, CleanUpHandler, ShallowCopySupporter<DataContainerReader>,
             FileFormatHandler, AdditionalInformationHandler {

  /**
   * Returns whether the input needs to be a file or directory.
   *
   * @return		true if the input needs to be a file, a directory
   * 			otherwise
   */
  public boolean isInputFile();

  /**
   * Sets the file/directory to read.
   *
   * @param value	the file/directory to read
   */
  public void setInput(PlaceholderFile value);

  /**
   * The file/directory to read.
   *
   * @return		the file/directory to read
   */
  public PlaceholderFile getInput();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTipText();

  /**
   * Sets whether to create a dummy report if none present.
   *
   * @param value	if true then a dummy report is generated if necessary
   */
  public void setCreateDummyReport(boolean value);

  /**
   * Returns whether to create a dummy report if none present.
   *
   * @return		true if a dummy report is generated if necessary
   */
  public boolean getCreateDummyReport();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String createDummyReportTipText();

  /**
   * Returns the spectrums generated from the file. If necessary,
   * performs the parsing (e.g., if not yet read).
   *
   * @return		the spectrums generated from the file
   */
  public List<T> read();
}
