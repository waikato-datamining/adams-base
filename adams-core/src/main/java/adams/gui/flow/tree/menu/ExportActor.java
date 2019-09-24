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
 * ExportActor.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */
package adams.gui.flow.tree.menu;

import adams.core.Properties;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.ApplyActorProducer;
import adams.core.option.OptionProducer;
import adams.gui.core.GUIHelper;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.PropertiesParameterDialog;
import adams.gui.goe.GenericObjectEditorPanel;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * For exporting an actor, eg to Java code.
 * 
 * @author fracpete
 */
public class ExportActor
  extends AbstractTreePopupMenuItemAction {

  /** for serialization. */
  private static final long serialVersionUID = 3991575839421394939L;

  public static final String KEY_EXPORTER = "exporter";

  public static final String KEY_OUTPUT = "output";

  /**
   * Returns the caption of this action.
   * 
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Export...";
  }

  /**
   * Updates the action using the current state information.
   */
  @Override
  protected void doUpdate() {
    setEnabled((m_State.numSel == 1) && (m_State.tree.getOwner() != null));
  }

  /**
   * The action to execute.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    PropertiesParameterDialog 	dialog;
    PropertiesParameterPanel	panel;
    Properties			props;
    OptionProducer		producer;
    PlaceholderFile 		outputFile;
    Object			output;
    String			msg;

    if ((m_State.tree != null) && (m_State.tree.getParentDialog() != null))
      dialog = new PropertiesParameterDialog(m_State.tree.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else if ((m_State.tree != null) && (m_State.tree.getParentFrame() != null))
      dialog = new PropertiesParameterDialog(m_State.tree.getParentFrame(), true);
    else
      dialog = new PropertiesParameterDialog(null, ModalityType.DOCUMENT_MODAL);
    dialog.setTitle("Export [" + m_State.selNode.getFullName() + "]");

    panel = dialog.getPropertiesParameterPanel();
    panel.addPropertyType(KEY_EXPORTER, PropertyType.OBJECT_EDITOR);
    panel.setLabel(KEY_EXPORTER, "Exporter");
    panel.setChooser(KEY_EXPORTER, new GenericObjectEditorPanel(OptionProducer.class, new ApplyActorProducer(), true));
    panel.addPropertyType(KEY_OUTPUT, PropertyType.FILE_ABSOLUTE);
    panel.setPropertyOrder(new String[]{KEY_EXPORTER, KEY_OUTPUT});
    props = new Properties();
    props.setObject(KEY_EXPORTER, new ApplyActorProducer());
    props.setProperty(KEY_OUTPUT, new PlaceholderFile().getAbsolutePath());
    panel.setProperties(props);
    dialog.pack();
    dialog.setLocationRelativeTo(m_State.tree);
    dialog.setVisible(true);
    if (dialog.getOption() != PropertiesParameterDialog.APPROVE_OPTION)
      return;

    props      = panel.getProperties();
    producer   = props.getObject(KEY_EXPORTER, OptionProducer.class);
    outputFile = new PlaceholderFile(props.getProperty(KEY_OUTPUT));
    output     = producer.produce(m_State.selNode.getFullActor());
    if (output != null)
      msg = FileUtils.writeToFileMsg(outputFile.getAbsolutePath(), output, false, null);
    else
      msg = "No output generated!";
    if (msg != null)
      GUIHelper.showErrorMessage(
        m_State.tree.getParent(), msg);
    else
      GUIHelper.showInformationMessage(
        m_State.tree.getParent(), "Exported " + m_State.selNode.getFullName() + " " + "to " + outputFile.getAbsolutePath());
  }
}
