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
 * AbstractListGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.newlist;

import java.util.List;

import adams.core.QuickInfoSupporter;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;

/**
 * Ancestor for list generators.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractListGenerator
  extends AbstractOptionHandler
  implements QuickInfoSupporter, ShallowCopySupporter<AbstractListGenerator> {

  /** for serialization. */
  private static final long serialVersionUID = 2875716109637703967L;

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for checks.
   * <p/>
   * Default implementation does nothing.
   * 
   * @return		the list of elements
   * @throws Exception	if check fails
   */
  protected void check() throws Exception {
  }

  /**
   * Generates the actual list.
   * 
   * @return		the list of elements
   * @throws Exception	if generation fails
   */
  protected abstract List<String> doGenerate() throws Exception;

  /**
   * Generates the list.
   * 
   * @return		the list of elements
   * @throws Exception	if check or generation fails
   */
  public List<String> generate() throws Exception {
    check();
    return doGenerate();
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  @Override
  public AbstractListGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public AbstractListGenerator shallowCopy(boolean expand) {
    return (AbstractListGenerator) OptionUtils.shallowCopy(this, expand);
  }
}
