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
 * BulkInsertTable.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.db;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

/**
 * Table that can do bulk inserts.
 *
 * @author dale
 * @version $Revision$
 */
public abstract class BulkInsertTable
  extends AbstractIndexedTable{

  /** for serialization. */
  private static final long serialVersionUID = 401732448232750879L;

  /** columns for bulk insert. */
  protected Vector<String> m_cols=new Vector<String>();

  /** debug. **/
  private boolean debug=false;

  /**
   * Constructor. Setup column vector.
   *
   * @param dbcon	the database context this table is used in
   * @param tableName	the name of the table
   */
  public BulkInsertTable(AbstractDatabaseConnection dbcon, String tablename) {
    super(dbcon, tablename);

    ColumnMapping cm=getColumnMapping();
    for (Enumeration enum1 = cm.keys() ; enum1.hasMoreElements() ;) {
      String cname=(String)enum1.nextElement();
      m_cols.add(cname);
    }
  }

  /**
   * Return Insert header. Generated from column names
   *
   * @return insert header
   */
  public String generateInsertHeader() {
    return("INSERT INTO "+getTableName()+" ("+getInsertColumnsAsString()+") VALUES ");
  }

  /**
   * Attach a multiple insert object to this table.
   *
   * @param mi
   */
  public void attach(MultipleInsert mi) {
    mi.setTable(this);
    mi.setColumnVector(this.m_cols);
  }

  /**
   * Insert bulk data.
   *
   * @param mi		multiple insert
   * @param vals	hashtable of values
   * @return	success?
   */
  public boolean insert(MultipleInsert mi, Hashtable<String,String> vals) {
    String insString=mi.insert(vals);
    if (insString!=null) {
      try {
	if (debug) {
	  getLogger().severe("Entered ms point execute from insert");
	}
	Boolean ret=execute(insString);
	if (ret == null)
	  ret = false;
	if (debug) {
	  getLogger().severe("Complete ms point execute from insert");
	}
	return(ret);
      } catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to insert", e);
	return(false);
      }
    }
    return(true);//ok
  }

  /**
   * Complete bulk insert.
   *
   * @param mi
   * @return	success?
   */
  protected boolean doInsert(MultipleInsert mi) {
    String ins=mi.getInsertString();
    if (ins!=null) {
      try {
	if (debug) {
	  getLogger().severe("Entered ms point execute");
	}
	return (execute(ins) != null);
      } catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to insert", e);
	return(false);
      }
    }
    return(true);
  }

  /**
   * Complete bulk insert.
   *
   * @param mi
   * @return	success?
   */
  public boolean insertComplete(MultipleInsert mi) {
    return(doInsert(mi));
  }

  /**
   * Return columns as comma separated list.
   *
   * @return	column names
   */
  protected String getInsertColumnsAsString() {
    String q="";
    for (int i=0;i<m_cols.size();i++) {
      String val=m_cols.elementAt(i);

      q+=val;
      if (i != m_cols.size()-1) {
	q+=",";
      }
    }
    return(q);
  }
}
