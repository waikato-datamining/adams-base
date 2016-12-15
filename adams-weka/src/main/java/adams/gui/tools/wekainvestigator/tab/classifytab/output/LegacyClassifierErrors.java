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
 * LegacyClassifierErrors.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.MessageCollection;
import adams.flow.core.Token;
import adams.flow.sink.WekaClassifierErrors;
import adams.gui.tools.wekainvestigator.output.ComponentContentPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Generates classifier errors plot (legacy Weka output).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LegacyClassifierErrors
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates classifier errors plot (legacy Weka output).";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Errors (legacy)";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasEvaluation() && (item.getEvaluation().predictions() != null);
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    WekaClassifierErrors 	sink;
    JPanel 			panel;

    sink  = new WekaClassifierErrors();
    panel = sink.createDisplayPanel(new Token(item.getEvaluation()));

    return new ComponentContentPanel(panel, sink.displayPanelRequiresScrollPane());
  }
}
