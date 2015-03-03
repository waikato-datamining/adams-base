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
 * ProgressInConsole.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.flow.condition.bool.Counting;
import adams.flow.control.Count;
import adams.flow.core.AbstractActor;
import adams.flow.sink.Console;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that outputs the token count every X number of tokens, with X being the interval supplied to this template.
 * <p/>
 <!-- globalinfo-end -->
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
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-interval &lt;int&gt; (property: interval)
 * &nbsp;&nbsp;&nbsp;The interval for the progress info.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use in the console.
 * &nbsp;&nbsp;&nbsp;default: # of tokens: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProgressInConsole
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 2310015199489870240L;

  /** the interval. */
  protected int m_Interval;

  /** the prefix. */
  protected String m_Prefix;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a sub-flow that outputs the token count every X number of "
	+ "tokens, with X being the interval supplied to this template.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "interval", "interval",
	    100, 1, null);

    m_OptionManager.add(
	    "prefix", "prefix",
	    "# of tokens: ");
  }

  /**
   * Sets the interval for the output.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    m_Interval = value;
    reset();
  }

  /**
   * Returns the interval for the output.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String intervalTipText() {
    return "The interval for the progress info.";
  }

  /**
   * Sets the prefix for the output.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix for the output.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String prefixTipText() {
    return "The prefix to use in the console.";
  }

  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  @Override
  protected AbstractActor doGenerate() {
    Count	result;
    Counting	cnt;
    Console	con;

    cnt = new Counting();
    cnt.setInterval(m_Interval);
    
    result = new Count();
    result.setCondition(cnt);
    result.setName("Progress info");

    con = new Console();
    con.setPrefix(m_Prefix);
    result.add(con);

    return result;
  }
}
