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
 * FixedNumSegmentsTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import adams.data.io.input.AbstractTimeseriesReader;
import adams.data.io.input.SimpleTimeseriesReader;

/**
 * Tests the FixedNumSegments row score.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class FixedNumSegmentsTest
  extends AbstractTimeseriesSplitterTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public FixedNumSegmentsTest(String name) {
    super(name);
  }
  
  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"simple.sts",
	"simple.sts",
    };
  }

  /**
   * Returns the reader setups to read the input data files to use
   * in the regression test.
   *
   * @return		the readers
   */
  @Override
  protected AbstractTimeseriesReader[] getRegressionInputReaders() {
    return new AbstractTimeseriesReader[]{
	new SimpleTimeseriesReader(),
	new SimpleTimeseriesReader(),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractTimeseriesSplitter[] getRegressionSetups() {
    FixedNumSegments[]	result;
    
    result    = new FixedNumSegments[2];
    result[0] = new FixedNumSegments();
    result[1] = new FixedNumSegments();
    result[1].setNumSegments(10);
    
    return result;
  }
}
