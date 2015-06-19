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
 * TimeseriesFileWriterTest.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.base.BaseClassname;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test for TimeseriesFileWriter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TimeseriesFileWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TimeseriesFileWriterTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("wine.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("wine.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.csv")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TimeseriesFileWriterTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[5];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[1];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/wine.csv");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.TimeseriesFileReader
      adams.flow.transformer.TimeseriesFileReader timeseriesfilereader4 = new adams.flow.transformer.TimeseriesFileReader();
      argOption = (AbstractArgumentOption) timeseriesfilereader4.getOptionManager().findByProperty("reader");
      adams.data.io.input.SpreadSheetTimeseriesReader spreadsheettimeseriesreader6 = new adams.data.io.input.SpreadSheetTimeseriesReader();
      argOption = (AbstractArgumentOption) spreadsheettimeseriesreader6.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader csvspreadsheetreader8 = new adams.data.io.input.CsvSpreadSheetReader();
      spreadsheettimeseriesreader6.setReader(csvspreadsheetreader8);

      timeseriesfilereader4.setReader(spreadsheettimeseriesreader6);

      actors1[1] = timeseriesfilereader4;

      // Flow.SetID
      adams.flow.transformer.SetID setid9 = new adams.flow.transformer.SetID();
      argOption = (AbstractArgumentOption) setid9.getOptionManager().findByProperty("ID");
      setid9.setID((java.lang.String) argOption.valueOf("dumpfile"));
      actors1[2] = setid9;

      // Flow.Cast
      adams.flow.control.Cast cast11 = new adams.flow.control.Cast();
      argOption = (AbstractArgumentOption) cast11.getOptionManager().findByProperty("classname");
      cast11.setClassname((BaseClassname) argOption.valueOf("adams.data.timeseries.Timeseries"));
      actors1[3] = cast11;

      // Flow.TimeseriesFileWriter
      adams.flow.transformer.TimeseriesFileWriter timeseriesfilewriter13 = new adams.flow.transformer.TimeseriesFileWriter();
      argOption = (AbstractArgumentOption) timeseriesfilewriter13.getOptionManager().findByProperty("writer");
      adams.data.io.output.SpreadSheetTimeseriesWriter spreadsheettimeserieswriter15 = new adams.data.io.output.SpreadSheetTimeseriesWriter();
      argOption = (AbstractArgumentOption) spreadsheettimeserieswriter15.getOptionManager().findByProperty("writer");
      adams.data.io.output.CsvSpreadSheetWriter csvspreadsheetwriter17 = new adams.data.io.output.CsvSpreadSheetWriter();
      spreadsheettimeserieswriter15.setWriter(csvspreadsheetwriter17);

      timeseriesfilewriter13.setWriter(spreadsheettimeserieswriter15);

      argOption = (AbstractArgumentOption) timeseriesfilewriter13.getOptionManager().findByProperty("outputDir");
      timeseriesfilewriter13.setOutputDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));
      actors1[4] = timeseriesfilewriter13;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener20 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener20);

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

