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
 * AbstractObjectPlainTextRenderer.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objecttree;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.ClassLister;

/**
 * Ancestor for classes that render certain objects as plain text.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectPlainTextRenderer {

  /** the cache for object class / renderer relation. */
  protected static Hashtable<Class,List<Class>> m_Cache;

  /** the renderers (classnames) currently available. */
  protected static String[] m_Renderers;

  /** the renderers (classes) currently available. */
  protected static Class[] m_RendererClasses;

  static {
    m_Cache          = new Hashtable<Class,List<Class>>();
    m_Renderers       = null;
    m_RendererClasses = null;
  }

  /**
   * Initializes the renderers.
   */
  protected static synchronized void initRenderers() {
    int		i;

    if (m_Renderers != null)
      return;

    m_Renderers       = ClassLister.getSingleton().getClassnames(AbstractObjectPlainTextRenderer.class);
    m_RendererClasses = new Class[m_Renderers.length];
    for (i = 0; i < m_Renderers.length; i++) {
      try {
	m_RendererClasses[i] = Class.forName(m_Renderers[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection renderer '" + m_Renderers[i] + "': ");
	e.printStackTrace();
      }
    }
  }

  /**
   * Returns a renderer for the specified object.
   *
   * @param obj		the object to get a commandline renderer for
   * @return		the renderer
   */
  public static synchronized List<AbstractObjectPlainTextRenderer> getRenderer(Object obj) {
    if (obj != null)
      return getRenderer(obj.getClass());
    else
      return getRenderer(Object.class);
  }

  /**
   * Instantiates the renderers.
   * 
   * @param renderers	the renderers to instantiate
   * @return		the instances
   */
  protected static List<AbstractObjectPlainTextRenderer> instantiate(List<Class> renderers) {
    List<AbstractObjectPlainTextRenderer>	result;
    int					i;
    
    result = new ArrayList<AbstractObjectPlainTextRenderer>();
    for (i = 0; i < renderers.size(); i++) {
      try {
	result.add((AbstractObjectPlainTextRenderer) renderers.get(i).newInstance());
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection renderer '" + renderers.get(i).getName() + "':");
	e.printStackTrace();
      }
    }
    
    return result;
  }
  
  /**
   * Returns a renderer for the specified class.
   *
   * @param cls		the class to get a commandline renderer for
   * @return		the renderer
   */
  public static synchronized List<AbstractObjectPlainTextRenderer> getRenderer(Class cls) {
    AbstractObjectPlainTextRenderer	renderer;
    List<Class>				renderers;
    int					i;

    initRenderers();

    // already cached?
    if (m_Cache.containsKey(cls))
      return instantiate(m_Cache.get(cls));

    // find suitable renderer
    renderers = new ArrayList<Class>();
    for (i = 0; i < m_RendererClasses.length; i++) {
      if (m_RendererClasses[i] == DefaultObjectRenderer.class)
	continue;
      try {
	renderer = (AbstractObjectPlainTextRenderer) m_RendererClasses[i].newInstance();
	if (renderer.handles(cls)) {
	  renderers.add(m_RendererClasses[i]);
	  break;
	}
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate inspection renderer '" + m_RendererClasses[i].getName() + "':");
	e.printStackTrace();
      }
    }

    if (renderers.size() == 0)
      renderers.add(DefaultObjectRenderer.class);

    // store in cache
    m_Cache.put(cls, renderers);

    return instantiate(renderers);
  }

  /**
   * Checks whether the renderer can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the renderer can handle this type of object
   */
  public abstract boolean handles(Class cls);

  /**
   * Performs the actual rendering.
   *
   * @param obj		the object to render
   * @return		the rendered string
   */
  protected abstract String doRender(Object obj);

  /**
   * Returns the rendered text.
   *
   * @param obj		the object to render
   * @return		the rendered string
   */
  public String render(Object obj) {
    if (obj == null)
      return "null";
    else
      return doRender(obj);
  }

  /**
   * Returns a list with classnames of renderers.
   *
   * @return		the renderer classnames
   */
  public static String[] getRenderers() {
    return ClassLister.getSingleton().getClassnames(AbstractObjectPlainTextRenderer.class);
  }
}
