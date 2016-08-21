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
 * Supplementary.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.output;

import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;

/**
 * Outputs the supplementary data if available.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Supplementary
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** the title. */
  protected String m_Title;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the supplementary data (e.g., cross-validation result) if available.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Title = "Supplementary";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasSupplementaryData();
  }

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    BaseTextArea 	text;

    if (!item.hasSupplementaryData())
      return "No supplementary data available!";

    if (item.hasSupplementaryName())
      m_Title = item.getSupplementaryName();

    text = new BaseTextArea();
    text.setTextFont(Fonts.getMonospacedFont());
    text.setText(item.getSupplementaryData().toString());
    text.setCaretPosition(0);
    addTab(item, new TextualContentPanel(text, true));

    return null;
  }
}
