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
 * MakeCompatibleDatasets.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.base.ArrayDimensions;
import adams.core.base.BaseString;
import adams.core.base.BaseText;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.OptionUtils;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.condition.bool.BooleanCondition;
import adams.flow.condition.bool.Expression;
import adams.flow.control.ArrayProcess;
import adams.flow.control.Flow;
import adams.flow.control.StorageName;
import adams.flow.control.SubProcess;
import adams.flow.control.Switch;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.control.UpdateProperties;
import adams.flow.core.Actor;
import adams.flow.execution.NullListener;
import adams.flow.sink.WekaFileWriter;
import adams.flow.source.CombineVariables;
import adams.flow.source.FileSupplier;
import adams.flow.source.NewArray;
import adams.flow.source.Start;
import adams.flow.source.StorageForLoop;
import adams.flow.source.StorageValue;
import adams.flow.standalone.SetVariable;
import adams.flow.transformer.ArrayLength;
import adams.flow.transformer.ArrayToSequence;
import adams.flow.transformer.BaseName;
import adams.flow.transformer.Convert;
import adams.flow.transformer.FileExtension;
import adams.flow.transformer.GetArrayElement;
import adams.flow.transformer.IncVariable;
import adams.flow.transformer.PrependDir;
import adams.flow.transformer.SetArrayElement;
import adams.flow.transformer.SetStorageValue;
import adams.flow.transformer.SpreadSheetAppend;
import adams.flow.transformer.SpreadSheetFileReader;
import adams.flow.transformer.SpreadSheetInfo;
import adams.flow.transformer.WekaFileReader;
import adams.flow.transformer.WekaFilter;
import adams.flow.transformer.WekaInstancesMerge;
import adams.flow.transformer.WekaRenameRelation;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.ParameterPanelPage;
import adams.gui.wizard.SelectDirectoryPage;
import adams.gui.wizard.WekaSelectMultipleDatasetsPage;
import adams.gui.wizard.WizardPane;
import adams.parser.BooleanExpressionText;
import weka.core.converters.AArffLoader;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.unsupervised.instance.RemoveRange;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * For making compatible ARFF datasets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MakeCompatibleDatasets
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;
  public static final String CUSTOM_WEKA_FILE_LOADER = "CustomWekaFileLoader";
  public static final String WEKA_FILE_LOADER = "WekaFileLoader";
  public static final String CUSTOM_ADAMS_READER = "CustomAdamsReader";
  public static final String ADAMS_READER = "AdamsReader";
  public static final String ADAMS_FILE_EXTENSION = "AdamsFileExtension";

  /**
   * Initializes the menu item with no owner.
   */
  public MakeCompatibleDatasets() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public MakeCompatibleDatasets(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "makecompatible.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    final WizardPane			wizard;
    WekaSelectMultipleDatasetsPage	infiles;
    ParameterPanelPage			format;
    Properties				props;
    SelectDirectoryPage 		outdir;
    FinalPage				finalpage;
    WekaInstancesMerge			merge;
    final ChildFrame			frame;

    // wizard
    wizard = new WizardPane();
    wizard.setCustomFinishText("Generate");
    infiles = new WekaSelectMultipleDatasetsPage("Input");
    infiles.setDescription(
      "Select the datasets that you want to make compatible.\n"
	+ "You have to choose at least two.");
    infiles.setPageCheck(new PageCheck() {
      @Override
      public boolean checkPage(AbstractWizardPage page) {
	Properties props = page.getProperties();
	try {
	  String[] files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
	  return (files.length >= 2);
	} catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to obtain files:", e);
	}
	return false;
      }
    });
    wizard.addPage(infiles);

    format = new ParameterPanelPage("Format");
    format.setDescription(
      "Here you can specify how to load the datasets.\n"
	+ "If you need to use custom parameters for the Weka loader, then please tick the checkbox "
	+ "for using a custom Weka loader and configure the loader accordingly.\n"
	+ "The Weka CSV loader does not handle all CSV files correctly. In that case, you can try using "
	+ "the ADAMS one. In that case, tick the checkbox for using a custom ADAMS reader and configure "
	+ "the reader accordingly. If necessary, you can also modify the file extension that the ADAMS "
	+ "reader reacts to.");
    format.getParameterPanel().addPropertyType(CUSTOM_WEKA_FILE_LOADER, PropertiesParameterPanel.PropertyType.BOOLEAN);
    format.getParameterPanel().setLabel(CUSTOM_WEKA_FILE_LOADER, "Use custom Weka loader");
    format.getParameterPanel().addPropertyType(WEKA_FILE_LOADER, PropertiesParameterPanel.PropertyType.OBJECT_EDITOR);
    format.getParameterPanel().setLabel(WEKA_FILE_LOADER, "Weka loader");
    format.getParameterPanel().setChooser(WEKA_FILE_LOADER, new WekaGenericObjectEditorPanel(weka.core.converters.AbstractFileLoader.class, new ArffLoader(), true));
    format.getParameterPanel().addPropertyType(CUSTOM_ADAMS_READER, PropertiesParameterPanel.PropertyType.BOOLEAN);
    format.getParameterPanel().setLabel(CUSTOM_ADAMS_READER, "Use custom ADAMS reader");
    format.getParameterPanel().addPropertyType(ADAMS_READER, PropertiesParameterPanel.PropertyType.OBJECT_EDITOR);
    format.getParameterPanel().setLabel(ADAMS_READER, "ADAMS reader");
    format.getParameterPanel().setChooser(ADAMS_READER, new GenericObjectEditorPanel(adams.data.io.input.SpreadSheetReader.class, new CsvSpreadSheetReader(), true));
    format.getParameterPanel().addPropertyType(ADAMS_FILE_EXTENSION, PropertiesParameterPanel.PropertyType.STRING);
    format.getParameterPanel().setLabel(ADAMS_FILE_EXTENSION, "File extension for ADAMS reader");
    format.getParameterPanel().setPropertyOrder(new String[]{CUSTOM_WEKA_FILE_LOADER, WEKA_FILE_LOADER, CUSTOM_ADAMS_READER, ADAMS_READER, ADAMS_FILE_EXTENSION});
    props = new Properties();
    props.setBoolean(CUSTOM_WEKA_FILE_LOADER, false);
    props.setProperty(WEKA_FILE_LOADER, OptionUtils.getCommandLine(new ArffLoader()));
    props.setBoolean(CUSTOM_ADAMS_READER, false);
    props.setProperty(ADAMS_READER, OptionUtils.getCommandLine(new CsvSpreadSheetReader()));
    props.setProperty(ADAMS_FILE_EXTENSION, "csv");
    format.getParameterPanel().setProperties(props);
    wizard.addPage(format);

    outdir = new SelectDirectoryPage("Output");
    outdir.setDescription("Select the directory to save the generated data to (in ARFF format).");
    wizard.addPage(outdir);
    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Ready</h2>Please click on <b>Generate</b> to start the process.</html>");
    wizard.addPage(finalpage);
    frame = createChildFrame(wizard, 900, 600);
    wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!e.getActionCommand().equals(WizardPane.ACTION_FINISH)) {
          frame.dispose();
          return;
        }
        Properties props = wizard.getProperties(false);
        doGenerate(frame, props);
      }
    });
  }

  /**
   * Performs the data generation.
   *
   * @param frame       the frame to close
   * @param props	the configuration
   */
  protected void doGenerate(ChildFrame frame, Properties props) {
    String[]			files;
    PlaceholderFile[]		input;
    PlaceholderDirectory	output;
    int				i;
    AbstractFileLoader		loader;
    SpreadSheetReader		reader;
    String			ext;
    Flow			flow;
    String			msg;

    try {
      files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
      input = new PlaceholderFile[files.length];
      for (i = 0; i < files.length; i++)
	input[i] = new PlaceholderFile(files[i]);
      loader = null;
      if (props.getBoolean(CUSTOM_WEKA_FILE_LOADER))
	loader = (AbstractFileLoader) OptionUtils.forAnyCommandLine(AbstractFileLoader.class, props.getProperty(WEKA_FILE_LOADER));
      reader = null;
      if (props.getBoolean(CUSTOM_ADAMS_READER))
	reader = (SpreadSheetReader) OptionUtils.forAnyCommandLine(SpreadSheetReader.class, props.getProperty(ADAMS_READER));
      ext = props.getProperty(ADAMS_FILE_EXTENSION);
      output = new PlaceholderDirectory(props.getPath(SelectDirectoryPage.KEY_DIRECTORY));
    }
    catch (Exception ex) {
      GUIHelper.showErrorMessage(
	getOwner(), "Failed to get setup from wizard!\n" + Utils.throwableToString(ex));
      return;
    }

    try {
      flow = createFlow(input, loader, reader, ext, output);
      msg  = flow.setUp();
      if (msg != null) {
	GUIHelper.showErrorMessage(
	  getOwner(), "Failed to setup flow!\n" + msg);
	return;
      }
      msg = flow.execute();
      if (msg != null) {
	GUIHelper.showErrorMessage(
	  getOwner(), "Failed to execute flow!\n" + msg);
	flow.wrapUp();
	flow.cleanUp();
	return;
      }
      flow.wrapUp();
      flow.cleanUp();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	getOwner(), "Failed to setup/execute flow!\n" + Utils.throwableToString(e));
      return;
    }

    GUIHelper.showInformationMessage(null, "Data successfully generated in the following directory:\n" + props.getProperty(SelectDirectoryPage.KEY_DIRECTORY));
    frame.dispose();
  }

  /**
   * Creates the flow.
   *
   * @param input	the input files
   * @param loader	the custom loader, null if automatic
   * @param reader	the spreadsheet reader, null if not to use
   * @param ext		the file extension to which to apply the spreadsheet reader
   * @param output	the output directory to store the generated ARFF files in
   * @return 		the flow
   * @throws Exception if set up fails
   */
  protected Flow createFlow(PlaceholderFile[] input, AbstractFileLoader loader, SpreadSheetReader reader, String ext, PlaceholderDirectory output) throws Exception {
    AbstractArgumentOption argOption;

    adams.flow.control.Flow actor = new adams.flow.control.Flow();

    List<Actor> actors = new ArrayList<>();

    // Flow.out dir
    SetVariable setvariable = new SetVariable();
    argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("name");
    setvariable.setName((String) argOption.valueOf("out dir"));
    argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableName");
    setvariable.setVariableName((VariableName) argOption.valueOf("outdir"));
    argOption = (AbstractArgumentOption) setvariable.getOptionManager().findByProperty("variableValue");
    setvariable.setVariableValue((BaseText) argOption.valueOf(output.getAbsolutePath()));
    actors.add(setvariable);

    // Flow.use adams spreadsheetreader
    SetVariable setvariable2 = new SetVariable();
    argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("name");
    setvariable2.setName((String) argOption.valueOf("use adams spreadsheetreader"));
    argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableName");
    setvariable2.setVariableName((VariableName) argOption.valueOf("use_adams_spreader"));
    argOption = (AbstractArgumentOption) setvariable2.getOptionManager().findByProperty("variableValue");
    setvariable2.setVariableValue((BaseText) argOption.valueOf("false"));
    actors.add(setvariable2);

    // Flow.Start
    Start start = new Start();
    actors.add(start);

    // Flow.load files
    Trigger trigger = new Trigger();
    {
      argOption = (AbstractArgumentOption) trigger.getOptionManager().findByProperty("name");
      trigger.setName((String) argOption.valueOf("load files"));
      List<Actor> actors2 = new ArrayList<>();

      // Flow.load files.FileSupplier
      FileSupplier filesupplier = new FileSupplier();
      filesupplier.setOutputArray(true);
      filesupplier.setFiles(input);
      actors2.add(filesupplier);

      // Flow.load files.num files
      Tee tee = new Tee();
      {
        argOption = (AbstractArgumentOption) tee.getOptionManager().findByProperty("name");
        tee.setName((String) argOption.valueOf("num files"));
        List<Actor> actors3 = new ArrayList<>();

        // Flow.load files.num files.ArrayLength
        ArrayLength arraylength = new ArrayLength();
        actors3.add(arraylength);

        // Flow.load files.num files.SetVariable
        adams.flow.transformer.SetVariable setvariable3 = new adams.flow.transformer.SetVariable();
        argOption = (AbstractArgumentOption) setvariable3.getOptionManager().findByProperty("variableName");
        setvariable3.setVariableName((VariableName) argOption.valueOf("num_files"));
        actors3.add(setvariable3);
        tee.setActors(actors3.toArray(new Actor[0]));

      }
      actors2.add(tee);

      // Flow.load files.new length array
      Trigger trigger2 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger2.getOptionManager().findByProperty("name");
        trigger2.setName((String) argOption.valueOf("new length array"));
        List<Actor> actors4 = new ArrayList<>();

        // Flow.load files.new length array.NewArray
        NewArray newarray = new NewArray();
        argOption = (AbstractArgumentOption) newarray.getOptionManager().findByProperty("arrayClass");
        newarray.setArrayClass((String) argOption.valueOf("java.lang.Integer"));
        argOption = (AbstractArgumentOption) newarray.getOptionManager().findByProperty("dimensions");
        newarray.setDimensions((ArrayDimensions) argOption.valueOf("[@{num_files}]"));
        actors4.add(newarray);

        // Flow.load files.new length array.SetStorageValue
        SetStorageValue setstoragevalue = new SetStorageValue();
        argOption = (AbstractArgumentOption) setstoragevalue.getOptionManager().findByProperty("storageName");
        setstoragevalue.setStorageName((StorageName) argOption.valueOf("lengths"));
        actors4.add(setstoragevalue);
        trigger2.setActors(actors4.toArray(new Actor[0]));

      }
      actors2.add(trigger2);

      // Flow.load files.new name array
      Trigger trigger3 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger3.getOptionManager().findByProperty("name");
        trigger3.setName((String) argOption.valueOf("new name array"));
        List<Actor> actors5 = new ArrayList<Actor>();

        // Flow.load files.new name array.NewArray
        NewArray newarray2 = new NewArray();
        argOption = (AbstractArgumentOption) newarray2.getOptionManager().findByProperty("dimensions");
        newarray2.setDimensions((ArrayDimensions) argOption.valueOf("[@{num_files}]"));
        actors5.add(newarray2);

        // Flow.load files.new name array.SetStorageValue
        SetStorageValue setstoragevalue2 = new SetStorageValue();
        argOption = (AbstractArgumentOption) setstoragevalue2.getOptionManager().findByProperty("storageName");
        setstoragevalue2.setStorageName((StorageName) argOption.valueOf("names"));
        actors5.add(setstoragevalue2);
        trigger3.setActors(actors5.toArray(new Actor[0]));

      }
      actors2.add(trigger3);

      // Flow.load files.SetVariable
      adams.flow.transformer.SetVariable setvariable4 = new adams.flow.transformer.SetVariable();
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableName");
      setvariable4.setVariableName((VariableName) argOption.valueOf("index"));
      argOption = (AbstractArgumentOption) setvariable4.getOptionManager().findByProperty("variableValue");
      setvariable4.setVariableValue((BaseText) argOption.valueOf("0"));
      actors2.add(setvariable4);

      // Flow.load files.ArrayProcess
      ArrayProcess arrayprocess = new ArrayProcess();
      {
        List<Actor> actors6 = new ArrayList<>();

        // Flow.load files.ArrayProcess.IncVariable
        IncVariable incvariable = new IncVariable();
        argOption = (AbstractArgumentOption) incvariable.getOptionManager().findByProperty("variableName");
        incvariable.setVariableName((VariableName) argOption.valueOf("index"));
        actors6.add(incvariable);

        // Flow.load files.ArrayProcess.name
        Tee tee2 = new Tee();
        {
          argOption = (AbstractArgumentOption) tee2.getOptionManager().findByProperty("name");
          tee2.setName((String) argOption.valueOf("name"));
          List<Actor> actors7 = new ArrayList<>();

          // Flow.load files.ArrayProcess.name.BaseName
          BaseName basename = new BaseName();
          basename.setRemoveExtension(true);

          actors7.add(basename);

          // Flow.load files.ArrayProcess.name.SetVariable
          adams.flow.transformer.SetVariable setvariable5 = new adams.flow.transformer.SetVariable();
          argOption = (AbstractArgumentOption) setvariable5.getOptionManager().findByProperty("variableName");
          setvariable5.setVariableName((VariableName) argOption.valueOf("name"));
          actors7.add(setvariable5);

          // Flow.load files.ArrayProcess.name.update array
          Trigger trigger4 = new Trigger();
          {
            argOption = (AbstractArgumentOption) trigger4.getOptionManager().findByProperty("name");
            trigger4.setName((String) argOption.valueOf("update array"));
            List<Actor> actors8 = new ArrayList<>();

            // Flow.load files.ArrayProcess.name.update array.StorageValue
            StorageValue storagevalue = new StorageValue();
            argOption = (AbstractArgumentOption) storagevalue.getOptionManager().findByProperty("storageName");
            storagevalue.setStorageName((StorageName) argOption.valueOf("names"));
            actors8.add(storagevalue);

            // Flow.load files.ArrayProcess.name.update array.SetArrayElement
            SetArrayElement setarrayelement = new SetArrayElement();
            argOption = (AbstractArgumentOption) setarrayelement.getOptionManager().findByProperty("index");
            argOption.setVariable("@{index}");
            argOption = (AbstractArgumentOption) setarrayelement.getOptionManager().findByProperty("value");
            argOption.setVariable("@{name}");
            actors8.add(setarrayelement);

            // Flow.load files.ArrayProcess.name.update array.SetStorageValue
            SetStorageValue setstoragevalue3 = new SetStorageValue();
            argOption = (AbstractArgumentOption) setstoragevalue3.getOptionManager().findByProperty("storageName");
            setstoragevalue3.setStorageName((StorageName) argOption.valueOf("names"));
            actors8.add(setstoragevalue3);
            trigger4.setActors(actors8.toArray(new Actor[0]));

          }
          actors7.add(trigger4);
          tee2.setActors(actors7.toArray(new Actor[0]));

        }
        actors6.add(tee2);

        // Flow.load files.ArrayProcess.extension
        Tee tee3 = new Tee();
        {
          argOption = (AbstractArgumentOption) tee3.getOptionManager().findByProperty("name");
          tee3.setName((String) argOption.valueOf("extension"));
          List<Actor> actors9 = new ArrayList<>();

          // Flow.load files.ArrayProcess.extension.FileExtension
          FileExtension fileextension = new FileExtension();
          actors9.add(fileextension);

          // Flow.load files.ArrayProcess.extension.SetVariable
          adams.flow.transformer.SetVariable setvariable6 = new adams.flow.transformer.SetVariable();
          argOption = (AbstractArgumentOption) setvariable6.getOptionManager().findByProperty("variableName");
          setvariable6.setVariableName((VariableName) argOption.valueOf("extension"));
          actors9.add(setvariable6);
          tee3.setActors(actors9.toArray(new Actor[0]));

        }
        actors6.add(tee3);

        // Flow.load files.ArrayProcess.Switch
        Switch switch_ = new Switch();
        {
          List<BooleanCondition> conditions = new ArrayList<BooleanCondition>();
          Expression expression = new Expression();
          argOption = (AbstractArgumentOption) expression.getOptionManager().findByProperty("expression");
          expression.setExpression((BooleanExpressionText) argOption.valueOf("(lower(\\\"@{extension}\\\") = \\\"" + ext + "\\\") and (@{use_adams_spreader} = " + (reader != null) + ")"));
          conditions.add(expression);
          switch_.setConditions(conditions.toArray(new BooleanCondition[0]));

          List<Actor> cases = new ArrayList<>();

          // Flow.load files.ArrayProcess.Switch.SpreadSheetFileReader
          SpreadSheetFileReader spreadsheetfilereader = new SpreadSheetFileReader();
          CsvSpreadSheetReader csvspreadsheetreader = new CsvSpreadSheetReader();
          DenseDataRow densedatarow = new DenseDataRow();
          csvspreadsheetreader.setDataRowType(densedatarow);

          SpreadSheet spreadsheet = new DefaultSpreadSheet();
          csvspreadsheetreader.setSpreadSheetType(spreadsheet);

	  if (reader != null)
	    spreadsheetfilereader.setReader(reader);
	  else
	    spreadsheetfilereader.setReader(csvspreadsheetreader);

          cases.add(spreadsheetfilereader);

          // Flow.load files.ArrayProcess.Switch.weka
          SubProcess subprocess = new SubProcess();
          {
            argOption = (AbstractArgumentOption) subprocess.getOptionManager().findByProperty("name");
            subprocess.setName((String) argOption.valueOf("weka"));
            List<Actor> actors10 = new ArrayList<>();

            // Flow.load files.ArrayProcess.Switch.weka.WekaFileReader
            WekaFileReader wekafilereader = new WekaFileReader();
	    wekafilereader.setUseCustomLoader(loader != null);
            AArffLoader aarffloader = new AArffLoader();
	    if (loader != null)
	      wekafilereader.setCustomLoader(loader);
	    else
	      wekafilereader.setCustomLoader(aarffloader);

            actors10.add(wekafilereader);

            // Flow.load files.ArrayProcess.Switch.weka.Convert
            Convert convert = new Convert();
            WekaInstancesToSpreadSheet wekainstancestospreadsheet = new WekaInstancesToSpreadSheet();
            DenseDataRow densedatarow2 = new DenseDataRow();
            wekainstancestospreadsheet.setDataRowType(densedatarow2);

            SpreadSheet spreadsheet2 = new DefaultSpreadSheet();
            wekainstancestospreadsheet.setSpreadSheetType(spreadsheet2);

            convert.setConversion(wekainstancestospreadsheet);

            actors10.add(convert);
            subprocess.setActors(actors10.toArray(new Actor[0]));

          }
          cases.add(subprocess);
          switch_.setCases(cases.toArray(new Actor[0]));

        }
        actors6.add(switch_);

        // Flow.load files.ArrayProcess.num rows
        Tee tee4 = new Tee();
        {
          argOption = (AbstractArgumentOption) tee4.getOptionManager().findByProperty("name");
          tee4.setName((String) argOption.valueOf("num rows"));
          List<Actor> actors11 = new ArrayList<>();

          // Flow.load files.ArrayProcess.num rows.SpreadSheetInfo
          SpreadSheetInfo spreadsheetinfo = new SpreadSheetInfo();
          actors11.add(spreadsheetinfo);

          // Flow.load files.ArrayProcess.num rows.SetVariable
          adams.flow.transformer.SetVariable setvariable7 = new adams.flow.transformer.SetVariable();
          argOption = (AbstractArgumentOption) setvariable7.getOptionManager().findByProperty("variableName");
          setvariable7.setVariableName((VariableName) argOption.valueOf("length"));
          actors11.add(setvariable7);

          // Flow.load files.ArrayProcess.num rows.update array
          Trigger trigger5 = new Trigger();
          {
            argOption = (AbstractArgumentOption) trigger5.getOptionManager().findByProperty("name");
            trigger5.setName((String) argOption.valueOf("update array"));
            List<Actor> actors12 = new ArrayList<>();

            // Flow.load files.ArrayProcess.num rows.update array.StorageValue
            StorageValue storagevalue2 = new StorageValue();
            argOption = (AbstractArgumentOption) storagevalue2.getOptionManager().findByProperty("storageName");
            storagevalue2.setStorageName((StorageName) argOption.valueOf("lengths"));
            actors12.add(storagevalue2);

            // Flow.load files.ArrayProcess.num rows.update array.SetArrayElement
            SetArrayElement setarrayelement2 = new SetArrayElement();
            argOption = (AbstractArgumentOption) setarrayelement2.getOptionManager().findByProperty("index");
            argOption.setVariable("@{index}");
            argOption = (AbstractArgumentOption) setarrayelement2.getOptionManager().findByProperty("value");
            argOption.setVariable("@{length}");
            actors12.add(setarrayelement2);

            // Flow.load files.ArrayProcess.num rows.update array.SetStorageValue
            SetStorageValue setstoragevalue4 = new SetStorageValue();
            argOption = (AbstractArgumentOption) setstoragevalue4.getOptionManager().findByProperty("storageName");
            setstoragevalue4.setStorageName((StorageName) argOption.valueOf("lengths"));
            actors12.add(setstoragevalue4);
            trigger5.setActors(actors12.toArray(new Actor[0]));

          }
          actors11.add(trigger5);
          tee4.setActors(actors11.toArray(new Actor[0]));

        }
        actors6.add(tee4);
        arrayprocess.setActors(actors6.toArray(new Actor[0]));

      }
      actors2.add(arrayprocess);

      // Flow.load files.SetStorageValue
      SetStorageValue setstoragevalue5 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue5.getOptionManager().findByProperty("storageName");
      setstoragevalue5.setStorageName((StorageName) argOption.valueOf("sheets"));
      actors2.add(setstoragevalue5);
      trigger.setActors(actors2.toArray(new Actor[0]));

    }
    actors.add(trigger);

    // Flow.combine files
    Trigger trigger6 = new Trigger();
    {
      argOption = (AbstractArgumentOption) trigger6.getOptionManager().findByProperty("name");
      trigger6.setName((String) argOption.valueOf("combine files"));
      List<Actor> actors13 = new ArrayList<>();

      // Flow.combine files.StorageValue
      StorageValue storagevalue3 = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue3.getOptionManager().findByProperty("storageName");
      storagevalue3.setStorageName((StorageName) argOption.valueOf("sheets"));
      actors13.add(storagevalue3);

      // Flow.combine files.ArrayToSequence
      ArrayToSequence arraytosequence = new ArrayToSequence();
      actors13.add(arraytosequence);

      // Flow.combine files.SpreadSheetAppend
      SpreadSheetAppend spreadsheetappend = new SpreadSheetAppend();
      spreadsheetappend.setNoCopy(true);

      argOption = (AbstractArgumentOption) spreadsheetappend.getOptionManager().findByProperty("storageName");
      spreadsheetappend.setStorageName((StorageName) argOption.valueOf("combined_sheets"));
      actors13.add(spreadsheetappend);
      trigger6.setActors(actors13.toArray(new Actor[0]));

    }
    actors.add(trigger6);

    // Flow.create combined arff
    Trigger trigger7 = new Trigger();
    {
      argOption = (AbstractArgumentOption) trigger7.getOptionManager().findByProperty("name");
      trigger7.setName((String) argOption.valueOf("create combined arff"));
      List<Actor> actors14 = new ArrayList<>();

      // Flow.create combined arff.StorageValue
      StorageValue storagevalue4 = new StorageValue();
      argOption = (AbstractArgumentOption) storagevalue4.getOptionManager().findByProperty("storageName");
      storagevalue4.setStorageName((StorageName) argOption.valueOf("combined_sheets"));
      actors14.add(storagevalue4);

      // Flow.create combined arff.Convert
      Convert convert2 = new Convert();
      SpreadSheetToWekaInstances spreadsheettowekainstances = new SpreadSheetToWekaInstances();
      convert2.setConversion(spreadsheettowekainstances);

      actors14.add(convert2);

      // Flow.create combined arff.SetStorageValue
      SetStorageValue setstoragevalue6 = new SetStorageValue();
      argOption = (AbstractArgumentOption) setstoragevalue6.getOptionManager().findByProperty("storageName");
      setstoragevalue6.setStorageName((StorageName) argOption.valueOf("combined_arff"));
      actors14.add(setstoragevalue6);
      trigger7.setActors(actors14.toArray(new Actor[0]));

    }
    actors.add(trigger7);

    // Flow.create separate arff
    Trigger trigger8 = new Trigger();
    {
      argOption = (AbstractArgumentOption) trigger8.getOptionManager().findByProperty("name");
      trigger8.setName((String) argOption.valueOf("create separate arff"));
      List<Actor> actors15 = new ArrayList<>();

      // Flow.create separate arff.StorageForLoop
      StorageForLoop storageforloop = new StorageForLoop();
      argOption = (AbstractArgumentOption) storageforloop.getOptionManager().findByProperty("loopUpper");
      argOption.setVariable("@{num_files}");
      argOption = (AbstractArgumentOption) storageforloop.getOptionManager().findByProperty("variableName");
      storageforloop.setVariableName((VariableName) argOption.valueOf("index"));
      argOption = (AbstractArgumentOption) storageforloop.getOptionManager().findByProperty("storageName");
      storageforloop.setStorageName((StorageName) argOption.valueOf("combined_arff"));
      actors15.add(storageforloop);

      // Flow.create separate arff.get name
      Trigger trigger9 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger9.getOptionManager().findByProperty("name");
        trigger9.setName((String) argOption.valueOf("get name"));
        List<Actor> actors16 = new ArrayList<>();

        // Flow.create separate arff.get name.StorageValue
        StorageValue storagevalue5 = new StorageValue();
        argOption = (AbstractArgumentOption) storagevalue5.getOptionManager().findByProperty("storageName");
        storagevalue5.setStorageName((StorageName) argOption.valueOf("names"));
        actors16.add(storagevalue5);

        // Flow.create separate arff.get name.GetArrayElement
        GetArrayElement getarrayelement = new GetArrayElement();
        argOption = (AbstractArgumentOption) getarrayelement.getOptionManager().findByProperty("index");
        argOption.setVariable("@{index}");
        actors16.add(getarrayelement);

        // Flow.create separate arff.get name.SetVariable
        adams.flow.transformer.SetVariable setvariable8 = new adams.flow.transformer.SetVariable();
        argOption = (AbstractArgumentOption) setvariable8.getOptionManager().findByProperty("variableName");
        setvariable8.setVariableName((VariableName) argOption.valueOf("name"));
        actors16.add(setvariable8);
        trigger9.setActors(actors16.toArray(new Actor[0]));

      }
      actors15.add(trigger9);

      // Flow.create separate arff.get length
      Trigger trigger10 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger10.getOptionManager().findByProperty("name");
        trigger10.setName((String) argOption.valueOf("get length"));
        List<Actor> actors17 = new ArrayList<>();

        // Flow.create separate arff.get length.StorageValue
        StorageValue storagevalue6 = new StorageValue();
        argOption = (AbstractArgumentOption) storagevalue6.getOptionManager().findByProperty("storageName");
        storagevalue6.setStorageName((StorageName) argOption.valueOf("lengths"));
        actors17.add(storagevalue6);

        // Flow.create separate arff.get length.GetArrayElement
        GetArrayElement getarrayelement2 = new GetArrayElement();
        argOption = (AbstractArgumentOption) getarrayelement2.getOptionManager().findByProperty("index");
        argOption.setVariable("@{index}");
        actors17.add(getarrayelement2);

        // Flow.create separate arff.get length.SetVariable
        adams.flow.transformer.SetVariable setvariable9 = new adams.flow.transformer.SetVariable();
        argOption = (AbstractArgumentOption) setvariable9.getOptionManager().findByProperty("variableName");
        setvariable9.setVariableName((VariableName) argOption.valueOf("length"));
        actors17.add(setvariable9);
        trigger10.setActors(actors17.toArray(new Actor[0]));

      }
      actors15.add(trigger10);

      // Flow.create separate arff.create range
      Trigger trigger11 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger11.getOptionManager().findByProperty("name");
        trigger11.setName((String) argOption.valueOf("create range"));
        List<Actor> actors18 = new ArrayList<>();

        // Flow.create separate arff.create range.CombineVariables
        CombineVariables combinevariables = new CombineVariables();
        argOption = (AbstractArgumentOption) combinevariables.getOptionManager().findByProperty("expression");
        combinevariables.setExpression((BaseText) argOption.valueOf("1-@{length}"));
        actors18.add(combinevariables);

        // Flow.create separate arff.create range.SetVariable
        adams.flow.transformer.SetVariable setvariable10 = new adams.flow.transformer.SetVariable();
        argOption = (AbstractArgumentOption) setvariable10.getOptionManager().findByProperty("variableName");
        setvariable10.setVariableName((VariableName) argOption.valueOf("range"));
        actors18.add(setvariable10);
        trigger11.setActors(actors18.toArray(new Actor[0]));

      }
      actors15.add(trigger11);

      // Flow.create separate arff.output file
      Trigger trigger12 = new Trigger();
      {
        argOption = (AbstractArgumentOption) trigger12.getOptionManager().findByProperty("name");
        trigger12.setName((String) argOption.valueOf("output file"));
        List<Actor> actors19 = new ArrayList<>();

        // Flow.create separate arff.output file.CombineVariables
        CombineVariables combinevariables2 = new CombineVariables();
        argOption = (AbstractArgumentOption) combinevariables2.getOptionManager().findByProperty("expression");
        combinevariables2.setExpression((BaseText) argOption.valueOf("@{name}.arff"));
        actors19.add(combinevariables2);

        // Flow.create separate arff.output file.PrependDir
        PrependDir prependdir = new PrependDir();
        argOption = (AbstractArgumentOption) prependdir.getOptionManager().findByProperty("prefix");
        argOption.setVariable("@{outdir}");
        actors19.add(prependdir);

        // Flow.create separate arff.output file.SetVariable
        adams.flow.transformer.SetVariable setvariable11 = new adams.flow.transformer.SetVariable();
        argOption = (AbstractArgumentOption) setvariable11.getOptionManager().findByProperty("variableName");
        setvariable11.setVariableName((VariableName) argOption.valueOf("outfile"));
        actors19.add(setvariable11);
        trigger12.setActors(actors19.toArray(new Actor[0]));

      }
      actors15.add(trigger12);

      // save ARFF
      Tee teeSave = new Tee();
      teeSave.setName("save ARFF");
      actors15.add(teeSave);
      {
        // Flow.create separate arff.save ARFF.UpdateProperties
        UpdateProperties updateproperties = new UpdateProperties();
        {
          argOption = (AbstractArgumentOption) updateproperties.getOptionManager().findByProperty("properties");
          List<BaseString> properties = new ArrayList<BaseString>();
          properties.add((BaseString) argOption.valueOf("filter.instancesIndices"));
          updateproperties.setProperties(properties.toArray(new BaseString[0]));
          argOption = (AbstractArgumentOption) updateproperties.getOptionManager().findByProperty("variableNames");
          List<VariableName> variablenames = new ArrayList<VariableName>();
          variablenames.add((VariableName) argOption.valueOf("range"));
          updateproperties.setVariableNames(variablenames.toArray(new VariableName[0]));

          // Flow.create separate arff.save ARFF.UpdateProperties.WekaFilter
          WekaFilter wekafilter = new WekaFilter();
          RemoveRange removerange = new RemoveRange();
          removerange.setOptions(OptionUtils.splitOptions("-V -R first-last"));
          wekafilter.setFilter(removerange);

          updateproperties.setSubActor(wekafilter);

        }
        teeSave.add(updateproperties);

        // Flow.create separate arff.save ARFF.WekaRenameRelation
        WekaRenameRelation wekarenamerelation = new WekaRenameRelation();
        argOption = (AbstractArgumentOption) wekarenamerelation.getOptionManager().findByProperty("replace");
        argOption.setVariable("@{name}");
        teeSave.add(wekarenamerelation);

        // Flow.create separate arff.save ARFF.WekaFileWriter
        WekaFileWriter wekafilewriter = new WekaFileWriter();
        argOption = (AbstractArgumentOption) wekafilewriter.getOptionManager().findByProperty("outputFile");
        argOption.setVariable("@{outfile}");
        ArffSaver arffsaver = new ArffSaver();
        arffsaver.setOptions(OptionUtils.splitOptions("-decimal 6"));
        wekafilewriter.setCustomSaver(arffsaver);

        teeSave.add(wekafilewriter);
      }

      // update dataset
      SubProcess subUpdate = new SubProcess();
      subUpdate.setName("update dataset");
      actors15.add(subUpdate);
      {
        // Flow.create separate arff.update dataset.UpdateProperties
        UpdateProperties updateproperties = new UpdateProperties();
        {
          argOption = (AbstractArgumentOption) updateproperties.getOptionManager().findByProperty("properties");
          List<BaseString> properties = new ArrayList<BaseString>();
          properties.add((BaseString) argOption.valueOf("filter.instancesIndices"));
          updateproperties.setProperties(properties.toArray(new BaseString[0]));
          argOption = (AbstractArgumentOption) updateproperties.getOptionManager().findByProperty("variableNames");
          List<VariableName> variablenames = new ArrayList<VariableName>();
          variablenames.add((VariableName) argOption.valueOf("range"));
          updateproperties.setVariableNames(variablenames.toArray(new VariableName[0]));

          // Flow.create separate arff.update dataset.UpdateProperties.WekaFilter
          WekaFilter wekafilter = new WekaFilter();
          RemoveRange removerange = new RemoveRange();
          removerange.setOptions(OptionUtils.splitOptions("-R first-last"));
          wekafilter.setFilter(removerange);

          updateproperties.setSubActor(wekafilter);
        }
        subUpdate.add(updateproperties);

        // Flow.create separate arff.update dataset.SetStorageValue
        SetStorageValue setstoragevalue6 = new SetStorageValue();
        argOption = (AbstractArgumentOption) setstoragevalue6.getOptionManager().findByProperty("storageName");
        setstoragevalue6.setStorageName((StorageName) argOption.valueOf("combined_arff"));
        subUpdate.add(setstoragevalue6);
      }

      trigger8.setActors(actors15.toArray(new Actor[0]));

    }
    actors.add(trigger8);
    actor.setActors(actors.toArray(new Actor[0]));

    NullListener nulllistener = new NullListener();
    actor.setFlowExecutionListener(nulllistener);

    return actor;
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Make compatible datasets";
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
    return CATEGORY_TOOLS;
  }
}