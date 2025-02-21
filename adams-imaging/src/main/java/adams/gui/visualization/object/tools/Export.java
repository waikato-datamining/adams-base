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

/*
 * Export.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.data.image.BufferedImageHelper;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Allows exporting the current view as an image.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class Export
  extends AbstractToolWithParameterPanel {

  private static final long serialVersionUID = 4123530788025166552L;

  /** the file to export the image to. */
  protected FileChooserPanel m_FileChooserPanel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows exporting the current view as an image.\n"
	     + "After selecting an output file, the image can be exported via the save button.";
  }

  /**
   * Creates the mouse cursor to use.
   *
   * @return		the cursor
   */
  @Override
  protected Cursor createCursor() {
    return Cursor.getDefaultCursor();
  }

  /**
   * Creates the mouse listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseAdapter createMouseListener() {
    return null;
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    return null;
  }

  /**
   * Returns the icon name for the unmodified state.
   *
   * @return		the image name
   */
  protected String getUnmodifiedIcon() {
    return "save.gif";
  }

  /**
   * Returns the icon name for the modified state.
   *
   * @return		the image name
   */
  protected String getModifiedIcon() {
    return "save.gif";
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel	result;

    result = super.createOptionPanel();

    m_ButtonApply.setEnabled(false);
    m_ButtonApply.setToolTipText("Click to export the current view");
    m_ButtonApply.setIcon(ImageManager.getIcon("save.gif"));

    return result;
  }

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel  for adding the options to
   */
  @Override
  protected void addOptions(ParameterPanel paramPanel) {
    m_FileChooserPanel = new FileChooserPanel();
    m_FileChooserPanel.addChoosableFileFilter(new ExtensionFileFilter("PNG image", "png"));
    m_FileChooserPanel.addChoosableFileFilter(new ExtensionFileFilter("JPG image", "jpg"));
    m_FileChooserPanel.setDefaultExtension("png");
    m_FileChooserPanel.setAutoAppendExtension(true);
    m_FileChooserPanel.setAcceptAllFileFilterUsed(false);
    m_FileChooserPanel.addChangeListener((ChangeEvent e) -> m_ButtonApply.setEnabled(!m_FileChooserPanel.getCurrent().isDirectory()));

    paramPanel.addParameter("Export to", m_FileChooserPanel);
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Export";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("save.gif");
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    File		file;
    BufferedImage 	image;
    Graphics2D		g2d;
    String		msg;

    file = m_FileChooserPanel.getCurrent();
    if (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg")) {
      image   = new BufferedImage(getCanvas().getImage().getWidth(), getCanvas().getImage().getHeight(), BufferedImage.TYPE_INT_ARGB);
      g2d     = image.createGraphics();
      getCanvas().paint(g2d, 1.0);
      g2d.dispose();
      msg = BufferedImageHelper.write(image, file);
      if (msg == null)
	GUIHelper.showInformationMessage(getCanvas(), "Image saved successfully to:\n" + file.getAbsolutePath());
      else
	GUIHelper.showErrorMessage(getCanvas(), msg);
    }
    else {
      GUIHelper.showErrorMessage(getCanvas(), "File must have either .jpg or .png extension!");
    }
  }
}
