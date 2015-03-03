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

/**
 * ImportDialog.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import java.awt.Dialog;
import java.awt.Frame;

import adams.core.option.NestedConsumer;
import adams.core.option.OptionConsumer;
import adams.gui.dialog.AbstractFileImportDialog;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * A dialog for importing a flow from various formats.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImportDialog
  extends AbstractFileImportDialog<OptionConsumer> {

  /** for serialization. */
  private static final long serialVersionUID = 201725070566669323L;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public ImportDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public ImportDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public ImportDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public ImportDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }
  
  /**
   * Returns the default title for the dialog.
   */
  @Override
  protected String getDefaultTitle() {
    return "Import flow";
  }
  
  /**
   * Creates the GOE panel to use.
   */
  @Override
  protected GenericObjectEditorPanel createGOE() {
    return new GenericObjectEditorPanel(OptionConsumer.class, new NestedConsumer(), true);
  }
}
