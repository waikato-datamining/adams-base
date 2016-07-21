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
 * AbstractOutputGenerator.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output;

import adams.core.option.AbstractOptionHandler;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;

import javax.swing.JComponent;

/**
 * Ancestor for output generators using t.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOutputGenerator
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -6176955975392722176L;

  /**
   * Adds the component as tab to the result item.
   *
   * @param item	the result item to add to
   * @param comp	the component to add
   */
  protected void addTab(ResultItem item, JComponent comp) {
    item.getTabbedPane().addTab(getTitle(), comp);
  }

  /**
   * The title to use for the tab.
   *
   * @return		the title
   */
  public abstract String getTitle();

  /**
   * Generates output and adds it to the {@link ResultItem}.
   *
   * @param item	the item to add the output to
   * @return		null if output could be generated, otherwise error message
   */
  public abstract String generateOutput(ResultItem item);
}
