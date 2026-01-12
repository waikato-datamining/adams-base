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
 * AppendDatasetsPanel.java
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.weka;

import adams.core.Properties;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.data.instances.Compatibility;
import adams.gui.core.BasePanel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.ParameterPanelPage;
import adams.gui.wizard.WekaSelectDatasetPage;
import adams.gui.wizard.WekaSelectMultipleDatasetsPage;
import adams.gui.wizard.WizardPane;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;

/**
 * Wizard panel that allows appending datasets (one after the other).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AppendDatasetsPanel
  extends BasePanel {

  private static final long serialVersionUID = -1965973872146968486L;

  public static final String LENIENT = "lenient";

  /** the wizard pane. */
  protected WizardPane m_Wizard;

  /** whether to close parent. */
  protected boolean m_CloseParent;

  /**
   * Initializes the panel.
   *
   * @param closeParent		whether to close parent once finished
   */
  public AppendDatasetsPanel(boolean closeParent) {
    super();
    m_CloseParent = closeParent;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    WekaSelectMultipleDatasetsPage 	inFiles;
    WekaSelectDatasetPage 		outFile;
    ParameterPanelPage			params;
    Properties 				propsParams;
    FinalPage 				finalpage;

    super.initGUI();

    // wizard
    m_Wizard = new WizardPane();
    add(m_Wizard, BorderLayout.CENTER);
    m_Wizard.setCustomFinishText("Append");

    inFiles = new WekaSelectMultipleDatasetsPage("Input");
    inFiles.setDescription("Select the Weka datasets to append (one-after-the-other).\nYou have to choose at least two.");
    inFiles.setPageCheck(new PageCheck() {
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
    m_Wizard.addPage(inFiles);

    params = new ParameterPanelPage("Parameters");
    params.getParameterPanel().addPropertyType(LENIENT, PropertyType.BOOLEAN);
    params.getParameterPanel().setLabel(LENIENT, "Lenient");
    params.getParameterPanel().setHelp(LENIENT, "If enabled, the attribute names don't have to match.");
    params.setDescription("Additional parameter settings for appending the datasets.");
    propsParams = new Properties();
    propsParams.setBoolean(LENIENT, false);
    params.setProperties(propsParams);
    m_Wizard.addPage(params);

    outFile = new WekaSelectDatasetPage("Output");
    outFile.setDescription("Select the file to save the combined data to.");
    outFile.setUseSaveDialog(true);
    m_Wizard.addPage(outFile);

    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Ready</h2>Please click on <b>Append</b> to start the process.</html>");
    m_Wizard.addPage(finalpage);

    m_Wizard.addActionListener((ActionEvent e) -> {
      if (!e.getActionCommand().equals(WizardPane.ACTION_FINISH)) {
        if (m_CloseParent)
	  closeParent();
        return;
      }
      Properties props = m_Wizard.getProperties(false);
      File[] input = null;
      File output = null;
      boolean lenient = props.getBoolean(LENIENT, false);
      try {
        String[] files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
        input = new File[files.length];
        for (int i = 0; i < files.length; i++)
          input[i] = new File(files[i]);
        output = new File(props.getProperty(WekaSelectDatasetPage.KEY_FILE));
      }
      catch (Exception ex) {
        GUIHelper.showErrorMessage(
          getParent(), "Failed to get setup from wizard!\n" + LoggingHelper.throwableToString(ex));
        return;
      }
      doAppend(input, lenient, output);
    });
    m_Wizard.update();
  }

  /**
   * Performs the append operation.
   *
   * @param input       the files to merge
   * @param lenient 	whether to be lenient or strict with the header comparison
   * @param output      the output file
   */
  protected void doAppend(File[] input, boolean lenient, File output) {
    Instances[]		data;
    Instances		full;
    int			i;
    int			n;
    AbstractFileLoader 	loader;
    DataSink 		sink;
    int			count;
    TIntArrayList 	transferAtt;
    int			index;

    if (input.length < 2) {
      GUIHelper.showErrorMessage(getParent(), "At least two files are required!");
      return;
    }

    // load and check compatibility
    loader      = ConverterUtils.getLoaderForFile(input[0]);
    data        = new Instances[input.length];
    count       = 0;
    transferAtt = new TIntArrayList();
    for (i = 0; i < input.length; i++) {
      try {
        loader.setFile(input[i]);
        data[i] = DataSource.read(loader);
        if (i > 0) {
	  if (Compatibility.isCompatible(data[0], data[i], !lenient) != null) {
            GUIHelper.showErrorMessage(
              getParent(),
	      "Dataset #1/'" + input[0] + "'\n"
		+ "and #" + (i+1) + "/'" + input[i] + "'\n"
		+ "are not compatible!\n"
                + Compatibility.isCompatible(data[0], data[i], !lenient));
            return;
          }
        }
        else {
          for (n = 0; n < data[0].numAttributes(); n++) {
            if (data[0].attribute(n).isString() || data[0].attribute(n).isRelationValued())
              transferAtt.add(n);
          }
        }
        count += data[i].numInstances();
      }
      catch (Exception e) {
        GUIHelper.showErrorMessage(
          getParent(), "Failed to read '" + input[i] + "'!\n" + LoggingHelper.throwableToString(e));
        return;
      }
    }

    // combine
    full = new Instances(data[0], count);
    for (i = 0; i < data.length; i++) {
      for (Instance inst: data[i]) {
        if (!transferAtt.isEmpty()) {
          for (n = 0; n < transferAtt.size(); n++) {
            index = transferAtt.get(n);
            if (inst.attribute(index).isString())
              full.attribute(index).addStringValue(inst.stringValue(index));
            else if (inst.attribute(n).isRelationValued())
              full.attribute(index).addRelation(inst.relationalValue(index));
            else
              throw new IllegalStateException(
                "Unhandled attribute type: " + Attribute.typeToString(inst.attribute(index)));
          }
        }
        full.add(inst);
      }
    }

    // save
    try {
      sink = new DataSink(output.getAbsolutePath());
      sink.write(full);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
        getParent(), "Failed to save data to '" + output + "'!\n" + LoggingHelper.throwableToString(e));
      return;
    }

    GUIHelper.showInformationMessage(null, "Successfully appended!\n" + output);
    if (m_CloseParent)
      closeParent();
  }
}
