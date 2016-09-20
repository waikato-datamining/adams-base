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
 * FtpDirectoryChooserPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.io.fileoperations.FileOperations;
import adams.core.io.fileoperations.FtpFileOperations;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.FtpDirectoryLister;
import adams.core.option.OptionUtils;
import adams.gui.goe.GenericObjectEditorDialog;
import org.apache.commons.net.ftp.FTPClient;

import java.awt.Dialog.ModalityType;

/**
 * Chooser for remote directories (via SFTP).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FtpDirectoryChooserPanel
  extends AbstractChooserPanelWithIOSupport<FtpRemoteDirectorySetup> {

  /** for serialization. */
  private static final long serialVersionUID = 6235369491956122980L;

  /** the current client. */
  protected FTPClient m_Client;

  /**
   * Initializes the panel with no file.
   */
  public FtpDirectoryChooserPanel() {
    super();
    setCurrent(new FtpRemoteDirectorySetup());
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
   * Resets members.
   */
  protected void reset() {
    m_Client = null;
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		if successfully set
   */
  @Override
  public boolean setCurrent(FtpRemoteDirectorySetup value) {
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
  protected FtpRemoteDirectorySetup doChoose() {
    FtpRemoteDirectorySetup 	currentSetup;
    FtpRemoteDirectorySetup 	newSetup;
    GenericObjectEditorDialog	dialog;

    currentSetup = getCurrent();
    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Remote directory");
    dialog.getGOEEditor().setClassType(FtpRemoteDirectorySetup.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.setCurrent(currentSetup);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return null;
    newSetup = (FtpRemoteDirectorySetup) dialog.getCurrent();
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
  protected String toString(FtpRemoteDirectorySetup value) {
    return value.toCommandLine();
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  protected FtpRemoteDirectorySetup fromString(String value) {
    try {
      return (FtpRemoteDirectorySetup) OptionUtils.forAnyCommandLine(FtpRemoteDirectorySetup.class, value);
    }
    catch (Exception e) {
      return new FtpRemoteDirectorySetup();
    }
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(String value) {
    FtpRemoteDirectorySetup current;

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
    return "FTP";
  }

  /**
   * Returns the client to use.
   *
   * @return		the client
   */
  protected synchronized FTPClient getClient() {
    if ((m_Client == null) || !m_Client.isConnected())
      m_Client = getCurrent().getClient();
    return m_Client;
  }

  /**
   * Returns the directory lister.
   *
   * @return		the lister
   */
  public DirectoryLister getDirectoryLister() {
    FtpDirectoryLister   	result;
    FtpRemoteDirectorySetup 	current;

    current = getCurrent();
    result = new FtpDirectoryLister();
    result.setClient(getClient());
    result.setWatchDir(current.getRemoteDir());

    return result;
  }

  /**
   * Returns the file operations.
   *
   * @return		the operations
   */
  @Override
  public FileOperations getFileOperations() {
    FtpFileOperations	result;

    result = new FtpFileOperations();
    result.setClient(getClient());

    return result;
  }
}
