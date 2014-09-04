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

import java.lang.reflect.Array;
import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.base.BaseString;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.featureconverter.AbstractFeatureConverter;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.featureconverter.SpreadSheetFeatureConverter;
import adams.data.report.DataType;
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

  /** the feature converter to use. */
  protected AbstractFeatureConverter m_Converter;
  
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
	    "converter", "converter",
	    new SpreadSheetFeatureConverter());

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

    if (m_Converter != null)
      m_Converter.reset();
  }

  /**
   * Sets the feature converter to use.
   *
   * @param value	the converter
   */
  public void setConverter(AbstractFeatureConverter value) {
    m_Converter = value;
    reset();
  }

  /**
   * Returns the feature converter in use.
   *
   * @return		the converter
   */
  public AbstractFeatureConverter getConverter() {
    return m_Converter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String converterTipText() {
    return "The feature converter to use to produce the output data.";
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
   * Returns the class of the dataset that the converter generates.
   * 
   * @return		the format
   */
  public Class getDatasetFormat() {
    return m_Converter.getDatasetFormat();
  }
  
  /**
   * Returns the class of the row that the converter generates.
   * 
   * @return		the format
   */
  public Class getRowFormat() {
    return m_Converter.getRowFormat();
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
  public abstract HeaderDefinition createHeader(T img);

  /**
   * Post-processes the header, adding fields and notes.
   * 
   * @param header	the header to process
   * @return		the post-processed header
   */
  public HeaderDefinition postProcessHeader(HeaderDefinition header) {
    HeaderDefinition	result;
    int			i;
    
    result = header;
    
    // notes
    for (i = 0; i < m_Notes.length; i++)
      header.add(m_Notes[i].getValue(), DataType.STRING);
    
    // fields
    for (i = 0; i < m_Fields.length; i++)
      header.add(m_Fields[i].getName(), m_Fields[i].getDataType());
    
    return result;
  }
  
  /**
   * Performs the actual feature genration.
   *
   * @param img		the image to process
   * @return		the generated features
   */
  public abstract List<Object>[] generateRows(T img);

  /**
   * Post-processes the generated row, adding notes and fields.
   * 
   * @param img		the image container
   * @param inst	the inst to process
   * @return		the updated instance
   */
  public List<Object> postProcessRow(T img, List<Object> data) {
    int		i;
    String	valueStr;
    Report	report;
    
    // notes
    for (i = 0; i < m_Notes.length; i++) {
      valueStr = img.getNotes().getPrefixSubset(m_Notes[i].getValue()).toString();
      data.add(valueStr);
    }
    
    // fields
    report = img.getReport();
    for (i = 0; i < m_Fields.length; i++) {
      if (report.hasValue(m_Fields[i])) {
	switch (m_Fields[i].getDataType()) {
	  case NUMERIC:
	    data.add(report.getDoubleValue(m_Fields[i]));
	    break;
	  case BOOLEAN:
	    data.add(report.getBooleanValue(m_Fields[i]));
	    break;
	  default:
	    data.add(report.getStringValue(m_Fields[i]));
	    break;
	}
      }
      else {
	data.add(null);
      }
    }
    
    return data;
  }

  /**
   * Post-processes the generated rows, adding notes and fields.
   * 
   * @param img		the image container
   * @param inst	the inst to process
   * @return		the updated instance
   */
  public List<Object>[] postProcessRows(T img, List<Object>[] data) {
    List<Object>[]	result;
    int			i;
    
    result = new List[data.length];
    for (i = 0; i < result.length; i++)
      result[i] = postProcessRow(img, data[i]);
    
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
  public Object[] generate(T img) {
    Object[]		result;
    HeaderDefinition	header;
    List<Object>[]	data;
    int			i;

    checkImage(img);

    // create header if necessary
    if (!m_Converter.isInitialized()) {
      header = createHeader(img);
      if (header == null)
	throw new IllegalStateException("Failed to create header!");
      header = postProcessHeader(header);
      m_Converter.generateHeader(header);
    }

    data   = generateRows(img);
    data   = postProcessRows(img, data);
    result = (Object[]) Array.newInstance(m_Converter.getRowFormat(), data.length);
    for (i = 0; i < result.length; i++)
      result[i] = m_Converter.generateRow(data[i]);

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
