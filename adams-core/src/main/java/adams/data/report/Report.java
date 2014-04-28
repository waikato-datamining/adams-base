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
 * Report.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.data.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import adams.core.CloneHandler;
import adams.core.Constants;
import adams.core.Mergeable;
import adams.core.Properties;
import adams.core.Utils;
import adams.core.logging.LoggingLevel;
import adams.core.logging.LoggingObject;
import adams.core.option.AbstractOption;
import adams.data.id.MutableDatabaseIDHandler;

/**
 * Data structure for a report.
 *
 * @author Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class Report
  extends LoggingObject
  implements Serializable, CloneHandler<Report>, Comparable, Mergeable<Report>,
             MutableDatabaseIDHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8377544506571885570L;

  /** field: Dummy report (in case there was no quantitation report and a
   * dummy report was generated automatically). */
  public final static String FIELD_DUMMYREPORT = "Dummy report";

  /** field: Excluded (= dodgy). */
  public final static String FIELD_EXCLUDED = "Excluded";

  /** the parent ID property. */
  public final static String PROPERTY_PARENTID = "Parent ID";

  /** the property suffix for the data type. */
  public final static String DATATYPE_SUFFIX = "\tDataType";

  /** Store Header parameters ( parameter:value ). */
  protected Hashtable<AbstractField,Object> m_Params;

  /** fields. */
  protected Hashtable<String, AbstractField> m_Fields;

  /** the database ID of the data structure this report belongs to. */
  protected int m_DatabaseID;

  /**
   * Default constructor.
   */
  public Report() {
    super();

    m_Params     = new Hashtable<AbstractField,Object>();
    m_DatabaseID = Constants.NO_ID;

    initFields();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    m_LoggingLevel = value;
    m_Logger       = null;
  }

  /**
   * Returns the logging level.
   *
   * @return 		the level
   */
  @Override
  public LoggingLevel getLoggingLevel() {
    return m_LoggingLevel;
  }

  /**
   * Sets the parent ID, i.e., the database ID of the data container this
   * report belongs to.
   *
   * @param value	the database ID
   */
  public void setDatabaseID(int value) {
    m_DatabaseID = value;
  }

  /**
   * Returns the database ID of the data container this report belongs to.
   *
   * @return		the database ID, -1 if not set
   */
  public int getDatabaseID() {
    return m_DatabaseID;
  }

  /**
   * Set field types.
   */
  protected void initFields() {
    m_Fields = new Hashtable<String,AbstractField>();

    addField(new Field(FIELD_DUMMYREPORT, DataType.BOOLEAN));
    addField(new Field(FIELD_EXCLUDED, DataType.BOOLEAN));
  }

  /**
   * Adds the given field.
   *
   * @param field		the field to add
   */
  public void addField(AbstractField field) {
    m_Fields.put(field.getName(), field);
  }

  /**
   * Checks whether the field is already stored.
   *
   * @param field	the field to check
   * @return		true if already added
   */
  public boolean hasField(String field) {
    return m_Fields.containsKey(field);
  }

  /**
   * Checks whether the field is already stored.
   *
   * @param field	the field to check
   * @return		true if already added
   */
  public boolean hasField(AbstractField field) {
    return m_Fields.containsKey(field.getName());
  }

  /**
   * Returns the type for given field. Either based on the stored fields
   * (m_Fields) or {@link DataType#UNKNOWN}.
   *
   * @param field	the field to look for
   * @return		the stored field type or {@link DataType#UNKNOWN} if
   * 			field not stored
   * @see		#m_Fields
   */
  public DataType getFieldType(AbstractField field) {
    if (hasField(field))
      return m_Fields.get(field.getName()).getDataType();
    else
      return DataType.UNKNOWN;
  }

  /**
   * Get all fields as vector.
   *
   * @return		the fields
   */
  public List<AbstractField> getFields() {
    List<AbstractField> 	result;

    result = new ArrayList<AbstractField>();

    for (AbstractField key:m_Params.keySet())
      result.add(key);

    return result;
  }

  /**
   * Get all fields as vector that have the same prefix.
   *
   * @param prefix	the common prefix
   * @return		the fields that match the prefix
   */
  public List<AbstractField> getFields(PrefixOnlyField prefix) {
    List<AbstractField> 	result;
    String			str;

    result = new ArrayList<AbstractField>();
    str    = prefix.getPrefix();
    for (AbstractField key: m_Params.keySet()) {
      if (key.isCompound() && key.getPrefix().equals(str))
	result.add(key);
    }

    return result;
  }

  /**
   * Get all fields as vector that have the same suffix.
   *
   * @param suffix	the common suffix
   * @return		the fields that match the suffix
   */
  public List<AbstractField> getFields(SuffixOnlyField suffix) {
    List<AbstractField> 	result;
    String		str;

    result = new ArrayList<AbstractField>();
    str    = suffix.getSuffix();
    for (AbstractField key: m_Params.keySet()) {
      if (key.isCompound() && key.getSuffix().equals(str))
	result.add(key);
    }

    return result;
  }

  /**
   * Returns all distinct prefix fields.
   *
   * @return		the prefix fields
   */
  public List<PrefixOnlyField> getPrefixFields() {
    List<PrefixOnlyField>	result;
    HashSet<PrefixOnlyField>	fields;

    fields = new HashSet<PrefixOnlyField>();
    for (AbstractField key: m_Params.keySet()) {
      if (key.isCompound())
	fields.add(new PrefixField(key));
    }

    result = new ArrayList<PrefixOnlyField>(fields);
    Collections.sort(result);

    return result;
  }

  /**
   * Returns all distinct suffix fields.
   *
   * @return		the suffix fields
   */
  public List<SuffixOnlyField> getSuffixFields() {
    List<SuffixOnlyField>	result;
    HashSet<SuffixOnlyField>	fields;

    fields = new HashSet<SuffixOnlyField>();
    for (AbstractField key: m_Params.keySet()) {
      if (key.isCompound())
	fields.add(new SuffixField(key));
    }

    result = new ArrayList<SuffixOnlyField>(fields);
    Collections.sort(result);

    return result;
  }

  /**
   * Add parameter value to store.
   *
   * @param key		the key
   * @param value	the value for the key
   */
  public void addParameter(String key, String value) {
    AbstractField f = m_Fields.get(key);
    if (f == null) {
      m_Params.put(new Field(key, DataType.UNKNOWN), Field.fixString(value));
    }
    else {
      Object o = f.valueOf(value);
      if (o == null) {
	getLogger().info("Null object from: " + value.toString());
	return;
      }
      m_Params.put(f, o);
    }
  }

  /**
   * Add parameter value to store.
   *
   * @param key		the key
   * @param value	the value for the key
   */
  public void addParameter(String key, Object value) {
    AbstractField f = m_Fields.get(key);
    if (f == null) {
      m_Params.put(new Field(key, DataType.UNKNOWN), Field.fixString(value.toString()));
    }
    else {
      Object o = f.valueOf(value.toString());
      if (o == null) {
	getLogger().info("Null object from: " + value.toString());
	return;
      }
      m_Params.put(f, value);
    }
  }

  /**
   * Set the parameters.
   *
   * @param ht	hashtable of parameters
   */
  public void setParams(Hashtable<AbstractField,Object> ht) {
    m_Params = ht;
  }

  /**
   * Get the parameters.
   *
   * @return hashtable of parameters
   */
  public Hashtable<AbstractField,Object> getParams() {
    return(m_Params);
  }

  /**
   * Returns whether a certain value is available in this report.
   *
   * @param key		the value to look for
   * @return		true if the value is available
   */
  public boolean hasValue(AbstractField key) {
    return (getValue(key) != null);
  }

  /**
   * Returns whether a certain value is available in this report.
   *
   * @param key		the value to look for
   * @return		true if the value is available
   */
  public boolean hasValue(String key) {
    return hasValue(new Field(key, DataType.UNKNOWN));
  }

  /**
   * Checks whether all the fields are available.
   *
   * @param fields	the required fields
   * @return		true if required fields available
   */
  protected boolean hasValues(AbstractField[] fields) {
    boolean	result;

    result = true;

    for (AbstractField field: fields) {
      if (!hasValue(field)) {
	result = false;
	break;
      }
    }

    return result;
  }

  /**
   * Checks whether all the fields are available.
   *
   * @param fields	the required fields
   * @return		true if required fields available
   */
  public boolean hasValues(String[] fields) {
    AbstractField[]	ffields;
    int			i;

    ffields = new AbstractField[fields.length];
    for (i = 0; i < fields.length; i++)
      ffields[i] = new Field(fields[i], DataType.UNKNOWN);

    return hasValues(ffields);
  }

  /**
   * Sets a value.
   *
   * @param key		the key of the value
   * @param value	the new value
   */
  public void setValue(AbstractField key, Object value) {
    // correct type if necessary (Boolean/Double/String)
    if (value instanceof Byte)
      value = new Double(((Byte) value).doubleValue());
    else if (value instanceof Short)
      value = new Double(((Short) value).doubleValue());
    else if (value instanceof Integer)
      value = new Double(((Integer) value).doubleValue());
    else if (value instanceof Long)
      value = new Double(((Long) value).doubleValue());
    else if (value instanceof Float)
      value = new Double(((Float) value).doubleValue());
    else if (value instanceof Character)
      value = new String(value.toString());

    if (value instanceof String)
      value = Field.fixString((String) value);

    // convert to correct type
    if (value instanceof String) {
      if (key.getDataType() == DataType.NUMERIC)
	value = new Double((String) value);
      else if (key.getDataType() == DataType.BOOLEAN)
	value = new Boolean((String) value);
    }
    else if (value instanceof Double) {
      if ((key.getDataType() == DataType.STRING) || (key.getDataType() == DataType.UNKNOWN))
	value = value.toString();
    }
    else if (value instanceof Boolean) {
      if ((key.getDataType() == DataType.STRING) || (key.getDataType() == DataType.UNKNOWN))
	value = value.toString();
    }

    m_Params.put(key, value);
  }

  /**
   * Sets a numeric value.
   *
   * @param key		the key of the value
   * @param value	the new value
   */
  public void setNumericValue(String key, double value) {
    setValue(new Field(key, DataType.NUMERIC), value);
  }

  /**
   * Sets a string value.
   *
   * @param key		the key of the value
   * @param value	the new value
   */
  public void setStringValue(String key, String value) {
    setValue(new Field(key, DataType.STRING), value);
  }

  /**
   * Sets a boolean value.
   *
   * @param key		the key of the value
   * @param value	the new value
   */
  public void setBooleanValue(String key, boolean value) {
    setValue(new Field(key, DataType.BOOLEAN), value);
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value
   */
  public Object getValue(AbstractField key) {
    return m_Params.get(key);
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value
   */
  public String getStringValue(String key) {
    return getStringValue(new Field(key, DataType.STRING));
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value, null if not present
   */
  public String getStringValue(AbstractField key) {
    if (m_Params.containsKey(key))
      return m_Params.get(key).toString();
    else
      return null;
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value
   */
  public Boolean getBooleanValue(String key) {
    return getBooleanValue(new Field(key, DataType.BOOLEAN));
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value, null if not present
   */
  public Boolean getBooleanValue(AbstractField key) {
    if (m_Params.containsKey(key))
      return (Boolean) m_Params.get(key);
    else
      return null;
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value
   */
  public Double getDoubleValue(String key) {
    return getDoubleValue(new Field(key, DataType.NUMERIC));
  }

  /**
   * Get parameter value, or null if not available.
   *
   * @param key		the key
   * @return 		parameter value, null if not present
   */
  public Double getDoubleValue(AbstractField key) {
    if (m_Params.containsKey(key))
      return (Double) m_Params.get(key);
    else
      return null;
  }

  /**
   * Removes the specified field.
   *
   * @param key		the key
   * @return		the value previously stored in the report, can be null
   * 			if the field wasn't present
   */
  public Object removeValue(AbstractField key) {
    return m_Params.remove(key);
  }

  /**
   * Updates certain dependant fields. This method should be called before
   * saving it to the database, after loading it from the database or when
   * a quantitation report has been created by hand.
   *
   * Default implementation does nothing.
   */
  public void update() {
  }

  /**
   * Return String representation of report.
   *
   * @return 		string representation of report.
   */
  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    List<AbstractField> fields = new ArrayList<AbstractField>();
    for (AbstractField s:m_Params.keySet())
      fields.add(s);
    Collections.sort(fields);
    for (int i = 0; i < fields.size(); i++) {
      AbstractField f = fields.get(i);
      String val = m_Params.get(f).toString();
      ret.append(f.toParseableString() + ": " + val + "\n");
    }

    return ret.toString();
  }

  /**
   * Returns a clone of itself.
   *
   * @return		the clone
   */
  public Report getClone() {
    Report	result;

    result = newInstance(this);

    if (result != null)
      result.assign(this);

    return result;
  }

  /**
   * Obtains all the values from the specified report.
   *
   * @param other	the report to obtain the values form
   */
  public void assign(Report other) {
    m_Fields     = (Hashtable<String,AbstractField>) other.m_Fields.clone();
    m_Params     = (Hashtable<AbstractField,Object>) other.m_Params.clone();
    m_LoggingLevel = other.m_LoggingLevel;
    m_DatabaseID = other.m_DatabaseID;
  }

  /**
   * Sets whether this report is dummy report or not.
   *
   * @param value	if true then this report will be flagged as dummy report
   */
  public void setDummyReport(boolean value) {
    addParameter(FIELD_DUMMYREPORT, "" + value);
  }

  /**
   * Returns whether this report is a dummy report or not.
   *
   * @return		true if this report is a dummy report
   */
  public boolean isDummyReport() {
    AbstractField	field;

    field = new Field(FIELD_DUMMYREPORT, DataType.BOOLEAN);
    if (hasValue(field))
      return getBooleanValue(field);
    else
      return false;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(Object o) {
    int				result;
    Report			qr;
    List<AbstractField>		keys;
    List<AbstractField>		keysQr;
    Enumeration<AbstractField>	enm;
    int				i;

    if (o == null)
      return 1;
    else
      result = 0;

    if (!(o instanceof Report))
      return -1;

    qr = (Report) o;

    if (result == 0)
      result = new Integer(m_Params.size()).compareTo(new Integer(qr.m_Params.size()));

    keys = new ArrayList<AbstractField>();
    enm = m_Params.keys();
    while (enm.hasMoreElements())
      keys.add(enm.nextElement());
    Collections.sort(keys);

    keysQr = new ArrayList<AbstractField>();
    enm = m_Params.keys();
    while (enm.hasMoreElements())
      keysQr.add(enm.nextElement());
    Collections.sort(keysQr);

    for (i = 0; i < keys.size(); i++) {
      result = keys.get(i).compareTo(keysQr.get(i));
      if (result == 0)
	result = m_Params.get(keys.get(i)).toString().compareTo(m_Params.get(keysQr.get(i)).toString());
      if (result != 0)
	break;
    }

    return result;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param obj		the reference object with which to compare.
   * @return		true if this object is the same as the obj argument;
   * 			false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    Report	qr;

    if (obj == null)
      return false;

    if (!(obj instanceof Report))
      return false;

    qr = (Report) obj;

    if (!m_Params.equals(qr.m_Params))
      return false;

    return true;
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * {@link #m_Params} hashtable.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return m_Params.hashCode();
  }

  /**
   * Returns the intersection with this Quantitation Report and the provided
   * one. The result contains the values of this quantitation report. No
   * merging of values between the two is performed.
   *
   * @param report	the report to get the intersection with
   * @return		the intersection
   */
  public Report intersect(Report report) {
    Report				result;
    Hashtable<AbstractField,Object> 	params;

    result = newInstance(report);
    if (result != null) {
      params = new Hashtable<AbstractField,Object>();
      for (AbstractField key: getFields()) {
	if (report.hasValue(key))
	  params.put(key, getValue(key));
      }
      result.setParams(params);
    }

    return result;
  }

  /**
   * Returns the subset of values that this Quantitation Report contains, but
   * not the provided one. The result contains the values of this quantitation
   * report.
   *
   * @param report	the report to get the fields from to not include in
   * 			the result
   * @return		the new report
   */
  public Report minus(Report report) {
    Report				result;
    Hashtable<AbstractField,Object> 	params;

    result = newInstance(report);
    if (result != null) {
      params = new Hashtable<AbstractField,Object>();
      for (AbstractField key: getFields()) {
	if (!report.hasValue(key))
	  params.put(key, getValue(key));
      }
      result.setParams(params);
    }

    return result;
  }

  /**
   * Merges its own data with the one provided by the specified object.
   * Never overwrites its own values, only adds missing ones.
   *
   * @param other		the object to merge with
   */
  public void mergeWith(Report other) {
    List<AbstractField>	fields;
    int			i;

    fields = other.getFields();
    for (i = 0; i < fields.size(); i++) {
      if (hasValue(fields.get(i)))
	continue;
      setValue(fields.get(i), other.getValue(fields.get(i)));
    }
  }

  /**
   * Returns a new instance of the concrete subclass for the report.
   *
   * @param report	the report class to create a new instance from
   * @return		the new instance
   */
  public static Report newInstance(Report report) {
    Report	result;

    try {
      result = (Report) report.getClass().newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Turns the report into a properties object. Also adds the parent ID.
   *
   * @return		the generated
   */
  public Properties toProperties() {
    Properties		result;
    List<AbstractField>	fields;
    int			i;

    result = new Properties();

    // the parent ID
    result.setInteger(PROPERTY_PARENTID, getDatabaseID());

    // transfer properties
    fields = getFields();
    for (i = 0; i < fields.size(); i++) {
      result.setProperty(fields.get(i).toString(), getValue(fields.get(i)).toString());
      result.setProperty(fields.get(i).toString() + DATATYPE_SUFFIX, fields.get(i).getDataType().toString());
    }

    return result;
  }

  /**
   * Parses the string generated by the toString() method.
   *
   * @param s		the string to parse
   * @return		the generated report
   * @see		#toString()
   */
  public static Report parseReport(String s) {
    Report		result;
    String[]		lines;
    int			i;
    int			pos;
    String		fieldStr;
    String		contentStr;
    AbstractField	field;
    Object		content;

    result = null;

    if (!s.equals("null") && (s.length() > 0)) {
      result = new Report();
      lines  = s.split("\n");
      for (i = 0; i < lines.length; i++) {
	pos = lines[i].indexOf(": ");
	if (pos == -1)
	  continue;
	fieldStr   = lines[i].substring(0, pos);
	contentStr = lines[i].substring(pos + 2);
	field      = Field.parseField(fieldStr);
	content    = field.valueOf(contentStr);
	result.m_Params.put(field, content);
      }
    }

    return result;
  }

  /**
   * Parses the properties (generated with the toProperties() method) and
   * generates a report object from it.
   *
   * @param props	the properties to generate the report from
   * @return		the report
   * @see		#toProperties()
   */
  public static Report parseProperties(Properties props) {
    Report		result;
    Enumeration<String>	enm;
    String		name;
    DataType		type;

    result = new Report();
    enm  = (Enumeration<String>) props.propertyNames();
    while (enm.hasMoreElements()) {
      name = enm.nextElement();
      if (name.endsWith(DATATYPE_SUFFIX))
	continue;
      if (name.equals(PROPERTY_PARENTID))
	result.setDatabaseID(props.getInteger(PROPERTY_PARENTID, -1));
      type = (DataType) DataType.valueOf((AbstractOption) null, props.getProperty(name + DATATYPE_SUFFIX, "U"));
      if (type == null)
	type = DataType.UNKNOWN;
      if (type == DataType.NUMERIC)
	result.setValue(new Field(name, type), Utils.toDouble(props.getProperty(name)));
      else if (type == DataType.BOOLEAN)
	result.setValue(new Field(name, type), new Boolean(props.getProperty(name)));
      else
	result.setValue(new Field(name, type), props.getProperty(name));
    }

    return result;
  }
}
