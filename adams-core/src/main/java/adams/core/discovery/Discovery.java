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
 * Discovery.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.Utils;
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
public class Discovery
  extends CustomLoggingLevelObject {

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
	child = prop.getReadMethod().invoke(obj, new Object[0]);
        if (child.getClass().isArray()) {
          len = Array.getLength(child);
          for (i = 0; i < len; i++) {
	    for (AbstractDiscoveryHandler handler : handlers) {
	      if (handler.handles(child))
		handler.addContainer(new PropertyContainer(path.append(prop.getDisplayName() + "[" + i + "]"), prop, child));
	    }
	  }
	}
	else {
	  for (AbstractDiscoveryHandler handler : handlers) {
	    if (handler.handles(child))
	      handler.addContainer(new PropertyContainer(path.append(prop.getDisplayName()), prop, child));
	  }
	}
	// recurse
        if (child.getClass().isArray()) {
          len = Array.getLength(child);
          for (i = 0; i < len; i++)
            discover(handlers, Array.get(child, i), path.append(prop.getDisplayName()));
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
  public void discover(AbstractDiscoveryHandler[] handlers, Object obj) {
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

    AbstractDiscoveryHandler[] handlers = new AbstractDiscoveryHandler[]{
      new ActorDiscoveryHandler()
    };

    Discovery d = new Discovery();
    d.setLoggingLevel(LoggingLevel.FINE);
    d.discover(handlers, flow);

    for (AbstractDiscoveryHandler handler: handlers)
      System.out.println(handler + "\n");
  }
}
