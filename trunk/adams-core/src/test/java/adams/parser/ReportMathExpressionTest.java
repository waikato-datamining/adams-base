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
 * ReportMathExpressionTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.env.Environment;

/**
 * Tests the adams.parser.ReportMathExpression class. Run from commandline with: <p/>
 * java adams.parser.ReportMathExpressionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportMathExpressionTest
  extends AbstractExpressionEvaluatorTestCase<Double, ReportMathExpression> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ReportMathExpressionTest(String name) {
    super(name);
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
	  "ifelse(T, 0, 1)",	// 30
	  "ifelse(F, 0, 1)",	// 31
	  "ifelse([This is true], 0, 1)",	// 32
	  "ifelse([value\\tfalse], 0, 1)",	// 33
	  "ifelse((.025 <> 0.024) and (not false) or (1 <> 1), 0, 1)",	// 33
	  "length(trim(\"blah\"))",		// 34
	  "length(trim(\" blah\"))",	// 35
	  "length(trim(\"blah \"))",	// 36
	  "length(trim(\" blah \"))",	// 37
	  "if(\"12345\" != \"NONE\" & \"018\" != \"NONE\"; 1; 0)",	// 38
	  "if((\"12345\" != \"NONE\") & (\"018\" != \"NONE\"); 1; 0)",	// 39
	  "if(isNaN(nan), 1.0, 0.0)", 		// 40
	  "if(isnan(NaN), 1.0, 0.0)", 		// 41
	  "if(isnan(1.0), 1.0, 0.0)", 		// 42
	  "if(isnan(0.0 / 0.0), 1.0, 0.0)", 	// 43
	}
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ReportMathExpression[] getRegressionSetups() {
    ReportMathExpression[]	result;
    Report			report;
    
    report = new Report();
    report.setValue(new Field("X", DataType.NUMERIC), 12.0);
    report.setValue(new Field("T", DataType.BOOLEAN), true);
    report.setValue(new Field("F", DataType.BOOLEAN), false);
    report.setValue(new Field("This is true", DataType.BOOLEAN), true);
    report.setValue(new Field("value\tfalse", DataType.BOOLEAN), false);
    
    result = new ReportMathExpression[1];
    result[0] = new ReportMathExpression();
    result[0].setReport(report);
    
    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ReportMathExpressionTest.class);
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
