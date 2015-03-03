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
 * UpdateContainerValueTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.control.Flow;
import adams.flow.control.UpdateContainerValue;
import adams.flow.core.AbstractActor;
import adams.flow.sink.SequencePlotter;
import adams.flow.transformer.Convert;
import adams.flow.transformer.MakePlotContainer;
import adams.flow.transformer.StringReplace;
import adams.flow.transformer.Convert;
import adams.data.conversion.IntToDouble;
import adams.flow.transformer.MathExpression;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.parser.MathematicalExpressionText;
import adams.test.TmpFile;

/**
 * Tests the UpdateContainerValue transformer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateContainerValueTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UpdateContainerValueTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.deleteFileFromTmp("dumpfile.txt");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("dumpfile.txt");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    adams.flow.source.ForLoop fl = new adams.flow.source.ForLoop();
    fl.setLoopLower(1);
    fl.setLoopUpper(30);
    fl.setLoopStep(1);

    IntToDouble i2d = new IntToDouble();
    Convert con = new Convert();
    con.setConversion(i2d);

    MathExpression me = new MathExpression();
    me.setExpression(new MathematicalExpressionText("X^2 + X"));

    MakePlotContainer make = new MakePlotContainer();
    make.setPlotName("X^2 + X");

    StringReplace sr = new StringReplace();
    sr.setFind(new BaseRegExp("$"));
    sr.setReplace(" * 100");

    UpdateContainerValue pcu1 = new UpdateContainerValue();
    pcu1.setContainerValueName(SequencePlotterContainer.VALUE_PLOTNAME);
    pcu1.setActors(new AbstractActor[]{sr});

    MathExpression me2 = new MathExpression();
    me2.setExpression(new MathematicalExpressionText("X * 100"));

    UpdateContainerValue pcu2 = new UpdateContainerValue();
    pcu2.setContainerValueName(SequencePlotterContainer.VALUE_Y);
    pcu2.setActors(new AbstractActor[]{me2});

    SequencePlotter sp = new SequencePlotter();
    sp.setPaintlet(new LinePaintlet());

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{fl, con, me, make, pcu1, pcu2, sp});

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UpdateContainerValueTest.class);
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
