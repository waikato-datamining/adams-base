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

import adams.core.Utils;
import adams.flow.core.Token;
import adams.flow.transformer.WekaInstancesMerge;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.ConverterFileChooser;

import java.awt.Dialog.ModalityType;
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
    ConverterFileChooser        filechooser;
    int                         retVal;
    File[]                      input;
    File                        output;
    Instances[]                 data;
    Instances                   full;
    int                         i;
    AbstractFileLoader          loader;
    AbstractFileSaver           saver;
    WekaInstancesMerge          merge;
    GenericObjectEditorDialog   dialog;
    Token                       token;
    String                      msg;

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

    // configuration
    merge = new WekaInstancesMerge();
    dialog = new GenericObjectEditorDialog(null, ModalityType.APPLICATION_MODAL);
    dialog.setTitle("Merge setup");
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.getGOEEditor().setClassType(WekaInstancesMerge.class);
    dialog.setCurrent(merge);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    merge = (WekaInstancesMerge) dialog.getCurrent();

    // save
    retVal = filechooser.showSaveDialog(null);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;
    output = filechooser.getSelectedFile();
    saver  = filechooser.getSaver();

    // load and check compatibility
    data = new Instances[input.length];
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