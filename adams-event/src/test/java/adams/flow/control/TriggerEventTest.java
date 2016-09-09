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
 * TriggerEventTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.io.lister.Sorting;
import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.Actor;

/**
 * Test for TriggerEvent actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class TriggerEventTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TriggerEventTest(String name) {
    super(name);
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TriggerEventTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[3];
      adams.flow.standalone.Events tmp2 = new adams.flow.standalone.Events();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.control.Flow tmp4 = new adams.flow.control.Flow();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("name");
      tmp4.setName((java.lang.String) argOption.valueOf("Blah"));

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp6 = new adams.flow.core.Actor[2];
      adams.flow.source.DirectoryLister tmp7 = new adams.flow.source.DirectoryLister();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("watchDir");
      tmp7.setWatchDir((adams.core.io.PlaceholderDirectory) argOption.valueOf("${TMP}"));

      tmp7.setListFiles(true);

      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("sorting");
      tmp7.setSorting((Sorting) argOption.valueOf("SORT_BY_NAME"));

      tmp6[0] = tmp7;
      adams.flow.sink.Display tmp10 = new adams.flow.sink.Display();
      tmp6[1] = tmp10;
      tmp4.setActors(tmp6);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.Start tmp11 = new adams.flow.source.Start();
      tmp1[1] = tmp11;
      adams.flow.control.TriggerEvent tmp12 = new adams.flow.control.TriggerEvent();
      argOption = (AbstractArgumentOption) tmp12.getOptionManager().findByProperty("event");
      tmp12.setEvent((adams.flow.core.TriggerableEventReference) argOption.valueOf("Blah"));

      tmp1[2] = tmp12;
      flow.setActors(tmp1);

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

