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
 * StringAnonymizerTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

/**
 * Tests the StringAnonymizer class.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringAnonymizerTest
  extends AbstractAnonymizerTestCase<String> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public StringAnonymizerTest(String name) {
    super(name);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  @Override
  protected String[] getRegressionInputData() {
    return new String[]{
	"1",
	"2",
	"3",
	"4",
	"5",
	"6",
	"7",
	"8",
	"9",
	"10",
	"31415",
	"123",
	"1230912"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractAnonymizer<String>[] getRegressionSetups() {
    return new StringAnonymizer[]{
	new StringAnonymizer("blah", 1, 100)
    };
  }

}
