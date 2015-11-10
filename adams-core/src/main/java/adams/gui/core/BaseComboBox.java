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
 * BaseComboBox.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import java.util.List;
import java.util.Vector;

/**
 * Improved JComboBox component.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <E> the type of the elements
 */
public class BaseComboBox<E>
  extends JComboBox<E> {

  private static final long serialVersionUID = -4382196370744637495L;

  /**
   * Creates a <code>BaseComboBox</code> with a default data model.
   * The default data model is an empty list of objects.
   * Use <code>addItem</code> to add items.  By default the first item
   * in the data model becomes selected.
   *
   * @see DefaultComboBoxModel
   */
  public BaseComboBox() {
    super();
  }

  /**
   * Creates a <code>BaseComboBox</code> that takes its items from an
   * existing <code>ComboBoxModel</code>.  Since the
   * <code>ComboBoxModel</code> is provided, a combo box created using
   * this constructor does not create a default combo box model and
   * may impact how the insert, remove and add methods behave.
   *
   * @param model the <code>ComboBoxModel</code> that provides the
   *          displayed list of items
   * @see DefaultComboBoxModel
   */
  public BaseComboBox(ComboBoxModel<E> model) {
    super(model);
  }

  /**
   * Creates a <code>BaseComboBox</code> that contains the elements
   * in the specified array.  By default the first item in the array
   * (and therefore the data model) becomes selected.
   *
   * @param items  an array of objects to insert into the combo box
   * @see DefaultComboBoxModel
   */
  public BaseComboBox(E[] items) {
    super(items);
  }

  /**
   * Creates a <code>BaseComboBox</code> that contains the elements
   * in the specified list.  By default the first item in the list
   * (and therefore the data model) becomes selected.
   *
   * @param items  list items to insert into the combo box
   * @see DefaultComboBoxModel
   */
  public BaseComboBox(List<E> items) {
    super(new Vector<>(items));
  }

  /**
   * Returns the current selected item.
   * <p>
   * If the combo box is editable, then this value may not have been added
   * to the combo box with <code>addItem</code>, <code>insertItemAt</code>
   * or the data constructors.
   *
   * @return the current selected Object
   */
  public E getSelectedItem() {
    return (E) super.getSelectedItem();
  }

  /**
   * Returns an array containing the selected items.
   * This method is implemented for compatibility with
   * <code>ItemSelectable</code>.
   *
   * @return an array of <code>Objects</code> containing one
   *          element -- the selected item
   */
  public E[] getSelectedObjects() {
    return (E[]) super.getSelectedObjects();
  }
}
