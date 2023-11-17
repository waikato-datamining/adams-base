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
 * BooleanExpressionTest.java
 * Copyright (C) 2010-2023 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.base.BaseString;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.parser.BooleanExpression class. Run from commandline with: <br><br>
 * java adams.parser.BooleanExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class BooleanExpressionTest
  extends AbstractSymbolEvaluatorTestCase<Boolean, BooleanExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BooleanExpressionTest(String name) {
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
	  {/* no symbols necessary */},		//  1
	  {/* no symbols necessary */},		//  2
	  {/* no symbols necessary */},		//  3
	  {/* no symbols necessary */},		//  4
	  {new BaseString("X=1.0")},			//  5
	  {new BaseString("X=1.0")},			//  6
	  {new BaseString("X=1.0")},			//  7
	  {new BaseString("X=1.0")},			//  8
	  {new BaseString("X=1.0")},			//  9
	  {new BaseString("X=1.0")},			// 10
	  {new BaseString("X=1.0")},			// 11
	  {new BaseString("X=1.0")},			// 12
	  {new BaseString("X=1.0")},			// 13
	  {new BaseString("X=2.0")},			// 14
	  {new BaseString("X=1.0")},			// 15
	  {new BaseString("X=0.0")},			// 16
	  {new BaseString("X=-10.0")},			// 17
	  {new BaseString("X=4.0")},			// 18
	  {new BaseString("X=0.5")},			// 19
	  {new BaseString("X=10.0")},			// 20
	  {new BaseString("X=1.0")},			// 21
	  {new BaseString("X=1.0")},			// 22
	  {new BaseString("X=1.0")},			// 23
	  {new BaseString("X=-10.234")},		// 24
	  {new BaseString("X=-10.3")},			// 25
	  {new BaseString("X=2")},			// 26
	  {new BaseString("X=-10.3")},			// 27
	  {new BaseString("X=9.0")},			// 28
	  {/* no symbols necessary */},		// 29
	  {/* no symbols necessary */},		// 30
	  {/* no symbols necessary */},		// 31
	  {/* no symbols necessary */},		// 32
	  {/* no symbols necessary */},		// 33
	  {/* no symbols necessary */},		// 34
	  {/* no symbols necessary */},		// 35
	  {/* no symbols necessary */},		// 36
	  {/* no symbols necessary */},		// 37
	  {/* no symbols necessary */},		// 38
	  {/* no symbols necessary */},		// 39
	  {/* no symbols necessary */},		// 40
	  {/* no symbols necessary */},		// 41
	  {/* no symbols necessary */},		// 42
	  {/* no symbols necessary */},		// 43
	  {/* no symbols necessary */},		// 44
	  {/* no symbols necessary */},		// 45
	  {/* no symbols necessary */},		// 46
	  {/* no symbols necessary */},		// 47
	  {/* no symbols necessary */},		// 48
	  {/* no symbols necessary */},		// 49
	  {/* no symbols necessary */},		// 50
	  {/* no symbols necessary */},		// 51
	  {/* no symbols necessary */},		// 52
	  {/* no symbols necessary */},		// 53
	  {/* no symbols necessary */},		// 54
	  {/* no symbols necessary */},		// 55
	  {/* no symbols necessary */},		// 56
	  {/* no symbols necessary */},		// 57
	  {/* no symbols necessary */},		// 58
	  {/* no symbols necessary */},		// 59
	  {/* no symbols necessary */},		// 60
	  {/* no symbols necessary */},		// 61
	  {/* no symbols necessary */},		// 62
	  {/* no symbols necessary */},		// 63
	  {/* no symbols necessary */},		// 64
	  {/* no symbols necessary */},		// 65
	  {/* no symbols necessary */},		// 66
	  {/* no symbols necessary */},		// 67
	  {/* no symbols necessary */},		// 68
	  {/* no symbols necessary */},		// 69
	  {/* no symbols necessary */},		// 70
	  {/* no symbols necessary */},		// 71
	  {/* no symbols necessary */},		// 72
	  {/* no symbols necessary */},		// 73
	  {/* no symbols necessary */},		// 74
	  {/* no symbols necessary */},		// 75
	  {/* no symbols necessary */},		// 76
	  {/* no symbols necessary */},		// 77
	  {/* no symbols necessary */},		// 78
	  {/* no symbols necessary */},		// 79
	  {/* no symbols necessary */},		// 80
	  {/* no symbols necessary */},		// 81
	  {/* no symbols necessary */},		// 82
	  {/* no symbols necessary */},		// 83
	  {/* no symbols necessary */},		// 84
	  {/* no symbols necessary */},		// 85
	  {/* no symbols necessary */},		// 86
	  {/* no symbols necessary */},		// 87
	  {/* no symbols necessary */},		// 88
	  {/* no symbols necessary */},		// 89
	  {/* no symbols necessary */},		// 90
	  {/* no symbols necessary */},		// 91
	  {/* no symbols necessary */},		// 92
	  {/* no symbols necessary */},		// 93
	  {/* no symbols necessary */},		// 94
	  {/* no symbols necessary */},		// 95
	  {/* no symbols necessary */},		// 96
	  {/* no symbols necessary */},		// 97
	  {/* no symbols necessary */},		// 98
	  {/* no symbols necessary */},		// 99
	  {/* no symbols necessary */},		// 100
	  {/* no symbols necessary */},		// 101
	  {/* no symbols necessary */},		// 102
	  {/* no symbols necessary */},		// 103
	  {/* no symbols necessary */},		// 104
	  {/* no symbols necessary */},		// 105
	  {new BaseString("X=9.0")},			// 106
	  {new BaseString("X=9.0")},			// 107
	  {/* no symbols necessary */},		// 108
	  {/* no symbols necessary */},		// 109
	  {/* no symbols necessary */},		// 110
	  {new BaseString("hello world=1")},		// 111
	  {new BaseString("hello world=1")},		// 112
	  {new BaseString("Ab_c0=1")},			// 113
	  {/* no symbols necessary */},		// 114
	  {/* no symbols necessary */},		// 115
	  {/* no symbols necessary */},		// 116
	  {/* no symbols necessary */},		// 117
	  {/* no symbols necessary */},		// 118
	  {/* no symbols necessary */},		// 119
	  {/* no symbols necessary */},		// 120
	  {/* no symbols necessary */},		// 121
	  {/* no symbols necessary */},		// 122
	  {/* no symbols necessary */},		// 123
	  {/* no symbols necessary */},		// 124
	  {/* no symbols necessary */},		// 125
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
	  "false",					//  1
	  "true",					//  2
	  "1 + 1 > 2",					//  3
	  "1 + 1 >= 2",					//  4
	  "X = 2",					//  5
	  "X = 1",					//  6
	  "X >= 1",					//  7
	  "X > 1",					//  8
	  "X <= 1",					//  9
	  "X < 1",					// 10
	  "X*100 > 1",					// 11
	  "X/100 > 1",					// 12
	  "X - 1 > 0",					// 13
	  "X - 1 > 0",					// 14
	  "(ifelse(X >= 0; X; -X) > 0)",		// 15
	  "abs(X) > 0",					// 16
	  "abs(X) > 0",					// 17
	  "sqrt(X) = 2",				// 18
	  "log(X) > 0",					// 19
	  "floor(exp(log(X))) = floor(X)",		// 20
	  "sin(X) > 0",					// 21
	  "cos(X) < 0",					// 22
	  "tan(X) > 0",					// 23
	  "rint(X) = 10",				// 24
	  "floor(X) = 10",				// 25
	  "pow(X,2) = 4",				// 26
	  "ceil(X) = 11",				// 27
	  "(1/sqrt(2*PI*pow(0.5,2))*exp(-pow(X-10;2)/(2*0.5))) > 0",	// 28
	  "\"hello\" = \"hello\"",			// 29
	  "\"hello\" <= \"hello\"",			// 30
	  "\"hello\" >= \"hello\"",			// 31
	  "\"hello\" < \"hello\"",			// 32
	  "\"hello\" > \"hello\"",			// 33
	  "length(\"hello\") = 5",			// 34
	  "length(\"hello\") > 0",			// 35
	  "length(\"hello\") >= 0",			// 36
	  "length(\"hello\") < 5",			// 37
	  "length(\"hello\") <= 5",			// 38
	  "(false)",					// 39
	  "(length(\"hello\") = 5)",			// 40
	  "\"hello\" = \"hell\"",			// 41
	  "10 % 3 > 1",					// 42
	  "-2^2 > 0",					// 43
	  "\"hello\" != \"hell\"",			// 44
	  "\"hello\" != \"hello\"",			// 45
	  "1 != 1",					// 46
	  "2 != 1",					// 47
	  ".025 = 0.025",				// 48
	  ".025 != 0.024",				// 49
	  "(.025 <> 0.024) and (not false) or (1 <> 1)",	// 50
	  "trim(\"blah\") = \"blah\"",			// 51
	  "trim(\" blah\") = \"blah\"",			// 52
	  "trim(\"blah \") = \"blah\"",			// 53
	  "trim(\" blah \") = \"blah\"",		// 54
	  "year(\"2013-09-04\") = 2013",		// 55
	  "year(\"2013-09-04 01:45:01\") = 2013",	// 56
	  "month(\"2013-09-04\") = 9",			// 57
	  "month(\"2013-09-04 02:45:01\") = 9",		// 58
	  "day(\"2013-09-04\") = 4",			// 59
	  "day(\"2013-09-04 03:45:01\") = 4",		// 60
	  "hour(\"2013-09-04 04:45:01\") = 4",		// 61
	  "hour(\"01:45:01\") = 1",			// 62
	  "minute(\"2013-09-04 05:45:01\") = 45",	// 63
	  "minute(\"01:45:01\") = 45",			// 64
	  "second(\"2013-09-04 06:45:01\") = 1",	// 65
	  "second(\"01:45:01\") = 1",			// 66
	  "weekday(\"2013-09-04\") = 4",		// 67
	  "weekday(\"2013-09-04 07:45:01\") = 4",	// 68
	  "weeknum(\"2013-09-04\") = 36",		// 69
	  "weeknum(\"2013-09-04 08:45:01\") = 36",	// 70
	  "left(\"abcdef\"; 3) = \"abc\"",		// 71
	  "mid(\"abcdef\"; 2; 3) = \"bcd\"",		// 72
	  "right(\"abcdef\"; 2) = \"ef\"",		// 73
	  "rept(\"abc\"; 3) = \"abcabcabc\"",		// 74
	  "concatenate(\"ab\"; \"cd\") = \"abcd\"",	// 75
	  "concatenate(\"ab\"; \"cd\"; \"ef\") = \"abcdef\"",		// 76
	  "concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\") = \"abcdefgh\"",	// 77
	  "concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\"; \"ij\") = \"abcdefghij\"",	// 78
	  "find(\"76\"; \"998877665544\") = 6",				// 79
	  "find(\"76\"; \"998877665544\"; 7) = 0",			// 80
	  "replace(\"1234567\"; 1; 1; \"444\") = \"444234567\"",	// 81
	  "SUBSTITUTE(\"123123123\";\"3\";\"abc\") = \"12abc12abc12abc\"",	// 82
	  "SUBSTITUTE(\"123123123\";\"3\";\"abc\";2) = \"12abc12abc123\"", 	// 83
	  "\"12345\" != \"NONE\" & \"018\" != \"NONE\"", 	// 84
	  "(\"12345\" != \"NONE\") & (\"018\" != \"NONE\")", 	// 85
	  "isNaN(nan)", 	// 86
	  "isnan(NaN)", 	// 87
	  "isnan(1.0)", 	// 88
	  "isnan(0.0 / 0.0)", 	// 89
	  "min(2.0, 1.0) = 1.0", 	// 90
	  "max(2.0, 1.0) = 2.0", 	// 91
	  "cbrt(27) = 3", 	// 92
	  "log10(1000) = 3", 	// 93
	  "sinh(3) > 0", 	// 94
	  "cosh(3) > 0", 	// 95
	  "tanh(3) > 0", 	// 96
	  "atan(3) > 0", 	// 97
	  "atan2(4, 3) > 0", 	// 98
	  "hypot(4, 3) = 5", 	// 99
	  "signum(-3.7) = -1", 	// 100
	  "signum(2.1) = 1", 	// 101
	  "str(2.1) = \"2.1\"", 	// 102
	  "str(2.1, 0) = \"2\"", 	// 103
	  "str(2.123, 2) = \"2.12\"", 	// 104
	  "str(2234.1, \"#,###.000\") = \"2,234.100\"", 	// 105
	  "has(X)", 	// 106
	  "has(Y)", 	// 107
	  "ext(\"hello_world.txt\") = \"txt\"", 	// 108
	  "replaceext(\"hello_world.txt\", \".doc\") = \"hello_world.doc\"", 	// 109
	  "replaceext(\"hello_world.txt\", \"\") = \"hello_world\"", 	// 110
	  "[hello world] = 1",			// 111
	  "'hello world' = 1",			// 112
	  "Ab_c0 = 1",				// 113
	  "contains(\"998877665544\"; \"76\") = true",				// 114
	  "contains(\"998877665544\"; \"7776\") = false",				// 115
	  "replaceall(\"hello_world.txt\", \"l\", \"L\") = \"heLLo_worLd.txt\"", 	// 116
	  "replaceall(\"hello_world.txt\", \".*l\", \"\") = \"d.txt\"", 	// 117
	  "rand(1) > 0.73", 	// 118
	  "rand(42) > 0.73", 	// 119
	  "randint(1, 10) > 0", 	// 120
	  "randint(42, 10) > 0", 	// 121
	  "startswith(\"998877665544\"; \"99\") = true",				// 122
	  "startswith(\"998877665544\"; \"77\") = false",				// 123
	  "endswith(\"998877665544\"; \"544\") = true",					// 124
	  "endswith(\"998877665544\"; \"554\") = false",				// 125
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected BooleanExpression[] getRegressionSetups() {
    return new BooleanExpression[]{new BooleanExpression()};
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BooleanExpressionTest.class);
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
