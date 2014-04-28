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
 * MathematicalExpressionTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.env.Environment;

/**
 * Tests the adams.parser.MathematicalExpression class. Run from commandline with: <p/>
 * java adams.parser.MathematicalExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MathematicalExpressionTest
  extends AbstractSymbolEvaluatorTestCase<Double, MathematicalExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MathematicalExpressionTest(String name) {
    super(name);
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
	  {/** no symbols. */},		//  1
	  {/** no symbols. */},		//  2
	  {/** no symbols. */},		//  3
	  {/** no symbols. */},		//  4
	  {new BaseString("X=2.0")},	//  5
	  {new BaseString("X=2.0")},	//  6
	  {new BaseString("X=2.0")},	//  7
	  {new BaseString("X=1.0")},	//  8
	  {new BaseString("X=1.0")},	//  9
	  {new BaseString("X=1.0")},	// 10
	  {new BaseString("X=1.0")},	// 11
	  {new BaseString("X=1.0")},	// 12
	  {new BaseString("X=1.0")},	// 13
	  {new BaseString("X=3.1")},	// 14
	  {new BaseString("X=2.3")},	// 15
	  {new BaseString("X=1.0")},	// 16
	  {new BaseString("X=-1.0")},	// 17
	  {new BaseString("X=9.0")},	// 18
	  {new BaseString("X=-1.0")},	// 19
	  {new BaseString("X=-1.0")},	// 20
	  {new BaseString("X=-1.0")},	// 21
	  {new BaseString("X=2.0")},	// 22
	  {new BaseString("X=2.0")},	// 23
	  {/** no symbols. */},		// 24
	  {/** no symbols. */},		// 25
	  {/** no symbols. */},		// 26
	  {/** no symbols. */},		// 27
	  {new BaseString("X=2.0")},	// 28
	  {/** no symbols. */},		// 29
	  {/** no symbols. */},		// 30
	  {/** no symbols. */},		// 31
	  {/** no symbols. */},		// 32
	  {/** no symbols. */},		// 33
	  {/** no symbols. */},		// 34
	  {/** no symbols. */},		// 35
	  {/** no symbols. */},		// 36
	  {/** no symbols. */},		// 37
	  {/** no symbols necessary */},		// 38
	  {/** no symbols necessary */},		// 39
	  {/** no symbols necessary */},		// 40
	  {/** no symbols necessary */},		// 41
	  {/** no symbols necessary */},		// 42
	  {/** no symbols necessary */},		// 43
	  {/** no symbols necessary */},		// 44
	  {/** no symbols necessary */},		// 45
	  {/** no symbols necessary */},		// 46
	  {/** no symbols necessary */},		// 47
	  {/** no symbols necessary */},		// 48
	  {/** no symbols necessary */},		// 49
	  {/** no symbols necessary */},		// 50
	  {/** no symbols necessary */},		// 51
	  {/** no symbols necessary */},		// 52
	  {/** no symbols necessary */},		// 53
	  {/** no symbols necessary */},		// 54
	  {/** no symbols necessary */},		// 55
	  {/** no symbols necessary */},		// 56
	  {/** no symbols necessary */},		// 57
	  {/** no symbols necessary */},		// 58
	  {/** no symbols necessary */},		// 59
	  {/** no symbols necessary */},		// 60
	  {/** no symbols necessary */},		// 61
	  {/** no symbols necessary */},		// 62
	  {/** no symbols necessary */},		// 63
	  {/** no symbols necessary */},		// 64
	  {/** no symbols necessary */},		// 65
	  {/** no symbols necessary */},		// 66
	  {/** no symbols necessary */},		// 67
	  {/** no symbols necessary */},		// 68
	  {/** no symbols necessary */},		// 69
	  {/** no symbols necessary */},		// 70
	  {/** no symbols necessary */},		// 71
	  {/** no symbols necessary */},		// 72
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
	  "1 + 2",			//  1
	  "1 - 2",			//  2
	  "3 * 2",			//  3
	  "1 / 2",			//  4
	  "2 ^ X",			//  5
	  "abs(-10.4 + X)",		//  6
	  "sqrt(X*1.3)",		//  7
	  "log(X + 2)",			//  8
	  "exp(3*100-2)",		//  9
	  "sin(X)",			// 10
	  "cos(X + 1.1)",		// 11
	  "tan(X)",			// 12
	  "rint(X)",			// 13
	  "floor(X * 10)",		// 14
	  "pow(3, X)",			// 15
	  "ceil(X/2)",			// 16
	  "ifelse(X < 0; X^2; X*2)",	// 17
	  "1/sqrt(2*PI*pow(0.5,2))*exp(-pow(X-10;2)/(2*0.5))",	// 18
	  "ifelse(X < 0, X, -X)",	// 19
	  "(ifelse(X < 0, X, -X))",	// 20
	  "ifelse((X < 0), X, -X)",	// 21
	  "(2 ^ X)",			// 22
	  "1/sqrt(2*PI*pow(1.0,2))*exp(-1*pow(X-0,2)/(2*1.0))",	// 23
	  "10 % 3",			// 24
	  "-2^2",			// 25
	  "ifelse(1 != 1, 0, 1)",	// 26
	  "ifelse(2 != 1, 0, 1)",	// 27
	  "ifmissing(X, 0)",		// 28
	  "ifmissing(Y, 0)",		// 29
	  ".025 - 0.025",		// 30
	  ".025 - .025",		// 31
	  "if((.025 <> 0.024) and (not false) or (1 <> 1); 0; 1)",	// 32
	  "length(trim(\"blah\"))",		// 33
	  "length(trim(\" blah\"))",		// 34
	  "length(trim(\"blah \"))",		// 35
	  "len(trim(\" blah \"))",		// 36
	  "power(3, 2)",			// 37
	  "year(\"2013-09-04\")",		// 38
	  "year(\"2013-09-04 01:45:01\")",	// 39
	  "month(\"2013-09-04\")",		// 40
	  "month(\"2013-09-04 02:45:01\")",	// 41
	  "day(\"2013-09-04\")",		// 42
	  "day(\"2013-09-04 03:45:01\")",	// 43
	  "hour(\"2013-09-04 04:45:01\")",	// 44
	  "hour(\"01:45:01\")",			// 45
	  "minute(\"2013-09-04 05:45:01\")",	// 46
	  "minute(\"01:45:01\")",		// 47
	  "second(\"2013-09-04 06:45:01\")",	// 48
	  "second(\"01:45:01\")",		// 49
	  "weekday(\"2013-09-04\")",		// 50
	  "weekday(\"2013-09-04 07:45:01\")",	// 51
	  "weeknum(\"2013-09-04\")",		// 52
	  "weeknum(\"2013-09-04 08:45:01\")",	// 53
	  "len(left(\"abcdef\"; 3))",		// 54
	  "len(mid(\"abcdef\"; 2; 3))",		// 55
	  "len(right(\"abcdef\"; 2))",		// 56
	  "len(rept(\"abc\"; 3))",		// 57
	  "len(concatenate(\"ab\"; \"cd\"))",	// 58
	  "len(concatenate(\"ab\"; \"cd\"; \"ef\"))",		// 59
	  "len(concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\"))",	// 60
	  "len(concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\"; \"ij\"))",	// 61
	  "find(\"76\"; \"998877665544\")",			// 62
	  "find(\"76\"; \"998877665544\"; 7)",			// 63
	  "len(replace(\"1234567\"; 1; 1; \"444\"))",		// 64
	  "len(SUBSTITUTE(\"123123123\";\"3\";\"abc\"))",	// 65
	  "len(SUBSTITUTE(\"123123123\";\"3\";\"abc\";2))", 	// 66
	  "if(\"12345\" != \"NONE\" & \"018\" != \"NONE\"; 1; 0)",	// 67
	  "if((\"12345\" != \"NONE\") & (\"018\" != \"NONE\"); 1; 0)",	// 68
	  "if(isNaN(nan), 1.0, 0.0)", 		// 69
	  "if(isnan(NaN), 1.0, 0.0)", 		// 70
	  "if(isnan(1.0), 1.0, 0.0)", 		// 71
	  "if(isnan(0.0 / 0.0), 1.0, 0.0)", 	// 72
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected MathematicalExpression[] getRegressionSetups() {
    return new MathematicalExpression[]{new MathematicalExpression()};
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MathematicalExpressionTest.class);
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
