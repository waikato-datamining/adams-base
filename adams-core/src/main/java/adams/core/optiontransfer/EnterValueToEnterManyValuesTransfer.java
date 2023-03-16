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
 * EnterValueToEnterManyValuesTransfer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.ObjectCopyHelper;
import adams.flow.source.EnterManyValues;
import adams.flow.source.EnterValue;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.flow.source.valuedefinition.ListSelectionValueDefinition;

/**
 * Transfers options from EnterValue to EnterManyValues actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EnterValueToEnterManyValuesTransfer
  extends AbstractOptionTransfer {

  /**
   * Returns whether it can handle the transfer.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		true if options can be transferred by this class
   */
  @Override
  public boolean handles(Object source, Object target) {
    return (source instanceof EnterValue) && (target instanceof EnterManyValues);
  }

  /**
   * Does the actual transfer of options.
   *
   * @param source	the source object
   * @param target	the target object
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doTransfer(Object source, Object target) {
    EnterValue 				ev;
    EnterManyValues 			em;
    ListSelectionValueDefinition 	lvd;

    ev = (EnterValue) source;
    em = (EnterManyValues) target;

    lvd = new ListSelectionValueDefinition();
    lvd.setName("value");
    lvd.setDisplay(ev.getMessage().getValue());
    lvd.setValues(ObjectCopyHelper.copyObjects(ev.getSelectionValues()));
    if (ev.getOptionManager().hasVariableForProperty("values"))
      lvd.getOptionManager().setVariableForProperty("values", ev.getOptionManager().getVariableForProperty("selectionValues"));
    lvd.setDefaultValue(ev.getInitialValue().getValue());
    if (ev.getOptionManager().hasVariableForProperty("initialValue"))
      lvd.getOptionManager().setVariableForProperty("defaultValue", ev.getOptionManager().getVariableForProperty("initialValue"));
    em.setValues(new AbstractValueDefinition[]{lvd});

    return null;
  }
}
