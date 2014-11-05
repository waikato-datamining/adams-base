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
 * AbstractIMSimpleOperation.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.imagemagick.im;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;

/**
 * Ancestor for simple ImageMagic operations.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractIMSimpleOperation
  extends AbstractIMOperation {

  /** for serialization. */
  private static final long serialVersionUID = 4447009209054143230L;

  /**
   * Adds the operation.
   * 
   * @param op		the operation object to update
   */
  public abstract void addOperation(IMOperation op);
  
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
    String	result;
    ConvertCmd	cmd;
    IMOperation	op;
    
    result = null;
    
    try {
      cmd = new ConvertCmd();
      op  = new IMOperation();
      op.addImage(input.getAbsolutePath());
      addOperation(op);
      op.addImage(output.getAbsolutePath());
      cmd.run(op);
    }
    catch (Exception e) {
      result = Utils.handleException(this, "Failed to apply operation!", e);
    }
    
    return result;
  }
}
