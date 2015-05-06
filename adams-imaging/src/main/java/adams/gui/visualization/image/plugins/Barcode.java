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
 * Barcode.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.barcode.decode.AbstractBarcodeDecoder;
import adams.data.barcode.decode.ZXing;
import adams.data.image.BufferedImageContainer;
import adams.data.text.TextContainer;
import adams.gui.dialog.TextDialog;
import adams.gui.visualization.image.ImagePanel;
import org.apache.commons.lang.time.StopWatch;

/**
 * Extracts the barcode from the image.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7714 $
 */
public class Barcode
  extends AbstractImageViewerPluginWithGOE {

  /** for serialization. */
  private static final long serialVersionUID = -3146372359577147914L;

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
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "Barcode...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "barcode.gif";
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
   * Creates the log message.
   * 
   * @return		always null
   */
  @Override
  protected String createLogEntry() {
    return OptionUtils.getCommandLine(getLastSetup());
  }

  /**
   * Returns the class to use as type (= superclass) in the GOE.
   * 
   * @return		the class
   */
  @Override
  protected Class getEditorType() {
    return AbstractBarcodeDecoder.class;
  }
  
  /**
   * Returns the default object to use in the GOE if no last setup is yet
   * available.
   * 
   * @return		the object
   */
  @Override
  protected Object getDefaultValue() {
    return new ZXing();
  }

  /**
   * Processes the image.
   */
  @Override
  protected String process() {
    String                  result;
    BufferedImageContainer  cont;
    TextContainer           text;
    TextDialog		    dialog;
    AbstractBarcodeDecoder  decoder;
    StopWatch               watch;
    
    result = null;
    
    try {
      cont    = new BufferedImageContainer();
      cont.setImage(m_CurrentPanel.getCurrentImage());
      watch   = new StopWatch();
      watch.start();
      decoder = (AbstractBarcodeDecoder) getLastSetup();
      text    = decoder.decode(cont);
      watch.stop();
      if (m_CurrentPanel.getParentDialog() != null)
	dialog = new TextDialog(m_CurrentPanel.getParentDialog());
      else
	dialog = new TextDialog(m_CurrentPanel.getParentFrame());
      if (m_CurrentPanel.getCurrentFile() != null)
        dialog.setDialogTitle("Barcode - " + m_CurrentPanel.getCurrentFile().getName() + " [" + m_CurrentPanel.getCurrentFile().getParent() + "]");
      else
        dialog.setDialogTitle("Barcode");
      dialog.setEditable(false);
      if (text != null) {
	dialog.setContent(text.getContent() + "\n\nTime (ms): " + watch.getTime() + "\n\nMeta-data\n\n" + text.getReport());
	dialog.setSize(400, 400);
	dialog.setLocationRelativeTo(null);
	dialog.setVisible(true);
      }
      else {
	result = "Failed to extract barcode! None present?";
      }
    }
    catch (Exception e) {
      result = "Failed to extract barcode: " + Utils.throwableToString(e);
    }
    
    return result;
  }
}
