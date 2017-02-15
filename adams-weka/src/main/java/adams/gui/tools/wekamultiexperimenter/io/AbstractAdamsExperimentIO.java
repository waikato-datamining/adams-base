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
 * AbstractAdamsExperimentIO.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.io;

import adams.core.ClassLister;
import adams.core.io.FileFormatHandler;
import adams.data.io.input.AbstractAdamsExperimentReader;
import adams.data.io.output.AbstractAdamsExperimentWriter;
import adams.gui.chooser.AdamsExperimentFileChooser;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.tools.wekamultiexperimenter.ExperimenterPanel;
import adams.gui.tools.wekamultiexperimenter.experiment.AbstractExperiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for classes that handle loading/saving of experiments.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of experiment
 */
public abstract class AbstractAdamsExperimentIO<T extends AbstractExperiment>
  extends AbstractExperimentIO<T> {

  /** for serialization. */
  private static final long serialVersionUID = -1358953690042787633L;

  /**
   * Creates and returns a file chooser for loading/saving experiments.
   * 
   * @return		the file chooser
   */
  protected BaseFileChooser createFileChooser() {
    AdamsExperimentFileChooser result;

    result = new AdamsExperimentFileChooser();
    result.setCurrentDirectory(
      new File(ExperimenterPanel.getProperties().getPath("Setups.InitialDir", "%c")));

    return result;
  }

  /**
   * Returns the experiment superclass/interface.
   *
   * @return		the super class/interface
   */
  public Class getExperimentClass() {
    return AbstractExperiment.class;
  }

  /**
   * Returns the supported file extensions.
   *
   * @param read	whether for reading or writing
   * @return		the extensions (no dot)
   */
  public String[] getSupportedFileExtensions(boolean read) {
    List<String> 	result;
    Class[]		classes;
    FileFormatHandler	handler;
    String[]		exts;

    result = new ArrayList<>();
    if (read)
      classes = ClassLister.getSingleton().getClasses(AbstractAdamsExperimentReader.class);
    else
      classes = ClassLister.getSingleton().getClasses(AbstractAdamsExperimentWriter.class);

    for (Class cls: classes) {
      try {
	handler = (FileFormatHandler) cls.newInstance();
	exts    = handler.getFormatExtensions();
	for (String ext: exts) {
	  if (!result.contains(ext))
	    result.add(ext);
	}
      }
      catch (Exception e) {
	// ignored
      }
    }
    Collections.sort(result);

    return result.toArray(new String[result.size()]);
  }
}
