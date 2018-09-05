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
 * AbstractSwapObject.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.ObjectCopyHelper;
import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;
import adams.data.InPlaceProcessing;

import java.beans.PropertyDescriptor;

/**
 * Ancestor for classes that swap all occurrences of one object for another.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSwapObject
  extends AbstractConversion
  implements InPlaceProcessing  {

  private static final long serialVersionUID = 3244184153762298547L;

  /** whether to create no copy of the object. */
  protected boolean m_NoCopy;

  /** the property traversal. */
  protected transient PropertyTraversal m_Traversal;

  /**
   * Observer that performs the swapping of the objects.
   */
  protected static class SwapObserver
    implements Observer {

    /** the owner. */
    protected AbstractSwapObject m_Owner;

    /**
     * Initializes the observer.
     *
     * @param owner	the owning conversion
     */
    public SwapObserver(AbstractSwapObject owner) {
      m_Owner = owner;
    }

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     */
    @Override
    public void observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
      if (m_Owner.canSwap(path, desc, parent, child)) {
        if (!m_Owner.performSwap(path, desc, parent, child))
	  m_Owner.getLogger().warning("Failed to swap: " + path + "/" + child.getClass().getName());
      }
    }
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Sets whether to skip creating a copy of the object before swapping.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the object before swapping.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the object is created before swapping.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Object.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Object.class;
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
  protected abstract boolean canSwap(Path path, PropertyDescriptor desc, Object parent, Object child);

  /**
   * Performs the swap.
   *
   * @param path 	the current path
   * @param desc 	the property descriptor
   * @param parent	the parent object to swap
   * @param child	the child object to swap
   * @return		true if swap was successful
   */
  protected abstract boolean performSwap(Path path, PropertyDescriptor desc, Object parent, Object child);

  /**
   * Performs the swap.
   *
   * @param input	the object to update
   * @return		the updated object
   */
  protected Object doConvert(Object input) {
    if (!m_NoCopy)
      input = ObjectCopyHelper.copyObject(input);
    m_Traversal.traverse(new SwapObserver(this), input);
    return input;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    if (m_Traversal == null)
      m_Traversal = new PropertyTraversal();

    return doConvert(m_Input);
  }
}
