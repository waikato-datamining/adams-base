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
 * MOAMeasurementPlotGeneratorTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.core.option.AbstractArgumentOption;
import adams.data.DecimalFormatString;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.sink.SequencePlotter;
import adams.flow.source.MOAStream;
import adams.flow.standalone.CallableActors;
import adams.gui.print.NullWriter;
import adams.gui.visualization.sequence.LinePaintlet;

/**
 * Test for MOAMeasurementPlotGenerator actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class MOAMeasurementPlotGeneratorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public MOAMeasurementPlotGeneratorTest(String name) {
    super(name);
  }

  /**
   *
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(MOAMeasurementPlotGeneratorTest.class);
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
      AbstractActor[] tmp1 = new AbstractActor[6];
      CallableActors tmp2 = new CallableActors();
      argOption = (AbstractArgumentOption) tmp2.getOptionManager().findByProperty("actors");
      AbstractActor[] tmp3 = new AbstractActor[1];
      MOAClassifier tmp4 = new MOAClassifier();
      argOption = (AbstractArgumentOption) tmp4.getOptionManager().findByProperty("outputInterval");
      tmp4.setOutputInterval((Integer) argOption.valueOf("1"));

      tmp3[0] = tmp4;
      tmp2.setActors(tmp3);

      tmp1[0] = tmp2;
      MOAStream tmp6 = new MOAStream();
      argOption = (AbstractArgumentOption) tmp6.getOptionManager().findByProperty("numExamples");
      tmp6.setNumExamples((Integer) argOption.valueOf("300"));

      tmp1[1] = tmp6;
      MOAClassifierEvaluation tmp8 = new MOAClassifierEvaluation();
      argOption = (AbstractArgumentOption) tmp8.getOptionManager().findByProperty("outputInterval");
      tmp8.setOutputInterval((Integer) argOption.valueOf("100"));

      tmp1[2] = tmp8;
      MOAMeasurementsFilter tmp10 = new MOAMeasurementsFilter();
      argOption = (AbstractArgumentOption) tmp10.getOptionManager().findByProperty("regExp");
      tmp10.setRegExp((BaseRegExp) argOption.valueOf(".*instances"));

      tmp10.setInvertMatching(true);

      tmp1[3] = tmp10;
      MOAMeasurementPlotGenerator tmp12 = new MOAMeasurementPlotGenerator();
      tmp1[4] = tmp12;
      SequencePlotter tmp13 = new SequencePlotter();
      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("writer");
      NullWriter tmp15 = new NullWriter();
      tmp13.setWriter(tmp15);

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("paintlet");
      LinePaintlet tmp17 = new LinePaintlet();
      tmp13.setPaintlet(tmp17);

      tmp13.getAxisX().setCustomFormat(new DecimalFormatString("0"));

      argOption = (AbstractArgumentOption) tmp13.getOptionManager().findByProperty("title");
      tmp13.setTitle((String) argOption.valueOf("Learning evaluation"));

      tmp1[5] = tmp13;
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

