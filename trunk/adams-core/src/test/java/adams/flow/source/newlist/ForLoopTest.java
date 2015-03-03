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
 * ForLoopTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.newlist;

/**
 * Tests the ForLoop list generator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5356 $
 */
public class ForLoopTest
  extends AbstractListGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public ForLoopTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractListGenerator[] getRegressionSetups() {
    ForLoop[]	result;
    
    result    = new ForLoop[3];
    result[0] = new ForLoop();
    result[1] = new ForLoop();
    result[1].setLoopStep(3);
    result[2] = new ForLoop();
    result[2].setLoopLower(10);
    result[2].setLoopUpper(1);
    result[2].setLoopStep(-2);
    
    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }
}
