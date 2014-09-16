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
 * AbstractObjectLocator.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.locateobjects;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.Stoppable;
import adams.core.option.AbstractOptionHandler;

/**
 * Ancestor for algorithms that locate objects in images.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 78 $
 */
public abstract class AbstractObjectLocator
  extends AbstractOptionHandler 
  implements Stoppable, CleanUpHandler, QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4035633099365011707L;

  /** for storing errors. */
  protected List<String> m_Errors;

  /** for storing warnings. */
  protected List<String> m_Warnings;
  
  /** whether the execution was stopped. */
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
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Checks whether there are any errors recorded.
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
   * Performs the actual locating of the objects.
   * 
   * @param imp	        the image to process
   * @return		the containers of located objects
   */
  protected abstract List<LocatedObject> doLocate(BufferedImage image);
  
  /**
   * Locates the objects in the image.
   * 
   * @param image	the image to process
   * @return		the containers of located objects
   */
  public List<LocatedObject> locate(BufferedImage image) {
    List<LocatedObject>	result;
    
    m_Stopped = false;
    m_Errors.clear();
    m_Warnings.clear();
   
    check(image);
    
    result = doLocate(image);
    if (m_Stopped)
      result = new ArrayList<LocatedObject>();
    
    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    reset();
  }
  
  /**
   * Frees up memory in a "destructive" non-reversible way.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }
}
