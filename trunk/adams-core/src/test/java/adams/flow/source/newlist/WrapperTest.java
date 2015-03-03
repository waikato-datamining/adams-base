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
 * WrapperTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.newlist;

/**
 * Tests the Wrapper list generator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5356 $
 */
public class WrapperTest
  extends AbstractListGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WrapperTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractListGenerator[] getRegressionSetups() {
    Wrapper[]	result;
    ForLoop	loop;
    
    result    = new Wrapper[4];

    result[0] = new Wrapper();
    loop      = new ForLoop();
    result[0].setGenerator(loop);
    
    result[1] = new Wrapper();
    loop      = new ForLoop();
    loop.setLoopStep(3);
    result[1].setGenerator(loop);
    result[1].setPrefix("att-");
    
    result[2] = new Wrapper();
    loop      = new ForLoop();
    loop.setLoopLower(10);
    loop.setLoopUpper(1);
    loop.setLoopStep(-2);
    result[2].setGenerator(loop);
    result[2].setSuffix("-blah");
    
    result[3] = new Wrapper();
    loop      = new ForLoop();
    loop.setLoopStep(3);
    result[3].setGenerator(loop);
    result[3].setPrefix("att-");
    result[3].setSuffix("-blah");
    
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
