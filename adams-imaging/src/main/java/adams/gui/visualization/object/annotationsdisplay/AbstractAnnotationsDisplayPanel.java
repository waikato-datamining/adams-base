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
 * AbstractAnnotationsDisplayPanel.java
 * Copyright (C) 2020-2026 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotationsdisplay;

import adams.core.CleanUpHandler;
import adams.data.objectfilter.ObjectFilter;
import adams.data.objectfilter.PassThrough;
import adams.data.report.Report;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BasePanel;
import adams.gui.visualization.object.ObjectAnnotationPanel;

/**
 * Ancestor for panels that display annotations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAnnotationsDisplayPanel
  extends BasePanel
  implements CleanUpHandler {

  private static final long serialVersionUID = 367484065391308363L;

  /** the owner. */
  protected ObjectAnnotationPanel m_Owner;

  /** the prefix to use. */
  protected String m_Prefix;
  
  /** the object filter to apply to the report/objects. */
  protected ObjectFilter m_ObjectFilter;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Owner        = null;
    m_Prefix       = AbstractAnnotationsDisplayGenerator.PREFIX_DEFAULT;
    m_ObjectFilter = new PassThrough();
  }

  /**
   * Sets the owning panel.
   *
   * @param value	the owner
   */
  public void setOwner(ObjectAnnotationPanel value) {
    m_Owner = value;
  }

  /**
   * Sets the owning panel.
   *
   * @return		the owner
   */
  public ObjectAnnotationPanel getOwner() {
    return m_Owner;
  }

  /**
   * Sets the prefix to use.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
  }

  /**
   * Returns the prefix in use.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Sets the object filter to use.
   *
   * @param value	the filter
   */
  public void setObjectFilter(ObjectFilter value) {
    m_ObjectFilter = value;
  }

  /**
   * Returns the object filter in use.
   *
   * @return		the filter
   */
  public ObjectFilter getObjectFilter() {
    return m_ObjectFilter;
  }

  /**
   * Filters the objects, if necessary.
   *
   * @param objects	the objects to filter
   * @return		the potentially updated objects
   */
  protected LocatedObjects filter(LocatedObjects objects) {
    if (m_ObjectFilter instanceof PassThrough)
      return objects;
    else
      return m_ObjectFilter.filter(objects);
  }

  /**
   * Filters the report, if necessary.
   *
   * @param report	the report to filter
   * @return		the potentially updated report
   */
  protected Report filter(Report report) {
    LocatedObjects	objects;
    LocatedObjects	filtered;

    if (m_ObjectFilter instanceof PassThrough)
      return report;

    objects  = LocatedObjects.fromReport(report, m_Prefix);
    filtered = m_ObjectFilter.filter(objects);
    return filtered.toReport(m_Prefix);
  }

  /**
   * Sets the report to get the annotations from.
   *
   * @param value	the report
   */
  public abstract void setReport(Report value);

  /**
   * Returns the report with the annotations.
   *
   * @return		the report
   */
  public abstract Report getReport();

  /**
   * Sets the annotations.
   *
   * @param value	the objects
   */
  public abstract void setObjects(LocatedObjects value);

  /**
   * Returns the annotations.
   *
   * @return		the objects
   */
  public abstract LocatedObjects getObjects();

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }
}
