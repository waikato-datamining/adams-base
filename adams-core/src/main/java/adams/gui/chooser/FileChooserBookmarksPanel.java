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
 * FileChooserBookmarksPanel.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import adams.core.Properties;
import adams.env.Environment;
import adams.gui.core.GUIHelper;
import com.googlecode.jfilechooserbookmarks.AbstractBookmarksPanel;
import com.googlecode.jfilechooserbookmarks.AbstractFactory;
import com.googlecode.jfilechooserbookmarks.AbstractIconLoader;
import com.googlecode.jfilechooserbookmarks.AbstractPropertiesHandler;
import com.googlecode.jfilechooserbookmarks.DefaultFactory;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

/**
 * Panel for bookmarking directories in a {@link JFileChooser}.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileChooserBookmarksPanel
  extends AbstractBookmarksPanel {

  /** for serialization. */
  private static final long serialVersionUID = -1969362821325599909L;

  /** the properties to store the bookmarks in. */
  public final static String FILENAME = "FileChooserBookmarks.props";

  /**
   * The ADAMS-specific properties handler.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileChooserBookmarksPropertiesHandler
    extends AbstractPropertiesHandler {

    /** for serialization. */
    private static final long serialVersionUID = 396276849965032378L;

    /**
     * Not used.
     *
     * @return		always null
     */
    @Override
    protected String getFilename() {
      return null;
    }

    /**
     * Loads the properties.
     *
     * @return		the properties loaded from disk
     */
    @Override
    public java.util.Properties loadProperties() {
      try {
        return Properties.read(FILENAME);
      }
      catch (Exception e) {
        return new Properties();
      }
    }

    /**
     * Saves the properties.
     *
     * @param props	the properties to save
     */
    @Override
    public boolean saveProperties(java.util.Properties props) {
      Properties	properties;
      if (!(props instanceof Properties))
        properties = new Properties(props);
      else
        properties = (Properties) props;
      return properties.save(Environment.getInstance().createPropertiesFilename(FILENAME));
    }
  }

  /**
   * ADAMS-specific icon loader.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileChooserBookmarksIconLoader
    extends AbstractIconLoader {

    /** for serialization. */
    private static final long serialVersionUID = 6580294780671707612L;

    /**
     * Returns the "down" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getDown() {
      return GUIHelper.getIcon("arrow_down.gif");
    }

    /**
     * Returns the "up" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getUp() {
      return GUIHelper.getIcon("arrow_up.gif");
    }

    /**
     * Returns the "add" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getAdd() {
      return GUIHelper.getIcon("add.gif");
    }

    /**
     * Returns the "remove" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getRemove() {
      return GUIHelper.getIcon("remove.gif");
    }

    /**
     * Returns the "rename" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getRename() {
      return GUIHelper.getEmptyIcon();
    }

    /**
     * Returns the "copy" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getCopy() {
      return GUIHelper.getIcon("copy.gif");
    }

    /**
     * Returns the "paste" icon.
     *
     * @return		the icon
     */
    @Override
    public ImageIcon getPaste() {
      return GUIHelper.getIcon("paste.gif");
    }
  }

  /**
   * ADAMS-specific factory.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileChooserBookmarksFactory
    extends DefaultFactory {

    /** for serialization. */
    private static final long serialVersionUID = -8327179027505887784L;

    /**
     * Returns a new instance of the properties handler to be used.
     *
     * @return		the handler instance
     */
    @Override
    public AbstractPropertiesHandler newPropertiesHandler() {
      return new FileChooserBookmarksPropertiesHandler();
    }

    /**
     * Returns a new instance of the icon loader to be used.
     *
     * @return		the loader instance
     */
    @Override
    public AbstractIconLoader newIconLoader() {
      return new FileChooserBookmarksIconLoader();
    }
  }

  /**
   * Creates a new instance of the factory.
   *
   * @return		the factory
   */
  @Override
  protected AbstractFactory newFactory() {
    return new FileChooserBookmarksFactory();
  }
}
