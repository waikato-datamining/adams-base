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
 * AddDOMAttributeTest.java
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
 * Test for AddDOMAttribute actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class AddDOMAttributeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AddDOMAttributeTest(String name) {
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
    return new TestSuite(AddDOMAttributeTest.class);
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
      adams.flow.core.Actor[] actors1 = new adams.flow.core.Actor[7];

      // Flow.NewDOMDocument
      adams.flow.source.NewDOMDocument newdomdocument2 = new adams.flow.source.NewDOMDocument();
      actors1[0] = newdomdocument2;

      // Flow.AddDOMNode
      adams.flow.transformer.AddDOMNode adddomnode3 = new adams.flow.transformer.AddDOMNode();
      argOption = (AbstractArgumentOption) adddomnode3.getOptionManager().findByProperty("nodeName");
      adddomnode3.setNodeName((java.lang.String) argOption.valueOf("outer"));
      actors1[1] = adddomnode3;

      // Flow.AddDOMAttribute
      adams.flow.transformer.AddDOMAttribute adddomattribute5 = new adams.flow.transformer.AddDOMAttribute();
      argOption = (AbstractArgumentOption) adddomattribute5.getOptionManager().findByProperty("attribute");
      adddomattribute5.setAttribute((java.lang.String) argOption.valueOf("outer1"));
      argOption = (AbstractArgumentOption) adddomattribute5.getOptionManager().findByProperty("value");
      adddomattribute5.setValue((java.lang.String) argOption.valueOf("1"));
      actors1[2] = adddomattribute5;

      // Flow.AddDOMAttribute-1
      adams.flow.transformer.AddDOMAttribute adddomattribute8 = new adams.flow.transformer.AddDOMAttribute();
      argOption = (AbstractArgumentOption) adddomattribute8.getOptionManager().findByProperty("name");
      adddomattribute8.setName((java.lang.String) argOption.valueOf("AddDOMAttribute-1"));
      argOption = (AbstractArgumentOption) adddomattribute8.getOptionManager().findByProperty("attribute");
      adddomattribute8.setAttribute((java.lang.String) argOption.valueOf("outer2"));
      argOption = (AbstractArgumentOption) adddomattribute8.getOptionManager().findByProperty("value");
      adddomattribute8.setValue((java.lang.String) argOption.valueOf("2"));
      actors1[3] = adddomattribute8;

      // Flow.AddDOMNode-1
      adams.flow.transformer.AddDOMNode adddomnode12 = new adams.flow.transformer.AddDOMNode();
      argOption = (AbstractArgumentOption) adddomnode12.getOptionManager().findByProperty("name");
      adddomnode12.setName((java.lang.String) argOption.valueOf("AddDOMNode-1"));
      argOption = (AbstractArgumentOption) adddomnode12.getOptionManager().findByProperty("nodeName");
      adddomnode12.setNodeName((java.lang.String) argOption.valueOf("inner"));
      actors1[4] = adddomnode12;

      // Flow.AddDOMAttribute-2
      adams.flow.transformer.AddDOMAttribute adddomattribute15 = new adams.flow.transformer.AddDOMAttribute();
      argOption = (AbstractArgumentOption) adddomattribute15.getOptionManager().findByProperty("name");
      adddomattribute15.setName((java.lang.String) argOption.valueOf("AddDOMAttribute-2"));
      argOption = (AbstractArgumentOption) adddomattribute15.getOptionManager().findByProperty("attribute");
      adddomattribute15.setAttribute((java.lang.String) argOption.valueOf("inner1"));
      argOption = (AbstractArgumentOption) adddomattribute15.getOptionManager().findByProperty("value");
      adddomattribute15.setValue((java.lang.String) argOption.valueOf("i1"));
      actors1[5] = adddomattribute15;

      // Flow.XMLFileWriter
      adams.flow.sink.XMLFileWriter xmlfilewriter19 = new adams.flow.sink.XMLFileWriter();
      argOption = (AbstractArgumentOption) xmlfilewriter19.getOptionManager().findByProperty("outputFile");
      xmlfilewriter19.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.xml"));
      argOption = (AbstractArgumentOption) xmlfilewriter19.getOptionManager().findByProperty("encoding");
      xmlfilewriter19.setEncoding((adams.core.base.BaseCharset) argOption.valueOf("UTF-8"));
      actors1[6] = xmlfilewriter19;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener23 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener23);

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

