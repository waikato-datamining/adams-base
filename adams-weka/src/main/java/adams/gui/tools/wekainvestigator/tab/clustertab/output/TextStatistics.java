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
 * TextStatistics.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.clustertab.output;

import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.clustertab.ResultItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates basic text statistic.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextStatistics
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /** whether to print the run information as well. */
  protected boolean m_RunInformation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates basic text statistic.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "run-information", "runInformation",
      false);
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Statistics";
  }

  /**
   * Sets whether the run information is output as well.
   *
   * @param value	if true then the run information is output as well
   */
  public void setRunInformation(boolean value) {
    m_RunInformation = value;
    reset();
  }

  /**
   * Returns whether the run information is output as well.
   *
   * @return		true if the run information is output as well
   */
  public boolean getRunInformation() {
    return m_RunInformation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String runInformationTipText() {
    return "If set to true, then the run information is output as well.";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation();
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
    StringBuilder	buffer;
    SpreadSheet 	meta;
    List<String> 	keys;
    List<String>	values;
    int			len;
    int			i;

    buffer = new StringBuilder(item.getEvaluation().clusterResultsToString());

    // run information
    if (m_RunInformation && item.hasRunInformation()) {
      meta   = item.getRunInformation().toSpreadSheet();
      keys   = new ArrayList<>();
      values = new ArrayList<>();
      len    = 0;
      for (Row row: meta.rows()) {
	keys.add(row.getCell(0).getContent());
	values.add(row.getCell(1).getContent());
	len = Math.max(len, keys.get(keys.size() - 1).length());
      }
      for (i = 0; i < keys.size(); i++) {
	while (keys.get(i).length() < len)
	  keys.set(i, keys.get(i) + ".");
	keys.set(i, keys.get(i) + ": ");
      }
      buffer.append("\n\n" + "=== Run information ===\n\n");
      for (i = 0; i < keys.size(); i++) {
	buffer.append(keys.get(i));
	buffer.append(values.get(i));
	buffer.append("\n");
      }
    }

    text = new BaseTextArea();
    text.setTextFont(Fonts.getMonospacedFont());
    text.setText(buffer.toString());
    text.setCaretPosition(0);
    addTab(item, new TextualContentPanel(text, true));

    return null;
  }
}
