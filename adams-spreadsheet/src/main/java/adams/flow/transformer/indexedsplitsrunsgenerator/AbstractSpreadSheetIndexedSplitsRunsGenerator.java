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
 * AbstractInstancesIndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Ancestor for generators that process SpreadSheet objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSpreadSheetIndexedSplitsRunsGenerator
  extends AbstractIndexedSplitsRunsGenerator
  implements SpreadSheetIndexedSplitsRunsGenerator {

  private static final long serialVersionUID = -3421372018638798691L;

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return		the classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{SpreadSheet.class};
  }
}
