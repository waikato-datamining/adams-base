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
 * AbstractColumnFinderApplier.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package weka.filters.unsupervised.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.CapabilitiesHandler;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;
import weka.filters.SimpleBatchFilter;
import adams.core.option.OptionUtils;
import adams.data.weka.columnfinder.AbstractColumnFinder;
import adams.data.weka.columnfinder.ColumnFinder;
import adams.data.weka.columnfinder.NullFinder;
import adams.data.weka.columnfinder.TrainableColumnFinder;

/**
 * Ancestor for filters that apply {@link ColumnFinder} schemes to the data.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColumnFinderApplier
  extends SimpleBatchFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2750612199034543886L;

  /** The classifier template used to do the classification. */
  protected ColumnFinder m_ColumnFinder = new NullFinder();

  /** Whether to invert the column indices. */
  protected boolean m_Invert = false;
  
  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  @Override
  public Enumeration listOptions() {
    Vector        	result;
    Enumeration   	en;

    result = new Vector();

    en = super.listOptions();
    while (en.hasMoreElements())
      result.addElement(en.nextElement());

    result.addElement(new Option(
	"\tFull class name of column finder to use, followed\n"
	+ "\tby scheme options. eg:\n"
	+ "\t\t\"" + NullFinder.class.getName() + " -D 1\"\n"
	+ "\t(default: " + NullFinder.class.getName() + ")",
	"W", 1, "-W <column finder specification>"));

    result.addElement(new Option(
	"\tWhether to invert the found column indices.\n"
	+ "\t(default: off)",
	"invert", 0, "-invert"));

    return result.elements();
  }

  /**
   * Parses the options for this object.
   *
   * @param options	the options to use
   * @thcolumns Exception	if setting of options fails
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    setInvert(Utils.getFlag("invert", options));
    
    tmpStr = Utils.getOption('W', options);
    if (tmpStr.length() == 0)
      tmpStr = NullFinder.class.getName();
    setColumnFinder(AbstractColumnFinder.forCommandLine(tmpStr));

    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    if (getInvert())
      result.add("-invert");

    result.add("-W");
    result.add(OptionUtils.getCommandLine(getColumnFinder()));
    
    return result.toArray(new String[result.size()]);	  
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return            the capabilities of this object
   * @see               Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities 	result;

    if (m_ColumnFinder instanceof CapabilitiesHandler) {
      result = ((CapabilitiesHandler) m_ColumnFinder).getCapabilities();
    }
    else {
      result = new Capabilities(this);
      result.enableAll();
      result.enable(Capability.NO_CLASS);
      result.enable(Capability.MISSING_VALUES);
      result.enable(Capability.MISSING_CLASS_VALUES);
      result.setMinimumNumberInstances(1);
    }

    return result;
  }

  /**
   * Sets the column finder to use.
   *
   * @param value 	The column finder to be used (with its options set).
   */
  public void setColumnFinder(ColumnFinder value) {
    m_ColumnFinder = value;
  }
  
  /**
   * Returns the column finder used by the filter.
   *
   * @return 		The column finder to be used.
   */
  public ColumnFinder getColumnFinder() {
    return m_ColumnFinder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public abstract String columnFinderTipText();
  
  /**
   * Set whether the invert the column indices.
   *
   * @param value 	true if to invert the indices
   */
  public void setInvert(boolean value) {
    m_Invert = value;
  }

  /**
   * Returns whether the invert the column indices.
   *
   * @return 		true if the indices get inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }
  
  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for
   * 			displaying in the explorer/experimenter gui
   */
  public String invertTipText() {
    return 
	"If enabled the column indices get inverted, ie the indices of the columns "
	+ "get returned that weren't identified by the column finder.";
  }

  /**
   * Determines the output format based on the input format and returns 
   * this. In case the output format cannot be returned immediately, i.e.,
   * immediateOutputFormat() returns false, then this method will be called
   * from batchFinished().
   *
   * @param inputFormat     the input format to base the output format on
   * @return                the output format
   * @thcolumns Exception      in case the determination goes wrong
   */
  @Override
  protected abstract Instances determineOutputFormat(Instances inputFormat) throws Exception;

  /**
   * Applies the indices to the data. In case inverting is enabled, the indices
   * have already been inverted.
   * 
   * @param data	the data to process
   * @param indices	the indices to use
   * @return		the processed data
   */
  protected abstract Instances apply(Instances data, int[] indices);
  
  /**
   * Processes the given data (may change the provided dataset) and returns
   * the modified version. This method is called in batchFinished().
   *
   * @param instances   the data to process
   * @return            the modified data
   * @thcolumns Exception  in case the processing goes wrong
   * @see               #batchFinished()
   */
  @Override
  protected Instances process(Instances instances) throws Exception {
    int[]		indices;
    HashSet<Integer>	set;
    ArrayList<Integer>	inverted;
    int			i;
    
    if (m_ColumnFinder instanceof TrainableColumnFinder) {
      if (!((TrainableColumnFinder) m_ColumnFinder).isColumnFinderTrained())
	((TrainableColumnFinder) m_ColumnFinder).trainColumnFinder(instances);
    }
    indices = m_ColumnFinder.findColumns(instances);
    
    if (m_Invert) {
      set      = new HashSet<Integer>(adams.core.Utils.toList(indices));
      inverted = new ArrayList<Integer>();
      for (i = 0; i < instances.numAttributes(); i++) {
	if (!set.contains(i))
	  inverted.add(i);
      }
      indices = adams.core.Utils.toIntArray(inverted);
    }
    
    return apply(instances, indices);
  }
}
