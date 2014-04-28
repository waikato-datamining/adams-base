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
 * TriggerableEventReferenceEditor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.goe;

import adams.core.Utils;
import adams.core.option.AbstractOption;
import adams.flow.core.TriggerableEvent;
import adams.flow.core.TriggerableEventReference;
import adams.gui.flow.tree.Node;

/**
 * A PropertyEditor for TriggerableEventReference objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TriggerableEventReferenceEditor
  extends EventReferenceEditor {

  /**
   * Returns the reference as string.
   *
   * @param option	the current option
   * @param object	the reference object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((TriggerableEventReference) object).getValue();
  }

  /**
   * Returns a reference generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to a reference
   * @return		the generated reference
   */
  public static Object valueOf(AbstractOption option, String str) {
    return new TriggerableEventReference(str);
  }

  /**
   * Returns an object created from the custom string representation.
   *
   * @param str		the string to turn into an object
   * @return		the object
   */
  public Object fromCustomStringRepresentation(String str) {
    return new TriggerableEventReference(str);
  }

  /**
   * Returns a representation of the current property value as java source.
   *
   * @return 		a value of type 'String'
   */
  public String getJavaInitializationString() {
    String	result;

    result = "new " + TriggerableEventReference.class.getName() + "(\"" + Utils.backQuoteChars(getValue().toString()) + "\")";

    return result;
  }

  /**
   * Parses the given string and returns the generated object. The string
   * has to be a valid one, i.e., the isValid(String) check has been
   * performed already and succeeded.
   *
   * @param s		the string to parse
   * @return		the generated object, or null in case of an error
   */
  protected TriggerableEventReference parse(String s) {
    TriggerableEventReference	result;

    try {
      result = new TriggerableEventReference();
      result.setValue(s);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Checks whether the node is valid and can be added to the tree.
   *
   * @param node	the node to check
   * @return		true if valid
   */
  protected boolean isValidNode(Node node) {
    return (node.getActor() instanceof TriggerableEvent);
  }
}
