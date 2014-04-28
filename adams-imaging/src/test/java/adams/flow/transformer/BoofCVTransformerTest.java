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
 * BoofCVTransformerTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.PlaceholderFile;
import adams.data.boofcv.BoofCVImageType;
import adams.data.boofcv.flattener.Pixels;
import adams.data.boofcv.transformer.Gray8;
import adams.data.conversion.BufferedImageToBoofCV;
import adams.data.jai.transformer.Resize;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;

/**
 * Tests the BoofCVTransformer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BoofCVTransformerTest(String name) {
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

    m_TestHelper.copyResourceToTmp("adams_logo.png");
    m_TestHelper.copyResourceToTmp("adams_icon.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("adams_logo.png");
    m_TestHelper.deleteFileFromTmp("adams_icon.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.arff");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    FileSupplier mfs = new FileSupplier();
    mfs.setFiles(
	new PlaceholderFile[]{
	    new TmpFile("adams_logo.png"),
	    new TmpFile("adams_icon.png")
	});

    JAIReader ir = new JAIReader();

    JAITransformer itr = new JAITransformer();
    Resize res = new Resize();
    res.setWidth(16);
    res.setHeight(16);
    itr.setTransformAlgorithm(res);

    BufferedImageToBoofCV bi2bcv = new BufferedImageToBoofCV();
    bi2bcv.setImageType(BoofCVImageType.SIGNED_INT_16);
    Convert con = new Convert();
    con.setConversion(bi2bcv);

    BoofCVTransformer btr = new BoofCVTransformer();
    btr.setTransformAlgorithm(new Gray8());

    BoofCVFlattener ifl = new BoofCVFlattener();
    Pixels pix = new Pixels();
    ifl.setFlattenAlgorithm(pix);

    WekaInstanceDumper id = new WekaInstanceDumper();
    id.setOutputPrefix(new TmpFile("dumpfile"));

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{mfs, ir, itr, con, btr, ifl, id});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.arff"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BoofCVTransformerTest.class);
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
