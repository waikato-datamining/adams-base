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
 * AbstractHelpGenerator.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.help;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.gui.core.ConsolePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for help generator classes.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractHelpGenerator {

  /** the generators. */
  protected static List<AbstractHelpGenerator> m_Generators;

  /**
   * Returns whether this class is handled by this generator.
   *
   * @param cls		the class to check
   * @return		true if handled
   */
  public abstract boolean handles(Class cls);

  /**
   * Returns whether this object is handled by this generator.
   *
   * @param obj		the object to check
   * @return		true if handled
   */
  public boolean handles(Object obj) {
    return (obj != null) && (handles(obj.getClass())) ;
  }

  /**
   * Returns whether the generated help is HTML or plain text.
   *
   * @param cls		the class to generate the help for
   * @return		true if HTML
   */
  public abstract boolean isHtml(Class cls);

  /**
   * Returns whether the generated help is HTML or plain text.
   *
   * @param obj		the object to generate the help for
   * @return		true if HTML
   */
  public boolean isHtml(Object obj) {
    return (obj != null) && isHtml(obj.getClass());
  }

  /**
   * Generates and returns the help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help, null if failed to produce
   */
  public abstract String generate(Class cls);

  /**
   * Generates and returns the help for the specified object.
   *
   * @param obj		the object to generate the help for
   * @return		the help, null if failed to produce
   */
  public abstract String generate(Object obj);

  /**
   * For initializing the help generators, if necessary.
   */
  protected static void initialize() {
    Class[]			classes;
    AbstractHelpGenerator	generator;

    if (m_Generators != null)
      return;

    classes      = ClassLister.getSingleton().getClasses(AbstractHelpGenerator.class);
    m_Generators = new ArrayList<>();
    for (Class cls: classes) {
      try {
	generator = (AbstractHelpGenerator) cls.getDeclaredConstructor().newInstance();
	if (generator instanceof DefaultHelpGenerator)
	  continue;
	m_Generators.add(generator);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  Level.SEVERE, "Failed to instantiate class: " + cls.getName(), e);
      }
    }
    m_Generators.add(new DefaultHelpGenerator());
  }

  /**
   * Generates help for the specified class.
   *
   * @param clsName	the class to generate the help for
   * @return		the help container or null if failed to instantiate class
   */
  public static synchronized HelpContainer generateHelp(String clsName) {
    Class 			cls;
    try {
      cls = ClassManager.getSingleton().forName(clsName);
      return generateHelp(cls);
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append(
	Level.SEVERE, "Failed to instantiate class: " + clsName, e);
      return null;
    }
  }

  /**
   * Generates help for the specified class.
   *
   * @param cls		the class to generate the help for
   * @return		the help container
   */
  public static synchronized HelpContainer generateHelp(Class cls) {
    String 			help;
    boolean 			html;
    AbstractHelpGenerator	generator;

    initialize();

    generator = new DefaultHelpGenerator();
    for (AbstractHelpGenerator gen: m_Generators) {
      if (gen.handles(cls)) {
	generator = gen;
	break;
      }
    }
    help = generator.generate(cls);
    html = generator.isHtml(cls);
    return new HelpContainer(help, html);
  }

  /**
   * Generates help for the specified object.
   *
   * @param obj		the object to generate the help for
   * @return		the help container
   */
  public static synchronized HelpContainer generateHelp(Object obj) {
    String 			help;
    boolean 			html;
    AbstractHelpGenerator	generator;

    initialize();

    generator = new DefaultHelpGenerator();
    for (AbstractHelpGenerator gen: m_Generators) {
      if (gen.handles(obj)) {
	generator = gen;
	break;
      }
    }
    help = generator.generate(obj);
    html = generator.isHtml(obj);
    return new HelpContainer(help, html);
  }
}
