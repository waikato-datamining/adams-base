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
 * ActorSuggestionTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.flow.core.AbstractActor;

/**
 * Tests the adams.parser.ActorSuggestion class. Run from commandline with: <p/>
 * java adams.parser.ActorSuggestionTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorSuggestionTest
  extends AbstractExpressionEvaluatorTestCase<AbstractActor, ActorSuggestion> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ActorSuggestionTest(String name) {
    super(name);
  }

  /**
   * Returns the expressions used in the regression test.
   *
   * @return		the data
   */
  @Override
  protected String[][] getRegressionExpressions() {
    return new String[][]{
	{
	  "IF PARENT IS adams.flow.control.Flow AND ISFIRST THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.standalone.Standalones THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.control.Branch THEN adams.flow.control.Sequence",
	  "IF AFTER adams.flow.transformer.MakePlotContainer THEN adams.flow.sink.SequencePlotter",
	  "IF AFTER adams.flow.source.Exec THEN adams.flow.sink.Display",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) AND ISFIRST THEN adams.flow.source.Start",
	},
	{
	  "IF PARENT IS adams.flow.control.Flow AND ISFIRST THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.standalone.Standalones THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.control.Branch THEN adams.flow.control.Sequence",
	  "IF AFTER adams.flow.transformer.MakePlotContainer THEN adams.flow.sink.SequencePlotter",
	  "IF AFTER adams.flow.source.Exec THEN adams.flow.sink.Display",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) AND ISFIRST THEN adams.flow.source.Start",
	},
	{
	  "IF PARENT IS adams.flow.control.Flow AND ISFIRST THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.standalone.Standalones THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.control.Branch THEN adams.flow.control.Sequence",
	  "IF AFTER adams.flow.transformer.MakePlotContainer THEN adams.flow.sink.SequencePlotter",
	  "IF AFTER adams.flow.source.Exec THEN adams.flow.sink.Display",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) AND ISFIRST THEN adams.flow.source.Start",
	},
	{
	  "IF PARENT IS adams.flow.control.Flow AND ISFIRST THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.standalone.Standalones THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.control.Branch THEN adams.flow.control.Sequence",
	  "IF AFTER adams.flow.transformer.MakePlotContainer THEN adams.flow.sink.SequencePlotter",
	  "IF AFTER adams.flow.source.Exec THEN adams.flow.sink.Display",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) AND ISFIRST THEN adams.flow.source.Start",
	},
	{
	  "IF PARENT IS adams.flow.control.Flow AND ISFIRST THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.standalone.Standalones THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.control.Branch THEN adams.flow.control.Sequence",
	  "IF AFTER adams.flow.transformer.MakePlotContainer THEN adams.flow.sink.SequencePlotter",
	  "IF AFTER adams.flow.source.Exec THEN adams.flow.sink.Display",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) AND ISFIRST THEN adams.flow.source.Start",
	},
	{
	  "IF PARENT IS adams.flow.control.Flow AND ISFIRST THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.standalone.Standalones THEN adams.flow.standalone.CallableActors",
	  "IF PARENT IS adams.flow.control.Branch THEN adams.flow.control.Sequence",
	  "IF AFTER adams.flow.transformer.MakePlotContainer THEN adams.flow.sink.SequencePlotter",
	  "IF AFTER adams.flow.source.Exec THEN adams.flow.sink.Display",
	  "IF (PARENT IS adams.flow.control.Flow OR PARENT IS adams.flow.control.Trigger) AND ISFIRST THEN adams.flow.source.Start",
	},
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ActorSuggestion[] getRegressionSetups() {
    ActorSuggestion[]	result;

    result = new ActorSuggestion[6];

    result[0] = new ActorSuggestion();
    result[0].setParent(new adams.flow.control.Flow());
    result[0].setPosition(0);
    result[0].setActors(new AbstractActor[0]);

    result[1] = new ActorSuggestion();
    result[1].setParent(new adams.flow.standalone.Standalones());
    result[1].setPosition(0);
    result[1].setActors(new AbstractActor[0]);

    result[2] = new ActorSuggestion();
    result[2].setParent(new adams.flow.control.Branch());
    result[2].setPosition(0);
    result[2].setActors(new AbstractActor[0]);

    result[3] = new ActorSuggestion();
    result[3].setParent(new adams.flow.control.Branch());
    result[3].setPosition(1);
    result[3].setActors(new AbstractActor[]{new adams.flow.control.Sequence()});

    result[4] = new ActorSuggestion();
    result[4].setParent(new adams.flow.control.Branch());
    result[4].setPosition(0);
    result[4].setActors(new AbstractActor[]{new adams.flow.control.Sequence()});

    result[5] = new ActorSuggestion();
    result[5].setParent(new adams.flow.control.Flow());
    result[5].setPosition(1);
    result[5].setActors(new AbstractActor[]{new adams.flow.transformer.MakePlotContainer()});

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ActorSuggestionTest.class);
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
