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
 * ObjectCopyHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core;

import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionHandler;
import adams.core.option.OptionUtils;
import adams.core.objectinstance.AbstractObjectInstanceHandler;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.logging.Level;

/**
 * Helper class for copying objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ObjectCopyHelper {

  /** for logging. */
  protected static Logger LOGGER = LoggingHelper.getLogger(ObjectCopyHelper.class);

  /**
   * How to copy an object.
   */
  public enum CopyType {
    OPTIONHANDLER,
    CLONEHANDLER,
    SERIALIZABLE,
    NEWINSTANCE,
    UNSUPPORTED
  }

  /**
   * Determines how to copy an object.
   *
   * @param source 	the object to inspect, cannot be null
   * @return 		the copy type
   */
  public static CopyType copyType(Object source) {
    if (source == null)
      throw new IllegalArgumentException("Object cannot be null!");

    if (source instanceof OptionHandler)
      return CopyType.OPTIONHANDLER;
    else if (source instanceof CloneHandler)
      return CopyType.CLONEHANDLER;
    else if (source instanceof Serializable) {
      if (Utils.deepCopy(source, true) != null)
        return CopyType.SERIALIZABLE;
      else
        return CopyType.NEWINSTANCE;
    }
    else {
      try {
        newInstance(source.getClass());
        return CopyType.NEWINSTANCE;
      }
      catch (Exception e) {
        // ignored
      }
    }

    return CopyType.UNSUPPORTED;
  }

  /**
   * Makes a copy of an object.
   *
   * @param source 	the object to copy
   * @return 		a copy of the source object
   * @see		#copyObjects(CopyType, Object[])
   */
  public static Object copyObject(Object source) {
    return copyObject(null, source);
  }

  /**
   * Makes a copy of an object.
   *
   * @param type	the copy type to use, null to automatically determine
   * @param source 	the object to copy
   * @return 		a copy of the source object
   * @see		#copyObjects(CopyType, Object[])
   */
  public static Object copyObject(CopyType type, Object source) {
    if (source == null)
      return null;

    return copyObjects(type, new Object[]{source})[0];
  }

  /**
   * Makes a copy of the objects.
   *
   * @param source 	the objects to copy
   * @return 		a copy of the source objects
   * @see		#copyObjects(CopyType, Object[])
   */
  public static Object[] copyObjects(Object[] source) {
    return copyObjects(null, source);
  }

  /**
   * Makes a copy of the objects.
   *
   * @param type	the copy type to use, null to automatically determine
   * @param source 	the objects to copy
   * @return 		a copy of the source objects
   * @see		OptionUtils#shallowCopy
   * @see		CloneHandler#getClone()
   * @see		Utils#deepCopy(Object)
   * @see		AbstractObjectInstanceHandler#getHandler(Class)
   * @see		CopyType
   */
  public static Object[] copyObjects(CopyType type, Object[] source) {
    Object[] 				result;
    int					i;
    Class				cls;
    AbstractObjectInstanceHandler 	instHandler;

    if (source == null)
      return null;
    if (source.length == 0)
      return new Object[0];

    if (type == null)
      type = copyType(source[0]);

    result = (Object[]) Array.newInstance(source[0].getClass(), source.length);
    switch (type) {
      case OPTIONHANDLER:
	for (i = 0; i < source.length; i++)
	  result[i] = OptionUtils.shallowCopy(source[i]);
	break;
      case CLONEHANDLER:
	for (i = 0; i < source.length; i++)
	  result[i] = ((CloneHandler) source[i]).getClone();
	break;
      case SERIALIZABLE:
	result = (Object[]) Utils.deepCopy(source);
	break;
      case NEWINSTANCE:
	cls         = source[0].getClass();
	instHandler = AbstractObjectInstanceHandler.getHandler(cls);
	if (instHandler != null) {
	  for (i = 0; i < source.length; i++)
	    result[i] = instHandler.newInstance(cls);
	}
	else {
	  for (i = 0; i < source.length; i++) {
	    try {
	      result[i] = cls.newInstance();
	    }
	    catch (Exception e) {
	      if (i == 0) {
		LOGGER.log(Level.SEVERE, "Failed to instantiate class: " + cls.getName(), e);
	      }
	    }
	  }
	}
	break;
      default:
	throw new IllegalStateException("Unhandled type of object copying: " + type);
    }

    return result;
  }

  /**
   * Creates a new instance of the given class.
   *
   * @param cls		the class to create an instance from
   * @return		the object, null if failed
   * @throws Exception	if instantiation fails
   */
  public static Object newInstance(String cls) throws Exception {
    return newInstance(Class.forName(cls));
  }

  /**
   * Creates a new instance of the given class.
   *
   * @param cls		the class to create an instance from
   * @return		the object, null if failed
   * @throws Exception	if instantiation fails
   */
  public static Object newInstance(Class cls) throws Exception {
    AbstractObjectInstanceHandler	instHandler;

    instHandler = AbstractObjectInstanceHandler.getHandler(cls);
    if (instHandler != null)
      return instHandler.newInstance(cls);
    else
      return cls.newInstance();
  }
}
