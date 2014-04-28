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
 * AbstractImagePreparation.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.imagepreparation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.core.Stoppable;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for algorithms that preprocess images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractImagePreparation
  extends AbstractOptionHandler
  implements Stoppable {

  /** for serialization. */
  private static final long serialVersionUID = -4035633099365011707L;

  /** for storing errors. */
  protected List<String> m_Errors;

  /** for storing warnings. */
  protected List<String> m_Warnings;
  
  /** whether the preparation was stopped. */
  protected boolean m_Stopped;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Errors   = new ArrayList<String>();
    m_Warnings = new ArrayList<String>();
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Errors.clear();
    m_Warnings.clear();
  }
  
  /**
   * Checks whether there are any errors recorded;
   * 
   * @return		true if at least one error recorded
   */
  public boolean hasErrors() {
    return (m_Errors.size() > 0);
  }
  
  /**
   * Returns the errors.
   * 
   * @return		the errors
   */
  public List<String> getErrors() {
    return m_Errors;
  }
  
  /**
   * Adds the error to its internal list of errors.
   * 
   * @param msg		the error message to add
   */
  protected void addError(String msg) {
    m_Errors.add(msg);
  }
  
  /**
   * Checks whether there are any warnings recorded;
   * 
   * @return		true if at least one error recorded
   */
  public boolean hasWarnings() {
    return (m_Warnings.size() > 0);
  }
  
  /**
   * Returns the warnings.
   * 
   * @return		the warnings
   */
  public List<String> getWarnings() {
    return m_Warnings;
  }
  
  /**
   * Adds the warning to its internal list of warnings.
   * 
   * @param msg		the warnings message to add
   */
  protected void addWarning(String msg) {
    m_Warnings.add(msg);
  }
  
  /**
   * Checks whether the input can be used.
   * <p/>
   * Default implementation only checks whether image is not null.
   * 
   * @param image	the image to check
   */
  protected void check(BufferedImage image) {
    if (image == null)
      throw new IllegalArgumentException("No image provided!");
  }
  
  /**
   * Performs the actual processing of the image.
   * 
   * @param image	the image with the seedlings
   * @return		the processed image
   */
  protected abstract BufferedImage doProcess(BufferedImage image);
  
  /**
   * Processes the image.
   * 
   * @param image	the image with the seedlings
   * @return		the process image
   */
  public BufferedImage process(BufferedImage image) {
    BufferedImage	result;
    
    m_Stopped = false;
    m_Errors.clear();
    m_Warnings.clear();
    
    check(image);
    result = doProcess(image);
    if (m_Stopped)
      result = image;
    
    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }
}
