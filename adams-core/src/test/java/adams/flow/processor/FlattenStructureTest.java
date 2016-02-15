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
 * FlattenStructureTest.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.condition.bool.Expression;
import adams.flow.core.Actor;
import adams.flow.source.Start;
import adams.parser.BooleanExpressionText;
import adams.parser.MathematicalExpressionText;

/**
 * Tests the FlattenStructure processor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlattenStructureTest
  extends AbstractActorProcessorTestCase {

  /**
   * Constructs the test.
   *
   * @param name 	the name of the test
   */
  public FlattenStructureTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    AbstractArgumentOption    argOption;

    adams.flow.control.Flow actor = new adams.flow.control.Flow();

    try {
      argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[3];
      adams.flow.standalone.SetVariable tmp2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableName");
      tmp2.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("variableValue");
      tmp2.setVariableValue((BaseText) argOption.valueOf("0"));

      tmp1[0] = tmp2;
      adams.flow.source.Start tmp5 = new adams.flow.source.Start();
      tmp1[1] = tmp5;
      adams.flow.control.WhileLoop tmp6 = new adams.flow.control.WhileLoop();
      Expression expr = new Expression();
      expr.setExpression(new BooleanExpressionText("@{i}<10"));
      tmp6.setCondition(expr);

      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp8 = new adams.flow.core.Actor[2];
      adams.flow.control.Sequence tmp9 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp9.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp10 = new adams.flow.core.Actor[1];
      adams.flow.control.Trigger tmp11 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp11.getOptionManager().findByProperty("teeActors");
      adams.flow.core.Actor[] tmp12 = new adams.flow.core.Actor[2];
      adams.flow.control.Sequence tmp13 = new adams.flow.control.Sequence();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp14 = new adams.flow.core.Actor[5];
      adams.flow.source.Variable tmp15 = new adams.flow.source.Variable();
      argOption = (AbstractArgumentOption) tmp15.getOptionManager().findByProperty("variableName");
      tmp15.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      tmp14[0] = tmp15;
      adams.flow.transformer.Convert tmp17 = new adams.flow.transformer.Convert();
      argOption = (AbstractArgumentOption) tmp17.getOptionManager().findByProperty("conversion");
      adams.data.conversion.StringToDouble tmp19 = new adams.data.conversion.StringToDouble();
      tmp17.setConversion(tmp19);

      tmp14[1] = tmp17;
      adams.flow.transformer.MathExpression tmp20 = new adams.flow.transformer.MathExpression();
      argOption = (AbstractArgumentOption) tmp20.getOptionManager().findByProperty("expression");
      tmp20.setExpression(new MathematicalExpressionText("X+1"));

      tmp14[2] = tmp20;
      adams.flow.transformer.SetVariable tmp22 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) tmp22.getOptionManager().findByProperty("variableName");
      tmp22.setVariableName((adams.core.VariableName) argOption.valueOf("i"));

      tmp14[3] = tmp22;
      adams.flow.sink.Display tmp24 = new adams.flow.sink.Display();
      tmp14[4] = tmp24;
      tmp13.setActors(tmp14);

      tmp12[0] = new Start();
      tmp12[1] = tmp13;
      tmp11.setActors(tmp12);

      tmp10[0] = tmp11;
      tmp9.setActors(tmp10);

      tmp8[0] = new Start();
      tmp8[1] = tmp9;
      tmp6.setActors(tmp8);

      tmp1[2] = tmp6;
      actor.setActors(tmp1);
    }
    catch (Exception e) {
      fail("Failed to set up actor: " + e);
    }

    return actor;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractActorProcessor[] getRegressionSetups() {
    return new AbstractActorProcessor[]{
	new FlattenStructure()
    };
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(FlattenStructureTest.class);
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
