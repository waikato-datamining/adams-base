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
  *    JComponentWriterFileFilter.java
  *    Copyright (C) 2005 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import adams.gui.core.ExtensionFileFilter;

/**
 * A specialized filter that also contains the associated filter class.
 * 
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JComponentWriterFileFilter
  extends ExtensionFileFilter {
  
  /** for serialization. */
  private static final long serialVersionUID = -2521950307763877692L;
  
  /** the associated writer. */
  private JComponentWriter m_Writer; 
  
  /**
   * Creates the ExtensionFileFilter.
   *
   * @param extension	the extension of accepted files.
   * @param description	a text description of accepted files.
   * @param writer	the associated writer 
   */
  public JComponentWriterFileFilter(String extension, String description, JComponentWriter writer) {
    this(new String[]{extension}, description, writer);
  }
  
  /**
   * Creates the ExtensionFileFilter.
   *
   * @param extensions	the extensions of accepted files.
   * @param description	a text description of accepted files.
   * @param writer	the associated writer 
   */
  public JComponentWriterFileFilter(String[] extensions, String description, JComponentWriter writer) {
    super(description, extensions);
    m_Writer = writer;
  }
  
  /**
   * returns the associated writer.
   * 
   * @return		the writer
   */
  public JComponentWriter getWriter() {
    return m_Writer;
  }
}