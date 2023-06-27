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
 * ActorStatistics.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Actor;
import adams.flow.core.ActorStatistic;
import adams.gui.dialog.SpreadSheetPanel;

import java.awt.Component;

/**
 * Generates statistics on actors, how many of what type etc.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ActorStatistics
  extends AbstractActorProcessor
  implements GraphicalOutputProducingProcessor {

  private static final long serialVersionUID = -2447016756906694930L;

  /** the statistics. */
  protected ActorStatistic m_Statistic;

  /** the spreadsheet to display. */
  protected SpreadSheet m_Sheet;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates statistics on actors, how many of what type etc.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Statistic = new ActorStatistic();
    m_Sheet     = null;
  }

  /**
   * Performs the actual processing.
   *
   * @param actor the actor to process (is a copy of original for
   *              processors implementing ModifyingProcessor)
   * @see                ModifyingProcessor
   */
  @Override
  protected void processActor(Actor actor) {
    m_Statistic.setActor(actor);
    m_Sheet = m_Statistic.toSpreadSheet();
  }

  /**
   * Returns whether graphical output was generated.
   *
   * @return true if graphical output was generated
   */
  @Override
  public boolean hasGraphicalOutput() {
    return (m_Sheet != null);
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return the graphical output
   */
  @Override
  public Component getGraphicalOutput() {
    SpreadSheetPanel	result;

    result = new SpreadSheetPanel();
    result.setSpreadSheet(m_Sheet);
    result.setShowColumnComboBox(false);
    result.setShowRowColumn(false);
    result.setUseSimpleHeader(true);
    result.setNumDecimals(0);

    return result;
  }

  /**
   * Returns the title for the dialog.
   *
   * @return the title
   */
  @Override
  public String getTitle() {
    return "Actor statistics";
  }
}
