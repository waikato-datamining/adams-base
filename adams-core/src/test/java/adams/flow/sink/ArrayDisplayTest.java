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
 * ArrayDisplayTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.base.BaseString;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.displaytype.Default;
import adams.flow.execution.NullListener;
import adams.flow.sink.ArrayDisplay.Arrangement;
import adams.flow.source.StringConstants;
import adams.gui.print.NullWriter;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for ArrayDisplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class ArrayDisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayDisplayTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayDisplayTest.class);
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

      // Flow.StringConstants
      StringConstants stringconstants = new StringConstants();
      stringconstants.setOutputArray(true);

      argOption = (AbstractArgumentOption) stringconstants.getOptionManager().findByProperty("strings");
      List<BaseString> strings = new ArrayList<>();
      strings.add((BaseString) argOption.valueOf("1"));
      strings.add((BaseString) argOption.valueOf("2"));
      strings.add((BaseString) argOption.valueOf("3"));
      strings.add((BaseString) argOption.valueOf("4"));
      strings.add((BaseString) argOption.valueOf("5"));
      strings.add((BaseString) argOption.valueOf("6"));
      strings.add((BaseString) argOption.valueOf("7"));
      strings.add((BaseString) argOption.valueOf("8"));
      strings.add((BaseString) argOption.valueOf("9"));
      strings.add((BaseString) argOption.valueOf("10"));
      stringconstants.setStrings(strings.toArray(new BaseString[0]));
      StringToString stringtostring = new StringToString();
      stringconstants.setConversion(stringtostring);

      actors.add(stringconstants);

      // Flow.ArrayDisplay
      ArrayDisplay arraydisplay = new ArrayDisplay();
      Default default_ = new Default();
      arraydisplay.setDisplayType(default_);

      NullWriter nullwriter = new NullWriter();
      arraydisplay.setWriter(nullwriter);


      // 
      Display display = new Display();
      Default default_2 = new Default();
      display.setDisplayType(default_2);

      adams.data.io.output.NullWriter nullwriter2 = new adams.data.io.output.NullWriter();
      display.setWriter(nullwriter2);

      arraydisplay.setPanelProvider(display);

      argOption = (AbstractArgumentOption) arraydisplay.getOptionManager().findByProperty("arrangement");
      arraydisplay.setArrangement((Arrangement) argOption.valueOf("GRID"));
      argOption = (AbstractArgumentOption) arraydisplay.getOptionManager().findByProperty("numCols");
      arraydisplay.setNumCols((Integer) argOption.valueOf("3"));
      actors.add(arraydisplay);
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

