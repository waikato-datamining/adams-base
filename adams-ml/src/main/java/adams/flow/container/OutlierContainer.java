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
 * OutlierContainer.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.data.spreadsheet.SpreadSheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for outlier data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutlierContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 5581530171877321061L;

  /** the identifier for the original data. */
  public final static String VALUE_ORIGINAL = "Original";

  /** the identifier for the cleaned up data. */
  public final static String VALUE_CLEAN = "Clean";

  /** the identifier for the outlier data. */
  public final static String VALUE_OUTLIERS = "Outliers";

  /**
   * Initializes the container.
   * <br><br>
   * Only used for generating help information.
   */
  public OutlierContainer() {
    this(null, null, null);
  }

  /**
   * Initializes the container with no header.
   *
   * @param original	the original data
   * @param clean	the clean data, can be null
   * @param outliers	the outlier data, can be null
   */
  public OutlierContainer(SpreadSheet original, SpreadSheet clean, SpreadSheet outliers) {
    super();

    store(VALUE_ORIGINAL, original);
    store(VALUE_CLEAN,    clean);
    store(VALUE_OUTLIERS, outliers);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_ORIGINAL, "original data; " + SpreadSheet.class.getName());
    addHelp(VALUE_CLEAN, "clean data; " + SpreadSheet.class.getName());
    addHelp(VALUE_OUTLIERS, "outliers; " + SpreadSheet.class.getName());
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		enumeration over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String>	result;

    result = new ArrayList<String>();

    result.add(VALUE_ORIGINAL);
    result.add(VALUE_CLEAN);
    result.add(VALUE_OUTLIERS);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_ORIGINAL);
  }
}
