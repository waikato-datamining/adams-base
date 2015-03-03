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
 * TableUpgradeTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.tools;

import adams.tools.AbstractToolTestCase;

/**
 * Tests the TableUpgrade tool.
 * <p/>
 * NB: Dummy test.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TableUpgradeTest
  extends AbstractToolTestCase {

  /**
   * Constructs the test case.
   *
   * @param name 	the name of the test
   */
  public TableUpgradeTest(String name) {
    super(name);
  }

  /**
   * The files to use as input in the regression tests, in case of tool
   * implementing the InputFileHandler interface.
   *
   * @return		the files, zero-length if not an InputFileHandler
   */
  protected String[] getRegressionInputFiles() {
    return new String[0];
  }

  /**
   * The files to use as output in the regression tests, in case of tool
   * implementing the OutputFileGenerator interface.
   * <p/>
   * NB: these names must be all different!
   *
   * @return		the files, zero-length if not an OutputFileGenerator
   */
  protected String[] getRegressionOutputFiles() {
    return new String[0];
  }

  /**
   * Returns the setups to test in the regression tests.
   *
   * @return		the setups to test
   */
  protected AbstractTool[] getRegressionSetups() {
    return new AbstractTool[0];
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }
}
