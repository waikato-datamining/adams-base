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
 * AbstractIndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.indexedsplitsrunsgenerator;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.flow.core.Compatibility;

/**
 * Ancestor for schemes that generate indexed splits runs.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIndexedSplitsRunsGenerator
  extends AbstractOptionHandler
  implements IndexedSplitsRunsGenerator {

  private static final long serialVersionUID = 6513142055574442720L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the type of classes that are accepted as input.
   *
   * @return		the classes
   */
  public abstract Class[] accepts();

  /**
   * Checks whether the data can be processed.
   *
   * @param data	the data to check
   * @return		null if checks passed, otherwise error message
   */
  protected String check(Object data) {
    Compatibility	comp;

    if (data == null)
      return "No data provided!";

    comp = new Compatibility();
    if (!comp.isCompatible(new Class[]{data.getClass()}, accepts()))
      return "Input of '" + Utils.classToString(data) + "' is not compatible with: " + Utils.classesToString(accepts());

    return null;
  }

  /**
   * Generates the indexed splits.
   *
   * @param data	the data to use for generating the splits
   * @param errors	for storing any errors occurring during processing
   * @return		the splits or null in case of error
   */
  protected abstract IndexedSplitsRuns doGenerate(Object data, MessageCollection errors);

  /**
   * Generates the indexed splits.
   *
   * @param data	the data to use for generating the splits
   * @param errors	for storing any errors occurring during processing
   * @return		the splits or null in case of error
   */
  public IndexedSplitsRuns generate(Object data, MessageCollection errors) {
    String	msg;

    msg = check(data);
    if (msg != null) {
      errors.add(msg);
      return null;
    }

    return doGenerate(data, errors);
  }
}
