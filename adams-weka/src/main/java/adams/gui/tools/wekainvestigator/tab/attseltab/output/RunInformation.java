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
 * RunInformation.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.attseltab.output;

import adams.core.MessageCollection;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.tools.wekainvestigator.output.RunInformationHelper;
import adams.gui.tools.wekainvestigator.output.TextualContentPanel;
import adams.gui.tools.wekainvestigator.tab.attseltab.ResultItem;

import javax.swing.JComponent;

/**
 * Generates run information.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RunInformation
  extends AbstractOutputGenerator {

  private static final long serialVersionUID = -6829245659118360739L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the run information.";
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Run information";
  }

  /**
   * Checks whether output can be generated from this item.
   *
   * @param item	the item to check
   * @return		true if output can be generated
   */
  public boolean canGenerateOutput(ResultItem item) {
    return item.hasRunInformation();
  }

  /**
   * Generates output from the item.
   *
   * @param item	the item to generate output for
   * @param errors	for collecting error messages
   * @return		the output component, null if failed to generate
   */
  public JComponent createOutput(ResultItem item, MessageCollection errors) {
    BaseTextArea  text;

    if (!item.hasRunInformation()) {
      errors.add("No run information available!");
      return null;
    }

    text = new BaseTextArea();
    text.setEditable(false);
    text.setTextFont(Fonts.getMonospacedFont());
    text.setText(RunInformationHelper.toString(item.getRunInformation().toSpreadSheet()));
    text.setCaretPosition(0);

    return new TextualContentPanel(text, true);
  }
}
