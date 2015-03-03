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
 * PlotProcessorTest.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.core.AbstractActor;

/**
 * Test for PlotProcessor actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class PlotProcessorTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PlotProcessorTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PlotProcessorTest.class);
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
      adams.flow.core.AbstractActor[] actors1 = new adams.flow.core.AbstractActor[7];

      // Flow.SetVariable
      adams.flow.standalone.SetVariable setvariable2 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((adams.core.VariableName) argOption.valueOf("i"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("0"));
      actors1[0] = setvariable2;

      // Flow.RandomNumberGenerator
      adams.flow.source.RandomNumberGenerator randomnumbergenerator5 = new adams.flow.source.RandomNumberGenerator();
      argOption = (AbstractArgumentOption) randomnumbergenerator5.getOptionManager().findByProperty("generator");
      adams.data.random.JavaRandomDouble javarandomdouble7 = new adams.data.random.JavaRandomDouble();
      randomnumbergenerator5.setGenerator(javarandomdouble7);

      argOption = (AbstractArgumentOption) randomnumbergenerator5.getOptionManager().findByProperty("maxNum");
      randomnumbergenerator5.setMaxNum((Integer) argOption.valueOf("200"));
      actors1[1] = randomnumbergenerator5;

      // Flow.IncVariable
      adams.flow.transformer.IncVariable incvariable9 = new adams.flow.transformer.IncVariable();
      argOption = (AbstractArgumentOption) incvariable9.getOptionManager().findByProperty("variableName");
      incvariable9.setVariableName((adams.core.VariableName) argOption.valueOf("i"));
      actors1[2] = incvariable9;

      // Flow.MakePlotContainer
      adams.flow.transformer.MakePlotContainer makeplotcontainer11 = new adams.flow.transformer.MakePlotContainer();
      actors1[3] = makeplotcontainer11;

      // Flow.SetPlotContainerValue
      adams.flow.transformer.SetPlotContainerValue setplotcontainervalue12 = new adams.flow.transformer.SetPlotContainerValue();
      argOption = (AbstractArgumentOption) setplotcontainervalue12.getOptionManager().findByProperty("containerValue");
      setplotcontainervalue12.setContainerValue((adams.flow.control.PlotContainerUpdater.PlotContainerValue) argOption.valueOf("X_VALUE"));
      argOption = (AbstractArgumentOption) setplotcontainervalue12.getOptionManager().findByProperty("value");
      argOption.setVariable("@{i}");
      actors1[4] = setplotcontainervalue12;

      // Flow.PlotProcessor
      adams.flow.control.PlotProcessor plotprocessor14 = new adams.flow.control.PlotProcessor();
      argOption = (AbstractArgumentOption) plotprocessor14.getOptionManager().findByProperty("processor");
      adams.flow.control.plotprocessor.LOWESS lowess16 = new adams.flow.control.plotprocessor.LOWESS();
      plotprocessor14.setProcessor(lowess16);

      actors1[5] = plotprocessor14;

      // Flow.SequencePlotter
      adams.flow.sink.SequencePlotter sequenceplotter17 = new adams.flow.sink.SequencePlotter();
      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("writer");
      adams.gui.print.NullWriter nullwriter19 = new adams.gui.print.NullWriter();
      sequenceplotter17.setWriter(nullwriter19);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("paintlet");
      adams.gui.visualization.sequence.CirclePaintlet xysequencecirclepaintlet21 = new adams.gui.visualization.sequence.CirclePaintlet();
      sequenceplotter17.setPaintlet(xysequencecirclepaintlet21);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("overlayPaintlet");
      adams.gui.visualization.sequence.LinePaintlet xysequencelinepaintlet23 = new adams.gui.visualization.sequence.LinePaintlet();
      sequenceplotter17.setOverlayPaintlet(xysequencelinepaintlet23);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("markerPaintlet");
      adams.flow.sink.sequenceplotter.NoMarkers nomarkers25 = new adams.flow.sink.sequenceplotter.NoMarkers();
      sequenceplotter17.setMarkerPaintlet(nomarkers25);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("errorPaintlet");
      adams.flow.sink.sequenceplotter.NoErrorPaintlet noerrorpaintlet27 = new adams.flow.sink.sequenceplotter.NoErrorPaintlet();
      sequenceplotter17.setErrorPaintlet(noerrorpaintlet27);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("colorProvider");
      adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider29 = new adams.gui.visualization.core.DefaultColorProvider();
      sequenceplotter17.setColorProvider(defaultcolorprovider29);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("overlayColorProvider");
      adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider31 = new adams.gui.visualization.core.DefaultColorProvider();
      sequenceplotter17.setOverlayColorProvider(defaultcolorprovider31);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("axisX");
      adams.gui.visualization.core.AxisPanelOptions axispaneloptions33 = new adams.gui.visualization.core.AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions33.getOptionManager().findByProperty("label");
      axispaneloptions33.setLabel((java.lang.String) argOption.valueOf("x"));
      argOption = (AbstractArgumentOption) axispaneloptions33.getOptionManager().findByProperty("tickGenerator");
      adams.gui.visualization.core.axis.SimpleTickGenerator simpletickgenerator36 = new adams.gui.visualization.core.axis.SimpleTickGenerator();
      argOption = (AbstractArgumentOption) simpletickgenerator36.getOptionManager().findByProperty("numTicks");
      simpletickgenerator36.setNumTicks((Integer) argOption.valueOf("20"));
      axispaneloptions33.setTickGenerator(simpletickgenerator36);

      argOption = (AbstractArgumentOption) axispaneloptions33.getOptionManager().findByProperty("width");
      axispaneloptions33.setWidth((Integer) argOption.valueOf("40"));
      sequenceplotter17.setAxisX(axispaneloptions33);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("axisY");
      adams.gui.visualization.core.AxisPanelOptions axispaneloptions40 = new adams.gui.visualization.core.AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions40.getOptionManager().findByProperty("label");
      axispaneloptions40.setLabel((java.lang.String) argOption.valueOf("y"));
      argOption = (AbstractArgumentOption) axispaneloptions40.getOptionManager().findByProperty("tickGenerator");
      adams.gui.visualization.core.axis.SimpleTickGenerator simpletickgenerator43 = new adams.gui.visualization.core.axis.SimpleTickGenerator();
      axispaneloptions40.setTickGenerator(simpletickgenerator43);

      argOption = (AbstractArgumentOption) axispaneloptions40.getOptionManager().findByProperty("width");
      axispaneloptions40.setWidth((Integer) argOption.valueOf("60"));
      sequenceplotter17.setAxisY(axispaneloptions40);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("plotUpdater");
      adams.flow.sink.sequenceplotter.SimplePlotUpdater simpleplotupdater46 = new adams.flow.sink.sequenceplotter.SimplePlotUpdater();
      sequenceplotter17.setPlotUpdater(simpleplotupdater46);

      argOption = (AbstractArgumentOption) sequenceplotter17.getOptionManager().findByProperty("postProcessor");
      adams.flow.sink.sequenceplotter.PassThrough passthrough48 = new adams.flow.sink.sequenceplotter.PassThrough();
      sequenceplotter17.setPostProcessor(passthrough48);

      actors1[6] = sequenceplotter17;
      flow.setActors(actors1);

      argOption = (AbstractArgumentOption) flow.getOptionManager().findByProperty("flowExecutionListener");
      adams.flow.execution.NullListener nulllistener50 = new adams.flow.execution.NullListener();
      flow.setFlowExecutionListener(nulllistener50);

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

