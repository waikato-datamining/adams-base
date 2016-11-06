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
 * SftpDirectoryChooserPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.io.fileoperations.FileOperations;
import adams.core.io.fileoperations.SftpFileOperations;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.SftpDirectoryLister;
import adams.core.net.SSHSessionProvider;
import adams.core.option.OptionUtils;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;

import java.awt.Dialog.ModalityType;

/**
 * Chooser for remote directories (via SFTP).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SftpDirectoryChooserPanel
  extends AbstractChooserPanelWithIOSupport<SftpRemoteDirectorySetup> {

  /** for serialization. */
  private static final long serialVersionUID = 6235369491956122980L;

  /** the session provider. */
  protected SSHSessionProvider m_Provider;

  /**
   * Initializes the panel with no file.
   */
  public SftpDirectoryChooserPanel() {
    super();
    setCurrent(new SftpRemoteDirectorySetup());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    reset();
  }

  /**
   * Resets some members.
   */
  protected void reset() {
    m_Provider = null;
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		if successfully set
   */
  @Override
  public boolean setCurrent(SftpRemoteDirectorySetup value) {
    boolean	result;

    result = super.setCurrent(value);
    if (result)
      reset();

    return result;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected SftpRemoteDirectorySetup doChoose() {
    SftpRemoteDirectorySetup 	currentSetup;
    SftpRemoteDirectorySetup 	newSetup;
    GenericObjectEditorDialog	dialog;

    currentSetup = getCurrent();
    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Remote directory");
    dialog.getGOEEditor().setClassType(SftpRemoteDirectorySetup.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.setCurrent(currentSetup);
    dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return null;
    newSetup = (SftpRemoteDirectorySetup) dialog.getCurrent();
    if (!newSetup.toCommandLine().equals(currentSetup.toCommandLine()))
      reset();

    return newSetup;
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  protected String toString(SftpRemoteDirectorySetup value) {
    return value.toCommandLine();
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  protected SftpRemoteDirectorySetup fromString(String value) {
    try {
      return (SftpRemoteDirectorySetup) OptionUtils.forAnyCommandLine(SftpRemoteDirectorySetup.class, value);
    }
    catch (Exception e) {
      return new SftpRemoteDirectorySetup();
    }
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(String value) {
    SftpRemoteDirectorySetup current;

    current = getCurrent();
    current.setRemoteDir(value);

    setCurrent(current);
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  public String getCurrentDirectory() {
    return getCurrent().getRemoteDir();
  }

  /**
   * Returns the type of chooser (description).
   *
   * @return		the type
   */
  public String getChooserType() {
    return "SFTP";
  }

  protected synchronized SSHSessionProvider getProvider() {
    if ((m_Provider == null) || (m_Provider.getSession() == null))
      m_Provider = getCurrent();
    return m_Provider;
  }

  /**
   * Returns the directory lister.
   *
   * @return		the lister
   */
  public DirectoryLister getDirectoryLister() {
    SftpDirectoryLister   	result;

    result = new SftpDirectoryLister();
    result.setSessionProvider(getProvider());
    result.setWatchDir(getCurrentDirectory());

    return result;
  }

  /**
   * Returns the file operations.
   *
   * @return		the operations
   */
  @Override
  public FileOperations getFileOperations() {
    SftpFileOperations	result;

    result = new SftpFileOperations();
    result.setProvider(getProvider());

    return result;
  }
}
