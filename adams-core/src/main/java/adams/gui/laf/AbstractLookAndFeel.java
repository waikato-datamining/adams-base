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
 * AbstractLookAndFeel.java
 * Copyright (C) 2022-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.laf;

import adams.core.ClassLister;
import adams.core.Properties;
import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingObject;
import adams.env.Environment;

import java.util.logging.Level;

/**
 * Ancestor for look and feels.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLookAndFeel
  extends LoggingObject {

  private static final long serialVersionUID = 741817667889232890L;

  public final static String FILENAME = "LookAndFeel.props";

  public static final String KEY_LOOKANDFEEL = "LookAndFeel";

  /** the current look and feel. */
  protected static AbstractLookAndFeel m_Current;

  /**
   * Returns the name for this look and feel.
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * Returns whether the Look'n'Feel has a flat or 3D visual appearance.
   *
   * @return		true if flat
   */
  public boolean isFlat() {
    return false;
  }

  /**
   * Checks whether the look and feel is available.
   *
   * @return		true if available
   */
  public abstract boolean isAvailable();

  /**
   * Installs the look and feel.
   *
   * @throws Exception 	if installation fails
   */
  public abstract void doInstall() throws Exception;

  /**
   * Installs the look and feel.
   *
   * @return		true if successful
   */
  public boolean install() {
    try {
      doInstall();
      m_Current = this;
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to install look and feel!", e);
      return false;
    }
  }

  /**
   * Returns the properties filename.
   *
   * @return		the filename to use
   */
  protected static String getPropertiesFilename() {
    return Environment.getInstance().createPropertiesFilename(FILENAME);
  }

  /**
   * Installs the specified look and feel. If successful, uses this one as default from now on.
   *
   * @param laf		the look and feel to use from now on
   */
  public static void installLookAndFeel(AbstractLookAndFeel laf) {
    Properties		props;

    if (!laf.install())
      return;

    props = new Properties();
    props.setProperty(KEY_LOOKANDFEEL, laf.getClass().getName());
    props.save(getPropertiesFilename());
  }

  /**
   * Installs the preferred look and feel or, if not defined, the default one.
   */
  public static void installLookAndFeel() {
    String		filename;
    Properties		props;
    String		lafClassName;
    AbstractLookAndFeel	laf;

    laf      = null;
    filename = getPropertiesFilename();
    if (FileUtils.fileExists(filename)) {
      props = new Properties();
      props.load(filename);
      if (props.hasKey(KEY_LOOKANDFEEL)) {
	lafClassName = props.getProperty(KEY_LOOKANDFEEL);
	try {
	  laf = (AbstractLookAndFeel) ClassManager.getSingleton().forName(lafClassName).getDeclaredConstructor().newInstance();
	}
	catch (Exception e){
	  System.err.println("Failed to instantiate look and feel: " + lafClassName);
	}
      }
    }

    // fall back on default?
    if (laf == null)
      laf = new FlatLafLight();

    laf.install();
  }

  /**
   * Returns the available look and feel classes.
   *
   * @return		the look and feels
   */
  public static Class[] getLookAndFeels() {
    return ClassLister.getSingleton().getClasses(AbstractLookAndFeel.class);
  }

  /**
   * Whether a look and feel has been installed.
   *
   * @return		true if look and feel available
   */
  public static boolean hasCurrent() {
    return (m_Current != null);
  }

  /**
   * Returns the currently installed look and feel.
   *
   * @return		the look and feel
   */
  public static AbstractLookAndFeel getCurrent() {
    return m_Current;
  }
}
