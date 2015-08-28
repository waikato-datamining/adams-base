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
 * PropertyPath.java
 * Copyright (C) 2006-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core.discovery;

import adams.core.CloneHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A helper class for accessing properties in nested objects, e.g., accessing
 * the "getRidge" method of a LinearRegression classifier part of
 * MultipleClassifierCombiner, e.g., Vote. For doing so, one needs to
 * supply the object to work on and a property path. The property path is a
 * dot delimited path of property names ("getFoo()" and "setFoo(int)" have
 * "foo" as property name), indices of arrays are 0-based. E.g.: <br><br>
 *
 * <code>getPropertyDescriptor(vote, "classifiers[1].ridge")</code> will return
 * the second classifier (which should be our LinearRegression) of the given
 * Vote meta-classifier and there the property descriptor of the "ridge"
 * property. <code>getValue(...)</code> will return the actual value of the
 * ridge parameter and <code>setValue(...)</code> will set it.<br><br>
 * 
 * Using <code>.get(X)</code> it is possible to access the items of 
 * {@link List} objects, with "X" being the index for the "get(index)" method.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PropertyPath {

  /**
   * The type of path element we're dealing with.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum PathElementType {
    /** object. */
    OBJECT,
    /** array. */
    ARRAY,
    /** list. */
    LIST
  }

  /**
   * Represents a single element of a property path.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PathElement
    implements CloneHandler<PathElement> {

    /** the property. */
    protected String m_Name;

    /** the index of the array (-1 for none). */
    protected int m_Index;
    
    /** the element type. */
    protected PathElementType m_Type;

    /**
     * initializes the path element with the given property.
     *
     * @param property	the property to initialize with
     */
    public PathElement(String property) {
      super();

      if (property.indexOf("[") > -1) {
	m_Name  = property.replaceAll("\\[.*$", "");
	m_Index = Integer.parseInt(
    		     property.replaceAll(".*\\[", "").replaceAll("\\].*", ""));
	m_Type  = PathElementType.ARRAY;
      }
      else if (property.indexOf("(") > -1) {
	m_Name  = property.replaceAll("\\(.*$", "");
	if (!m_Name.equals("get"))
	  throw new IllegalArgumentException("The path element used for lists must be 'get', provided: '" + m_Name + "'");
	m_Index = Integer.parseInt(
    		     property.replaceAll(".*\\(", "").replaceAll("\\).*", ""));
	m_Type  = PathElementType.LIST;
      }
      else {
	m_Name  = property;
	m_Index = -1;
	m_Type  = PathElementType.OBJECT;
      }
    }

    /**
     * returns a clone of the current object.
     *
     * @return		the clone of the current state
     */
    public PathElement getClone() {
      return new PathElement(this.toString());
    }

    /**
     * returns the name of the property.
     *
     * @return		the name of the property
     */
    public String getName() {
      return m_Name;
    }

    /**
     * returns the index of the property, -1 if the property is not an
     * index-based one.
     *
     * @return		the index of the property
     */
    public int getIndex() {
      return m_Index;
    }

    /**
     * Returns the type of path element this is.
     * 
     * @return		the type
     */
    public PathElementType getType() {
      return m_Type;
    }
    
    /**
     * returns the element once again as string.
     *
     * @return		the property as string
     */
    @Override
    public String toString() {
      String	result;

      result = getName();
      
      switch (m_Type) {
	case OBJECT:
	  // nothing
	  break;
	case ARRAY:
	  result += "[" + getIndex() + "]";
	  break;
	case LIST:
	  result += "(" + getIndex() + ")";
	  break;
	default:
	  throw new IllegalStateException("Unhandled path element type: " + m_Type);
      }

      return result;
    }
  }

  /**
   * Contains a (property) path structure.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Path {

    /** the path for the current object (only used at root). */
    public final static String CURRENT_OBJECT = ".";

    /** the structure. */
    protected List<PathElement> m_Elements;

    /** the full path. */
    protected String m_FullPath;

    /**
     * default constructor, only used internally.
     */
    protected Path() {
      super();

      m_Elements = new ArrayList<>();
      m_FullPath = null;
    }

    /**
     * uses the given dot-path.
     *
     * @param path	path in dot-notation
     */
    public Path(String path) {
      this();

      m_Elements = breakUp(path);
      m_FullPath = null;
    }

    /**
     * uses the vector with PathElement objects to initialize with.
     *
     * @param elements	the PathElements to use
     */
    public Path(List<PathElement> elements) {
      this();

      for (int i = 0; i < elements.size(); i++)
	m_Elements.add((elements.get(i)).getClone());

      m_FullPath = null;
    }

    /**
     * uses the given array as elements for the path.
     *
     * @param elements	the path elements to use
     */
    public Path(String[] elements) {
      this();

      for (int i = 0; i < elements.length; i++)
	m_Elements.add(new PathElement(elements[i]));

      m_FullPath = null;
    }

    /**
     * breaks up the given path and returns it as vector.
     *
     * @param path	the path to break up
     * @return		the single elements of the path
     */
    protected List<PathElement> breakUp(String path) {
      List<PathElement>		result;
      StringTokenizer		tok;

      result = new ArrayList<>();

      if (path.equals(CURRENT_OBJECT)) {
	result.add(new PathElement(CURRENT_OBJECT));
      }
      else {
	tok = new StringTokenizer(path, ".");
	while (tok.hasMoreTokens())
	  result.add(new PathElement(tok.nextToken()));
      }

      return result;
    }

    /**
     * returns the element at the given index.
     *
     * @param index	the index of the element to return
     * @return		the specified element
     */
    public PathElement get(int index) {
      return m_Elements.get(index);
    }

    /**
     * returns the number of path elements of this structure.
     *
     * @return		the number of path elements
     */
    public int size() {
      return m_Elements.size();
    }

    /**
     * returns a path object based on the given path string.
     *
     * @param path	path to work on
     * @return		the path structure
     */
    public static Path parsePath(String path) {
      return new Path(path);
    }

    /**
     * returns a subpath of the current structure, starting with the specified
     * element index up to the end.
     *
     * @param startIndex	the first element of the subpath
     * @return			the new subpath
     */
    public Path subpath(int startIndex) {
      return subpath(startIndex, size());
    }

    /**
     * returns a subpath of the current structure, starting with the specified
     * element index up. The endIndex specifies the element that is not part
     * of the new subpath. In other words, the new path contains the elements
     * from "startIndex" up to "(endIndex-1)".
     *
     * @param startIndex	the first element of the subpath
     * @param endIndex		the element that is after the last added element
     * @return			the new subpath
     */
    public Path subpath(int startIndex, int endIndex) {
      List<PathElement>		list;
      int			i;

      list = new ArrayList<>();
      for (i = startIndex; i < endIndex; i++)
	list.add(get(i));

      return new Path(list);
    }

    /**
     * Adds the subpath to the current list of path elements and returns
     * the extended Path object. Does not change this path object.
     *
     * @return			the new path
     */
    public Path append(String subpath) {
      List<PathElement>		list;

      list = new ArrayList<>(m_Elements);
      list.add(new PathElement(subpath));

      return new Path(list);
    }

    /**
     * Checks whether this is the special path denoting the current object.
     *
     * @return		true if the current object
     * @see		#CURRENT_OBJECT
     */
    public boolean isCurrentObject() {
      return getFullPath().equals(CURRENT_OBJECT);
    }

    /**
     * returns the structure again as a dot-path.
     *
     * @return		the path structure as dot-path
     */
    @Override
    public synchronized String toString() {
      return getFullPath();
    }

    /**
     * returns the structure again as a dot-path.
     *
     * @return		the path structure as dot-path
     */
    public synchronized String getFullPath() {
      StringBuilder	path;
      int		i;

      if (m_FullPath == null) {
	path = new StringBuilder();

	for (i = 0; i < m_Elements.size(); i++) {
	  if (i > 0)
	    path.append(".");
	  path.append(m_Elements.get(i));
	}

	m_FullPath = path.toString();
      }

      return m_FullPath;
    }
  }

  /**
   * A helper class that stores Object and PropertyDescriptor together.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PropertyContainer {

    /** the path to this property. */
    protected Path m_Path;

    /** the descriptor. */
    protected PropertyDescriptor m_Descriptor;
    
    /** the read method. */
    protected Method m_Read;
    
    /** the write method. */
    protected Method m_Write;

    /** the associated object. */
    protected Object m_Object;

    /**
     * Initializes the container.
     *
     * @param path	the path to this property
     * @param desc	the property descriptor
     * @param obj	the associated object
     */
    public PropertyContainer(Path path, PropertyDescriptor desc, Object obj) {
      super();

      m_Path       = path;
      m_Descriptor = desc;
      m_Read       = null;
      m_Write      = null;
      m_Object     = obj;
    }

    /**
     * Initializes the container.
     *
     * @param path	the path to this property
     * @param read	the read method
     * @param write	the write method
     * @param obj	the associated object
     */
    public PropertyContainer(Path path, Method read, Method write, Object obj) {
      super();

      m_Path       = path;
      m_Descriptor = null;
      m_Read       = read;
      m_Write      = write;
      m_Object     = obj;
    }

    /**
     * Returns the associated path.
     *
     * @return		the path
     */
    public Path getPath() {
      return m_Path;
    }

    /**
     * Returns the read method.
     * 
     * @return		the method
     */
    public Method getReadMethod() {
      if (m_Read != null)
	return m_Read;
      else if (m_Descriptor != null)
	return m_Descriptor.getReadMethod();
      else
	return null;
    }

    /**
     * Returns the write method.
     * 
     * @return		the method
     */
    public Method getWriteMethod() {
      if (m_Write != null)
	return m_Write;
      else if (m_Descriptor != null)
	return m_Descriptor.getWriteMethod();
      else
	return null;
    }
    
    /**
     * returns the stored object.
     *
     * @return		the stored object
     */
    public Object getObject() {
      return m_Object;
    }
  }

  /**
   * returns the property and object associated with the given path, null if
   * a problem occurred.
   *
   * @param src		the object to start from
   * @param path	the path to follow
   * @return		not null, if the property could be found
   */
  public static PropertyContainer find(Object src, String path) {
    return find(src, new Path(path));
  }

  /**
   * returns the property and object associated with the given path, null if
   * a problem occurred.
   *
   * @param src		the object to start from
   * @param path	the path to follow
   * @return		not null, if the property could be found
   */
  public static PropertyContainer find(Object src, Path path) {
    return find(src, path, path);
  }

  /**
   * returns the property and object associated with the given path, null if
   * a problem occurred.
   *
   * @param src		the object to start from
   * @param current	the path to follow
   * @param full	the full path
   * @return		not null, if the property could be found
   */
  protected static PropertyContainer find(Object src, Path current, Path full) {
    PropertyContainer	result;
    PropertyDescriptor	desc;
    Object		newSrc;
    PathElement		part;
    Method		method;
    Object		methodResult;
    Method		read;
    Method		write;

    part  = current.get(0);
    desc  = null;
    read  = null;
    write = null;

    if (part.getType() == PathElementType.LIST) {
      try {
	read  = src.getClass().getMethod("get", new Class[]{Integer.TYPE});
	write = src.getClass().getMethod("set", new Class[]{Integer.TYPE, Object.class});
      }
      catch (Exception e) {
	read  = null;
	write = null;
      }

      // problem occurred? -> stop
      if (read == null)
	return null;

      // end of path reached?
      if (current.size() == 1) {
	result = new PropertyContainer(full, read, write, src);
      }
      // recurse further
      else {
	try {
	  methodResult = read.invoke(src, new Object[]{part.getIndex()});
	  if (part.getType() == PathElementType.ARRAY)
	    newSrc = Array.get(methodResult, part.getIndex());
	  else
	    newSrc = methodResult;
	  result = find(newSrc, current.subpath(1));
	}
	catch (Exception e) {
	  result = null;
	  e.printStackTrace();
	}
      }
    }
    else {
      try {
	desc = new PropertyDescriptor(part.getName(), src.getClass());
      }
      catch (Exception e) {
	desc = null;
	e.printStackTrace();
      }

      // problem occurred? -> stop
      if (desc == null)
	return null;

      // end of path reached?
      if (current.size() == 1) {
	result = new PropertyContainer(full, desc, src);
      }
      // recurse further
      else {
	try {
	  method       = desc.getReadMethod();
	  methodResult = method.invoke(src, (Object[]) null);
	  if (part.getType() == PathElementType.ARRAY)
	    newSrc = Array.get(methodResult, part.getIndex());
	  else
	    newSrc = methodResult;
	  result = find(newSrc, current.subpath(1));
	}
	catch (Exception e) {
	  result = null;
	  e.printStackTrace();
	}
      }
    }

    return result;
  }

  /**
   * returns the value specified by the given path from the object.
   *
   * @param src		the object to work on
   * @param path	the retrieval path
   * @return		the value, null if an error occurred
   */
  public static Object getValue(Object src, Path path) {
    Object		result;
    PropertyContainer	cont;
    Method		method;
    Object		methodResult;
    PathElement		part;

    result = null;

    cont = find(src, path);
    // problem?
    if (cont == null)
      return null;

    // retrieve the value
    try {
      part         = path.get(path.size() - 1);
      method       = cont.getReadMethod();
      if (part.getType() == PathElementType.LIST)
	methodResult = method.invoke(cont.getObject(), new Object[]{part.getIndex()});
      else
	methodResult = method.invoke(cont.getObject(), (Object[]) null);
      if (part.getType() == PathElementType.ARRAY)
	result = Array.get(methodResult, part.getIndex());
      else
	result = methodResult;
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * returns the value specified by the given path from the object.
   *
   * @param src		the object to work on
   * @param path	the retrieval path
   * @return		the value, null if an error occurred
   */
  public static Object getValue(Object src, String path) {
    return getValue(src, new Path(path));
  }

  /**
   * set the given value specified by the given path in the object.
   *
   * @param src		the object to work on
   * @param path	the retrieval path
   * @param value	the value to set
   * @return		true if the value could be set
   */
  public static boolean setValue(Object src, Path path, Object value) {
    boolean		result;
    PropertyContainer	cont;
    Method		methodRead;
    Method		methodWrite;
    Object		methodResult;
    PathElement		part;

    result = false;

    cont = find(src, path);
    // problem?
    if (cont == null)
      return result;

    // set the value
    try {
      part         = path.get(path.size() - 1);
      methodRead   = cont.getReadMethod();
      methodWrite  = cont.getWriteMethod();
      if (part.getType() == PathElementType.ARRAY) {
	methodResult = methodRead.invoke(cont.getObject(), (Object[]) null);
	Array.set(methodResult, part.getIndex(), value);
	methodWrite.invoke(cont.getObject(), new Object[]{methodResult});
      }
      else {
	methodWrite.invoke(cont.getObject(), new Object[]{value});
      }
      result = true;
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * set the given value specified by the given path in the object.
   *
   * @param src		the object to work on
   * @param path	the retrieval path
   * @param value	the value to set
   * @return		true if succesfully set
   */
  public static boolean setValue(Object src, String path, Object value) {
    return setValue(src, new Path(path), value);
  }

  /**
   * for testing only.
   *
   * @param args	the commandline options - ignored
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    // Path
    Path path = new Path("hello.world[2].nothing");
    System.out.println("Path: " + path);
    System.out.println(" -size: " + path.size());
    System.out.println(" -elements:");
    for (int i = 0; i < path.size(); i++)
      System.out.println(
	  "  " + i + ". " + path.get(i).getName()
	  + " -> " + path.get(i).getIndex());
  }
}
