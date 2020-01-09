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
 * ImageProcessorMultiPagePane.java
 * Copyright (C) 2014-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.management.FileBrowser;
import adams.data.io.input.AbstractImageReader;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MultiPagePane;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Specialized {@link BaseTabbedPane} for managing images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ImageProcessorMultiPagePane
  extends MultiPagePane {

  /** for serialization. */
  private static final long serialVersionUID = 4949565559707097445L;
  
  /** the owner. */
  protected ImageProcessorPanel m_Owner;
  
  /**
   * Initializes the tabbed pane.
   * 
   * @param owner	the viewer this pane belongs to
   */
  public ImageProcessorMultiPagePane(ImageProcessorPanel owner) {
    super();
    m_Owner = owner;
    setMaxPageCloseUndo(10);
    setDividerLocation(250);
  }
  
  /**
   * Returns the owner.
   * 
   * @return		the owner
   */
  public ImageProcessorPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns whether a panel has been selected.
   *
   * @return		true if panel selected
   */
  public boolean hasCurrentPanel() {
    return (getSelectedIndex() != -1);
  }

  /**
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImageProcessorSubPanel getCurrentPanel() {
    return getPanelAt(getSelectedIndex());
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImageProcessorSubPanel getPanelAt(int index) {
    if ((index < 0) || (index >= getPageCount()))
      return null;
    else
      return (ImageProcessorSubPanel) getPageAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImageProcessorSubPanel[] getAllPanels() {
    ImageProcessorSubPanel[]	result;
    int				i;
    
    result = new ImageProcessorSubPanel[getPageCount()];
    for (i = 0; i < getPageCount(); i++)
      result[i] = (ImageProcessorSubPanel) getPageAt(i);
    
    return result;
  }

  /**
   * Returns the current filename.
   *
   * @return		the current filename, can be null
   */
  public File getCurrentFile() {
    return getFileAt(getSelectedIndex());
  }

  /**
   * Returns the current filename.
   *
   * @param index	the tab index
   * @return		the current filename, can be null
   */
  public File getFileAt(int index) {
    File			result;
    ImageProcessorSubPanel	panel;

    result = null;
    panel  = getPanelAt(index);
    if (panel != null)
      result = panel.getCurrentFile();

    return result;
  }

  /**
   * Loads the specified file in a new panel.
   *
   * @param file	the file to load
   * @return		true if successfully loaded
   */
  public boolean load(File file) {
    return load(file, null);
  }

  /**
   * Opens the file with the specified image reader.
   *
   * @param file	the file to open
   * @param reader	the reader to use, null for auto-detection
   * @return		true if successfully read
   */
  public boolean load(File file, AbstractImageReader reader) {
    ImageProcessorSubPanel	panel;

    panel = new ImageProcessorSubPanel(this);
    if (!panel.load(file, reader)) {
      GUIHelper.showErrorMessage(
	  this, "Failed to open image '" + file + "'!");
      return false;
    }
    else {
      addPage(file.getName(), panel);
      setSelectedPage(panel);
      return true;
    }
  }

  /**
   * Generates the right-click menu for the JList.
   *
   * @param e		the event that triggered the popup
   * @return		the generated menu
   * @see		#showPopup(MouseEvent)
   */
  @Override
  protected BasePopupMenu createPopup(MouseEvent e) {
    BasePopupMenu 	result;
    JMenuItem 		menuitem;

    result = super.createPopup(e);

    menuitem = new JMenuItem("Open containing folder");
    menuitem.setIcon(GUIHelper.getIcon("filebrowser.png"));
    menuitem.setEnabled(
      (getSelectedIndices().length == 1)
        && hasCurrentPanel()
        && (getCurrentPanel().getCurrentFile() != null));
    if (menuitem.isEnabled()) {
      menuitem.addActionListener((ActionEvent ae) ->
	FileBrowser.launch(getCurrentPanel().getCurrentFile()));
    }
    result.add(menuitem);

    menuitem = new JMenuItem("Copy filename");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(
      (getSelectedIndices().length == 1)
        && hasCurrentPanel()
        && (getCurrentPanel().getCurrentFile() != null));
    if (menuitem.isEnabled()) {
      menuitem.addActionListener((ActionEvent ae) ->
	ClipboardHelper.copyToClipboard(getCurrentPanel().getCurrentFile().getAbsolutePath()));
    }
    result.add(menuitem);

    return result;
  }
}
