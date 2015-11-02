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
 * CounterTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.execution.NullListener;
import adams.flow.sink.DumpFile;
import adams.flow.standalone.CounterInit;
import adams.flow.transformer.CounterAdd;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for Counter actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class CounterTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CounterTest(String name) {
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
    return new TestSuite(CounterTest.class);
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
      List<AbstractActor> actors = new ArrayList<AbstractActor>();

      // Flow.CounterInit
      CounterInit counterinit = new CounterInit();
      actors.add(counterinit);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.Trigger
      Trigger trigger = new Trigger();
      List<AbstractActor> actors2 = new ArrayList<AbstractActor>();

      // Flow.Trigger.StringConstants
      StringConstants stringconstants = new StringConstants();
      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<BaseString>();
      strings.add((BaseString) argOption.valueOf("a"));
      strings.add((BaseString) argOption.valueOf("b"));
      strings.add((BaseString) argOption.valueOf("b"));
      strings.add((BaseString) argOption.valueOf("c"));
      strings.add((BaseString) argOption.valueOf("b"));
      strings.add((BaseString) argOption.valueOf("b"));
      strings.add((BaseString) argOption.valueOf("d"));
      strings.add((BaseString) argOption.valueOf("d"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      actors2.add(stringconstants);

      // Flow.Trigger.CounterAdd
      CounterAdd counteradd = new CounterAdd();
      actors2.add(counteradd);
      trigger.setActors(actors2.toArray(new AbstractActor[0]));

      actors.add(trigger);

      // Flow.Trigger-1
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("Trigger-1"));
      List<AbstractActor> actors3 = new ArrayList<AbstractActor>();

      // Flow.Trigger-1.Counter
      Counter counter = new Counter();
      actors3.add(counter);

      // Flow.Trigger-1.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      actors3.add(dumpfile);
      trigger2.setActors(actors3.toArray(new AbstractActor[0]));

      actors.add(trigger2);
      flow.setActors(actors.toArray(new AbstractActor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

