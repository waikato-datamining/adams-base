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
 * SearchableWrapperListModel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import gnu.trove.list.array.TIntArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * Wraps around any list model and makes them automatically searchable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SearchableWrapperListModel
  extends AbstractListModel
  implements SearchableListModel, CustomSearchListModel {

  /** for serialization. */
  private static final long serialVersionUID = 1379439060928152100L;

  /** the actual list model. */
  protected ListModel m_Model;

  /** the string that was searched for. */
  protected String m_SearchString;

  /** whether the search was a regular expression based one. */
  protected boolean m_RegExpSearch;

  /** the indices of the indices to display that match a search string. */
  protected TIntArrayList m_DisplayIndices;

  /**
   * initializes with no model.
   */
  public SearchableWrapperListModel() {
    this(null);
  }

  /**
   * initializes with the given model.
   *
   * @param model       the model to initialize with
   */
  public SearchableWrapperListModel(ListModel model) {
    super();

    m_DisplayIndices  = null;

    setActualModel(model);
  }

  /**
   * Sets the model to use.
   *
   * @param value       the model to use
   */
  public void setActualModel(ListModel value) {
    m_Model = value;
    initialize();
    fireContentsChanged(this, 0, m_Model.getSize());
  }

  /**
   * returns the underlying model, can be null.
   *
   * @return            the current model
   */
  public ListModel getActualModel() {
    return m_Model;
  }

  /**
   * Initializes indices etc.
   */
  protected void initialize() {
    if (getActualModel() == null) {
      m_DisplayIndices  = null;
    }
    else {
      m_DisplayIndices = null;
      if (m_SearchString != null)
	search(m_SearchString, m_RegExpSearch);
    }
  }

  /**
   * whether the model is initialized.
   *
   * @return            true if the model is not null
   */
  protected boolean isInitialized() {
    return (getActualModel() != null);
  }

  /**
   * Returns the actual underlying index the given visible one represents. Useful
   * for retrieving "non-visual" data that is also stored in a ListModel.
   *
   * @param visibleIndex	the displayed index to retrieve the original index for
   * @return			the original index
   */
  public int getActualIndex(int visibleIndex) {
    int		result;

    result = -1;

    if (isInitialized()) {
      if (m_DisplayIndices != null)
	result = m_DisplayIndices.get(visibleIndex);
      else
	result = visibleIndex;
    }

    return result;
  }

  /**
   * Returns the number of (visible) elements in the model.
   *
   * @return              the number of elements in the model
   */
  public int getSize() {
    int		result;

    result = 0;

    if (isInitialized()) {
      if (m_DisplayIndices == null)
	result = getActualModel().getSize();
      else
	result = m_DisplayIndices.size();
    }

    return result;
  }

  /**
   * Returns the actual element count in the model.
   *
   * @return		the element count in the underlying data
   */
  public int getActualSize() {
    return getActualModel().getSize();
  }

  /**
   * Tests whether the search matches the specified element index.
   * <p/>
   * Default implementation just checks against the strings that getElementAt(...)
   * returns (using the toString() method of the returned objects). Derived
   * classes should override this method in order to implement
   * a proper/faster search functionality.
   *
   * @param params	the search parameters
   * @param index	the index of the element of the underlying model
   * @return		true if the search matches this element
   */
  public boolean isSearchMatch(SearchParameters params, int index) {
    boolean	result;
    Object	value;

    result = false;
    value  = getActualModel().getElementAt(index);
    if (value != null)
      result = params.matches(value.toString());

    return result;
  }

  /**
   * Performs a search for the given string. Limits the display of indices to
   * ones containing the search string.
   *
   * @param searchString	the string to search for
   * @param regexp		whether to perform regular expression matching
   * 				or just plain string comparison
   */
  public synchronized void search(String searchString, boolean regexp) {
    int			i;
    boolean		customSearch;
    SearchParameters	params;

    customSearch   = (getActualModel() instanceof CustomSearchListModel);
    m_RegExpSearch = regexp;
    m_SearchString = searchString;
    params         = new SearchParameters(m_SearchString, m_RegExpSearch);

    // no search -> display everything
    if (m_SearchString == null) {
      m_DisplayIndices = null;
    }
    // perform search
    else {
      m_DisplayIndices = new TIntArrayList();
      for (i = 0; i < getActualSize(); i++) {
	if (customSearch) {
	  if (((CustomSearchListModel) getActualModel()).isSearchMatch(params, i))
	    m_DisplayIndices.add(i);
	}
	else {
	  if (isSearchMatch(params, i))
	    m_DisplayIndices.add(i);
	}
      }
    }

    fireContentsChanged(this, 0, getSize());
  }

  /**
   * Returns the current search string.
   *
   * @return		the search string, null if not filtered
   */
  public String getSeachString() {
    return m_SearchString;
  }

  /**
   * Returns whether the last search was a regular expression based one.
   *
   * @return		true if last search was a reg exp one
   */
  public boolean isRegExpSearch() {
    return m_RegExpSearch;
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index 	the requested index
   * @return 		the value at <code>index</code>
   */
  public Object getElementAt(int index) {
    Object	result;

    result = null;

    if (isInitialized()) {
      if (m_DisplayIndices == null)
	return getActualModel().getElementAt(index);
      else
	return getActualModel().getElementAt(m_DisplayIndices.get(index));
    }

    return result;
  }
}
