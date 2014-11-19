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
 * TimeseriesFeatureGeneratorTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;

/**
 * Test for TimeseriesFeatureGenerator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimeseriesFeatureGeneratorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimeseriesFeatureGeneratorTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("timeseries.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("timeseries.csv");
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
    return new TestSuite(TimeseriesFeatureGeneratorTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[4];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/timeseries.csv");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.TimeseriesFileReader
      adams.flow.transformer.TimeseriesFileReader timeseriesfilereader4 = new adams.flow.transformer.TimeseriesFileReader();
      argOption = (AbstractArgumentOption) timeseriesfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.SpreadSheetTimeseriesReader spreadsheettimeseriesreader6 = new adams.data.io.input.SpreadSheetTimeseriesReader();
      argOption = (AbstractArgumentOption) spreadsheettimeseriesreader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader8 = new adams.data.io.input.CsvSpreadSheetReader();
      argOption = (AbstractArgumentOption) csvspreadsheetreader8.getOptionManager().findByProperty("dataRowType");
      adams.data.spreadsheet.DenseDataRow densedatarow10 = new adams.data.spreadsheet.DenseDataRow();
      csvspreadsheetreader8.setDataRowType(densedatarow10);

      argOption = (AbstractArgumentOption) csvspreadsheetreader8.getOptionManager().findByProperty("spreadSheetType");
      adams.data.spreadsheet.SpreadSheet spreadsheet12 = new adams.data.spreadsheet.SpreadSheet();
      csvspreadsheetreader8.setSpreadSheetType(spreadsheet12);

      spreadsheettimeseriesreader6.setReader(csvspreadsheetreader8);

      argOption = (AbstractArgumentOption) spreadsheettimeseriesreader6.getOptionManager().findByProperty("columnID");
      spreadsheettimeseriesreader6.setColumnID((java.lang.String) argOption.valueOf("PARENT_ID"));
      argOption = (AbstractArgumentOption) spreadsheettimeseriesreader6.getOptionManager().findByProperty("columnTimestamp");
      spreadsheettimeseriesreader6.setColumnTimestamp((java.lang.String) argOption.valueOf("TIMESTAMP"));
      argOption = (AbstractArgumentOption) spreadsheettimeseriesreader6.getOptionManager().findByProperty("columnValue");
      spreadsheettimeseriesreader6.setColumnValue((java.lang.String) argOption.valueOf("TEMPERATURE"));
      timeseriesfilereader4.setReader(spreadsheettimeseriesreader6);

      actors1[1] = timeseriesfilereader4;

      // Flow.TimeseriesFeatureGenerator
      adams.flow.transformer.TimeseriesFeatureGenerator timeseriesfeaturegenerator16 = new adams.flow.transformer.TimeseriesFeatureGenerator();
      argOption = (AbstractArgumentOption) timeseriesfeaturegenerator16.getOptionManager().findByProperty("algorithm");
      adams.data.timeseries.Values values18 = new adams.data.timeseries.Values();
      argOption = (AbstractArgumentOption) values18.getOptionManager().findByProperty("converter");
      adams.data.featureconverter.FixedNumFeatures fixednumfeatures20 = new adams.data.featureconverter.FixedNumFeatures();
      argOption = (AbstractArgumentOption) fixednumfeatures20.getOptionManager().findByProperty("converter");
      adams.data.featureconverter.TextualFeatureConverter textualfeatureconverter22 = new adams.data.featureconverter.TextualFeatureConverter();
      fixednumfeatures20.setConverter(textualfeatureconverter22);

      argOption = (AbstractArgumentOption) fixednumfeatures20.getOptionManager().findByProperty("numFeatures");
      fixednumfeatures20.setNumFeatures((Integer) argOption.valueOf("200"));
      argOption = (AbstractArgumentOption) fixednumfeatures20.getOptionManager().findByProperty("fillerType");
      fixednumfeatures20.setFillerType((adams.data.featureconverter.FixedNumFeatures.FillerType) argOption.valueOf("LAST_VALUE"));
      values18.setConverter(fixednumfeatures20);

      timeseriesfeaturegenerator16.setAlgorithm(values18);

      actors1[2] = timeseriesfeaturegenerator16;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile25 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile25.getOptionManager().findByProperty("outputFile");
      dumpfile25.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile25.setAppend(true);

      actors1[3] = dumpfile25;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener28 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener28);

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

