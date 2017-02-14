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
 * Documentation.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.base.BaseRegExp;
import adams.core.io.FileComparator;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.lister.LocalDirectoryLister;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.core.ConsolePanel;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Displays all available PDF documents in the documentation directories.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see AbstractApplicationFrame#setDocumentationDirectories(PlaceholderDirectory[])
 */
public class Documentation
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -6548349613973153076L;

  /**
   * Initializes the menu item.
   */
  public Documentation() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public Documentation(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Documentation";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "pdf.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
  }

  /**
   * Returns the JMenuItem to use.
   *
   * @return		the menu item
   * @see		#launch()
   */
  @Override
  public JMenuItem getMenuItem() {
    JMenu		result;
    List<File>		files;
    String[]		docs;
    JMenuItem		menuitem;
    LocalDirectoryLister lister;

    files = new ArrayList<>();
    for (PlaceholderDirectory dir: m_Owner.getDocumentationDirectories()) {
      lister = new LocalDirectoryLister();
      lister.setWatchDir(dir.getAbsolutePath());
      lister.setListDirs(false);
      lister.setListFiles(true);
      lister.setRecursive(true);
      lister.setRegExp(new BaseRegExp(".*\\.[pP][dD][fF]$"));
      docs = lister.list();
      for (String doc: docs) {
	File file = new PlaceholderFile(doc);
	if (!files.contains(file))
	  files.add(file);
      }
    }
    
    if (files.size() == 0)
      return null;

    Collections.sort(files, new FileComparator(false, true));
    
    result = new JMenu();
    result.setIcon(getIcon());
    result.setText(getTitle());

    for (final File file: files) {
      menuitem = new JMenuItem(FileUtils.replaceExtension(file, "").getName());
      menuitem.addActionListener((ActionEvent e) -> {
        try {
          Desktop.getDesktop().open(file);
        }
        catch (Exception ex) {
          ConsolePanel.getSingleton().append(Level.SEVERE, "Failed to open documentation: " + file, ex);
        }
      });
      result.add(menuitem);
    }
    
    return result;
  }
  
  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_HELP;
  }
}