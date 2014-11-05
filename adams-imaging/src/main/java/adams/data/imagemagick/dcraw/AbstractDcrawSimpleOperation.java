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
 * AbstractDcrawSimpleOperation.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.dcraw;

import org.im4java.core.DCRAWOperation;
import org.im4java.core.DcrawCmd;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;

/**
 * Ancestor for simple DCRAW operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDcrawSimpleOperation
  extends AbstractDcrawOperation {

  /** for serialization. */
  private static final long serialVersionUID = 4447009209054143230L;

  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  public abstract void addOperation(DCRAWOperation op);
  
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
    DcrawCmd		cmd;
    DCRAWOperation	op;
    
    result = null;
    
    try {
      cmd = new DcrawCmd();
      op  = new DCRAWOperation();
      addOperation(op);
      op.addImage(input.getAbsolutePath());
      cmd.run(op);
      // move ppm
      result = move(input, output);
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to apply operation!", e);
    }
    
    return result;
  }
}
