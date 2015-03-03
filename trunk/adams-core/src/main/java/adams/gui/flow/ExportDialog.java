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
 * ExportDialog.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow;

import java.awt.Dialog;
import java.awt.Frame;

import adams.core.option.NestedProducer;
import adams.core.option.OptionProducer;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.dialog.AbstractFileExportDialog;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * A dialog for exporting a flow into various formats.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExportDialog
  extends AbstractFileExportDialog<OptionProducer> {

  /** for serialization. */
  private static final long serialVersionUID = 201725070566669323L;

  /** the GOE panel for the export format. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the panel with the file. */
  protected FileChooserPanel m_PanelFile;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public ExportDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public ExportDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public ExportDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public ExportDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }
  
  /**
   * Returns the default title for the dialog.
   */
  @Override
  protected String getDefaultTitle() {
    return "Export flow";
  }
  
  /**
   * Creates the GOE panel to use.
   */
  @Override
  protected GenericObjectEditorPanel createGOE() {
    return new GenericObjectEditorPanel(OptionProducer.class, new NestedProducer(), true);
  }
}
