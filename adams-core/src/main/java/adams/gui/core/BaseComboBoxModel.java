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
 * BaseComboBoxModel.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import javax.swing.DefaultComboBoxModel;
import java.util.List;
import java.util.Vector;

/**
 * Enhanced {@link DefaultComboBoxModel}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseComboBoxModel<E>
  extends DefaultComboBoxModel<E> {

  private static final long serialVersionUID = -2804568752240468248L;

  /**
   * Constructs an empty DefaultComboBoxModel object.
   */
  public BaseComboBoxModel() {
    super();
  }

  /**
   * Constructs a DefaultComboBoxModel object initialized with
   * an array of objects.
   *
   * @param items  an array of Object objects
   */
  public BaseComboBoxModel(final E items[]) {
    super(items);
  }

  /**
   * Constructs a DefaultComboBoxModel object initialized with
   * a vector.
   *
   * @param v  a Vector object ...
   */
  public BaseComboBoxModel(List<E> v) {
    super(new Vector<>(v));
  }

  // implements javax.swing.ComboBoxModel
  public E getSelectedItem() {
    return (E) super.getSelectedItem();
  }
}
