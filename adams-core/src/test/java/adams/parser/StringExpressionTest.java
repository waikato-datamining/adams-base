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
 * StringExpressionTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.base.BaseString;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.parser.StringExpression class. Run from commandline with: <br><br>
 * java adams.parser.StringExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringExpressionTest
  extends AbstractSymbolEvaluatorTestCase<String, StringExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public StringExpressionTest(String name) {
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
	  {/** no symbols necessary */},		// 1
	  {/** no symbols necessary */},		// 2
	  {/** no symbols necessary */},		// 3
	  {/** no symbols necessary */},		// 4
	  {/** no symbols necessary */},		// 5
	  {/** no symbols necessary */},		// 6
	  {/** no symbols necessary */},		// 7
	  {/** no symbols necessary */},		// 8
	  {/** no symbols necessary */},		// 9
	  {/** no symbols necessary */},		// 10
	  {/** no symbols necessary */},		// 11
	  {/** no symbols necessary */},		// 12
	  {/** no symbols necessary */},		// 13
	  {/** no symbols necessary */},		// 14
	  {/** no symbols necessary */},		// 15
	  {/** no symbols necessary */},		// 16
	  {/** no symbols necessary */},		// 17
	  {/** no symbols necessary */},		// 18
	  {/** no symbols necessary */},		// 19
	  {/** no symbols necessary */},		// 20
	  {/** no symbols necessary */},		// 21
	  {/** no symbols necessary */},		// 22
	  {/** no symbols necessary */},		// 23
	  {/** no symbols necessary */},		// 24
	  {/** no symbols necessary */},		// 25
	  {new BaseString("hello world=1")},		// 26
	  {new BaseString("hello world=1")},		// 27
	  {new BaseString("Ab_c0=1")},			// 28
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
	  "ifelse(true; \"a\"; \"b\")",					// 1
	  "trim(\"blah\")",						// 2
	  "trim(\" blah\")",						// 3
	  "trim(\"blah \")",						// 4
	  "trim(\" blah \")",						// 5
	  "left(\"abcdef\"; 3)",					// 6
	  "mid(\"abcdef\"; 2; 3)",					// 7
	  "right(\"abcdef\"; 2)",					// 8
	  "rept(\"abc\"; 3)",						// 9
	  "concatenate(\"ab\"; \"cd\")",				// 10
	  "concatenate(\"ab\"; \"cd\"; \"ef\")",			// 11
	  "concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\")",		// 12
	  "concatenate(\"ab\"; \"cd\"; \"ef\"; \"gh\"; \"ij\")",	// 13
	  "str(find(\"76\"; \"998877665544\"))",			// 14
	  "str(find(\"76\"; \"998877665544\"; 7))",			// 15
	  "replace(\"1234567\"; 1; 1; \"444\")",			// 16
	  "SUBSTITUTE(\"123123123\";\"3\";\"abc\")",			// 17
	  "SUBSTITUTE(\"123123123\";\"3\";\"abc\";2)", 			// 18
	  "str(2.1)", 							// 19
	  "str(2.1, 0)", 						// 20
	  "str(2.123, 2)", 						// 21
	  "str(2234.1, \"#,###.000\")", 				// 22
	  "ext(\"hello_world.txt\")", 					// 23
	  "replaceext(\"hello_world.txt\", \".doc\")", 			// 24
	  "replaceext(\"hello_world.txt\", \"\")", 			// 25
	  "str([hello world] = 1)",					// 26
	  "str('hello world' = 1)",					// 27
	  "str(Ab_c0 = 1)",						// 28
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected StringExpression[] getRegressionSetups() {
    return new StringExpression[]{new StringExpression()};
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(StringExpressionTest.class);
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
