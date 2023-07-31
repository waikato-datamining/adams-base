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
 *    FileEditor.java
 *    Copyright (C) 1999-2023 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.management.FileBrowser;
import adams.core.management.Terminal;
import adams.core.management.User;
import adams.core.option.parsing.FileParsing;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.dialog.PreviewBrowserDialog;
import adams.gui.dialog.SimplePreviewBrowserDialog;
import adams.gui.goe.PropertyPanel.PopupMenuCustomizer;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * A PropertyEditor for File objects that lets the user select a file.
 * <br><br>
 * Based on <code>weka.gui.FileEditor</code>.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 */
public class FileEditor
  extends AbstractPropertyEditorSupport
  implements PopupMenuCustomizer, CustomStringRepresentationHandler, 
             MultiSelectionEditor, InlineEditorSupport {

  /** The file chooser used for selecting files. */
  protected transient BaseFileChooser m_FileChooser;

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return FileParsing.toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return FileParsing.valueOf(null, str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  @Override
  public String getJavaInitializationString() {
    File f = (File) getValue();
    if (f == null)
      return "null";
    else
      return "new java.io.File(\"" + f.getName() + "\")";
  }

  /**
   * Returns the file chooser and creates it if necessary.
   */
  protected BaseFileChooser getFileChooser() {
    BaseFileChooser	fileChooser;
    File 		currentFile;

    if (m_FileChooser == null) {
      currentFile = (File) getValue();
      if (currentFile != null)
	fileChooser = new BaseFileChooser();
      else
	fileChooser = new BaseFileChooser(new File(User.getCWD()));
      fileChooser.setApproveButtonText("Select");
      fileChooser.setApproveButtonMnemonic('S');
      fileChooser.setFileSelectionMode(BaseFileChooser.FILES_AND_DIRECTORIES);
      fileChooser.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  String cmdString = e.getActionCommand();
	  if (cmdString.equals(BaseFileChooser.APPROVE_SELECTION)) {
	    File newVal = getFileChooser().getSelectedFile();
	    setValue(newVal);
	    closeDialog(APPROVE_OPTION);
	  }
	  else {
	    closeDialog(CANCEL_OPTION);
	  }
	}
      });

      m_FileChooser = fileChooser;
    }
    
    return m_FileChooser;
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  @Override
  protected JComponent createCustomEditor() {
    return getFileChooser();
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    File 	currentFile;

    super.initForDisplay();
    
    currentFile = (File) getValue();
    if (currentFile != null)
      getFileChooser().setSelectedFile(currentFile);
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    int[] offset;
    File f = (File) getValue();
    String val = "No file";
    if (f != null)
      val = f.getPath();
    GUIHelper.configureAntiAliasing(gfx, true);
    offset = GUIHelper.calculateFontOffset(gfx, box);
    gfx.drawString(val, offset[0], offset[1]);
  }
  
  /**
   * Checks whether inline editing is available.
   * 
   * @return		true if editing available
   */
  public boolean isInlineEditingAvailable() {
    return true;
  }

  /**
   * Sets the value to use.
   * 
   * @param value	the value to use
   */
  public void setInlineValue(String value) {
    if (isInlineValueValid(value))
      setValue(new File(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((File) getValue()).toString();
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    try {
      new File(value).getAbsolutePath();
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the selected objects.
   *
   * @param parent	the parent container
   * @return		the objects
   */
  public Object[] getSelectedObjects(Container parent) {
    File[]		result;
    BaseFileChooser	chooser;
    int			retVal;
    
    chooser = new BaseFileChooser(new PlaceholderFile(User.getCWD()));
    chooser.setApproveButtonText("Select");
    chooser.setApproveButtonMnemonic('S');
    chooser.setMultiSelectionEnabled(true);
    chooser.setFileSelectionMode(BaseFileChooser.FILES_AND_DIRECTORIES);
    retVal = chooser.showOpenDialog(parent);
    if (retVal == BaseFileChooser.APPROVE_OPTION)
      result = chooser.getSelectedFiles();
    else
      result = new File[0];
    
    return result;
  }

  /**
   * For customizing the popup menu.
   *
   * @param owner	the property panel from where the menu originates
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(final BasePanel owner, JPopupMenu menu) {
    JMenuItem		menuitem;
    final File		file;

    menu.addSeparator();

    file     = (File) getValue();
    menuitem = new JMenuItem("Open in preview browser...");
    menuitem.setIcon(ImageManager.getIcon("previewbrowser.png"));
    menuitem.setEnabled(FileUtils.dirOrParentDirExists(file));
    menuitem.addActionListener((ActionEvent e) -> {
      BaseDialog dialog;
      if (file.isDirectory()) {
	dialog = new PreviewBrowserDialog();
	((PreviewBrowserDialog) dialog).open(new PlaceholderDirectory(file));
      }
      else {
	dialog = new SimplePreviewBrowserDialog();
	((SimplePreviewBrowserDialog) dialog).open(new PlaceholderFile(file));
      }
      dialog.setLocationRelativeTo(dialog.getOwner());
      dialog.setVisible(true);
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Open in file browser...");
    menuitem.setIcon(ImageManager.getIcon("filebrowser.png"));
    menuitem.setEnabled(FileUtils.dirOrParentDirExists(file));
    menuitem.addActionListener((ActionEvent e) -> FileBrowser.launch(file));
    menu.add(menuitem);

    menuitem = new JMenuItem("Open in terminal...");
    menuitem.setIcon(ImageManager.getIcon("terminal.png"));
    menuitem.setEnabled(FileUtils.dirOrParentDirExists(file));
    menuitem.addActionListener((ActionEvent e) -> Terminal.launch(file));
    menu.add(menuitem);

    menuitem = new JMenuItem("Copy (absolute path)");
    menuitem.setIcon(ImageManager.getIcon("copy.gif"));
    menuitem.setEnabled(true);
    menuitem.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(file.getAbsolutePath()));
    menu.add(menuitem);
  }
}
