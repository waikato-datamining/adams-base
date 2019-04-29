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
 * AbstractSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.sources;

import adams.data.spreadsheet.SpreadSheet;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

/**
 * Ancestor for sources.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSource
  extends AbstractWidget {

  private static final long serialVersionUID = 8315374668966776518L;

  /**
   * Checks whether data is available.
   *
   * @return		true if available
   */
  public abstract boolean hasData();

  /**
   * Returns the currently available data
   *
   * @return		the data, null if none available
   */
  public abstract SpreadSheet getData();
}
