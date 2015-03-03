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
 * AbstractInstancesGenerator.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.instances;

import java.util.logging.Level;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.data.weka.ArffUtils;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;

/**
 * Abstract base class for schemes that turn temperature profiles into
 * weka.core.Instance objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * <T> the type of data to process
 */
public abstract class AbstractInstanceGenerator<T extends DataContainer & ReportHandler>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, DatabaseConnectionHandler,
             ShallowCopySupporter<AbstractInstanceGenerator> {

  /** for serialization. */
  private static final long serialVersionUID = 5543015283566767256L;

  /** the "true" label for boolean data types. */
  public final static String LABEL_TRUE = "true";

  /** the "false" label for boolean data types. */
  public final static String LABEL_FALSE = "false";

  /** the generated header. */
  protected Instances m_OutputHeader;

  /** whether to add the database ID. */
  protected boolean m_AddDatabaseID;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /** whether to tolerate header changes. */
  protected boolean m_TolerateHeaderChanges;

  /** whether to operate in offline mode. */
  protected boolean m_Offline;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "add-db-id", "addDatabaseID",
	    false);

    m_OptionManager.add(
	    "tolerate-header-changes", "tolerateHeaderChanges",
	    false);

    m_OptionManager.add(
	    "offline", "offline",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DatabaseConnection = getDefaultDatabaseConnection();
  }

  /**
   * Resets the generator (but does not clear the input data!).
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  @Override
  protected void reset() {
    super.reset();

    m_OutputHeader = null;
  }

  /**
   * Cleans up data structures, frees up memory.
   * Sets the input data to null.
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

  /**
   * Sets whether the database ID is added to the data or not.
   *
   * @param value 	true if database ID should be added
   */
  public void setAddDatabaseID(boolean value) {
    m_AddDatabaseID = value;
    reset();
  }

  /**
   * Returns whether the database ID is added.
   *
   * @return 		true if database ID is added
   */
  public boolean getAddDatabaseID() {
    return m_AddDatabaseID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addDatabaseIDTipText() {
    return "If set to true, then the database ID will be added to the output.";
  }

  /**
   * Sets whether to operate in offline mode.
   *
   * @param value 	true if to operate in offline mode
   */
  public void setOffline(boolean value) {
    m_Offline = value;
    reset();
  }

  /**
   * Returns whether the generator operates in offline mode.
   *
   * @return 		true if operating in offline mode
   */
  public boolean getOffline() {
    return m_Offline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String offlineTipText() {
    return "If set to true, the generator operates in offline mode, ie does not access database.";
  }

  /**
   * Sets whether to tolerate header changes and merely re-generating the 
   * header instead of throwing an exception.
   *
   * @param value 	true if to tolerate header changes
   */
  public void setTolerateHeaderChanges(boolean value) {
    m_TolerateHeaderChanges = value;
    reset();
  }

  /**
   * Returns whether to tolerate header changes and merely re-generating the 
   * header instead of throwing an exception.
   *
   * @return 		true if to tolerate header changes
   */
  public boolean getTolerateHeaderChanges() {
    return m_TolerateHeaderChanges;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tolerateHeaderChangesTipText() {
    return 
	"If set to true, then changes in the header get tolerated (and the "
	+ "header recreated) instead of causing an error.";
  }

  /**
   * Returns the default database connection.
   *
   * @return		the default database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection = value;
    reset();
  }

  /**
   * Returns the current header.
   *
   * @return		the header, can be null
   */
  public Instances getOutputHeader() {
    return m_OutputHeader;
  }

  /**
   * Checks whether the setup is consistent.
   * <p/>
   * Default implementation does nothing.
   *
   * @return		null if everything OK, otherwise the error message
   */
  public String checkSetup() {
    return null;
  }

  /**
   * Returns the generated data, generates it if necessary.
   *
   * @param data	the profile to turn into an instance
   * @return		the generated data
   */
  public Instance generate(T data) {
    Instance	result;

    // input/profile
    checkInput(data);

    // header/instances
    if (m_OutputHeader == null) {
      generateHeader(data);
      postProcessHeader(data);
    }

    // check header
    try {
      checkHeader(data);
    }
    catch (Exception e) {
      if (m_TolerateHeaderChanges) {
	generateHeader(data);
	postProcessHeader(data);
      }
      else {
	throw new IllegalStateException(e);
      }
    }

    // output/instance
    result = generateOutput(data);
    result = postProcessOutput(data, result);

    return result;
  }

  /**
   * Checks the input profile.
   * <p/>
   * The default implementation only checks whether there is any data set.
   *
   * @param data	the data to process
   */
  protected void checkInput(T data) {
    if (data == null)
      throw new IllegalStateException("No input data provided!");
  }

  /**
   * Checks whether the number of waves are the same.
   *
   * @param data	the input data
   */
  protected abstract void checkHeader(T data);

  /**
   * Generates the header of the output data.
   *
   * @param data	the input data
   */
  protected abstract void generateHeader(T data);

  /**
   * Interpretes the position string based on the given dataset.
   * "first", "second", "third", "last-2", "last-1" and "last" and "last+1" are valid as well.
   *
   * @param data	the data to base the intepretation on
   * @param position	the position string
   * @return		the numeric position string
   */
  protected String interpretePosition(Instances data, String position) {
    if (position.equals("first"))
      return "1";
    else if (position.equals("second"))
      return "2";
    else if (position.equals("third"))
      return "3";
    else if (position.equals("last-2"))
      return "" + (data.numAttributes() - 2);
    else if (position.equals("last-1"))
      return "" + (data.numAttributes() - 1);
    else if (position.equals("last"))
      return "" + data.numAttributes();
    else if (position.equals("last+1"))
      return "" + (data.numAttributes()+1);
    else
      return position;
  }

  /**
   * Adds IDs, notes, additional fields to header.
   *
   * @param data	the input data
   */
  protected void postProcessHeader(T data) {
    Add		add;

    // add the database ID to the output?
    if (m_AddDatabaseID) {
      try {
	add = new Add();
	add.setAttributeIndex("1");
	add.setAttributeName(ArffUtils.getDBIDName());
	add.setAttributeType(new SelectedTag(Attribute.NUMERIC, Add.TAGS_TYPE));
	add.setInputFormat(m_OutputHeader);
	m_OutputHeader = Filter.useFilter(m_OutputHeader, add);
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Error initializing the Add filter for database ID!", e);
      }
    }
  }

  /**
   * Generates the actual data.
   *
   * @param data	the input data to transform
   * @return		the generated data
   */
  protected abstract Instance generateOutput(T data);

  /**
   * For adding IDs, notes, additional fields to the data.
   *
   * @param data	the input data
   * @param inst	the generated instance
   * @return		the processed instance
   */
  protected Instance postProcessOutput(T data, Instance inst) {
    Instance	result;
    double[]	values;
    int		index;
    Report	report;

    values = inst.toDoubleArray();
    report = data.getReport();

    if (m_AddDatabaseID) {
      index         = m_OutputHeader.attribute(ArffUtils.getDBIDName()).index();
      values[index] = report.getDatabaseID();
    }

    result = new DenseInstance(1.0, values);
    result.setDataset(m_OutputHeader);

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
  public AbstractInstanceGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractInstanceGenerator shallowCopy(boolean expand) {
    return (AbstractInstanceGenerator) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Returns a list with classnames of generators.
   *
   * @return		the generator classnames
   */
  public static String[] getGenerators() {
    return ClassLister.getSingleton().getClassnames(AbstractInstanceGenerator.class);
  }

  /**
   * Instantiates the generator with the given options.
   *
   * @param classname	the classname of the generator to instantiate
   * @param options	the options for the generator
   * @return		the instantiated generator or null if an error occurred
   */
  public static AbstractInstanceGenerator forName(String classname, String[] options) {
    AbstractInstanceGenerator	result;

    try {
      result = (AbstractInstanceGenerator) OptionUtils.forName(AbstractInstanceGenerator.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the generator from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			generator to instantiate
   * @return		the instantiated generator
   * 			or null if an error occurred
   */
  public static AbstractInstanceGenerator forCommandLine(String cmdline) {
    return (AbstractInstanceGenerator) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
