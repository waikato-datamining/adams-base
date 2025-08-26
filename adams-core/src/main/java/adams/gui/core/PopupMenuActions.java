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
 * PopupMenuActions.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.management.FileBrowser;
import adams.core.management.Terminal;
import adams.gui.dialog.PreviewBrowserDialog;
import adams.gui.dialog.SimplePreviewBrowserDialog;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Helper class for adding common actions to popup menus.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PopupMenuActions {

  /**
   * Adds a menu item for opening the file/dir in the Preview browser.
   * 
   * @param menu	the menu to append
   * @param path	the file/dir
   */
  public static void openInPreviewBrowser(JPopupMenu menu, File path) {
    JMenuItem	menuitem;
    
    menuitem = new JMenuItem("Open in preview browser...");
    menuitem.setIcon(ImageManager.getIcon("previewbrowser.png"));
    menuitem.setEnabled(FileUtils.dirOrParentDirExists(path));
    menuitem.addActionListener((ActionEvent e) -> {
      BaseDialog dialog;
      if (path.isDirectory()) {
	dialog = new PreviewBrowserDialog();
	((PreviewBrowserDialog) dialog).open(new PlaceholderDirectory(path));
      }
      else {
	dialog = new SimplePreviewBrowserDialog();
	((SimplePreviewBrowserDialog) dialog).open(new PlaceholderFile(path));
      }
      dialog.setLocationRelativeTo(dialog.getOwner());
      dialog.setVisible(true);
    });
    menu.add(menuitem);
  }

  /**
   * Adds a menu item for opening the file/dir in the system's file browser.
   *
   * @param menu	the menu to append
   * @param path	the file/dir
   */
  public static void openInFileBrowser(JPopupMenu menu, File path) {
    JMenuItem	menuitem;

    menuitem = new JMenuItem("Open in file browser...");
    menuitem.setIcon(ImageManager.getIcon("filebrowser.png"));
    menuitem.setEnabled(FileUtils.dirOrParentDirExists(path));
    menuitem.addActionListener((ActionEvent e) -> FileBrowser.launch(path));
    menu.add(menuitem);
  }

  /**
   * Adds a menu item for opening the file/dir in the system's terminal.
   *
   * @param menu	the menu to append
   * @param path	the file/dir
   */
  public static void openInTerminal(JPopupMenu menu, File path) {
    JMenuItem	menuitem;

    menuitem = new JMenuItem("Open in terminal...");
    menuitem.setIcon(ImageManager.getIcon("terminal.png"));
    menuitem.setEnabled(FileUtils.dirOrParentDirExists(path));
    menuitem.addActionListener((ActionEvent e) -> Terminal.launch(path));
    menu.add(menuitem);
  }

  /**
   * Copies the file's/dir's absolute path to the clipboard.
   *
   * @param menu	the menu to append
   * @param path	the file/dir
   */
  public static void copyAbsolutePath(JPopupMenu menu, File path) {
    JMenuItem	menuitem;

    menuitem = new JMenuItem("Copy (absolute path)");
    menuitem.setIcon(ImageManager.getIcon("copy.gif"));
    menuitem.setEnabled(true);
    menuitem.addActionListener((ActionEvent e) -> ClipboardHelper.copyToClipboard(path.getAbsolutePath()));
    menu.add(menuitem);

  }
}
