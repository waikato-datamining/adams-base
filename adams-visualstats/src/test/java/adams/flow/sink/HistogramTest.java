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
 * HistogramTest.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.flow.transformer.SpreadSheetFileReader;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for Histogram actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class HistogramTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HistogramTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("iris.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("iris.csv");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HistogramTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("annotations");
      flow.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("Generates a histogram of the first attribute\nof the UCI dataset \"iris\"."));

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor2 = new adams.flow.core.Actor[3];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier3 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier3.getOptionManager().findByProperty("files");
      filesupplier3.setFiles(new adams.core.io.PlaceholderFile[]{(adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/iris.csv")});

      abstractactor2[0] = filesupplier3;

      // Flow.WekaFileReader
      SpreadSheetFileReader fr = new SpreadSheetFileReader();

      abstractactor2[1] = fr;

      // Flow.Histogram
      adams.flow.sink.Histogram histogram8 = new adams.flow.sink.Histogram();
      argOption = (AbstractArgumentOption) histogram8.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter10 = new adams.gui.print.NullWriter();
      histogram8.setWriter(nullwriter10);

      argOption = (AbstractArgumentOption) histogram8.getOptionManager().findByProperty("options");
      adams.gui.visualization.stats.histogram.HistogramOptions histogramoptions12 = new adams.gui.visualization.stats.histogram.HistogramOptions();
      argOption = (AbstractArgumentOption) histogramoptions12.getOptionManager().findByProperty("numberBins");
      histogramoptions12.setNumberBins((Integer) argOption.valueOf("20"));

      argOption = (AbstractArgumentOption) histogramoptions12.getOptionManager().findByProperty("paintlet");
      adams.gui.visualization.stats.paintlet.HistogramPaintlet histogrampaintlet15 = new adams.gui.visualization.stats.paintlet.HistogramPaintlet();
      histogramoptions12.setPaintlet(histogrampaintlet15);

      histogram8.setOptions(histogramoptions12);

      abstractactor2[2] = histogram8;
      flow.setActors(abstractactor2);

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

