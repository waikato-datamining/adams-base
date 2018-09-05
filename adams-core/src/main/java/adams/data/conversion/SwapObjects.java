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
 * SwapObjects.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCommandLine;
import adams.core.discovery.PropertyPath.Path;
import adams.core.option.OptionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Swaps all occurrences of one object with another one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the object is created before swapping.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-old-object &lt;adams.core.base.BaseCommandLine&gt; (property: oldObject)
 * &nbsp;&nbsp;&nbsp;The old commandline to replace.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToInt
 * </pre>
 *
 * <pre>-exact-match &lt;boolean&gt; (property: exactMatch)
 * &nbsp;&nbsp;&nbsp;If enabled, then the complete command-line is used for comparison rather
 * &nbsp;&nbsp;&nbsp;than just the class name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-new-object &lt;adams.core.base.BaseCommandLine&gt; (property: newObject)
 * &nbsp;&nbsp;&nbsp;The new commandline to replace with.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.StringToLong
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SwapObjects
  extends AbstractSwapObject {

  private static final long serialVersionUID = 6354250690633555375L;

  /** the old object. */
  protected BaseCommandLine m_OldObject;

  /** whether to use an exact match (incl options) or just the class name. */
  protected boolean m_ExactMatch;

  /** the new object. */
  protected BaseCommandLine m_NewObject;

  /** the class of the old object. */
  protected transient Class m_OldObjectClass;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Swaps all occurrences of one object with another one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "old-object", "oldObject",
      new BaseCommandLine(StringToInt.class));

    m_OptionManager.add(
      "exact-match", "exactMatch",
      false);

    m_OptionManager.add(
      "new-object", "newObject",
      new BaseCommandLine(StringToLong.class));
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OldObjectClass = null;
  }

  /**
   * Sets the old object to replace.
   *
   * @param value 	the old object
   */
  public void setOldObject(BaseCommandLine value) {
    m_OldObject = value;
    reset();
  }

  /**
   * Returns the old object to replace.
   *
   * @return 		the old object
   */
  public BaseCommandLine getOldObject() {
    return m_OldObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String oldObjectTipText() {
    return "The old commandline to replace.";
  }

  /**
   * Sets whether to use the complete command-line for comparison rather
   * than just the class name.
   *
   * @param value 	true if to use exact match
   */
  public void setExactMatch(boolean value) {
    m_ExactMatch = value;
    reset();
  }

  /**
   * Returns whether to use the complete command-line for comparison rather
   * than just the class name.
   *
   * @return 		true if to use exact match
   */
  public boolean getExactMatch() {
    return m_ExactMatch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String exactMatchTipText() {
    return "If enabled, then the complete command-line is used for comparison rather than just the class name.";
  }

  /**
   * Sets the new object to replace with.
   *
   * @param value 	the new object
   */
  public void setNewObject(BaseCommandLine value) {
    m_NewObject = value;
    reset();
  }

  /**
   * Returns the new object to replace with.
   *
   * @return 		the new object
   */
  public BaseCommandLine getNewObject() {
    return m_NewObject;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String newObjectTipText() {
    return "The new commandline to replace with.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "oldObject", m_OldObject, "old: ");
    result += QuickInfoHelper.toString(this, "exactMatch", (m_ExactMatch ? "exact" : "just class"), ", ");
    result += QuickInfoHelper.toString(this, "newObject", m_NewObject, ", new: ");

    return result;
  }

  /**
   * Checks whether a swap can be made.
   *
   * @param path 	the current path
   * @param desc 	the property descriptor
   * @param parent	the parent object to swap
   * @param child	the child object to swap
   * @return		true if swap can be done
   */
  @Override
  protected boolean canSwap(Path path, PropertyDescriptor desc, Object parent, Object child) {
    if ((child == null) || (desc == null))
      return false;

    if (!m_ExactMatch) {
      if (m_OldObjectClass == null)
        m_OldObjectClass = m_OldObject.objectValue().getClass();
      return m_OldObjectClass.equals(child.getClass());
    }
    else {
      return m_OldObject.getValue().equals(OptionUtils.getCommandLine(child));
    }
  }

  /**
   * Performs the swap.
   *
   * @param path 	the current path
   * @param desc 	the property descriptor
   * @param parent	the parent object to swap
   * @param child	the child object to swap
   * @return		true if swap was successful
   */
  @Override
  protected boolean performSwap(Path path, PropertyDescriptor desc, Object parent, Object child) {
    Method 	method;
    Object	array;
    Object	newObject;
    int		index;

    newObject = m_NewObject.objectValue();

    index = path.get(path.size() - 1).getIndex();
    if (index > -1) {
      try {
        method = desc.getReadMethod();
        array = method.invoke(parent);
	Array.set(array, index, newObject);
	method = desc.getWriteMethod();
	method.invoke(parent, array);
	return true;
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to update object (" + path + ")!", e);
	return false;
      }
    }
    else {
      try {
	method = desc.getWriteMethod();
	method.invoke(parent, newObject);
	return true;
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to update object (" + path + ")!", e);
	return false;
      }
    }
  }
}
