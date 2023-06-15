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
 * Html4DisplayTest.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.data.conversion.StringToString;
import adams.data.io.output.NullWriter;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.control.flowrestart.NullManager;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.displaytype.Default;
import adams.flow.execution.NullListener;
import adams.flow.source.CombineVariables;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for Html4Display actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 */
public class Html4DisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public Html4DisplayTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(Html4DisplayTest.class);
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

      // Flow.CombineVariables
      CombineVariables combinevariables = new CombineVariables();
      argOption = (AbstractArgumentOption) combinevariables.getOptionManager().findByProperty("expression");
      combinevariables.setExpression((BaseText) argOption.valueOf("<html>\n  <head>\n    <title>HTML 4 in ADAMS</title>\n  </head>\n  <body>\n    <h2>ADAMS</h3>\n    <p>Flow dir: @{flow_dir}</p>\n    <p><a href=\\\"https://adams.cms.waikato.ac.nz\\\">ADAMS homepage</a></p>\n  </body>\n</html>"));
      StringToString stringtostring = new StringToString();
      combinevariables.setConversion(stringtostring);

      actors.add(combinevariables);

      // Flow.source code
      Tee tee = new Tee();
      argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
      tee.setName((String) argOption.valueOf("source code"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.source code.HTML Source
      Display display = new Display();
      argOption = (AbstractArgumentOption) display.getOptionManager().findByProperty("name");
      display.setName((String) argOption.valueOf("HTML Source"));
      display.setShortTitle(true);

      Default default_ = new Default();
      display.setDisplayType(default_);

      argOption = (AbstractArgumentOption) display.getOptionManager().findByProperty("x");
      display.setX((Integer) argOption.valueOf("-3"));
      NullWriter nullwriter = new NullWriter();
      display.setWriter(nullwriter);

      actors2.add(display);
      tee.setActors(actors2.toArray(new Actor[0]));

      actors.add(tee);

      // Flow.Html
      Html4Display html4display = new Html4Display();
      argOption = (AbstractArgumentOption) html4display.getOptionManager().findByProperty("name");
      html4display.setName((String) argOption.valueOf("Html"));
      html4display.setShortTitle(true);

      Default default_2 = new Default();
      html4display.setDisplayType(default_2);

      adams.gui.print.NullWriter nullwriter2 = new adams.gui.print.NullWriter();
      html4display.setWriter(nullwriter2);

      argOption = (AbstractArgumentOption) html4display.getOptionManager().findByProperty("CSS");
      List<BaseString> css = new ArrayList<>();
      css.add((BaseString) argOption.valueOf("h2 {color: blue;}"));
      html4display.setCSS(css.toArray(new BaseString[0]));
      actors.add(html4display);
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

