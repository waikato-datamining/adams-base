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
 * AbstractSymbolEvaluatorTestCase.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.CleanUpHandler;
import adams.core.Destroyable;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.management.LocaleHelper;
import adams.test.TmpFile;

/**
 * Ancestor for symbol evaluator test cases.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <D> the type of data the evaluator generates
 * @param <E> the expression evaluator
 */
public abstract class AbstractSymbolEvaluatorTestCase<D extends Object, E extends AbstractSymbolEvaluator>
  extends AbstractExpressionEvaluatorTestCase<D, E> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public AbstractSymbolEvaluatorTestCase(String name) {
    super(name);
  }

  /**
   * Obsolete.
   *
   * @param expressions	the expressions to work on
   * @param scheme	the scheme to process the data with
   * @return		the generated statistics
   */
  @Override
  protected Object[] process(String[] expressions, E scheme) {
    throw new IllegalStateException("Use 'process(String[],BaseString[][],E)' instead");
  }

  /**
   * Generates output from the input expressions.
   *
   * @param expressions	the expressions to work on
   * @param symbols	the symbols to use
   * @param scheme	the scheme to process the data with
   * @return		the generated statistics
   */
  protected Object[] process(String[] expressions, BaseString[][] symbols, E scheme) {
    Object[]	result;
    int		i;

    result = new Object[expressions.length];

    for (i = 0; i < expressions.length; i++) {
      scheme.setExpression(expressions[i]);
      scheme.setSymbols(symbols[i]);
      try {
	result[i] = scheme.evaluate();
      }
      catch (Exception e)  {
	result[i] = null;
      }
    }

    return result;
  }

  /**
   * Saves the generated output output as file.
   *
   * @param expressions	the expressions used to generate the output
   * @param data	the generated output data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  @Override
  protected boolean save(String[] expressions, Object[] data, String filename) {
    throw new IllegalStateException("Use 'save(String[],BaseString[][],Object[],String)' instead");
  }

  /**
   * Saves the generated output output as file.
   *
   * @param expressions	the expressions used to generate the output
   * @param symbols	the symbols used for generating the output
   * @param data	the generated output data
   * @param filename	the file to save the data to (in the temp directory)
   * @return		true if successfully saved
   */
  protected boolean save(String[] expressions, BaseString[][] symbols, Object[] data, String filename) {
    String[]	content;
    int		i;
    String	dataStr;

    content = new String[data.length];
    for (i = 0; i < data.length; i++) {
      if (data[i] instanceof Number)
	dataStr = Utils.doubleToStringFixed(((Number) data[i]).doubleValue(), 8, LocaleHelper.valueOf("en_US"));
      else
	dataStr = "" + data[i];
      if (symbols[i].length > 0)
	content[i] = "'" + expressions[i] + "' using " + Utils.arrayToString(symbols[i]) + ": " + dataStr;
      else
	content[i] = "'" + expressions[i] + ": " + dataStr;
    }

    return FileUtils.saveToFile(content, new TmpFile(filename));
  }

  /**
   * Returns the symbols used in the regression test.
   *
   * @return		the symbols
   */
  protected abstract BaseString[][][] getRegressionSymbols();

  /**
   * Compares the processed data against previously saved output data.
   */
  @Override
  public void testRegression() {
    String[][]		expr;
    BaseString[][][]	symbols;
    Object[]		processed;
    boolean		ok;
    String		regression;
    int			i;
    E[]			setups;
    String[]		output;
    TmpFile[]		outputFiles;

    expr    = getRegressionExpressions();
    symbols = getRegressionSymbols();
    output  = new String[expr.length];
    setups  = getRegressionSetups();
    assertEquals("Number of expression arrays and setups differ!", expr.length, setups.length);
    assertEquals("Number of symbol arrays and setups differ!", symbols.length, setups.length);
    for (i = 0; i < setups.length; i++)
      assertEquals("Number of symbol arrays and expressions differ (#" + i + ")!", expr[i].length, symbols[i].length);

    // process data
    for (i = 0; i < expr.length; i++) {
      processed = process(expr[i], symbols[i], setups[i]);
      assertNotNull("Failed to process data?", processed);

      output[i] = createOutputFilename(i);
      ok        = save(expr[i], symbols[i], processed, output[i]);
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
      if (setups[i] instanceof Destroyable)
	((Destroyable) setups[i]).destroy();
      else if (setups[i] instanceof CleanUpHandler)
	((CleanUpHandler) setups[i]).cleanUp();
      m_TestHelper.deleteFileFromTmp(output[i]);
    }
    cleanUpAfterRegression();
  }

  /**
   * For further cleaning up after the regression tests.
   * <br><br>
   * Default implementation does nothing.
   */
  @Override
  protected void cleanUpAfterRegression() {
  }
}
