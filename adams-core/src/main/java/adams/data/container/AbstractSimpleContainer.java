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
 * AbstractSimpleContainer.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.container;

import adams.core.CloneHandler;
import adams.data.MutableNotesHandler;
import adams.data.Notes;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;

/**
 * Ancestor for simple containers for objects that also offers notes and a report
 * for storing meta-data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6656 $
 * @param <T> the type of content to handle
 */
public abstract class AbstractSimpleContainer<T>
  implements CloneHandler<AbstractSimpleContainer<T>>, MutableNotesHandler, MutableReportHandler<Report> {

  /** the field for the filename. */
  public final static String FIELD_FILENAME = "Filename";
  
  /** the stored content. */
  protected T m_Content;

  /** the report. */
  protected Report m_Report;

  /** the notes. */
  protected Notes m_Notes;

  /**
   * Initializes the container.
   */
  protected AbstractSimpleContainer() {
    super();
    initialize();
  }
  
  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Content  = null;
    m_Notes  = new Notes();
    m_Report = new Report();
    m_Report.addField(new Field(FIELD_FILENAME, DataType.STRING));
  }
  
  /**
   * Returns a clone of the content.
   * 
   * @return		the clone
   */
  protected abstract T cloneContent();

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   * @see		#getHeader()
   */
  public AbstractSimpleContainer<T> getClone() {
    AbstractSimpleContainer<T>	result;
    
    result = getHeader();
    result.setContent(cloneContent());
    
    return result;
  }
  
  /**
   * Returns a new container with the report and notes copied, but without 
   * the content.
   *
   * @return		the container without the content
   * @see		#getClone()
   */
  public AbstractSimpleContainer<T> getHeader() {
    AbstractSimpleContainer<T>	result;
    
    try {
      result = (AbstractSimpleContainer<T>) getClass().newInstance();
      result.setReport(getReport().getClone());
      result.m_Notes = getNotes().getClone();
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    return result;
  }

  /**
   * Sets the content to use.
   *
   * @param value	the content
   */
  public void setContent(T value) {
    if (value == null)
      throw new IllegalArgumentException("Null content provided!");
    m_Content = value;
  }

  /**
   * Returns the store content.
   *
   * @return		the content
   */
  public T getContent() {
    return m_Content;
  }
  
  /**
   * Checks whether a report is present.
   *
   * @return		true if a report is present
   */
  public boolean hasReport() {
    return (m_Report != null);
  }

  /**
   * Sets the report to use.
   *
   * @param value	the report
   */
  public void setReport(Report value) {
    m_Report = value;
  }

  /**
   * Returns the current report.
   *
   * @return		the report
   */
  public Report getReport() {
    return m_Report;
  }

  /**
   * Sets the notes to use.
   *
   * @param value	the new notes
   */
  public void setNotes(Notes value) {
    m_Notes = value;
  }

  /**
   * Returns the currently stored notes.
   *
   * @return		the current notes
   */
  public Notes getNotes() {
    return m_Notes;
  }
  
  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "content=" + m_Content + ", report=" + m_Report + ", notes=" + m_Notes;
  }
}
