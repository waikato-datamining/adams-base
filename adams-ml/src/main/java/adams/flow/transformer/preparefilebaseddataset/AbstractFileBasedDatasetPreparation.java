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
 * AbstractFileBasedDatasetPreparation.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.FileBasedDatasetContainer;

/**
 * Ancestor for schemes that prepare file-based datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFileBasedDatasetPreparation<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -3778706669872750731L;

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
   * Returns the class that the preparation scheme accepts as input.
   *
   * @return		the class
   */
  public abstract Class accepts();

  /**
   * Hook method for checking the data.
   * <br/>
   * Default implementation just returns null.
   *
   * @param data	the data to check
   * @return		null if succesful, otherwise error message
   */
  protected String check(T data) {
    return null;
  }

  /**
   * Prepares the data.
   *
   * @param data	the data to use
   * @return		the generated container
   */
  protected abstract FileBasedDatasetContainer doPrepare(T data);

  /**
   * Prepares the data.
   *
   * @param data	the data to use
   * @return		the generated container
   */
  public FileBasedDatasetContainer prepare(T data) {
    String	msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doPrepare(data);
  }
}
