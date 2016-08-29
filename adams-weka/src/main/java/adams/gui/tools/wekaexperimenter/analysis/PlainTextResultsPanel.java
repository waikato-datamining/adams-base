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
 * PlainTextResultsPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekaexperimenter.analysis;

import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.Fonts;

import java.awt.BorderLayout;

/**
 * Displays the results in plain text.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlainTextResultsPanel
  extends AbstractResultsPanel {

  /** for displaying the results. */
  protected BaseTextAreaWithButtons m_TextAreaResults;

  private static final long serialVersionUID = 3608852939358175057L;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextAreaResults = new BaseTextAreaWithButtons();
    m_TextAreaResults.setFont(Fonts.getMonospacedFont());
    add(m_TextAreaResults, BorderLayout.CENTER);
  }

  /**
   * Returns the name to display in the GUI.
   *
   * @return		the name
   */
  public String getResultsName() {
    return "Plain text";
  }

  /**
   * Displays the results.
   */
  protected void doDisplay() {
    StringBuilder	results;

    results = new StringBuilder();
    results.append(m_Matrix.toStringHeader());
    results.append(m_Matrix.toStringMatrix());
    results.append(m_Matrix.toStringKey());

    m_TextAreaResults.setText(results.toString());
    m_TextAreaResults.setCaretPosition(0);
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public PlainTextResultsPanel getClone() {
    return new PlainTextResultsPanel();
  }
}
