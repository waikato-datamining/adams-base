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
 * WekaEvaluationToThresholdCurveTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import java.io.StringWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.Utils;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.test.TmpFile;


/**
 * Tests the WekaEvaluationToThresholdCurve conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5356 $
 */
public class WekaEvaluationToThresholdCurveTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public WekaEvaluationToThresholdCurveTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("labor.csv");
  }
  
  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("labor.csv");
    
    super.tearDown();
  }

  /**
   * Turns the data object into a useful string representation.
   *
   * @param data	the object to convert
   * @return		the string representation
   */
  @Override
  protected String toString(Object data) {
    String			result;
    Instances			inst;
    WekaInstancesToSpreadSheet	conv;
    String			msg;
    SpreadSheet			sheet;
    CsvSpreadSheetWriter	writer;
    StringWriter		swriter;
    
    inst = (Instances) data;
    conv = new WekaInstancesToSpreadSheet();
    conv.setInput(inst);
    msg  = conv.convert();
    if (msg == null) {
      sheet   = (SpreadSheet) conv.getOutput();
      swriter = new StringWriter();
      writer  = new CsvSpreadSheetWriter();
      writer.setNumberFormat("0.000000");
      writer.write(sheet, swriter);
      result = swriter.toString();
    }
    else {
      result = null;
    }
    conv.cleanUp();
    
    return result;
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Evaluation[]	result;
    Instances		data;
    
    try {
      data = DataSource.read(new TmpFile("labor.csv").getAbsolutePath());
      data.setClassIndex(data.numAttributes() - 1);

      result    = new Evaluation[1];
      result[0] = new Evaluation(data);
      result[0].crossValidateModel(new J48(), data, 10, new Random(1));
    }
    catch (Exception e) {
      result = null;
      fail(Utils.throwableToString(e));
    }

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    WekaEvaluationToThresholdCurve[]	result;

    result    = new WekaEvaluationToThresholdCurve[1];
    result[0] = new WekaEvaluationToThresholdCurve();

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Compares the processed data against previously saved output data.
   */
  @Override
  public void testRegression() {
    // TODO completely different results on 32bit??
  }
}
