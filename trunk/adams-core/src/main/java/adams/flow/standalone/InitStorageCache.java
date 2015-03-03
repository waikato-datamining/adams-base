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
 * InitStorageCache.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;

/**
 <!-- globalinfo-start -->
 * Adds a LRU cache in the responsible storage handler (usually the Flow actor).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: InitStorageCache
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-cache &lt;java.lang.String&gt; (property: cache)
 * &nbsp;&nbsp;&nbsp;The name of the LRU cache to initialize.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;The size of the LRU cache.
 * &nbsp;&nbsp;&nbsp;default: 50
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InitStorageCache
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 304023894924959060L;

  /** the name of the LRU cache to initialize. */
  protected String m_Cache;

  /** the size of the LRU cache. */
  protected int m_Size;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Adds a LRU cache in the responsible storage handler (usually the Flow actor).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cache", "cache",
	    "");

    m_OptionManager.add(
	    "size", "size",
	    50, 1, null);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "cache", m_Cache);
    if (result == null)
      result = "";
    result += "/";
    result += QuickInfoHelper.toString(this, "size", m_Size);

    return result;
  }

  /**
   * Sets the name of the cache to initialize.
   *
   * @param value	the name
   */
  public void setCache(String value) {
    m_Cache = value;
    reset();
  }

  /**
   * Returns the name of the cache to initialize.
   *
   * @return 		the name
   */
  public String getCache() {
    return m_Cache;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String cacheTipText() {
    return "The name of the LRU cache to initialize.";
  }

  /**
   * Sets the size of the cache.
   *
   * @param value	the size
   */
  public void setSize(int value) {
    if (value >= 1) {
      m_Size = value;
      reset();
    }
    else {
      getLogger().severe("Size must be at least 1, provided: " + value);
    }
  }

  /**
   * Returns the size of the cache.
   *
   * @return 		the size
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size of the LRU cache.";
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (m_Cache.length() == 0)
	result = "No cache name provided!";
    }

    return result;
  }

  /**
   * Executes the actor.
   *
   * @return		always null
   */
  @Override
  protected String doExecute() {
    getStorageHandler().getStorage().addCache(m_Cache, m_Size);
    return null;
  }
}
