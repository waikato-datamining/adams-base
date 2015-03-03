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
 * AbstractTickGenerator.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core.axis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import adams.core.ClassLister;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;

/**
 * An abstract class of a tick generator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTickGenerator
  extends AbstractOptionHandler 
  implements TickGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -3950212023344727427L;

  /** the owning axis model. */
  protected AbstractAxisModel m_Parent;

  /** the list of ticks to return. */
  protected List<Tick> m_Ticks;
  
  /** for avoiding duplicate labels. */
  protected HashSet<String> m_Labels;

  /**
   * Resets the generator.
   */
  @Override
  protected void reset() {
    super.reset();
    
    if (m_Parent != null)
      m_Parent.update();
  }
  
  /**
   * Initializes the member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Parent = null;
    m_Labels = new HashSet<String>();
    m_Ticks  = new ArrayList<Tick>();
  }
  
  /**
   * Sets the owning axis model.
   *
   * @param value	the model
   */
  public void setParent(AbstractAxisModel value) {
    m_Parent = value;
  }

  /**
   * Returns the owning axis model.
   *
   * @return		the model
   */
  public AbstractAxisModel getParent() {
    return m_Parent;
  }

  /**
   * Checks whether the label is already present. If not already present, gets
   * added to the internal list of labels generated so far.
   * 
   * @param label	the label to check
   * @return		true if already present, false otherwise (or if null)
   */
  protected boolean hasLabel(String label) {
    boolean	result;
    
    result = false;
    if (label != null) {
      if (m_Labels.contains(label))
	result = true;
    }
    
    if (isLoggingEnabled())
      getLogger().info("hasTick: '" + label + "' -> " + result);
    
    return result;
  }
  
  /**
   * Fixes the label, i.e., sets it to null if either NaN or already present.
   * 
   * @param label	the label to fix
   * @return		the (potentially) fixed label
   */
  protected String fixLabel(String label) {
    if (label == null)
      return null;
    if (label.equals("NaN"))
      return null;
    if (hasLabel(label))
      return null;
    return label;
  }
  
  /**
   * Adds the label to the internal list of labels generated so far.
   * 
   * @param label	the label to add
   */
  protected void addLabel(String label) {
    if (label != null)
      m_Labels.add(label);
  }
  
  /**
   * Gets executed before generating the ticks.
   */
  protected void preGenerate() {
    m_Labels.clear();
  }
  
  /**
   * Generate the ticks of this axis.
   */
  protected abstract void doGenerate();
  
  /**
   * Gets executed after generating the ticks.
   */
  protected void postGenerate() {
    m_Labels.clear();
  }
  
  /**
   * Returns the ticks of this axis.
   *
   * @return		the current ticks to display
   */
  public List<Tick> getTicks() {
    m_Ticks.clear();

    if (m_Parent == null) {
      if (isLoggingEnabled())
	getLogger().info("no parent set, no ticks");
      return m_Ticks;
    }

    preGenerate();
    doGenerate();
    postGenerate();
    
    if (isLoggingEnabled())
      getLogger().info("ticks: " + m_Ticks);
    
    return m_Ticks;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public TickGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public TickGenerator shallowCopy(boolean expand) {
    return (TickGenerator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of generators.
   *
   * @return		the generator classnames
   */
  public static String[] getGenerators() {
    return ClassLister.getSingleton().getClassnames(TickGenerator.class);
  }

  /**
   * Instantiates the generator with the given options.
   *
   * @param classname	the classname of the generator to instantiate
   * @param options	the options for the generator
   * @return		the instantiated generator or null if an error occurred
   */
  public static TickGenerator forName(String classname, String[] options) {
    TickGenerator	result;

    try {
      result = (TickGenerator) OptionUtils.forName(TickGenerator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the generator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			generator to instantiate
   * @return		the instantiated generator
   * 			or null if an error occurred
   */
  public static TickGenerator forCommandLine(String cmdline) {
    return (TickGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}