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
 * IndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;

/**
 * Interface for schemes that generate indexed splits runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface IndexedSplitsRunsGenerator
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return		the classes
   */
  public abstract Class[] accepts();

  /**
   * Generates the indexed splits.
   *
   * @param data	the data to use for generating the splits
   * @param errors	for storing any errors occurring during processing
   * @return		the splits or null in case of error
   */
  public IndexedSplitsRuns generate(Object data, MessageCollection errors);
}
