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

/**
 * UpdateCallableActorNameTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Tests the UpdateCallableActorName processor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateCallableActorNameTest
  extends AbstractActorProcessorTestCase {

  /**
   * Constructs the test.
   *
   * @param name 	the name of the test
   */
  public UpdateCallableActorNameTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  @Override
  public AbstractActor getActor() {
    AbstractArgumentOption    argOption;

    Flow flow = new Flow();

    try {
      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp1 = new adams.flow.core.AbstractActor[3];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.AbstractActor[] tmp3 = new adams.flow.core.AbstractActor[2];
      adams.flow.sink.Display tmp4 = new adams.flow.sink.Display();
      tmp3[0] = tmp4;
      adams.flow.sink.DumpFile tmp5 = new adams.flow.sink.DumpFile();
      argOption = (AbstractArgumentOption) tmp5.getOptionManager().findByProperty("outputFile");
      tmp5.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${TMP}/out.tmp"));

      tmp3[1] = tmp5;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.StringConstants tmp7 = new adams.flow.source.StringConstants();
      argOption = (AbstractArgumentOption) tmp7.getOptionManager().findByProperty("strings");
      adams.core.base.BaseString[] tmp8 = new adams.core.base.BaseString[1];
      tmp8[0] = (adams.core.base.BaseString) argOption.valueOf("blah");
      tmp7.setStrings(tmp8);

      tmp1[1] = tmp7;
      adams.flow.control.Branch tmp9 = new adams.flow.control.Branch();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("branches");
      adams.flow.core.AbstractActor[] tmp10 = new adams.flow.core.AbstractActor[2];
      adams.flow.sink.CallableSink tmp11 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("callableName");
      tmp11.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Display"));

      tmp10[0] = tmp11;
      adams.flow.sink.CallableSink tmp13 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("name");
      tmp13.setName((java.lang.String) argOption.valueOf("CallableSink-1"));

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("callableName");
      tmp13.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("DumpFile"));

      tmp10[1] = tmp13;
      tmp9.setBranches(tmp10);

      tmp1[2] = tmp9;
      flow.setActors(tmp1);

    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return flow;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractActorProcessor[] getRegressionSetups() {
    UpdateCallableActorName[]	result;

    result    = new UpdateCallableActorName[2];
    result[0] = new UpdateCallableActorName();
    result[0].setOldName("Display");
    result[0].setNewName("DisplayNew");
    result[1] = new UpdateCallableActorName();
    result[1].setOldName("DumpFile");
    result[1].setNewName("DumpFileNew");

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(UpdateCallableActorNameTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
