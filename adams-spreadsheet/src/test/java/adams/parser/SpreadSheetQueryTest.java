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
 * SpreadSheetQueryTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.test.TmpFile;

/**
 * Tests the adams.parser.SpreadSheetQuery class. Run from commandline with: <p/>
 * java adams.parser.SpreadSheetQueryTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetQueryTest
  extends AbstractSymbolEvaluatorTestCase<Double, SpreadSheetQuery> {

  /** the spreadsheet to use as basis for the formulas. */
  protected SpreadSheet m_Sheet;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetQueryTest(String name) {
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
    
    filename = "labor.csv";
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
	  {/** no symbols. */},	//  7
	  {/** no symbols. */},	//  8
	  {/** no symbols. */},	//  9
	  {/** no symbols. */},	// 10
	  {/** no symbols. */},	// 11
	  {/** no symbols. */},	// 12
	  {/** no symbols. */},	// 13
	  {/** no symbols. */},	// 14
	  {/** no symbols. */},	// 15
	  {/** no symbols. */},	// 16
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
	  "SELECT duration",					//  1
	  "SELECT duration as dur",				//  2
	  "SELECT duration ORDER by class",			//  3
	  "SELECT duration,wage-increase-first-year,wage-increase-second-year,wage-increase-third-year,cost-of-living-adjustment WHERE (duration >= 2)",			//  4
	  "SELECT duration as dur,wage-increase-first-year as wfy,wage-increase-second-year,wage-increase-third-year,cost-of-living-adjustment WHERE ((duration >= 2) or (working-hours < 40) or (working-hours < 38)) and not (bereavement-assistance regexp \"^yes$\") ORDER by class",			//  5
	  "UPDATE SET duration = 3.5",				//  6
	  "UPDATE SET duration = 3.5 WHERE (duration >= 2)",	//  7
	  "UPDATE SET duration = 3.5 WHERE ((duration >= 2) or (working-hours < 40) or (working-hours < 38)) and not (bereavement-assistance regexp \"^yes$\")",			//  8
	  "DELETE WHERE (duration >= 2)",			//  9
	  "DELETE WHERE ((duration >= 2) or (working-hours < 40) or (working-hours < 38)) and not (bereavement-assistance regexp \"^yes$\")",			//  10
	  "SELECT duration LIMIT 2",					//  11
	  "SELECT duration LIMIT 10,2",					//  12
	  "SELECT duration WHERE bereavement-assistance = \"yes\"",	//  13
	  "SELECT * WHERE vacation > \"average\"",	//  14
	  "SELECT * WHERE vacation >= \"average\"",	//  15
	  "SELECT * WHERE vacation <> \"below_average\"",	//  16
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
  protected Object[] process(String[] expressions, BaseString[][] symbols, SpreadSheetQuery scheme) {
    scheme.setSheet(m_Sheet);
    return super.process(expressions, symbols, scheme);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetQuery[] getRegressionSetups() {
    return new SpreadSheetQuery[]{new SpreadSheetQuery()};
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetQueryTest.class);
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
