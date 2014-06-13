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
 * Brightness.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JTextField;

import adams.gui.core.BaseDialog;
import adams.gui.visualization.image.ImagePanel;

/**
 * Allows the user to change the brightness of an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Brightness
  extends AbstractImageViewerPluginWithRestore {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;
  
  /**
   * Dialog that allows user to change brightness of image (and preview it).
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class BrightnessDialog
    extends BaseDialog {

    /** for serialization. */
    private static final long serialVersionUID = -9194915750729439570L;

    /** the brightness value. */
    protected JTextField m_TextBrightness;
    
    /** the preview button. */
    protected JButton m_ButtonPreview;
    
    /** the ok button. */
    protected JButton m_ButtonOK;
    
    /** the cancel button. */
    protected JButton m_ButtonCancel;
    
    /**
     * Creates a modal dialog without a title with the specified Dialog as
     * its owner.
     *
     * @param owner	the owning dialog
     */
    public BrightnessDialog(Dialog owner) {
      super(owner, ModalityType.DOCUMENT_MODAL);
    }

    /**
     * Creates a modal dialog without a title with the specified Frame as
     * its owner.
     *
     * @param owner	the owning frame
     */
    public BrightnessDialog(Frame owner) {
      super(owner, true);
    }

    /**
     * Initializes the widgets. 
     */
    @Override
    protected void initGUI() {
      super.initGUI();
      
      
    }
  }
  
  /** the current setup for changing the brightness. */
  protected adams.data.jai.transformer.Brightness m_Brightness;
  
  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "Brightness...";
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
   * @return		the message, null if none available
   */
  @Override
  protected String createLogEntry() {
    return "Changing brightness: " + m_Brightness.toCommandLine();
  }

  /**
   * The actual interaction with the user.
   * 
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doInteract() {
    return null;
  }
}
