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
 * ReportWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.data.report.Report;

/**
 * Interface for report writers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ReportWriter<T extends Report> {

  /**
   * Sets the file/directory to write to.
   *
   * @param value	the file/directory to write to
   */
  public void setOutput(PlaceholderFile value);

  /**
   * The file/directory to write to.
   *
   * @return		the file/directory to write to
   */
  public PlaceholderFile getOutput();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputTipText();

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(T data);
}
