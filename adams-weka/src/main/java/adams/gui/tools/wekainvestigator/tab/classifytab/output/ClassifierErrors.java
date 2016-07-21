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
 * ClassifierErrors.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.flow.core.Token;
import adams.flow.sink.ActualVsPredictedPlot;
import adams.flow.transformer.WekaPredictionsToSpreadSheet;
import adams.gui.core.BaseScrollPane;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JPanel;

/**
 * Generates classifier errors plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ClassifierErrors
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates classifier errors plot.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Errors";
  }

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  @Override
  public String generateOutput(ResultItem item) {
    ActualVsPredictedPlot 		sink;
    JPanel 				panel;
    WekaPredictionsToSpreadSheet	p2s;
    Token				token;

    p2s = new WekaPredictionsToSpreadSheet();
    p2s.input(new Token(item.getEvaluation()));
    p2s.execute();
    token = p2s.output();

    sink  = new ActualVsPredictedPlot();
    panel = sink.createDisplayPanel(token);

    if (sink.displayPanelRequiresScrollPane())
      addTab(item, new BaseScrollPane(panel));
    else
      addTab(item, panel);

    return null;
  }
}
