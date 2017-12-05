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
 * AbstractInstrumentSupplier.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.db.InstrumentProvider;

import java.util.ArrayList;

/**
 * Abstract ancestor for instrument suppliers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractInstrumentSupplier
  extends AbstractDbArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -8159720259695436880L;

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to return the instruments as array or one by one.";
  }

  /**
   * Returns the instrument provider to use for retrieving the instruments.
   *
   * @return		the instrument provider
   */
  protected abstract InstrumentProvider getProvider();

  /**
   * Performs the actual database query.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String queryDatabase() {
    String		result;
    InstrumentProvider	provider;

    result = null;

    provider = getProvider();
    m_Queue  = new ArrayList(provider.getInstruments());
    if (m_Queue.size() == 0)
      result = "No instruments found!";

    return result;
  }
}
