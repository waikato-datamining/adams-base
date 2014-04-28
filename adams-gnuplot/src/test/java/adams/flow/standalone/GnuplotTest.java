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
 * GnuplotTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Test for Gnuplot actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class GnuplotTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GnuplotTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("bolts_plot.data");
    m_TestHelper.deleteFileFromTmp("bolts_plot.script");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("bolts_plot.data");
    m_TestHelper.deleteFileFromTmp("bolts_plot.script");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(GnuplotTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((adams.core.base.BaseText) argOption.valueOf("This flow generates a Gnuplot script file for the \"bolts\" UCI dataset.\nIt then executes gnuplot (executable needs to be on the PATH).\nThe plot closes automatically after 5 seconds."));

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp2 = new adams.flow.core.AbstractActor[6];
      adams.flow.standalone.SetVariable tmp3 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("name");
      tmp3.setName((java.lang.String) argOption.valueOf("SetVariable (data)"));

      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("variableName");
      tmp3.setVariableName((adams.core.VariableName) argOption.valueOf("data_file"));

      argOption = (AbstractArgumentOption) tmp3.getOptionManager().findByProperty("variableValue");
      tmp3.setVariableValue((java.lang.String) argOption.valueOf("${TMP}/bolts_plot.data"));

      tmp2[0] = tmp3;
      adams.flow.standalone.SetVariable tmp7 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("name");
      tmp7.setName((java.lang.String) argOption.valueOf("SetVariable (script)"));

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableName");
      tmp7.setVariableName((adams.core.VariableName) argOption.valueOf("script_file"));

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("variableValue");
      tmp7.setVariableValue((java.lang.String) argOption.valueOf("${TMP}/bolts_plot.script"));

      tmp2[1] = tmp7;
      adams.flow.source.Start tmp11 = new adams.flow.source.Start();
      tmp2[2] = tmp11;
      adams.flow.control.Trigger tmp12 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("name");
      tmp12.setName((java.lang.String) argOption.valueOf("generate data file"));

      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp14 = new adams.flow.core.AbstractActor[3];
      adams.flow.source.FileSupplier tmp15 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("files");
      tmp15.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv")});

      tmp14[0] = tmp15;
      adams.flow.transformer.SpreadSheetFileReader tmp17 = new adams.flow.transformer.SpreadSheetFileReader();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("reader");
      adams.data.io.input.CsvSpreadSheetReader tmp19 = new adams.data.io.input.CsvSpreadSheetReader();
      tmp17.setReader(tmp19);

      tmp14[1] = tmp17;
      adams.flow.sink.SpreadSheetFileWriter tmp20 = new adams.flow.sink.SpreadSheetFileWriter();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("outputFile");
      argOption.setVariable("@{data_file}");

      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("writer");
      adams.data.io.output.GnuplotSpreadSheetWriter tmp22 = new adams.data.io.output.GnuplotSpreadSheetWriter();
      tmp20.setWriter(tmp22);

      tmp14[2] = tmp20;
      tmp12.setActors(tmp14);

      tmp2[3] = tmp12;
      adams.flow.control.Trigger tmp23 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("name");
      tmp23.setName((java.lang.String) argOption.valueOf("generate script file"));

      argOption = (AbstractArgumentOption) tmp23.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp25 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.Variable tmp26 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp26.getOptionManager().findByProperty("variableName");
      tmp26.setVariableName((adams.core.VariableName) argOption.valueOf("data_file"));

      tmp25[0] = tmp26;
      adams.flow.sink.GnuplotScript tmp28 = new adams.flow.sink.GnuplotScript();
      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("outputFile");
      argOption.setVariable("@{script_file}");

      argOption = (AbstractArgumentOption) tmp28.getOptionManager().findByProperty("scriptlet");
      adams.core.gnuplot.MultiScriptlet tmp30 = new adams.core.gnuplot.MultiScriptlet();
      argOption = (AbstractArgumentOption) tmp30.getOptionManager().findByProperty("scriptlets");
      adams.core.gnuplot.AbstractScriptlet[] tmp31 = new adams.core.gnuplot.AbstractScriptlet[4];
      adams.core.gnuplot.Initialize tmp32 = new adams.core.gnuplot.Initialize();
      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("title");
      tmp32.setTitle((java.lang.String) argOption.valueOf("bolts dataset"));

      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("XLabel");
      tmp32.setXLabel((java.lang.String) argOption.valueOf("target variable"));

      argOption = (AbstractArgumentOption) tmp32.getOptionManager().findByProperty("YLabel");
      tmp32.setYLabel((java.lang.String) argOption.valueOf("input variables"));

      tmp31[0] = tmp32;
      adams.core.gnuplot.SimplePlot tmp36 = new adams.core.gnuplot.SimplePlot();
      argOption = (AbstractArgumentOption) tmp36.getOptionManager().findByProperty("columns");
      tmp36.setColumns((java.lang.String) argOption.valueOf("8:2"));

      argOption = (AbstractArgumentOption) tmp36.getOptionManager().findByProperty("plotType");
      tmp36.setPlotType((adams.core.gnuplot.SimplePlot.PlotType) argOption.valueOf("POINTS"));

      argOption = (AbstractArgumentOption) tmp36.getOptionManager().findByProperty("plotName");
      tmp36.setPlotName((java.lang.String) argOption.valueOf("speed1 vs t20bolt"));

      tmp36.setFirstPlot(true);

      tmp31[1] = tmp36;
      adams.core.gnuplot.SimplePlot tmp40 = new adams.core.gnuplot.SimplePlot();
      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("columns");
      tmp40.setColumns((java.lang.String) argOption.valueOf("8:7"));

      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("plotType");
      tmp40.setPlotType((adams.core.gnuplot.SimplePlot.PlotType) argOption.valueOf("POINTS"));

      argOption = (AbstractArgumentOption) tmp40.getOptionManager().findByProperty("plotName");
      tmp40.setPlotName((java.lang.String) argOption.valueOf("time vs t20bolt"));

      tmp31[2] = tmp40;
      adams.core.gnuplot.Pause tmp44 = new adams.core.gnuplot.Pause();
      argOption = (AbstractArgumentOption) tmp44.getOptionManager().findByProperty("waitingPeriod");
      tmp44.setWaitingPeriod((Integer) argOption.valueOf("1"));

      argOption = (AbstractArgumentOption) tmp44.getOptionManager().findByProperty("message");
      tmp44.setMessage((java.lang.String) argOption.valueOf("Press <Enter> to close the plot..."));

      tmp31[3] = tmp44;
      tmp30.setScriptlets(tmp31);

      tmp30.setUseSingleDataFile(true);

      tmp28.setScriptlet(tmp30);

      tmp25[1] = tmp28;
      tmp23.setActors(tmp25);

      tmp2[4] = tmp23;
      adams.flow.control.Trigger tmp47 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp47.getOptionManager().findByProperty("name");
      tmp47.setName((java.lang.String) argOption.valueOf("execute gnuplot"));

      argOption = (AbstractArgumentOption) tmp47.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp49 = new adams.flow.core.AbstractActor[1];
      adams.flow.standalone.Gnuplot tmp50 = new adams.flow.standalone.Gnuplot();
      argOption = (AbstractArgumentOption) tmp50.getOptionManager().findByProperty("scriptFile");
      argOption.setVariable("@{script_file}");

      tmp49[0] = tmp50;
      tmp47.setActors(tmp49);

      tmp2[5] = tmp47;
      flow.setActors(tmp2);

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

