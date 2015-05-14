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

/**
 * TimingContainer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Container to store timing information from actor execution.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimingContainer
  extends AbstractContainer {

  private static final long serialVersionUID = 1960872156580346093L;

  /** the identifier for the the msec. */
  public final static String VALUE_MSEC = "msec";

  /** the identifier for the optional prefix. */
  public final static String VALUE_PREFIX = "Prefix";

  /** the identifier for the origin. */
  public final static String VALUE_ORIGIN = "Origin";

  /**
   * Initializes the container with no prefix and origin and a msec
   * value of 0.0.
   * <p/>
   * Only used for generating help information.
   */
  public TimingContainer() {
    this(0.0);
  }

  /**
   * Initializes the container with no prefix and origin and the specified msec
   * value.
   *
   * @param msec	the timing in msec
   */
  public TimingContainer(double msec) {
    this(msec, null, null);
  }

  /**
   * Initializes the container with the specified plot name and no X value.
   *
   * @param msec	the timing in msec
   * @param prefix	the prefix to use, can be null
   * @param origin	the origin of the timing, can be null
   */
  public TimingContainer(double msec, String prefix, String origin) {
    super();

    if ((prefix != null) && prefix.isEmpty())
      prefix = null;
    if ((origin != null) && origin.isEmpty())
      origin = null;

    store(VALUE_MSEC, msec);
    store(VALUE_PREFIX, prefix);
    store(VALUE_ORIGIN, origin);
  }

  /**
   * Returns all value names that can be used (theoretically).
   *
   * @return		iterator over all possible value names
   */
  @Override
  public Iterator<String> names() {
    List<String> result;

    result = new ArrayList<String>();

    result.add(VALUE_MSEC);
    result.add(VALUE_PREFIX);
    result.add(VALUE_ORIGIN);

    return result.iterator();
  }

  /**
   * Checks whether the setup of the container is valid.
   *
   * @return		true if all the necessary values are available
   */
  @Override
  public boolean isValid() {
    return hasValue(VALUE_MSEC);
  }
}
