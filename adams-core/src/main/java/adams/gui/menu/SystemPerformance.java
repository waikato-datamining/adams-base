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
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.flow.control.Flow;
import adams.flow.control.Flow.ErrorHandling;
import adams.flow.core.Actor;
import adams.flow.source.valuedefinition.ValueDefinition;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;

/**
 * Tests the System performance.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
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
	getOwner(), "Failed to test system performance:\n" + Utils.throwableToString(e));
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
    actor.setErrorHandling(ErrorHandling.ACTORS_DECIDE_TO_STOP_ON_ERROR);

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("annotations");
    actor.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("Uses flow components to give an overview of the system\'s performance."));
    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors2 = new adams.flow.core.Actor[8];

    // Flow.System performance
    adams.flow.standalone.GridView gridview3 = new adams.flow.standalone.GridView();
    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("name");
    gridview3.setName((java.lang.String) argOption.valueOf("System performance"));
    gridview3.setShortTitle(true);

    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("x");
    gridview3.setX((Integer) argOption.valueOf("-2"));
    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("y");
    gridview3.setY((Integer) argOption.valueOf("-2"));
    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors7 = new adams.flow.core.Actor[3];

    //
    adams.flow.sink.SequencePlotter sequenceplotter8 = new adams.flow.sink.SequencePlotter();
    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("name");
    sequenceplotter8.setName((java.lang.String) argOption.valueOf("CPU"));
    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter11 = new adams.gui.print.NullWriter();
    sequenceplotter8.setWriter(nullwriter11);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.CirclePaintlet circlepaintlet13 = new adams.gui.visualization.sequence.CirclePaintlet();
    sequenceplotter8.setPaintlet(circlepaintlet13);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.LOWESSOverlayPaintlet lowessoverlaypaintlet15 = new adams.gui.visualization.sequence.LOWESSOverlayPaintlet();
    argOption = (AbstractArgumentOption) lowessoverlaypaintlet15.getOptionManager().findByProperty("color");
    lowessoverlaypaintlet15.setColor((java.awt.Color) argOption.valueOf("#ff0000"));
    argOption = (AbstractArgumentOption) lowessoverlaypaintlet15.getOptionManager().findByProperty("window");
    lowessoverlaypaintlet15.setWindow((Integer) argOption.valueOf("50"));
    sequenceplotter8.setOverlayPaintlet(lowessoverlaypaintlet15);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("markerPaintlet");
    adams.flow.sink.sequenceplotter.NoMarkers nomarkers19 = new adams.flow.sink.sequenceplotter.NoMarkers();
    sequenceplotter8.setMarkerPaintlet(nomarkers19);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("errorPaintlet");
    adams.flow.sink.sequenceplotter.NoErrorPaintlet noerrorpaintlet21 = new adams.flow.sink.sequenceplotter.NoErrorPaintlet();
    sequenceplotter8.setErrorPaintlet(noerrorpaintlet21);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction23 = new adams.flow.sink.sequenceplotter.NullClickAction();
    sequenceplotter8.setMouseClickAction(nullclickaction23);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider25 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter8.setColorProvider(defaultcolorprovider25);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("overlayColorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider27 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter8.setOverlayColorProvider(defaultcolorprovider27);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("title");
    sequenceplotter8.setTitle((java.lang.String) argOption.valueOf("CPU"));
    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions30 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions30.getOptionManager().findByProperty("label");
    axispaneloptions30.setLabel((java.lang.String) argOption.valueOf("evaluations"));
    argOption = (AbstractArgumentOption) axispaneloptions30.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator33 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions30.setTickGenerator(fancytickgenerator33);

    argOption = (AbstractArgumentOption) axispaneloptions30.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions30.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions30.getOptionManager().findByProperty("width");
    axispaneloptions30.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions30.getOptionManager().findByProperty("customFormat");
    axispaneloptions30.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter8.setAxisX(axispaneloptions30);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions38 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("label");
    axispaneloptions38.setLabel((java.lang.String) argOption.valueOf("msec"));
    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator41 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions38.setTickGenerator(fancytickgenerator41);

    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions38.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("width");
    axispaneloptions38.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("topMargin");
    axispaneloptions38.setTopMargin((Double) argOption.valueOf("0.05"));
    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("bottomMargin");
    axispaneloptions38.setBottomMargin((Double) argOption.valueOf("0.05"));
    argOption = (AbstractArgumentOption) axispaneloptions38.getOptionManager().findByProperty("customFormat");
    axispaneloptions38.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter8.setAxisY(axispaneloptions38);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("plotUpdater");
    adams.flow.sink.sequenceplotter.SimplePlotUpdater simpleplotupdater48 = new adams.flow.sink.sequenceplotter.SimplePlotUpdater();
    argOption = (AbstractArgumentOption) simpleplotupdater48.getOptionManager().findByProperty("updateInterval");
    argOption.setVariable("@{cpu_plot_update}");
    sequenceplotter8.setPlotUpdater(simpleplotupdater48);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("postProcessor");
    adams.flow.sink.sequenceplotter.PassThrough passthrough50 = new adams.flow.sink.sequenceplotter.PassThrough();
    sequenceplotter8.setPostProcessor(passthrough50);

    actors7[0] = sequenceplotter8;

    //
    adams.flow.sink.SequencePlotter sequenceplotter51 = new adams.flow.sink.SequencePlotter();
    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("name");
    sequenceplotter51.setName((java.lang.String) argOption.valueOf("Write"));
    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter54 = new adams.gui.print.NullWriter();
    sequenceplotter51.setWriter(nullwriter54);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.CirclePaintlet circlepaintlet56 = new adams.gui.visualization.sequence.CirclePaintlet();
    sequenceplotter51.setPaintlet(circlepaintlet56);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.LOWESSOverlayPaintlet lowessoverlaypaintlet58 = new adams.gui.visualization.sequence.LOWESSOverlayPaintlet();
    argOption = (AbstractArgumentOption) lowessoverlaypaintlet58.getOptionManager().findByProperty("color");
    lowessoverlaypaintlet58.setColor((java.awt.Color) argOption.valueOf("#ff0000"));
    argOption = (AbstractArgumentOption) lowessoverlaypaintlet58.getOptionManager().findByProperty("window");
    lowessoverlaypaintlet58.setWindow((Integer) argOption.valueOf("50"));
    sequenceplotter51.setOverlayPaintlet(lowessoverlaypaintlet58);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("markerPaintlet");
    adams.flow.sink.sequenceplotter.NoMarkers nomarkers62 = new adams.flow.sink.sequenceplotter.NoMarkers();
    sequenceplotter51.setMarkerPaintlet(nomarkers62);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("errorPaintlet");
    adams.flow.sink.sequenceplotter.NoErrorPaintlet noerrorpaintlet64 = new adams.flow.sink.sequenceplotter.NoErrorPaintlet();
    sequenceplotter51.setErrorPaintlet(noerrorpaintlet64);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction66 = new adams.flow.sink.sequenceplotter.NullClickAction();
    sequenceplotter51.setMouseClickAction(nullclickaction66);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider68 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter51.setColorProvider(defaultcolorprovider68);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("overlayColorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider70 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter51.setOverlayColorProvider(defaultcolorprovider70);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("title");
    sequenceplotter51.setTitle((java.lang.String) argOption.valueOf("Write"));
    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions73 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions73.getOptionManager().findByProperty("label");
    axispaneloptions73.setLabel((java.lang.String) argOption.valueOf("evaluations"));
    argOption = (AbstractArgumentOption) axispaneloptions73.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator76 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions73.setTickGenerator(fancytickgenerator76);

    argOption = (AbstractArgumentOption) axispaneloptions73.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions73.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions73.getOptionManager().findByProperty("width");
    axispaneloptions73.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions73.getOptionManager().findByProperty("customFormat");
    axispaneloptions73.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter51.setAxisX(axispaneloptions73);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions81 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("label");
    axispaneloptions81.setLabel((java.lang.String) argOption.valueOf("msec"));
    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator84 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions81.setTickGenerator(fancytickgenerator84);

    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions81.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("width");
    axispaneloptions81.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("topMargin");
    axispaneloptions81.setTopMargin((Double) argOption.valueOf("0.05"));
    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("bottomMargin");
    axispaneloptions81.setBottomMargin((Double) argOption.valueOf("0.05"));
    argOption = (AbstractArgumentOption) axispaneloptions81.getOptionManager().findByProperty("customFormat");
    axispaneloptions81.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter51.setAxisY(axispaneloptions81);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("plotUpdater");
    adams.flow.sink.sequenceplotter.SimplePlotUpdater simpleplotupdater91 = new adams.flow.sink.sequenceplotter.SimplePlotUpdater();
    argOption = (AbstractArgumentOption) simpleplotupdater91.getOptionManager().findByProperty("updateInterval");
    argOption.setVariable("@{write_plot_update}");
    sequenceplotter51.setPlotUpdater(simpleplotupdater91);

    argOption = (AbstractArgumentOption) sequenceplotter51.getOptionManager().findByProperty("postProcessor");
    adams.flow.sink.sequenceplotter.PassThrough passthrough93 = new adams.flow.sink.sequenceplotter.PassThrough();
    sequenceplotter51.setPostProcessor(passthrough93);

    actors7[1] = sequenceplotter51;

    //
    adams.flow.sink.Display display94 = new adams.flow.sink.Display();
    argOption = (AbstractArgumentOption) display94.getOptionManager().findByProperty("name");
    display94.setName((java.lang.String) argOption.valueOf("Overall"));
    argOption = (AbstractArgumentOption) display94.getOptionManager().findByProperty("writer");
    adams.data.io.output.NullWriter nullwriter97 = new adams.data.io.output.NullWriter();
    display94.setWriter(nullwriter97);

    actors7[2] = display94;
    gridview3.setActors(actors7);

    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("numRows");
    gridview3.setNumRows((Integer) argOption.valueOf("3"));
    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter100 = new adams.gui.print.NullWriter();
    gridview3.setWriter(nullwriter100);

    actors2[0] = gridview3;

    // Flow.CallableActors
    adams.flow.standalone.CallableActors callableactors101 = new adams.flow.standalone.CallableActors();
    argOption = (AbstractArgumentOption) callableactors101.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors102 = new adams.flow.core.Actor[3];

    // Flow.CallableActors.timing_write
    adams.flow.control.Sequence sequence103 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence103.getOptionManager().findByProperty("name");
    sequence103.setName((java.lang.String) argOption.valueOf("timing_write"));
    argOption = (AbstractArgumentOption) sequence103.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors105 = new adams.flow.core.Actor[3];

    // Flow.CallableActors.timing_write.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker106 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker106.getOptionManager().findByProperty("valueName");
    containervaluepicker106.setValueName((java.lang.String) argOption.valueOf("msec"));
    containervaluepicker106.setSwitchOutputs(true);

    actors105[0] = containervaluepicker106;

    // Flow.CallableActors.timing_write.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer108 = new adams.flow.transformer.MakePlotContainer();
    argOption = (AbstractArgumentOption) makeplotcontainer108.getOptionManager().findByProperty("plotName");
    makeplotcontainer108.setPlotName((java.lang.String) argOption.valueOf("Write speed"));
    actors105[1] = makeplotcontainer108;

    // Flow.CallableActors.timing_write.CallableSink
    adams.flow.sink.CallableSink callablesink110 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink110.getOptionManager().findByProperty("callableName");
    callablesink110.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Write"));
    actors105[2] = callablesink110;
    sequence103.setActors(actors105);

    actors102[0] = sequence103;

    // Flow.CallableActors.timing_cpuspeed
    adams.flow.control.Sequence sequence112 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence112.getOptionManager().findByProperty("name");
    sequence112.setName((java.lang.String) argOption.valueOf("timing_cpuspeed"));
    argOption = (AbstractArgumentOption) sequence112.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors114 = new adams.flow.core.Actor[3];

    // Flow.CallableActors.timing_cpuspeed.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker115 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker115.getOptionManager().findByProperty("valueName");
    containervaluepicker115.setValueName((java.lang.String) argOption.valueOf("msec"));
    containervaluepicker115.setSwitchOutputs(true);

    actors114[0] = containervaluepicker115;

    // Flow.CallableActors.timing_cpuspeed.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer117 = new adams.flow.transformer.MakePlotContainer();
    argOption = (AbstractArgumentOption) makeplotcontainer117.getOptionManager().findByProperty("plotName");
    makeplotcontainer117.setPlotName((java.lang.String) argOption.valueOf("CPU Speed"));
    actors114[1] = makeplotcontainer117;

    // Flow.CallableActors.timing_cpuspeed.CallableSink
    adams.flow.sink.CallableSink callablesink119 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink119.getOptionManager().findByProperty("callableName");
    callablesink119.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("CPU"));
    actors114[2] = callablesink119;
    sequence112.setActors(actors114);

    actors102[1] = sequence112;

    // Flow.CallableActors.timing_overall
    adams.flow.control.Sequence sequence121 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence121.getOptionManager().findByProperty("name");
    sequence121.setName((java.lang.String) argOption.valueOf("timing_overall"));
    argOption = (AbstractArgumentOption) sequence121.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors123 = new adams.flow.core.Actor[3];

    // Flow.CallableActors.timing_overall.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker124 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker124.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors125 = new adams.flow.core.Actor[1];

    // Flow.CallableActors.timing_overall.ContainerValuePicker.SetVariable
    adams.flow.transformer.SetVariable setvariable126 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable126.getOptionManager().findByProperty("variableName");
    setvariable126.setVariableName((adams.core.VariableName) argOption.valueOf("msec"));
    actors125[0] = setvariable126;
    containervaluepicker124.setActors(actors125);

    argOption = (AbstractArgumentOption) containervaluepicker124.getOptionManager().findByProperty("valueName");
    containervaluepicker124.setValueName((java.lang.String) argOption.valueOf("msec"));
    actors123[0] = containervaluepicker124;

    // Flow.CallableActors.timing_overall.ContainerValuePicker-1
    adams.flow.control.ContainerValuePicker containervaluepicker129 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker129.getOptionManager().findByProperty("name");
    containervaluepicker129.setName((java.lang.String) argOption.valueOf("ContainerValuePicker-1"));
    argOption = (AbstractArgumentOption) containervaluepicker129.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors131 = new adams.flow.core.Actor[1];

    // Flow.CallableActors.timing_overall.ContainerValuePicker-1.SetVariable
    adams.flow.transformer.SetVariable setvariable132 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable132.getOptionManager().findByProperty("variableName");
    setvariable132.setVariableName((adams.core.VariableName) argOption.valueOf("prefix"));
    actors131[0] = setvariable132;
    containervaluepicker129.setActors(actors131);

    argOption = (AbstractArgumentOption) containervaluepicker129.getOptionManager().findByProperty("valueName");
    containervaluepicker129.setValueName((java.lang.String) argOption.valueOf("Prefix"));
    actors123[1] = containervaluepicker129;

    // Flow.CallableActors.timing_overall.generate output
    adams.flow.control.Trigger trigger135 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger135.getOptionManager().findByProperty("name");
    trigger135.setName((java.lang.String) argOption.valueOf("generate output"));
    argOption = (AbstractArgumentOption) trigger135.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors137 = new adams.flow.core.Actor[2];

    // Flow.CallableActors.timing_overall.generate output.CombineVariables
    adams.flow.source.CombineVariables combinevariables138 = new adams.flow.source.CombineVariables();
    argOption = (AbstractArgumentOption) combinevariables138.getOptionManager().findByProperty("expression");
    combinevariables138.setExpression((adams.core.base.BaseText) argOption.valueOf("Overall @{prefix}: @{msec} msec"));
    actors137[0] = combinevariables138;

    // Flow.CallableActors.timing_overall.generate output.CallableSink
    adams.flow.sink.CallableSink callablesink140 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink140.getOptionManager().findByProperty("callableName");
    callablesink140.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Overall"));
    actors137[1] = callablesink140;
    trigger135.setActors(actors137);

    actors123[2] = trigger135;
    sequence121.setActors(actors123);

    actors102[2] = sequence121;
    callableactors101.setActors(actors102);

    actors2[1] = callableactors101;

    // Flow.speed variables
    adams.flow.standalone.Standalones standalones142 = new adams.flow.standalone.Standalones();
    argOption = (AbstractArgumentOption) standalones142.getOptionManager().findByProperty("name");
    standalones142.setName((java.lang.String) argOption.valueOf("speed variables"));
    argOption = (AbstractArgumentOption) standalones142.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors144 = new adams.flow.core.Actor[5];

    // Flow.speed variables.# curves
    adams.flow.standalone.SetVariable setvariable145 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable145.getOptionManager().findByProperty("name");
    setvariable145.setName((java.lang.String) argOption.valueOf("# curves"));
    argOption = (AbstractArgumentOption) setvariable145.getOptionManager().findByProperty("variableName");
    setvariable145.setVariableName((adams.core.VariableName) argOption.valueOf("num_curves"));
    argOption = (AbstractArgumentOption) setvariable145.getOptionManager().findByProperty("variableValue");
    setvariable145.setVariableValue((adams.core.base.BaseText) argOption.valueOf("500"));
    actors144[0] = setvariable145;

    // Flow.speed variables.# means
    adams.flow.standalone.SetVariable setvariable149 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable149.getOptionManager().findByProperty("name");
    setvariable149.setName((java.lang.String) argOption.valueOf("# means"));
    argOption = (AbstractArgumentOption) setvariable149.getOptionManager().findByProperty("variableName");
    setvariable149.setVariableName((adams.core.VariableName) argOption.valueOf("num_means"));
    argOption = (AbstractArgumentOption) setvariable149.getOptionManager().findByProperty("variableValue");
    setvariable149.setVariableValue((adams.core.base.BaseText) argOption.valueOf("5"));
    actors144[1] = setvariable149;

    // Flow.speed variables.# stdevs
    adams.flow.standalone.SetVariable setvariable153 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable153.getOptionManager().findByProperty("name");
    setvariable153.setName((java.lang.String) argOption.valueOf("# stdevs"));
    argOption = (AbstractArgumentOption) setvariable153.getOptionManager().findByProperty("variableName");
    setvariable153.setVariableName((adams.core.VariableName) argOption.valueOf("num_stdevs"));
    argOption = (AbstractArgumentOption) setvariable153.getOptionManager().findByProperty("variableValue");
    setvariable153.setVariableValue((adams.core.base.BaseText) argOption.valueOf("5"));
    actors144[2] = setvariable153;

    // Flow.speed variables.# data points
    adams.flow.standalone.SetVariable setvariable157 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable157.getOptionManager().findByProperty("name");
    setvariable157.setName((java.lang.String) argOption.valueOf("# data points"));
    argOption = (AbstractArgumentOption) setvariable157.getOptionManager().findByProperty("variableName");
    setvariable157.setVariableName((adams.core.VariableName) argOption.valueOf("num_points"));
    argOption = (AbstractArgumentOption) setvariable157.getOptionManager().findByProperty("variableValue");
    setvariable157.setVariableValue((adams.core.base.BaseText) argOption.valueOf("200"));
    actors144[3] = setvariable157;

    // Flow.speed variables.cpu plot update interval
    adams.flow.standalone.SetVariable setvariable161 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable161.getOptionManager().findByProperty("name");
    setvariable161.setName((java.lang.String) argOption.valueOf("cpu plot update interval"));
    argOption = (AbstractArgumentOption) setvariable161.getOptionManager().findByProperty("variableName");
    setvariable161.setVariableName((adams.core.VariableName) argOption.valueOf("cpu_plot_update"));
    argOption = (AbstractArgumentOption) setvariable161.getOptionManager().findByProperty("variableValue");
    setvariable161.setVariableValue((adams.core.base.BaseText) argOption.valueOf("100"));
    actors144[4] = setvariable161;
    standalones142.setActors(actors144);

    actors2[2] = standalones142;

    // Flow.write variables
    adams.flow.standalone.Standalones standalones165 = new adams.flow.standalone.Standalones();
    argOption = (AbstractArgumentOption) standalones165.getOptionManager().findByProperty("name");
    standalones165.setName((java.lang.String) argOption.valueOf("write variables"));
    argOption = (AbstractArgumentOption) standalones165.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors167 = new adams.flow.core.Actor[5];

    // Flow.write variables.number of files
    adams.flow.standalone.SetVariable setvariable168 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable168.getOptionManager().findByProperty("name");
    setvariable168.setName((java.lang.String) argOption.valueOf("number of files"));
    argOption = (AbstractArgumentOption) setvariable168.getOptionManager().findByProperty("variableName");
    setvariable168.setVariableName((adams.core.VariableName) argOption.valueOf("num_files"));
    argOption = (AbstractArgumentOption) setvariable168.getOptionManager().findByProperty("variableValue");
    setvariable168.setVariableValue((adams.core.base.BaseText) argOption.valueOf("500"));
    actors167[0] = setvariable168;

    // Flow.write variables.number of numbers per file
    adams.flow.standalone.SetVariable setvariable172 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable172.getOptionManager().findByProperty("name");
    setvariable172.setName((java.lang.String) argOption.valueOf("number of numbers per file"));
    argOption = (AbstractArgumentOption) setvariable172.getOptionManager().findByProperty("variableName");
    setvariable172.setVariableName((adams.core.VariableName) argOption.valueOf("num_rand"));
    argOption = (AbstractArgumentOption) setvariable172.getOptionManager().findByProperty("variableValue");
    setvariable172.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors167[1] = setvariable172;

    // Flow.write variables.combine numbers
    adams.flow.standalone.SetVariable setvariable176 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable176.getOptionManager().findByProperty("name");
    setvariable176.setName((java.lang.String) argOption.valueOf("combine numbers"));
    argOption = (AbstractArgumentOption) setvariable176.getOptionManager().findByProperty("variableName");
    setvariable176.setVariableName((adams.core.VariableName) argOption.valueOf("combine_numbers"));
    argOption = (AbstractArgumentOption) setvariable176.getOptionManager().findByProperty("variableValue");
    setvariable176.setVariableValue((adams.core.base.BaseText) argOption.valueOf("true"));
    actors167[2] = setvariable176;

    // Flow.write variables.output file
    adams.flow.standalone.SetVariable setvariable180 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable180.getOptionManager().findByProperty("name");
    setvariable180.setName((java.lang.String) argOption.valueOf("output file"));
    argOption = (AbstractArgumentOption) setvariable180.getOptionManager().findByProperty("variableName");
    setvariable180.setVariableName((adams.core.VariableName) argOption.valueOf("outfile"));
    argOption = (AbstractArgumentOption) setvariable180.getOptionManager().findByProperty("variableValue");
    setvariable180.setVariableValue((adams.core.base.BaseText) argOption.valueOf("${TMP}/rand.txt"));
    actors167[3] = setvariable180;

    // Flow.write variables.write plot update interval
    adams.flow.standalone.SetVariable setvariable184 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable184.getOptionManager().findByProperty("name");
    setvariable184.setName((java.lang.String) argOption.valueOf("write plot update interval"));
    argOption = (AbstractArgumentOption) setvariable184.getOptionManager().findByProperty("variableName");
    setvariable184.setVariableName((adams.core.VariableName) argOption.valueOf("write_plot_update"));
    argOption = (AbstractArgumentOption) setvariable184.getOptionManager().findByProperty("variableValue");
    setvariable184.setVariableValue((adams.core.base.BaseText) argOption.valueOf("100"));
    actors167[4] = setvariable184;
    standalones165.setActors(actors167);

    actors2[3] = standalones165;

    // Flow.Start
    adams.flow.source.Start start188 = new adams.flow.source.Start();
    actors2[4] = start188;

    // Flow.prompt user
    adams.flow.control.Trigger trigger189 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger189.getOptionManager().findByProperty("name");
    trigger189.setName((java.lang.String) argOption.valueOf("prompt user"));
    argOption = (AbstractArgumentOption) trigger189.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors191 = new adams.flow.core.Actor[4];

    // Flow.prompt user.Enter parameters
    adams.flow.source.EnterManyValues entermanyvalues192 = new adams.flow.source.EnterManyValues();
    argOption = (AbstractArgumentOption) entermanyvalues192.getOptionManager().findByProperty("name");
    entermanyvalues192.setName((java.lang.String) argOption.valueOf("Enter parameters"));
    entermanyvalues192.setStopFlowIfCanceled(true);

    argOption = (AbstractArgumentOption) entermanyvalues192.getOptionManager().findByProperty("message");
    entermanyvalues192.setMessage((java.lang.String) argOption.valueOf("Please enter the performance test parameters"));
    argOption = (AbstractArgumentOption) entermanyvalues192.getOptionManager().findByProperty("values");
    ValueDefinition[] values195 = new ValueDefinition[10];
    ValueDefinition valuedefinition196 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition196.getOptionManager().findByProperty("name");
    valuedefinition196.setName((java.lang.String) argOption.valueOf("num_curves"));
    argOption = (AbstractArgumentOption) valuedefinition196.getOptionManager().findByProperty("display");
    valuedefinition196.setDisplay((java.lang.String) argOption.valueOf("CPU: # curves"));
    argOption = (AbstractArgumentOption) valuedefinition196.getOptionManager().findByProperty("help");
    valuedefinition196.setHelp((java.lang.String) argOption.valueOf("The number of bell curves to calculate"));
    argOption = (AbstractArgumentOption) valuedefinition196.getOptionManager().findByProperty("type");
    valuedefinition196.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition196.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{num_curves}");
    values195[0] = valuedefinition196;
    ValueDefinition valuedefinition201 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition201.getOptionManager().findByProperty("name");
    valuedefinition201.setName((java.lang.String) argOption.valueOf("num_means"));
    argOption = (AbstractArgumentOption) valuedefinition201.getOptionManager().findByProperty("display");
    valuedefinition201.setDisplay((java.lang.String) argOption.valueOf("CPU: # means"));
    argOption = (AbstractArgumentOption) valuedefinition201.getOptionManager().findByProperty("help");
    valuedefinition201.setHelp((java.lang.String) argOption.valueOf("The number of different bell curve means to use in each iteration"));
    argOption = (AbstractArgumentOption) valuedefinition201.getOptionManager().findByProperty("type");
    valuedefinition201.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition201.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{num_means}");
    values195[1] = valuedefinition201;
    ValueDefinition valuedefinition206 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition206.getOptionManager().findByProperty("name");
    valuedefinition206.setName((java.lang.String) argOption.valueOf("num_stdevs"));
    argOption = (AbstractArgumentOption) valuedefinition206.getOptionManager().findByProperty("display");
    valuedefinition206.setDisplay((java.lang.String) argOption.valueOf("CPU: # standard deviations"));
    argOption = (AbstractArgumentOption) valuedefinition206.getOptionManager().findByProperty("help");
    valuedefinition206.setHelp((java.lang.String) argOption.valueOf("The number of different bell curve standard deviations to use in each iteration"));
    argOption = (AbstractArgumentOption) valuedefinition206.getOptionManager().findByProperty("type");
    valuedefinition206.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition206.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{num_stdevs}");
    values195[2] = valuedefinition206;
    ValueDefinition valuedefinition211 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition211.getOptionManager().findByProperty("name");
    valuedefinition211.setName((java.lang.String) argOption.valueOf("num_points"));
    argOption = (AbstractArgumentOption) valuedefinition211.getOptionManager().findByProperty("display");
    valuedefinition211.setDisplay((java.lang.String) argOption.valueOf("CPU: # data points"));
    argOption = (AbstractArgumentOption) valuedefinition211.getOptionManager().findByProperty("help");
    valuedefinition211.setHelp((java.lang.String) argOption.valueOf("The number of data points to calculate per bell curve"));
    argOption = (AbstractArgumentOption) valuedefinition211.getOptionManager().findByProperty("type");
    valuedefinition211.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition211.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{num_points}");
    values195[3] = valuedefinition211;
    ValueDefinition valuedefinition216 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition216.getOptionManager().findByProperty("name");
    valuedefinition216.setName((java.lang.String) argOption.valueOf("cpu_plot_update"));
    argOption = (AbstractArgumentOption) valuedefinition216.getOptionManager().findByProperty("display");
    valuedefinition216.setDisplay((java.lang.String) argOption.valueOf("CPU: plot update interval"));
    argOption = (AbstractArgumentOption) valuedefinition216.getOptionManager().findByProperty("help");
    valuedefinition216.setHelp((java.lang.String) argOption.valueOf("After how many iterations to refresh the plot"));
    argOption = (AbstractArgumentOption) valuedefinition216.getOptionManager().findByProperty("type");
    valuedefinition216.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition216.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{cpu_plot_update}");
    values195[4] = valuedefinition216;
    ValueDefinition valuedefinition221 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition221.getOptionManager().findByProperty("name");
    valuedefinition221.setName((java.lang.String) argOption.valueOf("num_files"));
    argOption = (AbstractArgumentOption) valuedefinition221.getOptionManager().findByProperty("display");
    valuedefinition221.setDisplay((java.lang.String) argOption.valueOf("Write: # files"));
    argOption = (AbstractArgumentOption) valuedefinition221.getOptionManager().findByProperty("help");
    valuedefinition221.setHelp((java.lang.String) argOption.valueOf("The number of files to generate"));
    argOption = (AbstractArgumentOption) valuedefinition221.getOptionManager().findByProperty("type");
    valuedefinition221.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition221.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{num_files}");
    values195[5] = valuedefinition221;
    ValueDefinition valuedefinition226 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition226.getOptionManager().findByProperty("name");
    valuedefinition226.setName((java.lang.String) argOption.valueOf("num_rand"));
    argOption = (AbstractArgumentOption) valuedefinition226.getOptionManager().findByProperty("display");
    valuedefinition226.setDisplay((java.lang.String) argOption.valueOf("Write: # random numbers per file"));
    argOption = (AbstractArgumentOption) valuedefinition226.getOptionManager().findByProperty("help");
    valuedefinition226.setHelp((java.lang.String) argOption.valueOf("How many random numbers to store in a single file"));
    argOption = (AbstractArgumentOption) valuedefinition226.getOptionManager().findByProperty("type");
    valuedefinition226.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition226.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{num_rand}");
    values195[6] = valuedefinition226;
    ValueDefinition valuedefinition231 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition231.getOptionManager().findByProperty("name");
    valuedefinition231.setName((java.lang.String) argOption.valueOf("combine_numbers"));
    argOption = (AbstractArgumentOption) valuedefinition231.getOptionManager().findByProperty("display");
    valuedefinition231.setDisplay((java.lang.String) argOption.valueOf("Write: single write operation per file"));
    argOption = (AbstractArgumentOption) valuedefinition231.getOptionManager().findByProperty("help");
    valuedefinition231.setHelp((java.lang.String) argOption.valueOf("Whether to save one number at a time or all at once"));
    argOption = (AbstractArgumentOption) valuedefinition231.getOptionManager().findByProperty("type");
    valuedefinition231.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("BOOLEAN"));
    argOption = (AbstractArgumentOption) valuedefinition231.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{combine_numbers}");
    values195[7] = valuedefinition231;
    ValueDefinition valuedefinition236 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition236.getOptionManager().findByProperty("name");
    valuedefinition236.setName((java.lang.String) argOption.valueOf("outfile"));
    argOption = (AbstractArgumentOption) valuedefinition236.getOptionManager().findByProperty("display");
    valuedefinition236.setDisplay((java.lang.String) argOption.valueOf("Write: temporary file"));
    argOption = (AbstractArgumentOption) valuedefinition236.getOptionManager().findByProperty("help");
    valuedefinition236.setHelp((java.lang.String) argOption.valueOf("The temporary to use for saving the random numbers"));
    argOption = (AbstractArgumentOption) valuedefinition236.getOptionManager().findByProperty("type");
    valuedefinition236.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("FILE_ABSOLUTE"));
    argOption = (AbstractArgumentOption) valuedefinition236.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{outfile}");
    values195[8] = valuedefinition236;
    ValueDefinition valuedefinition241 = new ValueDefinition();
    argOption = (AbstractArgumentOption) valuedefinition241.getOptionManager().findByProperty("name");
    valuedefinition241.setName((java.lang.String) argOption.valueOf("write_plot_update"));
    argOption = (AbstractArgumentOption) valuedefinition241.getOptionManager().findByProperty("display");
    valuedefinition241.setDisplay((java.lang.String) argOption.valueOf("Write: plot update interval"));
    argOption = (AbstractArgumentOption) valuedefinition241.getOptionManager().findByProperty("help");
    valuedefinition241.setHelp((java.lang.String) argOption.valueOf("After how many iterations to refresh the plot"));
    argOption = (AbstractArgumentOption) valuedefinition241.getOptionManager().findByProperty("type");
    valuedefinition241.setType((adams.gui.core.PropertiesParameterPanel.PropertyType) argOption.valueOf("INTEGER"));
    argOption = (AbstractArgumentOption) valuedefinition241.getOptionManager().findByProperty("defaultValue");
    argOption.setVariable("@{write_plot_update}");
    values195[9] = valuedefinition241;
    entermanyvalues192.setValues(values195);

    argOption = (AbstractArgumentOption) entermanyvalues192.getOptionManager().findByProperty("outputType");
    entermanyvalues192.setOutputType((adams.flow.source.EnterManyValues.OutputType) argOption.valueOf("KEY_VALUE_PAIRS"));
    actors191[0] = entermanyvalues192;

    // Flow.prompt user.output param
    adams.flow.control.Tee tee247 = new adams.flow.control.Tee();
    argOption = (AbstractArgumentOption) tee247.getOptionManager().findByProperty("name");
    tee247.setName((java.lang.String) argOption.valueOf("output param"));
    argOption = (AbstractArgumentOption) tee247.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors249 = new adams.flow.core.Actor[2];

    // Flow.prompt user.output param.StringJoin
    adams.flow.transformer.StringJoin stringjoin250 = new adams.flow.transformer.StringJoin();
    argOption = (AbstractArgumentOption) stringjoin250.getOptionManager().findByProperty("glue");
    stringjoin250.setGlue((java.lang.String) argOption.valueOf(": "));
    actors249[0] = stringjoin250;

    // Flow.prompt user.output param.CallableSink
    adams.flow.sink.CallableSink callablesink252 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink252.getOptionManager().findByProperty("callableName");
    callablesink252.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Overall"));
    actors249[1] = callablesink252;
    tee247.setActors(actors249);

    actors191[1] = tee247;

    // Flow.prompt user.var name
    adams.flow.control.Tee tee254 = new adams.flow.control.Tee();
    argOption = (AbstractArgumentOption) tee254.getOptionManager().findByProperty("name");
    tee254.setName((java.lang.String) argOption.valueOf("var name"));
    argOption = (AbstractArgumentOption) tee254.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors256 = new adams.flow.core.Actor[2];

    // Flow.prompt user.var name.GetArrayElement
    adams.flow.transformer.GetArrayElement getarrayelement257 = new adams.flow.transformer.GetArrayElement();
    actors256[0] = getarrayelement257;

    // Flow.prompt user.var name.SetVariable
    adams.flow.transformer.SetVariable setvariable258 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable258.getOptionManager().findByProperty("variableName");
    setvariable258.setVariableName((adams.core.VariableName) argOption.valueOf("name"));
    actors256[1] = setvariable258;
    tee254.setActors(actors256);

    actors191[2] = tee254;

    // Flow.prompt user.var value
    adams.flow.control.Tee tee260 = new adams.flow.control.Tee();
    argOption = (AbstractArgumentOption) tee260.getOptionManager().findByProperty("name");
    tee260.setName((java.lang.String) argOption.valueOf("var value"));
    argOption = (AbstractArgumentOption) tee260.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors262 = new adams.flow.core.Actor[2];

    // Flow.prompt user.var value.GetArrayElement
    adams.flow.transformer.GetArrayElement getarrayelement263 = new adams.flow.transformer.GetArrayElement();
    argOption = (AbstractArgumentOption) getarrayelement263.getOptionManager().findByProperty("index");
    getarrayelement263.setIndex((adams.core.Index) argOption.valueOf("2"));
    actors262[0] = getarrayelement263;

    // Flow.prompt user.var value.SetVariable
    adams.flow.transformer.SetVariable setvariable265 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable265.getOptionManager().findByProperty("variableName");
    argOption.setVariable("@{name}");
    actors262[1] = setvariable265;
    tee260.setActors(actors262);

    actors191[3] = tee260;
    trigger189.setActors(actors191);

    actors2[5] = trigger189;

    // Flow.speed
    adams.flow.control.TimedTrigger timedtrigger266 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger266.getOptionManager().findByProperty("name");
    timedtrigger266.setName((java.lang.String) argOption.valueOf("speed"));
    argOption = (AbstractArgumentOption) timedtrigger266.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors268 = new adams.flow.core.Actor[2];

    // Flow.speed.ForLoop
    adams.flow.source.ForLoop forloop269 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop269.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_curves}");
    actors268[0] = forloop269;

    // Flow.speed.calc curves
    adams.flow.control.TimedTrigger timedtrigger270 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger270.getOptionManager().findByProperty("name");
    timedtrigger270.setName((java.lang.String) argOption.valueOf("calc curves"));
    argOption = (AbstractArgumentOption) timedtrigger270.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors272 = new adams.flow.core.Actor[4];

    // Flow.speed.calc curves.ForLoop
    adams.flow.source.ForLoop forloop273 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop273.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_means}");
    actors272[0] = forloop273;

    // Flow.speed.calc curves.MathExpression
    adams.flow.transformer.MathExpression mathexpression274 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression274.getOptionManager().findByProperty("expression");
    mathexpression274.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / @{num_means}"));
    actors272[1] = mathexpression274;

    // Flow.speed.calc curves.SetVariable
    adams.flow.transformer.SetVariable setvariable276 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable276.getOptionManager().findByProperty("variableName");
    setvariable276.setVariableName((adams.core.VariableName) argOption.valueOf("mean"));
    actors272[2] = setvariable276;

    // Flow.speed.calc curves.mean
    adams.flow.control.Trigger trigger278 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger278.getOptionManager().findByProperty("name");
    trigger278.setName((java.lang.String) argOption.valueOf("mean"));
    argOption = (AbstractArgumentOption) trigger278.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors280 = new adams.flow.core.Actor[4];

    // Flow.speed.calc curves.mean.ForLoop
    adams.flow.source.ForLoop forloop281 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop281.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_stdevs}");
    actors280[0] = forloop281;

    // Flow.speed.calc curves.mean.MathExpression
    adams.flow.transformer.MathExpression mathexpression282 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression282.getOptionManager().findByProperty("expression");
    mathexpression282.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / @{num_stdevs}"));
    actors280[1] = mathexpression282;

    // Flow.speed.calc curves.mean.SetVariable
    adams.flow.transformer.SetVariable setvariable284 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable284.getOptionManager().findByProperty("variableName");
    setvariable284.setVariableName((adams.core.VariableName) argOption.valueOf("stdev"));
    actors280[2] = setvariable284;

    // Flow.speed.calc curves.mean.stdev
    adams.flow.control.Trigger trigger286 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger286.getOptionManager().findByProperty("name");
    trigger286.setName((java.lang.String) argOption.valueOf("stdev"));
    argOption = (AbstractArgumentOption) trigger286.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors288 = new adams.flow.core.Actor[4];

    // Flow.speed.calc curves.mean.stdev.plot name
    adams.flow.standalone.SetVariable setvariable289 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable289.getOptionManager().findByProperty("name");
    setvariable289.setName((java.lang.String) argOption.valueOf("plot name"));
    argOption = (AbstractArgumentOption) setvariable289.getOptionManager().findByProperty("variableName");
    setvariable289.setVariableName((adams.core.VariableName) argOption.valueOf("plot_name"));
    argOption = (AbstractArgumentOption) setvariable289.getOptionManager().findByProperty("variableValue");
    setvariable289.setVariableValue((adams.core.base.BaseText) argOption.valueOf("@{mean}/@{stdev}"));
    setvariable289.setExpandValue(true);

    actors288[0] = setvariable289;

    // Flow.speed.calc curves.mean.stdev.ForLoop
    adams.flow.source.ForLoop forloop293 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop293.getOptionManager().findByProperty("loopLower");
    forloop293.setLoopLower((Integer) argOption.valueOf("0"));
    argOption = (AbstractArgumentOption) forloop293.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_points}");
    actors288[1] = forloop293;

    // Flow.speed.calc curves.mean.stdev.MathExpression
    adams.flow.transformer.MathExpression mathexpression295 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression295.getOptionManager().findByProperty("expression");
    mathexpression295.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("(X - (@{num_points} / 2)) / 33"));
    actors288[2] = mathexpression295;

    // Flow.speed.calc curves.mean.stdev.MathExpression-1
    adams.flow.transformer.MathExpression mathexpression297 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression297.getOptionManager().findByProperty("name");
    mathexpression297.setName((java.lang.String) argOption.valueOf("MathExpression-1"));
    argOption = (AbstractArgumentOption) mathexpression297.getOptionManager().findByProperty("expression");
    mathexpression297.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
    mathexpression297.setOutputValuePair(true);

    actors288[3] = mathexpression297;
    trigger286.setActors(actors288);

    actors280[3] = trigger286;
    trigger278.setActors(actors280);

    actors272[3] = trigger278;
    timedtrigger270.setActors(actors272);

    argOption = (AbstractArgumentOption) timedtrigger270.getOptionManager().findByProperty("callableName");
    timedtrigger270.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_cpuspeed"));
    actors268[1] = timedtrigger270;
    timedtrigger266.setActors(actors268);

    argOption = (AbstractArgumentOption) timedtrigger266.getOptionManager().findByProperty("prefix");
    timedtrigger266.setPrefix((java.lang.String) argOption.valueOf("CPU"));
    argOption = (AbstractArgumentOption) timedtrigger266.getOptionManager().findByProperty("callableName");
    timedtrigger266.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_overall"));
    actors2[6] = timedtrigger266;

    // Flow.write
    adams.flow.control.TimedTrigger timedtrigger303 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger303.getOptionManager().findByProperty("name");
    timedtrigger303.setName((java.lang.String) argOption.valueOf("write"));
    argOption = (AbstractArgumentOption) timedtrigger303.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors305 = new adams.flow.core.Actor[5];

    // Flow.write.RandomNumberGenerator
    adams.flow.source.RandomNumberGenerator randomnumbergenerator306 = new adams.flow.source.RandomNumberGenerator();
    argOption = (AbstractArgumentOption) randomnumbergenerator306.getOptionManager().findByProperty("generator");
    adams.data.random.JavaRandomInt javarandomint308 = new adams.data.random.JavaRandomInt();
    randomnumbergenerator306.setGenerator(javarandomint308);

    argOption = (AbstractArgumentOption) randomnumbergenerator306.getOptionManager().findByProperty("maxNum");
    argOption.setVariable("@{num_files}");
    actors305[0] = randomnumbergenerator306;

    // Flow.write.Convert
    adams.flow.transformer.Convert convert309 = new adams.flow.transformer.Convert();
    argOption = (AbstractArgumentOption) convert309.getOptionManager().findByProperty("conversion");
    adams.data.conversion.DoubleToString doubletostring311 = new adams.data.conversion.DoubleToString();
    argOption = (AbstractArgumentOption) doubletostring311.getOptionManager().findByProperty("numDecimals");
    doubletostring311.setNumDecimals((Integer) argOption.valueOf("0"));
    doubletostring311.setFixedDecimals(true);

    convert309.setConversion(doubletostring311);

    actors305[1] = convert309;

    // Flow.write.set seed
    adams.flow.transformer.SetVariable setvariable313 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable313.getOptionManager().findByProperty("name");
    setvariable313.setName((java.lang.String) argOption.valueOf("set seed"));
    argOption = (AbstractArgumentOption) setvariable313.getOptionManager().findByProperty("variableName");
    setvariable313.setVariableName((adams.core.VariableName) argOption.valueOf("seed"));
    actors305[2] = setvariable313;

    // Flow.write.delete file
    adams.flow.control.Trigger trigger316 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger316.getOptionManager().findByProperty("name");
    trigger316.setName((java.lang.String) argOption.valueOf("delete file"));
    argOption = (AbstractArgumentOption) trigger316.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors318 = new adams.flow.core.Actor[2];

    // Flow.write.delete file.Variable
    adams.flow.source.Variable variable319 = new adams.flow.source.Variable();
    argOption = (AbstractArgumentOption) variable319.getOptionManager().findByProperty("variableName");
    variable319.setVariableName((adams.core.VariableName) argOption.valueOf("outfile"));
    actors318[0] = variable319;

    // Flow.write.delete file.DeleteFile
    adams.flow.transformer.DeleteFile deletefile321 = new adams.flow.transformer.DeleteFile();
    actors318[1] = deletefile321;
    trigger316.setActors(actors318);

    actors305[3] = trigger316;

    // Flow.write.generate random array
    adams.flow.control.Trigger trigger322 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger322.getOptionManager().findByProperty("name");
    trigger322.setName((java.lang.String) argOption.valueOf("generate random array"));
    argOption = (AbstractArgumentOption) trigger322.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors324 = new adams.flow.core.Actor[4];

    // Flow.write.generate random array.RandomNumberGenerator
    adams.flow.source.RandomNumberGenerator randomnumbergenerator325 = new adams.flow.source.RandomNumberGenerator();
    argOption = (AbstractArgumentOption) randomnumbergenerator325.getOptionManager().findByProperty("generator");
    adams.data.random.JavaRandomDouble javarandomdouble327 = new adams.data.random.JavaRandomDouble();
    argOption = (AbstractArgumentOption) javarandomdouble327.getOptionManager().findByProperty("seed");
    argOption.setVariable("@{seed}");
    randomnumbergenerator325.setGenerator(javarandomdouble327);

    argOption = (AbstractArgumentOption) randomnumbergenerator325.getOptionManager().findByProperty("maxNum");
    argOption.setVariable("@{num_rand}");
    actors324[0] = randomnumbergenerator325;

    // Flow.write.generate random array.SequenceToArray
    adams.flow.transformer.SequenceToArray sequencetoarray328 = new adams.flow.transformer.SequenceToArray();
    argOption = (AbstractArgumentOption) sequencetoarray328.getOptionManager().findByProperty("arrayLength");
    argOption.setVariable("@{num_rand}");
    actors324[1] = sequencetoarray328;

    // Flow.write.generate random array.ArrayProcess
    adams.flow.control.ArrayProcess arrayprocess329 = new adams.flow.control.ArrayProcess();
    argOption = (AbstractArgumentOption) arrayprocess329.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors330 = new adams.flow.core.Actor[1];

    // Flow.write.generate random array.ArrayProcess.Convert
    adams.flow.transformer.Convert convert331 = new adams.flow.transformer.Convert();
    argOption = (AbstractArgumentOption) convert331.getOptionManager().findByProperty("conversion");
    adams.data.conversion.DoubleToString doubletostring333 = new adams.data.conversion.DoubleToString();
    argOption = (AbstractArgumentOption) doubletostring333.getOptionManager().findByProperty("numDecimals");
    doubletostring333.setNumDecimals((Integer) argOption.valueOf("6"));
    doubletostring333.setFixedDecimals(true);

    convert331.setConversion(doubletostring333);

    actors330[0] = convert331;
    arrayprocess329.setActors(actors330);

    actors324[2] = arrayprocess329;

    // Flow.write.generate random array.TimedTee
    adams.flow.control.TimedTee timedtee335 = new adams.flow.control.TimedTee();
    argOption = (AbstractArgumentOption) timedtee335.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors336 = new adams.flow.core.Actor[1];

    // Flow.write.generate random array.TimedTee.IfThenElse
    adams.flow.control.IfThenElse ifthenelse337 = new adams.flow.control.IfThenElse();
    argOption = (AbstractArgumentOption) ifthenelse337.getOptionManager().findByProperty("condition");
    adams.flow.condition.bool.Expression expression339 = new adams.flow.condition.bool.Expression();
    argOption = (AbstractArgumentOption) expression339.getOptionManager().findByProperty("expression");
    expression339.setExpression((adams.parser.BooleanExpressionText) argOption.valueOf("(@{combine_numbers})"));
    ifthenelse337.setCondition(expression339);

    argOption = (AbstractArgumentOption) ifthenelse337.getOptionManager().findByProperty("thenActor");

    // Flow.write.generate random array.TimedTee.IfThenElse.then
    adams.flow.control.Sequence sequence342 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence342.getOptionManager().findByProperty("name");
    sequence342.setName((java.lang.String) argOption.valueOf("then"));
    argOption = (AbstractArgumentOption) sequence342.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors344 = new adams.flow.core.Actor[2];

    // Flow.write.generate random array.TimedTee.IfThenElse.then.StringJoin
    adams.flow.transformer.StringJoin stringjoin345 = new adams.flow.transformer.StringJoin();
    argOption = (AbstractArgumentOption) stringjoin345.getOptionManager().findByProperty("glue");
    stringjoin345.setGlue((java.lang.String) argOption.valueOf("\n"));
    actors344[0] = stringjoin345;

    // Flow.write.generate random array.TimedTee.IfThenElse.then.DumpFile
    adams.flow.sink.DumpFile dumpfile347 = new adams.flow.sink.DumpFile();
    argOption = (AbstractArgumentOption) dumpfile347.getOptionManager().findByProperty("outputFile");
    dumpfile347.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${HOME}/temp/rand.txt"));
    dumpfile347.setAppend(true);

    actors344[1] = dumpfile347;
    sequence342.setActors(actors344);

    ifthenelse337.setThenActor(sequence342);

    argOption = (AbstractArgumentOption) ifthenelse337.getOptionManager().findByProperty("elseActor");

    // Flow.write.generate random array.TimedTee.IfThenElse.else
    adams.flow.control.Sequence sequence350 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence350.getOptionManager().findByProperty("name");
    sequence350.setName((java.lang.String) argOption.valueOf("else"));
    argOption = (AbstractArgumentOption) sequence350.getOptionManager().findByProperty("actors");
    adams.flow.core.Actor[] actors352 = new adams.flow.core.Actor[2];

    // Flow.write.generate random array.TimedTee.IfThenElse.else.ArrayToSequence
    adams.flow.transformer.ArrayToSequence arraytosequence353 = new adams.flow.transformer.ArrayToSequence();
    actors352[0] = arraytosequence353;

    // Flow.write.generate random array.TimedTee.IfThenElse.else.DumpFile
    adams.flow.sink.DumpFile dumpfile354 = new adams.flow.sink.DumpFile();
    argOption = (AbstractArgumentOption) dumpfile354.getOptionManager().findByProperty("outputFile");
    dumpfile354.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${HOME}/temp/rand.txt"));
    dumpfile354.setAppend(true);

    actors352[1] = dumpfile354;
    sequence350.setActors(actors352);

    ifthenelse337.setElseActor(sequence350);

    actors336[0] = ifthenelse337;
    timedtee335.setActors(actors336);

    argOption = (AbstractArgumentOption) timedtee335.getOptionManager().findByProperty("callableName");
    timedtee335.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_write"));
    actors324[3] = timedtee335;
    trigger322.setActors(actors324);

    actors305[4] = trigger322;
    timedtrigger303.setActors(actors305);

    argOption = (AbstractArgumentOption) timedtrigger303.getOptionManager().findByProperty("prefix");
    timedtrigger303.setPrefix((java.lang.String) argOption.valueOf("Write"));
    argOption = (AbstractArgumentOption) timedtrigger303.getOptionManager().findByProperty("callableName");
    timedtrigger303.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_overall"));
    actors2[7] = timedtrigger303;
    actor.setActors(actors2);

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("flowExecutionListener");
    adams.flow.execution.NullListener nulllistener360 = new adams.flow.execution.NullListener();
    actor.setFlowExecutionListener(nulllistener360);

    return actor;
  }
}