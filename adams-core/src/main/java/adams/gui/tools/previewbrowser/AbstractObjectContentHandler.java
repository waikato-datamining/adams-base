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
 * AbstractObjectContentHandler.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Ancestor for content handlers that handle files and objects.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectContentHandler
  extends AbstractContentHandler
  implements ObjectContentHandler {

  private static final long serialVersionUID = -3198323865188366884L;

  /** the cache for managed classes. */
  protected static Map<Class,List<Class<? extends ObjectContentHandler>>> m_ObjectHandlerMapping;

  /**
   * Reuses the last preview, if possible.
   * <br>
   * Default implementation just creates a new preview.
   *
   * @param obj		the object to create the view for
   * @return		the preview
   */
  @Override
  public PreviewPanel reusePreview(Object obj, PreviewPanel lastPreview) {
    return createPreview(obj);
  }

  /**
   * Checks whether the object is handled by this content handler.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean canHandle(Object obj) {
    if (obj == null)
      return false;
    else
      return canHandle(obj.getClass());
  }

  /**
   * Hook method for checks before generating the preview.
   *
   * @param obj		the object to check
   * @return		null if checks passed, otherwise error message
   */
  protected String checkObject(Object obj) {
    String	result;

    result = null;

    if (obj == null)
      result = "No object supplied!";

    return result;
  }

  /**
   * Returns the preview for the specified object.
   *
   * @param obj		the object to create the view for
   * @return		the preview, NoPreviewAvailablePanel in case of an error
   * @see		NoPreviewAvailablePanel
   */
  @Override
  public PreviewPanel getPreview(Object obj) {
    String	msg;

    msg = checkObject(obj);
    if (msg == null) {
      try {
	return createPreview(obj);
      }
      catch (Exception e) {
	msg = "Failed to create preview with " + getClass().getName() + ":\n\n" + LoggingHelper.throwableToString(e);
	getLogger().log(Level.SEVERE, msg);
      }
    }
    return new NoPreviewAvailablePanel(msg);

  }

  /**
   * Returns a list with classnames of handlers.
   *
   * @return		the handler classnames
   */
  public static String[] getObjectHandlers() {
    return ClassLister.getSingleton().getClassnames(ObjectContentHandler.class);
  }

  /**
   * Checks whether the specified object can be managed.
   *
   * @param obj		the object to check
   * @return		true if the object represents a managed object
   */
  public static boolean hasObjectHandler(Object obj) {
    return (getObjectHandlersFor(obj) != null);
  }

  /**
   * Checks whether the specified class can be managed.
   *
   * @param cls		the object to check
   * @return		true if the object represents a managed object
   */
  public static boolean hasObjectHandler(Class cls) {
    return (getObjectHandlersFor(cls) != null);
  }

  /**
   * Returns the handlers registered for the object.
   *
   * @param obj		the object to get the handlers for
   * @return		the handlers, null if none available
   */
  public static List<Class<? extends ObjectContentHandler>> getObjectHandlersFor(Object obj) {
    if (obj == null)
      return null;
    else
      return getObjectHandlersFor(obj.getClass());
  }

  /**
   * Returns the handlers registered for the class.
   *
   * @param cls		the class to get the handlers for
   * @return		the handlers, null if none available
   */
  public static synchronized List<Class<? extends ObjectContentHandler>> getObjectHandlersFor(Class cls) {
    List<Class<? extends ObjectContentHandler>>	handlers;
    ObjectContentHandler			handler;
    Class					handlerCls;

    if (m_ObjectHandlerMapping == null)
      m_ObjectHandlerMapping = new HashMap<>();

    if (!m_ObjectHandlerMapping.containsKey(cls)) {
      handlers = new ArrayList<>();
      for (String clsname : getObjectHandlers()) {
	try {
	  handlerCls = ClassManager.getSingleton().forName(clsname);
	  // skip default handler, gets added at the end
	  if (handlerCls.equals(PlainTextHandler.class))
	    continue;
	  handler = (ObjectContentHandler) handlerCls.getDeclaredConstructor().newInstance();
	  if (handler.canHandle(cls))
	    handlers.add(handler.getClass());
	}
	catch (Exception e) {
	  System.err.println("Error processing object content handler: " + clsname);
	  e.printStackTrace();
	}
      }
      // add default handler
      handlers.add(PlainTextHandler.class);
      // add to cache
      m_ObjectHandlerMapping.put(cls, handlers);
    }

    handlers = m_ObjectHandlerMapping.get(cls);
    if (handlers.size() == 0)
      return null;
    else
      return handlers;
  }
}
