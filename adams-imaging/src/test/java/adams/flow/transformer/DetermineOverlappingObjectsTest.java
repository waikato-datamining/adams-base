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
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.data.io.input.DefaultSimpleReportReader;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.IntersectOverUnionRatio;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.Start;
import adams.flow.source.Variable;
import adams.flow.standalone.DeleteStorageValue;
import adams.flow.standalone.SetVariable;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for IntersectOverUnion actor.
 *
 * @author habdelqa
 * @author FracPete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class DetermineOverlappingObjectsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DetermineOverlappingObjectsTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
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
    return new TestSuite(DetermineOverlappingObjectsTest.class);
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

      // IOU
      {
        // Flow.CompareIOU
        Trigger triggerCompareIOU = new Trigger();
        argOption = (AbstractArgumentOption) triggerCompareIOU.getOptionManager().findByProperty("name");
        triggerCompareIOU.setName((String) argOption.valueOf("CompareIOU"));
        List<Actor> actorsIOU = new ArrayList<>();

        // Flow.CompareIOU.Variable
        Variable variable2 = new Variable();
        argOption = (AbstractArgumentOption) variable2.getOptionManager().findByProperty("variableName");
        variable2.setVariableName((VariableName) argOption.valueOf("gt_file"));
        StringToString stringtostring2 = new StringToString();
        variable2.setConversion(stringtostring2);

        actorsIOU.add(variable2);

        // Flow.CompareIOU.ReportFileReader
        ReportFileReader reportfilereader2 = new ReportFileReader();
        DefaultSimpleReportReader defaultsimplereportreader2 = new DefaultSimpleReportReader();
        reportfilereader2.setReader(defaultsimplereportreader2);

        actorsIOU.add(reportfilereader2);

        // Flow.CompareIOU.DetermineOverlappingObjects
        IntersectOverUnionRatio iou = new IntersectOverUnionRatio();
        iou.setMinIntersectOverUnionRatio(0.5);
        iou.setLabelKey("type");
        iou.setUseOtherObject(true);
        iou.setAdditionalObject(true);
        DetermineOverlappingObjects overlappingIOU = new DetermineOverlappingObjects();
        overlappingIOU.setStorageName(new StorageName("pred"));
        overlappingIOU.setAlgorithm(iou);
        actorsIOU.add(overlappingIOU);

        // Flow.CompareIOU.DumpFile
        DumpFile dumpfile = new DumpFile();
        argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
        dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
        actorsIOU.add(dumpfile);
        triggerCompareIOU.setActors(actorsIOU.toArray(new Actor[0]));

        actors.add(triggerCompareIOU);
      }

      // area
      {
        // Flow.CompareArea
        Trigger triggerArea = new Trigger();
        argOption = (AbstractArgumentOption) triggerArea.getOptionManager().findByProperty("name");
        triggerArea.setName((String) argOption.valueOf("CompareArea"));
        List<Actor> actorsArea = new ArrayList<>();

        // Flow.CompareArea.Variable
        Variable variable2 = new Variable();
        argOption = (AbstractArgumentOption) variable2.getOptionManager().findByProperty("variableName");
        variable2.setVariableName((VariableName) argOption.valueOf("gt_file"));
        StringToString stringtostring2 = new StringToString();
        variable2.setConversion(stringtostring2);

        actorsArea.add(variable2);

        // Flow.CompareArea.ReportFileReader
        ReportFileReader reportfilereader2 = new ReportFileReader();
        DefaultSimpleReportReader defaultsimplereportreader2 = new DefaultSimpleReportReader();
        reportfilereader2.setReader(defaultsimplereportreader2);

        actorsArea.add(reportfilereader2);

        // Flow.CompareArea.DetermineOverlappingObjects
        AreaRatio area = new AreaRatio();
        area.setMinOverlapRatio(0.5);
        area.setLabelKey("type");
        area.setUseOtherObject(true);
        area.setAdditionalObject(true);
        area.setAverageRatio(true);
        DetermineOverlappingObjects overlappingArea = new DetermineOverlappingObjects();
        overlappingArea.setStorageName(new StorageName("pred"));
        overlappingArea.setAlgorithm(area);
        actorsArea.add(overlappingArea);

        // Flow.CompareArea.DumpFile
        DumpFile dumpfile = new DumpFile();
        argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
        dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
        dumpfile.setAppend(true);
        actorsArea.add(dumpfile);
        triggerArea.setActors(actorsArea.toArray(new Actor[0]));

        actors.add(triggerArea);
      }

      // Flow.Clean
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("Clean"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.Clean.DeleteStorageValue
      DeleteStorageValue deletestoragevalue = new DeleteStorageValue();
      argOption = (AbstractArgumentOption) deletestoragevalue.getOptionManager().findByProperty("storageName");
      deletestoragevalue.setStorageName((StorageName) argOption.valueOf("pred"));
      actors4.add(deletestoragevalue);
      trigger3.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger3);
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
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}

