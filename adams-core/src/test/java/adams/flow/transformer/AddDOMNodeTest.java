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
 * AddDOMNodeTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
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
 * Test for AddDOMNode actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class AddDOMNodeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AddDOMNodeTest(String name) {
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
    
    m_TestHelper.deleteFileFromTmp("dumpfile.xml");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.xml");
    
    super.tearDown();
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
        new TmpFile[]{
          new TmpFile("dumpfile.xml")
        });
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(AddDOMNodeTest.class);
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

      // Flow.NewDOMDocument
      adams.flow.source.NewDOMDocument newdomdocument2 = new adams.flow.source.NewDOMDocument();
      actors1[0] = newdomdocument2;

      // Flow.AddDOMNode
      adams.flow.transformer.AddDOMNode adddomnode3 = new adams.flow.transformer.AddDOMNode();
      argOption = (AbstractArgumentOption) adddomnode3.getOptionManager().findByProperty("nodeName");
      adddomnode3.setNodeName((java.lang.String) argOption.valueOf("nodes"));
      actors1[1] = adddomnode3;

      // Flow.AddDOMNode-1
      adams.flow.transformer.AddDOMNode adddomnode5 = new adams.flow.transformer.AddDOMNode();
      argOption = (AbstractArgumentOption) adddomnode5.getOptionManager().findByProperty("name");
      adddomnode5.setName((java.lang.String) argOption.valueOf("AddDOMNode-1"));
      actors1[2] = adddomnode5;

      // Flow.AddDOMNode-2
      adams.flow.transformer.AddDOMNode adddomnode7 = new adams.flow.transformer.AddDOMNode();
      argOption = (AbstractArgumentOption) adddomnode7.getOptionManager().findByProperty("name");
      adddomnode7.setName((java.lang.String) argOption.valueOf("AddDOMNode-2"));
      argOption = (AbstractArgumentOption) adddomnode7.getOptionManager().findByProperty("nodeName");
      adddomnode7.setNodeName((java.lang.String) argOption.valueOf("inner"));
      actors1[3] = adddomnode7;

      // Flow.XMLFileWriter
      adams.flow.sink.XMLFileWriter xmlfilewriter10 = new adams.flow.sink.XMLFileWriter();
      argOption = (AbstractArgumentOption) xmlfilewriter10.getOptionManager().findByProperty("outputFile");
      xmlfilewriter10.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.xml"));
      argOption = (AbstractArgumentOption) xmlfilewriter10.getOptionManager().findByProperty("encoding");
      xmlfilewriter10.setEncoding((adams.core.base.BaseCharset) argOption.valueOf("UTF-8"));
      actors1[4] = xmlfilewriter10;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener14 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener14);

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

