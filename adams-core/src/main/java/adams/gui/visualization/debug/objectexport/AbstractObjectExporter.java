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
 * AbstractObjectExporter.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.objectexport;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.io.FileFormatHandler;
import adams.core.option.AbstractOptionHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Ancestor for classes that can export certain objects to files.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectExporter
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = -7742758428210374232L;

  /** the cache for object class / exporter relation. */
  protected static Hashtable<Class,List<Class>> m_Cache;

  /** the exporters (classnames) currently available. */
  protected static String[] m_Exporters;

  /** the exporters (classes) currently available. */
  protected static Class[] m_ExporterClasses;

  static {
    m_Cache          = new Hashtable<>();
    m_Exporters       = null;
    m_ExporterClasses = null;
  }

  /**
   * Initializes the exporters.
   */
  protected static synchronized void initExporters() {
    int		i;

    if (m_Exporters != null)
      return;

    m_Exporters       = ClassLister.getSingleton().getClassnames(AbstractObjectExporter.class);
    m_ExporterClasses = new Class[m_Exporters.length];
    for (i = 0; i < m_Exporters.length; i++) {
      try {
	m_ExporterClasses[i] = Class.forName(m_Exporters[i]);
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object exporter '" + m_Exporters[i] + "': ");
	e.printStackTrace();
      }
    }
  }

  /**
   * Returns a exporter for the specified object.
   *
   * @param obj		the object to get a commandline exporter for
   * @return		the exporter
   */
  public static synchronized List<AbstractObjectExporter> getExporter(Object obj) {
    if (obj != null)
      return getExporter(obj.getClass());
    else
      return getExporter(Object.class);
  }

  /**
   * Instantiates the exporters.
   * 
   * @param exporters	the exporters to instantiate
   * @return		the instances
   */
  protected static List<AbstractObjectExporter> instantiate(List<Class> exporters) {
    List<AbstractObjectExporter>	result;
    int					i;
    
    result = new ArrayList<>();
    for (i = 0; i < exporters.size(); i++) {
      try {
	result.add((AbstractObjectExporter) exporters.get(i).newInstance());
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object exporter '" + exporters.get(i).getName() + "':");
	e.printStackTrace();
      }
    }
    
    return result;
  }
  
  /**
   * Returns a exporter for the specified class.
   *
   * @param cls		the class to get a commandline exporter for
   * @return		the exporter
   */
  public static synchronized List<AbstractObjectExporter> getExporter(Class cls) {
    AbstractObjectExporter exporter;
    List<Class>				exporters;
    int					i;

    initExporters();

    // already cached?
    if (m_Cache.containsKey(cls))
      return instantiate(m_Cache.get(cls));

    // find suitable exporter
    exporters = new ArrayList<>();
    for (i = 0; i < m_ExporterClasses.length; i++) {
      if (m_ExporterClasses[i] == PlainTextExporter.class)
	continue;
      if (m_ExporterClasses[i] == RenderedPlainTextExporter.class)
	continue;
      try {
	exporter = (AbstractObjectExporter) m_ExporterClasses[i].newInstance();
	if (exporter.handles(cls)) {
	  exporters.add(m_ExporterClasses[i]);
	  break;
	}
      }
      catch (Exception e) {
	System.err.println("Failed to instantiate object exporter '" + m_ExporterClasses[i].getName() + "':");
	e.printStackTrace();
      }
    }

    if (exporters.size() == 0) {
      exporters.add(PlainTextExporter.class);
      exporters.add(RenderedPlainTextExporter.class);
    }

    // store in cache
    m_Cache.put(cls, exporters);

    return instantiate(exporters);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Export format: " + getFormatDescription() + "\n"
      + "Extension(s): " + Utils.flatten(getFormatExtensions(), ", ") + "\n"
      + "Default extension: " + getDefaultFormatExtension();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Checks whether the exporter can handle the specified class.
   *
   * @param cls		the class to check
   * @return		true if the exporter can handle this type of object
   */
  public abstract boolean handles(Class cls);

  /**
   * Performs the actual export.
   *
   * @param obj		the object to export
   * @param file	the file to export to
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExport(Object obj, File file);

  /**
   * Exports the object.
   *
   * @param obj		the object to export
   * @param file	the file to export to
   * @return		null if successful, otherwise error message
   */
  public String export(Object obj, File file) {
    if (obj == null)
      return "No object provided!";
    else
      return doExport(obj, file);
  }

  /**
   * Returns a list with classnames of exporters.
   *
   * @return		the exporter classnames
   */
  public static String[] getExporters() {
    return ClassLister.getSingleton().getClassnames(AbstractObjectExporter.class);
  }
}
