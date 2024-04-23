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
 * ChangeExternalActorFile.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.quickaction;

import adams.core.io.FlowFile;
import adams.core.io.PlaceholderFile;
import adams.flow.core.AbstractBaseExternalActor;
import adams.gui.chooser.DirectoryChooserFactory;
import adams.gui.chooser.FileChooser;
import adams.gui.chooser.FlowFileChooser;
import adams.gui.core.GUIHelper;

import java.awt.event.ActionEvent;

/**
 * Lets the user update the external actor file.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeExternalActorFile
  extends AbstractTreeQuickMenuItemAction {

  private static final long serialVersionUID = -6455846796708144253L;

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  @Override
  protected String getTitle() {
    return "Change external actor file...";
  }

  /**
   * Performs the actual update of the state of the action.
   */
  @Override
  protected void doUpdate() {
    setEnabled(m_State.editable && m_State.isSingleSel && (m_State.selNode.getActor() instanceof AbstractBaseExternalActor));
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    AbstractBaseExternalActor 	extActOld;
    AbstractBaseExternalActor 	extActNew;
    FileChooser			chooser;
    int				retVal;
    PlaceholderFile 		oldFile;
    PlaceholderFile 		newFile;

    extActOld   = (AbstractBaseExternalActor) m_State.selNode.getActor();
    oldFile     = extActOld.getActorFile();
    chooser     = new FlowFileChooser();
    chooser.setSelectedFile(oldFile);
    retVal      = chooser.showOpenDialog(GUIHelper.getParentComponent(m_State.tree));
    if (retVal != DirectoryChooserFactory.APPROVE_OPTION)
      return;
    newFile = new PlaceholderFile(chooser.getSelectedFile());
    if (oldFile.equals(newFile))
      return;

    addUndoPoint("Changed actor file for: " + extActOld.getName());
    extActNew = (AbstractBaseExternalActor) extActOld.shallowCopy();
    extActNew.setActorFile(new FlowFile(newFile));
    updateSelectedActor(extActNew);
  }
}
