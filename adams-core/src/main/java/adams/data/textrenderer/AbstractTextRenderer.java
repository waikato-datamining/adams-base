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
 * AbstractTextRenderer.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.textrenderer;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for text renderer classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTextRenderer
  extends AbstractOptionHandler
  implements TextRenderer {

  private static final long serialVersionUID = -9153271896361695400L;

  /** all available renderers. */
  protected static List<TextRenderer> m_Renderers;

  /** the default renderer. */
  protected static TextRenderer m_Default;

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  protected String check(Object obj) {
    return null;
  }

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  protected abstract String doRender(Object obj);

  /**
   * Renders the object as text.
   *
   * @param obj		the object to render
   * @return		the generated string or null if failed to render
   */
  public String render(Object obj) {
    String	msg;

    if (obj == null)
      return "null";

    msg = check(obj);
    if (msg != null) {
      getLogger().warning(msg);
      return null;
    }

    return doRender(obj);
  }

  /**
   * Returns the default renderer.
   *
   * @return		the default
   */
  public static synchronized TextRenderer getDefaultRenderer() {
    if (m_Default == null)
      m_Default = new DefaultTextRenderer();
    return m_Default;
  }

  /**
   * Returns the renderer for the specified object.
   *
   * @param obj		the object to get the renderer for
   * @return		the renderer
   */
  public static synchronized TextRenderer getRenderer(Object obj) {
    if (obj == null)
      return getDefaultRenderer();

    return getRenderer(obj.getClass());
  }

  /**
   * Returns the renderer for the specified class.
   *
   * @param cls		the class to get the renderer for
   * @return		the renderer
   */
  public static synchronized TextRenderer getRenderer(Class cls) {
    TextRenderer	result;
    Class[]		renderers;

    result = getDefaultRenderer();

    // initialize list of renderers
    if (m_Renderers == null) {
      m_Renderers = new ArrayList<>();
      renderers   = ClassLister.getSingleton().getClasses(TextRenderer.class);
      for (Class renderer: renderers) {
        // skip default one
        if (renderer.equals(getDefaultRenderer().getClass()))
          continue;
        try {
          m_Renderers.add((TextRenderer) renderer.getDeclaredConstructor().newInstance());
	}
	catch (Exception e) {
          System.err.println("Failed to instantiate text renderer: " + Utils.classToString(renderer));
          e.printStackTrace();
	}
      }
    }

    // find renderer
    for (TextRenderer renderer: m_Renderers) {
      if (renderer.handles(cls)) {
        result = renderer;
        break;
      }
    }

    return result;
  }

  /**
   * Returns the renderers for the specified object.
   *
   * @param obj		the object to get the renderer for
   * @return		the renderers
   */
  public static synchronized List<TextRenderer> getRenderers(Object obj) {
    List<TextRenderer>	result;

    if (obj == null) {
      result = new ArrayList<>();
      result.add(getDefaultRenderer());
      return result;
    }

    return getRenderers(obj.getClass());
  }

  /**
   * Returns the renderers for the specified class.
   *
   * @param cls		the class to get the renderer for
   * @return		the renderers
   */
  public static synchronized List<TextRenderer> getRenderers(Class cls) {
    List<TextRenderer>	result;
    Class[]		renderers;

    result = new ArrayList<>();
    result.add(getDefaultRenderer());

    // initialize list of renderers
    if (m_Renderers == null) {
      m_Renderers = new ArrayList<>();
      renderers   = ClassLister.getSingleton().getClasses(TextRenderer.class);
      for (Class renderer: renderers) {
        // skip default one
        if (renderer.equals(getDefaultRenderer().getClass()))
          continue;
        try {
          m_Renderers.add((TextRenderer) renderer.getDeclaredConstructor().newInstance());
	}
	catch (Exception e) {
          System.err.println("Failed to instantiate text renderer: " + Utils.classToString(renderer));
          e.printStackTrace();
	}
      }
    }

    // find renderer
    for (TextRenderer renderer: m_Renderers) {
      if (renderer.handles(cls))
        result.add(renderer);
    }

    return result;
  }

  /**
   * Renders the object.
   *
   * @param obj		the object to render
   * @return		the generated string, null if failed to render
   */
  public static synchronized String renderObject(Object obj) {
    if (obj == null)
      return "null";
    else
      return getRenderer(obj).render(obj);
  }
}
