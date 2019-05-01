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
 * FindInFileTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.io.filesearch.TextFileSearchHandler;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.AnyToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for FindInFile actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class FindInFileTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FindInFileTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.copyResourceToTmp("books.xml");
    m_TestHelper.copyResourceToTmp("cdcollection.xml");
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("books.xml");
    m_TestHelper.deleteFileFromTmp("cdcollection.xml");
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
    return new TestSuite(FindInFileTest.class);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;
    
    Flow flow = new Flow();
    
    try {
      List<Actor> actors = new ArrayList<>();

      // Flow.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      argOption = (AbstractArgumentOption) filesupplier.getOptionManager().findByProperty("files");
      List<PlaceholderFile> files = new ArrayList<>();
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/books.xml"));
      files.add((PlaceholderFile) argOption.valueOf("${TMP}/cdcollection.xml"));
      filesupplier.setFiles(files.toArray(new PlaceholderFile[0]));
      actors.add(filesupplier);

      // Flow.Tee
      Tee tee = new Tee();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.Tee.BaseName
      BaseName basename = new BaseName();
      actors2.add(basename);

      // Flow.Tee.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("name"));
      actors2.add(setvariable);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.FindInFile
      FindInFile findinfile = new FindInFile();
      argOption = (AbstractArgumentOption) findinfile.getOptionManager().findByProperty("find");
      findinfile.setFind((String) argOption.valueOf("Harry"));
      TextFileSearchHandler textfilesearchhandler = new TextFileSearchHandler();
      findinfile.setCustomHandler(textfilesearchhandler);

      actors.add(findinfile);

      // Flow.Convert
      Convert convert = new Convert();
      AnyToString anytostring = new AnyToString();
      convert.setConversion(anytostring);

      actors.add(convert);

      // Flow.StringInsert
      StringInsert stringinsert = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("position");
      stringinsert.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("@{name}: "));
      stringinsert.setValueContainsVariable(true);

      actors.add(stringinsert);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors.add(dumpfile);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

      NullManager nullmanager = new NullManager();
      flow.setFlowRestartManager(nullmanager);

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

