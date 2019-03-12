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
 * ViaAnnotationsToReportsTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.io.SimpleFixedFilenameGenerator;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.data.io.input.LineByLineTextReader;
import adams.data.io.output.DefaultSimpleReportWriter;
import adams.data.report.Field;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.displaytype.Default;
import adams.flow.execution.NullListener;
import adams.flow.sink.ReportDisplay;
import adams.flow.source.Start;
import adams.flow.source.Variable;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.JsonFileReader.OutputType;
import adams.flow.transformer.SetVariable.UpdateType;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for ViaAnnotationsToReports actor.
 *
 * @author habdelqa
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class ViaAnnotationsToReportsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ViaAnnotationsToReportsTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("test.json");
    m_TestHelper.copyResourceToTmp("mappings.txt");
    m_TestHelper.deleteFileFromTmp("animals-cats-cute-45170.report");
    m_TestHelper.deleteFileFromTmp("dog-viszla-close.report");
    m_TestHelper.deleteFileFromTmp("pexels-photo-145939.report");
    m_TestHelper.deleteFileFromTmp("dumpfile.report");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("test.json");
    m_TestHelper.deleteFileFromTmp("mappings.txt");
    m_TestHelper.deleteFileFromTmp("animals-cats-cute-45170.report");
    m_TestHelper.deleteFileFromTmp("dog-viszla-close.report");
    m_TestHelper.deleteFileFromTmp("pexels-photo-145939.report");
    m_TestHelper.deleteFileFromTmp("dumpfile.report");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
      new TmpFile[]{
	new TmpFile("dumpfile.report")
      });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ViaAnnotationsToReportsTest.class);
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
      setvariable.setVariableName((VariableName) argOption.valueOf("label_mapping_file"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("${TMP}/mappings.txt"));
      actors.add(setvariable);

      // Flow.SetVariable (2)
      SetVariable setvariable2 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("name");
      setvariable2.setName((String) argOption.valueOf("SetVariable (2)"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((VariableName) argOption.valueOf("via_json_file"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("${TMP}/test.json"));
      actors.add(setvariable2);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.Get label mappings
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("Get label mappings"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.Get label mappings.Variable
      Variable variable = new Variable();
      argOption = (AbstractArgumentOption) variable.getOptionManager().findByProperty("variableName");
      variable.setVariableName((VariableName) argOption.valueOf("label_mapping_file"));
      StringToString stringtostring = new StringToString();
      variable.setConversion(stringtostring);

      actors2.add(variable);

      // Flow.Get label mappings.TextFileReader
      TextFileReader textfilereader = new TextFileReader();
      LineByLineTextReader linebylinetextreader = new LineByLineTextReader();
      textfilereader.setReader(linebylinetextreader);

      actors2.add(textfilereader);

      // Flow.Get label mappings.StringInsert
      StringInsert stringinsert = new StringInsert();
      stringinsert.setAfter(true);

      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("\\n"));
      actors2.add(stringinsert);

      // Flow.Get label mappings.SetVariable
      adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((VariableName) argOption.valueOf("mappings"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("updateType");
      setvariable3.setUpdateType((UpdateType) argOption.valueOf("APPEND"));
      actors2.add(setvariable3);
      trigger.setActors(actors2.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.Convert to reports
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("Convert to reports"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.Convert to reports.Variable
      Variable variable2 = new Variable();
      argOption = (AbstractArgumentOption) variable2.getOptionManager().findByProperty("variableName");
      variable2.setVariableName((VariableName) argOption.valueOf("via_json_file"));
      StringToString stringtostring2 = new StringToString();
      variable2.setConversion(stringtostring2);

      actors3.add(variable2);

      // Flow.Convert to reports.JsonFileReader
      JsonFileReader jsonfilereader = new JsonFileReader();
      argOption = (AbstractArgumentOption) jsonfilereader.getOptionManager().findByProperty("type");
      jsonfilereader.setType((OutputType) argOption.valueOf("OBJECT"));
      actors3.add(jsonfilereader);

      // Flow.Convert to reports.ViaAnnotationsToReports
      ViaAnnotationsToReports viaannotationstoreports = new ViaAnnotationsToReports();
      argOption = (AbstractArgumentOption) viaannotationstoreports.getOptionManager().findByProperty("defaultLabel");
      viaannotationstoreports.setDefaultLabel((String) argOption.valueOf("cat"));
      argOption = (AbstractArgumentOption) viaannotationstoreports.getOptionManager().findByProperty("labelMapping");
      argOption.setVariable("@{mappings}");
      actors3.add(viaannotationstoreports);

      // Flow.Convert to reports.Get report file name
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("Get report file name"));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.Convert to reports.Get report file name.GetReportValue
      GetReportValue getreportvalue = new GetReportValue();
      argOption = (AbstractArgumentOption) getreportvalue.getOptionManager().findByProperty("field");
      getreportvalue.setField((Field) argOption.valueOf("Filename[S]"));
      actors4.add(getreportvalue);

      // Flow.Convert to reports.Get report file name.BaseName
      BaseName basename = new BaseName();
      basename.setRemoveExtension(true);

      actors4.add(basename);

      // Flow.Convert to reports.Get report file name.PrependDir
      PrependDir prependdir = new PrependDir();
      argOption = (AbstractArgumentOption) prependdir.getOptionManager().findByProperty("prefix");
      prependdir.setPrefix((String) argOption.valueOf("${TMP}"));
      prependdir.setUseForwardSlashes(true);

      actors4.add(prependdir);

      // Flow.Convert to reports.Get report file name.AppendName
      AppendName appendname = new AppendName();
      argOption = (AbstractArgumentOption) appendname.getOptionManager().findByProperty("suffix");
      appendname.setSuffix((String) argOption.valueOf(".report"));
      appendname.setNoSeparator(true);

      actors4.add(appendname);

      // Flow.Convert to reports.Get report file name.SetVariable
      adams.flow.transformer.SetVariable setvariable4 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((VariableName) argOption.valueOf("report_file_name"));
      actors4.add(setvariable4);
      tee.setActors(actors4.toArray(new Actor[0]));

      actors3.add(tee);

      // Flow.Convert to reports.save to file
      Tee tee2 = new Tee();
      argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
      tee2.setName((String) argOption.valueOf("save to file"));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.Convert to reports.save to file.ReportFileWriter
      ReportFileWriter reportfilewriter = new ReportFileWriter();
      DefaultSimpleReportWriter defaultsimplereportwriter = new DefaultSimpleReportWriter();
      reportfilewriter.setWriter(defaultsimplereportwriter);

      SimpleFixedFilenameGenerator simplefixedfilenamegenerator = new SimpleFixedFilenameGenerator();
      argOption = (AbstractArgumentOption) simplefixedfilenamegenerator.getOptionManager().findByProperty("name");
      argOption.setVariable("@{report_file_name}");
      reportfilewriter.setFilenameGenerator(simplefixedfilenamegenerator);

      actors5.add(reportfilewriter);
      tee2.setActors(actors5.toArray(new Actor[0]));

      actors3.add(tee2);

      // Flow.Convert to reports.ReportDisplay
      ReportDisplay reportdisplay = new ReportDisplay();
      Default default_ = new Default();
      reportdisplay.setDisplayType(default_);

      actors3.add(reportdisplay);
      trigger2.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger2);
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

