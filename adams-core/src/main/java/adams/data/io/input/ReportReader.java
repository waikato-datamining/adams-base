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
 * ReportReader.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.PlaceholderFile;
import adams.data.report.Report;

import java.util.List;

/**
 * Interface for report readers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ReportReader<T extends Report> {

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
   * Performs checks and (always) reads the data.
   *
   * @return		the report loaded from the file
   */
  public List<T> read();
}
