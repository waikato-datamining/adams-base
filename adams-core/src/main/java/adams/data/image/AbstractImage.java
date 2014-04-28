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
 * AbstractImage.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image;

import java.awt.image.BufferedImage;

import adams.core.CloneHandler;
import adams.data.MutableNotesHandler;
import adams.data.Notes;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;

/**
 * Ancestor for various image format containers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of image to handle
 */
public abstract class AbstractImage<T>
  implements CloneHandler<AbstractImage<T>>, MutableNotesHandler, MutableReportHandler<Report> {

  /** the field for the filename. */
  public final static String FIELD_FILENAME = "Filename";
  
  /** the stored image. */
  protected T m_Image;

  /** the report. */
  protected Report m_Report;

  /** the notes. */
  protected Notes m_Notes;

  /**
   * Initializes the image.
   */
  protected AbstractImage() {
    super();
    initialize();
  }
  
  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Image  = null;
    m_Notes  = new Notes();
    m_Report = new Report();
    m_Report.addField(new Field(FIELD_FILENAME, DataType.STRING));
  }
  
  /**
   * Returns a clone of the image.
   * 
   * @return		the clone
   */
  protected abstract T cloneImage();
  
  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   * @see		#cloneImage()
   * @see		#getHeader()
   */
  public AbstractImage<T> getClone() {
    AbstractImage<T>	result;
    
    result = getHeader();
    result.setImage(cloneImage());
    
    return result;
  }
  
  /**
   * Returns a new container with the report and notes copied, but without 
   * the image.
   *
   * @return		the container without the image
   * @see		#getClone()
   */
  public AbstractImage<T> getHeader() {
    AbstractImage<T>	result;
    
    try {
      result = (AbstractImage<T>) getClass().newInstance();
      result.setReport(getReport().getClone());
      result.m_Notes = getNotes().getClone();
    }
    catch (Exception e) {
      throw new IllegalStateException(e);
    }
    
    return result;
  }

  /**
   * Sets the image to use.
   *
   * @param value	the image
   */
  public void setImage(T value) {
    if (value == null)
      throw new IllegalArgumentException("Null image provided!");
    m_Image = value;
  }

  /**
   * Returns the store image.
   *
   * @return		the image
   */
  public T getImage() {
    return m_Image;
  }

  /**
   * Returns the width of the image.
   * 
   * @return		the width
   */
  public abstract int getWidth();

  /**
   * Returns the height of the image.
   * 
   * @return		the height
   */
  public abstract int getHeight();
  
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
   * Turns the image into a buffered image.
   * 
   * @return		the buffered image
   */
  public abstract BufferedImage toBufferedImage();
  
  /**
   * Returns a string representation of the container.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    return "image=" + m_Image + ", report=" + m_Report + ", notes=" + m_Notes;
  }
}
