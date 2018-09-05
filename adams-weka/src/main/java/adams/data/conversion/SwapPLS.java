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
 * SwapPLS.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.core.discovery.PropertyPath.Path;
import adams.core.option.OptionUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.PLS;
import weka.filters.supervised.attribute.PLSFilter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Swaps one PLS filter for another.
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
 * <pre>-old-filter &lt;weka.filters.Filter&gt; (property: oldFilter)
 * &nbsp;&nbsp;&nbsp;The old PLS filter to replace.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.supervised.attribute.PLSFilter -C 20 -M -A PLS1 -P center
 * </pre>
 *
 * <pre>-exact-match &lt;boolean&gt; (property: exactMatch)
 * &nbsp;&nbsp;&nbsp;If enabled, then the complete command-line is used for comparison rather
 * &nbsp;&nbsp;&nbsp;than just the class name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-new-filter &lt;weka.filters.Filter&gt; (property: newFilter)
 * &nbsp;&nbsp;&nbsp;The new PLS filter to replace with.
 * &nbsp;&nbsp;&nbsp;default: weka.filters.supervised.attribute.PLS -algorithm adams.data.instancesanalysis.pls.PLS1
 * </pre>
 *
 * <pre>-keep-num-components &lt;boolean&gt; (property: keepNumComponents)
 * &nbsp;&nbsp;&nbsp;If enabled, then the 'number of components' of the old filter are retained.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SwapPLS
  extends AbstractSwapObject {

  private static final long serialVersionUID = -1837107962029937245L;

  /** the old PLS filter. */
  protected Filter m_OldFilter;

  /** whether to use an exact match (incl options) or just the class name. */
  protected boolean m_ExactMatch;

  /** the new PLS filter. */
  protected Filter m_NewFilter;

  /** whether to migrate the number of components. */
  protected boolean m_KeepNumComponents;

  /** the command-line of the old filter. */
  protected transient String m_OldFilterCommandLine;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Swaps one PLS filter for another.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "old-filter", "oldFilter",
      new PLSFilter());

    m_OptionManager.add(
      "exact-match", "exactMatch",
      false);

    m_OptionManager.add(
      "new-filter", "newFilter",
      new PLS());

    m_OptionManager.add(
      "keep-num-components", "keepNumComponents",
      true);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OldFilterCommandLine = null;
  }

  /**
   * Sets the old PLS filter to replace.
   *
   * @param value 	the old filter
   */
  public void setOldFilter(Filter value) {
    if (isPLSFilter(value)) {
      m_OldFilter = value;
      reset();
    }
    else {
      getLogger().warning("Not a PLS filter: " + OptionUtils.getCommandLine(value));
    }
  }

  /**
   * Returns the old PLS filter to replace.
   *
   * @return 		the old filter
   */
  public Filter getOldFilter() {
    return m_OldFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String oldFilterTipText() {
    return "The old PLS filter to replace.";
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
   * Sets the new PLS filter to replace with.
   *
   * @param value 	the new filter
   */
  public void setNewFilter(Filter value) {
    if (isPLSFilter(value)) {
      m_NewFilter = value;
      reset();
    }
    else {
      getLogger().warning("Not a PLS filter: " + OptionUtils.getCommandLine(value));
    }
  }

  /**
   * Returns the new PLS filter to replace with.
   *
   * @return 		the new filter
   */
  public Filter getNewFilter() {
    return m_NewFilter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String newFilterTipText() {
    return "The new PLS filter to replace with.";
  }

  /**
   * Sets whether the 'number of components' of the old filter are retained.
   *
   * @param value 	true if to keep
   */
  public void setKeepNumComponents(boolean value) {
    m_KeepNumComponents = value;
    reset();
  }

  /**
   * Returns whether the 'number of components' of the old filter are retained.
   *
   * @return 		true if to keep
   */
  public boolean getKeepNumComponents() {
    return m_KeepNumComponents;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String keepNumComponentsTipText() {
    return "If enabled, then the 'number of components' of the old filter are retained.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "oldFilter", m_OldFilter, "old: ");
    result += QuickInfoHelper.toString(this, "exactMatch", (m_ExactMatch ? "exact" : "just class"), ", ");
    result += QuickInfoHelper.toString(this, "newFilter", m_NewFilter, ", new: ");
    result += QuickInfoHelper.toString(this, "keepNumComponents", (m_KeepNumComponents ? "keep #comps" : "update #comps"), ", ");

    return result;
  }

  /**
   * Checks whether the filter is an actual PLS filter.
   *
   * @param filter	the filter to check
   * @return		true if PLS filter
   */
  protected boolean isPLSFilter(Filter filter) {
    return (filter instanceof PLSFilter)
      || (filter instanceof PLS);
  }

  /**
   * Retrieves the number of components from the filter.
   *
   * @param filter	the filter to get the number of components from
   * @return		the number of components, -1 if failed to retrieve
   */
  protected int getNumComponents(Object filter) {
    if (filter instanceof PLSFilter)
      return ((PLSFilter) filter).getNumComponents();
    if (filter instanceof PLS)
      return ((PLS) filter).getAlgorithm().getNumComponents();
    return -1;
  }

  /**
   * Sets the number of components in the filter.
   *
   * @param filter		the filter to update
   * @param numComponents	the number of components to set
   * @return			true if successfully updated
   */
  protected boolean setNumComponents(Object filter, int numComponents) {
    if (filter instanceof PLSFilter) {
      ((PLSFilter) filter).setNumComponents(numComponents);
      return true;
    }
    if (filter instanceof PLS) {
      ((PLS) filter).getAlgorithm().setNumComponents(numComponents);
      return true;
    }
    return false;
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

    if (m_ExactMatch) {
      if (m_OldFilterCommandLine == null)
        m_OldFilterCommandLine = OptionUtils.getCommandLine(m_OldFilter);
      return m_OldFilterCommandLine.equals(OptionUtils.getCommandLine(child));
    }
    else {
      return m_OldFilter.getClass().equals(child.getClass());
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
    Method	method;
    Object	array;
    Object	newFilter;
    int		numComps;
    int		index;

    newFilter = ObjectCopyHelper.copyObject(m_NewFilter);
    if (m_KeepNumComponents) {
      numComps = getNumComponents(child);
      if (numComps == -1) {
	getLogger().warning("Failed to obtain number of components from: " + OptionUtils.getCommandLine(child));
      }
      else {
        if (!setNumComponents(newFilter, numComps))
	  getLogger().warning("Failed to set number of components in: " + OptionUtils.getCommandLine(newFilter));
      }
    }

    index = path.get(path.size() - 1).getIndex();
    if (index > -1) {
      try {
        method = desc.getReadMethod();
        array = method.invoke(parent);
	Array.set(array, index, newFilter);
	method = desc.getWriteMethod();
	method.invoke(parent, array);
	return true;
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to update filter (" + path + ")!", e);
	return false;
      }
    }
    else {
      try {
	method = desc.getWriteMethod();
	method.invoke(parent, newFilter);
	return true;
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to update filter (" + path + ")!", e);
	return false;
      }
    }
  }
}
