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
 * MarkLocation.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.image.plugins;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.image.HighlightLocations;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.wizard.FinalPage;
import adams.gui.wizard.PropertySheetPanelPage;
import adams.gui.wizard.TextAreaPage;
import adams.gui.wizard.WizardPane;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Highlights the locations entered by the user in the current image.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MarkLocation
  extends AbstractImageViewerPlugin {

  private static final long serialVersionUID = -4461021007662529135L;

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
    return "Mark locations...";
  }

  /**
   * Returns the icon name.
   *
   * @return		the name, null if none available
   */
  @Override
  public String getIconName() {
    return "locations.png";
  }

  /**
   * Checks whether the plugin can be executed given the specified panel.
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
   * Displays the highlight specified in the properties.
   *
   * @param props	the overlay setup and locations
   */
  protected void showHighlights(Properties props) {
    HighlightLocations		overlay;
    String[]			lines;
    List<Point> 		locations;
    String[]			parts;

    try {
      overlay = (HighlightLocations) OptionUtils.forAnyCommandLine(
	HighlightLocations.class,
	props.getProperty(
	  PropertySheetPanelPage.PROPERTY_CMDLINE, OptionUtils.getCommandLine(new HighlightLocations())));
      lines     = props.getProperty(TextAreaPage.KEY_TEXT, "").split("\n");
      locations = new ArrayList<>();
      for (String line: lines) {
	line = line.trim().replaceAll("  ", " ");
	if (line.isEmpty())
	  continue;
	if (line.indexOf(';') > -1)
	  parts = line.split(";");
	else if (line.indexOf(',') > -1)
	  parts = line.split(",");
	else if (line.indexOf('\t') > -1)
	  parts = line.split("\t");
	else
	  parts = line.split(" ");
	if (parts.length == 2)
	  locations.add(new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
      }
      overlay.setLocations(locations);
      m_CurrentPanel.removeImageOverlays(HighlightLocations.class);
      m_CurrentPanel.addImageOverlay(overlay);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	null, "Failed to process locations/overlay setup:\n" + props + "\n" + Utils.throwableToString(e));
    }
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
    final BaseDialog		dialog;
    final WizardPane		wizard;
    TextAreaPage		textpage;
    PropertySheetPanelPage	overlaypage;
    HighlightLocations		overlay;
    FinalPage			finalpage;
    Properties 			props;

    dialog = new BaseDialog(null, ModalityType.DOCUMENT_MODAL);

    // assemble the wizard
    wizard = new WizardPane();
    textpage = new TextAreaPage("Locations");
    textpage.setDescription(
      "Please enter all the locations that you want to highlight.\n"
	+ "Each location must consist of X and Y.\n"
	+ "You can use whitespaces (blank or tab), comma or semicolon to separate X and Y.");
    wizard.addPage(textpage);
    overlaypage = new PropertySheetPanelPage("Overlay");
    overlaypage.setDescription("Please configure the overlay for highlighting the locations.");
    overlay = new HighlightLocations();
    overlaypage.setTarget(overlay);
    wizard.addPage(overlaypage);
    finalpage = new FinalPage();
    finalpage.setLogo(null);
    finalpage.setDescription("<html><h2>Finish</h2>Please click <b>Finish</b> to highlight the locations.");
    wizard.addPage(finalpage);
    wizard.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	if (e.getActionCommand().equals(WizardPane.ACTION_FINISH)) {
	  Properties props = wizard.getProperties(false);
	  setLastSetup(props);
	  showHighlights(props);
	}
	dialog.setVisible(false);
      }
    });

    // restore setup?
    props = (Properties) getLastSetup();
    if (props != null) {
      try {
	overlay = (HighlightLocations) OptionUtils.forAnyCommandLine(
	  HighlightLocations.class,
	  props.getProperty(
	    PropertySheetPanelPage.PROPERTY_CMDLINE, OptionUtils.getCommandLine(overlay)));
	overlaypage.setTarget(overlay);
      }
      catch (Exception e) {
	// ignored
      }
      textpage.setText(props.getProperty(TextAreaPage.KEY_TEXT, ""));
    }

    // show dialog
    dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(wizard, BorderLayout.CENTER);
    dialog.setTitle("Mark locations");
    dialog.setSize(
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600),
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600));
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);

    return null;
  }
}
