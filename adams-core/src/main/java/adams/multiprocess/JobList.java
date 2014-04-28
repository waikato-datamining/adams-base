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
 * JobList.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.multiprocess;

import adams.core.CleanUpHandler;
import adams.event.JobCompleteEvent;
import adams.event.JobCompleteListener;
import adams.event.JobListCompleteEvent;
import adams.event.JobListCompleteListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * A container for jobs to execute. A listener can be added which listens
 * for all jobs to be finished.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of job to use
 */
public class JobList<T extends Job>
  implements List<T>, JobCompleteListener, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4324015262351789451L;

  /** the jobs. */
  protected Vector<T> m_Jobs;

  /** the listeners listening for all jobs to be completed. */
  protected HashSet<JobListCompleteListener> m_Listeners;

  /** indicates whether a queue has been added to a JobRunner and can no longer
   * be modified. */
  protected boolean m_Locked;

  /** indicates whether all jobs have finished. */
  protected boolean m_Finished;

  /**
   * Initializes the queue.
   */
  public JobList() {
    super();

    m_Locked    = false;
    m_Finished  = false;
    m_Jobs      = new Vector<T>();
    m_Listeners = new HashSet<JobListCompleteListener>();
  }

  /**
   * Ensures that this collection contains the specified element.
   *
   * @param e		the job to add
   * @return		true if this collection changed as a result of the call
   */
  public boolean add(T e) {
    if (m_Locked) {
      return false;
    }
    else {
      // set the list as listener
      e.setJobCompleteListener(this);

      return m_Jobs.add(e);
    }
  }

  /**
   * Adds all of the elements in the specified collection to this collection.
   *
   * @param c		the collection to add
   * @return		true if this collection changed as a result of the call
   */
  public boolean addAll(Collection<? extends T> c) {
    Iterator<Job>	iter;

    if (m_Locked) {
      return false;
    }
    else {
      // set the list as listener
      iter = (Iterator<Job>) c.iterator();
      while (iter.hasNext())
	iter.next().setJobCompleteListener(this);

      return m_Jobs.addAll(c);
    }
  }

  /**
   * Inserts the specified element at the specified position in this list.
   *
   * @param index	index at which the specified element is to be inserted
   * @param element	the element to add
   */
  public void add(int index, T element) {
    if (!m_Locked) {
      // set the list as listener
      element.setJobCompleteListener(this);

      m_Jobs.add(index, element);
    }
  }

  /**
   * Inserts all of the elements in the specified collection into this list at
   * the specified position.
   *
   * @param index	index at which to insert first element from the
   * 			specified collection.
   * @param c		elements to be inserted into this list.
   * @return		true if the collection changed
   */
  public boolean addAll(int index, Collection<? extends T> c) {
    Iterator<T>	iter;

    if (m_Locked) {
      return false;
    }
    else {
      // set the list as listener
      iter = (Iterator<T>) c.iterator();
      while (iter.hasNext())
	iter.next().setJobCompleteListener(this);

      return m_Jobs.addAll(index, c);
    }
  }

  /**
   * Removes all of the elements from this collection.
   */
  public void clear() {
    if (!m_Locked)
      m_Jobs.clear();
  }

  /**
   * Returns true if this collection contains the specified element.
   *
   * @param o		the job to look for
   * @return		true if the job is in the list
   */
  public boolean contains(Object o) {
    return m_Jobs.contains(o);
  }

  /**
   * Returns true if this collection contains all of the elements in the
   * specified collection.
   *
   * @param c		the collection to check against
   * @return		true if all elements are also in this collection
   */
  public boolean containsAll(Collection<?> c) {
    return m_Jobs.containsAll(c);
  }

  /**
   * Compares the specified object with this collection for equality.
   *
   * @param obj		the object to compare with
   * @return		true if the contained jobs, listeners and lock status
   * 			are the same, false otherwise
   */
  public boolean equals(Object obj) {
    JobList	queue;

    if (obj == null)
      return false;

    if (!(obj instanceof JobList))
      return false;

    queue = (JobList) obj;

    if (!m_Jobs.equals(queue.m_Jobs))
      return false;

    if (!m_Listeners.equals(queue.m_Listeners))
      return false;

    if (m_Locked != queue.m_Locked)
      return false;

    return true;
  }

  /**
   * Returns the hash code value for this collection (just the hash code of the
   * underlying job vector).
   *
   * @return		the hash code
   */
  public int hashCode() {
    return m_Jobs.hashCode();
  }

  /**
   * Returns the element at the specified position in this list.
   *
   * @param index	the index of the job
   * @return		the job at the specified position
   */
  public T get(int index) {
    return m_Jobs.get(index);
  }

  /**
   * Returns the index in this list of the first occurrence of the specified
   * element, or -1 if this list does not contain this element.
   *
   * @param o		the job to get the index for
   * @return		the index or -1 if not found
   */
  public int indexOf(Object o) {
    return m_Jobs.indexOf(o);
  }

  /**
   * Returns true if this collection contains no elements.
   *
   * @return		true if no jobs present
   */
  public boolean isEmpty() {
    return m_Jobs.isEmpty();
  }

  /**
   * Returns an iterator over the elements in this collection.
   *
   * @return		the iterator
   */
  public Iterator<T> iterator() {
    return m_Jobs.iterator();
  }

  /**
   * Returns the index in this list of the last occurrence of the specified
   * element, or -1 if this list does not contain this element.
   *
   * @param o		the job to look for
   * @return		the index or -1 if not found
   */
  public int lastIndexOf(Object o) {
    return m_Jobs.lastIndexOf(o);
  }

  /**
   * Returns a list iterator of the elements in this list (in proper sequence).
   *
   * @return		the iterator
   */
  public ListIterator<T> listIterator() {
    return m_Jobs.listIterator();
  }

  /**
   * Returns a list iterator of the elements in this list (in proper sequence),
   * starting at the specified position in this list.
   *
   * @param index	the index to start the iterator
   * @return		the iterator
   */
  public ListIterator<T> listIterator(int index) {
    return m_Jobs.listIterator(index);
  }

  /**
   * Removes a single instance of the specified element from this collection,
   * if it is present.
   *
   * @param o		the object to remove
   * @return		true if the collection changed
   */
  public boolean remove(Object o) {
    if (m_Locked)
      return false;
    else
      return m_Jobs.remove(o);
  }

  /**
   * Removes the element at the specified position in this list.
   *
   * @param index	the index of the job to remove
   * @return		the job or null if locked
   */
  public T remove(int index) {
    if (m_Locked)
      return null;
    else
      return m_Jobs.remove(index);
  }

  /**
   * Removes all this collection's elements that are also contained in the
   * specified collection.
   *
   * @param c		the collection's objects to remove
   * @return		true if the collection changed
   */
  public boolean removeAll(Collection<?> c) {
    if (m_Locked)
      return false;
    else
      return m_Jobs.removeAll(c);
  }

  /**
   * Retains only the elements in this collection that are contained in the
   * specified collection.
   *
   * @param c		the collection's objects to retain
   * @return		true if the collection changed
   */
  public boolean retainAll(Collection<?> c) {
    if (m_Locked)
      return false;
    else
      return m_Jobs.retainAll(c);
  }

  /**
   * Replaces the element at the specified position in this list with the
   * specified element.
   *
   * @param index	the index of the job to replace
   * @param element	the replacement
   * @return		the job or null if locked
   */
  public T set(int index, T element) {
    if (m_Locked) {
      return null;
    }
    else {
      // set listener
      element.setJobCompleteListener(this);

      return m_Jobs.set(index, element);
    }
  }

  /**
   * Returns a view of the portion of this list between the specified
   * fromIndex, inclusive, and toIndex, exclusive.
   *
   * @param fromIndex	the start index
   * @param toIndex	the end index (exclusive)
   * @return		the sublist (of type JobList)
   */
  public List<T> subList(int fromIndex, int toIndex) {
    JobList	result;
    int		i;

    result = new JobList();
    for (i = fromIndex; i < toIndex; i++)
      result.add(m_Jobs.get(i));

    return result;
  }

  /**
   * Returns the number of elements in this collection.
   *
   * @return		the number of jobs
   */
  public int size() {
    return m_Jobs.size();
  }

  /**
   * Returns an array containing all of the elements in this collection.
   *
   * @return		the array of jobs
   */
  public Object[] toArray() {
    return m_Jobs.toArray();
  }

  /**
   * Returns an array containing all of the elements in this collection; the
   * runtime type of the returned array is that of the specified array.
   *
   * @param a		the array to fill
   * @return		the array of jobs
   * @param <T>		the type of array
   */
  public <T> T[] toArray(T[] a) {
    return m_Jobs.toArray(a);
  }

  /**
   * Returns whether the queue has been locked due to being processed in a
   * JobRunner or not.
   *
   * @return		true if queue is being processed and therefore locked
   */
  public boolean isLocked() {
    return m_Locked;
  }

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addJobListCompleteListener(JobListCompleteListener l) {
    m_Listeners.add(l);
  }

  /**
   * Removes the listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeJobListCompleteListener(JobListCompleteListener l) {
    m_Listeners.remove(l);
  }

  /**
   * Notifies all JobListComplete listeners.
   *
   * @param e		the event to send
   */
  protected void notifyJobListCompleteListeners(JobListCompleteEvent e) {
    Iterator<JobListCompleteListener> 	iter;

    iter = m_Listeners.iterator();
    while (iter.hasNext())
      iter.next().queueCompleted(e);
  }

  /**
   * Returns whether all jobs in the list have been finished.
   *
   * @return		true if all jobs have finished
   */
  public synchronized boolean isFinished() {
    return m_Finished;
  }

  /**
   * Post process job. If all jobs got completed, the list of
   *  JobListCompleteListener will be notified.
   *
   * @param e		the event
   */
  public synchronized void jobCompleted(JobCompleteEvent e) {
    boolean	finished;
    Iterator<T>	iter;
    Job		job;

    finished = true;
    iter     = iterator();
    while (iter.hasNext()) {
      job = iter.next();
      if (!job.isComplete()) {
	finished = false;
	break;
      }
    }

    m_Finished = finished;
    if (m_Finished)
      notifyJobListCompleteListeners(new JobListCompleteEvent(this));
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Listeners != null)
      m_Listeners.clear();
    clear();
  }
}
