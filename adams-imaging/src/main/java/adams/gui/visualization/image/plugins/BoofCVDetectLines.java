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
 * BoofCVDetectLines.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import adams.core.option.OptionUtils;
import adams.data.boofcv.BoofCVImageContainer;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.gui.dialog.SpreadSheetDialog;
import adams.gui.visualization.image.BoofCVDetectLinesImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;

/**
 * Allows the user to change the brightness of an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVDetectLines
  extends AbstractImageViewerPluginWithGOE {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;
  
  /** the overlay in use. */
  protected BoofCVDetectLinesImageOverlay m_Overlay;
  
  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "BoofCV detect lines...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "lines.png";
  }

  /**
   * Checks whether the plugin can be executed given the specified image panel.
   *
   * @param panel	the panel to use as basis for decision
   * @return		true if plugin can be executed
   */
  @Override
  public boolean canExecute(ImagePanel panel) {
    return (panel != null) && (panel.getCurrentImage() != null);
  }

  /**
   * Returns whether the class can be changed in the GOE.
   * 
   * @return		true if class can be changed by the user
   */
  @Override
  protected boolean getCanChangeClassInDialog() {
    return false;
  }
  
  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return Actor.class;
  }

  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new adams.flow.transformer.BoofCVDetectLines();
  }

  /**
   * Creates the log message.
   * 
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return OptionUtils.getCommandLine(getLastSetup());
  }

  /**
   * Processes the image.
   */
  @Override
  protected String process() {
    String					result;
    adams.flow.transformer.BoofCVDetectLines	detect;
    BoofCVImageContainer			cont;
    SpreadSheet					sheet;
    SpreadSheetDialog				dlg;
    String					title;

    result = null;
    
    cont   = new BoofCVImageContainer();
    cont.setImage(ConvertBufferedImage.convertFromSingle(m_CurrentPanel.getCurrentImage(), null, ImageUInt8.class));
    detect = (adams.flow.transformer.BoofCVDetectLines) getLastSetup();
    detect.input(new Token(cont));
    result = detect.execute();
    if (result == null) {
      if (detect.hasPendingOutput()) {
	sheet = (SpreadSheet) detect.output().getPayload();
	// overlay
	m_Overlay = new BoofCVDetectLinesImageOverlay();
	m_Overlay.setLines(sheet);
	m_CurrentPanel.removeImageOverlays(BoofCVDetectLinesImageOverlay.class);
	m_CurrentPanel.addImageOverlay(m_Overlay);
	// dialog
	if (m_CurrentPanel.getParentDialog() != null)
	  dlg = new SpreadSheetDialog(m_CurrentPanel.getParentDialog());
	else
	  dlg = new SpreadSheetDialog(m_CurrentPanel.getParentFrame());
	dlg.setDefaultCloseOperation(SpreadSheetDialog.DISPOSE_ON_CLOSE);
	dlg.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    m_CurrentPanel.removeImageOverlay(m_Overlay);
	    super.windowClosing(e);
	  }
	});
	title = "BoofCV detect lines";
	if (m_CurrentPanel.getCurrentFile() != null)
	  title += " [" + m_CurrentPanel.getCurrentFile().getName() + " - " + m_CurrentPanel.getCurrentFile().getParentFile() + "]";
	dlg.setTitle(title);
	dlg.setSpreadSheet(sheet);
	dlg.setLocationRelativeTo(m_CurrentPanel);
	dlg.setVisible(true);
      }
      else {
	result = "No output generated?";
      }
    }
    
    return result;
  }
}
