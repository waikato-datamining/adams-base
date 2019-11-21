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
 * BatchFilterDatasetsPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.weka;

import adams.core.Properties;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.gui.core.BasePanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.ParameterPanelPage;
import adams.gui.wizard.WekaSelectMultipleDatasetsPage;
import adams.gui.wizard.WizardPane;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.AllFilter;
import weka.filters.Filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;

/**
 * Wizard panel that allows appending datasets (one after the other).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BatchFilterDatasetsPanel
  extends BasePanel {

  private static final long serialVersionUID = -1965973872146968486L;

  /** the wizard pane. */
  protected WizardPane m_Wizard;

  /** whether to close parent. */
  protected boolean m_CloseParent;

  /**
   * Initializes the panel.
   *
   * @param closeParent		whether to close parent once finished
   */
  public BatchFilterDatasetsPanel(boolean closeParent) {
    super();
    m_CloseParent = closeParent;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    WekaSelectMultipleDatasetsPage	infiles;
    ParameterPanelPage 			paramsFilter;
    ParameterPanelPage 			paramsOutput;
    Properties 				props;
    FinalPage				finalpage;
    Filter 				filter;

    super.initGUI();

    // configuration
    filter = new AllFilter();

    // wizard
    m_Wizard = new WizardPane();
    add(m_Wizard, BorderLayout.CENTER);
    m_Wizard.setCustomFinishText("Filter");

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
          ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to obtain files:", e);
	}
	return false;
      }
    });
    m_Wizard.addPage(infiles);

    paramsFilter = new ParameterPanelPage("Filter");
    paramsFilter.setDescription(
      "Set up the filter that is used to filter the datasets.\n"
	+ "If no class attribute is to be set, simply empty the 'Class' property. "
	+ "You can use 'first' and 'last' as well as 1-based indices.");
    paramsFilter.getParameterPanel().addPropertyType("Filter", PropertyType.OBJECT_EDITOR);
    paramsFilter.getParameterPanel().setChooser("Filter", new GenericObjectEditorPanel(weka.filters.Filter.class, filter, true));
    paramsFilter.getParameterPanel().addPropertyType("Class", PropertyType.STRING);
    paramsFilter.getParameterPanel().addPropertyType("Keep relation name", PropertyType.BOOLEAN);
    paramsFilter.getParameterPanel().setPropertyOrder(new String[]{"Filter", "Class", "Keep relation name"});
    props = new Properties();
    props.setProperty("Filter", OptionUtils.getCommandLine(filter));
    props.setProperty("Class", "last");
    props.setBoolean("Keep relation name", false);
    paramsFilter.getParameterPanel().setProperties(props);
    m_Wizard.addPage(paramsFilter);

    paramsOutput = new ParameterPanelPage("Output");
    paramsOutput.setDescription("Select the directory where to place the generated datasets in ARFF format (the input file names get reused for the output).");
    paramsOutput.getParameterPanel().addPropertyType("Output", PropertyType.DIRECTORY_ABSOLUTE);
    props = new Properties();
    props.setPath("Output", ".");
    paramsOutput.getParameterPanel().setProperties(props);
    m_Wizard.addPage(paramsOutput);

    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Ready</h2>Please click on <b>Filter</b> to start the process.</html>");
    m_Wizard.addPage(finalpage);
    m_Wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!e.getActionCommand().equals(WizardPane.ACTION_FINISH)) {
	  if (m_CloseParent)
	    closeParent();
          return;
        }
        Properties props = m_Wizard.getProperties(false);
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
            getParent(), "Failed to get setup from wizard!\n" + LoggingHelper.throwableToString(ex));
          return;
        }
        batchFilter(files, filter, classIndex, keep, outdir);
      }
    });
  }

  /**
   * Performs the batch filtering.
   *
   * @param input       the files to filter
   * @param filter      the filter setup
   * @param classIndex	the class index, empty for no class
   * @param keep	whether to keep the relation name
   * @param outdir      the output directory
   */
  protected void batchFilter(String[] input, Filter filter, String classIndex, boolean keep, File outdir) {
    Instances[]                 data;
    int                         i;
    AbstractFileLoader loader;
    Instances			filtered;
    int				clsIndex;
    String			outfile;
    StringBuilder		outfiles;

    if (input.length < 2) {
      GUIHelper.showErrorMessage(getParent(), "At least two files are required!");
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
          getParent(),
	  "Failed to read '" + input[i] + "'!\n" + LoggingHelper.throwableToString(e));
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
	  getParent(),
	  "Failed to parse class attribute index: " + classIndex + "\n"
	    + LoggingHelper.throwableToString(e));
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
	  getParent(),
	  "Failed to filter dataset #" + (i+1) + " ('" + input[i] + "')!\n"
	    + LoggingHelper.throwableToString(e));
	return;
      }
    }

    GUIHelper.showInformationMessage(null, "Successfully filtered!\n" + outfiles);
    if (m_CloseParent)
      closeParent();
  }
}
