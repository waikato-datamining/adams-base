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
 * DisplayObjectLocationsFromReport.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.plugins;

import adams.core.io.FileUtils;
import adams.data.io.input.AbstractReportReader;
import adams.data.report.Report;
import adams.gui.chooser.DefaultReportFileChooser;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.image.AbstractImageOverlay;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import java.awt.Dialog.ModalityType;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Prompts user to select report with object locations to be overlayed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 198 $
 */
public class DisplayObjectLocationsFromReport
  extends AbstractImageViewerPlugin {
  
  /** for serialization. */
  private static final long serialVersionUID = -7792128547228275760L;

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
    return "Located objects (report)...";
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
    return null;
  }
  
  /**
   * Lets the user configure the overlay.
   * 
   * @return		the configured overlay, null if user cancelled
   */
  protected ObjectLocationsOverlayFromReport configureOverlay() {
    ObjectLocationsOverlayFromReport	result;
    GenericObjectEditorDialog		dialog;
    
    result = new ObjectLocationsOverlayFromReport();
    if (m_CurrentPanel.getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(m_CurrentPanel.getParentFrame(), true);
    dialog.setTitle("Configure overlay");
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.getGOEEditor().setClassType(AbstractImageOverlay.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(false);
    dialog.setCurrent(result);
    dialog.pack();
    dialog.setLocationRelativeTo(m_CurrentPanel);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return null;
    result = (ObjectLocationsOverlayFromReport) dialog.getCurrent();
    
    return result;
  }
  
  /**
   * Configures the filechooser for selecting the report.
   * 
   * @param file	the current image file
   * @return		the filechooser
   */
  protected DefaultReportFileChooser configureFileChooser(File file) {
    DefaultReportFileChooser 		result;

    result = new DefaultReportFileChooser();
    
    if (file != null) {
      result.setCurrentDirectory(file.getParentFile().getAbsoluteFile());
      file = FileUtils.replaceExtension(file.getAbsoluteFile(), ".report");
      result.setCorrectOpenFileFilter(file);
      result.setSelectedFile(file);
    }
    
    return result;
  }

  /**
   * Executes the plugin.
   *
   * @return		null if OK, otherwise error message
   */
  @Override
  protected String doExecute() {
    DefaultReportFileChooser 		fileChooser;
    ImageOverlay			io;
    ObjectLocationsOverlayFromReport	overlay;
    int					retVal;
    AbstractReportReader<Report>	reader;
    List<Report>			locations;
    Iterator<ImageOverlay>		iter;
    
    fileChooser = configureFileChooser(m_CurrentPanel.getCurrentFile());
    retVal      = fileChooser.showOpenDialog(m_CurrentPanel);
    if (retVal != DefaultReportFileChooser.APPROVE_OPTION)
      return null;
    
    reader = fileChooser.getReader();
    reader.setInput(fileChooser.getSelectedPlaceholderFile());
    locations = reader.read();
    if (locations.size() == 0)
      return "Failed to read any report from: " + fileChooser.getSelectedFile();

    // create/update overlay
    overlay = null;
    iter    = m_CurrentPanel.imageOverlays();
    while (iter.hasNext()) {
      io = iter.next();
      if (io instanceof ObjectLocationsOverlayFromReport) {
	overlay = (ObjectLocationsOverlayFromReport) io;
	break;
      }
    }
    if (overlay == null) {
      overlay = configureOverlay();
      if (overlay == null)
	return "Cancelled overlay configuration!";
    }
    
    m_CurrentPanel.addImageOverlay(overlay);
    m_CurrentPanel.setAdditionalProperties(locations.get(0));

    return null;
  }
}
