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
    adams.flow.core.AbstractActor[] actors7 = new adams.flow.core.AbstractActor[2];

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
    gridview3.setActors(actors7);

    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("numRows");
    gridview3.setNumRows((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) gridview3.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter98 = new adams.gui.print.NullWriter();
    gridview3.setWriter(nullwriter98);

    actors2[0] = gridview3;

    // Flow.CallableActors
    adams.flow.standalone.CallableActors callableactors99 = new adams.flow.standalone.CallableActors();
    argOption = (AbstractArgumentOption) callableactors99.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors100 = new adams.flow.core.AbstractActor[2];

    // Flow.CallableActors.timing_write
    adams.flow.control.Sequence sequence101 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence101.getOptionManager().findByProperty("name");
    sequence101.setName((java.lang.String) argOption.valueOf("timing_write"));
    argOption = (AbstractArgumentOption) sequence101.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors103 = new adams.flow.core.AbstractActor[3];

    // Flow.CallableActors.timing_write.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker104 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker104.getOptionManager().findByProperty("valueName");
    containervaluepicker104.setValueName((java.lang.String) argOption.valueOf("msec"));
    containervaluepicker104.setSwitchOutputs(true);

    actors103[0] = containervaluepicker104;

    // Flow.CallableActors.timing_write.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer106 = new adams.flow.transformer.MakePlotContainer();
    argOption = (AbstractArgumentOption) makeplotcontainer106.getOptionManager().findByProperty("plotName");
    makeplotcontainer106.setPlotName((java.lang.String) argOption.valueOf("Write speed"));
    actors103[1] = makeplotcontainer106;

    // Flow.CallableActors.timing_write.CallableSink
    adams.flow.sink.CallableSink callablesink108 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink108.getOptionManager().findByProperty("callableName");
    callablesink108.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Write"));
    actors103[2] = callablesink108;
    sequence101.setActors(actors103);

    actors100[0] = sequence101;

    // Flow.CallableActors.timing_cpuspeed
    adams.flow.control.Sequence sequence110 = new adams.flow.control.Sequence();
    argOption = (AbstractArgumentOption) sequence110.getOptionManager().findByProperty("name");
    sequence110.setName((java.lang.String) argOption.valueOf("timing_cpuspeed"));
    argOption = (AbstractArgumentOption) sequence110.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors112 = new adams.flow.core.AbstractActor[3];

    // Flow.CallableActors.timing_cpuspeed.ContainerValuePicker
    adams.flow.control.ContainerValuePicker containervaluepicker113 = new adams.flow.control.ContainerValuePicker();
    argOption = (AbstractArgumentOption) containervaluepicker113.getOptionManager().findByProperty("valueName");
    containervaluepicker113.setValueName((java.lang.String) argOption.valueOf("msec"));
    containervaluepicker113.setSwitchOutputs(true);

    actors112[0] = containervaluepicker113;

    // Flow.CallableActors.timing_cpuspeed.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer115 = new adams.flow.transformer.MakePlotContainer();
    argOption = (AbstractArgumentOption) makeplotcontainer115.getOptionManager().findByProperty("plotName");
    makeplotcontainer115.setPlotName((java.lang.String) argOption.valueOf("CPU Speed"));
    actors112[1] = makeplotcontainer115;

    // Flow.CallableActors.timing_cpuspeed.CallableSink
    adams.flow.sink.CallableSink callablesink117 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink117.getOptionManager().findByProperty("callableName");
    callablesink117.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("CPU"));
    actors112[2] = callablesink117;
    sequence110.setActors(actors112);

    actors100[1] = sequence110;
    callableactors99.setActors(actors100);

    actors2[1] = callableactors99;

    // Flow.speed variables
    adams.flow.standalone.Standalones standalones119 = new adams.flow.standalone.Standalones();
    argOption = (AbstractArgumentOption) standalones119.getOptionManager().findByProperty("name");
    standalones119.setName((java.lang.String) argOption.valueOf("speed variables"));
    argOption = (AbstractArgumentOption) standalones119.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors121 = new adams.flow.core.AbstractActor[3];

    // Flow.speed variables.# means
    adams.flow.standalone.SetVariable setvariable122 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable122.getOptionManager().findByProperty("name");
    setvariable122.setName((java.lang.String) argOption.valueOf("# means"));
    argOption = (AbstractArgumentOption) setvariable122.getOptionManager().findByProperty("variableName");
    setvariable122.setVariableName((adams.core.VariableName) argOption.valueOf("num_means"));
    argOption = (AbstractArgumentOption) setvariable122.getOptionManager().findByProperty("variableValue");
    setvariable122.setVariableValue((adams.core.base.BaseText) argOption.valueOf("25"));
    actors121[0] = setvariable122;

    // Flow.speed variables.# stdevs
    adams.flow.standalone.SetVariable setvariable126 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable126.getOptionManager().findByProperty("name");
    setvariable126.setName((java.lang.String) argOption.valueOf("# stdevs"));
    argOption = (AbstractArgumentOption) setvariable126.getOptionManager().findByProperty("variableName");
    setvariable126.setVariableName((adams.core.VariableName) argOption.valueOf("num_stdevs"));
    argOption = (AbstractArgumentOption) setvariable126.getOptionManager().findByProperty("variableValue");
    setvariable126.setVariableValue((adams.core.base.BaseText) argOption.valueOf("25"));
    actors121[1] = setvariable126;

    // Flow.speed variables.# data points
    adams.flow.standalone.SetVariable setvariable130 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable130.getOptionManager().findByProperty("name");
    setvariable130.setName((java.lang.String) argOption.valueOf("# data points"));
    argOption = (AbstractArgumentOption) setvariable130.getOptionManager().findByProperty("variableName");
    setvariable130.setVariableName((adams.core.VariableName) argOption.valueOf("num_points"));
    argOption = (AbstractArgumentOption) setvariable130.getOptionManager().findByProperty("variableValue");
    setvariable130.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors121[2] = setvariable130;
    standalones119.setActors(actors121);

    actors2[2] = standalones119;

    // Flow.write variables
    adams.flow.standalone.Standalones standalones134 = new adams.flow.standalone.Standalones();
    argOption = (AbstractArgumentOption) standalones134.getOptionManager().findByProperty("name");
    standalones134.setName((java.lang.String) argOption.valueOf("write variables"));
    argOption = (AbstractArgumentOption) standalones134.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors136 = new adams.flow.core.AbstractActor[3];

    // Flow.write variables.number of files
    adams.flow.standalone.SetVariable setvariable137 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable137.getOptionManager().findByProperty("name");
    setvariable137.setName((java.lang.String) argOption.valueOf("number of files"));
    argOption = (AbstractArgumentOption) setvariable137.getOptionManager().findByProperty("variableName");
    setvariable137.setVariableName((adams.core.VariableName) argOption.valueOf("num_files"));
    argOption = (AbstractArgumentOption) setvariable137.getOptionManager().findByProperty("variableValue");
    setvariable137.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors136[0] = setvariable137;

    // Flow.write variables.number of numbers per file
    adams.flow.standalone.SetVariable setvariable141 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable141.getOptionManager().findByProperty("name");
    setvariable141.setName((java.lang.String) argOption.valueOf("number of numbers per file"));
    argOption = (AbstractArgumentOption) setvariable141.getOptionManager().findByProperty("variableName");
    setvariable141.setVariableName((adams.core.VariableName) argOption.valueOf("num_rand"));
    argOption = (AbstractArgumentOption) setvariable141.getOptionManager().findByProperty("variableValue");
    setvariable141.setVariableValue((adams.core.base.BaseText) argOption.valueOf("1000"));
    actors136[1] = setvariable141;

    // Flow.write variables.output file
    adams.flow.standalone.SetVariable setvariable145 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable145.getOptionManager().findByProperty("name");
    setvariable145.setName((java.lang.String) argOption.valueOf("output file"));
    argOption = (AbstractArgumentOption) setvariable145.getOptionManager().findByProperty("variableName");
    setvariable145.setVariableName((adams.core.VariableName) argOption.valueOf("outfile"));
    argOption = (AbstractArgumentOption) setvariable145.getOptionManager().findByProperty("variableValue");
    setvariable145.setVariableValue((adams.core.base.BaseText) argOption.valueOf("${TMP}/rand.txt"));
    actors136[2] = setvariable145;
    standalones134.setActors(actors136);

    actors2[3] = standalones134;

    // Flow.Start
    adams.flow.source.Start start149 = new adams.flow.source.Start();
    actors2[4] = start149;

    // Flow.speed
    adams.flow.control.Trigger trigger150 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger150.getOptionManager().findByProperty("name");
    trigger150.setName((java.lang.String) argOption.valueOf("speed"));
    argOption = (AbstractArgumentOption) trigger150.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors152 = new adams.flow.core.AbstractActor[4];

    // Flow.speed.ForLoop
    adams.flow.source.ForLoop forloop153 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop153.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_means}");
    actors152[0] = forloop153;

    // Flow.speed.MathExpression
    adams.flow.transformer.MathExpression mathexpression154 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression154.getOptionManager().findByProperty("expression");
    mathexpression154.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / @{num_means}"));
    actors152[1] = mathexpression154;

    // Flow.speed.SetVariable
    adams.flow.transformer.SetVariable setvariable156 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable156.getOptionManager().findByProperty("variableName");
    setvariable156.setVariableName((adams.core.VariableName) argOption.valueOf("mean"));
    actors152[2] = setvariable156;

    // Flow.speed.mean
    adams.flow.control.Trigger trigger158 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger158.getOptionManager().findByProperty("name");
    trigger158.setName((java.lang.String) argOption.valueOf("mean"));
    argOption = (AbstractArgumentOption) trigger158.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors160 = new adams.flow.core.AbstractActor[4];

    // Flow.speed.mean.ForLoop
    adams.flow.source.ForLoop forloop161 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop161.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_stdevs}");
    actors160[0] = forloop161;

    // Flow.speed.mean.MathExpression
    adams.flow.transformer.MathExpression mathexpression162 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression162.getOptionManager().findByProperty("expression");
    mathexpression162.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("X / @{num_stdevs}"));
    actors160[1] = mathexpression162;

    // Flow.speed.mean.SetVariable
    adams.flow.transformer.SetVariable setvariable164 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable164.getOptionManager().findByProperty("variableName");
    setvariable164.setVariableName((adams.core.VariableName) argOption.valueOf("stdev"));
    actors160[2] = setvariable164;

    // Flow.speed.mean.stdev
    adams.flow.control.TimedTrigger timedtrigger166 = new adams.flow.control.TimedTrigger();
    argOption = (AbstractArgumentOption) timedtrigger166.getOptionManager().findByProperty("name");
    timedtrigger166.setName((java.lang.String) argOption.valueOf("stdev"));
    argOption = (AbstractArgumentOption) timedtrigger166.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors168 = new adams.flow.core.AbstractActor[7];

    // Flow.speed.mean.stdev.plot name
    adams.flow.standalone.SetVariable setvariable169 = new adams.flow.standalone.SetVariable();
    argOption = (AbstractArgumentOption) setvariable169.getOptionManager().findByProperty("name");
    setvariable169.setName((java.lang.String) argOption.valueOf("plot name"));
    argOption = (AbstractArgumentOption) setvariable169.getOptionManager().findByProperty("variableName");
    setvariable169.setVariableName((adams.core.VariableName) argOption.valueOf("plot_name"));
    argOption = (AbstractArgumentOption) setvariable169.getOptionManager().findByProperty("variableValue");
    setvariable169.setVariableValue((adams.core.base.BaseText) argOption.valueOf("@{mean}/@{stdev}"));
    setvariable169.setExpandValue(true);

    actors168[0] = setvariable169;

    // Flow.speed.mean.stdev.ForLoop
    adams.flow.source.ForLoop forloop173 = new adams.flow.source.ForLoop();
    argOption = (AbstractArgumentOption) forloop173.getOptionManager().findByProperty("loopLower");
    forloop173.setLoopLower((Integer) argOption.valueOf("0"));
    argOption = (AbstractArgumentOption) forloop173.getOptionManager().findByProperty("loopUpper");
    argOption.setVariable("@{num_points}");
    actors168[1] = forloop173;

    // Flow.speed.mean.stdev.MathExpression
    adams.flow.transformer.MathExpression mathexpression175 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression175.getOptionManager().findByProperty("expression");
    mathexpression175.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("(X - (@{num_points} / 2)) / 33"));
    actors168[2] = mathexpression175;

    // Flow.speed.mean.stdev.MathExpression-1
    adams.flow.transformer.MathExpression mathexpression177 = new adams.flow.transformer.MathExpression();
    argOption = (AbstractArgumentOption) mathexpression177.getOptionManager().findByProperty("name");
    mathexpression177.setName((java.lang.String) argOption.valueOf("MathExpression-1"));
    argOption = (AbstractArgumentOption) mathexpression177.getOptionManager().findByProperty("expression");
    mathexpression177.setExpression((adams.parser.MathematicalExpressionText) argOption.valueOf("1/sqrt(2*PI*pow(@{stdev},2))*exp(-1*pow(X-@{mean},2)/(2*@{stdev}))"));
    mathexpression177.setOutputValuePair(true);

    actors168[3] = mathexpression177;

    // Flow.speed.mean.stdev.MakePlotContainer
    adams.flow.transformer.MakePlotContainer makeplotcontainer180 = new adams.flow.transformer.MakePlotContainer();
    makeplotcontainer180.setSkip(true);

    argOption = (AbstractArgumentOption) makeplotcontainer180.getOptionManager().findByProperty("plotName");
    argOption.setVariable("@{plot_name}");
    actors168[4] = makeplotcontainer180;

    // Flow.speed.mean.stdev.Plot
    adams.flow.sink.SequencePlotter sequenceplotter181 = new adams.flow.sink.SequencePlotter();
    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("name");
    sequenceplotter181.setName((java.lang.String) argOption.valueOf("Plot"));
    sequenceplotter181.setSkip(true);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter184 = new adams.gui.print.NullWriter();
    sequenceplotter181.setWriter(nullwriter184);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.LinePaintlet linepaintlet186 = new adams.gui.visualization.sequence.LinePaintlet();
    sequenceplotter181.setPaintlet(linepaintlet186);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.NullPaintlet nullpaintlet188 = new adams.gui.visualization.sequence.NullPaintlet();
    sequenceplotter181.setOverlayPaintlet(nullpaintlet188);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("markerPaintlet");
    adams.flow.sink.sequenceplotter.NoMarkers nomarkers190 = new adams.flow.sink.sequenceplotter.NoMarkers();
    sequenceplotter181.setMarkerPaintlet(nomarkers190);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("errorPaintlet");
    adams.flow.sink.sequenceplotter.NoErrorPaintlet noerrorpaintlet192 = new adams.flow.sink.sequenceplotter.NoErrorPaintlet();
    sequenceplotter181.setErrorPaintlet(noerrorpaintlet192);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction194 = new adams.flow.sink.sequenceplotter.NullClickAction();
    sequenceplotter181.setMouseClickAction(nullclickaction194);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider196 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter181.setColorProvider(defaultcolorprovider196);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("overlayColorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider198 = new adams.gui.visualization.core.DefaultColorProvider();
    sequenceplotter181.setOverlayColorProvider(defaultcolorprovider198);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions200 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions200.getOptionManager().findByProperty("label");
    axispaneloptions200.setLabel((java.lang.String) argOption.valueOf("steps"));
    argOption = (AbstractArgumentOption) axispaneloptions200.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator203 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions200.setTickGenerator(fancytickgenerator203);

    argOption = (AbstractArgumentOption) axispaneloptions200.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions200.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions200.getOptionManager().findByProperty("width");
    axispaneloptions200.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions200.getOptionManager().findByProperty("customFormat");
    axispaneloptions200.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter181.setAxisX(axispaneloptions200);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions208 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions208.getOptionManager().findByProperty("label");
    axispaneloptions208.setLabel((java.lang.String) argOption.valueOf("msec"));
    argOption = (AbstractArgumentOption) axispaneloptions208.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator211 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions208.setTickGenerator(fancytickgenerator211);

    argOption = (AbstractArgumentOption) axispaneloptions208.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions208.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions208.getOptionManager().findByProperty("width");
    axispaneloptions208.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions208.getOptionManager().findByProperty("customFormat");
    axispaneloptions208.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0"));
    sequenceplotter181.setAxisY(axispaneloptions208);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("plotUpdater");
    adams.flow.sink.sequenceplotter.SimplePlotUpdater simpleplotupdater216 = new adams.flow.sink.sequenceplotter.SimplePlotUpdater();
    argOption = (AbstractArgumentOption) simpleplotupdater216.getOptionManager().findByProperty("updateInterval");
    argOption.setVariable("@{num_points}");
    sequenceplotter181.setPlotUpdater(simpleplotupdater216);

    argOption = (AbstractArgumentOption) sequenceplotter181.getOptionManager().findByProperty("postProcessor");
    adams.flow.sink.sequenceplotter.PassThrough passthrough218 = new adams.flow.sink.sequenceplotter.PassThrough();
    sequenceplotter181.setPostProcessor(passthrough218);

    actors168[5] = sequenceplotter181;

    // Flow.speed.mean.stdev.SimplePlot
    adams.flow.sink.SimplePlot simpleplot219 = new adams.flow.sink.SimplePlot();
    simpleplot219.setSkip(true);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("writer");
    adams.gui.print.NullWriter nullwriter221 = new adams.gui.print.NullWriter();
    simpleplot219.setWriter(nullwriter221);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("paintlet");
    adams.gui.visualization.sequence.LinePaintlet linepaintlet223 = new adams.gui.visualization.sequence.LinePaintlet();
    simpleplot219.setPaintlet(linepaintlet223);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("overlayPaintlet");
    adams.gui.visualization.sequence.NullPaintlet nullpaintlet225 = new adams.gui.visualization.sequence.NullPaintlet();
    simpleplot219.setOverlayPaintlet(nullpaintlet225);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("mouseClickAction");
    adams.flow.sink.sequenceplotter.NullClickAction nullclickaction227 = new adams.flow.sink.sequenceplotter.NullClickAction();
    simpleplot219.setMouseClickAction(nullclickaction227);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("colorProvider");
    adams.gui.visualization.core.DefaultColorProvider defaultcolorprovider229 = new adams.gui.visualization.core.DefaultColorProvider();
    simpleplot219.setColorProvider(defaultcolorprovider229);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("axisX");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions231 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions231.getOptionManager().findByProperty("label");
    axispaneloptions231.setLabel((java.lang.String) argOption.valueOf("x"));
    argOption = (AbstractArgumentOption) axispaneloptions231.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator234 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    argOption = (AbstractArgumentOption) fancytickgenerator234.getOptionManager().findByProperty("numTicks");
    fancytickgenerator234.setNumTicks((Integer) argOption.valueOf("20"));
    axispaneloptions231.setTickGenerator(fancytickgenerator234);

    argOption = (AbstractArgumentOption) axispaneloptions231.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions231.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions231.getOptionManager().findByProperty("width");
    axispaneloptions231.setWidth((Integer) argOption.valueOf("40"));
    argOption = (AbstractArgumentOption) axispaneloptions231.getOptionManager().findByProperty("customFormat");
    axispaneloptions231.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0.0"));
    simpleplot219.setAxisX(axispaneloptions231);

    argOption = (AbstractArgumentOption) simpleplot219.getOptionManager().findByProperty("axisY");
    adams.gui.visualization.core.AxisPanelOptions axispaneloptions240 = new adams.gui.visualization.core.AxisPanelOptions();
    argOption = (AbstractArgumentOption) axispaneloptions240.getOptionManager().findByProperty("label");
    axispaneloptions240.setLabel((java.lang.String) argOption.valueOf("y"));
    argOption = (AbstractArgumentOption) axispaneloptions240.getOptionManager().findByProperty("tickGenerator");
    adams.gui.visualization.core.axis.FancyTickGenerator fancytickgenerator243 = new adams.gui.visualization.core.axis.FancyTickGenerator();
    axispaneloptions240.setTickGenerator(fancytickgenerator243);

    argOption = (AbstractArgumentOption) axispaneloptions240.getOptionManager().findByProperty("nthValueToShow");
    axispaneloptions240.setNthValueToShow((Integer) argOption.valueOf("2"));
    argOption = (AbstractArgumentOption) axispaneloptions240.getOptionManager().findByProperty("width");
    axispaneloptions240.setWidth((Integer) argOption.valueOf("60"));
    argOption = (AbstractArgumentOption) axispaneloptions240.getOptionManager().findByProperty("customFormat");
    axispaneloptions240.setCustomFormat((adams.data.DecimalFormatString) argOption.valueOf("0.0"));
    simpleplot219.setAxisY(axispaneloptions240);

    actors168[6] = simpleplot219;
    timedtrigger166.setActors(actors168);

    argOption = (AbstractArgumentOption) timedtrigger166.getOptionManager().findByProperty("callableName");
    timedtrigger166.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_cpuspeed"));
    actors160[3] = timedtrigger166;
    trigger158.setActors(actors160);

    actors152[3] = trigger158;
    trigger150.setActors(actors152);

    actors2[5] = trigger150;

    // Flow.write
    adams.flow.control.Trigger trigger248 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger248.getOptionManager().findByProperty("name");
    trigger248.setName((java.lang.String) argOption.valueOf("write"));
    argOption = (AbstractArgumentOption) trigger248.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors250 = new adams.flow.core.AbstractActor[5];

    // Flow.write.RandomNumberGenerator
    adams.flow.source.RandomNumberGenerator randomnumbergenerator251 = new adams.flow.source.RandomNumberGenerator();
    argOption = (AbstractArgumentOption) randomnumbergenerator251.getOptionManager().findByProperty("generator");
    adams.data.random.JavaRandomInt javarandomint253 = new adams.data.random.JavaRandomInt();
    randomnumbergenerator251.setGenerator(javarandomint253);

    argOption = (AbstractArgumentOption) randomnumbergenerator251.getOptionManager().findByProperty("maxNum");
    argOption.setVariable("@{num_files}");
    actors250[0] = randomnumbergenerator251;

    // Flow.write.Convert
    adams.flow.transformer.Convert convert254 = new adams.flow.transformer.Convert();
    argOption = (AbstractArgumentOption) convert254.getOptionManager().findByProperty("conversion");
    adams.data.conversion.DoubleToString doubletostring256 = new adams.data.conversion.DoubleToString();
    argOption = (AbstractArgumentOption) doubletostring256.getOptionManager().findByProperty("numDecimals");
    doubletostring256.setNumDecimals((Integer) argOption.valueOf("0"));
    doubletostring256.setFixedDecimals(true);

    convert254.setConversion(doubletostring256);

    actors250[1] = convert254;

    // Flow.write.set seed
    adams.flow.transformer.SetVariable setvariable258 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable258.getOptionManager().findByProperty("name");
    setvariable258.setName((java.lang.String) argOption.valueOf("set seed"));
    argOption = (AbstractArgumentOption) setvariable258.getOptionManager().findByProperty("variableName");
    setvariable258.setVariableName((adams.core.VariableName) argOption.valueOf("seed"));
    actors250[2] = setvariable258;

    // Flow.write.delete file
    adams.flow.control.Trigger trigger261 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger261.getOptionManager().findByProperty("name");
    trigger261.setName((java.lang.String) argOption.valueOf("delete file"));
    argOption = (AbstractArgumentOption) trigger261.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors263 = new adams.flow.core.AbstractActor[2];

    // Flow.write.delete file.Variable
    adams.flow.source.Variable variable264 = new adams.flow.source.Variable();
    argOption = (AbstractArgumentOption) variable264.getOptionManager().findByProperty("variableName");
    variable264.setVariableName((adams.core.VariableName) argOption.valueOf("outfile"));
    actors263[0] = variable264;

    // Flow.write.delete file.DeleteFile
    adams.flow.transformer.DeleteFile deletefile266 = new adams.flow.transformer.DeleteFile();
    actors263[1] = deletefile266;
    trigger261.setActors(actors263);

    actors250[3] = trigger261;

    // Flow.write.generate random array
    adams.flow.control.Trigger trigger267 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger267.getOptionManager().findByProperty("name");
    trigger267.setName((java.lang.String) argOption.valueOf("generate random array"));
    argOption = (AbstractArgumentOption) trigger267.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors269 = new adams.flow.core.AbstractActor[3];

    // Flow.write.generate random array.RandomNumberGenerator
    adams.flow.source.RandomNumberGenerator randomnumbergenerator270 = new adams.flow.source.RandomNumberGenerator();
    argOption = (AbstractArgumentOption) randomnumbergenerator270.getOptionManager().findByProperty("generator");
    adams.data.random.JavaRandomDouble javarandomdouble272 = new adams.data.random.JavaRandomDouble();
    argOption = (AbstractArgumentOption) javarandomdouble272.getOptionManager().findByProperty("seed");
    argOption.setVariable("@{seed}");
    randomnumbergenerator270.setGenerator(javarandomdouble272);

    argOption = (AbstractArgumentOption) randomnumbergenerator270.getOptionManager().findByProperty("maxNum");
    argOption.setVariable("@{num_rand}");
    actors269[0] = randomnumbergenerator270;

    // Flow.write.generate random array.SequenceToArray
    adams.flow.transformer.SequenceToArray sequencetoarray273 = new adams.flow.transformer.SequenceToArray();
    argOption = (AbstractArgumentOption) sequencetoarray273.getOptionManager().findByProperty("arrayLength");
    argOption.setVariable("@{num_rand}");
    actors269[1] = sequencetoarray273;

    // Flow.write.generate random array.TimedTee
    adams.flow.control.TimedTee timedtee274 = new adams.flow.control.TimedTee();
    argOption = (AbstractArgumentOption) timedtee274.getOptionManager().findByProperty("actors");
    adams.flow.core.AbstractActor[] actors275 = new adams.flow.core.AbstractActor[2];

    // Flow.write.generate random array.TimedTee.ArrayToSequence
    adams.flow.transformer.ArrayToSequence arraytosequence276 = new adams.flow.transformer.ArrayToSequence();
    actors275[0] = arraytosequence276;

    // Flow.write.generate random array.TimedTee.DumpFile
    adams.flow.sink.DumpFile dumpfile277 = new adams.flow.sink.DumpFile();
    argOption = (AbstractArgumentOption) dumpfile277.getOptionManager().findByProperty("outputFile");
    dumpfile277.setOutputFile((adams.core.io.PlaceholderFile) argOption.valueOf("${HOME}/temp/rand.txt"));
    dumpfile277.setAppend(true);

    actors275[1] = dumpfile277;
    timedtee274.setActors(actors275);

    argOption = (AbstractArgumentOption) timedtee274.getOptionManager().findByProperty("callableName");
    timedtee274.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("timing_write"));
    actors269[2] = timedtee274;
    trigger267.setActors(actors269);

    actors250[4] = trigger267;
    trigger248.setActors(actors250);

    actors2[6] = trigger248;
    actor.setActors(actors2);

    argOption = (AbstractArgumentOption) actor.getOptionManager().findByProperty("flowExecutionListener");
    adams.flow.execution.NullListener nulllistener281 = new adams.flow.execution.NullListener();
    actor.setFlowExecutionListener(nulllistener281);

    return actor;
  }
}