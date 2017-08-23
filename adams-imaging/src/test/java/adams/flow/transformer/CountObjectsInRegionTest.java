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
 * CountObjectsInRegionTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.DoubleToString;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CountObjectsInRegion actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CountObjectsInRegionTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CountObjectsInRegionTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("annotated_objects.report");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("annotated_objects.report");
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
    return new TestSuite(CountObjectsInRegionTest.class);
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

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/annotated_objects.report"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.ReportFileReader
      ReportFileReader reportfilereader = new ReportFileReader();
      DefaultSimpleReportReader defaultsimplereportreader = new DefaultSimpleReportReader();
      reportfilereader.setReader(defaultsimplereportreader);

      actors.add(reportfilereader);

      // Flow.full
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("full"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.full.CountObjectsInRegion
      CountObjectsInRegion countobjectsinregion = new CountObjectsInRegion();
      argOption = (AbstractArgumentOption) countobjectsinregion.getOptionManager().findByProperty("height");
      countobjectsinregion.setHeight((Integer) argOption.valueOf("3000"));
      argOption = (AbstractArgumentOption) countobjectsinregion.getOptionManager().findByProperty("width");
      countobjectsinregion.setWidth((Integer) argOption.valueOf("2000"));
      actors2.add(countobjectsinregion);

      // Flow.full.Convert
      Convert convert = new Convert();
      DoubleToString doubletostring = new DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring.getOptionManager().findByProperty("numDecimals");
      doubletostring.setNumDecimals((Integer) argOption.valueOf("3"));
      doubletostring.setFixedDecimals(true);

      convert.setConversion(doubletostring);

      actors2.add(convert);

      // Flow.full.StringInsert
      StringInsert stringinsert = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("position");
      stringinsert.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("full: "));
      actors2.add(stringinsert);

      // Flow.full.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors2.add(dumpfile);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.partial
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("partial"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.partial.CountObjectsInRegion
      CountObjectsInRegion countobjectsinregion2 = new CountObjectsInRegion();
      argOption = (AbstractArgumentOption) countobjectsinregion2.getOptionManager().findByProperty("height");
      countobjectsinregion2.setHeight((Integer) argOption.valueOf("3000"));
      argOption = (AbstractArgumentOption) countobjectsinregion2.getOptionManager().findByProperty("width");
      countobjectsinregion2.setWidth((Integer) argOption.valueOf("2000"));
      countobjectsinregion2.setPartialCounts(true);

      actors3.add(countobjectsinregion2);

      // Flow.partial.Convert
      Convert convert2 = new Convert();
      DoubleToString doubletostring2 = new DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring2.getOptionManager().findByProperty("numDecimals");
      doubletostring2.setNumDecimals((Integer) argOption.valueOf("3"));
      doubletostring2.setFixedDecimals(true);

      convert2.setConversion(doubletostring2);

      actors3.add(convert2);

      // Flow.partial.StringInsert
      StringInsert stringinsert2 = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("position");
      stringinsert2.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("value");
      stringinsert2.setValue((BaseString) argOption.valueOf("partial: "));
      actors3.add(stringinsert2);

      // Flow.partial.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile2.setAppend(true);

      actors3.add(dumpfile2);
      tee2.setActors(actors3.toArray(new Actor[0]));

      actors.add(tee2);
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

