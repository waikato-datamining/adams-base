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
 * TextDirectory.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.source;

import adams.core.ObjectCopyHelper;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.tools.wekainvestigator.data.TextDirectoryLoaderContainer;
import adams.gui.tools.wekainvestigator.job.InvestigatorJob;
import weka.core.converters.Loader;
import weka.core.converters.TextDirectoryLoader;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;

/**
 * Uses the TextDirectoryLoader to load text documents.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextDirectory
  extends AbstractSource {

  private static final long serialVersionUID = 5646388990155938153L;

  /** the last config. */
  protected TextDirectoryLoader m_Loader;

  /**
   * Instantiates the action.
   */
  public TextDirectory() {
    super();
    setName("Text directory...");
    setIcon("editor.gif");
  }

  /**
   * Invoked when an action occurs.
   *
   * @param e		the event
   */
  @Override
  protected void doActionPerformed(ActionEvent e) {
    GenericObjectEditorDialog	dialog;
    InvestigatorJob 		job;

    if (m_Loader == null)
      m_Loader = new TextDirectoryLoader();

    if (getOwner().getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getOwner().getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getOwner().getParentFrame(), true);
    dialog.setTitle("Text directory loader");
    dialog.setUISettingsPrefix(Loader.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.getGOEEditor().setClassType(Loader.class);
    dialog.setCurrent(m_Loader);
    dialog.pack();
    dialog.setLocationRelativeTo(getOwner());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      dialog.dispose();
      logMessage("Text loading cancelled!");
      return;
    }
    m_Loader = (TextDirectoryLoader) dialog.getCurrent();
    dialog.dispose();

    job = new InvestigatorJob(getOwner(), "Loading text data with " + dialog.getCurrent().getClass().getSimpleName()) {
      @Override
      protected void doRun() {
	addData(new TextDirectoryLoaderContainer(ObjectCopyHelper.copyObject(m_Loader)));
      }
    };
    getOwner().startExecution(job);
  }
}
