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
 * ActorTemplateSuggestionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseDate;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.flow.template.AbstractActorTemplate;
import adams.parser.ActorTemplateSuggestion;

/**
 * Tests the adams.parser.ActorTemplateSuggestion class. Run from commandline with: <br><br>
 * java adams.parser.ActorTemplateSuggestionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorTemplateSuggestionTest
  extends AbstractExpressionEvaluatorTestCase<AbstractActorTemplate, ActorTemplateSuggestion> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ActorTemplateSuggestionTest(String name) {
    super(name);
  }

  /**
   * Returns the expressions used in the regression test.
   *
   * @return		the data
   */
  protected String[][] getRegressionExpressions() {
    return new String[][]{
	{
	  "IF PARENT IS adams.flow.control.Flow THEN adams.flow.template.UpdateVariable",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) THEN adams.flow.template.UpdateVariable",
	},
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected ActorTemplateSuggestion[] getRegressionSetups() {
    ActorTemplateSuggestion[]	result;

    result = new ActorTemplateSuggestion[1];

    result[0] = new ActorTemplateSuggestion();
    result[0].setParent(new adams.flow.control.Flow());
    result[0].setPosition(0);
    result[0].setActors(new AbstractActor[0]);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ActorTemplateSuggestionTest.class);
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
