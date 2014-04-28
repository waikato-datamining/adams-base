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
 * GnuplotScriptTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.test.Platform;
import adams.test.TmpFile;

/**
 * Test for GnuplotScript actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class GnuplotScriptTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GnuplotScriptTest(String name) {
    super(name);
  }

  /**
   * Returns the platform this test class is for.
   * 
   * @return		the platform.
   */
  @Override
  protected HashSet<Platform> getPlatforms() {
    return new HashSet<Platform>(Arrays.asList(new Platform[]{Platform.LINUX}));
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("bolts.csv", "out.data");
    m_TestHelper.deleteFileFromTmp("dumpfile.script");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("out.data");
    m_TestHelper.deleteFileFromTmp("dumpfile.script");

    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.script")
        });
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(GnuplotScriptTest.class);
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
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[2];
      adams.flow.source.FileSupplier tmp2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("files");
      tmp2.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/out.data")});

      tmp1[0] = tmp2;
      adams.flow.sink.GnuplotScript tmp4 = new adams.flow.sink.GnuplotScript();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputFile");
      tmp4.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.script"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("scriptlet");
      adams.core.gnuplot.MultiScriptlet tmp7 = new adams.core.gnuplot.MultiScriptlet();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("scriptlets");
      adams.core.gnuplot.AbstractScriptlet[] tmp8 = new adams.core.gnuplot.AbstractScriptlet[4];
      adams.core.gnuplot.Initialize tmp9 = new adams.core.gnuplot.Initialize();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("title");
      tmp9.setTitle((java.lang.String) argOption.valueOf("bolts dataset"));

      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("XLabel");
      tmp9.setXLabel((java.lang.String) argOption.valueOf("target variable"));

      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("YLabel");
      tmp9.setYLabel((java.lang.String) argOption.valueOf("input variables"));

      tmp8[0] = tmp9;
      adams.core.gnuplot.SimplePlot tmp13 = new adams.core.gnuplot.SimplePlot();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("columns");
      tmp13.setColumns((java.lang.String) argOption.valueOf("8:2"));

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("plotType");
      tmp13.setPlotType((adams.core.gnuplot.SimplePlot.PlotType) argOption.valueOf("POINTS"));

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("plotName");
      tmp13.setPlotName((java.lang.String) argOption.valueOf("speed1 vs t20bolt"));

      tmp13.setFirstPlot(true);

      tmp8[1] = tmp13;
      adams.core.gnuplot.SimplePlot tmp17 = new adams.core.gnuplot.SimplePlot();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("columns");
      tmp17.setColumns((java.lang.String) argOption.valueOf("8:7"));

      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("plotType");
      tmp17.setPlotType((adams.core.gnuplot.SimplePlot.PlotType) argOption.valueOf("POINTS"));

      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("plotName");
      tmp17.setPlotName((java.lang.String) argOption.valueOf("time vs t20bolt"));

      tmp8[2] = tmp17;
      adams.core.gnuplot.Pause tmp21 = new adams.core.gnuplot.Pause();
      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("waitingPeriod");
      tmp21.setWaitingPeriod((Integer) argOption.valueOf("5"));

      argOption = (AbstractArgumentOption) tmp21.getOptionManager().findByProperty("message");
      tmp21.setMessage((java.lang.String) argOption.valueOf("Press <Enter> to close the plot..."));

      tmp8[3] = tmp21;
      tmp7.setScriptlets(tmp8);

      tmp7.setUseSingleDataFile(true);

      tmp4.setScriptlet(tmp7);

      tmp1[1] = tmp4;
      flow.setActors(tmp1);

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

