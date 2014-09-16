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

/**
 * ImageProcessorTabbedPane.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.io.File;

import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;

/**
 * Specialized {@link BaseTabbedPane} for managing images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ImageProcessorTabbedPane
  extends BaseTabbedPane {

  /** for serialization. */
  private static final long serialVersionUID = 4949565559707097445L;
  
  /** the owner. */
  protected ImageProcessorPanel m_Owner;
  
  /**
   * Initializes the tabbed pane.
   * 
   * @param owner	the viewer this pane belongs to
   */
  public ImageProcessorTabbedPane(ImageProcessorPanel owner) {
    super();
    m_Owner = owner;
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
   * Returns the image panel in the currently selected tab.
   *
   * @return		the image panel, null if none available
   */
  public ImageProcessingPanel getCurrentPanel() {
    return getPanelAt(getSelectedIndex());
  }

  /**
   * Returns the image panel of the specified tab.
   *
   * @param index	the tab index
   * @return		the image panel, null if none available
   */
  public ImageProcessingPanel getPanelAt(int index) {
    if ((index < 0) || (index >= getTabCount()))
      return null;
    else
      return (ImageProcessingPanel) getComponentAt(index);
  }

  /**
   * Returns all the image panels.
   *
   * @return		the image panels
   */
  public ImageProcessingPanel[] getAllPanels() {
    ImageProcessingPanel[]	result;
    int				i;
    
    result = new ImageProcessingPanel[getTabCount()];
    for (i = 0; i < getTabCount(); i++)
      result[i] = (ImageProcessingPanel) getComponentAt(i);
    
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
    ImageProcessingPanel	panel;

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
    ImageProcessingPanel	panel;

    panel = new ImageProcessingPanel();
    if (!panel.load(file)) {
      GUIHelper.showErrorMessage(
	  this, "Failed to open image '" + file + "'!");
      return false;
    }
    else {
      addTab(file.getName(), panel);
      setSelectedComponent(panel);
      return true;
    }
  }
}
