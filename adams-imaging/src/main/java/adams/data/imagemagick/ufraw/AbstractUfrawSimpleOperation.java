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
 * AbstractUfrawSimpleOperation.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.ufraw;

import org.im4java.core.UFRawCmd;
import org.im4java.core.UFRawOperation;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

/**
 * Ancestor for simple ufraw operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractUfrawSimpleOperation
  extends AbstractUfrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = 4447009209054143230L;

  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  protected abstract void addOperation(UFRawOperation op);
  
  /**
   * Tries to determine output file type based on extension of output file.
   * Fallback is ppm.
   * 
   * @param op		the operation to add the output type to
   * @param output	the output file
   */
  protected void addOutType(UFRawOperation op, PlaceholderFile output) {
    String	ext;
    String	out;
    
    ext = FileUtils.getExtension(output.getAbsolutePath()).toLowerCase();
    
    out = "ppm";
    for (String ot: OUT_TYPES) {
      if (ext.equals(ot)) {
	out = ot;
	break;
      }
    }
    
    op.outType(out);
  }
  
  /**
   * Applies the actual operation to the input file and stores the result in the 
   * output file.
   * 
   * @param input	the input file
   * @param output	the output file
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doApply(PlaceholderFile input, PlaceholderFile output) {
    String		result;
    UFRawCmd		cmd;
    UFRawOperation	op;
    
    result = null;

    if (result == null) {
      try {
	cmd = new UFRawCmd(true);
	op  = new UFRawOperation();
	op.overwrite();
	op.addImage(input.getAbsolutePath());
	addOperation(op);
	op.output(output.getAbsolutePath());
	addOutType(op, output);
	cmd.run(op);
      }
      catch (Exception e) {
	result = Utils.handleException(this, "Failed to apply operation!", e);
      }
    }
    
    return result;
  }
}
