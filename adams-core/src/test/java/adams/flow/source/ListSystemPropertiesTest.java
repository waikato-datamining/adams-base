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
 * ListSystemPropertiesTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
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
 * Test for ListSystemProperties actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ListSystemPropertiesTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ListSystemPropertiesTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ListSystemPropertiesTest.class);
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

      // Flow.ListSystemProperties
      adams.flow.source.ListSystemProperties listsystemproperties2 = new adams.flow.source.ListSystemProperties();
      abstractactor1[0] = listsystemproperties2;

      // Flow.SetVariable
      adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((adams.core.VariableName) argOption.valueOf("prop"));

      abstractactor1[1] = setvariable3;

      // Flow.Trigger
      adams.flow.control.Trigger trigger5 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] abstractactor6 = new adams.flow.core.Actor[4];

      // Flow.Trigger.GetSystemProperty
      adams.flow.source.GetSystemProperty getsystemproperty7 = new adams.flow.source.GetSystemProperty();
      argOption = (AbstractArgumentOption) getsystemproperty7.getOptionManager().findByProperty("property");
      argOption.setVariable("@{prop}");

      abstractactor6[0] = getsystemproperty7;

      // Flow.Trigger.Convert
      adams.flow.transformer.Convert convert8 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) convert8.getOptionManager().findByProperty("conversion");
      adams.data.conversion.BackQuote backquote10 = new adams.data.conversion.BackQuote();
      convert8.setConversion(backquote10);

      abstractactor6[1] = convert8;

      // Flow.Trigger.StringInsert
      adams.flow.transformer.StringInsert stringinsert11 = new adams.flow.transformer.StringInsert();
      argOption = (AbstractArgumentOption) stringinsert11.getOptionManager().findByProperty("position");
      stringinsert11.setPosition((adams.core.Index) argOption.valueOf("first"));

      argOption = (AbstractArgumentOption) stringinsert11.getOptionManager().findByProperty("value");
      stringinsert11.setValue((adams.core.base.BaseString) argOption.valueOf("@{prop}="));

      stringinsert11.setValueContainsVariable(true);

      abstractactor6[2] = stringinsert11;

      // Flow.Trigger.Display
      adams.flow.sink.Display display14 = new adams.flow.sink.Display();
      abstractactor6[3] = display14;
      trigger5.setActors(abstractactor6);

      abstractactor1[2] = trigger5;
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

