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

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextField;

import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.DialogWithButtons;
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
    extends DialogWithButtons {

    /** for serialization. */
    private static final long serialVersionUID = -9194915750729439570L;

    /** the owner. */
    protected Brightness m_Owner;
    
    /** the brightness value. */
    protected JTextField m_TextBrightness;
    
    /** the preview button. */
    protected JButton m_ButtonPreview;
    
    /** the ok button. */
    protected JButton m_ButtonOK;
    
    /** the cancel button. */
    protected JButton m_ButtonCancel;
    
    /** the current setup for changing the brightness. */
    protected adams.data.jai.transformer.Brightness m_Brightness;
    
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
      ParameterPanel	panel;
      
      super.initGUI();

      setTitle("Brightness");
      
      panel = new ParameterPanel();
      getContentPane().add(panel, BorderLayout.CENTER);
      
      // the factor
      m_TextBrightness = new JTextField(10);
      panel.addParameter("Factor", m_TextBrightness);
      
      // the buttons
      m_ButtonPreview = new JButton("Preview");
      m_ButtonPreview.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          updateSetup();
          preview();
        }
      });
      m_PanelButtonsLeft.add(m_ButtonPreview);
      
      m_ButtonOK = new JButton("OK");
      m_ButtonOK.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          updateSetup();
          setVisible(false);
        }
      });
      m_PanelButtonsRight.add(m_ButtonOK);
      
      m_ButtonCancel = new JButton("Cancel");
      m_ButtonCancel.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          m_Brightness = null;
          setVisible(false);
        }
      });
      m_PanelButtonsRight.add(m_ButtonCancel);
    }

    /**
     * Finishes the initialization.
     */
    @Override
    protected void finishInit() {
      super.finishInit();
      
      pack();
      setLocationRelativeTo(null);
    }
    
    /**
     * Sets the owner of this dialog.
     * 
     * @param value	the owner
     */
    public void setOwnerPlugin(Brightness value) {
      m_Owner = value;
    }
    
    /**
     * Returns the owner of this dialog.
     * 
     * @return		the owner
     */
    public Brightness getOwnerPlugin() {
      return m_Owner;
    }

    /**
     * Updates the setup.
     * 
     * @return		whether successfully updated
     */
    protected boolean updateSetup() {
      try {
	m_Brightness.setFactor(Float.parseFloat(m_TextBrightness.getText()));
	return true;
      }
      catch (Exception e) {
	return false;
      }
    }
    
    /**
     * Performs a preview with the current settings.
     */
    protected void preview() {
      List	backup;
      
      backup = m_Owner.getBackup();
      m_Owner.restore(backup);
      m_Owner.apply(m_Brightness);
    }

    /**
     * Sets the setup to use.
     * 
     * @param value	the setup, use null for default setup
     */
    public void setBrightness(adams.data.jai.transformer.Brightness value) {
      m_Brightness = value;
      if (m_Brightness == null)
	m_Brightness = new adams.data.jai.transformer.Brightness();
      m_TextBrightness.setText("" + m_Brightness.getFactor());
    }
    
    /**
     * Returns the current setup.
     * 
     * @return		the setup, null if dialog canceled
     */
    public adams.data.jai.transformer.Brightness getBrightness() {
      return m_Brightness;
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
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "brightness.png";
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
   * Applies the brightness setup to the current image.
   * 
   * @param brightness	the setup to apply
   * @return		null if successful, otherwise error message
   */
  protected String apply(adams.data.jai.transformer.Brightness brightness) {
    String			result;
    BufferedImageContainer	cont;
    BufferedImageContainer[]	trans;
    Report			additional;
    double			scale;
    
    result = null;
    
    scale = m_CurrentPanel.getScale();
    cont  = new BufferedImageContainer();
    cont.setImage(m_CurrentPanel.getCurrentImage());
    cont.setReport(m_CurrentPanel.getImageProperties());
    additional = m_CurrentPanel.getAdditionalProperties();
    trans = brightness.transform(cont);
    if (trans.length != 1) {
      result = "Failed to change brightness!";
    }
    else {
      m_CurrentPanel.setCurrentImage(trans[0]);
      m_CurrentPanel.setAdditionalProperties(additional);
      m_CurrentPanel.setScale(scale);
    }
    
    return result;
  }
  
  /**
   * The actual interaction with the user.
   * 
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doInteract() {
    String			result;
    BrightnessDialog		dialog;
    
    result = null;

    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new BrightnessDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new BrightnessDialog(m_CurrentPanel.getParentFrame());
    dialog.setOwnerPlugin(this);
    dialog.setBrightness(m_Brightness);
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getBrightness() != null) {
      m_Brightness = dialog.getBrightness();
      result = apply(m_Brightness);
    }
    else {
      result = "";  // will suppress error msg/log entry
    }
    
    return result;
  }
}
