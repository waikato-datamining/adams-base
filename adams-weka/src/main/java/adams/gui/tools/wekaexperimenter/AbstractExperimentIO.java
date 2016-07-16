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
 * AbstractExperimentIO.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaexperimenter;

import java.io.File;

import weka.core.xml.KOML;
import weka.experiment.Experiment;
import weka.gui.experiment.ExperimenterDefaults;
import adams.core.logging.LoggingObject;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;

/**
 * Ancestor for classes that handle loading/saving of experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of experiment
 */
public abstract class AbstractExperimentIO<T extends Experiment>
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -1358953690042787633L;
  
  /** the filechooser for experiments. */
  protected BaseFileChooser m_FileChooser;

  /**
   * Creates and returns a file chooser for loading/saving experiments.
   * 
   * @return		the file chooser
   */
  protected BaseFileChooser createFileChooser() {
    BaseFileChooser	result;
    ExtensionFileFilter	filter;
    ExtensionFileFilter	preferred;
    ExtensionFileFilter	xml;
    
    result    = new BaseFileChooser();
    preferred = null;
    
    result.setAcceptAllFileFilterUsed(false);
    result.setAutoAppendExtension(true);
    
    filter = new ExtensionFileFilter("Binary experiment", "exp");
    result.addChoosableFileFilter(filter);
    if (ExperimenterDefaults.getExtension().equals(filter.getExtensions()[0]))
      preferred = filter;

    filter = new ExtensionFileFilter("XML experiment", "xml");
    xml   = filter;
    result.addChoosableFileFilter(filter);
    if (ExperimenterDefaults.getExtension().equals(filter.getExtensions()[0]))
      preferred = filter;

    if (KOML.isPresent()) {
      filter = new ExtensionFileFilter("KOML experiment", KOML.FILE_EXTENSION);
      result.addChoosableFileFilter(filter);
      if (ExperimenterDefaults.getExtension().equals(filter.getExtensions()[0]))
	preferred = filter;
    }

    if (preferred != null)
      result.setFileFilter(preferred);
    else
      result.setFileFilter(xml);
    
    return result;
  }
  
  /**
   * Returns the file chooser to use.
   * 
   * @return		the file chooser
   */
  public synchronized BaseFileChooser getFileChooser() {
    if (m_FileChooser == null)
      m_FileChooser = createFileChooser();
    
    return m_FileChooser;
  }

  /**
   * Creates a new experiment.
   * 
   * @return		the generated experiment, null if failed
   */
  public abstract T create();

  /**
   * Loads an experiment.
   * 
   * @param file	the file to load
   * @return		the experiment, null if failed to load
   */
  public abstract T load(File file);

  /**
   * Saves an experiment.
   * 
   * @param exp		the experiment to save
   * @param file	the file to save to
   * @return		false if failed to save
   */
  public abstract boolean save(T exp, File file);
  
  /**
   * Creates an experiment runner thread object.
   * 
   * @param owner	the owning experimenter
   * @return		the runner
   * @throws Exception	if failed to instantiate runner
   */
  public abstract AbstractExperimentRunner createRunner(ExperimenterPanel owner) throws Exception;
}
