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
 * DefaultPropertyDiscovery.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.discovery.IntrospectionHelper.IntrospectionContainer;
import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.logging.CustomLoggingLevelObject;
import adams.core.logging.LoggingLevel;
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
 * Class for performing object discovery.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultPropertyDiscovery
  extends CustomLoggingLevelObject
  implements PropertyDiscovery {

  private static final long serialVersionUID = -5792552578076134111L;

  /**
   * Performs the object discovery.
   *
   * @param handlers		the handlers to use and configure
   * @param obj			the object to analyze
   * @param path		the path so far
   */
  protected void discover(AbstractDiscoveryHandler[] handlers, Object obj, Path path) {
    IntrospectionContainer	cont;
    PropertyDescriptor[] 	props;
    Object			child;
    int				i;
    int				len;
    Path			newPath;

    if (isLoggingEnabled())
      getLogger().info("discover: " + path.toString());

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
	    for (AbstractDiscoveryHandler handler : handlers) {
	      newPath = path.append(prop.getDisplayName() + "[" + i + "]");
	      if (handler.handles(newPath, Array.get(child, i)))
		handler.addContainer(new PropertyContainer(newPath, prop, Array.get(child, i)));
	    }
	  }
	}
	else {
	  for (AbstractDiscoveryHandler handler : handlers) {
	    newPath = path.append(prop.getDisplayName());
	    if (handler.handles(newPath, child))
	      handler.addContainer(new PropertyContainer(newPath, prop, child));
	  }
	}
	// recurse
        if (child.getClass().isArray()) {
          len = Array.getLength(child);
          for (i = 0; i < len; i++)
            discover(handlers, Array.get(child, i), path.append(prop.getDisplayName() + "[" + i + "]"));
        }
        else {
          discover(handlers, child, path.append(prop.getDisplayName()));
        }
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to obtain object from read method: " + path, e);
      }
    }
  }

  /**
   * Performs the object discovery.
   *
   * @param handlers		the handlers to use and configure
   * @param obj			the object to analyze
   */
  @Override
  public void discover(AbstractDiscoveryHandler[] handlers, Object obj) {
    Path	newPath;

    // check current object
    for (AbstractDiscoveryHandler handler : handlers) {
      newPath = new Path(Path.CURRENT_OBJECT);
      if (handler.handles(newPath, obj))
	handler.addContainer(new PropertyContainer(newPath, null, obj));
    }
    // check object's properties
    discover(handlers, obj, new Path());
  }

  /**
   * For testing only.
   *
   * @param args		ignored
   * @throws Exception		if discovery fails
   */
  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);

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

    Actor[] handlers = new Actor[1];
    handlers[0] = new Actor();

    DefaultPropertyDiscovery d = new DefaultPropertyDiscovery();
    d.setLoggingLevel(LoggingLevel.FINE);
    d.discover(handlers, flow);

    for (AbstractDiscoveryHandler handler: handlers)
      System.out.println(handler.toCommandLine() + "\n" + handler + "\n");

    handlers = new Actor[1];
    handlers[0] = new Actor();
    handlers[0].setRegExp(new BaseRegExp(".*actors\\[1\\]$"));

    d = new DefaultPropertyDiscovery();
    d.setLoggingLevel(LoggingLevel.FINE);
    d.discover(handlers, flow);

    for (AbstractDiscoveryHandler handler: handlers)
      System.out.println(handler.toCommandLine() + "\n" + handler + "\n");
  }
}
