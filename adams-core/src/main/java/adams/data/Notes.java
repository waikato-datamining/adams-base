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
 * Notes.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import adams.core.CloneHandler;
import adams.core.Mergeable;
import adams.core.option.OptionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A helper class for the Chromatogram class for storing meta-data.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Notes
  implements Serializable, CloneHandler<Notes>, Mergeable<Notes> {

  /** for serialization. */
  private static final long serialVersionUID = -6179090129357843542L;

  /** the warning prefix. */
  public final static String WARNING = "WARNING: ";

  /** the error prefix. */
  public final static String ERROR = "ERROR: ";

  /** the process prefix. */
  public final static String PROCESS_INFORMATION = "PROCESS INFORMATION";

  /** the notes for the chromatogram (classname &lt;-&gt; list of notes). */
  protected HashMap<String,List<String>> m_Notes;

  /**
   * Initializes the notes.
   */
  public Notes() {
    super();

    m_Notes = new HashMap<String,List<String>>();
  }

  /**
   * Removes all notes.
   */
  public void clear() {
    m_Notes.clear();
  }

  /**
   * Adds the given note under the specified classname. Skips duplicates.
   *
   * @param cls		the class the note is for
   * @param note	the note to add
   */
  public void addNote(Class cls, String note) {
    addNote(cls.getName(), note);
  }

  /**
   * Adds the given note under the specified classname. Skips duplicates.
   *
   * @param classname	the class the note is for
   * @param note	the note to add
   */
  public void addNote(String classname, String note) {
    List<String>	notes;

    notes = m_Notes.get(classname);
    if (notes == null) {
      notes = new ArrayList<String>();
      m_Notes.put(classname, notes);
    }

    // no duplicates!
    if (!notes.contains(note))
      notes.add(new String(note));
  }

  /**
   * Adds the given warning under the specified classname. Skips duplicates.
   *
   * @param cls		the class the note is for
   * @param note	the warning to add
   */
  public void addWarning(Class cls, String note) {
    addNote(cls.getName(), WARNING + note);
  }

  /**
   * Adds the given warning under the specified classname. Skips duplicates.
   *
   * @param classname	the class the note is for
   * @param note	the warning to add
   */
  public void addWarning(String classname, String note) {
    addNote(classname, WARNING + note);
  }

  /**
   * Adds the given error under the specified classname. Skips duplicates.
   *
   * @param cls		the class the note is for
   * @param note	the error to add
   */
  public void addError(Class cls, String note) {
    addNote(cls.getName(), ERROR + note);
  }

  /**
   * Adds the given error under the specified classname. Skips duplicates.
   *
   * @param classname	the class the note is for
   * @param note	the error to add
   */
  public void addError(String classname, String note) {
    addNote(classname, ERROR + note);
  }

  /**
   * Adds the commandline from the given object as process information.
   *
   * @param obj		the object to add its commandline to the process information
   */
  public void addProcessInformation(Object obj) {
    addNote(PROCESS_INFORMATION, OptionUtils.getCommandLine(obj));
  }

  /**
   * Checks whether there are any notes for the given class available.
   *
   * @param cls		the class to look for notes for
   * @return		true if notes are available
   */
  public boolean hasNotes(Class cls) {
    return hasNotes(cls.getName());
  }

  /**
   * Checks whether there are any notes for the given class available.
   *
   * @param classname	the class to look for notes for
   * @return		true if notes are available
   */
  public boolean hasNotes(String classname) {
    return (m_Notes.get(classname) != null);
  }

  /**
   * Checks whether at least one warning is among the notes.
   *
   * @return		true if notes contain at least one warning
   */
  public boolean hasWarning() {
    return (getWarnings().size() > 0);
  }

  /**
   * Checks whether at least one error is among the notes.
   *
   * @return		true if notes contain at least one error
   */
  public boolean hasError() {
    return (getErrors().size() > 0);
  }

  /**
   * Checks whether at least one process information is among the notes.
   *
   * @return		true if notes contain at least one process information
   */
  public boolean hasProcessInformation() {
    return hasNotes(PROCESS_INFORMATION);
  }

  /**
   * Checks whether at least one other (not warning/error/proc.info) is among
   * the notes.
   *
   * @return		true if notes contain at least one other note
   */
  public boolean hasOthers() {
    return (getOthers().size() > 0);
  }

  /**
   * Returns the notes for the given class.
   *
   * @param cls		the class to look for notes for
   * @return		the notes or null if not available
   */
  public List<String> getNotes(Class cls) {
    return getNotes(cls.getName());
  }

  /**
   * Returns the notes for the given class.
   *
   * @param classname	the class to look for notes for
   * @return		the notes or null if not available
   */
  public List<String> getNotes(String classname) {
    return m_Notes.get(classname);
  }

  /**
   * Returns the subset for the given prefix.
   *
   * @param regex	the regular expression that notes must match
   * @return		the subset
   */
  public Notes getSubset(String regex) {
    Notes	 	result;
    Iterator<String>	iter;
    String		key;
    List<String>	list;
    int			i;

    result = new Notes();

    iter = notes();
    while (iter.hasNext()) {
      key  = iter.next();
      list = getNotes(key);
      for (i = 0; i < list.size(); i++) {
	if (list.get(i).matches(regex))
	  result.addNote(key, list.get(i));
      }
    }

    return result;
  }

  /**
   * Returns the warning subset.
   *
   * @return		the warnings (if any)
   */
  public Notes getWarnings() {
    return getSubset("^" + WARNING + ".*");
  }

  /**
   * Returns the error subset.
   *
   * @return		the errors (if any)
   */
  public Notes getErrors() {
    return getSubset("^" + ERROR + ".*");
  }

  /**
   * Returns the subset for the given prefix, e.g., PROCESS_INFORMATION.
   *
   * @param prefix	the prefix to return the subset for
   * @return		the subset (if any)
   */
  public Notes getPrefixSubset(String prefix) {
    Notes		result;
    List<String>	items;
    List<String>	itemsNew;

    result = new Notes();
    items  = getNotes(prefix);
    if (items != null) {
      itemsNew = new ArrayList<String>(items);
      result.m_Notes.put(prefix, itemsNew);
    }

    return result;
  }

  /**
   * Returns the process information subset.
   *
   * @return		the process informations (if any)
   */
  public Notes getProcessInformation() {
    return getPrefixSubset(PROCESS_INFORMATION);
  }

  /**
   * Returns the other notes, not warning/error/process information.
   *
   * @return		the other notes (if any)
   */
  public Notes getOthers() {
    Notes	result;
    Notes	excluded;

    excluded = new Notes();
    excluded.mergeWith(getWarnings());
    excluded.mergeWith(getErrors());
    excluded.mergeWith(getProcessInformation());

    result = this.minus(excluded);

    return result;
  }

  /**
   * Removes all notes for the given class.
   *
   * @param cls		the class to remove the notes for
   */
  public void removeNotes(Class cls) {
    removeNotes(cls.getName());
  }

  /**
   * Removes all notes for the given class.
   *
   * @param classname	the class to remove the notes for
   */
  public void removeNotes(String classname) {
    m_Notes.remove(classname);
  }

  /**
   * Returns an enumeration over all classes that have notes stored for at the
   * moment.
   *
   * @return		the enumeration
   */
  public Iterator<String> notes() {
    return m_Notes.keySet().iterator();
  }

  /**
   * Returns a deep copy of this object.
   *
   * @return		the cloned object
   */
  public Notes getClone() {
    Notes			result;
    Iterator<String>		iter;
    String			classname;
    List<String>		list;
    int				i;

    result = new Notes();

    iter = m_Notes.keySet().iterator();
    while (iter.hasNext()) {
      classname = iter.next();
      list      = m_Notes.get(classname);
      for (i = 0; i < list.size(); i++)
	result.addNote(classname, list.get(i));
    }

    return result;
  }

  /**
   * Merges the currently stored notes with the specified ones.
   *
   * @param other	the notes to merge with
   */
  public void mergeWith(Notes other) {
    m_Notes = this.union(other).m_Notes;
  }

  /**
   * Merges the currently stored notes with the specified ones and returns the
   * new notes (the current ones aren't changed).
   *
   * @param other	the notes to merge with
   * @return		the union
   */
  public Notes union(Notes other) {
    Notes		result;
    Iterator<String>	iter;
    String		classname;
    int			i;
    List<String>	list;

    result  = getClone();
    iter     = other.notes();
    while (iter.hasNext()) {
      classname = iter.next();
      list      = other.getNotes(classname);
      if (result.hasNotes(classname)) {
	for (i = 0; i < list.size(); i++) {
	  if (!result.m_Notes.get(classname).contains(list.get(i)))
	    result.addNote(classname, list.get(i));
	}
      }
      else {
	for (i = 0; i < list.size(); i++) {
	  result.addNote(classname, list.get(i));
	}
      }
    }

    return result;
  }

  /**
   * Returns all the notes after removing the provided ones.
   *
   * @param exclude	the notes to exclude
   * @return		the remaining notes
   */
  public Notes minus(Notes exclude) {
    Notes		result;
    Iterator<String>	keys;
    String		key;
    List<String>	items;
    List<String>	itemsNew;
    int			i;

    result = new Notes();

    keys = notes();
    while (keys.hasNext()) {
      key = keys.next();
      // add the ones that are not excluded
      if (!exclude.hasNotes(key)) {
	items = getNotes(key);
	for (i = 0; i < items.size(); i++)
	  result.addNote(key, items.get(i));
      }
      // add only the new ones
      else {
	itemsNew = new ArrayList<String>(getNotes(key));
	items    = exclude.getNotes(key);
	i        = 0;
	while (i < itemsNew.size()) {
	  if (items.contains(itemsNew.get(i)))
	    itemsNew.remove(i);
	  else
	    i++;
	}

	// add remaining notes
	if (itemsNew.size() > 0) {
	  for (i = 0; i < itemsNew.size(); i++)
	    result.addNote(key, itemsNew.get(i));
	}
      }
    }

    return result;
  }

  /**
   * Returns the number of classes that have notes stored.
   *
   * @return		the number of classes
   */
  public int size() {
    return m_Notes.size();
  }

  /**
   * Returns whether the notes are equal.
   *
   * @param o		the object to compare with
   * @return		true if the objects contain the same notes (in the same order)
   */
  @Override
  public boolean equals(Object o) {
    Notes	notes;

    if ((o == null) || !(o instanceof Notes))
      return false;

    notes = (Notes) o;

    return m_Notes.equals(notes.m_Notes);
  }

  /**
   * Returns the hashtable's hash code.
   *
   * @return		the hash code
   */
  @Override
  public int hashCode() {
    return m_Notes.hashCode();
  }

  /**
   * Returns a string representation of the notes.
   *
   * @return		the notes as string
   */
  @Override
  public String toString() {
    StringBuilder	result;
    Iterator<String>	iter;
    String		key;
    List<String>	list;
    int			i;

    result = new StringBuilder();

    iter = notes();
    while (iter.hasNext()) {
      key  = iter.next();
      list = getNotes(key);

      result.append(key + ":\n");
      for (i = 0; i < list.size(); i++)
	result.append("  " + (i+1) + ". " + list.get(i) + "\n");
    }

    return result.toString();
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Notes n1 = new Notes();
    n1.addNote(String.class, "blah");
    n1.addNote(String.class, "bloerk");
    n1.addNote(Object.class, "oblah");
    n1.addNote(Object.class, "obble");

    Notes n2 = n1.getClone();
    Notes n3 = n1.getClone();
    n3.mergeWith(n2);
    Notes n4 = n1.getClone();
    n4.removeNotes(Object.class);
    Notes n5 = n1.getClone();
    n5.addError(String.class, "nooooo!");

    System.out.println("n1 =?= n2: " + n1.equals(n2));
    System.out.println("n1 =?= n3: " + n1.equals(n3));
    System.out.println("n1 =?= n4: " + n1.equals(n4));

    System.out.println();

    System.out.println("error in n1: " + n1.hasError());
    System.out.println("error in n2: " + n2.hasError());
    System.out.println("error in n3: " + n3.hasError());
    System.out.println("error in n4: " + n4.hasError());
    System.out.println("error in n5: " + n5.hasError());

    System.out.println();

    System.out.println("warning in n1: " + n1.hasWarning());
    System.out.println("warning in n2: " + n1.hasWarning());
    System.out.println("warning in n3: " + n1.hasWarning());
    System.out.println("warning in n4: " + n1.hasWarning());
    System.out.println("warning in n5: " + n1.hasWarning());

    System.out.println();

    Notes merged = n1.getClone();
    merged.mergeWith(n5);
    System.out.println("merged =?= n5: " + merged.equals(n5));
    Notes union = n1.union(n5);
    System.out.println("union =?= n5: " + union.equals(n5));
    Notes minus = merged.minus(n1);
    System.out.println("merged - n1:\n" + minus);
  }
}
