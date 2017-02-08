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
 * NotesPropertyExtractor.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.debug.propertyextractor;

import adams.data.Notes;
import adams.data.NotesHandler;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles {@link Notes} and {@link NotesHandler} classes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NotesPropertyExtractor
  extends AbstractPropertyExtractor {

  /** the notes names. */
  protected List<String> m_Names;

  /**
   * Initializes the extractor.
   */
  @Override
  protected void initialize() {
    Notes		notes;

    super.initialize();

    m_Names = new ArrayList<>();
    notes   = getNotes();
    if (notes.hasError())
      m_Names.add(Notes.ERROR);
    if (notes.hasWarning())
      m_Names.add(Notes.WARNING);
    if (notes.hasProcessInformation())
      m_Names.add(Notes.PROCESS_INFORMATION);
  }

  /**
   * Returns the underlying Notes object.
   *
   * @return		the notes
   */
  protected Notes getNotes() {
    if (m_Current instanceof Notes)
      return (Notes) m_Current;
    else
      return ((NotesHandler) m_Current).getNotes();
  }

  /**
   * Checks whether this extractor actually handles this type of class.
   *
   * @param cls		the class to check
   * @return		true if the extractor handles the object/class
   */
  @Override
  public boolean handles(Class cls) {
    return ClassLocator.isSubclass(Notes.class, cls)
      || ClassLocator.hasInterface(NotesHandler.class, cls);
  }

  /**
   * The number of properties that are available.
   * 
   * @return		the number of variables
   */
  @Override
  public int size() {
    return m_Names.size();
  }

  /**
   * Returns the current value of the specified property.
   * 
   * @param index	the index of the property to retrieve
   * @return		the variable value
   */
  @Override
  public Object getValue(int index) {
    Notes	notes;

    notes = getNotes();
    switch (m_Names.get(index)) {
      case Notes.ERROR:
	return notes.getErrors().toString();
      case Notes.WARNING:
	return notes.getWarnings().toString();
      case Notes.PROCESS_INFORMATION:
	return notes.getProcessInformation().toString();
      default:
	throw new IllegalStateException("Unhandled type: " + m_Names.get(index));
    }
  }

  /**
   * Returns the label for the specified property.
   * 
   * @param index	the index of the property to get the label for
   * @return		the variable name
   */
  @Override
  public String getLabel(int index) {
    return m_Names.get(index);
  }
}
