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
 * IndexedSplitsRunsCompatibility.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.LenientModeSupporter;
import adams.core.option.OptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;

/**
 * Interface for compatibility checks between data and indexed splits runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface IndexedSplitsRunsCompatibility
  extends OptionHandler, LenientModeSupporter {

  /**
   * Checks whether the data is compatible with the indexed splits.
   *
   * @param data	the data to check
   * @param runs 	the indexed splits to compare against
   * @return		null if successfully passed checks, otherwise error message
   */
  public String isCompatible(Object data, IndexedSplitsRuns runs);
}
