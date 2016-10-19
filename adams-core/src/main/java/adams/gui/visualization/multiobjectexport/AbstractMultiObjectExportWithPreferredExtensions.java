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
 * AbstractMultiObjectExportWithPreferredExtensions.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.multiobjectexport;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.base.BaseString;
import adams.gui.visualization.debug.objectexport.AbstractObjectExporter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for exporters that have preferred extensions in order to
 * locate an {@link adams.gui.visualization.debug.objectexport.AbstractObjectExporter}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiObjectExportWithPreferredExtensions
  extends AbstractMultiObjectExport {

  private static final long serialVersionUID = 766130390901568068L;

  /** the preferred extensions. */
  protected BaseString[] m_PreferredExtensions;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "preferred-extension", "preferredExtensions",
      getDefaultPreferredExtensions());
  }

  /**
   * Returns the default extensions.
   *
   * @return		the default
   */
  protected BaseString[] getDefaultPreferredExtensions() {
    return new BaseString[]{
      new BaseString("png"),
      new BaseString("csv"),
      new BaseString("txt"),
    };
  }

  /**
   * Sets the preferred extensions to look for when exporting objects.
   *
   * @param value	the extensions (no dot!)
   */
  public void setPreferredExtensions(BaseString[] value) {
    m_PreferredExtensions = value;
    reset();
  }

  /**
   * Returns the preferred extensions to look for when exporting objects.
   *
   * @return		the extensions (no dot!)
   */
  public BaseString[] getPreferredExtensions() {
    return m_PreferredExtensions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String preferredExtensionsTipText() {
    return "The preferred extensions to use when determining object exporters for an object.";
  }

  /**
   * Determines the exporter to use for the object.
   *
   * @param name	the name of the object
   * @param obj		the object to determine the exporter for
   * @param errors 	for storing errors
   * @return		the exporter
   */
  @Override
  protected AbstractObjectExporter determineExporter(String name, Object obj, MessageCollection errors) {
    AbstractObjectExporter		result;
    List<AbstractObjectExporter> 	exporters;
    Set<String> 			preferred;
    String[]				extensions;

    exporters = AbstractObjectExporter.getExporter(obj);
    if (exporters.size() == 0) {
      errors.add("Failed to find object exporter for '" + name + "'/" + Utils.classToString(obj.getClass()));
      return null;
    }
    result    = exporters.get(0);
    preferred = new HashSet<>();
    for (BaseString pref: m_PreferredExtensions)
      preferred.add(pref.getValue());
    for (AbstractObjectExporter exporter: exporters) {
      extensions = exporter.getFormatExtensions();
      for (String extension: extensions) {
	if (preferred.contains(extension)) {
	  result = exporter;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the extension for the given exporter.
   *
   * @param exporter 	the exporter to get the extension for
   * @return		the extension to use
   */
  @Override
  protected String determineExtension(AbstractObjectExporter exporter) {
    String	result;
    String[]	extensions;
    Set<String> preferred;

    preferred  = new HashSet<>();
    for (BaseString pref: m_PreferredExtensions)
      preferred.add(pref.getValue());
    extensions = exporter.getFormatExtensions();
    result     = extensions[0];
    for (String extension: extensions) {
      if (preferred.contains(extension)) {
        result = extension;
        break;
      }
    }

    return result;
  }
}
