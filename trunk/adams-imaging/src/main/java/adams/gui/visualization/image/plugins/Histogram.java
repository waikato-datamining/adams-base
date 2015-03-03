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
 * Histogram.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.gui.dialog.ApprovalDialog;
import adams.gui.visualization.image.HistogramPanel;
import adams.gui.visualization.image.ImagePanel;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

/**
 * Displays the histogram(s) for an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Histogram
  extends AbstractImageViewerPlugin {
  
  /** for serialization. */
  private static final long serialVersionUID = 3286345601880725626L;

  /**
   * Returns the text for the menu to place the plugin beneath.
   *
   * @return		the menu
   */
  @Override
  public String getMenu() {
    return "View";
  }

  /**
   * Returns the text for the menu item to create.
   *
   * @return		the text
   */
  @Override
  public String getCaption() {
    return "Histogram...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "histogram.png";
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
    return null;
  }

  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message. Using an empty 
   * 			string will suppress the error message display and
   * 			the creation of a log entry.
   */
  @Override
  protected String doExecute() {
    BufferedImage	image;
    HistogramPanel	panel;
    ApprovalDialog	dialog;
    
    image = m_CurrentPanel.getCurrentImage();
    panel = new HistogramPanel();
    panel.setImage(image);
    
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new ApprovalDialog(m_CurrentPanel.getParentDialog());
    else
      dialog = new ApprovalDialog(m_CurrentPanel.getParentFrame());
    dialog.setTitle("Histogram");
    dialog.setApproveVisible(true);
    dialog.setCancelVisible(false);
    dialog.setDiscardVisible(false);
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    
    return null;
  }
}
