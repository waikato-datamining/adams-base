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
 * LocateObjects.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.core.option.OptionUtils;
import adams.flow.transformer.locateobjects.AbstractObjectLocator;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.flow.transformer.locateobjects.PassThrough;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

/**
 * Allows the user to locate objects in the image(s).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocateObjects
  extends AbstractSelectedImagesViewerPluginWithGOE {

  /** for serialization. */
  private static final long serialVersionUID = -7713559121399012254L;

  /**
   * Returns the text for the menu to place the plugin beneath.
   *
   * @return		the menu
   */
  @Override
  public String getMenu() {
    return "Process";
  }

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return AbstractObjectLocator.class;
  }

  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new PassThrough();
  }

  /**
   * Processes the specified panel.
   * 
   * @param panel	the panel to process
   * @return		null if successful, error message otherwise
   */
  @Override
  protected String process(ImagePanel panel) {
    AbstractObjectLocator		locater;
    LocatedObjects			located;
    ObjectLocationsOverlayFromReport	overlay;
    
    setLastSetup(m_Editor.getValue());
    locater = (AbstractObjectLocator) m_Editor.getValue();
    overlay = new ObjectLocationsOverlayFromReport();
    panel.removeImageOverlays(ObjectLocationsOverlayFromReport.class);
    panel.addImageOverlay(overlay);
    located = locater.locate(panel.getCurrentImage());
    panel.setAdditionalProperties(located.toReport(overlay.getPrefix()));
    locater.cleanUp();
    
    return null;
  }

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "Locate objects...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "locateobjects.gif";
  }
  
  /**
   * Creates the log message.
   * 
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return getClass().getSimpleName() + ": " + OptionUtils.getCommandLine(m_Editor.getValue());
  }
}
