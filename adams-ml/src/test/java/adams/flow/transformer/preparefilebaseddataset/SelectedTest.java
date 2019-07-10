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
 * SelectedTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.preparefilebaseddataset;

import adams.core.Index;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests Selected.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SelectedTest
  extends AbstractFileBasedDatasetPreparationTestCase<String[][]> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public SelectedTest(String name) {
    super(name);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  @Override
  protected String[][] getRegressionInputData() {
    return new String[][]{
      {
	"00.txt",
	"01.txt",
	"02.txt",
	"03.txt",
	"04.txt",
	"05.txt",
	"06.txt",
	"07.txt",
	"08.txt",
	"09.txt",
      },
      {
	"10.txt",
	"11.txt",
	"12.txt",
	"13.txt",
	"14.txt",
	"15.txt",
	"16.txt",
	"17.txt",
	"18.txt",
	"19.txt",
      },
      {
	"20.txt",
	"21.txt",
	"22.txt",
	"23.txt",
	"24.txt",
	"25.txt",
	"26.txt",
	"27.txt",
	"28.txt",
	"29.txt",
      },
      {
	"30.txt",
	"31.txt",
	"32.txt",
	"33.txt",
	"34.txt",
	"35.txt",
	"36.txt",
	"37.txt",
	"38.txt",
	"39.txt",
      },
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFileBasedDatasetPreparation<String[][]>[] getRegressionSetups() {
    Selected[]	result;

    result = new Selected[4];
    result[0] = new Selected();
    result[0].setTrain(new Index("1"));
    result[1] = new Selected();
    result[1].setTrain(new Index("1"));
    result[1].setTest(new Index("2"));
    result[2] = new Selected();
    result[2].setTrain(new Index("1"));
    result[2].setTest(new Index("2"));
    result[2].setValidation(new Index("3"));
    result[3] = new Selected();
    result[3].setTrain(new Index("1"));
    result[3].setTest(new Index("2"));
    result[3].setValidation(new Index("3"));
    result[3].setNegative(new Index("4"));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SelectedTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
