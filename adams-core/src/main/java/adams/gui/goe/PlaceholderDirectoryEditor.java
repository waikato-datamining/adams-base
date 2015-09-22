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
 *    PlaceholderDirectoryEditor.java
 *    Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.management.FileBrowser;
import adams.core.management.Terminal;
import adams.core.option.AbstractOption;
import adams.gui.chooser.BaseDirectoryChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.PreviewBrowserDialog;
import adams.gui.goe.PropertyPanel.PopupMenuCustomizer;

/**
 * A PropertyEditor for PlaceholderDirectory objects that lets the user select
 * a directory.
 * <br><br>
 * Based on <code>weka.gui.FileEditor</code>.
 *
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @version $Revision$
 * @see weka.gui.PlaceholderDirectoryEditor
 */
public class PlaceholderDirectoryEditor
  extends AbstractPropertyEditorSupport
  implements CustomStringRepresentationHandler, InlineEditorSupport, 
             PopupMenuCustomizer {

  /** The directory chooser used for selecting dirs. */
  protected BaseDirectoryChooser m_DirChooser;

  /**
   * Returns the file as string.
   *
   * @param option	the current option
   * @param object	the file object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((PlaceholderDirectory) object).getPath();
  }

  /**
   * Returns a file generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a file
   * @return		the generated file
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new PlaceholderDirectory(str);
  }

  /**
   * Returns a custom string representation of the object.
   *
   * @param obj		the object to turn into a string
   * @return		the string representation
   */
  public String toCustomStringRepresentation(Object obj) {
    return toString(null, obj);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return valueOf(null, str);
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
      return "new adams.core.io.PlaceholderDirectory(\"" + f.getName() + "\")";
  }

  /**
   * Gets the custom editor component.
   *
   * @return 		a value of type 'Component'
   */
  @Override
  protected JComponent createCustomEditor() {
    PlaceholderDirectory 	currentDir;

    currentDir = (PlaceholderDirectory) getValue();
    if (currentDir != null)
      m_DirChooser = new BaseDirectoryChooser();
    else
      m_DirChooser = new BaseDirectoryChooser(new File(System.getProperty("user.dir")));
    m_DirChooser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	String cmdString = e.getActionCommand();
	if (cmdString.equals(BaseDirectoryChooser.APPROVE_SELECTION)) {
	  PlaceholderDirectory newVal = new PlaceholderDirectory(m_DirChooser.getSelectedFile().getAbsolutePath());
	  setValue(newVal);
	  closeDialog(APPROVE_OPTION);
	}
	else {
	  closeDialog(CANCEL_OPTION);
	}
      }
    });

    return m_DirChooser;
  }

  /**
   * Initializes the display of the value.
   */
  @Override
  protected void initForDisplay() {
    PlaceholderDirectory 	currentFile;

    super.initForDisplay();

    currentFile = (PlaceholderDirectory) getValue();
    if (currentFile != null)
      m_DirChooser.setSelectedFile(currentFile);
  }

  /**
   * Paints a representation of the current Object.
   *
   * @param gfx 	the graphics context to use
   * @param box 	the area we are allowed to paint into
   */
  @Override
  public void paintValue(java.awt.Graphics gfx, java.awt.Rectangle box) {
    FontMetrics fm = gfx.getFontMetrics();
    int vpad = (box.height - fm.getHeight()) / 2 ;
    File f = (File) getValue();
    String val = "No directory";
    if (f != null)
      val = f.getPath();
    gfx.drawString(val, 2, fm.getHeight() + vpad);
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
      setValue(new PlaceholderDirectory(value));
  }

  /**
   * Returns the current value.
   * 
   * @return		the current value
   */
  public String getInlineValue() {
    return ((PlaceholderDirectory) getValue()).toString();
  }

  /**
   * Checks whether the value id valid.
   * 
   * @param value	the value to check
   * @return		true if valid
   */
  public boolean isInlineValueValid(String value) {
    return PlaceholderDirectory.isValid(value);
  }

  /**
   * For customizing the popup menu.
   *
   * @param owner	the property panel from where the menu originates
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(final BasePanel owner, JPopupMenu menu) {
    JMenuItem			menuitem;
    final PlaceholderFile	file;

    menu.addSeparator();

    file     = (PlaceholderFile) getValue();
    menuitem = new JMenuItem("Open in preview browser...");
    menuitem.setIcon(GUIHelper.getIcon("open.gif"));
    menuitem.setEnabled(FileUtils.directoryExists(file));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PreviewBrowserDialog dialog = new PreviewBrowserDialog();
	if (file.isDirectory())
	  dialog.open(new PlaceholderDirectory(file));
	else
	  dialog.open(file);
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.setVisible(true);
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Open in file browser...");
    menuitem.setIcon(GUIHelper.getIcon("filebrowser.png"));
    menuitem.setEnabled(FileUtils.directoryExists(file));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FileBrowser.launch(file);
      }
    });
    menu.add(menuitem);

    menuitem = new JMenuItem("Open in terminal...");
    menuitem.setIcon(GUIHelper.getIcon("terminal.png"));
    menuitem.setEnabled(FileUtils.directoryExists(file));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	Terminal.launch(file);
      }
    });
    menu.add(menuitem);
  }
}
