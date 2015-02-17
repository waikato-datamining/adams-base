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
 * SimplePlotTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.base.BaseString;
import adams.data.conversion.StringToDouble;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.source.StringConstants;
import adams.flow.transformer.Convert;
import adams.flow.transformer.MakePlotContainer;
import adams.flow.transformer.MathExpression;
import adams.parser.MathematicalExpressionText;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the SimplePlot sink.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePlotTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public SimplePlotTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    StringConstants ids = new StringConstants();
    ids.setStrings(new BaseString[]{
	new BaseString("1"),
	new BaseString("2"),
	new BaseString("3"),
	new BaseString("4"),
	new BaseString("5"),
	new BaseString("6"),
	new BaseString("7"),
	new BaseString("8"),
	new BaseString("9"),
	new BaseString("10"),
	new BaseString("11"),
	new BaseString("12"),
	new BaseString("13"),
	new BaseString("14"),
	new BaseString("15"),
	new BaseString("16"),
	new BaseString("17"),
	new BaseString("18"),
	new BaseString("19"),
	new BaseString("20"),
	new BaseString("21"),
	new BaseString("22"),
	new BaseString("23"),
	new BaseString("24"),
	new BaseString("25"),
	new BaseString("26"),
	new BaseString("27"),
	new BaseString("28"),
	new BaseString("29"),
	new BaseString("30")
    });

    StringToDouble s2d = new StringToDouble();
    Convert con = new Convert();
    con.setConversion(s2d);

    MathExpression me = new MathExpression();
    me.setExpression(new MathematicalExpressionText("X^2"));
    me.setOutputValuePair(true);

    MakePlotContainer mspc = new MakePlotContainer();
    mspc.setPlotName("X^2");

    SimplePlot sp = new SimplePlot();

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{ids, con, me, mspc, sp});

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(SimplePlotTest.class);
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
