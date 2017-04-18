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
 * ListCallableActorsTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Trigger;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.CallableSink;
import adams.flow.sink.DumpFile;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.StringInsert;
import adams.flow.transformer.StringJoin;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for ListCallableActors actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ListCallableActorsTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ListCallableActorsTest(String name) {
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
    return new TestSuite(ListCallableActorsTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.new-report
      NewReport newreport = new NewReport();
      argOption = (AbstractArgumentOption) newreport.getOptionManager().findByProperty("name");
      newreport.setName((String) argOption.valueOf("new-report"));
      actors2.add(newreport);

      // Flow.CallableActors.dump-storage
      DumpStorage dumpstorage = new DumpStorage();
      argOption = (AbstractArgumentOption) dumpstorage.getOptionManager().findByProperty("name");
      dumpstorage.setName((String) argOption.valueOf("dump-storage"));
      actors2.add(dumpstorage);

      // Flow.CallableActors.dump-variables
      DumpVariables dumpvariables = new DumpVariables();
      argOption = (AbstractArgumentOption) dumpvariables.getOptionManager().findByProperty("name");
      dumpvariables.setName((String) argOption.valueOf("dump-variables"));
      actors2.add(dumpvariables);

      // Flow.CallableActors.DumpFile
      DumpFile dumpfile = new DumpFile();
      argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
      dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${TMP}/dumpfile.txt"));
      dumpfile.setAppend(true);

      actors2.add(dumpfile);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.Start
      Start start = new Start();
      actors.add(start);

      // Flow.list all
      Trigger trigger = new Trigger();
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("list all"));
      List<Actor> actors3 = new ArrayList<>();

      // Flow.list all.ListCallableActors
      ListCallableActors listcallableactors = new ListCallableActors();
      listcallableactors.setOutputArray(true);

      actors3.add(listcallableactors);

      // Flow.list all.StringJoin
      StringJoin stringjoin = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
      stringjoin.setGlue((String) argOption.valueOf(", "));
      actors3.add(stringjoin);

      // Flow.list all.StringInsert
      StringInsert stringinsert = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("position");
      stringinsert.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert.getOptionManager().findByProperty("value");
      stringinsert.setValue((BaseString) argOption.valueOf("all: "));
      actors3.add(stringinsert);

      // Flow.list all.CallableSink
      CallableSink callablesink = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink.getOptionManager().findByProperty("callableName");
      callablesink.setCallableName((CallableActorReference) argOption.valueOf("DumpFile"));
      actors3.add(callablesink);
      trigger.setActors(actors3.toArray(new Actor[0]));

      actors.add(trigger);

      // Flow.list "dump-*"
      Trigger trigger2 = new Trigger();
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("list \"dump-*\""));
      List<Actor> actors4 = new ArrayList<>();

      // Flow.list "dump-*".ListCallableActors
      ListCallableActors listcallableactors2 = new ListCallableActors();
      listcallableactors2.setOutputArray(true);

      argOption = (AbstractArgumentOption) listcallableactors2.getOptionManager().findByProperty("regExp");
      listcallableactors2.setRegExp((BaseRegExp) argOption.valueOf("dump-.*"));
      actors4.add(listcallableactors2);

      // Flow.list "dump-*".StringJoin
      StringJoin stringjoin2 = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin2.getOptionManager().findByProperty("glue");
      stringjoin2.setGlue((String) argOption.valueOf(", "));
      actors4.add(stringjoin2);

      // Flow.list "dump-*".StringInsert
      StringInsert stringinsert2 = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("position");
      stringinsert2.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert2.getOptionManager().findByProperty("value");
      stringinsert2.setValue((BaseString) argOption.valueOf("\\\\\"dump-*\\\\\": "));
      actors4.add(stringinsert2);

      // Flow.list "dump-*".CallableSink
      CallableSink callablesink2 = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink2.getOptionManager().findByProperty("callableName");
      callablesink2.setCallableName((CallableActorReference) argOption.valueOf("DumpFile"));
      actors4.add(callablesink2);
      trigger2.setActors(actors4.toArray(new Actor[0]));

      actors.add(trigger2);

      // Flow.list "new-*"
      Trigger trigger3 = new Trigger();
      argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
      trigger3.setName((String) argOption.valueOf("list \"new-*\""));
      List<Actor> actors5 = new ArrayList<>();

      // Flow.list "new-*".CallableActors
      CallableActors callableactors2 = new CallableActors();
      List<Actor> actors6 = new ArrayList<>();

      // Flow.list "new-*".CallableActors.new-report2
      NewReport newreport2 = new NewReport();
      argOption = (AbstractArgumentOption) newreport2.getOptionManager().findByProperty("name");
      newreport2.setName((String) argOption.valueOf("new-report2"));
      actors6.add(newreport2);
      callableactors2.setActors(actors6.toArray(new Actor[0]));

      actors5.add(callableactors2);

      // Flow.list "new-*".ListCallableActors
      ListCallableActors listcallableactors3 = new ListCallableActors();
      listcallableactors3.setOutputArray(true);

      argOption = (AbstractArgumentOption) listcallableactors3.getOptionManager().findByProperty("regExp");
      listcallableactors3.setRegExp((BaseRegExp) argOption.valueOf("new-.*"));
      actors5.add(listcallableactors3);

      // Flow.list "new-*".StringJoin
      StringJoin stringjoin3 = new StringJoin();
      argOption = (AbstractArgumentOption) stringjoin3.getOptionManager().findByProperty("glue");
      stringjoin3.setGlue((String) argOption.valueOf(", "));
      actors5.add(stringjoin3);

      // Flow.list "new-*".StringInsert
      StringInsert stringinsert3 = new StringInsert();
      argOption = (AbstractArgumentOption) stringinsert3.getOptionManager().findByProperty("position");
      stringinsert3.setPosition((Index) argOption.valueOf("first"));
      argOption = (AbstractArgumentOption) stringinsert3.getOptionManager().findByProperty("value");
      stringinsert3.setValue((BaseString) argOption.valueOf("\\\\\"new-*\\\\\": "));
      actors5.add(stringinsert3);

      // Flow.list "new-*".CallableSink
      CallableSink callablesink3 = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink3.getOptionManager().findByProperty("callableName");
      callablesink3.setCallableName((CallableActorReference) argOption.valueOf("DumpFile"));
      actors5.add(callablesink3);
      trigger3.setActors(actors5.toArray(new Actor[0]));

      actors.add(trigger3);
      flow.setActors(actors.toArray(new Actor[0]));

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

