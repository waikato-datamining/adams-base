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
 * UpdateCallableDisplayTest.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.core.VariableName;
import adams.core.base.BaseText;
import adams.core.option.AbstractArgumentOption;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.condition.bool.Counting;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.CallableSink;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.NoErrorPaintlet;
import adams.flow.sink.sequenceplotter.NoMarkers;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.PassThrough;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.flow.source.ForLoop;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.MakePlotContainer;
import adams.flow.transformer.MathExpression;
import adams.gui.print.NullWriter;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.SimpleTickGenerator;
import adams.gui.visualization.sequence.LinePaintlet;
import adams.gui.visualization.sequence.NullPaintlet;
import adams.parser.MathematicalExpressionText;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Test for UpdateCallableDisplay actor.
 *
 * @author fracpete
 * @author adams.core.option.FlowJUnitTestProducer (code generator)
 * @version $Revision$
 */
public class UpdateCallableDisplayTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UpdateCallableDisplayTest(String name) {
    super(name);
  }

  /**
   * 
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UpdateCallableDisplayTest.class);
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

      // Flow.CallableActors
      CallableActors callableactors = new CallableActors();
      List<Actor> actors2 = new ArrayList<>();

      // Flow.CallableActors.SequencePlotter
      SequencePlotter sequenceplotter = new SequencePlotter();
      NullWriter nullwriter = new NullWriter();
      sequenceplotter.setWriter(nullwriter);

      LinePaintlet linepaintlet = new LinePaintlet();
      sequenceplotter.setPaintlet(linepaintlet);

      NullPaintlet nullpaintlet = new NullPaintlet();
      sequenceplotter.setOverlayPaintlet(nullpaintlet);

      NoMarkers nomarkers = new NoMarkers();
      sequenceplotter.setMarkerPaintlet(nomarkers);

      NoErrorPaintlet noerrorpaintlet = new NoErrorPaintlet();
      sequenceplotter.setErrorPaintlet(noerrorpaintlet);

      NullClickAction nullclickaction = new NullClickAction();
      sequenceplotter.setMouseClickAction(nullclickaction);

      DefaultColorProvider defaultcolorprovider = new DefaultColorProvider();
      sequenceplotter.setColorProvider(defaultcolorprovider);

      DefaultColorProvider defaultcolorprovider2 = new DefaultColorProvider();
      sequenceplotter.setOverlayColorProvider(defaultcolorprovider2);

      AxisPanelOptions axispaneloptions = new AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions.getOptionManager().findByProperty("label");
      axispaneloptions.setLabel((String) argOption.valueOf("x"));
      SimpleTickGenerator simpletickgenerator = new SimpleTickGenerator();
      argOption = (AbstractArgumentOption) simpletickgenerator.getOptionManager().findByProperty("numTicks");
      simpletickgenerator.setNumTicks((Integer) argOption.valueOf("20"));
      axispaneloptions.setTickGenerator(simpletickgenerator);

      argOption = (AbstractArgumentOption) axispaneloptions.getOptionManager().findByProperty("width");
      axispaneloptions.setWidth((Integer) argOption.valueOf("40"));
      sequenceplotter.setAxisX(axispaneloptions);

      AxisPanelOptions axispaneloptions2 = new AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("label");
      axispaneloptions2.setLabel((String) argOption.valueOf("y"));
      SimpleTickGenerator simpletickgenerator2 = new SimpleTickGenerator();
      axispaneloptions2.setTickGenerator(simpletickgenerator2);

      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("width");
      axispaneloptions2.setWidth((Integer) argOption.valueOf("60"));
      sequenceplotter.setAxisY(axispaneloptions2);

      SimplePlotUpdater simpleplotupdater = new SimplePlotUpdater();
      argOption = (AbstractArgumentOption) simpleplotupdater.getOptionManager().findByProperty("updateInterval");
      simpleplotupdater.setUpdateInterval((Integer) argOption.valueOf("-1"));
      sequenceplotter.setPlotUpdater(simpleplotupdater);

      PassThrough passthrough = new PassThrough();
      sequenceplotter.setPostProcessor(passthrough);

      actors2.add(sequenceplotter);
      callableactors.setActors(actors2.toArray(new Actor[0]));

      actors.add(callableactors);

      // Flow.SetVariable
      SetVariable setvariable = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
      setvariable.setVariableName((VariableName) argOption.valueOf("mean"));
      argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
      setvariable.setVariableValue((BaseText) argOption.valueOf("0"));
      actors.add(setvariable);

      // Flow.SetVariable-1
      SetVariable setvariable2 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("name");
      setvariable2.setName((String) argOption.valueOf("SetVariable-1"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
      setvariable2.setVariableName((VariableName) argOption.valueOf("stdev"));
      argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
      setvariable2.setVariableValue((BaseText) argOption.valueOf("1.0"));
      actors.add(setvariable2);

      // Flow.ForLoop
      ForLoop forloop = new ForLoop();
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopLower");
      forloop.setLoopLower((Integer) argOption.valueOf("-200"));
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopUpper");
      forloop.setLoopUpper((Integer) argOption.valueOf("200"));
      actors.add(forloop);

      // Flow.ConditionalTee
      ConditionalTee conditionaltee = new ConditionalTee();
      List<Actor> actors3 = new ArrayList<>();

      // Flow.ConditionalTee.UpdateCallableDisplay
      UpdateCallableDisplay updatecallabledisplay = new UpdateCallableDisplay();
      argOption = (AbstractArgumentOption) updatecallabledisplay.getOptionManager().findByProperty("callableName");
      updatecallabledisplay.setCallableName((CallableActorReference) argOption.valueOf("SequencePlotter"));
      actors3.add(updatecallabledisplay);
      conditionaltee.setActors(actors3.toArray(new Actor[0]));

      Counting counting = new Counting();
      argOption = (AbstractArgumentOption) counting.getOptionManager().findByProperty("interval");
      counting.setInterval((Integer) argOption.valueOf("50"));
      conditionaltee.setCondition(counting);

      actors.add(conditionaltee);

      // Flow.MathExpression
      MathExpression mathexpression = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression.getOptionManager().findByProperty("expression");
      mathexpression.setExpression((MathematicalExpressionText) argOption.valueOf("X/33"));
      actors.add(mathexpression);

      // Flow.MathExpression-1
      MathExpression mathexpression2 = new MathExpression();
      argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("name");
      mathexpression2.setName((String) argOption.valueOf("MathExpression-1"));
      argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("expression");
      mathexpression2.setExpression((MathematicalExpressionText) argOption.valueOf("1/(@{stdev}*sqrt(2*pi))*pow(e, (-pow((X-@{mean}), 2)/(2*pow(@{stdev},2))))"));
      mathexpression2.setOutputValuePair(true);

      actors.add(mathexpression2);

      // Flow.MakePlotContainer
      MakePlotContainer makeplotcontainer = new MakePlotContainer();
      argOption = (AbstractArgumentOption) makeplotcontainer.getOptionManager().findByProperty("plotName");
      makeplotcontainer.setPlotName((String) argOption.valueOf("normal"));
      actors.add(makeplotcontainer);

      // Flow.CallableSink
      CallableSink callablesink = new CallableSink();
      argOption = (AbstractArgumentOption) callablesink.getOptionManager().findByProperty("callableName");
      callablesink.setCallableName((CallableActorReference) argOption.valueOf("SequencePlotter"));
      actors.add(callablesink);
      flow.setActors(actors.toArray(new Actor[0]));

      NullListener nulllistener = new NullListener();
      flow.setFlowExecutionListener(nulllistener);

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

