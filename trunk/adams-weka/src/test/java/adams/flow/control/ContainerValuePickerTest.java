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
 * ContainerValuePickerTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.source.WekaClassifierSetup;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaClassSelector;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaTrainClassifier;
import adams.test.TmpFile;

/**
 * Tests the ContainerValuePicker actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ContainerValuePickerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ContainerValuePickerTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile_all.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_model.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile_all.txt");
    m_TestHelper.deleteFileFromTmp("dumpfile_model.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    CallableActors ga = new CallableActors();
    
    WekaClassifierSetup wcs = new WekaClassifierSetup();
    wcs.setName("cls");
    wcs.setClassifier(new weka.classifiers.trees.J48());
    ga.add(wcs);
    
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();

    WekaClassSelector cs = new WekaClassSelector();

    WekaTrainClassifier cls = new WekaTrainClassifier();
    cls.setClassifier(new CallableActorReference("cls"));

    DumpFile df_model = new DumpFile();
    df_model.setOutputFile(new TmpFile("dumpfile_model.txt"));

    ContainerValuePicker cvp = new ContainerValuePicker();
    cvp.setValueName(WekaModelContainer.VALUE_MODEL);
    cvp.add(0, df_model);

    DumpFile df_all = new DumpFile();
    df_all.setOutputFile(new TmpFile("dumpfile_all.txt"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ga, sfs, fr, cs, cls, cvp, df_all});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new File[]{
	    new TmpFile("dumpfile_all.txt"),
	    new TmpFile("dumpfile_model.txt")});
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ContainerValuePickerTest.class);
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
