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
 * MimeTypeTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.test.TmpFile;

/**
 * Test for MimeType actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MimeTypeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MimeTypeTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("3666455665_18795f0741.jpg");
    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.copyResourceToTmp("books.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("3666455665_18795f0741.jpg");
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("books.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.txt")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MimeTypeTest.class);
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
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[5];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] files3 = new adams.core.io.PlaceholderFile[3];
      files3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/3666455665_18795f0741.jpg");
      files3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
      files3[2] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/books.xml");
      filesupplier2.setFiles(files3);
      actors1[0] = filesupplier2;

      // Flow.Store filename in @{file}
      adams.flow.control.Tee tee4 = new adams.flow.control.Tee();
      argOption = (AbstractArgumentOption) tee4.getOptionManager().findByProperty("name");
      tee4.setName((java.lang.String) argOption.valueOf("Store filename in @{file}"));
      argOption = (AbstractArgumentOption) tee4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] actors6 = new adams.flow.core.Actor[2];

      // Flow.Store filename in @{file}.BaseName
      adams.flow.transformer.BaseName basename7 = new adams.flow.transformer.BaseName();
      actors6[0] = basename7;

      // Flow.Store filename in @{file}.SetVariable
      adams.flow.transformer.SetVariable setvariable8 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("variableName");
      setvariable8.setVariableName((adams.core.VariableName) argOption.valueOf("file"));
      actors6[1] = setvariable8;
      tee4.setActors(actors6);

      actors1[1] = tee4;

      // Flow.MimeType
      adams.flow.transformer.MimeType mimetype10 = new adams.flow.transformer.MimeType();
      actors1[2] = mimetype10;

      // Flow.StringInsert
      adams.flow.transformer.StringInsert stringinsert11 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert11.getOptionManager().findByProperty("position");
      stringinsert11.setPosition((adams.core.Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert11.getOptionManager().findByProperty("value");
      stringinsert11.setValue((adams.core.base.BaseString) argOption.valueOf("@{file}: "));
      stringinsert11.setValueContainsVariable(true);

      actors1[3] = stringinsert11;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile14 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile14.getOptionManager().findByProperty("outputFile");
      dumpfile14.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile14.setAppend(true);

      actors1[4] = dumpfile14;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener17 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener17);

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

