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
 * AnyToCommandlineTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.data.conversion.StringToDouble;
import adams.data.conversion.StringToInt;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Branch;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.sink.CallableSink;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.flow.standalone.CallableActors;
import adams.test.TmpFile;

/**
 * Tests the AnyToCommandline actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AnyToCommandlineTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AnyToCommandlineTest(String name) {
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

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Flow</code>
   */
  @Override
  public AbstractActor getActor() {
    DumpFile df = new DumpFile();
    df.setName("out");
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.txt"));

    CallableActors ga = new CallableActors();
    ga.setActors(new AbstractActor[]{
	df
    });

    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("10"),
	new BaseString("1"),
	new BaseString("-10"),
	new BaseString("987")
    });

    StringToInt s2I = new StringToInt();
    Convert conI = new Convert();
    conI.setConversion(s2I);
    AnyToCommandline anyI = new AnyToCommandline();
    CallableSink gsI = new CallableSink();
    gsI.setCallableName(new CallableActorReference("out"));
    Sequence sI = new Sequence();
    sI.setActors(new AbstractActor[]{
	conI, anyI, gsI
    });

    StringToDouble s2D = new StringToDouble();
    Convert conD = new Convert();
    conD.setConversion(s2D);
    AnyToCommandline anyD = new AnyToCommandline();
    CallableSink gsD = new CallableSink();
    gsD.setCallableName(new CallableActorReference("out"));
    Sequence sD = new Sequence();
    sD.setActors(new AbstractActor[]{
	conD, anyD, gsD
    });

    Branch br = new Branch();
    br.setNumThreads(0);
    br.setBranches(new AbstractActor[]{
	sI, sD
    });

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ga, sc, br});

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
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AnyToCommandlineTest.class);
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
