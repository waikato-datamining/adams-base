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
 * AbstractTweetReplay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.util.Iterator;

import twitter4j.Status;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for classes that replay tweets.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTweetReplay
  extends AbstractOptionHandler
  implements Iterator<Status>, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3466962177087642257L;

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Performs checks before starting the replay.
   * <p/>
   * Default implementation does nothing and always returns null.
   * 
   * @return		null if everything OK, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Performs the actual setup.
   * 
   * @return		null if everything OK, otherwise error message
   */
  protected abstract String doConfigure();
  
  /**
   * Initializes the replay.
   * 
   * @return		null if everything OK, otherwise error message
   */
  public String configure() {
    String	result;
    
    result = check();
    if (result == null)
      result = doConfigure();
    
    return result;
  }

  /**
   * Does nothing.
   */
  @Override
  public void remove() {
  }

  /**
   * Checks whether there is another tweet available.
   * 
   * @return		true if tweet available
   */
  @Override
  public abstract boolean hasNext();
  
  /**
   * Returns the next tweet.
   * 
   * @return		the next tweet, null if none available
   */
  @Override
  public abstract Status next();
}
