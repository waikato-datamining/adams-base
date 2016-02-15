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
 * ClearCallableDisplayTest.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.option.AbstractArgumentOption;
import adams.data.DecimalFormatString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.Actor;

/**
 * Test for ClearCallableDisplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class ClearCallableDisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ClearCallableDisplayTest(String name) {
    super(name);
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ClearCallableDisplayTest.class);
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
      adams.flow.core.Actor[] tmp1 = new adams.flow.core.Actor[4];
      adams.flow.standalone.CallableActors tmp2 = new adams.flow.standalone.CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp3 = new adams.flow.core.Actor[1];
      adams.flow.sink.SequencePlotter tmp4 = new adams.flow.sink.SequencePlotter();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter tmp6 = new adams.gui.print.NullWriter();
      tmp4.setWriter(tmp6);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("paintlet");
      adams.gui.visualization.sequence.StickPaintlet tmp8 = new adams.gui.visualization.sequence.StickPaintlet();
      tmp4.setPaintlet(tmp8);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("markerPaintlet");
      adams.flow.sink.sequenceplotter.NoMarkers tmp10 = new adams.flow.sink.sequenceplotter.NoMarkers();
      tmp4.setMarkerPaintlet(tmp10);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("colorProvider");
      adams.gui.visualization.core.DefaultColorProvider tmp12 = new adams.gui.visualization.core.DefaultColorProvider();
      tmp4.setColorProvider(tmp12);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("axisX");
      adams.gui.visualization.core.AxisPanelOptions tmp14 = new adams.gui.visualization.core.AxisPanelOptions();
      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("label");
      tmp14.setLabel((java.lang.String) argOption.valueOf("x"));

      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("width");
      tmp14.setWidth((Integer) argOption.valueOf("40"));

      argOption = (AbstractArgumentOption) tmp14.getOptionManager().findByProperty("customFormat");
      tmp14.setCustomFormat(new DecimalFormatString("0.000"));

      tmp4.setAxisX(tmp14);

      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("axisY");
      adams.gui.visualization.core.AxisPanelOptions tmp19 = new adams.gui.visualization.core.AxisPanelOptions();
      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("label");
      tmp19.setLabel((java.lang.String) argOption.valueOf("y"));

      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("width");
      tmp19.setWidth((Integer) argOption.valueOf("40"));

      argOption = (AbstractArgumentOption) tmp19.getOptionManager().findByProperty("customFormat");
      tmp19.setCustomFormat(new DecimalFormatString("0.0"));

      tmp4.setAxisY(tmp19);

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      adams.flow.source.Start tmp24 = new adams.flow.source.Start();
      tmp1[1] = tmp24;
      adams.flow.control.Trigger tmp25 = new adams.flow.control.Trigger();
      argOption = (AbstractArgumentOption) tmp25.getOptionManager().findByProperty("actors");
      adams.flow.core.Actor[] tmp26 = new adams.flow.core.Actor[3];
      adams.flow.source.ForLoop tmp27 = new adams.flow.source.ForLoop();
      tmp26[0] = tmp27;
      adams.flow.transformer.MakePlotContainer tmp28 = new adams.flow.transformer.MakePlotContainer();
      tmp26[1] = tmp28;
      adams.flow.sink.CallableSink tmp29 = new adams.flow.sink.CallableSink();
      argOption = (AbstractArgumentOption) tmp29.getOptionManager().findByProperty("callableName");
      tmp29.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("SequencePlotter"));

      tmp26[2] = tmp29;
      tmp25.setActors(tmp26);

      tmp1[2] = tmp25;
      adams.flow.control.ClearCallableDisplay tmp31 = new adams.flow.control.ClearCallableDisplay();
      argOption = (AbstractArgumentOption) tmp31.getOptionManager().findByProperty("callableName");
      tmp31.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("SequencePlotter"));

      tmp1[3] = tmp31;
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

