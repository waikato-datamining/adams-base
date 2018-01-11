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
 * PlotAttributeVsAttribute.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.UserMode;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import weka.classifiers.evaluation.ThresholdCurve;

/**
 * Allows the user to select a dataset and plot attribute vs attribute (selected by user).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ThresholdCurve
 */
public class PlotAttributeVsAttribute
  extends AbstractParameterHandlingWekaMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -771667287275117680L;

  /**
   * Initializes the menu item with no owner.
   */
  public PlotAttributeVsAttribute() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public PlotAttributeVsAttribute(AbstractApplicationFrame owner) {
    super(owner);
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

    adams.flow.core.Actor[] actors2 = new adams.flow.core.Actor[9];

    // Flow.CallableActors
    adams.flow.standalone.CallableActors callableactors3 = new adams.flow.standalone.CallableActors();
    adams.flow.core.Actor[] actors4 = new adams.flow.core.Actor[1];

    // Flow.CallableActors.DisplayPanelManager
    adams.flow.sink.DisplayPanelManager displaypanelmanager5 = new adams.flow.sink.DisplayPanelManager();
    argOption = (AbstractArgumentOption) displaypanelmanager5.getOptionManager().findByProperty("width");
    displaypanelmanager5.setWidth((Integer) argOption.valueOf("900"));
    argOption = (AbstractArgumentOption) displaypanelmanager5.getOptionManager().findByProperty("height");
    displaypanelmanager5.setHeight((Integer) argOption.valueOf("600"));
    displaypanelmanager5.setX(-2);
    displaypanelmanager5.setY(-2);
    displaypanelmanager5.setShortTitle(true);
    displaypanelmanager5.setName("Attribute vs attribute");

    //
    adams.flow.sink.WekaInstancesPlot wekainstancesplot9 = new adams.flow.sink.WekaInstancesPlot();
    adams.gui.print.NullWriter nullwriter11 = new adams.gui.print.NullWriter();
    wekainstancesplot9.setWriter(nullwriter11);

    displaypanelmanager5.setPanelProvider(wekainstancesplot9);

    argOption = (AbstractArgumentOption) displaypanelmanager5.getOptionManager().findByProperty("entryNameVariable");
    displaypanelmanager5.setEntryNameVariable((adams.core.VariableNameNoUpdate) argOption.valueOf("label"));
    actors4[0] = displaypanelmanager5;
    callableactors3.setActors(actors4);

    actors2[0] = callableactors3;

    // Flow.WekaSelectDataset
    adams.flow.source.WekaSelectDataset wekaselectdataset13 = new adams.flow.source.WekaSelectDataset();
    wekaselectdataset13.setStopFlowIfCanceled(true);

    argOption = (AbstractArgumentOption) wekaselectdataset13.getOptionManager().findByProperty("initialFiles");
    adams.core.io.PlaceholderFile[] initialfiles14 = new adams.core.io.PlaceholderFile[0];
    wekaselectdataset13.setInitialFiles(initialfiles14);
    actors2[1] = wekaselectdataset13;

    // Flow.WekaFileReader
    adams.flow.transformer.WekaFileReader wekafilereader15 = new adams.flow.transformer.WekaFileReader();
    weka.core.converters.AArffLoader aarffloader17 = new weka.core.converters.AArffLoader();
    wekafilereader15.setCustomLoader(aarffloader17);

    actors2[2] = wekafilereader15;

    // Flow.SetStorageValue
    adams.flow.transformer.SetStorageValue setstoragevalue18 = new adams.flow.transformer.SetStorageValue();
    argOption = (AbstractArgumentOption) setstoragevalue18.getOptionManager().findByProperty("storageName");
    setstoragevalue18.setStorageName((adams.flow.control.StorageName) argOption.valueOf("data"));
    actors2[3] = setstoragevalue18;

    // Flow.WekaFilter
    adams.flow.transformer.WekaFilter wekafilter20 = new adams.flow.transformer.WekaFilter();
    weka.filters.unsupervised.instance.RemoveRange removerange22 = new weka.filters.unsupervised.instance.RemoveRange();
    removerange22.setOptions(OptionUtils.splitOptions("-R first-last"));
    wekafilter20.setFilter(removerange22);

    actors2[4] = wekafilter20;

    // Flow.SetStorageValue-1
    adams.flow.transformer.SetStorageValue setstoragevalue23 = new adams.flow.transformer.SetStorageValue();
    argOption = (AbstractArgumentOption) setstoragevalue23.getOptionManager().findByProperty("name");
    setstoragevalue23.setName((java.lang.String) argOption.valueOf("SetStorageValue-1"));
    argOption = (AbstractArgumentOption) setstoragevalue23.getOptionManager().findByProperty("storageName");
    setstoragevalue23.setStorageName((adams.flow.control.StorageName) argOption.valueOf("atts"));
    actors2[5] = setstoragevalue23;

    // Flow.first lot of attributes
    adams.flow.control.Trigger trigger26 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger26.getOptionManager().findByProperty("name");
    trigger26.setName((java.lang.String) argOption.valueOf("first lot of attributes"));
    adams.flow.core.Actor[] actors28 = new adams.flow.core.Actor[5];

    // Flow.first lot of attributes.StorageValue
    adams.flow.source.StorageValue storagevalue29 = new adams.flow.source.StorageValue();
    argOption = (AbstractArgumentOption) storagevalue29.getOptionManager().findByProperty("storageName");
    storagevalue29.setStorageName((adams.flow.control.StorageName) argOption.valueOf("atts"));
    actors28[0] = storagevalue29;

    // Flow.first lot of attributes.WekaChooseAttributes
    adams.flow.transformer.WekaChooseAttributes wekachooseattributes31 = new adams.flow.transformer.WekaChooseAttributes();
    wekachooseattributes31.setStopFlowIfCanceled(true);

    argOption = (AbstractArgumentOption) wekachooseattributes31.getOptionManager().findByProperty("message");
    wekachooseattributes31.setMessage((java.lang.String) argOption.valueOf("Choose the first set of attributes"));
    actors28[1] = wekachooseattributes31;

    // Flow.first lot of attributes.WekaInstancesInfo
    adams.flow.transformer.WekaInstancesInfo wekainstancesinfo33 = new adams.flow.transformer.WekaInstancesInfo();
    wekainstancesinfo33.setOutputArray(true);

    argOption = (AbstractArgumentOption) wekainstancesinfo33.getOptionManager().findByProperty("type");
    wekainstancesinfo33.setType((adams.flow.transformer.WekaInstancesInfo.InfoType) argOption.valueOf("ATTRIBUTE_NAMES"));
    actors28[2] = wekainstancesinfo33;

    // Flow.first lot of attributes.Convert
    adams.flow.transformer.Convert convert35 = new adams.flow.transformer.Convert();
    adams.data.conversion.JoinOptions joinoptions37 = new adams.data.conversion.JoinOptions();
    convert35.setConversion(joinoptions37);

    actors28[3] = convert35;

    // Flow.first lot of attributes.SetVariable
    adams.flow.transformer.SetVariable setvariable38 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable38.getOptionManager().findByProperty("variableName");
    setvariable38.setVariableName((adams.core.VariableName) argOption.valueOf("first_atts"));
    actors28[4] = setvariable38;
    trigger26.setActors(actors28);

    actors2[6] = trigger26;

    // Flow.second lot of attributes
    adams.flow.control.Trigger trigger40 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger40.getOptionManager().findByProperty("name");
    trigger40.setName((java.lang.String) argOption.valueOf("second lot of attributes"));
    adams.flow.core.Actor[] actors42 = new adams.flow.core.Actor[5];

    // Flow.second lot of attributes.StorageValue
    adams.flow.source.StorageValue storagevalue43 = new adams.flow.source.StorageValue();
    argOption = (AbstractArgumentOption) storagevalue43.getOptionManager().findByProperty("storageName");
    storagevalue43.setStorageName((adams.flow.control.StorageName) argOption.valueOf("atts"));
    actors42[0] = storagevalue43;

    // Flow.second lot of attributes.WekaChooseAttributes
    adams.flow.transformer.WekaChooseAttributes wekachooseattributes45 = new adams.flow.transformer.WekaChooseAttributes();
    wekachooseattributes45.setStopFlowIfCanceled(true);

    argOption = (AbstractArgumentOption) wekachooseattributes45.getOptionManager().findByProperty("message");
    wekachooseattributes45.setMessage((java.lang.String) argOption.valueOf("Choose the second set of attributes to plot against"));
    actors42[1] = wekachooseattributes45;

    // Flow.second lot of attributes.WekaInstancesInfo
    adams.flow.transformer.WekaInstancesInfo wekainstancesinfo47 = new adams.flow.transformer.WekaInstancesInfo();
    wekainstancesinfo47.setOutputArray(true);

    argOption = (AbstractArgumentOption) wekainstancesinfo47.getOptionManager().findByProperty("type");
    wekainstancesinfo47.setType((adams.flow.transformer.WekaInstancesInfo.InfoType) argOption.valueOf("ATTRIBUTE_NAMES"));
    actors42[2] = wekainstancesinfo47;

    // Flow.second lot of attributes.Convert
    adams.flow.transformer.Convert convert49 = new adams.flow.transformer.Convert();
    adams.data.conversion.JoinOptions joinoptions51 = new adams.data.conversion.JoinOptions();
    convert49.setConversion(joinoptions51);

    actors42[3] = convert49;

    // Flow.second lot of attributes.SetVariable
    adams.flow.transformer.SetVariable setvariable52 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable52.getOptionManager().findByProperty("variableName");
    setvariable52.setVariableName((adams.core.VariableName) argOption.valueOf("second_atts"));
    actors42[4] = setvariable52;
    trigger40.setActors(actors42);

    actors2[7] = trigger40;

    // Flow.iterate 1st lot
    adams.flow.control.Trigger trigger54 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger54.getOptionManager().findByProperty("name");
    trigger54.setName((java.lang.String) argOption.valueOf("iterate 1st lot"));
    adams.flow.core.Actor[] actors56 = new adams.flow.core.Actor[3];

    // Flow.iterate 1st lot.StringConstants
    adams.flow.source.StringConstants stringconstants57 = new adams.flow.source.StringConstants();
    argOption = (AbstractArgumentOption) stringconstants57.getOptionManager().findByProperty("strings");
    argOption.setVariable("@{first_atts}");
    actors56[0] = stringconstants57;

    // Flow.iterate 1st lot.SetVariable
    adams.flow.transformer.SetVariable setvariable58 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable58.getOptionManager().findByProperty("variableName");
    setvariable58.setVariableName((adams.core.VariableName) argOption.valueOf("first_att"));
    actors56[1] = setvariable58;

    // Flow.iterate 1st lot.iterate 2nd lot
    adams.flow.control.Trigger trigger60 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger60.getOptionManager().findByProperty("name");
    trigger60.setName((java.lang.String) argOption.valueOf("iterate 2nd lot"));
    adams.flow.core.Actor[] actors62 = new adams.flow.core.Actor[5];

    // Flow.iterate 1st lot.iterate 2nd lot.StringConstants
    adams.flow.source.StringConstants stringconstants63 = new adams.flow.source.StringConstants();
    argOption = (AbstractArgumentOption) stringconstants63.getOptionManager().findByProperty("strings");
    argOption.setVariable("@{second_atts}");
    actors62[0] = stringconstants63;

    // Flow.iterate 1st lot.iterate 2nd lot.SetVariable
    adams.flow.transformer.SetVariable setvariable64 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable64.getOptionManager().findByProperty("variableName");
    setvariable64.setVariableName((adams.core.VariableName) argOption.valueOf("second_att"));
    actors62[1] = setvariable64;

    // Flow.iterate 1st lot.iterate 2nd lot.regexp
    adams.flow.control.Trigger trigger66 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger66.getOptionManager().findByProperty("name");
    trigger66.setName((java.lang.String) argOption.valueOf("regexp"));
    adams.flow.core.Actor[] actors68 = new adams.flow.core.Actor[2];

    // Flow.iterate 1st lot.iterate 2nd lot.regexp.CombineVariables
    adams.flow.source.CombineVariables combinevariables69 = new adams.flow.source.CombineVariables();
    argOption = (AbstractArgumentOption) combinevariables69.getOptionManager().findByProperty("expression");
    combinevariables69.setExpression((adams.core.base.BaseText) argOption.valueOf("(@{first_att}|@{second_att})"));
    actors68[0] = combinevariables69;

    // Flow.iterate 1st lot.iterate 2nd lot.regexp.SetVariable
    adams.flow.transformer.SetVariable setvariable71 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable71.getOptionManager().findByProperty("variableName");
    setvariable71.setVariableName((adams.core.VariableName) argOption.valueOf("current"));
    actors68[1] = setvariable71;
    trigger66.setActors(actors68);

    actors62[2] = trigger66;

    // Flow.iterate 1st lot.iterate 2nd lot.label
    adams.flow.control.Trigger trigger73 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger73.getOptionManager().findByProperty("name");
    trigger73.setName((java.lang.String) argOption.valueOf("label"));
    adams.flow.core.Actor[] actors75 = new adams.flow.core.Actor[2];

    // Flow.iterate 1st lot.iterate 2nd lot.label.CombineVariables
    adams.flow.source.CombineVariables combinevariables76 = new adams.flow.source.CombineVariables();
    argOption = (AbstractArgumentOption) combinevariables76.getOptionManager().findByProperty("expression");
    combinevariables76.setExpression((adams.core.base.BaseText) argOption.valueOf("@{first_att} vs @{second_att}"));
    actors75[0] = combinevariables76;

    // Flow.iterate 1st lot.iterate 2nd lot.label.SetVariable
    adams.flow.transformer.SetVariable setvariable78 = new adams.flow.transformer.SetVariable();
    argOption = (AbstractArgumentOption) setvariable78.getOptionManager().findByProperty("variableName");
    setvariable78.setVariableName((adams.core.VariableName) argOption.valueOf("label"));
    actors75[1] = setvariable78;
    trigger73.setActors(actors75);

    actors62[3] = trigger73;

    // Flow.iterate 1st lot.iterate 2nd lot.create plot
    adams.flow.control.Trigger trigger80 = new adams.flow.control.Trigger();
    argOption = (AbstractArgumentOption) trigger80.getOptionManager().findByProperty("name");
    trigger80.setName((java.lang.String) argOption.valueOf("create plot"));
    adams.flow.core.Actor[] actors82 = new adams.flow.core.Actor[3];

    // Flow.iterate 1st lot.iterate 2nd lot.create plot.StorageValue
    adams.flow.source.StorageValue storagevalue83 = new adams.flow.source.StorageValue();
    argOption = (AbstractArgumentOption) storagevalue83.getOptionManager().findByProperty("storageName");
    storagevalue83.setStorageName((adams.flow.control.StorageName) argOption.valueOf("data"));
    actors82[0] = storagevalue83;

    // Flow.iterate 1st lot.iterate 2nd lot.create plot.UpdateProperties
    adams.flow.control.UpdateProperties updateproperties85 = new adams.flow.control.UpdateProperties();
    argOption = (AbstractArgumentOption) updateproperties85.getOptionManager().findByProperty("properties");
    adams.core.base.BaseString[] properties86 = new adams.core.base.BaseString[1];
    properties86[0] = (adams.core.base.BaseString) argOption.valueOf("filter.expression");
    updateproperties85.setProperties(properties86);
    argOption = (AbstractArgumentOption) updateproperties85.getOptionManager().findByProperty("variableNames");
    adams.core.VariableName[] variablenames87 = new adams.core.VariableName[1];
    variablenames87[0] = (adams.core.VariableName) argOption.valueOf("current");
    updateproperties85.setVariableNames(variablenames87);

    // Flow.iterate 1st lot.iterate 2nd lot.create plot.UpdateProperties.WekaFilter
    adams.flow.transformer.WekaFilter wekafilter89 = new adams.flow.transformer.WekaFilter();
    weka.filters.unsupervised.attribute.RemoveByName removebyname91 = new weka.filters.unsupervised.attribute.RemoveByName();
    removebyname91.setOptions(OptionUtils.splitOptions("-E ^.*id$ -V"));
    wekafilter89.setFilter(removebyname91);

    wekafilter89.setKeepRelationName(true);

    updateproperties85.setSubActor(wekafilter89);

    actors82[1] = updateproperties85;

    // Flow.iterate 1st lot.iterate 2nd lot.create plot.CallableSink
    adams.flow.sink.CallableSink callablesink92 = new adams.flow.sink.CallableSink();
    argOption = (AbstractArgumentOption) callablesink92.getOptionManager().findByProperty("callableName");
    callablesink92.setCallableName((adams.flow.core.CallableActorReference) argOption.valueOf("Attribute vs attribute"));
    actors82[2] = callablesink92;
    trigger80.setActors(actors82);

    actors62[4] = trigger80;
    trigger60.setActors(actors62);

    actors56[2] = trigger60;
    trigger54.setActors(actors56);

    actors2[8] = trigger54;
    actor.setActors(actors2);

    adams.flow.execution.NullListener nulllistener95 = new adams.flow.execution.NullListener();
    actor.setFlowExecutionListener(nulllistener95);

    return actor;
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    Flow	flow;
    String	msg;

    try {
      flow = (Flow) getActor();
      flow.setDefaultCloseOperation(BaseFrame.DISPOSE_ON_CLOSE);
      msg = flow.setUp();
      if (msg != null)
	throw new IllegalStateException("Failed to setup flow:\n" + msg);
      msg = flow.execute();
      if (msg != null)
	throw new IllegalStateException("Failed to execute flow:\n" + msg);
      flow.wrapUp();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	null,
	"Failed to instantiate/run the flow for plotting the attributes!\n"
	  + Utils.throwableToString(e));
    }
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Plot attribute vs attribute";
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
   * Whether to use a simple runnable for launching or a separate thread.
   *
   * @return		true if to use separate thread
   */
  protected boolean getUseThread() {
    return true;
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
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_VISUALIZATION;
  }
}