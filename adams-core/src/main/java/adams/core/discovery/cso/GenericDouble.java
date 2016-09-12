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
 * GenericDoubleResolution.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery.cso;

import adams.core.ClassLocator;
import adams.core.Utils;
import adams.core.base.BaseClassname;
import adams.core.discovery.IntrospectionHelper;
import adams.core.discovery.IntrospectionHelper.IntrospectionContainer;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.data.outlier.MinMax;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Generic handler for double properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GenericDouble
  extends AbstractCatSwarmOptimizationDoubleDiscoveryHandler {

  private static final long serialVersionUID = 9168998412950337023L;

  /** the class name. */
  protected BaseClassname m_Classname;

  /** the actual class. */
  protected transient Class m_ActualClass;

  /** the property name. */
  protected String m_Property;

  /** the property descriptor. */
  protected transient PropertyDescriptor m_PropertyDescriptor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generic handler for double properties.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "classname", "classname",
      getDefaultClassname());

    m_OptionManager.add(
      "property", "property",
      getDefaultProperty());
  }

  @Override
  protected void reset() {
    super.reset();

    m_ActualClass        = null;
    m_PropertyDescriptor = null;
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMinimum() {
    return 10.0;
  }

  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  @Override
  protected double getDefaultMaximum() {
    return 25.0;
  }

  /**
   * Returns the default list.
   *
   * @return		the default
   */
  protected String getDefaultList() {
    return "10.0 15.0 20.0 25.0";
  }

  /**
   * Returns the default classname.
   *
   * @return		the default
   */
  protected BaseClassname getDefaultClassname() {
    return new BaseClassname(MinMax.class);
  }

  /**
   * Sets the classname to be the handler for.
   *
   * @param value	the classname
   */
  public void setClassname(BaseClassname value) {
    m_Classname = value;
    reset();
  }

  /**
   * Returns the classname to be the handler for.
   *
   * @return		the classname
   */
  public BaseClassname getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The classname of the objects to handle.";
  }

  /**
   * Returns the default property.
   *
   * @return		the default
   */
  protected String getDefaultProperty() {
    return "min";
  }

  /**
   * Sets the property to manage.
   *
   * @param value	the property
   */
  public void setProperty(String value) {
    m_Property = value;
    reset();
  }

  /**
   * Returns the property to manage.
   *
   * @return		the property
   */
  public String getProperty() {
    return m_Property;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propertyTipText() {
    return "The property of the objects to manage.";
  }

  /**
   * Returns the class that is being handled.
   *
   * @return				the class
   * @throws IllegalStateException	if no valid classname provided
   */
  protected synchronized Class getActualClass() {
    if (m_ActualClass == null) {
      m_ActualClass = m_Classname.classValue();
      if (m_ActualClass == null)
	throw new IllegalStateException("No valid class name provided? " + m_Classname);
    }

    return m_ActualClass;
  }

  /**
   * Returns the property descriptor for the handled property.
   *
   * @return				the descriptor
   * @throws IllegalStateException	if introspection fails
   */
  protected synchronized PropertyDescriptor getPropertyDescriptor() {
    IntrospectionContainer 	cont;

    if (m_PropertyDescriptor == null) {
      try {
	cont = IntrospectionHelper.introspect(getActualClass());
	for (PropertyDescriptor pd: cont.properties) {
	  if (pd.getDisplayName().equals(m_Property)) {
	    m_PropertyDescriptor = pd;
	    break;
	  }
	}
      }
      catch (Exception e) {
	throw new IllegalStateException("Failed to introspect: " + m_Classname + "/" + m_Property, e);
      }
    }

    return m_PropertyDescriptor;
  }

  /**
   * Returns the number of dimensions (= double values) this scheme requires.
   *
   * @return		the dimensions
   */
  public int getDimensions() {
    return 1;
  }

  /**
   * Returns the double values from the property container.
   *
   * @param cont	the container
   * @return		the values
   */
  protected double[] getValue(PropertyContainer cont) {
    double[]		result;
    Double		value;
    PropertyDescriptor	pd;
    Method		method;

    pd     = getPropertyDescriptor();
    method = pd.getReadMethod();
    try {
      value = (Double) method.invoke(cont.getObject());
      if (value == null)
	throw new IllegalStateException("Property '" + m_Property + "' of class '" + m_Classname + "' returned null!");
      result = new double[]{value};
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to get value from property '" + m_Property + "' of class '" + m_Classname + "'!", e);
      result = new double[]{Double.NaN};
    }

    return result;
  }

  /**
   * Sets the double values in the property container.
   *
   * @param cont	the container
   * @param value	the values to set
   */
  protected void setValue(PropertyContainer cont, double[] value) {
    PropertyDescriptor	pd;
    Method		method;

    pd     = getPropertyDescriptor();
    method = pd.getWriteMethod();

    try {
      method.invoke(cont.getObject(), value[0]);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to set values '" + Utils.arrayToString(value) + "' for property '" + m_Property + "' of class '" + m_Classname + "'!", e);
    }
  }

  /**
   * Checks whether this object is handled by this discovery handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  @Override
  protected boolean handles(Object obj) {
    return ClassLocator.isSubclass(getActualClass(), obj.getClass())
      || ClassLocator.hasInterface(getActualClass(), obj.getClass());
  }
}
