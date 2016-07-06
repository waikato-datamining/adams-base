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
 * SearchableBaseList.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.core;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Extended BaseList class that allows searching in its elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SearchableBaseList
  extends BaseList
  implements SearchableList {

  /** for serialization. */
  private static final long serialVersionUID = -5870481646786200108L;

  /** the key for the search string. */
  public static final String KEY_SEARCHSTRING = "search string";

  /** the key for the regular expression search flag. */
  public static final String KEY_SEARCHREGEXP = "search reg exp";

  /** the searchable model. */
  protected SearchableWrapperListModel m_Model;

  /**
   * Constructs a <code>SearchableBaseList</code> with an empty, read-only, model.
   */
  public SearchableBaseList() {
    super();
    setModel(new DefaultListModel());
  }

  /**
   * Constructs a <code>SearchableBaseList</code> that displays the elements in
   * the specified array. This constructor creates a read-only model
   * for the given array, and then delegates to the constructor that
   * takes a {@code ListModel}.
   * <p>
   * Attempts to pass a {@code null} value to this method results in
   * undefined behavior and, most likely, exceptions. The created model
   * references the given array directly. Attempts to modify the array
   * after constructing the list results in undefined behavior.
   *
   * @param  listData  the array of Objects to be loaded into the data model,
   *                   {@code non-null}
   */
  public SearchableBaseList(final Object[] listData) {
    super();

    DefaultListModel model = new DefaultListModel();
    for (Object obj: listData)
      model.addElement(obj);
    setModel(model);
  }

  /**
   * Constructs a <code>SearchableBaseList</code> that displays the elements in
   * the specified <code>Vector</code>. This constructor creates a read-only
   * model for the given {@code Vector}, and then delegates to the constructor
   * that takes a {@code ListModel}.
   * <p>
   * Attempts to pass a {@code null} value to this method results in
   * undefined behavior and, most likely, exceptions. The created model
   * references the given {@code Vector} directly. Attempts to modify the
   * {@code Vector} after constructing the list results in undefined behavior.
   *
   * @param  listData  the <code>Vector</code> to be loaded into the
   *		         data model, {@code non-null}
   */
  public SearchableBaseList(final Vector<?> listData) {
    super();

    DefaultListModel model = new DefaultListModel();
    for (Object obj: listData)
      model.addElement(obj);
    setModel(model);
  }

  /**
   * Constructs a {@code SearchableBaseList} that displays elements from the specified,
   * {@code non-null}, model. All {@code SearchableBaseList} constructors delegate to
   * this one.
   * <p>
   * This constructor registers the list with the {@code ToolTipManager},
   * allowing for tooltips to be provided by the cell renderers.
   *
   * @param dataModel the model for the list
   * @throws IllegalArgumentException if the model is {@code null}
   */
  public SearchableBaseList(ListModel dataModel) {
    super();
    setModel(dataModel);
  }

  /**
   * Returns the class of the list model that the models need to be derived
   * from. The default implementation just returns ListModel.class
   *
   * @return		the class the models must be derived from
   */
  protected Class getListModelClass() {
    return ListModel.class;
  }

  /**
   * Backs up the settings from the old model.
   *
   * @param model	the old model (the model stored within the SortedModel)
   * @return		the backed up settings
   */
  protected Hashtable<String,Object> backupModelSettings(ListModel model) {
    Hashtable<String,Object>	result;

    result = new Hashtable<>();

    if (model instanceof SearchableListModel) {
      if (((SearchableListModel) model).getSeachString() != null)
	result.put(KEY_SEARCHSTRING, ((SearchableListModel) model).getSeachString());
      result.put(KEY_SEARCHREGEXP, ((SearchableListModel) model).isRegExpSearch());
    }

    return result;
  }

  /**
   * Restores the settings previously backed up.
   *
   * @param model	the new model (the model stored within the SortedModel)
   * @param settings	the old settings, null if no settings were available
   */
  protected void restoreModelSettings(ListModel model, Hashtable<String,Object> settings) {
    String	search;
    boolean	regexp;

    // default values
    search = null;
    regexp = false;

    // get stored values
    if (settings != null) {
      if (model instanceof SearchableListModel) {
	search = (String) settings.get(KEY_SEARCHSTRING);
	regexp = (Boolean) settings.get(KEY_SEARCHREGEXP);
      }
    }

    // restore search
    if (model instanceof SearchableListModel)
      ((SearchableListModel) model).search(search, regexp);
  }

  /**
   * Sets the model to display - only {@link #getListModelClass()}.
   *
   * @param model	the model to display
   */
  public synchronized void setModel(ListModel model) {
    Hashtable<String,Object>	settings;

    if (!(getListModelClass().isInstance(model)))
      model = new DefaultListModel();

    // backup current setup
    if (m_Model != null)
      settings = backupModelSettings(m_Model);
    else
      settings = null;

    m_Model = new SearchableWrapperListModel(model);
    super.setModel(m_Model);

    // restore setup
    restoreModelSettings(m_Model, settings);
  }

  /**
   * Sets the model to use.
   *
   * @param value       the model to use
   */
  public void setActualModel(ListModel value) {
    m_Model.setActualModel(value);
  }

  /**
   * returns the underlying model, can be null.
   *
   * @return            the current model
   */
  public ListModel getActualModel() {
    return m_Model.getActualModel();
  }

  /**
   * Returns the actual index in the model.
   *
   * @param index	the index of the currently displayed data
   * @return		the index in the underlying data
   */
  public int getActualIndex(int index) {
    return m_Model.getActualIndex(index);
  }

  /**
   * Returns the actual size of the model.
   *
   * @return		the size in the underlying data
   */
  public int getActualSize() {
    return m_Model.getActualSize();
  }

  /**
   * Performs a search for the given string. Limits the display of rows to
   * ones containing the search string.
   *
   * @param searchString	the string to search for
   * @param regexp		whether to perform regular expression matching
   * 				or just plain string comparison
   */
  public void search(String searchString, boolean regexp) {
    m_Model.search(searchString, regexp);
  }

  /**
   * Returns the current search string.
   *
   * @return		the search string, null if not filtered
   */
  public String getSeachString() {
    return m_Model.getSeachString();
  }

  /**
   * Returns whether the last search was a regular expression based one.
   *
   * @return		true if last search was a reg exp one
   */
  public boolean isRegExpSearch() {
    return m_Model.isRegExpSearch();
  }
}
