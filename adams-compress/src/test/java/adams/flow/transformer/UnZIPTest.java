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
 * UnZIPTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Sequence;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.StringConstants;
import adams.test.TmpDirectory;
import adams.test.TmpFile;

/**
 * Tests the UnZIP actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UnZIPTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UnZIPTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("external_source.flow");
    m_TestHelper.copyResourceToTmp("external_standalone.flow");
    m_TestHelper.deleteFileFromTmp("dumpfile.zip");
    m_TestHelper.deleteFileFromTmp("dumpfile.flow");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("external_source.flow");
    m_TestHelper.deleteFileFromTmp("external_standalone.flow");
    m_TestHelper.deleteFileFromTmp("dumpfile.zip");
    m_TestHelper.deleteFileFromTmp("dumpfile.flow");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  public Actor getActor() {
    StringConstants dummy = new StringConstants();
    dummy.setStrings(new BaseString[]{
	new BaseString("dummy")
    });

    StringConstants sc = new StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("${TMP}/external_standalone.flow"),
	new BaseString("${TMP}/external_source.flow")
    });

    SequenceToArray s2a = new SequenceToArray();
    s2a.setArrayLength(2);

    // creating the zip
    ZIP zip = new ZIP();
    zip.setOutput(new TmpFile("dumpfile.zip"));

    Trigger tr1 = new Trigger();
    tr1.setActors(new Actor[]{
	sc, s2a, zip
    });

    // deleting the input files
    adams.flow.source.DirectoryLister dirDel = new adams.flow.source.DirectoryLister();
    dirDel.setWatchDir(new TmpDirectory());
    dirDel.setListFiles(true);
    dirDel.setRegExp(new BaseRegExp("external_(standalone|source)\\.flow"));

    DeleteFile del = new DeleteFile();

    Trigger tr2 = new Trigger();
    tr2.setActors(new Actor[]{
	dirDel, del
    });

    // unzipping the zip
    adams.flow.source.DirectoryLister dirUn = new adams.flow.source.DirectoryLister();
    dirUn.setWatchDir(new TmpDirectory());
    dirUn.setListFiles(true);
    dirUn.setRegExp(new BaseRegExp("dumpfile\\.zip"));

    UnZIP unzip = new UnZIP();
    unzip.setOutputDir(new TmpDirectory());

    ArrayToSequence a2sUn2 = new ArrayToSequence();

    TextFileReader tfr = new TextFileReader();

    ArrayToSequence a2sUn3 = new ArrayToSequence();

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.flow"));

    Trigger tr3 = new Trigger();
    tr3.setActors(new Actor[]{
	dirUn, unzip, a2sUn2, tfr, a2sUn3, df
    });

    Flow flow = new Flow();
    flow.setActors(new Actor[]{dummy, tr1, tr2, tr3});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.flow"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UnZIPTest.class);
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
