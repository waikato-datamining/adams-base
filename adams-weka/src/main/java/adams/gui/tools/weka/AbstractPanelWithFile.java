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
 * AbstractPanelWithFile.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.weka;

import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.core.BasePanel;

import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Ancestor for panels that allow the user to select a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractPanelWithFile<T extends AbstractChooserPanel>
  extends BasePanel {

  private static final long serialVersionUID = 2584774228409477738L;

  /** the chooser panel. */
  protected T m_PanelChooser;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout(5, 5));
    setBorder(BorderFactory.createEmptyBorder());

    m_PanelChooser = createChooserPanel();
    add(m_PanelChooser, BorderLayout.NORTH);
  }

  /**
   * Generates the panel to use.
   *
   * @return		the generated panel
   */
  protected abstract T createChooserPanel();

  /**
   * Sets the current file to use.
   *
   * @param file	the file
   */
  public abstract void setCurrent(File file);
}
