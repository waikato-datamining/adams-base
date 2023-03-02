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
 * AbstractToolWithParameterPanel.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.tools;

import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ParameterPanel;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Tool that uses a {@link ParameterPanel} for its parameters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractToolWithParameterPanel
  extends AbstractTool {

  private static final long serialVersionUID = -3238804649373495561L;

  /** the apply button. */
  protected BaseFlatButton m_ButtonApply;

  /**
   * Fills the parameter panel with the options.
   *
   * @param paramPanel for adding the options to
   */
  protected abstract void addOptions(ParameterPanel paramPanel);

  /**
   * Creates the panel for setting the options.
   *
   * @return the options panel
   */
  @Override
  protected BasePanel createOptionPanel() {
    BasePanel result;
    JPanel panel;
    JPanel panel2;
    ParameterPanel paramPanel;

    result = new BasePanel();

    m_ButtonApply = createApplyButton();

    panel = new JPanel(new GridLayout(0, 1));
    result.add(panel, BorderLayout.NORTH);

    paramPanel = new ParameterPanel();
    addOptions(paramPanel);
    panel.add(paramPanel);

    panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel2.add(m_ButtonApply);
    setApplyButtonState(m_ButtonApply, false);
    result.add(panel2, BorderLayout.SOUTH);

    return result;
  }
}
