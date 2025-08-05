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
 * Clear.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.visualization.segmentation.ImageUtils;

import javax.swing.Icon;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * Clears the annotations of the active label.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Clear
  extends AbstractTool {

  private static final long serialVersionUID = 3995038607501291060L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Clears the annotations of the active label.";
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Clear";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("new.gif");
  }

  /**
   * Returns the mouse cursor to use.
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
    return "run.gif";
  }

  /**
   * Returns the icon name for the modified state.
   *
   * @return		the image name
   */
  protected String getModifiedIcon() {
    return "run.gif";
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel		result;
    JPanel 		panel;
    BaseFlatButton	buttonClear;

    result = new BasePanel();
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    buttonClear = new BaseFlatButton("Remove annotations", ImageManager.getIcon("new.gif"));
    buttonClear.setToolTipText("Removes all annotations of the active label");
    buttonClear.addActionListener((ActionEvent e) -> clear());
    panel.add(buttonClear);

    result.add(panel, BorderLayout.NORTH);

    return result;
  }

  /**
   * Clears the annotations.
   */
  protected void clear() {
    BufferedImage	active;

    if (isAutomaticUndoEnabled())
      getCanvas().getOwner().addUndoPoint();

    active = getActiveImage();
    ImageUtils.replaceColor(
      active, getActiveColor(), new Color(0, 0, 0, 0),
      new int[]{0, active.getWidth() - 1}, new int[]{0, active.getHeight() - 1});

    getCanvas().getOwner().getManager().update();
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
    // nothing to do
  }
}
