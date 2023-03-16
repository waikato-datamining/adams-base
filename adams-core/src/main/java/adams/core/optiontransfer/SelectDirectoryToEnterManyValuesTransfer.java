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
 * SelectDirectoryToEnterManyValuesTransfer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.source.EnterManyValues;
import adams.flow.source.SelectDirectory;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.flow.source.valuedefinition.DirectoryValueDefinition;

/**
 * Transfers options from SelectDirectory to EnterManyValues actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SelectDirectoryToEnterManyValuesTransfer
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
    return (source instanceof SelectDirectory) && (target instanceof EnterManyValues);
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
    SelectDirectory 		sd;
    EnterManyValues 		em;
    DirectoryValueDefinition 	dvd;

    sd = (SelectDirectory) source;
    em = (EnterManyValues) target;

    dvd = new DirectoryValueDefinition();
    dvd.setName("directory");
    dvd.setDisplay("Directory");
    dvd.setFileChooserTitle(sd.getDirectoryChooserTitle());
    dvd.setUseAbsolutePath(sd.getAbsoluteDirectoryName());
    dvd.setUseForwardSlashes(sd.getUseForwardSlashes());
    dvd.setDefaultValue(sd.getInitialDirectory());
    if (sd.getOptionManager().hasVariableForProperty("initialDirectory"))
      dvd.getOptionManager().setVariableForProperty("defaultValue", sd.getOptionManager().getVariableForProperty("initialDirectory"));
    em.setValues(new AbstractValueDefinition[]{dvd});

    return null;
  }
}
