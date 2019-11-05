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
 * AbstractOutputGenerator.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.gui.core.MultiPagePane;
import adams.gui.core.MultiPagePane.PageContainer;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import adams.gui.tools.wekainvestigator.tab.classifytab.output.perfold.PerFoldMultiPagePane;

import javax.swing.JComponent;

/**
 * Ancestor for output generators using the data from the per-fold pane.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOutputGenerator
  extends adams.gui.tools.wekainvestigator.output.AbstractOutputGenerator<ResultItem> {

  private static final long serialVersionUID = -6176955975392722176L;

  /**
   * Generates a new MultiPagePane instance.
   *
   * @return		the instance
   */
  protected MultiPagePane newMultiPagePane(ResultItem item) {
    MultiPagePane 	result;

    result = new PerFoldMultiPagePane(this, item, getClass());

    return result;
  }

  /**
   * Adds a page for the confusion matrix.
   *
   * @param multiPage	the multi-page to add to
   * @param title	the title to use
   * @param comp	the component to add as page
   * @param fold	the fold, -1 for non-fold related page, 0 for full, 1-* for fold indices
   */
  protected void addPage(MultiPagePane multiPage, String title, JComponent comp, int fold) {
    PageContainer 	cont;

    if (comp != null) {
      cont = new PageContainer(title, comp);
      cont.getMetaData().put(PerFoldMultiPagePane.KEY_FOLD, fold);
      multiPage.addPage(cont);
    }
  }
}
