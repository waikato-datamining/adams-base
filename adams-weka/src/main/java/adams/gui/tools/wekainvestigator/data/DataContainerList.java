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
 * DataContainerList.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * For managing the data containers.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class DataContainerList
  extends ArrayList<DataContainer> {

  private static final long serialVersionUID = 8763903971623553841L;

  /** whether undo is enabled. */
  protected boolean m_UndoEnabled;

  /**
   * Sets whether undo is enabled.
   *
   * @param value	true if enabled
   */
  public synchronized void setUndoEnabled(boolean value) {
    m_UndoEnabled = value;
    for (DataContainer cont: this)
      cont.getUndo().setEnabled(value);
  }

  /**
   * Returns whether undo is enabled.
   *
   * @return		true if enabled
   */
  public boolean isUndoEnabled() {
    return m_UndoEnabled;
  }

  /**
   * Adds the data container and sets the undo enabled flag accordingly.
   *
   * @param cont	the container to add
   * @return		true if collection modified
   */
  @Override
  public boolean add(DataContainer cont) {
    cont.getUndo().setEnabled(isUndoEnabled());
    return super.add(cont);
  }

  /**
   * Adds all the data containers and sets their undo enabled flag accordingly.
   *
   * @param c		the containers to add
   * @return		true if collection modified
   */
  @Override
  public boolean addAll(Collection<? extends DataContainer> c) {
    for (DataContainer cont: c)
      cont.getUndo().setEnabled(isUndoEnabled());
    return super.addAll(c);
  }

  @Override
  public DataContainer set(int index, DataContainer element) {
    get(index).cleanUp();
    return super.set(index, element);
  }

  @Override
  public boolean remove(Object o) {
    ((DataContainer) o).cleanUp();
    return super.remove(o);
  }

  @Override
  public DataContainer remove(int index) {
    get(index).cleanUp();
    return super.remove(index);
  }

  @Override
  protected void removeRange(int fromIndex, int toIndex) {
    for (int i = fromIndex; i < toIndex; i++)
      get(i).cleanUp();
    super.removeRange(fromIndex, toIndex);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return super.removeAll(c);
  }

  @Override
  public boolean removeIf(Predicate<? super DataContainer> filter) {
    return super.removeIf(filter);
  }

  @Override
  public void clear() {
    for (DataContainer c: this)
      c.cleanUp();
    super.clear();
  }
}
