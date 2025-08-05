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
 * Pointer.java
 * Copyright (C) 2020-2023 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.segmentation.tool;

import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;

/**
 * Pointer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Pointer
  extends AbstractTool {

  private static final long serialVersionUID = 3995038607501291060L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return null;
  }

  /**
   * The name of the tool.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Pointer";
  }

  /**
   * The icon of the tool.
   *
   * @return		the icon
   */
  @Override
  public Icon getIcon() {
    return ImageManager.getIcon("cursor.png");
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
    return new ToolMouseAdapter(this);
  }

  /**
   * Creates the mouse motion listener to use.
   *
   * @return		the listener, null if not applicable
   */
  @Override
  protected ToolMouseMotionAdapter createMouseMotionListener() {
    return new ToolMouseMotionAdapter(this);
  }

  /**
   * Applies the settings.
   */
  @Override
  protected void doApply() {
  }

  /**
   * Creates the panel for setting the options.
   *
   * @return		the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel		result;
    JPanel		panel;

    result = new BasePanel();
    panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panel.add(new JLabel("No options"));
    result.add(panel, BorderLayout.NORTH);

    return result;
  }
}
