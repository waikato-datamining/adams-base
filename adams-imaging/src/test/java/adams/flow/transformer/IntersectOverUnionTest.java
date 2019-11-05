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
 * IntersectOverUnionTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.env.Environment;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionUtils;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.flow.core.AbstractActor;
import adams.flow.control.Flow;
import adams.flow.AbstractFlowTest;
import adams.test.TmpFile;
import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.data.conversion.StringToString;
import adams.data.conversion.UnknownToUnknown;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.objectfinder.AllFinder;
import adams.data.report.Field;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.Start;
import adams.flow.source.StorageValue;
import adams.flow.source.Variable;
import adams.flow.standalone.DeleteStorageValue;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.GetReportValue;
import adams.flow.transformer.IntersectOverUnion;
import adams.flow.transformer.ReportFileReader;
import adams.flow.transformer.SetStorageValue;

/**
 * Test for IntersectOverUnion actor.
 *
 * @author habdelqa
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class IntersectOverUnionTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public IntersectOverUnionTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("image_object_overlap_iou_gt.report");
    m_TestHelper.copyResourceToTmp("image_object_overlap_iou_pred.report");
    //m_TestHelper.copyResourceToTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("image_object_overlap_iou_gt.report");
    m_TestHelper.deleteFileFromTmp("image_object_overlap_iou_pred.report");
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
    return new TestSuite(IntersectOverUnionTest.class);
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

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("gt_file"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("${TMP}/image_object_overlap_iou_gt.report"));
      actors.add(setvariable);

      // Flow.SetVariable (2)
      SetVariable setvariable2 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("name");
      setvariable2.setName((String) argOption.valueOf("SetVariable (2)"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((VariableName) argOption.valueOf("pred_file"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("${TMP}/image_object_overlap_iou_pred.report"));
      actors.add(setvariable2);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.Load Pred
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("Load Pred"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.Load Pred.Variable
      Variable variable = new Variable();
      argOption = (AbstractArgumentOption) variable.getOptionManager().findByProperty("variableName");
      variable.setVariableName((VariableName) argOption.valueOf("pred_file"));
      StringToString stringtostring = new StringToString();
      variable.setConversion(stringtostring);

      actors2.add(variable);

      // Flow.Load Pred.ReportFileReader
      ReportFileReader reportfilereader = new ReportFileReader();
      DefaultSimpleReportReader defaultsimplereportreader = new DefaultSimpleReportReader();
      reportfilereader.setReader(defaultsimplereportreader);

      actors2.add(reportfilereader);

      // Flow.Load Pred.SetStorageValue
      SetStorageValue setstoragevalue = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
      setstoragevalue.setStorageName((StorageName) argOption.valueOf("pred"));
      actors2.add(setstoragevalue);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.Compare
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("Compare"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.Compare.Variable
      Variable variable2 = new Variable();
      argOption = (AbstractArgumentOption) variable2.getOptionManager().findByProperty("variableName");
      variable2.setVariableName((VariableName) argOption.valueOf("gt_file"));
      StringToString stringtostring2 = new StringToString();
      variable2.setConversion(stringtostring2);

      actors3.add(variable2);

      // Flow.Compare.ReportFileReader
      ReportFileReader reportfilereader2 = new ReportFileReader();
      DefaultSimpleReportReader defaultsimplereportreader2 = new DefaultSimpleReportReader();
      reportfilereader2.setReader(defaultsimplereportreader2);

      actors3.add(reportfilereader2);

      // Flow.Compare.IntersectOverUnion
      IntersectOverUnion intersectoverunion = new IntersectOverUnion();
      argOption = (AbstractArgumentOption) intersectoverunion.getOptionManager().findByProperty("storageName");
      intersectoverunion.setStorageName((StorageName) argOption.valueOf("pred"));
      AllFinder allfinder = new AllFinder();
      intersectoverunion.setFinder(allfinder);

      argOption = (AbstractArgumentOption) intersectoverunion.getOptionManager().findByProperty("minIntersectOverUnionRatio");
      intersectoverunion.setMinIntersectOverUnionRatio((Double) argOption.valueOf("0.5"));
      intersectoverunion.setUseOtherObject(true);

      actors3.add(intersectoverunion);

      // Flow.Compare.SetStorageValue
      SetStorageValue setstoragevalue2 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
      setstoragevalue2.setStorageName((StorageName) argOption.valueOf("result"));
      actors3.add(setstoragevalue2);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.Save to file
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("Save to file"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.Save to file.StorageValue
      StorageValue storagevalue = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue.getOptionManager().findByProperty("storageName");
      storagevalue.setStorageName((StorageName) argOption.valueOf("result"));
      UnknownToUnknown unknowntounknown = new UnknownToUnknown();
      storagevalue.setConversion(unknowntounknown);

      actors4.add(storagevalue);

      // Flow.Save to file.GetReportValue
      GetReportValue getreportvalue = new GetReportValue();
      argOption = (AbstractArgumentOption) getreportvalue.getOptionManager().findByProperty("field");
      getreportvalue.setField((Field) argOption.valueOf("Object.1.iou_highest[N]"));
      actors4.add(getreportvalue);

      // Flow.Save to file.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors4.add(dumpfile);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);

      // Flow.Clean
      Trigger trigger4 = new Trigger();
      argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
      trigger4.setName((String) argOption.valueOf("Clean"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.Clean.DeleteStorageValue
      DeleteStorageValue deletestoragevalue = new DeleteStorageValue();
      argOption = (AbstractArgumentOption) deletestoragevalue.getOptionManager().findByProperty("storageName");
      deletestoragevalue.setStorageName((StorageName) argOption.valueOf("pred"));
      actors5.add(deletestoragevalue);

      // Flow.Clean.DeleteStorageValue (2)
      DeleteStorageValue deletestoragevalue2 = new DeleteStorageValue();
      argOption = (AbstractArgumentOption) deletestoragevalue2.getOptionManager().findByProperty("name");
      deletestoragevalue2.setName((String) argOption.valueOf("DeleteStorageValue (2)"));
      argOption = (AbstractArgumentOption) deletestoragevalue2.getOptionManager().findByProperty("storageName");
      deletestoragevalue2.setStorageName((StorageName) argOption.valueOf("result"));
      actors5.add(deletestoragevalue2);
      trigger4.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger4);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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

