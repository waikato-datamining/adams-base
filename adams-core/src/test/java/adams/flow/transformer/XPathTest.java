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
 * XPathTest.java
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
 * Test for XPath actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class XPathTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public XPathTest(String name) {
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
    return new TestSuite(XPathTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[7];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile3 = new adams.core.io.PlaceholderFile[1];
      placeholderfile3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/books.xml");
      filesupplier2.setFiles(placeholderfile3);

      abstractactor1[0] = filesupplier2;

      // Flow.XMLFileReader
      adams.flow.transformer.XMLFileReader xmlfilereader4 = new adams.flow.transformer.XMLFileReader();
      abstractactor1[1] = xmlfilereader4;

      // Flow.XPath
      adams.flow.transformer.XPath xpath5 = new adams.flow.transformer.XPath();
      argOption = (AbstractArgumentOption) xpath5.getOptionManager().findByProperty("expression");
      xpath5.setExpression((adams.core.base.XPathExpression) argOption.valueOf("//title[@lang='en']"));

      abstractactor1[2] = xpath5;

      // Flow.Convert
      adams.flow.transformer.Convert convert7 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert7.getOptionManager().findByProperty("conversion");
      adams.data.conversion.DOMNodeListToArray domnodelisttoarray9 = new adams.data.conversion.DOMNodeListToArray();
      convert7.setConversion(domnodelisttoarray9);

      abstractactor1[3] = convert7;

      // Flow.ArrayToSequence
      adams.flow.transformer.ArrayToSequence arraytosequence10 = new adams.flow.transformer.ArrayToSequence();
      abstractactor1[4] = arraytosequence10;

      // Flow.XPath-1
      adams.flow.transformer.XPath xpath11 = new adams.flow.transformer.XPath();
      argOption = (AbstractArgumentOption) xpath11.getOptionManager().findByProperty("name");
      xpath11.setName((java.lang.String) argOption.valueOf("XPath-1"));

      argOption = (AbstractArgumentOption) xpath11.getOptionManager().findByProperty("expression");
      xpath11.setExpression((adams.core.base.XPathExpression) argOption.valueOf("./text()"));

      argOption = (AbstractArgumentOption) xpath11.getOptionManager().findByProperty("resultType");
      xpath11.setResultType((adams.core.XPathResult) argOption.valueOf("STRING"));

      abstractactor1[5] = xpath11;

      // Flow.DumpFile
      adams.flow.sink.DumpFile dumpfile15 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) dumpfile15.getOptionManager().findByProperty("outputFile");
      dumpfile15.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      abstractactor1[6] = dumpfile15;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener18 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener18);

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

