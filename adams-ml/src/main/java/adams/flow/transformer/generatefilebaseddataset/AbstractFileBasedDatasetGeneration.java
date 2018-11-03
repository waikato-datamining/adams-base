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
 * AbstractFileBasedDatasetGeneration.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.generatefilebaseddataset;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.FileBasedDatasetContainer;

/**
 * Ancestor for schemes that generate datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileBasedDatasetGeneration<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 4495314537877166279L;

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
   * Returns the class that gets generated.
   *
   * @return		the generated class
   */
  public abstract Class generates();

  /**
   * The keys of the values that need to be present in the container.
   *
   * @return		the keys
   */
  protected abstract String[] requiredValues();

  /**
   * Performs checks on the container.
   *
   * @param cont	the container to check
   * @return		null if successful, otherwise error message
   */
  protected String check(FileBasedDatasetContainer cont) {
    if (cont == null)
      return "No container provided!";

    for (String key: requiredValues()) {
      if (!cont.hasValue(key))
        return "Contains is missing value: " + key;
    }

    return null;
  }

  /**
   * Generates the dataset.
   *
   * @param cont	the container to use
   * @return		the generated output
   */
  protected abstract T doGenerate(FileBasedDatasetContainer cont);

  /**
   * Generates the dataset.
   *
   * @param cont	the container to use
   * @return		the generated output
   */
  public T generate(FileBasedDatasetContainer cont) {
    String	msg;

    msg = check(cont);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doGenerate(cont);
  }
}
