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
 * LookUpUpdateTest.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.base.BaseString;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.env.Environment;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.parser.LookUpUpdate class. Run from commandline with: <br><br>
 * java adams.parser.LookUpUpdateTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LookUpUpdateTest
  extends AbstractSymbolEvaluatorTestCase<Double, LookUpUpdate> {

  /** the spreadsheet to use as basis for the formulas. */
  protected SpreadSheet m_Sheet;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LookUpUpdateTest(String name) {
    super(name);
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
    
    filename = "lookup2.csv";
    m_TestHelper.copyResourceToTmp(filename);
    m_Sheet = new CsvSpreadSheetReader().read(new TmpFile(filename));
    m_TestHelper.deleteFileFromTmp(filename);
  }

  /**
   * Returns the symbols used in the regression test.
   *
   * @return		the symbols
   */
  @Override
  protected BaseString[][][] getRegressionSymbols() {
    return new BaseString[][][]{
	{
	  {/** no symbols. */},	//  1
	  {/** no symbols. */},	//  2
	  {/** no symbols. */},	//  3
	  {/** no symbols. */},	//  4
	  {/** no symbols. */},	//  5
	  {/** no symbols. */},	//  6
	}
    };
  }

  /**
   * Returns the expressions used in the regression test.
   *
   * @return		the data
   */
  @Override
  protected String[][] getRegressionExpressions() {
    return new String[][]{
	{
	  "",					                                           //  1
	  "if (C = 2) then C := A * B; end",				                   //  2
	  "if (C = 3) then C := A * B + 0.1; else C := A + 10; end",			           //  3
	  "C := 5;",			                                                   //  4
	  "if (C = 2) then C := A * B + 0.1; E := \"A*B+0.1\"; else C := A + 10; E := \"A+10\"; end",  //  5
	  "if (C = 2) then C := A * B + 0.1; end D := 0.5; E := \"updated\";",		   //  6
	}
    };
  }

  /**
   * Generates output from the input expressions.
   *
   * @param expressions	the expressions to work on
   * @param symbols	the symbols to use
   * @param scheme	the scheme to process the data with
   * @return		the generated statistics
   */
  @Override
  protected Object[] process(String[] expressions, BaseString[][] symbols, LookUpUpdate scheme) {
    scheme.setSheet(m_Sheet);
    return super.process(expressions, symbols, scheme);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected LookUpUpdate[] getRegressionSetups() {
    LookUpUpdate[]	result;

    result = new LookUpUpdate[1];
    result[0] = new LookUpUpdate();
    result[0].setKeyColumn(new SpreadSheetColumnIndex("1"));
    result[0].setValueColumn(new SpreadSheetColumnIndex("2"));

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(LookUpUpdateTest.class);
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
