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
 * LocalScopeHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.control;

import adams.core.base.BaseRegExp;

/**
 * Interface for actor handlers that provide a local scope for their sub-actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface LocalScopeHandler
  extends ScopeHandler {

  /**
   * Sets how to handle variables into the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingVariables(ScopeHandling value);

  /**
   * Returns how variables are handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingVariables();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingVariablesTipText();

  /**
   * Sets the regular expression that variable names must match to get
   * into the local scope.
   *
   * @param value	the expression
   */
  public void setVariablesFilter(BaseRegExp value);

  /**
   * Returns the regular expression that variable names must match to get
   * into the local scope.
   *
   * @return		the expression
   */
  public BaseRegExp getVariablesFilter();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablesFilterTipText();

  /**
   * Sets whether to propagate variables from the local to the outer scope.
   *
   * @param value	if true then variables get propagated
   */
  public void setPropagateVariables(boolean value);

  /**
   * Returns whether to propagate variables from the local to the outer scope.
   *
   * @return		true if variables get propagated
   */
  public boolean getPropagateVariables();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propagateVariablesTipText();

  /**
   * Sets the regular expression that variable names must match to get
   * propagated.
   *
   * @param value	the expression
   */
  public void setVariablesRegExp(BaseRegExp value);

  /**
   * Returns the regular expression that variable names must match to get
   * propagated.
   *
   * @return		the expression
   */
  public BaseRegExp getVariablesRegExp();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variablesRegExpTipText();

  /**
   * Sets how to handle storage in the local scope.
   *
   * @param value	the scope handling
   */
  public void setScopeHandlingStorage(ScopeHandling value);

  /**
   * Returns how storage is handled in the local scope.
   *
   * @return		the scope handling
   */
  public ScopeHandling getScopeHandlingStorage();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String scopeHandlingStorageTipText();

  /**
   * Sets the regular expression that storage item names must match to get
   * into the local scope.
   *
   * @param value	the expression
   */
  public void setStorageFilter(BaseRegExp value);

  /**
   * Returns the regular expression that storage item names must match to get
   * into the local scope.
   *
   * @return		the expression
   */
  public BaseRegExp getStorageFilter();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageFilterTipText();

  /**
   * Sets whether to propagate storage items from the local to the outer scope.
   *
   * @param value	if true then storage items get propagated
   */
  public void setPropagateStorage(boolean value);

  /**
   * Returns whether to propagate storage items from the local to the outer scope.
   *
   * @return		true if storage items get propagated
   */
  public boolean getPropagateStorage();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propagateStorageTipText();

  /**
   * Sets the regular expression that storage item names must match to get
   * propagated.
   *
   * @param value	the expression
   */
  public void setStorageRegExp(BaseRegExp value);

  /**
   * Returns the regular expression that storage item names must match to get
   * propagated.
   *
   * @return		the expression
   */
  public BaseRegExp getStorageRegExp();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageRegExpTipText();
}
