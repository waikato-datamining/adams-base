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
 * AbstractExpressionEvaluatorTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.option.OptionUtils;
import adams.test.AbstractTestHelper;
import adams.test.AdamsTestCase;
import adams.test.TestHelper;
import adams.test.TmpFile;

import java.util.Date;

/**
 * Ancestor for expression evaluator test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <D> the type of data the evaluator generates
 * @param <E> the expression evaluator
 */
public abstract class AbstractExpressionEvaluatorTestCase<D extends Object, E extends AbstractExpressionEvaluator>
  extends AdamsTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractExpressionEvaluatorTestCase(String name) {
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
   * Generates output from the input expressions.
   *
   * @param expressions	the expressions to work on
   * @param scheme	the scheme to process the data with
   * @return		the generated statistics
   */
  protected Object[] process(String[] expressions, E scheme) {
    Object[]	result;
    int		i;

    result = new Object[expressions.length];

    for (i = 0; i < expressions.length; i++) {
      scheme.setExpression(expressions[i]);
      try {
	result[i] = scheme.evaluate();
      }
      catch (Exception e)  {
	System.err.println("Failed to evaluate expression '" + expressions[i] + "' using '" + OptionUtils.getCommandLine(scheme) + "':");
	e.printStackTrace();
	result[i] = null;
      }
    }

    return result;
  }

  /**
   * Returns the expressions used in the regression test.
   *
   * @return		the data
   */
  protected abstract String[][] getRegressionExpressions();

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected abstract E[] getRegressionSetups();

  /**
   * Saves the generated output output as file.
   *
   * @param expressions	the expressions used to generate the output
   * @param data	the generated output data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(String[] expressions, Object[] data, String filename) {
    String[]	content;
    int		i;
    String	dataStr;
    DateFormat	formatter;

    content = new String[data.length];
    formatter = DateUtils.getTimestampFormatterMsecs();
    for (i = 0; i < data.length; i++) {
      if (data[i] instanceof Number)
	dataStr = Utils.doubleToStringFixed(((Number) data[i]).doubleValue(), 8);
      else if (data[i] instanceof Date)
	dataStr = formatter.format((Date) data[i]);
      else
	dataStr = "" + data[i];
      content[i] = expressions[i] + ": " + dataStr;
    }

    return FileUtils.saveToFile(content, new TmpFile(filename));
  }

  /**
   * Creates an output filename based on the number of the test.
   *
   * @param no		the number of the test
   * @return		the generated output filename (no path)
   */
  protected String createOutputFilename(int no) {
    return "out-" + no;
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  public void testRegression() {
    String[][]		expr;
    Object[]		processed;
    boolean		ok;
    String		regression;
    int			i;
    E[]			setups;
    String[]		output;
    TmpFile[]		outputFiles;

    if (m_NoRegressionTest)
      return;

    expr    = getRegressionExpressions();
    output  = new String[expr.length];
    setups  = getRegressionSetups();
    assertEquals("Number of expression arrays and setups differ!", expr.length, setups.length);

    // process data
    for (i = 0; i < expr.length; i++) {
      processed = process(expr[i], setups[i]);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(i);
      ok        = save(expr[i], processed, output[i]);
      assertTrue("Failed to save regression data?", ok);
    }

    // test regression
    outputFiles = new TmpFile[output.length];
    for (i = 0; i < output.length; i++)
      outputFiles[i] = new TmpFile(output[i]);
    regression = m_Regression.compare(outputFiles);
    assertNull("Output differs:\n" + regression, regression);

    // remove output, clean up scheme
    for (i = 0; i < output.length; i++) {
      setups[i].destroy();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void cleanUpAfterRegression() {
  }
}
