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
 * SpreadSheetFormulaTextTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.AbstractBaseObjectTestCase;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.test.AbstractTestHelper;
import adams.test.TestHelper;
import adams.test.TmpFile;

/**
 * Tests the adams.parser.SpreadSheetFormulaText class. Run from commandline with: <p/>
 * java adams.parser.SpreadSheetFormulaTextTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFormulaTextTest
  extends AbstractBaseObjectTestCase<SpreadSheetFormulaText> {

  /** the spreadsheet to use as basis for the formulas. */
  protected SpreadSheet m_Sheet;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetFormulaTextTest(String name) {
    super(name);
  }

  /**
   * Returns the test helper class to use.
   *
   * @return		the helper class instance
   */
  @Override
  protected AbstractTestHelper newTestHelper() {
    return new TestHelper(this, "adams/parser/data");
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    String	filename;
    
    super.setUp();
    
    filename = "bolts.csv";
    m_TestHelper.copyResourceToTmp(filename);
    m_Sheet = new CsvSpreadSheetReader().read(new TmpFile(filename));
    m_TestHelper.deleteFileFromTmp(filename);
  }

  /**
   * Returns a default base object.
   *
   * @return		the default object
   */
  @Override
  protected SpreadSheetFormulaText getDefault() {
    return new SpreadSheetFormulaText();
  }

  /**
   * Returns a base object initialized with the given string.
   *
   * @param s		the string to initialize the object with
   * @return		the custom object
   */
  @Override
  protected SpreadSheetFormulaText getCustom(String s) {
    return new SpreadSheetFormulaText(s);
  }

  /**
   * Returns the string representing a typical value to parse that doesn't
   * fail.
   *
   * @return		the value
   */
  @Override
  protected String getTypicalValue() {
    return "=exp(2, 10)";
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetFormulaTextTest.class);
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
