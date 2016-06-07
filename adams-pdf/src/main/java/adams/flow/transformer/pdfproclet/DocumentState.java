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
 * DocumentState.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.pdfproclet;

import com.itextpdf.text.Document;

import java.io.Serializable;

/**
 * Container class for storing state information about the document
 * currently being processed.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13473 $
 */
public class DocumentState
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4076944821318913218L;

  /** whether a new page got added. */
  protected boolean m_NewPageAdded;

  /** the files added so far. */
  protected int m_TotalFiles;

  /** the files added since last page break. */
  protected int m_CurrentFiles;

  /**
   * Initializes the state.
   */
  public DocumentState() {
    super();
  }

  /**
   * Adds a new page only if none has been added so far.
   *
   * @param doc		the document to add the page to
   * @return		true if successfully added (or not necessary)
   */
  public boolean newPage(Document doc) {
    boolean	result;

    result = true;

    if (!isNewPage()) {
      result = doc.newPage();
      if (result)
	newPageAdded();
    }

    return result;
  }

  /**
   * Stores that a new page got added.
   */
  public void newPageAdded() {
    m_NewPageAdded = true;
  }

  /**
   * Stores that content was added.
   */
  public void contentAdded() {
    m_NewPageAdded = false;
  }

  /**
   * Returns whether a new page was just added.
   *
   * @return		true if a new page was just added
   */
  public boolean isNewPage() {
    return m_NewPageAdded;
  }

  /**
   * Increments the file counters.
   */
  public void addFile() {
    m_TotalFiles++;
    m_CurrentFiles++;
  }

  /**
   * Resets the counter for the current files.
   */
  public void resetCurrentFiles() {
    m_CurrentFiles = 0;
  }

  /**
   * Returns the number of files that have been added so far.
   *
   * @return		the number of files
   */
  public int numTotalFiles() {
    return m_TotalFiles;
  }

  /**
   * Returns the number of files that have been added since the last page break.
   *
   * @return		the number of files
   */
  public int numCurrentFiles() {
    return m_CurrentFiles;
  }

  /**
   * Returns a short representation of the document state.
   *
   * @return		the representation
   */
  @Override
  public String toString() {
    return "#total=" + numTotalFiles() + "#current=" + numCurrentFiles() + ", newPage=" + isNewPage();
  }
}
