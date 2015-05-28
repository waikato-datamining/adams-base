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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
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
  extends AbstractMenuItemDefinition {

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
   * @return a suitably configured <code>AbstractActor</code> value
   * @throws Exception if set up fails
   */
  public AbstractActor getActor() throws Exception {
    AbstractArgumentOption    argOption;

    adams.flow.control.Flow actor = new adams.flow.control.Flow();

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("annotations");
    actor.setAnnotations((adams.core.base.BaseAnnotation) argOption.valueOf("Uses flow components to give an overview of the system\'s performance."));
    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors2 = new adams.flow.core.AbstractActor[7];

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
    adams.flow.core.AbstractActor[] actors7 = new adams.flow.core.AbstractActor[3];

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
    simpleplotupdater48.setUpdateInterval((Integer) argOption.valueOf("100"));
    sequenceplotter8.setPlotUpdater(simpleplotupdater48);

    argOption = (AbstractArgumentOption) sequenceplotter8.getOptionManager().findByProperty("postProcessor");
    adams.flow.sink.sequenceplotter.PassThrough passthrough51 = new adams.flow.sink.sequenceplotter.PassThrough();
    sequenceplotter8.setPostProcessor(passthrough51);

    actors7[0] = sequenceplotter8;

    //
    adams.flow.sink.SequencePlotter sequenceplotter52 = new adams.flow.sink.SequencePlotter();
    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("name");
    sequenceplotter52.setName((java.lang.String) argOption.valueOf("Write"));
    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter55 = new adams.gui.print.NullWriter();
    sequenceplotter52.setWriter(nullwriter55);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.CirclePaintlet circlepaintlet57 = new adams.gui.visualization.sequence.CirclePaintlet();
    sequenceplotter52.setPaintlet(circlepaintlet57);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.LOWESSOverlayPaintlet lowessoverlaypaintlet59 = new adams.gui.visualization.sequence.LOWESSOverlayPaintlet();
    argOption = (AbstractArgumentOption) lowessoverlaypaintlet59.getOptionManager().findByProperty("color");
    lowessoverlaypaintlet59.setColor((java.awt.Color) argOption.valueOf("#ff0000"));
    argOption = (AbstractArgumentOption) lowessoverlaypaintlet59.getOptionManager().findByProperty("window");
    lowessoverlaypaintlet59.setWindow((Integer) argOption.valueOf("50"));
    sequenceplotter52.setOverlayPaintlet(lowessoverlaypaintlet59);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("markerPaintlet");
    adams.flow.sink.sequenceplotter.NoMarkers nomarkers63 = new adams.flow.sink.sequenceplotter.NoMarkers();
    sequenceplotter52.setMarkerPaintlet(nomarkers63);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("errorPaintlet");
    adams.flow.sink.sequenceplotter.NoErrorPaintlet noerrorpaintlet65 = new adams.flow.sink.sequenceplotter.NoErrorPaintlet();
    sequenceplotter52.setErrorPaintlet(noerrorpaintlet65);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction67 = new adams.flow.sink.sequenceplotter.NullClickAction();
    sequenceplotter52.setMouseClickAction(nullclickaction67);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider69 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter52.setColorProvider(defaultcolorprovider69);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("overlayColorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider71 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter52.setOverlayColorProvider(defaultcolorprovider71);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("title");
    sequenceplotter52.setTitle((java.lang.String) argOption.valueOf("Write"));
    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions74 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions74.getOptionManager().findByProperty("label");
    axispaneloptions74.setLabel((java.lang.String) argOption.valueOf("evaluations"));
    argOption = (AbstractArgumentOption) axispaneloptions74.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator77 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions74.setTickGenerator(fancytickgenerator77);

    argOption = (AbstractArgumentOption) axispaneloptions74.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions74.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions74.getOptionManager().findByProperty("width");
    axispaneloptions74.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions74.getOptionManager().findByProperty("customFormat");
    axispaneloptions74.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter52.setAxisX(axispaneloptions74);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions82 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("label");
    axispaneloptions82.setLabel((java.lang.String) argOption.valueOf("msec"));
    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator85 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions82.setTickGenerator(fancytickgenerator85);

    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions82.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("width");
    axispaneloptions82.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("topMargin");
    axispaneloptions82.setTopMargin((Double) argOption.valueOf("0.05"));
    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("bottomMargin");
    axispaneloptions82.setBottomMargin((Double) argOption.valueOf("0.05"));
    argOption = (AbstractArgumentOption) axispaneloptions82.getOptionManager().findByProperty("customFormat");
    axispaneloptions82.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter52.setAxisY(axispaneloptions82);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("plotUpdater");
    adams.flow.sink.sequenceplotter.SimplePlotUpdater simpleplotupdater92 = new adams.flow.sink.sequenceplotter.SimplePlotUpdater();
    argOption = (AbstractArgumentOption) simpleplotupdater92.getOptionManager().findByProperty("updateInterval");
    simpleplotupdater92.setUpdateInterval((Integer) argOption.valueOf("100"));
    sequenceplotter52.setPlotUpdater(simpleplotupdater92);

    argOption = (AbstractArgumentOption) sequenceplotter52.getOptionManager().findByProperty("postProcessor");
    adams.flow.sink.sequenceplotter.PassThrough passthrough95 = new adams.flow.sink.sequenceplotter.PassThrough();
    sequenceplotter52.setPostProcessor(passthrough95);

    actors7[1] = sequenceplotter52;

    //
    adams.flow.sink.Display display96 = new adams.flow.sink.Display();
    argOption = (AbstractArgumentOption) display96.getOptionManager().findByProperty("name");
    display96.setName((java.lang.String) argOption.valueOf("Overall"));
    argOption = (AbstractArgumentOption) display96.getOptionManager().findByProperty("writer");
    adams.data.io.output.NullWriter nullwriter99 = new adams.data.io.output.NullWriter();
    display96.setWriter(nullwriter99);

    actors7[2] = display96;
    gridview3.setActors(actors7);

    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("numRows");
    gridview3.setNumRows((Integer) argOption.valueOf("3"));
    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter102 = new adams.gui.print.NullWriter();
    gridview3.setWriter(nullwriter102);

    actors2[0] = gridview3;

    // Flow.CallableActors
    adams.flow.standalone.CallableActors callableactors103 = new adams.flow.standalone.CallableActors();
    argOption = (AbstractArgumentOption) callableactors103.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors104 = new adams.flow.core.AbstractActor[3];

    // Flow.CallableActors.timing_write
    adams.flow.control.Sequence sequence105 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence105.getOptionManager().findByProperty("name");
    sequence105.setName((java.lang.String) argOption.valueOf("timing_write"));
    argOption = (AbstractArgumentOption) sequence105.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors107 = new adams.flow.core.AbstractActor[3];

    // Flow.CallableActors.timing_write.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker108 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker108.getOptionManager().findByProperty("valueName");
    containervaluepicker108.setValueName((java.lang.String) argOption.valueOf("msec"));
    containervaluepicker108.setSwitchOutputs(true);

    actors107[0] = containervaluepicker108;

    // Flow.CallableActors.timing_write.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer110 = new adams.flow.transformer.MakePlotContainer();
    argOption = (AbstractArgumentOption) makeplotcontainer110.getOptionManager().findByProperty("plotName");
    makeplotcontainer110.setPlotName((java.lang.String) argOption.valueOf("Write speed"));
    actors107[1] = makeplotcontainer110;

    // Flow.CallableActors.timing_write.CallableSink
    adams.flow.sink.CallableSink callablesink112 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink112.getOptionManager().findByProperty("callableName");
    callablesink112.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Write"));
    actors107[2] = callablesink112;
    sequence105.setActors(actors107);

    actors104[0] = sequence105;

    // Flow.CallableActors.timing_cpuspeed
    adams.flow.control.Sequence sequence114 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence114.getOptionManager().findByProperty("name");
    sequence114.setName((java.lang.String) argOption.valueOf("timing_cpuspeed"));
    argOption = (AbstractArgumentOption) sequence114.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors116 = new adams.flow.core.AbstractActor[3];

    // Flow.CallableActors.timing_cpuspeed.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker117 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker117.getOptionManager().findByProperty("valueName");
    containervaluepicker117.setValueName((java.lang.String) argOption.valueOf("msec"));
    containervaluepicker117.setSwitchOutputs(true);

    actors116[0] = containervaluepicker117;

    // Flow.CallableActors.timing_cpuspeed.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer119 = new adams.flow.transformer.MakePlotContainer();
    argOption = (AbstractArgumentOption) makeplotcontainer119.getOptionManager().findByProperty("plotName");
    makeplotcontainer119.setPlotName((java.lang.String) argOption.valueOf("CPU Speed"));
    actors116[1] = makeplotcontainer119;

    // Flow.CallableActors.timing_cpuspeed.CallableSink
    adams.flow.sink.CallableSink callablesink121 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink121.getOptionManager().findByProperty("callableName");
    callablesink121.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("CPU"));
    actors116[2] = callablesink121;
    sequence114.setActors(actors116);

    actors104[1] = sequence114;

    // Flow.CallableActors.timing_overall
    adams.flow.control.Sequence sequence123 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence123.getOptionManager().findByProperty("name");
    sequence123.setName((java.lang.String) argOption.valueOf("timing_overall"));
    argOption = (AbstractArgumentOption) sequence123.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors125 = new adams.flow.core.AbstractActor[3];

    // Flow.CallableActors.timing_overall.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker126 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker126.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors127 = new adams.flow.core.AbstractActor[1];

    // Flow.CallableActors.timing_overall.ContainerValuePicker.SetVariable
    adams.flow.transformer.SetVariable setvariable128 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable128.getOptionManager().findByProperty("variableName");
    setvariable128.setVariableName((adams.core.VariableName) argOption.valueOf("msec"));
    actors127[0] = setvariable128;
    containervaluepicker126.setActors(actors127);

    argOption = (AbstractArgumentOption) containervaluepicker126.getOptionManager().findByProperty("valueName");
    containervaluepicker126.setValueName((java.lang.String) argOption.valueOf("msec"));
    actors125[0] = containervaluepicker126;

    // Flow.CallableActors.timing_overall.ContainerValuePicker-1
    adams.flow.control.ContainerValuePicker containervaluepicker131 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker131.getOptionManager().findByProperty("name");
    containervaluepicker131.setName((java.lang.String) argOption.valueOf("ContainerValuePicker-1"));
    argOption = (AbstractArgumentOption) containervaluepicker131.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors133 = new adams.flow.core.AbstractActor[1];

    // Flow.CallableActors.timing_overall.ContainerValuePicker-1.SetVariable
    adams.flow.transformer.SetVariable setvariable134 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable134.getOptionManager().findByProperty("variableName");
    setvariable134.setVariableName((adams.core.VariableName) argOption.valueOf("prefix"));
    actors133[0] = setvariable134;
    containervaluepicker131.setActors(actors133);

    argOption = (AbstractArgumentOption) containervaluepicker131.getOptionManager().findByProperty("valueName");
    containervaluepicker131.setValueName((java.lang.String) argOption.valueOf("Prefix"));
    actors125[1] = containervaluepicker131;

    // Flow.CallableActors.timing_overall.generate output
    adams.flow.control.Trigger trigger137 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger137.getOptionManager().findByProperty("name");
    trigger137.setName((java.lang.String) argOption.valueOf("generate output"));
    argOption = (AbstractArgumentOption) trigger137.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors139 = new adams.flow.core.AbstractActor[2];

    // Flow.CallableActors.timing_overall.generate output.CombineVariables
    adams.flow.source.CombineVariables combinevariables140 = new adams.flow.source.CombineVariables();
    argOption = (AbstractArgumentOption) combinevariables140.getOptionManager().findByProperty("expression");
    combinevariables140.setExpression((adams.core.base.BaseText) argOption.valueOf("Overall @{prefix}: @{msec} msec"));
    actors139[0] = combinevariables140;

    // Flow.CallableActors.timing_overall.generate output.CallableSink
    adams.flow.sink.CallableSink callablesink142 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink142.getOptionManager().findByProperty("callableName");
    callablesink142.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Overall"));
    actors139[1] = callablesink142;
    trigger137.setActors(actors139);

    actors125[2] = trigger137;
    sequence123.setActors(actors125);

    actors104[2] = sequence123;
    callableactors103.setActors(actors104);

    actors2[1] = callableactors103;

    // Flow.speed variables
    adams.flow.standalone.Standalones standalones144 = new adams.flow.standalone.Standalones();
    argOption = (AbstractArgumentOption) standalones144.getOptionManager().findByProperty("name");
    standalones144.setName((java.lang.String) argOption.valueOf("speed variables"));
    argOption = (AbstractArgumentOption) standalones144.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors146 = new adams.flow.core.AbstractActor[3];

    // Flow.speed variables.# means
    adams.flow.standalone.SetVariable setvariable147 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable147.getOptionManager().findByProperty("name");
    setvariable147.setName((java.lang.String) argOption.valueOf("# means"));
    argOption = (AbstractArgumentOption) setvariable147.getOptionManager().findByProperty("variableName");
    setvariable147.setVariableName((adams.core.VariableName) argOption.valueOf("num_means"));
    argOption = (AbstractArgumentOption) setvariable147.getOptionManager().findByProperty("variableValue");
    setvariable147.setVariableValue((adams.core.base.BaseText) argOption.valueOf("25"));
    actors146[0] = setvariable147;

    // Flow.speed variables.# stdevs
    adams.flow.standalone.SetVariable setvariable151 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable151.getOptionManager().findByProperty("name");
    setvariable151.setName((java.lang.String) argOption.valueOf("# stdevs"));
    argOption = (AbstractArgumentOption) setvariable151.getOptionManager().findByProperty("variableName");
    setvariable151.setVariableName((adams.core.VariableName) argOption.valueOf("num_stdevs"));
    argOption = (AbstractArgumentOption) setvariable151.getOptionManager().findByProperty("variableValue");
    setvariable151.setVariableValue((adams.core.base.BaseText) argOption.valueOf("25"));
    actors146[1] = setvariable151;

    // Flow.speed variables.# data points
    adams.flow.standalone.SetVariable setvariable155 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable155.getOptionManager().findByProperty("name");
    setvariable155.setName((java.lang.String) argOption.valueOf("# data points"));
    argOption = (AbstractArgumentOption) setvariable155.getOptionManager().findByProperty("variableName");
    setvariable155.setVariableName((adams.core.VariableName) argOption.valueOf("num_points"));
    argOption = (AbstractArgumentOption) setvariable155.getOptionManager().findByProperty("variableValue");
    setvariable155.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors146[2] = setvariable155;
    standalones144.setActors(actors146);

    actors2[2] = standalones144;

    // Flow.write variables
    adams.flow.standalone.Standalones standalones159 = new adams.flow.standalone.Standalones();
    argOption = (AbstractArgumentOption) standalones159.getOptionManager().findByProperty("name");
    standalones159.setName((java.lang.String) argOption.valueOf("write variables"));
    argOption = (AbstractArgumentOption) standalones159.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors161 = new adams.flow.core.AbstractActor[3];

    // Flow.write variables.number of files
    adams.flow.standalone.SetVariable setvariable162 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable162.getOptionManager().findByProperty("name");
    setvariable162.setName((java.lang.String) argOption.valueOf("number of files"));
    argOption = (AbstractArgumentOption) setvariable162.getOptionManager().findByProperty("variableName");
    setvariable162.setVariableName((adams.core.VariableName) argOption.valueOf("num_files"));
    argOption = (AbstractArgumentOption) setvariable162.getOptionManager().findByProperty("variableValue");
    setvariable162.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors161[0] = setvariable162;

    // Flow.write variables.number of numbers per file
    adams.flow.standalone.SetVariable setvariable166 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable166.getOptionManager().findByProperty("name");
    setvariable166.setName((java.lang.String) argOption.valueOf("number of numbers per file"));
    argOption = (AbstractArgumentOption) setvariable166.getOptionManager().findByProperty("variableName");
    setvariable166.setVariableName((adams.core.VariableName) argOption.valueOf("num_rand"));
    argOption = (AbstractArgumentOption) setvariable166.getOptionManager().findByProperty("variableValue");
    setvariable166.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors161[1] = setvariable166;

    // Flow.write variables.output file
    adams.flow.standalone.SetVariable setvariable170 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable170.getOptionManager().findByProperty("name");
    setvariable170.setName((java.lang.String) argOption.valueOf("output file"));
    argOption = (AbstractArgumentOption) setvariable170.getOptionManager().findByProperty("variableName");
    setvariable170.setVariableName((adams.core.VariableName) argOption.valueOf("outfile"));
    argOption = (AbstractArgumentOption) setvariable170.getOptionManager().findByProperty("variableValue");
    setvariable170.setVariableValue((adams.core.base.BaseText) argOption.valueOf("${TMP}/rand.txt"));
    actors161[2] = setvariable170;
    standalones159.setActors(actors161);

    actors2[3] = standalones159;

    // Flow.Start
    adams.flow.source.Start start174 = new adams.flow.source.Start();
    actors2[4] = start174;

    // Flow.speed
    adams.flow.control.TimedTrigger timedtrigger175 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger175.getOptionManager().findByProperty("name");
    timedtrigger175.setName((java.lang.String) argOption.valueOf("speed"));
    argOption = (AbstractArgumentOption) timedtrigger175.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors177 = new adams.flow.core.AbstractActor[4];

    // Flow.speed.ForLoop
    adams.flow.source.ForLoop forloop178 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop178.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_means}");
    actors177[0] = forloop178;

    // Flow.speed.MathExpression
    adams.flow.transformer.MathExpression mathexpression179 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression179.getOptionManager().findByProperty("expression");
    mathexpression179.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / @{num_means}"));
    actors177[1] = mathexpression179;

    // Flow.speed.SetVariable
    adams.flow.transformer.SetVariable setvariable181 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable181.getOptionManager().findByProperty("variableName");
    setvariable181.setVariableName((adams.core.VariableName) argOption.valueOf("mean"));
    actors177[2] = setvariable181;

    // Flow.speed.mean
    adams.flow.control.Trigger trigger183 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger183.getOptionManager().findByProperty("name");
    trigger183.setName((java.lang.String) argOption.valueOf("mean"));
    argOption = (AbstractArgumentOption) trigger183.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors185 = new adams.flow.core.AbstractActor[4];

    // Flow.speed.mean.ForLoop
    adams.flow.source.ForLoop forloop186 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop186.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_stdevs}");
    actors185[0] = forloop186;

    // Flow.speed.mean.MathExpression
    adams.flow.transformer.MathExpression mathexpression187 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression187.getOptionManager().findByProperty("expression");
    mathexpression187.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / @{num_stdevs}"));
    actors185[1] = mathexpression187;

    // Flow.speed.mean.SetVariable
    adams.flow.transformer.SetVariable setvariable189 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable189.getOptionManager().findByProperty("variableName");
    setvariable189.setVariableName((adams.core.VariableName) argOption.valueOf("stdev"));
    actors185[2] = setvariable189;

    // Flow.speed.mean.stdev
    adams.flow.control.TimedTrigger timedtrigger191 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger191.getOptionManager().findByProperty("name");
    timedtrigger191.setName((java.lang.String) argOption.valueOf("stdev"));
    argOption = (AbstractArgumentOption) timedtrigger191.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors193 = new adams.flow.core.AbstractActor[7];

    // Flow.speed.mean.stdev.plot name
    adams.flow.standalone.SetVariable setvariable194 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable194.getOptionManager().findByProperty("name");
    setvariable194.setName((java.lang.String) argOption.valueOf("plot name"));
    argOption = (AbstractArgumentOption) setvariable194.getOptionManager().findByProperty("variableName");
    setvariable194.setVariableName((adams.core.VariableName) argOption.valueOf("plot_name"));
    argOption = (AbstractArgumentOption) setvariable194.getOptionManager().findByProperty("variableValue");
    setvariable194.setVariableValue((adams.core.base.BaseText) argOption.valueOf("@{mean}/@{stdev}"));
    setvariable194.setExpandValue(true);

    actors193[0] = setvariable194;

    // Flow.speed.mean.stdev.ForLoop
    adams.flow.source.ForLoop forloop198 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop198.getOptionManager().findByProperty("loopLower");
    forloop198.setLoopLower((Integer) argOption.valueOf("0"));
    argOption = (AbstractArgumentOption) forloop198.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_points}");
    actors193[1] = forloop198;

    // Flow.speed.mean.stdev.MathExpression
    adams.flow.transformer.MathExpression mathexpression200 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression200.getOptionManager().findByProperty("expression");
    mathexpression200.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("(X - (@{num_points} / 2)) / 33"));
    actors193[2] = mathexpression200;

    // Flow.speed.mean.stdev.MathExpression-1
    adams.flow.transformer.MathExpression mathexpression202 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression202.getOptionManager().findByProperty("name");
    mathexpression202.setName((java.lang.String) argOption.valueOf("MathExpression-1"));
    argOption = (AbstractArgumentOption) mathexpression202.getOptionManager().findByProperty("expression");
    mathexpression202.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
    mathexpression202.setOutputValuePair(true);

    actors193[3] = mathexpression202;

    // Flow.speed.mean.stdev.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer205 = new adams.flow.transformer.MakePlotContainer();
    makeplotcontainer205.setSkip(true);

    argOption = (AbstractArgumentOption) makeplotcontainer205.getOptionManager().findByProperty("plotName");
    argOption.setVariable("@{plot_name}");
    actors193[4] = makeplotcontainer205;

    // Flow.speed.mean.stdev.Plot
    adams.flow.sink.SequencePlotter sequenceplotter206 = new adams.flow.sink.SequencePlotter();
    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("name");
    sequenceplotter206.setName((java.lang.String) argOption.valueOf("Plot"));
    sequenceplotter206.setSkip(true);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter209 = new adams.gui.print.NullWriter();
    sequenceplotter206.setWriter(nullwriter209);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.LinePaintlet linepaintlet211 = new adams.gui.visualization.sequence.LinePaintlet();
    sequenceplotter206.setPaintlet(linepaintlet211);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.NullPaintlet nullpaintlet213 = new adams.gui.visualization.sequence.NullPaintlet();
    sequenceplotter206.setOverlayPaintlet(nullpaintlet213);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("markerPaintlet");
    adams.flow.sink.sequenceplotter.NoMarkers nomarkers215 = new adams.flow.sink.sequenceplotter.NoMarkers();
    sequenceplotter206.setMarkerPaintlet(nomarkers215);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("errorPaintlet");
    adams.flow.sink.sequenceplotter.NoErrorPaintlet noerrorpaintlet217 = new adams.flow.sink.sequenceplotter.NoErrorPaintlet();
    sequenceplotter206.setErrorPaintlet(noerrorpaintlet217);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction219 = new adams.flow.sink.sequenceplotter.NullClickAction();
    sequenceplotter206.setMouseClickAction(nullclickaction219);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider221 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter206.setColorProvider(defaultcolorprovider221);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("overlayColorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider223 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter206.setOverlayColorProvider(defaultcolorprovider223);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions225 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions225.getOptionManager().findByProperty("label");
    axispaneloptions225.setLabel((java.lang.String) argOption.valueOf("steps"));
    argOption = (AbstractArgumentOption) axispaneloptions225.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator228 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions225.setTickGenerator(fancytickgenerator228);

    argOption = (AbstractArgumentOption) axispaneloptions225.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions225.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions225.getOptionManager().findByProperty("width");
    axispaneloptions225.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions225.getOptionManager().findByProperty("customFormat");
    axispaneloptions225.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter206.setAxisX(axispaneloptions225);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions233 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions233.getOptionManager().findByProperty("label");
    axispaneloptions233.setLabel((java.lang.String) argOption.valueOf("msec"));
    argOption = (AbstractArgumentOption) axispaneloptions233.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator236 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions233.setTickGenerator(fancytickgenerator236);

    argOption = (AbstractArgumentOption) axispaneloptions233.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions233.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions233.getOptionManager().findByProperty("width");
    axispaneloptions233.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions233.getOptionManager().findByProperty("customFormat");
    axispaneloptions233.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter206.setAxisY(axispaneloptions233);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("plotUpdater");
    adams.flow.sink.sequenceplotter.SimplePlotUpdater simpleplotupdater241 = new adams.flow.sink.sequenceplotter.SimplePlotUpdater();
    argOption = (AbstractArgumentOption) simpleplotupdater241.getOptionManager().findByProperty("updateInterval");
    argOption.setVariable("@{num_points}");
    sequenceplotter206.setPlotUpdater(simpleplotupdater241);

    argOption = (AbstractArgumentOption) sequenceplotter206.getOptionManager().findByProperty("postProcessor");
    adams.flow.sink.sequenceplotter.PassThrough passthrough243 = new adams.flow.sink.sequenceplotter.PassThrough();
    sequenceplotter206.setPostProcessor(passthrough243);

    actors193[5] = sequenceplotter206;

    // Flow.speed.mean.stdev.SimplePlot
    adams.flow.sink.SimplePlot simpleplot244 = new adams.flow.sink.SimplePlot();
    simpleplot244.setSkip(true);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter246 = new adams.gui.print.NullWriter();
    simpleplot244.setWriter(nullwriter246);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.LinePaintlet linepaintlet248 = new adams.gui.visualization.sequence.LinePaintlet();
    simpleplot244.setPaintlet(linepaintlet248);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.NullPaintlet nullpaintlet250 = new adams.gui.visualization.sequence.NullPaintlet();
    simpleplot244.setOverlayPaintlet(nullpaintlet250);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction252 = new adams.flow.sink.sequenceplotter.NullClickAction();
    simpleplot244.setMouseClickAction(nullclickaction252);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider254 = new adams.gui.visualization.core.DefaultColorProvider();
    simpleplot244.setColorProvider(defaultcolorprovider254);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions256 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions256.getOptionManager().findByProperty("label");
    axispaneloptions256.setLabel((java.lang.String) argOption.valueOf("x"));
    argOption = (AbstractArgumentOption) axispaneloptions256.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator259 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    argOption = (AbstractArgumentOption) fancytickgenerator259.getOptionManager().findByProperty("numTicks");
    fancytickgenerator259.setNumTicks((Integer) argOption.valueOf("20"));
    axispaneloptions256.setTickGenerator(fancytickgenerator259);

    argOption = (AbstractArgumentOption) axispaneloptions256.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions256.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions256.getOptionManager().findByProperty("width");
    axispaneloptions256.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions256.getOptionManager().findByProperty("customFormat");
    axispaneloptions256.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0.0"));
    simpleplot244.setAxisX(axispaneloptions256);

    argOption = (AbstractArgumentOption) simpleplot244.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions265 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions265.getOptionManager().findByProperty("label");
    axispaneloptions265.setLabel((java.lang.String) argOption.valueOf("y"));
    argOption = (AbstractArgumentOption) axispaneloptions265.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator268 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions265.setTickGenerator(fancytickgenerator268);

    argOption = (AbstractArgumentOption) axispaneloptions265.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions265.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions265.getOptionManager().findByProperty("width");
    axispaneloptions265.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions265.getOptionManager().findByProperty("customFormat");
    axispaneloptions265.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0.0"));
    simpleplot244.setAxisY(axispaneloptions265);

    actors193[6] = simpleplot244;
    timedtrigger191.setActors(actors193);

    argOption = (AbstractArgumentOption) timedtrigger191.getOptionManager().findByProperty("callableName");
    timedtrigger191.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_cpuspeed"));
    actors185[3] = timedtrigger191;
    trigger183.setActors(actors185);

    actors177[3] = trigger183;
    timedtrigger175.setActors(actors177);

    argOption = (AbstractArgumentOption) timedtrigger175.getOptionManager().findByProperty("prefix");
    timedtrigger175.setPrefix((java.lang.String) argOption.valueOf("CPU"));
    argOption = (AbstractArgumentOption) timedtrigger175.getOptionManager().findByProperty("callableName");
    timedtrigger175.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_overall"));
    actors2[5] = timedtrigger175;

    // Flow.write
    adams.flow.control.TimedTrigger timedtrigger275 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger275.getOptionManager().findByProperty("name");
    timedtrigger275.setName((java.lang.String) argOption.valueOf("write"));
    argOption = (AbstractArgumentOption) timedtrigger275.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors277 = new adams.flow.core.AbstractActor[5];

    // Flow.write.RandomNumberGenerator
    adams.flow.source.RandomNumberGenerator randomnumbergenerator278 = new adams.flow.source.RandomNumberGenerator();
    argOption = (AbstractArgumentOption) randomnumbergenerator278.getOptionManager().findByProperty("generator");
    adams.data.random.JavaRandomInt javarandomint280 = new adams.data.random.JavaRandomInt();
    randomnumbergenerator278.setGenerator(javarandomint280);

    argOption = (AbstractArgumentOption) randomnumbergenerator278.getOptionManager().findByProperty("maxNum");
    argOption.setVariable("@{num_files}");
    actors277[0] = randomnumbergenerator278;

    // Flow.write.Convert
    adams.flow.transformer.Convert convert281 = new adams.flow.transformer.Convert();
    argOption = (AbstractArgumentOption) convert281.getOptionManager().findByProperty("conversion");
    adams.data.conversion.DoubleToString doubletostring283 = new adams.data.conversion.DoubleToString();
    argOption = (AbstractArgumentOption) doubletostring283.getOptionManager().findByProperty("numDecimals");
    doubletostring283.setNumDecimals((Integer) argOption.valueOf("0"));
    doubletostring283.setFixedDecimals(true);

    convert281.setConversion(doubletostring283);

    actors277[1] = convert281;

    // Flow.write.set seed
    adams.flow.transformer.SetVariable setvariable285 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable285.getOptionManager().findByProperty("name");
    setvariable285.setName((java.lang.String) argOption.valueOf("set seed"));
    argOption = (AbstractArgumentOption) setvariable285.getOptionManager().findByProperty("variableName");
    setvariable285.setVariableName((adams.core.VariableName) argOption.valueOf("seed"));
    actors277[2] = setvariable285;

    // Flow.write.delete file
    adams.flow.control.Trigger trigger288 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger288.getOptionManager().findByProperty("name");
    trigger288.setName((java.lang.String) argOption.valueOf("delete file"));
    argOption = (AbstractArgumentOption) trigger288.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors290 = new adams.flow.core.AbstractActor[2];

    // Flow.write.delete file.Variable
    adams.flow.source.Variable variable291 = new adams.flow.source.Variable();
    argOption = (AbstractArgumentOption) variable291.getOptionManager().findByProperty("variableName");
    variable291.setVariableName((adams.core.VariableName) argOption.valueOf("outfile"));
    actors290[0] = variable291;

    // Flow.write.delete file.DeleteFile
    adams.flow.transformer.DeleteFile deletefile293 = new adams.flow.transformer.DeleteFile();
    actors290[1] = deletefile293;
    trigger288.setActors(actors290);

    actors277[3] = trigger288;

    // Flow.write.generate random array
    adams.flow.control.Trigger trigger294 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger294.getOptionManager().findByProperty("name");
    trigger294.setName((java.lang.String) argOption.valueOf("generate random array"));
    argOption = (AbstractArgumentOption) trigger294.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors296 = new adams.flow.core.AbstractActor[3];

    // Flow.write.generate random array.RandomNumberGenerator
    adams.flow.source.RandomNumberGenerator randomnumbergenerator297 = new adams.flow.source.RandomNumberGenerator();
    argOption = (AbstractArgumentOption) randomnumbergenerator297.getOptionManager().findByProperty("generator");
    adams.data.random.JavaRandomDouble javarandomdouble299 = new adams.data.random.JavaRandomDouble();
    argOption = (AbstractArgumentOption) javarandomdouble299.getOptionManager().findByProperty("seed");
    argOption.setVariable("@{seed}");
    randomnumbergenerator297.setGenerator(javarandomdouble299);

    argOption = (AbstractArgumentOption) randomnumbergenerator297.getOptionManager().findByProperty("maxNum");
    argOption.setVariable("@{num_rand}");
    actors296[0] = randomnumbergenerator297;

    // Flow.write.generate random array.SequenceToArray
    adams.flow.transformer.SequenceToArray sequencetoarray300 = new adams.flow.transformer.SequenceToArray();
    argOption = (AbstractArgumentOption) sequencetoarray300.getOptionManager().findByProperty("arrayLength");
    argOption.setVariable("@{num_rand}");
    actors296[1] = sequencetoarray300;

    // Flow.write.generate random array.TimedTee
    adams.flow.control.TimedTee timedtee301 = new adams.flow.control.TimedTee();
    argOption = (AbstractArgumentOption) timedtee301.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors302 = new adams.flow.core.AbstractActor[2];

    // Flow.write.generate random array.TimedTee.ArrayToSequence
    adams.flow.transformer.ArrayToSequence arraytosequence303 = new adams.flow.transformer.ArrayToSequence();
    actors302[0] = arraytosequence303;

    // Flow.write.generate random array.TimedTee.DumpFile
    adams.flow.sink.DumpFile dumpfile304 = new adams.flow.sink.DumpFile();
    argOption = (AbstractArgumentOption) dumpfile304.getOptionManager().findByProperty("outputFile");
    dumpfile304.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${HOME}/temp/rand.txt"));
    dumpfile304.setAppend(true);

    actors302[1] = dumpfile304;
    timedtee301.setActors(actors302);

    argOption = (AbstractArgumentOption) timedtee301.getOptionManager().findByProperty("callableName");
    timedtee301.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_write"));
    actors296[2] = timedtee301;
    trigger294.setActors(actors296);

    actors277[4] = trigger294;
    timedtrigger275.setActors(actors277);

    argOption = (AbstractArgumentOption) timedtrigger275.getOptionManager().findByProperty("prefix");
    timedtrigger275.setPrefix((java.lang.String) argOption.valueOf("Write"));
    argOption = (AbstractArgumentOption) timedtrigger275.getOptionManager().findByProperty("callableName");
    timedtrigger275.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_overall"));
    actors2[6] = timedtrigger275;
    actor.setActors(actors2);

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("flowExecutionListener");
    adams.flow.execution.NullListener nulllistener310 = new adams.flow.execution.NullListener();
    actor.setFlowExecutionListener(nulllistener310);

    return actor;
  }
}