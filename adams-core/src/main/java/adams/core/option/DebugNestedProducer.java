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
 * DebugNestedProducer.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.ArrayList;
import java.util.List;

import adams.flow.core.AbstractExternalActor;

/**
 * Nested producer that outputs format useful for debugging purposes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DebugNestedProducer
  extends NestedProducer
  implements DebugOptionProducer {

  /** for serialization. */
  private static final long serialVersionUID = 931016182843089428L;

  /** the property for the external actors. */
  public final static String PROPERTY_EXTERNALACTOR_FILE = "actorFile";

  /**
   * Returns the current value for the option.
   *
   * @param option	the option to get the current value for
   * @return		the current value (can be array)
   */
  @Override
  protected Object getCurrentValue(AbstractOption option) {
    if (    (option.getOwner().getOwner() instanceof AbstractExternalActor)
	 && option.getProperty().equals(PROPERTY_EXTERNALACTOR_FILE) )
      return ((AbstractExternalActor) option.getOwner().getOwner()).getExternalActor();
    else
      return option.getCurrentValue();
  }

  /**
   * Checks whether the value represents the default value for the option.
   *
   * @param option	the option to check the default value for
   * @param value	the (potential) default value
   * @return		true if the value represents the default value
   */
  @Override
  protected boolean isDefaultValue(AbstractArgumentOption option, Object value) {
    if (    (option.getOwner().getOwner() instanceof AbstractExternalActor)
	 && option.getProperty().equals(PROPERTY_EXTERNALACTOR_FILE) )
      return false;
    else
      return option.isDefaultValue(value);
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  @Override
  public List processOption(AbstractArgumentOption option) {
    List		result;
    List		nested;
    NestedProducer	producer;
    Object		current;

    if (option.getProperty().equals(PROPERTY_EXTERNALACTOR_FILE)) {
      current = getCurrentValue(option);
      result = new ArrayList();
      if (getUsePropertyNames())
	result.add(getOptionIdentifier(option) + "Expanded");
      else
	result.add(getOptionIdentifier(option) + "-expanded");
      if (current != null) {
	producer = new DebugNestedProducer();
	nested   = producer.produce((OptionHandler) current);
      }
      else {
	nested = new ArrayList();
	nested.add("null");
      }
      result.add(nested);

      if (m_Nesting.empty())
	m_Output.addAll(result);
      else
	((List) m_Nesting.peek()).addAll(result);
    }
    else {
      result = super.processOption(option);
    }

    return result;
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(DebugNestedProducer.class, args);
  }
}
