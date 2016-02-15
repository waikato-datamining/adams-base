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
 * SelectDateTimeTest.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;

/**
 * Test for SelectDateTime actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class SelectDateTimeTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SelectDateTimeTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SelectDateTimeTest.class);
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
      adams.flow.core.Actor[] abstractactor1 = new adams.flow.core.Actor[5];

      // Flow.CallableActors
      adams.flow.standalone.CallableActors globalactors2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) globalactors2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor3 = new adams.flow.core.Actor[1];

      // Flow.CallableActors.Display
      adams.flow.sink.Display display4 = new adams.flow.sink.Display();
      abstractactor3[0] = display4;
      globalactors2.setActors(abstractactor3);

      abstractactor1[0] = globalactors2;

      // Flow.Start
      adams.flow.source.Start start5 = new adams.flow.source.Start();
      abstractactor1[1] = start5;

      // Flow.date/time
      adams.flow.control.Trigger trigger6 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
      trigger6.setName((java.lang.String) argOption.valueOf("date/time"));

      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor8 = new adams.flow.core.Actor[2];

      // Flow.date/time.SelectDateTime
      adams.flow.source.SelectDateTime selectdatetime9 = new adams.flow.source.SelectDateTime();
      selectdatetime9.setOutputAsString(true);

      selectdatetime9.setNonInteractive(true);

      abstractactor8[0] = selectdatetime9;

      // Flow.date/time.GlobalSink
      adams.flow.sink.CallableSink globalsink10 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink10.getOptionManager().findByProperty("callableName");
      globalsink10.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Display"));

      abstractactor8[1] = globalsink10;
      trigger6.setActors(abstractactor8);

      abstractactor1[2] = trigger6;

      // Flow.date
      adams.flow.control.Trigger trigger12 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger12.getOptionManager().findByProperty("name");
      trigger12.setName((java.lang.String) argOption.valueOf("date"));

      argOption = (AbstractArgumentOption) trigger12.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor14 = new adams.flow.core.Actor[2];

      // Flow.date.SelectDateTime
      adams.flow.source.SelectDateTime selectdatetime15 = new adams.flow.source.SelectDateTime();
      argOption = (AbstractArgumentOption) selectdatetime15.getOptionManager().findByProperty("type");
      selectdatetime15.setType((adams.flow.source.SelectDateTime.Type) argOption.valueOf("DATE"));

      selectdatetime15.setOutputAsString(true);

      selectdatetime15.setNonInteractive(true);

      abstractactor14[0] = selectdatetime15;

      // Flow.date.GlobalSink
      adams.flow.sink.CallableSink globalsink17 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink17.getOptionManager().findByProperty("callableName");
      globalsink17.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Display"));

      abstractactor14[1] = globalsink17;
      trigger12.setActors(abstractactor14);

      abstractactor1[3] = trigger12;

      // Flow.time
      adams.flow.control.Trigger trigger19 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger19.getOptionManager().findByProperty("name");
      trigger19.setName((java.lang.String) argOption.valueOf("time"));

      argOption = (AbstractArgumentOption) trigger19.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor21 = new adams.flow.core.Actor[2];

      // Flow.time.SelectDateTime
      adams.flow.source.SelectDateTime selectdatetime22 = new adams.flow.source.SelectDateTime();
      argOption = (AbstractArgumentOption) selectdatetime22.getOptionManager().findByProperty("type");
      selectdatetime22.setType((adams.flow.source.SelectDateTime.Type) argOption.valueOf("TIME"));

      selectdatetime22.setOutputAsString(true);

      selectdatetime22.setNonInteractive(true);

      abstractactor21[0] = selectdatetime22;

      // Flow.time.GlobalSink
      adams.flow.sink.CallableSink globalsink24 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) globalsink24.getOptionManager().findByProperty("callableName");
      globalsink24.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Display"));

      abstractactor21[1] = globalsink24;
      trigger19.setActors(abstractactor21);

      abstractactor1[4] = trigger19;
      flow.setActors(abstractactor1);

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
