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
 * AppendDatasets.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Utils;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.ConverterFileChooser;

import java.io.File;

/**
 * For appending datasets into single dataset.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AppendDatasets
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = 7586443345167287461L;

  /**
   * Initializes the menu item with no owner.
   */
  public AppendDatasets() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public AppendDatasets(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "append.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    ConverterFileChooser    filechooser;
    int                     retVal;
    File[]                  input;
    File                    output;
    Instances[]             data;
    int                     i;
    int                     count;
    AbstractFileLoader      loader;
    AbstractFileSaver       saver;

    // choose files
    filechooser = new ConverterFileChooser();
    filechooser.setMultiSelectionEnabled(true);
    retVal = filechooser.showOpenDialog(null);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;
    input  = filechooser.getSelectedFiles();
    loader = filechooser.getLoader();
    if (input.length < 2) {
      GUIHelper.showErrorMessage(getOwner(), "At least two files are required!");
      return;
    }

    // save
    retVal = filechooser.showSaveDialog(null);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;
    output = filechooser.getSelectedFile();
    saver  = filechooser.getSaver();

    // load and check compatibility
    data  = new Instances[input.length];
    count = 0;
    for (i = 0; i < input.length; i++) {
      try {
        loader.setFile(input[i]);
        data[i] = DataSource.read(loader);
        if (i > 0) {
          if (!data[0].equalHeaders(data[i])) {
            GUIHelper.showErrorMessage(
              getOwner(), "Datasets '" + input[0] + "' and '" + input[i] + "' are not compatible!\n"
                + data[0].equalHeadersMsg(data[i]));
            return;
          }
        }
        count += data[i].numInstances();
      }
      catch (Exception e) {
        GUIHelper.showErrorMessage(
          getOwner(), "Failed to read '" + input[i] + "'!\n" + Utils.throwableToString(e));
        return;
      }
    }

    // combine
    Instances full = new Instances(data[0], count);
    for (i = 0; i < data.length; i++) {
      for (Instance inst: data[i])
        full.add(inst);
    }

    // save
    try {
      saver.setFile(output);
      saver.setInstances(full);
      saver.writeBatch();
    }
    catch (Exception e) {
        GUIHelper.showErrorMessage(
          getOwner(), "Failed to save data to '" + output + "'!\n" + Utils.throwableToString(e));
        return;
    }

    GUIHelper.showInformationMessage(null, "Successfully merged!\n" + output);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Append datasets";
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