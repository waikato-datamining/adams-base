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
 * AbstractOptionTransfer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.ClassLister;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for transferring options between option handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionTransfer {

  /** the cache of transfer handlers. */
  protected static List<AbstractOptionTransfer> m_Transfers;

  /**
   * Returns whether it can handle the transfer.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		true if options can be transferred by this class
   */
  public abstract boolean handles(Object source, Object target);

  /**
   * Hook method for checks.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		null if successful, otherwise error message
   */
  protected String check(Object source, Object target) {
    if (source == null)
      return "Source object is null!";
    if (target == null)
      return "Target object is null!";
    return null;
  }

  /**
   * Transfers the variable (if any attached).
   *
   * @param source	the source from which to transfer the variable
   * @param target	the target to receive the variable
   * @param property	the property for which to transfer the variable
   */
  protected void transferVariable(OptionHandler source, OptionHandler target, String property) {
    String		var;
    AbstractOption	option;

    if (source.getOptionManager().hasVariableForProperty(property)) {
      var    = source.getOptionManager().getVariableForProperty(property);
      option = target.getOptionManager().findByProperty(property);
      if ((option != null) && (option instanceof AbstractArgumentOption))
	((AbstractArgumentOption) option).setVariable(var);
    }
  }

  /**
   * Does the actual transfer of options.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		null if successful, otherwise error message
   */
  protected abstract String doTransfer(Object source, Object target);

  /**
   * Transfers the options.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		null if successful, otherwise error message
   */
  public String transfer(Object source, Object target) {
    String	result;

    result = check(source, target);
    if (result == null)
      result = doTransfer(source, target);

    return result;
  }

  /**
   * Returns all the transfer schemes that can handle the provided source/target.
   *
   * @param source	the source object to handle
   * @param target	the target object to handle
   */
  public static synchronized List<AbstractOptionTransfer> getTransfers(Object source, Object target) {
    List<AbstractOptionTransfer>	result;
    Class[]				classes;
    AbstractOptionTransfer		transfer;

    result = new ArrayList<>();

    if (m_Transfers == null) {
      m_Transfers = new ArrayList<>();
      classes     = ClassLister.getSingleton().getClasses(AbstractOptionTransfer.class);
      for (Class cls: classes) {
	try {
	  transfer = (AbstractOptionTransfer) cls.newInstance();
	  m_Transfers.add(transfer);
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate: " + cls.getName());
	  e.printStackTrace();
	}
      }
    }

    for (AbstractOptionTransfer t: m_Transfers) {
      if (t.handles(source, target))
	result.add(t);
    }

    return result;
  }
}
