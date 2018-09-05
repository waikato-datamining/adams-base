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
 * DefaultPropertyDiscovery.java
 * Copyright (C) 2015-2018 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

import adams.core.base.BaseRegExp;
import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyPath.PropertyContainer;
import adams.core.discovery.PropertyTraversal.Observer;
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
   * Observer using discovery handlers.
   */
  public class HandlerObserver
    implements Observer {

    /** the handlers to use. */
    protected AbstractDiscoveryHandler[] m_Handlers;

    /**
     * Initializes the observer with the handlers.
     *
     * @param handlers	the handlers to use
     */
    public HandlerObserver(AbstractDiscoveryHandler[] handlers) {
      m_Handlers = handlers;
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
      for (AbstractDiscoveryHandler handler : m_Handlers) {
	if (handler.handles(path, child))
	  handler.addContainer(new PropertyContainer(path, desc, child));
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
    PropertyTraversal	traversal;
    HandlerObserver	observer;

    observer  = new HandlerObserver(handlers);
    traversal = new PropertyTraversal();
    traversal.setLoggingLevel(getLoggingLevel());
    traversal.traverse(observer, obj);
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
