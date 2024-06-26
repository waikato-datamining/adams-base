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
 * CollectionProcessTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.Index;
import adams.core.VariableName;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.AnyToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.source.ForLoop;
import adams.flow.transformer.ArrayToCollection;
import adams.flow.transformer.CollectionToArray;
import adams.flow.transformer.Convert;
import adams.flow.transformer.StringInsert;
import adams.flow.transformer.StringJoin;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for CollectionProcess actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class CollectionProcessTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CollectionProcessTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs.
   */
  protected void setUp() throws Exception {
    super.setUp();
    
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
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
    return new TestSuite(CollectionProcessTest.class);
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

      // Flow.ForLoop
      ForLoop forloop = new ForLoop();
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopLower");
      forloop.setLoopLower((Integer) argOption.valueOf("11"));
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopUpper");
      forloop.setLoopUpper((Integer) argOption.valueOf("20"));
      forloop.setOutputArray(true);

      actors.add(forloop);

      // Flow.ArrayToCollection
      ArrayToCollection arraytocollection = new ArrayToCollection();
      actors.add(arraytocollection);

      // Flow.CollectionProcess
      CollectionProcess collectionprocess = new CollectionProcess();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CollectionProcess.Convert
      Convert convert = new Convert();
      AnyToString anytostring = new AnyToString();
      convert.setConversion(anytostring);

      actors2.add(convert);

      // Flow.CollectionProcess.StringInsert
      StringInsert stringinsert = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("position");
      stringinsert.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("@{i}: "));
      stringinsert.setValueContainsVariable(true);

      actors2.add(stringinsert);
      collectionprocess.setActors(actors2.toArray(new Actor[0]));

      collectionprocess.setStoreElementIndex(true);

      argOption = (AbstractArgumentOption) collectionprocess.getOptionManager().findByProperty("elementIndexVariable");
      collectionprocess.setElementIndexVariable((VariableName) argOption.valueOf("i"));
      actors.add(collectionprocess);

      // Flow.CollectionToArray
      CollectionToArray collectiontoarray = new CollectionToArray();
      actors.add(collectiontoarray);

      // Flow.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf("\n"));
      actors.add(stringjoin);

      // Flow.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
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

