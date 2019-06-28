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
 * AbstractDatasetGenerator.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.dataset;

import adams.core.QuickInfoSupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.SpreadSheet;
import org.jfree.data.general.Dataset;

/**
 * Ancestor for dataset generators.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractDatasetGenerator<T extends Dataset>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 125224185085489847L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the class of dataset that it generates.
   *
   * @return		the dataset class
   */
  public abstract Class<? extends Dataset> generates();

  /**
   * Hook method for checks before generating the dataset.
   *
   * @param data	the data to use
   * @return		null if checks passed, otherwise error message
   */
  protected String check(SpreadSheet data) {
    if (data == null)
      return "No spreadsheet provided!";
    return null;
  }

  /**
   * Performs the actual generation of the dataset.
   *
   * @param data	the data to use
   * @return		the dataset
   */
  protected abstract T doGenerate(SpreadSheet data);

  /**
   * Generates the dataset.
   *
   * @param data	the data to use
   * @return		the dataset
   */
  public T generate(SpreadSheet data) {
    String	msg;

    msg = check(data);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doGenerate(data);
  }

  /**
   * Performs checks before adding series.
   *
   * @param dataset   	the dataset to add the series to
   * @param data	the data to use
   * @return		null if checks passed, otherwise error message
   */
  protected String checkAddSeries(Dataset dataset, SpreadSheet data) {
    String	result;

    result = check(data);
    if (result == null) {
      if (dataset == null)
	result = "No dataset provided!";
      else if (!(dataset.getClass().equals(generates())))
	result = "Dataset must be of type " + Utils.classToString(generates()) + ", not " + Utils.classToString(dataset);
    }

    return result;
  }

  /**
   * Performs the actual addition of the series to the dataset.
   *
   * @param dataset   	the dataset to add the series to
   * @param data	the data to use
   * @return		the updated dataset
   */
  protected abstract T doAddSeries(Dataset dataset, SpreadSheet data);

  /**
   * Adds the series to the dataset.
   *
   * @param dataset   	the dataset to add the series to
   * @param data	the data to use
   * @return		the updated dataset
   */
  public T addSeries(T dataset, SpreadSheet data) {
    String	msg;

    msg = checkAddSeries(dataset, data);
    if (msg != null)
      throw new IllegalStateException(msg);

    return doAddSeries(dataset, data);
  }
}
