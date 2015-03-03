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
 * ScreenshotTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.source.FileSupplier;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaClassSelector;
import adams.flow.transformer.WekaCrossValidationEvaluator;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.gui.print.PNGWriter;
import adams.test.TmpFile;

/**
 * Tests the Screenshot actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScreenshotTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ScreenshotTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("screenshot1.png");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("screenshot1.png");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    WekaClassifierSetup cls = new WekaClassifierSetup();
    cls.setName("cls");
    cls.setClassifier(new weka.classifiers.trees.J48());

    CallableActors ga = new CallableActors();
    ga.setActors(new AbstractActor[]{cls});

    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.DATASET);

    WekaClassSelector cs = new WekaClassSelector();

    WekaCrossValidationEvaluator cv = new WekaCrossValidationEvaluator();
    cv.setClassifier(new CallableActorReference("cls"));

    WekaCostCurve cc = new WekaCostCurve();

    PNGWriter writer = new PNGWriter();
    Screenshot shot = new Screenshot();
    shot.setWriter(writer);
    shot.setOutput(new TmpFile());
    shot.setPanelProvider(cc);

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ga, sfs, fr, cs, cv, shot});

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ScreenshotTest.class);
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
