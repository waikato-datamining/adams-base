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
 * WekaInstanceBufferTest.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Counting;
import adams.flow.control.ConditionalTee;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.AbstractActor;
import adams.flow.core.Token;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.flow.transformer.WekaFileReader.OutputType;
import adams.flow.transformer.WekaInstanceBuffer.Operation;
import adams.test.TmpFile;

/**
 * Tests the WekaInstanceBuffer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstanceBufferTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public WekaInstanceBufferTest(String name) {
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
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("vote.arff");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    FileSupplier sfs = new FileSupplier();
    sfs.setFiles(new adams.core.io.PlaceholderFile[]{new TmpFile("vote.arff")});

    WekaFileReader fr = new WekaFileReader();
    fr.setOutputType(OutputType.INCREMENTAL);

    WekaInstanceBuffer ib = new WekaInstanceBuffer();

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    Sequence seq = new Sequence();
    seq.setActors(new AbstractActor[]{ib, df});

    ConditionalTee ct = new ConditionalTee();
    Counting count = new Counting();
    count.setMaximum(10);
    ct.setCondition(count);
    ct.add(0, seq);

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{sfs, fr, ct});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.txt"));
  }

  /**
   * Tests the other direction for the buffer than the regression test.
   */
  public void testInstancesToInstance() {
    Instances data = null;
    try {
      data = DataSource.read(new TmpFile("vote.arff").getAbsolutePath());
    }
    catch (Exception e) {
      fail("Failed to load data!");
      e.printStackTrace();
    }

    WekaInstanceBuffer buffer = new WekaInstanceBuffer();
    buffer.setOperation(Operation.INSTANCES_TO_INSTANCE);
    buffer.setUp();
    buffer.input(new Token(data));
    buffer.execute();
    int count = 0;
    for (int i = 0; (i < data.numInstances()) && (buffer.hasPendingOutput()); i++) {
      Instance inst = (Instance) buffer.output().getPayload();
      assertEquals("Data row #" + (i+1)  + " differs", data.instance(i).toString(), inst.toString());
      count++;
    }
    buffer.wrapUp();
    buffer.cleanUp();
    assertEquals("Number of rows differ", data.numInstances(), count);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(WekaInstanceBufferTest.class);
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
