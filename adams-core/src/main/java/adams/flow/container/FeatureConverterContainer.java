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
 * FeatureConverterContainer.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.container;

import adams.data.featureconverter.HeaderDefinition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container for a feature converter's raw output (header/row data).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10096 $
 */
public class FeatureConverterContainer
  extends AbstractContainer {

  /** for serialization. */
  private static final long serialVersionUID = 7610036927945350224L;
  
  /** the identifier for the Header. */
  public final static String VALUE_HEADER = "Header";

  /** the identifier for the Row. */
  public final static String VALUE_ROW = "Row";

  /**
   * Initializes the container with empty header and no row.
   * <br><br>
   * Only used for generating help information.
   */
  public FeatureConverterContainer() {
    this(new HeaderDefinition(), null);
  }

  /**
   * Initializes the container with the specified plot name and no X value.
   *
   * @param header	the header definition
   * @param row		the data row, can be null
   */
  public FeatureConverterContainer(HeaderDefinition header, List row) {
    super();

    store(VALUE_HEADER, header);
    store(VALUE_ROW, row);
  }

  /**
   * Initializes the help strings.
   */
  protected void initHelp() {
    super.initHelp();

    addHelp(VALUE_HEADER, "header definition; " + HeaderDefinition.class.getName());
    addHelp(VALUE_ROW, "data row; " + List.class.getName());
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

    result.add(VALUE_HEADER);
    result.add(VALUE_ROW);

    return result.iterator();
  }
  
  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_HEADER);
  }
}
