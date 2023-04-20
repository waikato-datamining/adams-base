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
 * SelectFileToEnterManyValuesTransfer.java
 * Copyright (C) 2023 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.core.ObjectCopyHelper;
import adams.flow.source.EnterManyValues;
import adams.flow.source.SelectFile;
import adams.flow.source.valuedefinition.AbstractValueDefinition;
import adams.flow.source.valuedefinition.FileValueDefinition;

/**
 * Transfers options from SelectFile to EnterManyValues actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SelectFileToEnterManyValuesTransfer
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
    return (source instanceof SelectFile) && (target instanceof EnterManyValues);
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
    SelectFile 			sf;
    EnterManyValues 		em;
    FileValueDefinition 	fvd;

    sf = (SelectFile) source;
    em = (EnterManyValues) target;

    fvd = new FileValueDefinition();
    fvd.setName("file");
    fvd.setDisplay("File");
    fvd.setFileChooserTitle(sf.getFileChooserTitle());
    fvd.setUseAbsolutePath(sf.getAbsoluteFileNames());
    fvd.setUseForwardSlashes(sf.getUseForwardSlashes());
    if (sf.getInitialFiles().length == 1)
      fvd.setDefaultValue(sf.getInitialFiles()[0]);
    if (sf.getOptionManager().hasVariableForProperty("initialFiles"))
      fvd.getOptionManager().setVariableForProperty("defaultValue", sf.getOptionManager().getVariableForProperty("initialFiles"));
    if (sf.getExtensions().length > 0)
      fvd.setExtensions(ObjectCopyHelper.copyObjects(sf.getExtensions()));
    if (sf.getOptionManager().hasVariableForProperty("extensions"))
      fvd.getOptionManager().setVariableForProperty("extensions", sf.getOptionManager().getVariableForProperty("extensions"));
    em.setValues(new AbstractValueDefinition[]{fvd});

    return null;
  }
}
