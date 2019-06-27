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
 * BinnableGroup.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning;

import adams.core.Mergeable;
import adams.data.id.IDHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Container for multiple Binnable items that fall in the same group ID.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BinnableGroup<T>
  implements Serializable, IDHandler, Comparable<BinnableGroup<T>>, Mergeable<BinnableGroup<T>> {

  private static final long serialVersionUID = -4045725760262383385L;

  /** the ID of the group. */
  protected String m_ID;

  /** the grouped items. */
  protected List<Binnable<T>> m_Members;

  /**
   * Initializes an empty group with the ID.
   *
   * @param id		the ID to use
   */
  public BinnableGroup(String id) {
    m_ID      = id;
    m_Members = new ArrayList<>();
  }

  /**
   * Initializes the group with the ID and members.
   *
   * @param id		the ID to use
   * @param members 	the members to add
   */
  public BinnableGroup(String id, List<Binnable<T>> members) {
    this(id);
    addAll(members);
  }

  /**
   * Adds the member.
   *
   * @param member	the member to add
   */
  public void add(Binnable<T> member) {
    m_Members.add(member);
  }

  /**
   * Adds all the members.
   *
   * @param members	the members to add
   */
  public void addAll(Collection<Binnable<T>> members) {
    m_Members.addAll(members);
  }

  /**
   * Returns the number of members.
   *
   * @return		the member count
   */
  public int size() {
    return m_Members.size();
  }

  /**
   * Returns the list of members.
   *
   * @return		the members
   */
  public List<Binnable<T>> get() {
    return m_Members;
  }

  /**
   * Returns the ID.
   *
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }

  /**
   * Just adds all the members of the other group.
   * Ignores the ID.
   *
   * @param other		the object to merge with
   */
  @Override
  public void mergeWith(BinnableGroup<T> other) {
    addAll(other.get());
  }

  /**
   * Returns the comparison of the two group IDs
   *
   * @param o		the other group to compare with
   * @return		the result of the string comparison of the IDs
   */
  @Override
  public int compareTo(BinnableGroup<T> o) {
    return getID().compareTo(o.getID());
  }

  /**
   * Checks wether the object is a BinnableGroup with the same ID.
   *
   * @param obj		the object to check
   * @return		true if BinnableGroup and same ID
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof BinnableGroup) && (compareTo((BinnableGroup) obj) == 0);
  }

  /**
   * Returns a short description of the group.
   *
   * @return		the description
   */
  public String toString() {
    return toString(-1);
  }

  /**
   * Returns a short description of the group.
   *
   * @param decimals 	the number of decimals to use for printing, -1 for no limit
   * @return		the description
   */
  public String toString(int decimals) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append("Group: ");
    result.append(getID());
    result.append("\n");
    result.append("Members: ").append(size()).append("\n");
    for (Binnable<T> member: m_Members)
      result.append("- ").append(member.toString(decimals)).append("\n");

    return result.toString();
  }
}
