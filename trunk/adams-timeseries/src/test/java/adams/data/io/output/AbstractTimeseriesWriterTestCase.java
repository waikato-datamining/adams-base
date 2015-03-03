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
 * AbstractTimeseriesWriterTestCase.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.timeseries.Timeseries;
import adams.test.AbstractTestHelper;
import adams.test.TimeseriesTestHelper;

/**
 * Abstract test class for the timeseries writers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <A> the type of reader
 * @param <D> the type of data container
 */
public abstract class AbstractTimeseriesWriterTestCase<A extends AbstractDataContainerWriter, D extends Timeseries>
  extends AbstractDataContainerWriterTestCase<A, D> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractTimeseriesWriterTestCase(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TimeseriesTestHelper(this, "adams/data/io/output/data");
  }
}
