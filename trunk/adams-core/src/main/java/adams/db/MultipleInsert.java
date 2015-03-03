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
 * MultipleInsert.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Manages a multiple insert operation for a table (bulkinserttable)
 *
 * @author dale
 * @version $Revision$
 */
public class MultipleInsert
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 6066126049923343182L;

  /** builds insert string. */
  protected StringBuilder m_sb=new StringBuilder();

  /** inserts. */
  protected int count=0;

  /** force insert to db at count. */
  protected int m_insert_at=250;

  /** Table this is associated with. */
  protected BulkInsertTable m_insert;

  /** columns for insert. */
  protected Vector<String> m_cols=null; // vector of column names

  /**
   * Constructor. Set max inserts before db insert
   * @param max		when to do actual insert
   */
  public MultipleInsert(int max) {
    m_insert_at=max;
  }

  /**
   * Set the table associated with this insert manager
   * @param bti	table
   */
  public void setTable(BulkInsertTable bti) {
    m_insert=bti;
  }

  /**
   * Set the columns used for insert
   * @param sv	column vector
   */
  public void setColumnVector(Vector<String> sv) {
    m_cols=sv;
  }

  /**
   * Insert. Get values from hashtable
   * @param vals	Hashtable of ColumnName(string)->InsertValue(string)
   * @return	Insert string, if time to insert. Else null;
   */
  public String insert(Hashtable<String,String> vals) {  // return true if time to insert
    String ret=null;
    String q="(";
    for (int i=0;i<m_cols.size();i++) {
      String val=vals.get(m_cols.elementAt(i));
      if (val == null) {
	System.err.println("null in insert");
	return(null);
      }
      q+=val;
      if (i != m_cols.size()-1) {
	q+=",";
      }
    }
    q+=")";

    if (m_sb.length()+q.length()+1 > (1<<19)) {
      ret=getInsertString();
    }
    if (count != 0) {
      m_sb.append(",");
    } else {
      m_sb.append(m_insert.generateInsertHeader());
    }
    count++;
    m_sb.append(q);
    if (ret != null) {
      return(ret);
    }
    if (count % m_insert_at ==0) {
      return(getInsertString());
    }
    return(null);
  }

  /**
   * Finalise insert. Insert any remaining rows to db
   * @return success?
   */
  public boolean insertComplete() {
    return(m_insert.insertComplete(this));
  }

  /**
   * Return an sql string for insert of current data
   * @return	insert string
   */
  public String getInsertString() {
    if (count ==0) {
      return(null);
    }
    String ret=m_sb.toString();
    count=0;
    m_sb=new StringBuilder();
    //m_sb.append(generateHeader());
    return(ret);
  }
}
