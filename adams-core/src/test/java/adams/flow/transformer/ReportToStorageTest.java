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
 * ReportToStorageTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.VariableName;
import adams.core.base.BaseRegExp;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToTextContainer;
import adams.data.io.input.SingleStringTextReader;
import adams.data.report.Field;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.DumpStorage;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for ReportToStorage actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ReportToStorageTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ReportToStorageTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("a.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("a.csv");
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
    return new TestSuite(ReportToStorageTest.class);
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
      List<AbstractActor> actors = new ArrayList<AbstractActor>();

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<PlaceholderFile>();
      files.add((PlaceholderFile) argOption.valueOf("${CWD}/src/test/resources/adams/flow/data/a.csv"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.Tee
      Tee tee = new Tee();
      List<AbstractActor> actors2 = new ArrayList<AbstractActor>();

      // Flow.Tee.BaseName
      BaseName basename = new BaseName();
      basename.setRemoveExtension(true);

      actors2.add(basename);

      // Flow.Tee.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("id"));
      actors2.add(setvariable);
      tee.setActors(actors2.toArray(new AbstractActor[0]));

      actors.add(tee);

      // Flow.TextFileReader
      TextFileReader textfilereader = new TextFileReader();
      SingleStringTextReader singlestringtextreader = new SingleStringTextReader();
      textfilereader.setReader(singlestringtextreader);

      actors.add(textfilereader);

      // Flow.Convert
      Convert convert = new Convert();
      StringToTextContainer stringtotextcontainer = new StringToTextContainer();
      argOption = (AbstractArgumentOption) stringtotextcontainer.getOptionManager().findByProperty("ID");
      argOption.setVariable("@{id}");
      convert.setConversion(stringtotextcontainer);

      actors.add(convert);

      // Flow.SetReportValue
      SetReportValue setreportvalue = new SetReportValue();
      argOption = (AbstractArgumentOption) setreportvalue.getOptionManager().findByProperty("field");
      setreportvalue.setField((Field) argOption.valueOf("id[S]"));
      argOption = (AbstractArgumentOption) setreportvalue.getOptionManager().findByProperty("value");
      argOption.setVariable("@{id}");
      actors.add(setreportvalue);

      // Flow.ReportToStorage
      ReportToStorage reporttostorage = new ReportToStorage();
      argOption = (AbstractArgumentOption) reporttostorage.getOptionManager().findByProperty("prefix");
      reporttostorage.setPrefix((String) argOption.valueOf("blah."));
      actors.add(reporttostorage);

      // Flow.Trigger
      Trigger trigger = new Trigger();
      List<AbstractActor> actors3 = new ArrayList<AbstractActor>();

      // Flow.Trigger.DumpStorage
      DumpStorage dumpstorage = new DumpStorage();
      argOption = (AbstractArgumentOption) dumpstorage.getOptionManager().findByProperty("regExp");
      dumpstorage.setRegExp((BaseRegExp) argOption.valueOf("blah.*"));
      actors3.add(dumpstorage);

      // Flow.Trigger.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors3.add(dumpfile);
      trigger.setActors(actors3.toArray(new AbstractActor[0]));

      actors.add(trigger);
      flow.setActors(actors.toArray(new AbstractActor[0]));

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

