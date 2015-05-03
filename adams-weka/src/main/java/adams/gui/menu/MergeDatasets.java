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
 * MergeDatasets.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.transformer.WekaInstancesMerge;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.ChildFrame;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import adams.gui.wizard.AbstractWizardPage;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PageCheck;
import adams.gui.wizard.PropertySheetPanelPage;
import adams.gui.wizard.WekaPropertySheetPanelPage;
import adams.gui.wizard.WekaSelectDatasetPage;
import adams.gui.wizard.WekaSelectMultipleDatasetsPage;
import adams.gui.wizard.WizardPane;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.converters.ConverterUtils.DataSource;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * For merging datasets (side-by-side) into single dataset.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MergeDatasets
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;

  /**
   * Initializes the menu item with no owner.
   */
  public MergeDatasets() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public MergeDatasets(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "merge.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    final WizardPane                wizard;
    WekaSelectMultipleDatasetsPage  infiles;
    PropertySheetPanelPage          goe;
    WekaSelectDatasetPage           outfile;
    FinalPage                       finalpage;
    WekaInstancesMerge              merge;
    final ChildFrame                frame;

    // configuration
    merge = new WekaInstancesMerge();

    // wizard
    wizard = new WizardPane();
    wizard.setCustomFinishText("Merge");
    infiles = new WekaSelectMultipleDatasetsPage("Input");
    infiles.setDescription("Select the Weka datasets to merge (side-by-side).\nYou have to choose at least two.");
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
    goe = new PropertySheetPanelPage("Setup");
    goe.setDescription("Specify how the files should get merged.");
    goe.setTarget(merge);
    wizard.addPage(goe);
    outfile = new WekaSelectDatasetPage("Output");
    outfile.setDescription("Select the file to save the merged data to.");
    outfile.setUseSaveDialog(true);
    wizard.addPage(outfile);
    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Ready</h2>Please click on <b>Merge</b> to start the process.</html>");
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
        File[] input = null;
        File output = null;
        WekaInstancesMerge merge = null;
        try {
          String[] files = OptionUtils.splitOptions(props.getProperty(WekaSelectMultipleDatasetsPage.KEY_FILES));
          input = new File[files.length];
          for (int i = 0; i < files.length; i++)
            input[i] = new File(files[i]);
          merge = (WekaInstancesMerge) OptionUtils.forCommandLine(WekaInstancesMerge.class, props.getProperty(WekaPropertySheetPanelPage.PROPERTY_CMDLINE));
          output = new File(props.getProperty(WekaSelectDatasetPage.KEY_FILE));
        }
        catch (Exception ex) {
          GUIHelper.showErrorMessage(
            getOwner(), "Failed to get setup from wizard!\n" + Utils.throwableToString(ex));
          return;
        }
        doMerge(frame, input, merge, output);
      }
    });
  }

  /**
   * Performs the merge.
   *
   * @param frame       the frame to close
   * @param input       the files to merge
   * @param merge       the merge setup
   * @param output      the output file
   */
  protected void doMerge(ChildFrame frame, File[] input, WekaInstancesMerge merge, File output) {
    Instances[]                 data;
    Instances                   full;
    int                         i;
    AbstractFileLoader          loader;
    DataSink                    sink;
    Token                       token;
    String                      msg;

    if (input.length < 2) {
      GUIHelper.showErrorMessage(getOwner(), "At least two files are required!");
      return;
    }

    // load and check compatibility
    loader = ConverterUtils.getLoaderForFile(input[0]);
    data   = new Instances[input.length];
    for (i = 0; i < input.length; i++) {
      try {
        loader.setFile(input[i]);
        data[i] = DataSource.read(loader);
      }
      catch (Exception e) {
        GUIHelper.showErrorMessage(
          getOwner(), "Failed to read '" + input[i] + "'!\n" + Utils.throwableToString(e));
        return;
      }
    }

    // merge
    msg = merge.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(
        getOwner(), "Failed to set up merge actor!\n" + msg);
      return;
    }
    token = new Token(data);
    merge.input(token);
    msg = merge.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(
        getOwner(), "Failed to merge files!\n" + Utils.flatten(input, "\n") + "\n\n" + msg);
      return;
    }
    token = merge.output();
    full  = (Instances) token.getPayload();

    // save
    try {
      sink = new DataSink(output.getAbsolutePath());
      sink.write(full);
    }
    catch (Exception e) {
        GUIHelper.showErrorMessage(
          getOwner(), "Failed to save data to '" + output + "'!\n" + Utils.throwableToString(e));
        return;
    }

    GUIHelper.showInformationMessage(null, "Successfully merged!\n" + output);
    frame.dispose();
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Merge datasets";
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