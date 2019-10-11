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
 * SystemPerformance.java
 * Copyright (C) 2015-2019 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Index;
import adams.core.VariableName;
import adams.core.base.BaseAnnotation;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractArgumentOption;
import adams.data.DecimalFormatString;
import adams.data.conversion.Conversion;
import adams.data.conversion.DoubleToString;
import adams.data.conversion.MultiConversion;
import adams.data.conversion.NumberToDouble;
import adams.data.conversion.StringToString;
import adams.data.random.JavaRandomDouble;
import adams.data.random.JavaRandomInt;
import adams.flow.condition.bool.Expression;
import adams.flow.control.ArrayProcess;
import adams.flow.control.ContainerValuePicker;
import adams.flow.control.Flow;
import adams.flow.control.Flow.ErrorHandling;
import adams.flow.control.IfThenElse;
import adams.flow.control.Sequence;
import adams.flow.control.Tee;
import adams.flow.control.TimedTee;
import adams.flow.control.TimedTrigger;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.core.CallableActorReference;
import adams.flow.execution.NullListener;
import adams.flow.sink.CallableSink;
import adams.flow.sink.Display;
import adams.flow.sink.DumpFile;
import adams.flow.sink.SequencePlotter;
import adams.flow.sink.sequenceplotter.NoErrorPaintlet;
import adams.flow.sink.sequenceplotter.NoMarkers;
import adams.flow.sink.sequenceplotter.NullClickAction;
import adams.flow.sink.sequenceplotter.PassThrough;
import adams.flow.sink.sequenceplotter.SimplePlotUpdater;
import adams.flow.source.CombineVariables;
import adams.flow.source.EnterManyValues;
import adams.flow.source.EnterManyValues.OutputType;
import adams.flow.source.ForLoop;
import adams.flow.source.RandomNumberGenerator;
import adams.flow.source.Start;
import adams.flow.source.Variable;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.flow.source.valuedefinition.DefaultValueDefinition;
import adams.flow.standalone.CallableActors;
import adams.flow.standalone.GridView;
import adams.flow.standalone.Standalones;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.Convert;
import adams.flow.transformer.DeleteFile;
import adams.flow.transformer.GetArrayElement;
import adams.flow.transformer.MakePlotContainer;
import adams.flow.transformer.MathExpression;
import adams.flow.transformer.SequenceToArray;
import adams.flow.transformer.SetVariable;
import adams.flow.transformer.StringJoin;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.print.NullWriter;
import adams.gui.visualization.core.AxisPanelOptions;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.axis.FancyTickGenerator;
import adams.gui.visualization.sequence.CirclePaintlet;
import adams.gui.visualization.sequence.metadatacolor.Dummy;
import adams.parser.BooleanExpressionText;
import adams.parser.MathematicalExpressionText;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the System performance.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SystemPerformance
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;

  /**
   * Initializes the menu item with no owner.
   */
  public SystemPerformance() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public SystemPerformance(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "performance.png";
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "System performance";
  }

  /**
   * Whether to use a runnable for launching.
   *
   * @return		true if to use runnable
   */
  protected boolean getUseThread() {
    return true;
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    final Flow 	flow;
    String 	msg;

    try {
      flow = (Flow) getActor();
      flow.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);
      msg = flow.setUp();
      if (msg == null)
	msg = flow.execute();
      flow.wrapUp();
      if (msg != null)
	GUIHelper.showErrorMessage(
	  getOwner(), "Failed to test system performance:\n" + msg);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	getOwner(), "Failed to test system performance:\n" + LoggingHelper.throwableToString(e));
    }
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_HELP;
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   * @throws Exception if set up fails
   */
  public Actor getActor() throws Exception {
    AbstractArgumentOption    argOption;

    adams.flow.control.Flow actor = new adams.flow.control.Flow();

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("annotations");
    actor.setAnnotations((BaseAnnotation) argOption.valueOf("Uses flow components to give an overview of the system\'s performance."));
    List<Actor> actors = new ArrayList<>();

    // Flow.System performance
    GridView gridview = new GridView();
    {
      argOption = (AbstractArgumentOption) gridview.getOptionManager().findByProperty("name");
      gridview.setName((String) argOption.valueOf("System performance"));
      gridview.setShortTitle(true);

      argOption = (AbstractArgumentOption) gridview.getOptionManager().findByProperty("x");
      gridview.setX((Integer) argOption.valueOf("-2"));
      argOption = (AbstractArgumentOption) gridview.getOptionManager().findByProperty("y");
      gridview.setY((Integer) argOption.valueOf("-2"));
      List<Actor> actors2 = new ArrayList<>();

      //
      SequencePlotter sequenceplotter = new SequencePlotter();
      argOption = (AbstractArgumentOption) sequenceplotter.getOptionManager().findByProperty("name");
      sequenceplotter.setName((String) argOption.valueOf("CPU"));
      NullWriter nullwriter = new NullWriter();
      sequenceplotter.setWriter(nullwriter);

      CirclePaintlet circlepaintlet = new CirclePaintlet();
      Dummy dummy = new Dummy();
      circlepaintlet.setMetaDataColor(dummy);

      sequenceplotter.setPaintlet(circlepaintlet);

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

      argOption = (AbstractArgumentOption) sequenceplotter.getOptionManager().findByProperty("title");
      sequenceplotter.setTitle((String) argOption.valueOf("CPU"));
      AxisPanelOptions axispaneloptions = new AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions.getOptionManager().findByProperty("label");
      axispaneloptions.setLabel((String) argOption.valueOf("evaluations"));
      FancyTickGenerator fancytickgenerator = new FancyTickGenerator();
      axispaneloptions.setTickGenerator(fancytickgenerator);

      argOption = (AbstractArgumentOption) axispaneloptions.getOptionManager().findByProperty("nthValueToShow");
      axispaneloptions.setNthValueToShow((Integer) argOption.valueOf("2"));
      argOption = (AbstractArgumentOption) axispaneloptions.getOptionManager().findByProperty("width");
      axispaneloptions.setWidth((Integer) argOption.valueOf("40"));
      argOption = (AbstractArgumentOption) axispaneloptions.getOptionManager().findByProperty("customFormat");
      axispaneloptions.setCustomFormat((DecimalFormatString) argOption.valueOf("0"));
      sequenceplotter.setAxisX(axispaneloptions);

      AxisPanelOptions axispaneloptions2 = new AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("label");
      axispaneloptions2.setLabel((String) argOption.valueOf("msec"));
      FancyTickGenerator fancytickgenerator2 = new FancyTickGenerator();
      axispaneloptions2.setTickGenerator(fancytickgenerator2);

      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("nthValueToShow");
      axispaneloptions2.setNthValueToShow((Integer) argOption.valueOf("2"));
      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("width");
      axispaneloptions2.setWidth((Integer) argOption.valueOf("60"));
      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("topMargin");
      axispaneloptions2.setTopMargin((Double) argOption.valueOf("0.05"));
      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("bottomMargin");
      axispaneloptions2.setBottomMargin((Double) argOption.valueOf("0.05"));
      argOption = (AbstractArgumentOption) axispaneloptions2.getOptionManager().findByProperty("customFormat");
      axispaneloptions2.setCustomFormat((DecimalFormatString) argOption.valueOf("0"));
      sequenceplotter.setAxisY(axispaneloptions2);

      SimplePlotUpdater simpleplotupdater = new SimplePlotUpdater();
      argOption = (AbstractArgumentOption) simpleplotupdater.getOptionManager().findByProperty("updateInterval");
      argOption.setVariable("@{cpu_plot_update}");
      sequenceplotter.setPlotUpdater(simpleplotupdater);

      PassThrough passthrough = new PassThrough();
      sequenceplotter.setPostProcessor(passthrough);

      actors2.add(sequenceplotter);

      //
      SequencePlotter sequenceplotter2 = new SequencePlotter();
      argOption = (AbstractArgumentOption) sequenceplotter2.getOptionManager().findByProperty("name");
      sequenceplotter2.setName((String) argOption.valueOf("Write"));
      NullWriter nullwriter2 = new NullWriter();
      sequenceplotter2.setWriter(nullwriter2);

      CirclePaintlet circlepaintlet2 = new CirclePaintlet();
      Dummy dummy2 = new Dummy();
      circlepaintlet2.setMetaDataColor(dummy2);

      sequenceplotter2.setPaintlet(circlepaintlet2);

      NoMarkers nomarkers2 = new NoMarkers();
      sequenceplotter2.setMarkerPaintlet(nomarkers2);

      NoErrorPaintlet noerrorpaintlet2 = new NoErrorPaintlet();
      sequenceplotter2.setErrorPaintlet(noerrorpaintlet2);

      NullClickAction nullclickaction2 = new NullClickAction();
      sequenceplotter2.setMouseClickAction(nullclickaction2);

      DefaultColorProvider defaultcolorprovider3 = new DefaultColorProvider();
      sequenceplotter2.setColorProvider(defaultcolorprovider3);

      DefaultColorProvider defaultcolorprovider4 = new DefaultColorProvider();
      sequenceplotter2.setOverlayColorProvider(defaultcolorprovider4);

      argOption = (AbstractArgumentOption) sequenceplotter2.getOptionManager().findByProperty("title");
      sequenceplotter2.setTitle((String) argOption.valueOf("Write"));
      AxisPanelOptions axispaneloptions3 = new AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions3.getOptionManager().findByProperty("label");
      axispaneloptions3.setLabel((String) argOption.valueOf("evaluations"));
      FancyTickGenerator fancytickgenerator3 = new FancyTickGenerator();
      axispaneloptions3.setTickGenerator(fancytickgenerator3);

      argOption = (AbstractArgumentOption) axispaneloptions3.getOptionManager().findByProperty("nthValueToShow");
      axispaneloptions3.setNthValueToShow((Integer) argOption.valueOf("2"));
      argOption = (AbstractArgumentOption) axispaneloptions3.getOptionManager().findByProperty("width");
      axispaneloptions3.setWidth((Integer) argOption.valueOf("40"));
      argOption = (AbstractArgumentOption) axispaneloptions3.getOptionManager().findByProperty("customFormat");
      axispaneloptions3.setCustomFormat((DecimalFormatString) argOption.valueOf("0"));
      sequenceplotter2.setAxisX(axispaneloptions3);

      AxisPanelOptions axispaneloptions4 = new AxisPanelOptions();
      argOption = (AbstractArgumentOption) axispaneloptions4.getOptionManager().findByProperty("label");
      axispaneloptions4.setLabel((String) argOption.valueOf("msec"));
      FancyTickGenerator fancytickgenerator4 = new FancyTickGenerator();
      axispaneloptions4.setTickGenerator(fancytickgenerator4);

      argOption = (AbstractArgumentOption) axispaneloptions4.getOptionManager().findByProperty("nthValueToShow");
      axispaneloptions4.setNthValueToShow((Integer) argOption.valueOf("2"));
      argOption = (AbstractArgumentOption) axispaneloptions4.getOptionManager().findByProperty("width");
      axispaneloptions4.setWidth((Integer) argOption.valueOf("60"));
      argOption = (AbstractArgumentOption) axispaneloptions4.getOptionManager().findByProperty("topMargin");
      axispaneloptions4.setTopMargin((Double) argOption.valueOf("0.05"));
      argOption = (AbstractArgumentOption) axispaneloptions4.getOptionManager().findByProperty("bottomMargin");
      axispaneloptions4.setBottomMargin((Double) argOption.valueOf("0.05"));
      argOption = (AbstractArgumentOption) axispaneloptions4.getOptionManager().findByProperty("customFormat");
      axispaneloptions4.setCustomFormat((DecimalFormatString) argOption.valueOf("0"));
      sequenceplotter2.setAxisY(axispaneloptions4);

      SimplePlotUpdater simpleplotupdater2 = new SimplePlotUpdater();
      argOption = (AbstractArgumentOption) simpleplotupdater2.getOptionManager().findByProperty("updateInterval");
      argOption.setVariable("@{write_plot_update}");
      sequenceplotter2.setPlotUpdater(simpleplotupdater2);

      PassThrough passthrough2 = new PassThrough();
      sequenceplotter2.setPostProcessor(passthrough2);

      actors2.add(sequenceplotter2);

      //
      Display display = new Display();
      argOption = (AbstractArgumentOption) display.getOptionManager().findByProperty("name");
      display.setName((String) argOption.valueOf("Overall"));
      adams.data.io.output.NullWriter nullwriter3 = new adams.data.io.output.NullWriter();
      display.setWriter(nullwriter3);

      actors2.add(display);
      gridview.setActors(actors2.toArray(new Actor[0]));

      argOption = (AbstractArgumentOption) gridview.getOptionManager().findByProperty("numRows");
      gridview.setNumRows((Integer) argOption.valueOf("3"));
      NullWriter nullwriter4 = new NullWriter();
      gridview.setWriter(nullwriter4);

    }
    actors.add(gridview);

    // Flow.CallableActors
    CallableActors callableactors = new CallableActors();
    {
      List<Actor> actors3 = new ArrayList<>();

      // Flow.CallableActors.timing_write
      Sequence sequence = new Sequence();
      {
        argOption = (AbstractArgumentOption) sequence.getOptionManager().findByProperty("name");
        sequence.setName((String) argOption.valueOf("timing_write"));
        List<Actor> actors4 = new ArrayList<>();

        // Flow.CallableActors.timing_write.ContainerValuePicker
        ContainerValuePicker containervaluepicker = new ContainerValuePicker();
        {
          argOption = (AbstractArgumentOption) containervaluepicker.getOptionManager().findByProperty("valueName");
          containervaluepicker.setValueName((String) argOption.valueOf("msec"));
          containervaluepicker.setSwitchOutputs(true);

        }
        actors4.add(containervaluepicker);

        // Flow.CallableActors.timing_write.MakePlotContainer
        MakePlotContainer makeplotcontainer = new MakePlotContainer();
        argOption = (AbstractArgumentOption) makeplotcontainer.getOptionManager().findByProperty("plotName");
        makeplotcontainer.setPlotName((String) argOption.valueOf("Write speed"));
        actors4.add(makeplotcontainer);

        // Flow.CallableActors.timing_write.CallableSink
        CallableSink callablesink = new CallableSink();
        argOption = (AbstractArgumentOption) callablesink.getOptionManager().findByProperty("callableName");
        callablesink.setCallableName((CallableActorReference) argOption.valueOf("Write"));
        actors4.add(callablesink);
        sequence.setActors(actors4.toArray(new Actor[0]));

      }
      actors3.add(sequence);

      // Flow.CallableActors.timing_cpuspeed
      Sequence sequence2 = new Sequence();
      {
        argOption = (AbstractArgumentOption) sequence2.getOptionManager().findByProperty("name");
        sequence2.setName((String) argOption.valueOf("timing_cpuspeed"));
        List<Actor> actors5 = new ArrayList<>();

        // Flow.CallableActors.timing_cpuspeed.ContainerValuePicker
        ContainerValuePicker containervaluepicker2 = new ContainerValuePicker();
        {
          argOption = (AbstractArgumentOption) containervaluepicker2.getOptionManager().findByProperty("valueName");
          containervaluepicker2.setValueName((String) argOption.valueOf("msec"));
          containervaluepicker2.setSwitchOutputs(true);

        }
        actors5.add(containervaluepicker2);

        // Flow.CallableActors.timing_cpuspeed.MakePlotContainer
        MakePlotContainer makeplotcontainer2 = new MakePlotContainer();
        argOption = (AbstractArgumentOption) makeplotcontainer2.getOptionManager().findByProperty("plotName");
        makeplotcontainer2.setPlotName((String) argOption.valueOf("CPU Speed"));
        actors5.add(makeplotcontainer2);

        // Flow.CallableActors.timing_cpuspeed.CallableSink
        CallableSink callablesink2 = new CallableSink();
        argOption = (AbstractArgumentOption) callablesink2.getOptionManager().findByProperty("callableName");
        callablesink2.setCallableName((CallableActorReference) argOption.valueOf("CPU"));
        actors5.add(callablesink2);
        sequence2.setActors(actors5.toArray(new Actor[0]));

      }
      actors3.add(sequence2);

      // Flow.CallableActors.timing_overall
      Sequence sequence3 = new Sequence();
      {
        argOption = (AbstractArgumentOption) sequence3.getOptionManager().findByProperty("name");
        sequence3.setName((String) argOption.valueOf("timing_overall"));
        List<Actor> actors6 = new ArrayList<>();

        // Flow.CallableActors.timing_overall.ContainerValuePicker
        ContainerValuePicker containervaluepicker3 = new ContainerValuePicker();
        {
          List<Actor> actors7 = new ArrayList<>();

          // Flow.CallableActors.timing_overall.ContainerValuePicker.SetVariable
          SetVariable setvariable = new SetVariable();
          argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
          setvariable.setVariableName((VariableName) argOption.valueOf("msec"));
          actors7.add(setvariable);
          containervaluepicker3.setActors(actors7.toArray(new Actor[0]));

          argOption = (AbstractArgumentOption) containervaluepicker3.getOptionManager().findByProperty("valueName");
          containervaluepicker3.setValueName((String) argOption.valueOf("msec"));
        }
        actors6.add(containervaluepicker3);

        // Flow.CallableActors.timing_overall.ContainerValuePicker-1
        ContainerValuePicker containervaluepicker4 = new ContainerValuePicker();
        {
          argOption = (AbstractArgumentOption) containervaluepicker4.getOptionManager().findByProperty("name");
          containervaluepicker4.setName((String) argOption.valueOf("ContainerValuePicker-1"));
          List<Actor> actors8 = new ArrayList<>();

          // Flow.CallableActors.timing_overall.ContainerValuePicker-1.SetVariable
          SetVariable setvariable2 = new SetVariable();
          argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
          setvariable2.setVariableName((VariableName) argOption.valueOf("prefix"));
          actors8.add(setvariable2);
          containervaluepicker4.setActors(actors8.toArray(new Actor[0]));

          argOption = (AbstractArgumentOption) containervaluepicker4.getOptionManager().findByProperty("valueName");
          containervaluepicker4.setValueName((String) argOption.valueOf("Prefix"));
        }
        actors6.add(containervaluepicker4);

        // Flow.CallableActors.timing_overall.generate output
        Trigger trigger = new Trigger();
        {
          argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
          trigger.setName((String) argOption.valueOf("generate output"));
          List<Actor> actors9 = new ArrayList<>();

          // Flow.CallableActors.timing_overall.generate output.CombineVariables
          CombineVariables combinevariables = new CombineVariables();
          argOption = (AbstractArgumentOption) combinevariables.getOptionManager().findByProperty("expression");
          combinevariables.setExpression((BaseText) argOption.valueOf("Overall @{prefix}: @{msec} msec"));
          StringToString stringtostring = new StringToString();
          combinevariables.setConversion(stringtostring);

          actors9.add(combinevariables);

          // Flow.CallableActors.timing_overall.generate output.CallableSink
          CallableSink callablesink3 = new CallableSink();
          argOption = (AbstractArgumentOption) callablesink3.getOptionManager().findByProperty("callableName");
          callablesink3.setCallableName((CallableActorReference) argOption.valueOf("Overall"));
          actors9.add(callablesink3);
          trigger.setActors(actors9.toArray(new Actor[0]));

        }
        actors6.add(trigger);
        sequence3.setActors(actors6.toArray(new Actor[0]));

      }
      actors3.add(sequence3);
      callableactors.setActors(actors3.toArray(new Actor[0]));

    }
    actors.add(callableactors);

    // Flow.speed variables
    Standalones standalones = new Standalones();
    {
      argOption = (AbstractArgumentOption) standalones.getOptionManager().findByProperty("name");
      standalones.setName((String) argOption.valueOf("speed variables"));
      List<Actor> actors10 = new ArrayList<>();

      // Flow.speed variables.# curves
      adams.flow.standalone.SetVariable setvariable3 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("name");
      setvariable3.setName((String) argOption.valueOf("# curves"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
      setvariable3.setVariableName((VariableName) argOption.valueOf("num_curves"));
      argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableValue");
      setvariable3.setVariableValue((BaseText) argOption.valueOf("500"));
      actors10.add(setvariable3);

      // Flow.speed variables.# means
      adams.flow.standalone.SetVariable setvariable4 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("name");
      setvariable4.setName((String) argOption.valueOf("# means"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((VariableName) argOption.valueOf("num_means"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableValue");
      setvariable4.setVariableValue((BaseText) argOption.valueOf("5"));
      actors10.add(setvariable4);

      // Flow.speed variables.# stdevs
      adams.flow.standalone.SetVariable setvariable5 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("name");
      setvariable5.setName((String) argOption.valueOf("# stdevs"));
      argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("variableName");
      setvariable5.setVariableName((VariableName) argOption.valueOf("num_stdevs"));
      argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("variableValue");
      setvariable5.setVariableValue((BaseText) argOption.valueOf("5"));
      actors10.add(setvariable5);

      // Flow.speed variables.# data points
      adams.flow.standalone.SetVariable setvariable6 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("name");
      setvariable6.setName((String) argOption.valueOf("# data points"));
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableName");
      setvariable6.setVariableName((VariableName) argOption.valueOf("num_points"));
      argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableValue");
      setvariable6.setVariableValue((BaseText) argOption.valueOf("200"));
      actors10.add(setvariable6);

      // Flow.speed variables.cpu plot update interval
      adams.flow.standalone.SetVariable setvariable7 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("name");
      setvariable7.setName((String) argOption.valueOf("cpu plot update interval"));
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableName");
      setvariable7.setVariableName((VariableName) argOption.valueOf("cpu_plot_update"));
      argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableValue");
      setvariable7.setVariableValue((BaseText) argOption.valueOf("100"));
      actors10.add(setvariable7);
      standalones.setActors(actors10.toArray(new Actor[0]));

    }
    actors.add(standalones);

    // Flow.write variables
    Standalones standalones2 = new Standalones();
    {
      argOption = (AbstractArgumentOption) standalones2.getOptionManager().findByProperty("name");
      standalones2.setName((String) argOption.valueOf("write variables"));
      List<Actor> actors11 = new ArrayList<>();

      // Flow.write variables.number of files
      adams.flow.standalone.SetVariable setvariable8 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("name");
      setvariable8.setName((String) argOption.valueOf("number of files"));
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("variableName");
      setvariable8.setVariableName((VariableName) argOption.valueOf("num_files"));
      argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("variableValue");
      setvariable8.setVariableValue((BaseText) argOption.valueOf("500"));
      actors11.add(setvariable8);

      // Flow.write variables.number of numbers per file
      adams.flow.standalone.SetVariable setvariable9 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable9.getOptionManager().findByProperty("name");
      setvariable9.setName((String) argOption.valueOf("number of numbers per file"));
      argOption = (AbstractArgumentOption) setvariable9.getOptionManager().findByProperty("variableName");
      setvariable9.setVariableName((VariableName) argOption.valueOf("num_rand"));
      argOption = (AbstractArgumentOption) setvariable9.getOptionManager().findByProperty("variableValue");
      setvariable9.setVariableValue((BaseText) argOption.valueOf("1000"));
      actors11.add(setvariable9);

      // Flow.write variables.combine numbers
      adams.flow.standalone.SetVariable setvariable10 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable10.getOptionManager().findByProperty("name");
      setvariable10.setName((String) argOption.valueOf("combine numbers"));
      argOption = (AbstractArgumentOption) setvariable10.getOptionManager().findByProperty("variableName");
      setvariable10.setVariableName((VariableName) argOption.valueOf("combine_numbers"));
      argOption = (AbstractArgumentOption) setvariable10.getOptionManager().findByProperty("variableValue");
      setvariable10.setVariableValue((BaseText) argOption.valueOf("true"));
      actors11.add(setvariable10);

      // Flow.write variables.output file
      adams.flow.standalone.SetVariable setvariable11 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable11.getOptionManager().findByProperty("name");
      setvariable11.setName((String) argOption.valueOf("output file"));
      argOption = (AbstractArgumentOption) setvariable11.getOptionManager().findByProperty("variableName");
      setvariable11.setVariableName((VariableName) argOption.valueOf("outfile"));
      argOption = (AbstractArgumentOption) setvariable11.getOptionManager().findByProperty("variableValue");
      setvariable11.setVariableValue((BaseText) argOption.valueOf("${TMP}/rand.txt"));
      actors11.add(setvariable11);

      // Flow.write variables.write plot update interval
      adams.flow.standalone.SetVariable setvariable12 = new adams.flow.standalone.SetVariable();
      argOption = (AbstractArgumentOption) setvariable12.getOptionManager().findByProperty("name");
      setvariable12.setName((String) argOption.valueOf("write plot update interval"));
      argOption = (AbstractArgumentOption) setvariable12.getOptionManager().findByProperty("variableName");
      setvariable12.setVariableName((VariableName) argOption.valueOf("write_plot_update"));
      argOption = (AbstractArgumentOption) setvariable12.getOptionManager().findByProperty("variableValue");
      setvariable12.setVariableValue((BaseText) argOption.valueOf("100"));
      actors11.add(setvariable12);
      standalones2.setActors(actors11.toArray(new Actor[0]));

    }
    actors.add(standalones2);

    // Flow.Start
    Start start = new Start();
    actors.add(start);

    // Flow.prompt user
    Trigger trigger2 = new Trigger();
    {
      argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
      trigger2.setName((String) argOption.valueOf("prompt user"));
      List<Actor> actors12 = new ArrayList<>();

      // Flow.prompt user.Enter parameters
      EnterManyValues entermanyvalues = new EnterManyValues();
      argOption = (AbstractArgumentOption) entermanyvalues.getOptionManager().findByProperty("name");
      entermanyvalues.setName((String) argOption.valueOf("Enter parameters"));
      entermanyvalues.setStopFlowIfCanceled(true);

      argOption = (AbstractArgumentOption) entermanyvalues.getOptionManager().findByProperty("message");
      entermanyvalues.setMessage((String) argOption.valueOf("Please enter the performance test parameters"));
      List<AbstractValueDefinition> values = new ArrayList<>();
      DefaultValueDefinition defaultvaluedefinition = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition.getOptionManager().findByProperty("name");
      defaultvaluedefinition.setName((String) argOption.valueOf("num_curves"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition.getOptionManager().findByProperty("display");
      defaultvaluedefinition.setDisplay((String) argOption.valueOf("CPU: # curves"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition.getOptionManager().findByProperty("help");
      defaultvaluedefinition.setHelp((String) argOption.valueOf("The number of bell curves to calculate"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition.getOptionManager().findByProperty("type");
      defaultvaluedefinition.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{num_curves}");
      values.add(defaultvaluedefinition);
      DefaultValueDefinition defaultvaluedefinition2 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition2.getOptionManager().findByProperty("name");
      defaultvaluedefinition2.setName((String) argOption.valueOf("num_means"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition2.getOptionManager().findByProperty("display");
      defaultvaluedefinition2.setDisplay((String) argOption.valueOf("CPU: # means"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition2.getOptionManager().findByProperty("help");
      defaultvaluedefinition2.setHelp((String) argOption.valueOf("The number of different bell curve means to use in each iteration"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition2.getOptionManager().findByProperty("type");
      defaultvaluedefinition2.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition2.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{num_means}");
      values.add(defaultvaluedefinition2);
      DefaultValueDefinition defaultvaluedefinition3 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition3.getOptionManager().findByProperty("name");
      defaultvaluedefinition3.setName((String) argOption.valueOf("num_stdevs"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition3.getOptionManager().findByProperty("display");
      defaultvaluedefinition3.setDisplay((String) argOption.valueOf("CPU: # standard deviations"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition3.getOptionManager().findByProperty("help");
      defaultvaluedefinition3.setHelp((String) argOption.valueOf("The number of different bell curve standard deviations to use in each iteration"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition3.getOptionManager().findByProperty("type");
      defaultvaluedefinition3.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition3.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{num_stdevs}");
      values.add(defaultvaluedefinition3);
      DefaultValueDefinition defaultvaluedefinition4 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition4.getOptionManager().findByProperty("name");
      defaultvaluedefinition4.setName((String) argOption.valueOf("num_points"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition4.getOptionManager().findByProperty("display");
      defaultvaluedefinition4.setDisplay((String) argOption.valueOf("CPU: # data points"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition4.getOptionManager().findByProperty("help");
      defaultvaluedefinition4.setHelp((String) argOption.valueOf("The number of data points to calculate per bell curve"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition4.getOptionManager().findByProperty("type");
      defaultvaluedefinition4.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition4.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{num_points}");
      values.add(defaultvaluedefinition4);
      DefaultValueDefinition defaultvaluedefinition5 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition5.getOptionManager().findByProperty("name");
      defaultvaluedefinition5.setName((String) argOption.valueOf("cpu_plot_update"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition5.getOptionManager().findByProperty("display");
      defaultvaluedefinition5.setDisplay((String) argOption.valueOf("CPU: plot update interval"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition5.getOptionManager().findByProperty("help");
      defaultvaluedefinition5.setHelp((String) argOption.valueOf("After how many iterations to refresh the plot"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition5.getOptionManager().findByProperty("type");
      defaultvaluedefinition5.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition5.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{cpu_plot_update}");
      values.add(defaultvaluedefinition5);
      DefaultValueDefinition defaultvaluedefinition6 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition6.getOptionManager().findByProperty("name");
      defaultvaluedefinition6.setName((String) argOption.valueOf("num_files"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition6.getOptionManager().findByProperty("display");
      defaultvaluedefinition6.setDisplay((String) argOption.valueOf("Write: # files"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition6.getOptionManager().findByProperty("help");
      defaultvaluedefinition6.setHelp((String) argOption.valueOf("The number of files to generate"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition6.getOptionManager().findByProperty("type");
      defaultvaluedefinition6.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition6.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{num_files}");
      values.add(defaultvaluedefinition6);
      DefaultValueDefinition defaultvaluedefinition7 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition7.getOptionManager().findByProperty("name");
      defaultvaluedefinition7.setName((String) argOption.valueOf("num_rand"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition7.getOptionManager().findByProperty("display");
      defaultvaluedefinition7.setDisplay((String) argOption.valueOf("Write: # random numbers per file"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition7.getOptionManager().findByProperty("help");
      defaultvaluedefinition7.setHelp((String) argOption.valueOf("How many random numbers to store in a single file"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition7.getOptionManager().findByProperty("type");
      defaultvaluedefinition7.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition7.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{num_rand}");
      values.add(defaultvaluedefinition7);
      DefaultValueDefinition defaultvaluedefinition8 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition8.getOptionManager().findByProperty("name");
      defaultvaluedefinition8.setName((String) argOption.valueOf("combine_numbers"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition8.getOptionManager().findByProperty("display");
      defaultvaluedefinition8.setDisplay((String) argOption.valueOf("Write: single write operation per file"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition8.getOptionManager().findByProperty("help");
      defaultvaluedefinition8.setHelp((String) argOption.valueOf("Whether to save one number at a time or all at once"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition8.getOptionManager().findByProperty("type");
      defaultvaluedefinition8.setType((PropertyType) argOption.valueOf("BOOLEAN"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition8.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{combine_numbers}");
      values.add(defaultvaluedefinition8);
      DefaultValueDefinition defaultvaluedefinition9 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition9.getOptionManager().findByProperty("name");
      defaultvaluedefinition9.setName((String) argOption.valueOf("outfile"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition9.getOptionManager().findByProperty("display");
      defaultvaluedefinition9.setDisplay((String) argOption.valueOf("Write: temporary file"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition9.getOptionManager().findByProperty("help");
      defaultvaluedefinition9.setHelp((String) argOption.valueOf("The temporary to use for saving the random numbers"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition9.getOptionManager().findByProperty("type");
      defaultvaluedefinition9.setType((PropertyType) argOption.valueOf("FILE_ABSOLUTE"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition9.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{outfile}");
      values.add(defaultvaluedefinition9);
      DefaultValueDefinition defaultvaluedefinition10 = new DefaultValueDefinition();
      argOption = (AbstractArgumentOption) defaultvaluedefinition10.getOptionManager().findByProperty("name");
      defaultvaluedefinition10.setName((String) argOption.valueOf("write_plot_update"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition10.getOptionManager().findByProperty("display");
      defaultvaluedefinition10.setDisplay((String) argOption.valueOf("Write: plot update interval"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition10.getOptionManager().findByProperty("help");
      defaultvaluedefinition10.setHelp((String) argOption.valueOf("After how many iterations to refresh the plot"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition10.getOptionManager().findByProperty("type");
      defaultvaluedefinition10.setType((PropertyType) argOption.valueOf("INTEGER"));
      argOption = (AbstractArgumentOption) defaultvaluedefinition10.getOptionManager().findByProperty("defaultValue");
      argOption.setVariable("@{write_plot_update}");
      values.add(defaultvaluedefinition10);
      entermanyvalues.setValues(values.toArray(new AbstractValueDefinition[0]));

      argOption = (AbstractArgumentOption) entermanyvalues.getOptionManager().findByProperty("outputType");
      entermanyvalues.setOutputType((OutputType) argOption.valueOf("KEY_VALUE_PAIRS"));
      actors12.add(entermanyvalues);

      // Flow.prompt user.output param
      Tee tee = new Tee();
      {
        argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
        tee.setName((String) argOption.valueOf("output param"));
        List<Actor> actors13 = new ArrayList<>();

        // Flow.prompt user.output param.StringJoin
        StringJoin stringjoin = new StringJoin();
        argOption = (AbstractArgumentOption) stringjoin.getOptionManager().findByProperty("glue");
        stringjoin.setGlue((String) argOption.valueOf(": "));
        actors13.add(stringjoin);

        // Flow.prompt user.output param.CallableSink
        CallableSink callablesink4 = new CallableSink();
        argOption = (AbstractArgumentOption) callablesink4.getOptionManager().findByProperty("callableName");
        callablesink4.setCallableName((CallableActorReference) argOption.valueOf("Overall"));
        actors13.add(callablesink4);
        tee.setActors(actors13.toArray(new Actor[0]));

      }
      actors12.add(tee);

      // Flow.prompt user.var name
      Tee tee2 = new Tee();
      {
        argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
        tee2.setName((String) argOption.valueOf("var name"));
        List<Actor> actors14 = new ArrayList<>();

        // Flow.prompt user.var name.GetArrayElement
        GetArrayElement getarrayelement = new GetArrayElement();
        actors14.add(getarrayelement);

        // Flow.prompt user.var name.SetVariable
        SetVariable setvariable13 = new SetVariable();
        argOption = (AbstractArgumentOption) setvariable13.getOptionManager().findByProperty("variableName");
        setvariable13.setVariableName((VariableName) argOption.valueOf("name"));
        actors14.add(setvariable13);
        tee2.setActors(actors14.toArray(new Actor[0]));

      }
      actors12.add(tee2);

      // Flow.prompt user.var value
      Tee tee3 = new Tee();
      {
        argOption = (AbstractArgumentOption) tee3.getOptionManager().findByProperty("name");
        tee3.setName((String) argOption.valueOf("var value"));
        List<Actor> actors15 = new ArrayList<>();

        // Flow.prompt user.var value.GetArrayElement
        GetArrayElement getarrayelement2 = new GetArrayElement();
        argOption = (AbstractArgumentOption) getarrayelement2.getOptionManager().findByProperty("index");
        getarrayelement2.setIndex((Index) argOption.valueOf("2"));
        actors15.add(getarrayelement2);

        // Flow.prompt user.var value.SetVariable
        SetVariable setvariable14 = new SetVariable();
        argOption = (AbstractArgumentOption) setvariable14.getOptionManager().findByProperty("variableName");
        argOption.setVariable("@{name}");
        actors15.add(setvariable14);
        tee3.setActors(actors15.toArray(new Actor[0]));

      }
      actors12.add(tee3);
      trigger2.setActors(actors12.toArray(new Actor[0]));

    }
    actors.add(trigger2);

    // Flow.speed
    TimedTrigger timedtrigger = new TimedTrigger();
    {
      argOption = (AbstractArgumentOption) timedtrigger.getOptionManager().findByProperty("name");
      timedtrigger.setName((String) argOption.valueOf("speed"));
      List<Actor> actors16 = new ArrayList<>();

      // Flow.speed.ForLoop
      ForLoop forloop = new ForLoop();
      argOption = (AbstractArgumentOption) forloop.getOptionManager().findByProperty("loopUpper");
      argOption.setVariable("@{num_curves}");
      actors16.add(forloop);

      // Flow.speed.calc curves
      TimedTrigger timedtrigger2 = new TimedTrigger();
      {
        argOption = (AbstractArgumentOption) timedtrigger2.getOptionManager().findByProperty("name");
        timedtrigger2.setName((String) argOption.valueOf("calc curves"));
        List<Actor> actors17 = new ArrayList<>();

        // Flow.speed.calc curves.ForLoop
        ForLoop forloop2 = new ForLoop();
        argOption = (AbstractArgumentOption) forloop2.getOptionManager().findByProperty("loopUpper");
        argOption.setVariable("@{num_means}");
        actors17.add(forloop2);

        // Flow.speed.calc curves.MathExpression
        MathExpression mathexpression = new MathExpression();
        argOption = (AbstractArgumentOption) mathexpression.getOptionManager().findByProperty("expression");
        mathexpression.setExpression((MathematicalExpressionText) argOption.valueOf("X / @{num_means}"));
        actors17.add(mathexpression);

        // Flow.speed.calc curves.SetVariable
        SetVariable setvariable15 = new SetVariable();
        argOption = (AbstractArgumentOption) setvariable15.getOptionManager().findByProperty("variableName");
        setvariable15.setVariableName((VariableName) argOption.valueOf("mean"));
        actors17.add(setvariable15);

        // Flow.speed.calc curves.mean
        Trigger trigger3 = new Trigger();
        {
          argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
          trigger3.setName((String) argOption.valueOf("mean"));
          List<Actor> actors18 = new ArrayList<>();

          // Flow.speed.calc curves.mean.ForLoop
          ForLoop forloop3 = new ForLoop();
          argOption = (AbstractArgumentOption) forloop3.getOptionManager().findByProperty("loopUpper");
          argOption.setVariable("@{num_stdevs}");
          actors18.add(forloop3);

          // Flow.speed.calc curves.mean.MathExpression
          MathExpression mathexpression2 = new MathExpression();
          argOption = (AbstractArgumentOption) mathexpression2.getOptionManager().findByProperty("expression");
          mathexpression2.setExpression((MathematicalExpressionText) argOption.valueOf("X / @{num_stdevs}"));
          actors18.add(mathexpression2);

          // Flow.speed.calc curves.mean.SetVariable
          SetVariable setvariable16 = new SetVariable();
          argOption = (AbstractArgumentOption) setvariable16.getOptionManager().findByProperty("variableName");
          setvariable16.setVariableName((VariableName) argOption.valueOf("stdev"));
          actors18.add(setvariable16);

          // Flow.speed.calc curves.mean.stdev
          Trigger trigger4 = new Trigger();
          {
            argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
            trigger4.setName((String) argOption.valueOf("stdev"));
            List<Actor> actors19 = new ArrayList<>();

            // Flow.speed.calc curves.mean.stdev.plot name
            adams.flow.standalone.SetVariable setvariable17 = new adams.flow.standalone.SetVariable();
            argOption = (AbstractArgumentOption) setvariable17.getOptionManager().findByProperty("name");
            setvariable17.setName((String) argOption.valueOf("plot name"));
            argOption = (AbstractArgumentOption) setvariable17.getOptionManager().findByProperty("variableName");
            setvariable17.setVariableName((VariableName) argOption.valueOf("plot_name"));
            argOption = (AbstractArgumentOption) setvariable17.getOptionManager().findByProperty("variableValue");
            setvariable17.setVariableValue((BaseText) argOption.valueOf("@{mean}/@{stdev}"));
            setvariable17.setExpandValue(true);

            actors19.add(setvariable17);

            // Flow.speed.calc curves.mean.stdev.ForLoop
            ForLoop forloop4 = new ForLoop();
            argOption = (AbstractArgumentOption) forloop4.getOptionManager().findByProperty("loopLower");
            forloop4.setLoopLower((Integer) argOption.valueOf("0"));
            argOption = (AbstractArgumentOption) forloop4.getOptionManager().findByProperty("loopUpper");
            argOption.setVariable("@{num_points}");
            actors19.add(forloop4);

            // Flow.speed.calc curves.mean.stdev.MathExpression
            MathExpression mathexpression3 = new MathExpression();
            argOption = (AbstractArgumentOption) mathexpression3.getOptionManager().findByProperty("expression");
            mathexpression3.setExpression((MathematicalExpressionText) argOption.valueOf("(X - (@{num_points} / 2)) / 33"));
            actors19.add(mathexpression3);

            // Flow.speed.calc curves.mean.stdev.MathExpression-1
            MathExpression mathexpression4 = new MathExpression();
            argOption = (AbstractArgumentOption) mathexpression4.getOptionManager().findByProperty("name");
            mathexpression4.setName((String) argOption.valueOf("MathExpression-1"));
            argOption = (AbstractArgumentOption) mathexpression4.getOptionManager().findByProperty("expression");
            mathexpression4.setExpression((MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
            mathexpression4.setOutputValuePair(true);

            actors19.add(mathexpression4);
            trigger4.setActors(actors19.toArray(new Actor[0]));

          }
          actors18.add(trigger4);
          trigger3.setActors(actors18.toArray(new Actor[0]));

        }
        actors17.add(trigger3);
        timedtrigger2.setActors(actors17.toArray(new Actor[0]));

        argOption = (AbstractArgumentOption) timedtrigger2.getOptionManager().findByProperty("callableName");
        timedtrigger2.setCallableName((CallableActorReference) argOption.valueOf("timing_cpuspeed"));
      }
      actors16.add(timedtrigger2);
      timedtrigger.setActors(actors16.toArray(new Actor[0]));

      argOption = (AbstractArgumentOption) timedtrigger.getOptionManager().findByProperty("prefix");
      timedtrigger.setPrefix((String) argOption.valueOf("CPU"));
      argOption = (AbstractArgumentOption) timedtrigger.getOptionManager().findByProperty("callableName");
      timedtrigger.setCallableName((CallableActorReference) argOption.valueOf("timing_overall"));
    }
    actors.add(timedtrigger);

    // Flow.write
    TimedTrigger timedtrigger3 = new TimedTrigger();
    {
      argOption = (AbstractArgumentOption) timedtrigger3.getOptionManager().findByProperty("name");
      timedtrigger3.setName((String) argOption.valueOf("write"));
      List<Actor> actors20 = new ArrayList<>();

      // Flow.write.RandomNumberGenerator
      RandomNumberGenerator randomnumbergenerator = new RandomNumberGenerator();
      JavaRandomInt javarandomint = new JavaRandomInt();
      randomnumbergenerator.setGenerator(javarandomint);

      argOption = (AbstractArgumentOption) randomnumbergenerator.getOptionManager().findByProperty("maxNum");
      argOption.setVariable("@{num_files}");
      actors20.add(randomnumbergenerator);

      // Flow.write.Convert
      Convert convert = new Convert();
      MultiConversion multiconversion = new MultiConversion();
      List<Conversion> subconversions = new ArrayList<>();
      NumberToDouble numbertodouble = new NumberToDouble();
      subconversions.add(numbertodouble);
      DoubleToString doubletostring = new DoubleToString();
      argOption = (AbstractArgumentOption) doubletostring.getOptionManager().findByProperty("numDecimals");
      doubletostring.setNumDecimals((Integer) argOption.valueOf("0"));
      doubletostring.setFixedDecimals(true);

      subconversions.add(doubletostring);
      multiconversion.setSubConversions(subconversions.toArray(new Conversion[0]));

      convert.setConversion(multiconversion);

      actors20.add(convert);

      // Flow.write.set seed
      SetVariable setvariable18 = new SetVariable();
      argOption = (AbstractArgumentOption) setvariable18.getOptionManager().findByProperty("name");
      setvariable18.setName((String) argOption.valueOf("set seed"));
      argOption = (AbstractArgumentOption) setvariable18.getOptionManager().findByProperty("variableName");
      setvariable18.setVariableName((VariableName) argOption.valueOf("seed"));
      actors20.add(setvariable18);

      // Flow.write.delete file
      Trigger trigger5 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("name");
        trigger5.setName((String) argOption.valueOf("delete file"));
        List<Actor> actors21 = new ArrayList<>();

        // Flow.write.delete file.Variable
        Variable variable = new Variable();
        argOption = (AbstractArgumentOption) variable.getOptionManager().findByProperty("variableName");
        variable.setVariableName((VariableName) argOption.valueOf("outfile"));
        StringToString stringtostring2 = new StringToString();
        variable.setConversion(stringtostring2);

        actors21.add(variable);

        // Flow.write.delete file.DeleteFile
        DeleteFile deletefile = new DeleteFile();
        actors21.add(deletefile);
        trigger5.setActors(actors21.toArray(new Actor[0]));

      }
      actors20.add(trigger5);

      // Flow.write.generate random array
      Trigger trigger6 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
        trigger6.setName((String) argOption.valueOf("generate random array"));
        List<Actor> actors22 = new ArrayList<>();

        // Flow.write.generate random array.RandomNumberGenerator
        RandomNumberGenerator randomnumbergenerator2 = new RandomNumberGenerator();
        JavaRandomDouble javarandomdouble = new JavaRandomDouble();
        argOption = (AbstractArgumentOption) javarandomdouble.getOptionManager().findByProperty("seed");
        argOption.setVariable("@{seed}");
        randomnumbergenerator2.setGenerator(javarandomdouble);

        argOption = (AbstractArgumentOption) randomnumbergenerator2.getOptionManager().findByProperty("maxNum");
        argOption.setVariable("@{num_rand}");
        actors22.add(randomnumbergenerator2);

        // Flow.write.generate random array.SequenceToArray
        SequenceToArray sequencetoarray = new SequenceToArray();
        argOption = (AbstractArgumentOption) sequencetoarray.getOptionManager().findByProperty("arrayLength");
        argOption.setVariable("@{num_rand}");
        actors22.add(sequencetoarray);

        // Flow.write.generate random array.ArrayProcess
        ArrayProcess arrayprocess = new ArrayProcess();
        {
          List<Actor> actors23 = new ArrayList<>();

          // Flow.write.generate random array.ArrayProcess.Convert
          Convert convert2 = new Convert();
          DoubleToString doubletostring2 = new DoubleToString();
          argOption = (AbstractArgumentOption) doubletostring2.getOptionManager().findByProperty("numDecimals");
          doubletostring2.setNumDecimals((Integer) argOption.valueOf("6"));
          doubletostring2.setFixedDecimals(true);

          convert2.setConversion(doubletostring2);

          actors23.add(convert2);
          arrayprocess.setActors(actors23.toArray(new Actor[0]));

        }
        actors22.add(arrayprocess);

        // Flow.write.generate random array.TimedTee
        TimedTee timedtee = new TimedTee();
        {
          List<Actor> actors24 = new ArrayList<>();

          // Flow.write.generate random array.TimedTee.IfThenElse
          IfThenElse ifthenelse = new IfThenElse();
          {
            Expression expression6 = new Expression();
            argOption = (AbstractArgumentOption) expression6.getOptionManager().findByProperty("expression");
            expression6.setExpression((BooleanExpressionText) argOption.valueOf("(@{combine_numbers})"));
            ifthenelse.setCondition(expression6);


            // Flow.write.generate random array.TimedTee.IfThenElse.then
            Sequence sequence4 = new Sequence();
            {
              argOption = (AbstractArgumentOption) sequence4.getOptionManager().findByProperty("name");
              sequence4.setName((String) argOption.valueOf("then"));
              List<Actor> actors25 = new ArrayList<>();

              // Flow.write.generate random array.TimedTee.IfThenElse.then.StringJoin
              StringJoin stringjoin2 = new StringJoin();
              argOption = (AbstractArgumentOption) stringjoin2.getOptionManager().findByProperty("glue");
              stringjoin2.setGlue((String) argOption.valueOf("\n"));
              actors25.add(stringjoin2);

              // Flow.write.generate random array.TimedTee.IfThenElse.then.DumpFile
              DumpFile dumpfile = new DumpFile();
              argOption = (AbstractArgumentOption) dumpfile.getOptionManager().findByProperty("outputFile");
              dumpfile.setOutputFile((PlaceholderFile) argOption.valueOf("${HOME}/temp/rand.txt"));
              dumpfile.setAppend(true);

              actors25.add(dumpfile);
              sequence4.setActors(actors25.toArray(new Actor[0]));

            }
            ifthenelse.setThenActor(sequence4);


            // Flow.write.generate random array.TimedTee.IfThenElse.else
            Sequence sequence5 = new Sequence();
            {
              argOption = (AbstractArgumentOption) sequence5.getOptionManager().findByProperty("name");
              sequence5.setName((String) argOption.valueOf("else"));
              List<Actor> actors26 = new ArrayList<>();

              // Flow.write.generate random array.TimedTee.IfThenElse.else.ArrayToSequence
              ArrayToSequence arraytosequence = new ArrayToSequence();
              actors26.add(arraytosequence);

              // Flow.write.generate random array.TimedTee.IfThenElse.else.DumpFile
              DumpFile dumpfile2 = new DumpFile();
              argOption = (AbstractArgumentOption) dumpfile2.getOptionManager().findByProperty("outputFile");
              dumpfile2.setOutputFile((PlaceholderFile) argOption.valueOf("${HOME}/temp/rand.txt"));
              dumpfile2.setAppend(true);

              actors26.add(dumpfile2);
              sequence5.setActors(actors26.toArray(new Actor[0]));

            }
            ifthenelse.setElseActor(sequence5);

          }
          actors24.add(ifthenelse);
          timedtee.setActors(actors24.toArray(new Actor[0]));

          argOption = (AbstractArgumentOption) timedtee.getOptionManager().findByProperty("callableName");
          timedtee.setCallableName((CallableActorReference) argOption.valueOf("timing_write"));
        }
        actors22.add(timedtee);
        trigger6.setActors(actors22.toArray(new Actor[0]));

      }
      actors20.add(trigger6);
      timedtrigger3.setActors(actors20.toArray(new Actor[0]));

      argOption = (AbstractArgumentOption) timedtrigger3.getOptionManager().findByProperty("prefix");
      timedtrigger3.setPrefix((String) argOption.valueOf("Write"));
      argOption = (AbstractArgumentOption) timedtrigger3.getOptionManager().findByProperty("callableName");
      timedtrigger3.setCallableName((CallableActorReference) argOption.valueOf("timing_overall"));
    }
    actors.add(timedtrigger3);
    actor.setActors(actors.toArray(new Actor[0]));

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("errorHandling");
    actor.setErrorHandling((ErrorHandling) argOption.valueOf("ACTORS_DECIDE_TO_STOP_ON_ERROR"));
    NullListener nulllistener = new NullListener();
    actor.setFlowExecutionListener(nulllistener);

    return actor;
  }
}