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
 * AbstractWekaExperimentIO.java
 * Copyright (C) 2014-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.io;

import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import weka.core.xml.KOML;
import weka.experiment.Experiment;
import weka.gui.experiment.ExperimenterDefaults;

import java.io.File;

/**
 * Ancestor for classes that handle loading/saving of experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of experiment
 */
public abstract class AbstractWekaExperimentIO<T extends Experiment>
  extends AbstractExperimentIO<T> {

  /** for serialization. */
  private static final long serialVersionUID = -1358953690042787633L;

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
    
    result = new BaseFileChooser();
    result.setCurrentDirectory(
      new File(ExperimenterPanel.getProperties().getPath("Setups.InitialDir", "%c")));

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
   * Returns the experiment superclass/interface.
   *
   * @return		the super class/interface
   */
  public Class getExperimentClass() {
    return Experiment.class;
  }
}
