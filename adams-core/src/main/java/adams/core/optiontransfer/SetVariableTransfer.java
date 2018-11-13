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
 * SetVariableTransfer.java
 * Copyright (C) 2016-2018 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

/**
 * Transfers options between {@link adams.flow.standalone.SetVariable}
 * and {@link adams.flow.transformer.SetVariable} objects as vice versa.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SetVariableTransfer
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
    return ((source instanceof adams.flow.standalone.SetVariable) && (target instanceof adams.flow.transformer.SetVariable))
      || ((source instanceof adams.flow.transformer.SetVariable) && (target instanceof adams.flow.standalone.SetVariable));
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
    adams.flow.standalone.SetVariable 	standalone;
    adams.flow.transformer.SetVariable 	transformer;

    if (source instanceof adams.flow.standalone.SetVariable) {
      standalone  = (adams.flow.standalone.SetVariable) source;
      transformer = (adams.flow.transformer.SetVariable) target;

      transformer.setVariableName(standalone.getVariableName());
      transferVariable(standalone, transformer, "variableName");

      transformer.setVariableValue(standalone.getVariableValue());
      transferVariable(standalone, transformer, "variableValue");

      transformer.setValueType(standalone.getValueType());
      transferVariable(standalone, transformer, "valueType");

      transformer.setExpandValue(standalone.getExpandValue());
      transferVariable(standalone, transformer, "expandValue");
    }
    else {
      transformer = (adams.flow.transformer.SetVariable) source;
      standalone  = (adams.flow.standalone.SetVariable) target;

      standalone.setVariableName(transformer.getVariableName());
      transferVariable(transformer, standalone, "variableName");

      standalone.setVariableValue(transformer.getVariableValue());
      transferVariable(transformer, standalone, "variableValue");

      standalone.setValueType(transformer.getValueType());
      transferVariable(transformer, standalone, "valueType");

      standalone.setExpandValue(transformer.getExpandValue());
      transferVariable(transformer, standalone, "expandValue");
    }

    return null;
  }
}
