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
 * WekaInstancesHistogramRangesTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.DoubleMatrixToSpreadSheet;
import adams.data.conversion.NumberToDouble;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.random.JavaRandomDouble;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.RandomNumberGenerator;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for WekaInstancesHistogramRanges actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class WekaInstancesHistogramRangesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaInstancesHistogramRangesTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaInstancesHistogramRangesTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.RandomNumberGenerator
      RandomNumberGenerator randomnumbergenerator = new RandomNumberGenerator();
      randomnumbergenerator.setOutputArray(true);

      JavaRandomDouble javarandomdouble = new JavaRandomDouble();
      randomnumbergenerator.setGenerator(javarandomdouble);

      actors.add(randomnumbergenerator);

      // Flow.ArrayProcess
      ArrayProcess arrayprocess = new ArrayProcess();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.ArrayProcess.Convert
      Convert convert = new Convert();
      NumberToDouble numbertodouble = new NumberToDouble();
      convert.setConversion(numbertodouble);

      actors2.add(convert);
      arrayprocess.setActors(actors2.toArray(new Actor[0]));

      actors.add(arrayprocess);

      // Flow.SequenceToArray
      SequenceToArray sequencetoarray = new SequenceToArray();
      actors.add(sequencetoarray);

      // Flow.Convert
      Convert convert2 = new Convert();
      DoubleMatrixToSpreadSheet doublematrixtospreadsheet = new DoubleMatrixToSpreadSheet();
      DenseDataRow densedatarow = new DenseDataRow();
      doublematrixtospreadsheet.setDataRowType(densedatarow);

      DefaultSpreadSheet defaultspreadsheet = new DefaultSpreadSheet();
      doublematrixtospreadsheet.setSpreadSheetType(defaultspreadsheet);

      convert2.setConversion(doublematrixtospreadsheet);

      actors.add(convert2);

      // Flow.Convert (2)
      Convert convert3 = new Convert();
      argOption = (AbstractArgumentOption) convert3.getOptionManager().findByProperty("name");
      convert3.setName((String) argOption.valueOf("Convert (2)"));
      SpreadSheetToWekaInstances spreadsheettowekainstances = new SpreadSheetToWekaInstances();
      convert3.setConversion(spreadsheettowekainstances);

      actors.add(convert3);

      // Flow.WekaInstancesHistogramRanges
      WekaInstancesHistogramRanges wekainstanceshistogramranges = new WekaInstancesHistogramRanges();
      wekainstanceshistogramranges.setOutputArray(true);

      argOption = (AbstractArgumentOption) wekainstanceshistogramranges.getOptionManager().findByProperty("dataType");
      wekainstanceshistogramranges.setDataType((WekaInstancesStatisticDataType) argOption.valueOf("ROW_BY_INDEX"));
      argOption = (AbstractArgumentOption) wekainstanceshistogramranges.getOptionManager().findByProperty("locations");
      List<BaseString> locations = new ArrayList<>();
      locations.add((BaseString) argOption.valueOf("1"));
      wekainstanceshistogramranges.setLocations(locations.toArray(new BaseString[0]));
      argOption = (AbstractArgumentOption) wekainstanceshistogramranges.getOptionManager().findByProperty("numBins");
      wekainstanceshistogramranges.setNumBins((Integer) argOption.valueOf("10"));
      actors.add(wekainstanceshistogramranges);

      // Flow.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors.add(stringjoin);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors.add(dumpfile);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }
    
    return flow;
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(adams.env.Environment.class);
    runTest(suite());
  }
}

