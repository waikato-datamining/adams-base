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
 * InstanceExplorerHandler.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ConverterUtils.DataSource;
import adams.data.instance.Instance;
import adams.gui.visualization.instance.InstanceContainer;
import adams.gui.visualization.instance.InstancePanel;

/**
 <!-- globalinfo-start -->
 * Displays the following WEKA dataset types in the Instance Explorer: csv,arff,arff.gz,xrff,xrff.gz
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceExplorerHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1029811668794627962L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the following WEKA dataset types in the Instance Explorer: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"csv", "arff", "arff.gz", "xrff", "xrff.gz"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    InstancePanel		result;
    Instances			dataset;
    int				i;
    Instance			inst;
    List<InstanceContainer>	data;

    result = new InstancePanel();
    try {
      data    = new ArrayList<InstanceContainer>();
      dataset = DataSource.read(file.getAbsolutePath());
      for (i = 0; i < dataset.numInstances(); i++) {
	inst = new Instance();
	inst.set(dataset.instance(i));
	inst.setID((i + 1) + "." + dataset.relationName());
	data.add(result.getContainerManager().newContainer(inst));
      }
      result.getContainerManager().addAll(data);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to load dataset '" + file + "':", e);
    }

    return new PreviewPanel(result);
  }
}
