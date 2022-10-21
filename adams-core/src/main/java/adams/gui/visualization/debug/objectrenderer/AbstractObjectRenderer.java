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
 * AbstractObjectRenderer.java
 * Copyright (C) 2015-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objectrenderer;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingObject;

import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Ancestor for classes that render objects visually.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectRenderer
  extends LoggingObject {

  private static final long serialVersionUID = -7742758428210374232L;

  /** the cache for object class / renderer relation. */
  protected static Hashtable<Class,List<Class>> m_Cache;

  /** the renderers (classnames) currently available. */
  protected static String[] m_Renderers;

  /** the renderers (classes) currently available. */
  protected static Class[] m_RendererClasses;

  static {
    m_Cache          = new Hashtable<>();
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

    m_Renderers       = ClassLister.getSingleton().getClassnames(AbstractObjectRenderer.class);
    m_RendererClasses = new Class[m_Renderers.length];
    for (i = 0; i < m_Renderers.length; i++) {
      try {
	m_RendererClasses[i] = ClassManager.getSingleton().forName(m_Renderers[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object renderer '" + m_Renderers[i] + "': ");
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
  public static synchronized List<AbstractObjectRenderer> getRenderer(Object obj) {
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
  protected static List<AbstractObjectRenderer> instantiate(List<Class> renderers) {
    List<AbstractObjectRenderer>	result;
    int					i;
    
    result = new ArrayList<>();
    for (i = 0; i < renderers.size(); i++) {
      try {
	result.add((AbstractObjectRenderer) renderers.get(i).getDeclaredConstructor().newInstance());
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object renderer '" + renderers.get(i).getName() + "':");
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
  public static synchronized List<AbstractObjectRenderer> getRenderer(Class cls) {
    AbstractObjectRenderer renderer;
    List<Class>				renderers;
    int					i;

    initRenderers();

    // already cached?
    if (m_Cache.containsKey(cls))
      return instantiate(m_Cache.get(cls));

    // find suitable renderer
    renderers = new ArrayList<>();
    for (i = 0; i < m_RendererClasses.length; i++) {
      if (m_RendererClasses[i] == PlainTextRenderer.class)
	continue;
      try {
	renderer = (AbstractObjectRenderer) m_RendererClasses[i].getDeclaredConstructor().newInstance();
	if (renderer.handles(cls)) {
	  renderers.add(m_RendererClasses[i]);
	  break;
	}
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object renderer '" + m_RendererClasses[i].getName() + "':");
	e.printStackTrace();
      }
    }

    if (renderers.size() == 0)
      renderers.add(PlainTextRenderer.class);

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
   * Performs the actual rendering with a new renderer setup.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  protected abstract String doRender(Object obj, JPanel panel);

  /**
   * Renders the object with a new renderer setup.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  public String render(Object obj, JPanel panel) {
    String	result;

    if (obj == null) {
      result = "No object provided!";
    }
    else {
      result = doRender(obj, panel);
      if (result == null) {
	panel.invalidate();
	panel.validate();
	panel.repaint();
      }
    }

    return result;
  }

  /**
   * Checks whether the renderer can use a cached setup to render an object.
   * <br>
   * Default implementation returns false.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		true if possible
   */
  public boolean canRenderCached(Object obj, JPanel panel) {
    return false;
  }

  /**
   * Performs the actual rendering using a cached setup.
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  protected String doRenderCached(Object obj, JPanel panel) {
    return null;
  }

  /**
   * Renders the object using a cached setup (if available).
   *
   * @param obj		the object to render
   * @param panel	the panel to render into
   * @return		null if successful, otherwise error message
   */
  public String renderCached(Object obj, JPanel panel) {
    String	result;

    if (!canRenderCached(obj, panel))
      return render(obj, panel);

    if (obj == null) {
      result = "No object provided!";
    }
    else {
      result = doRenderCached(obj, panel);
      if (result == null) {
	panel.invalidate();
	panel.validate();
	panel.repaint();
      }
    }

    return result;
  }

  /**
   * Returns a list with classnames of renderers.
   *
   * @return		the renderer classnames
   */
  public static String[] getRenderers() {
    return ClassLister.getSingleton().getClassnames(AbstractObjectRenderer.class);
  }
}
