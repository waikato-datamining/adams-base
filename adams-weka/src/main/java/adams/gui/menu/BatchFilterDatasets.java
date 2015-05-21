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
 * BatchFilterDatasets.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.goe.WekaGenericObjectEditorPanel;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.ParameterPanelPage;
import adams.gui.wizard.WekaSelectDatasetPage;
import adams.gui.wizard.WekaSelectMultipleDatasetsPage;
import adams.gui.wizard.WizardPane;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.AllFilter;
import weka.filters.Filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * For batch filtering datasets using a single filter setup (files get output
 * into a different directory).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BatchFilterDatasets
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;

  /**
   * Initializes the menu item with no owner.
   */
  public BatchFilterDatasets() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public BatchFilterDatasets(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "filter.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    final WizardPane			wizard;
    WekaSelectMultipleDatasetsPage	infiles;
    ParameterPanelPage 			paramsFilter;
    ParameterPanelPage 			paramsOutput;
    Properties				props;
    WekaSelectDatasetPage		outfile;
    FinalPage				finalpage;
    Filter 				filter;
    final ChildFrame			frame;

    // configuration
    filter = new AllFilter();

    // wizard
    wizard = new WizardPane();
    wizard.setCustomFinishText("Filter");

    infiles = new WekaSelectMultipleDatasetsPage("Input");
    infiles.setDescription(
      "Select the Weka datasets to batch-filter.\n"
	+ "You have to choose at least two.\n"
	+ "The first dataset is used to set up the filter, all subsequent files get filtered with this set up.");
    infiles.setPageCheck(new PageCheck() {
      @Override
      public boolean checkPage(AbstractWizardPage page) {
	Properties props = page.getProperties();
	try {
	  String[] files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
	  return (files.length >= 2);
	}
	catch (Exception e) {
	  System.err.println("Failed to obtain files:");
	  e.printStackTrace();
	}
	return false;
      }
    });
    wizard.addPage(infiles);

    paramsFilter = new ParameterPanelPage("Filter");
    paramsFilter.setDescription(
      "Set up the filter that is used to filter the datasets.\n"
	+ "If no class attribute is to be set, simply empty the 'Class' property. "
	+ "You can use 'first' and 'last' as well as 1-based indices.");
    paramsFilter.getParameterPanel().addPropertyType("Filter", PropertyType.OBJECT_EDITOR);
    paramsFilter.getParameterPanel().setChooser("Filter", new WekaGenericObjectEditorPanel(weka.filters.Filter.class, filter, true));
    paramsFilter.getParameterPanel().addPropertyType("Class", PropertyType.STRING);
    paramsFilter.getParameterPanel().addPropertyType("Keep relation name", PropertyType.BOOLEAN);
    paramsFilter.getParameterPanel().setPropertyOrder(new String[]{"Filter", "Class", "Keep relation name"});
    props = new Properties();
    props.setProperty("Filter", OptionUtils.getCommandLine(filter));
    props.setProperty("Class", "last");
    props.setBoolean("Keep relation name", false);
    paramsFilter.getParameterPanel().setProperties(props);
    wizard.addPage(paramsFilter);

    paramsOutput = new ParameterPanelPage("Output");
    paramsOutput.setDescription("Select the directory where to place the generated datasets in ARFF format (the input file names get reused for the output).");
    paramsOutput.getParameterPanel().addPropertyType("Output", PropertyType.DIRECTORY_ABSOLUTE);
    props = new Properties();
    props.setPath("Output", ".");
    paramsOutput.getParameterPanel().setProperties(props);
    wizard.addPage(paramsOutput);

    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Ready</h2>Please click on <b>Filter</b> to start the process.</html>");
    wizard.addPage(finalpage);
    frame = createChildFrame(wizard, 800, 600);
    wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!e.getActionCommand().equals(WizardPane.ACTION_FINISH)) {
          frame.dispose();
          return;
        }
        Properties props = wizard.getProperties(false);
	String[] files = null;
	Filter filter = null;
	String classIndex = null;
	File outdir = null;
	boolean keep = false;
        try {
          files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
	  filter = (Filter) OptionUtils.forAnyCommandLine(Filter.class, props.getProperty("Filter"));
	  classIndex = props.getProperty("Class");
	  outdir = new File(props.getPath("Output"));
	  keep = props.getBoolean("Keep relation name");
        }
        catch (Exception ex) {
          GUIHelper.showErrorMessage(
            getOwner(), "Failed to get setup from wizard!\n" + Utils.throwableToString(ex));
          return;
        }
        batchFilter(frame, files, filter, classIndex, keep, outdir);
      }
    });
  }

  /**
   * Performs the batch filtering.
   *
   * @param frame       the frame to close
   * @param input       the files to filter
   * @param filter      the filter setup
   * @param classIndex	the class index, empty for no class
   * @param keep	whether to keep the relation name
   * @param outdir      the output directory
   */
  protected void batchFilter(ChildFrame frame, String[] input, Filter filter, String classIndex, boolean keep, File outdir) {
    Instances[]                 data;
    int                         i;
    AbstractFileLoader          loader;
    Instances			filtered;
    int				clsIndex;
    String			outfile;
    StringBuilder		outfiles;

    if (input.length < 2) {
      GUIHelper.showErrorMessage(getOwner(), "At least two files are required!");
      return;
    }

    // load and check compatibility
    loader = ConverterUtils.getLoaderForFile(input[0]);
    data   = new Instances[input.length];
    for (i = 0; i < input.length; i++) {
      try {
        loader.setFile(new File(input[i]));
        data[i] = DataSource.read(loader);
      }
      catch (Exception e) {
        GUIHelper.showErrorMessage(
          getOwner(),
	  "Failed to read '" + input[i] + "'!\n" + Utils.throwableToString(e));
        return;
      }
    }

    // class index
    if (classIndex.isEmpty()) {
      clsIndex = -1;
    }
    else {
      try {
	if (classIndex.equalsIgnoreCase("first"))
	  clsIndex = 0;
	else if (classIndex.equalsIgnoreCase("last"))
	  clsIndex = data[0].numAttributes() - 1;
	else
	  clsIndex = Integer.parseInt(classIndex) - 1;
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(
	  getOwner(),
	  "Failed to parse class attribute index: " + classIndex + "\n"
	    + Utils.throwableToString(e));
	return;
      }
    }

    // filter
    outfiles = new StringBuilder();
    for (i = 0; i < input.length; i++) {
      try {
	outfile = outdir.getAbsolutePath() + File.separator + new File(input[i]).getName();
	data[i].setClassIndex(clsIndex);
	if (i == 0)
	  filter.setInputFormat(data[i]);
	filtered = Filter.useFilter(data[i], filter);
	if (keep)
	  filtered.setRelationName(data[i].relationName());
	DataSink.write(outfile, filtered);
	if (outfiles.length() > 0)
	  outfiles.append("\n");
	outfiles.append(outfile);
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(
	  getOwner(),
	  "Failed to filter dataset #" + (i+1) + " ('" + input[i] + "')!\n"
	    + Utils.throwableToString(e));
	return;
      }
    }

    GUIHelper.showInformationMessage(null, "Successfully filtered!\n" + outfiles);
    frame.dispose();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Batch-filter datasets";
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
    return CATEGORY_MACHINELEARNING;
  }
}