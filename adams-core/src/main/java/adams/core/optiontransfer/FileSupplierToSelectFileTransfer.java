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
 * FileSupplierToSelectFile.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.optiontransfer;

import adams.flow.source.FileSupplier;
import adams.flow.source.SelectFile;

/**
 * Transfers options from FileSupplier to SelectFile actors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileSupplierToSelectFileTransfer
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
    return (source instanceof FileSupplier) && (target instanceof SelectFile);
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
    FileSupplier	fs;
    SelectFile		sf;

    fs = (FileSupplier) source;
    sf = (SelectFile) target;

    sf.setOutputArray(fs.getOutputArray());
    transferVariable(sf, fs, "outputArray");

    sf.setInitialFiles(fs.getFiles());
    if (fs.getOptionManager().hasVariableForProperty("files"))
      sf.getOptionManager().setVariableForProperty("initialFiles", fs.getOptionManager().getVariableForProperty("files"));

    return null;
  }
}
