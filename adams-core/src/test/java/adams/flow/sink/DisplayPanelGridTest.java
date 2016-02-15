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
 * DisplayPanelGridTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.data.io.input.SingleStringTextReader;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for DisplayPanelGrid actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class DisplayPanelGridTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public DisplayPanelGridTest(String name) {
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
    
    m_TestHelper.copyResourceToTmp("diff1.txt");
    m_TestHelper.copyResourceToTmp("diff2.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("diff1.txt");
    m_TestHelper.deleteFileFromTmp("diff2.txt");
    
    super.tearDown();
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(DisplayPanelGridTest.class);
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
      argOption = (AbstractArgumentOption) filesupplier2.getOptionManager().findByProperty("files");
      adams.core.io.PlaceholderFile[] placeholderfile3 = new adams.core.io.PlaceholderFile[2];
      placeholderfile3[0] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/diff1.txt");
      placeholderfile3[1] = (adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/diff2.txt");
      filesupplier2.setFiles(placeholderfile3);

      abstractactor1[0] = filesupplier2;

      // Flow.TextFileReader
      adams.flow.transformer.TextFileReader textfilereader4 = new adams.flow.transformer.TextFileReader();
      argOption = (AbstractArgumentOption) textfilereader4.getOptionManager().findByProperty("outputType");
      textfilereader4.setReader(new SingleStringTextReader());

      abstractactor1[1] = textfilereader4;

      // Flow.DisplayPanelGrid
      adams.flow.sink.DisplayPanelGrid displaypanelgrid6 = new adams.flow.sink.DisplayPanelGrid();
      argOption = (AbstractArgumentOption) displaypanelgrid6.getOptionManager().findByProperty("panelProvider");

      // 
      adams.flow.sink.Display display8 = new adams.flow.sink.Display();
      displaypanelgrid6.setPanelProvider(display8);

      argOption = (AbstractArgumentOption) displaypanelgrid6.getOptionManager().findByProperty("numColumns");
      displaypanelgrid6.setNumColumns((Integer) argOption.valueOf("1"));

      abstractactor1[2] = displaypanelgrid6;
      flow.setActors(abstractactor1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener11 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener11);

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

