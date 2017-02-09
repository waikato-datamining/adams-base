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
 * MergeReportTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.Start;
import adams.flow.source.StorageValue;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.MergeReport.MergeType;
import adams.flow.transformer.MergeReport.SourceType;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for MergeReport actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MergeReportTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MergeReportTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("simple.report");
    m_TestHelper.copyResourceToTmp("simple2.report");
    m_TestHelper.deleteFileFromTmp("merged-source.txt");
    m_TestHelper.deleteFileFromTmp("merged-storage.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("simple.report");
    m_TestHelper.deleteFileFromTmp("simple2.report");
    m_TestHelper.deleteFileFromTmp("merged-source.txt");
    m_TestHelper.deleteFileFromTmp("merged-storage.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("merged-source.txt"),
          new TmpFile("merged-storage.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MergeReportTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.StorageValue
      StorageValue storagevalue = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue.getOptionManager().findByProperty("storageName");
      storagevalue.setStorageName((StorageName) argOption.valueOf("2nd"));
      actors2.add(storagevalue);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.2nd report
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("2nd report"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.2nd report.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/simple2.report"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors3.add(filesupplier);

      // Flow.2nd report.ReportFileReader
      ReportFileReader reportfilereader = new ReportFileReader();
      DefaultSimpleReportReader defaultsimplereportreader = new DefaultSimpleReportReader();
      reportfilereader.setReader(defaultsimplereportreader);

      actors3.add(reportfilereader);

      // Flow.2nd report.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("2nd"));
      actors3.add(setstoragevalue);
      trigger.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.source
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("source"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.source.FileSupplier
      FileSupplier filesupplier2 = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files2 = new ArrayList<>();
      files2.add((PlaceholderFile) argOption.valueOf("${TMP}/simple.report"));
      filesupplier2.setFiles(files2.toArray(new PlaceholderFile[0]));
      actors4.add(filesupplier2);

      // Flow.source.ReportFileReader
      ReportFileReader reportfilereader2 = new ReportFileReader();
      DefaultSimpleReportReader defaultsimplereportreader2 = new DefaultSimpleReportReader();
      reportfilereader2.setReader(defaultsimplereportreader2);

      actors4.add(reportfilereader2);

      // Flow.source.MergeReport
      MergeReport mergereport = new MergeReport();
      argOption = (AbstractArgumentOption) mergereport.getOptionManager().findByProperty("source");
      mergereport.setSource((CallableActorReference) argOption.valueOf("StorageValue"));
      actors4.add(mergereport);

      // Flow.source.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/merged-source.txt"));
      actors4.add(dumpfile);
      trigger2.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.storage
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("storage"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.storage.FileSupplier
      FileSupplier filesupplier3 = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier3.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files3 = new ArrayList<>();
      files3.add((PlaceholderFile) argOption.valueOf("${TMP}/simple.report"));
      filesupplier3.setFiles(files3.toArray(new PlaceholderFile[0]));
      actors5.add(filesupplier3);

      // Flow.storage.ReportFileReader
      ReportFileReader reportfilereader3 = new ReportFileReader();
      DefaultSimpleReportReader defaultsimplereportreader3 = new DefaultSimpleReportReader();
      reportfilereader3.setReader(defaultsimplereportreader3);

      actors5.add(reportfilereader3);

      // Flow.storage.MergeReport
      MergeReport mergereport2 = new MergeReport();
      argOption = (AbstractArgumentOption) mergereport2.getOptionManager().findByProperty("type");
      mergereport2.setType((SourceType) argOption.valueOf("STORAGE"));
      argOption = (AbstractArgumentOption) mergereport2.getOptionManager().findByProperty("storage");
      mergereport2.setStorage((StorageName) argOption.valueOf("2nd"));
      argOption = (AbstractArgumentOption) mergereport2.getOptionManager().findByProperty("merge");
      mergereport2.setMerge((MergeType) argOption.valueOf("MERGE_OTHER_WITH_CURRENT"));
      actors5.add(mergereport2);

      // Flow.storage.DumpFile
      DumpFile dumpfile2 = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
      dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/merged-storage.txt"));
      actors5.add(dumpfile2);
      trigger3.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger3);
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

