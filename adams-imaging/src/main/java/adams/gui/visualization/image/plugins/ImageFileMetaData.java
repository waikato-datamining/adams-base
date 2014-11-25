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
 * ImageFileMetaData.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.transformer.metadata.AbstractMetaDataExtractor;
import adams.flow.transformer.metadata.Sanselan;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.image.ImagePanel;

/**
 * Loads the image meta-data from disk.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7714 $
 */
public class ImageFileMetaData
  extends AbstractImageViewerPluginWithGOE {

  /** for serialization. */
  private static final long serialVersionUID = -3146372359577147914L;
  
  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "Image file meta-data";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "imagemetadata.png";
  }

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(ImagePanel panel) {
    return (panel != null) && (panel.getCurrentImage() != null) && (panel.getCurrentFile() != null) && (!panel.isModified());
  }

  /**
   * Creates the log message.
   * 
   * @return		always null
   */
  @Override
  protected String createLogEntry() {
    return null;
  }

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return AbstractMetaDataExtractor.class;
  }
  
  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new Sanselan();
  }

  /**
   * Processes the image.
   */
  @Override
  protected String process() {
    String			result;
    SpreadSheet			sheet;
    SpreadSheetDialog		dialog;
    AbstractMetaDataExtractor	extractor;
    
    result = null;
    
    if ((m_CurrentPanel.getCurrentFile() == null) || (m_CurrentPanel.isModified()))
      return result;
    
    try {
      extractor = (AbstractMetaDataExtractor) getLastSetup();
      sheet     = extractor.extract(m_CurrentPanel.getCurrentFile());
      if (m_CurrentPanel.getParentDialog() != null)
	dialog = new SpreadSheetDialog(m_CurrentPanel.getParentDialog());
      else
	dialog = new SpreadSheetDialog(m_CurrentPanel.getParentFrame());
      dialog.setTitle("Meta-data - " + m_CurrentPanel.getCurrentFile().getName() + " [" + m_CurrentPanel.getCurrentFile().getParent() + "]");
      dialog.setSpreadSheet(sheet);
      dialog.setShowSearch(true);
      dialog.setSize(600, 600);
      dialog.setLocationRelativeTo(null);
      dialog.setVisible(true);
    }
    catch (Exception e) {
      result = "Failed to read meta-data from file '" + m_CurrentPanel.getCurrentFile() + "': " 
	  + Utils.throwableToString(e);
    }
    
    return result;
  }
}
