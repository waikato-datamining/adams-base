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
 * PropertyTraversal.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.Utils;
import adams.core.discovery.IntrospectionHelper.IntrospectionContainer;
import adams.core.discovery.PropertyPath.Path;
import adams.core.logging.CustomLoggingLevelObject;
import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.sink.Display;
import adams.flow.sink.Null;
import adams.flow.source.Start;
import adams.flow.transformer.PassThrough;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.logging.Level;

/**
 * Performs property traversal presenting the properties to an observer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertyTraversal
  extends CustomLoggingLevelObject {

  private static final long serialVersionUID = -7623097519689686115L;

  /**
   * Interface for traversal observers.
   */
  public interface Observer {

    /**
     * Presents the current path, descriptor and object to the observer.
     *
     * @param path	the path
     * @param desc	the property descriptor
     * @param parent	the parent object
     * @param child	the child object
     */
    public void observe(Path path, PropertyDescriptor desc, Object parent, Object child);
  }

  /**
   * Performs the property traversal.
   *
   * @param observer		the observer to use
   * @param obj			the object to analyze
   * @param path		the path so far
   */
  protected void traverse(Observer observer, Object obj, Path path) {
    IntrospectionContainer 	cont;
    PropertyDescriptor[] 	props;
    Object			child;
    int				i;
    int				len;
    Path			newPath;

    if (isLoggingEnabled())
      getLogger().info("traverse: " + path.toString());

    try {
      cont  = IntrospectionHelper.introspect(obj, false);
      props = cont.properties;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to analyze object: " + obj, e);
      props = null;
    }
    if (props == null)
      return;

    for (PropertyDescriptor prop: props) {
      if (Utils.isPrimitive(prop.getReadMethod().getReturnType()))
	continue;
      try {
	child = prop.getReadMethod().invoke(obj);
	if (child == null) {
	  getLogger().info("Read method of property '" + prop.getDisplayName() + "' returned 'null': " + path);
	  continue;
	}
        if (child.getClass().isArray()) {
          len = Array.getLength(child);
	  for (i = 0; i < len; i++) {
	    newPath = path.append(prop.getDisplayName() + "[" + i + "]");
	    observer.observe(newPath, prop, obj, Array.get(child, i));
	  }
	}
	else {
	  newPath = path.append(prop.getDisplayName());
	  observer.observe(newPath, prop, obj, child);
	}
	// recurse
        if (child.getClass().isArray()) {
          len = Array.getLength(child);
          for (i = 0; i < len; i++)
            traverse(observer, Array.get(child, i), path.append(prop.getDisplayName() + "[" + i + "]"));
        }
        else {
          traverse(observer, child, path.append(prop.getDisplayName()));
        }
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to obtain object from read method: " + path, e);
      }
    }
  }

  /**
   * Performs the property traversal.
   *
   * @param observer		the observer to use
   * @param obj			the object to analyze
   */
  public void traverse(Observer observer, Object obj) {
    Path	newPath;

    // check current object
    newPath = new Path(Path.CURRENT_OBJECT);
    observer.observe(newPath, null, null, obj);

    // check object's properties
    traverse(observer, obj, new Path());
  }

  /**
   * For testing only.
   *
   * @param args		ignored
   * @throws Exception		if discovery fails
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);

    PropertyTraversal traversal = new PropertyTraversal();

    Observer observer = new Observer() {
      @Override
      public void observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
	System.out.println(path + " --> " + (child == null ? "-" : child.getClass().getName()));
      }
    };

    Flow flow = new Flow();
    flow.add(new Start());
    flow.add(new PassThrough());
    Tee tee = new Tee();
    flow.add(tee);
    {
      tee.add(new PassThrough());
      tee.add(new Null());
    }
    flow.add(new Display());

    traversal.traverse(observer, flow);
  }
}
