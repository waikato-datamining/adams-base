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
 * AbstractImageFeatureGenerator.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image;

import java.util.logging.Level;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import adams.core.CleanUpHandler;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImage;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Abstract base class for AbstractImage feature generation.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of image to process
 */
public abstract class AbstractImageFeatureGenerator<T extends AbstractImage>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4566948525813804085L;

  /** the current header. */
  protected Instances m_Header;

  /** fields to add to the output data. */
  protected Field[] m_Fields;

  /** the notes to add as attributes. */
  protected BaseString[] m_Notes;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "fields",
	    new Field[0]);

    m_OptionManager.add(
	    "notes", "notes",
	    new BaseString[0]);
  }

  /**
   * Resets the scheme, i.e., the header information.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Header = null;
  }

  /**
   * Sets the targets to add.
   *
   * @param value	the targets
   */
  public void setFields(Field[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the targets to add.
   *
   * @return		the targets
   */
  public Field[] getFields() {
    return m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldsTipText() {
    return "The fields to add to the output.";
  }

  /**
   * Sets the notes to add as attributes.
   *
   * @param value	the notes prefixes, e.g., "PROCESS INFORMATION"
   */
  public void setNotes(BaseString[] value) {
    m_Notes = value;
    reset();
  }

  /**
   * Returns the current notes to add as attributes.
   *
   * @return		the notes prefixes, e.g., "PROCESS INFORMATION"
   */
  public BaseString[] getNotes() {
    return m_Notes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String notesTipText() {
    return "The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'.";
  }

  /**
   * Optional checks of the image.
   * <p/>
   * Default implementation only checks whether image is null.
   *
   * @param img		the image to check
   */
  protected void checkImage(T img) {
    if (img == null)
      throw new IllegalStateException("No image provided!");
  }

  /**
   * Creates the header from a template image.
   *
   * @param img		the image to act as a template
   * @return		the generated header
   */
  public abstract Instances createHeader(T img);

  /**
   * Post-processes the header, adding fields and notes.
   * 
   * @param header	the header to process
   * @return		the post-processed header
   */
  public Instances postProcessHeader(Instances header) {
    Instances	result;
    int		i;
    Add		add;
    String	name;
    
    result = header;
    name   = header.relationName();
    
    // notes
    for (i = m_Notes.length - 1; i >= 0; i--) {
      header = result;
      try {
	add = new Add();
	add.setAttributeIndex("last");
	add.setAttributeName(m_Notes[i].getValue());
	add.setAttributeType(new SelectedTag(Attribute.STRING, Add.TAGS_TYPE));
	add.setInputFormat(header);
	result = Filter.useFilter(header, add);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to add note '" + m_Notes[i] + "' as attribute:", e);
      }
    }
    
    // fields
    for (i = m_Fields.length - 1; i >= 0; i--) {
      header = result;
      try {
	add = new Add();
	add.setAttributeIndex("last");
	add.setAttributeName(m_Fields[i].toDisplayString());
	switch (m_Fields[i].getDataType()) {
	  case BOOLEAN:
	    add.setAttributeType(new SelectedTag(Attribute.NOMINAL, Add.TAGS_TYPE));
	    add.setNominalLabels("no,yes");
	    break;
	  case NUMERIC:
	    add.setAttributeType(new SelectedTag(Attribute.NUMERIC, Add.TAGS_TYPE));
	    break;
	  default:
	    add.setAttributeType(new SelectedTag(Attribute.STRING, Add.TAGS_TYPE));
	    break;
	}
	add.setInputFormat(header);
	result = Filter.useFilter(header, add);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to add field '" + m_Fields[i] + "' as attribute:", e);
      }
    }
    
    result.setRelationName(name);
    
    return result;
  }
  
  /**
   * Returns a double array filled with missing values.
   * 
   * @param numAttributes	the length of the array
   * @return			the array
   */
  protected double[] newArray(int numAttributes) {
    double[]	result;
    int		i;
    
    result = new double[numAttributes];
    for (i = 0; i < result.length; i++)
      result[i] = Utils.missingValue();
    
    return result;
  }
  
  /**
   * Performs the actual flattening of the image. Will use the previously
   * generated header.
   *
   * @param img		the image to process
   * @return		the generated array
   * @see		#m_Header
   */
  public abstract Instance[] doGenerate(T img);

  /**
   * Post-processes the generated instance, adding notes and fields.
   * 
   * @param img		the image container
   * @param inst	the inst to process
   * @return		the updated instance
   */
  public Instance postProcessInstance(T img, Instance inst) {
    Instance	result;
    int		i;
    Attribute	att;
    double[]	values;
    String	valueStr;
    Report	report;
    
    if ((m_Notes.length == 0) && (m_Fields.length == 0))
      return inst;
    values = inst.toDoubleArray();
    
    // notes
    for (i = 0; i < m_Notes.length; i++) {
      att      = m_Header.attribute(m_Notes[i].getValue());
      valueStr = img.getNotes().getPrefixSubset(m_Notes[i].getValue()).toString();
      if (valueStr != null)
	values[att.index()] = att.addStringValue(valueStr);
    }
    
    // fields
    report = img.getReport();
    for (i = 0; i < m_Fields.length; i++) {
      att = m_Header.attribute(m_Fields[i].toDisplayString());
      if (report.hasValue(m_Fields[i])) {
	switch (m_Fields[i].getDataType()) {
	  case NUMERIC:
	    values[att.index()] = report.getDoubleValue(m_Fields[i]);
	    break;
	  case BOOLEAN:
	    if (report.getBooleanValue(m_Fields[i]))
	      values[att.index()] = 1.0;
	    else
	      values[att.index()] = 0.0;
	    break;
	  default:
	    values[att.index()] = att.addStringValue(report.getStringValue(m_Fields[i]));
	    break;
	}
      }
    }
    
    result = new DenseInstance(inst.weight(), values);
    result.setDataset(m_Header);
    
    return result;
  }
  
  /**
   * Process the given image. This method will also create the header if
   * necessary.
   *
   * @param img		the image to process
   * @return		the generated array
   * @see		#m_Header
   * @see		#createHeader(T)
   */
  public Instance[] generate(T img) {
    Instance[]	result;
    Instances	header;
    int		i;

    checkImage(img);

    // create header if necessary
    if (m_Header == null) {
      header = createHeader(img);
      if (header == null)
	throw new IllegalStateException("Failed to create header!");
      m_Header = postProcessHeader(header);
    }

    result = doGenerate(img);
    for (i = 0; i < result.length; i++)
      result[i] = postProcessInstance(img, result[i]);

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <p/>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public T shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public T shallowCopy(boolean expand) {
    return (T) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    reset();
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <p/>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }
}
