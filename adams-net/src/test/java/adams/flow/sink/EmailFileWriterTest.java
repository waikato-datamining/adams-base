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
 * EmailFileWriterTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for EmailFileWriter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class EmailFileWriterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public EmailFileWriterTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("bolts.csv");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(EmailFileWriterTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[3];

      // Flow.FileSupplier
      adams.flow.source.FileSupplier filesupplier2 = new adams.flow.source.FileSupplier();
      filesupplier2.setOutputArray(true);

      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile3 = new adams.core.io.PlaceholderFile[1];
      placeholderfile3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/bolts.csv");
      filesupplier2.setFiles(placeholderfile3);

      abstractactor1[0] = filesupplier2;

      // Flow.CreateEmail
      adams.flow.transformer.CreateEmail createemail4 = new adams.flow.transformer.CreateEmail();
      argOption = (AbstractArgumentOption) createemail4.getOptionManager().findByProperty("recipients");
      adams.core.net.EmailAddress[] emailaddress5 = new adams.core.net.EmailAddress[1];
      emailaddress5[0] = (adams.core.net.EmailAddress) argOption.valueOf("\"Peter Reutemann\" <fracpete@waikato.ac.nz>");
      createemail4.setRecipients(emailaddress5);

      argOption = (AbstractArgumentOption) createemail4.getOptionManager().findByProperty("subject");
      createemail4.setSubject((java.lang.String) argOption.valueOf("test"));

      argOption = (AbstractArgumentOption) createemail4.getOptionManager().findByProperty("body");
      createemail4.setBody((adams.core.base.BaseText) argOption.valueOf("asdas\nasdasd\nasdasd"));

      abstractactor1[1] = createemail4;

      // Flow.EmailFileWriter
      adams.flow.sink.EmailFileWriter emailfilewriter8 = new adams.flow.sink.EmailFileWriter();
      argOption = (AbstractArgumentOption) emailfilewriter8.getOptionManager().findByProperty("outputFile");
      emailfilewriter8.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));

      argOption = (AbstractArgumentOption) emailfilewriter8.getOptionManager().findByProperty("writer");
      adams.data.io.output.PlainTextEmailFileWriter plaintextemailfilewriter11 = new adams.data.io.output.PlainTextEmailFileWriter();
      emailfilewriter8.setWriter(plaintextemailfilewriter11);

      abstractactor1[2] = emailfilewriter8;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener13 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener13);

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

