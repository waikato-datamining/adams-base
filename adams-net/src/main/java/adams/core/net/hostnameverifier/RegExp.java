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
 * RegExp.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.net.hostnameverifier;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;

import javax.net.ssl.SSLSession;

/**
 * Hostnames must match regular expression.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RegExp
  extends AbstractHostnameVerifier {

  private static final long serialVersionUID = 8421540491799583225L;

  /** the regular expression to match. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no checks, verifies all hosts.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reg-exp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "regExp", m_RegExp);
  }

  /**
   * Sets the regular expression that the hostnames must match.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the hostnames must match.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the hostnames must match.";
  }

  /**
   * Performs the actual verification of the the host.
   *
   * @param hostname	the hostname to check
   * @param session	the current session
   * @return		true if passes check
   */
  @Override
  protected boolean doVerify(String hostname, SSLSession session) {
    return m_RegExp.isMatch(hostname);
  }
}
