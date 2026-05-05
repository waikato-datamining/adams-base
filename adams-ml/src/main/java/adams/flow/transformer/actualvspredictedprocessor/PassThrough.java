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
 * PassThrough.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.actualvspredictedprocessor;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Dummy, just returns the input.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PassThrough
  extends AbstractActualVsPredictedProcessor<SpreadSheet> {

  private static final long serialVersionUID = -4503591629503475658L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy, just returns the input.";
  }

  /**
   * Returns the type of output the processor generates.
   *
   * @return the type of output
   */
  @Override
  public Class generates() {
    return SpreadSheet.class;
  }

  /**
   * Processes the actual vs predicted data and returns
   * the output generated.
   *
   * @param sheet the data to process
   * @return the output
   */
  @Override
  protected SpreadSheet doProcess(SpreadSheet sheet) {
    return sheet;
  }
}
