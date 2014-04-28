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
 * SpreadSheetFormulaTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Tests the adams.parser.SpreadSheetFormula class. Run from commandline with: <p/>
 * java adams.parser.SpreadSheetFormulaTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFormulaTest
  extends AbstractSymbolEvaluatorTestCase<Double, SpreadSheetFormula> {

  /** the spreadsheet to use as basis for the formulas. */
  protected SpreadSheet m_Sheet;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SpreadSheetFormulaTest(String name) {
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
    
    filename = "bolts.csv";
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
	  {/** no symbols. */},	// 17
	  {/** no symbols. */},	// 18
	  {/** no symbols. */},	// 19
	  {/** no symbols. */},	// 20
	  {/** no symbols. */},	// 21
	  {/** no symbols. */},	// 22
	  {/** no symbols. */},	// 23
	  {/** no symbols. */},	// 24
	  {/** no symbols. */},	// 25
	  {/** no symbols. */},	// 26
	  {/** no symbols. */},	// 27
	  {/** no symbols. */},	// 28
	  {/** no symbols. */},	// 29
	  {/** no symbols. */},	// 30
	  {/** no symbols. */},	// 31
	  {/** no symbols. */},	// 32
	  {/** no symbols. */},	// 33
	  {/** no symbols. */},	// 34
	  {/** no symbols. */},	// 35
	  {/** no symbols. */},	// 36
	  {/** no symbols. */},	// 37
	  {/** no symbols. */},	// 38
	  {/** no symbols. */},	// 39
	  {/** no symbols. */},	// 40
	  {/** no symbols. */},	// 41
	  {/** no symbols. */},	// 42
	  {/** no symbols. */},	// 43
	  {/** no symbols. */},	// 44
	  {/** no symbols. */},	// 45
	  {/** no symbols. */},	// 46
	  {/** no symbols. */},	// 47
	  {/** no symbols. */},	// 48
	  {/** no symbols. */},	// 49
	  {/** no symbols. */},	// 50
	  {/** no symbols. */},	// 51
	  {/** no symbols. */},	// 52
	  {/** no symbols. */},	// 53
	  {/** no symbols. */},	// 54
	  {/** no symbols. */},	// 55
	  {/** no symbols. */},	// 56
	  {/** no symbols. */},	// 57
	  {/** no symbols. */},	// 58
	  {/** no symbols. */},	// 59
	  {/** no symbols. */},	// 60
	  {/** no symbols. */},	// 61
	  {/** no symbols. */},	// 62
	  {/** no symbols. */},	// 63
	  {/** no symbols. */},	// 64
	  {/** no symbols. */},	// 65
	  {/** no symbols. */},	// 66
	  {/** no symbols. */},	// 67
	  {/** no symbols. */},	// 68
	  {/** no symbols. */},	// 69
	  {/** no symbols. */},	// 70
	  {/** no symbols. */},	// 71
	  {/** no symbols. */},	// 72
	  {/** no symbols. */},	// 73
	  {/** no symbols. */},	// 74
	  {/** no symbols. */},	// 75
	  {/** no symbols. */},	// 76
	  {/** no symbols. */},	// 77
	  {/** no symbols. */},	// 78
	  {/** no symbols. */},	// 79
	  {/** no symbols. */},	// 80
	  {/** no symbols. */},	// 81
	  {/** no symbols. */},	// 82
	  {/** no symbols. */},	// 83
	  {/** no symbols. */},	// 84
	  {/** no symbols. */},	// 85
	  {/** no symbols. */},	// 86
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
	  "=1 + 2",			//  1
	  "=1 - 2",			//  2
	  "=3 * 2",			//  3
	  "=1 / 2",			//  4
	  "=2 ^ 3",			//  5
	  "=abs(-10.4 + 2)",		//  6
	  "=sqrt(3*1.3)",		//  7
	  "=log(3 + 2)",		//  8
	  "=exp(3*100-2)",		//  9
	  "=sin(3)",			// 10
	  "=cos(3 + 1.1)",		// 11
	  "=tan(3)",			// 12
	  "=rint(3)",			// 13
	  "=floor(3 * 10)",		// 14
	  "=pow(3, 3)",			// 15
	  "=ceil(3/2)",			// 16
	  "=ifelse(3 < 0; 3^2; 3*2)",	// 17
	  "=1/sqrt(2*PI*pow(0.5,2))*exp(-pow(3-10;2)/(2*0.5))",	// 18
	  "=ifelse(3 < 0, 3, -3)",	// 19
	  "=(ifelse(3 < 0, 3, -3))",	// 20
	  "=ifelse((3 < 0), 3, -3)",	// 21
	  "=(2 ^ 3)",			// 22
	  "=1/sqrt(2*PI*pow(1.0,2))*exp(-1*pow(3-0,2)/(2*1.0))",	// 23
	  "=10 % 3",			// 24
	  "=-2^2",			// 25
	  "=ifelse(1 != 1, 0, 1)",	// 26
	  "=ifelse(2 != 1, 0, 1)",	// 27
	  "=ifelse(1 <> 1, 0, 1)",	// 28
	  "=ifelse(1 <> 2, 0, 1)",	// 29
	  "=.025 - 0.025",		// 30
	  "=.025 - .025",		// 31
	  "=SUM(A1:A41)",		// 32
	  "=SUM(A1:C41)",		// 33
	  "=sum(A1:A41)",		// 34
	  "=MIN(A1:A41)",		// 35
	  "=MAX(A1:A41)",		// 36
	  "=AVERAGE(A1:A41)",		// 37
	  "=STDEV(A1:A41)",		// 38
	  "=STDEVP(A1:A41)",		// 39
	  "ifelse((.025 <> 0.024) and (not false) or (1 <> 1), 0, 1)",	// 40
	  "length(trim(\"blah\"))",	// 41
	  "length(trim(\" blah\"))",	// 42
	  "length(trim(\"blah \"))",	// 43
	  "length(trim(\" blah \"))",	// 44
	  "countif(B2:B41;2.0)",	// 45
	  "countif(C2:C41,\"10\")",	// 46
	  "=(1 != 2)",			// 47
	  "=(1 != 1)",			// 48
	  "=\"blah\"",			// 49
	  "ifelse(true, true, false)",	// 50
	  "ifelse(true, \"T\", \"F\")",	// 51
	  "countif(C2:C41,true)",	// 52
	  "year(\"2013-09-04\")",		// 53
	  "year(\"2013-09-04 01:45:01\")",	// 54
	  "month(\"2013-09-04\")",		// 55
	  "month(\"2013-09-04 02:45:01\")",	// 56
	  "day(\"2013-09-04\")",		// 57
	  "day(\"2013-09-04 03:45:01\")",	// 58
	  "hour(\"2013-09-04 04:45:01\")",	// 59
	  "hour(\"01:45:01\")",			// 60
	  "minute(\"2013-09-04 05:45:01\")",	// 61
	  "minute(\"01:45:01\")",		// 62
	  "second(\"2013-09-04 06:45:01\")",	// 63
	  "second(\"01:45:01\")",		// 64
	  "weekday(\"2013-09-04\")",		// 65
	  "weekday(\"2013-09-04 07:45:01\")",	// 66
	  "weeknum(\"2013-09-04\")",		// 67
	  "weeknum(\"2013-09-04 08:45:01\")",	// 68
	  "left(\"abcdef\"; 3)",		// 69
	  "mid(\"abcdef\"; 2; 3)",		// 70
	  "right(\"abcdef\"; 2)",		// 71
	  "rept(\"abc\"; 3)",			// 72
	  "concatenate(\"ab\"; \"cd\")",	// 73
	  "concatenate(\"ab\"; \"cd\"; \"ef\")",		// 74
	  "concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\")",	// 75
	  "concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\"; \"ij\")",	// 76
	  "find(\"76\"; \"998877665544\")",		// 77
	  "find(\"76\"; \"998877665544\"; 7)",		// 78
	  "replace(\"1234567\"; 1; 1; \"444\")",	// 79
	  "SUBSTITUTE(\"123123123\";\"3\";\"abc\")",	// 80
	  "SUBSTITUTE(\"123123123\";\"3\";\"abc\";2)", 	// 81
	  "countif(C2:C41;\">10.0\")",			// 82
	  "sumif(C2:C41;\">10.0\")",			// 83
	  "sumif(C2:C41;\">10.0\";B2:B41)",		// 84
	  "if(\"12345\" != \"NONE\" & \"018\" != \"NONE\"; 1; 0)",	// 85
	  "if((\"12345\" != \"NONE\") & (\"018\" != \"NONE\"); 1; 0)",	// 86
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
  protected Object[] process(String[] expressions, BaseString[][] symbols, SpreadSheetFormula scheme) {
    scheme.setSheet(m_Sheet);
    return super.process(expressions, symbols, scheme);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected SpreadSheetFormula[] getRegressionSetups() {
    return new SpreadSheetFormula[]{new SpreadSheetFormula()};
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SpreadSheetFormulaTest.class);
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
