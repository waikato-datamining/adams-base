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
 * WekaExperimenterPreferencesPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.goe.GenericObjectEditorPanel;
import weka.core.Utils;
import weka.core.WekaPackageManager;
import weka.gui.experiment.ExperimenterDefaults;

import java.io.File;

/**
 * Preferences for the WEKA Experimenter.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExperimenterPreferencesPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3895159356677639564L;
  
  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType("Extension", PropertyType.LIST);
    setList("Extension", new String[]{".exp", ".xml"});
    addPropertyType("Destination", PropertyType.LIST);
    setList("Destination", new String[]{"ARFF file", "CSV file", "JDBC database"});
    addPropertyType("ExperimentType", PropertyType.LIST);
    setList("ExperimentType", new String[]{"Cross-validation", "Train/Test Percentage Split (data randomized)", "Train/Test Percentage Split (order preserved)"});
    addPropertyType("UseClassification", PropertyType.BOOLEAN);
    addPropertyType("Folds", PropertyType.INTEGER);
    addPropertyType("TrainPercentage", PropertyType.INTEGER);
    addPropertyType("Repetitions", PropertyType.INTEGER);
    addPropertyType("DatasetsFirst", PropertyType.BOOLEAN);
    addPropertyType("InitialDatasetsDirectory", PropertyType.DIRECTORY);
    addPropertyType("UseRelativePaths", PropertyType.BOOLEAN);
    addPropertyType("Tester", PropertyType.LIST);
    setList("Tester", new String[]{"Paired T-Tester (corrected)", "Paired T-Tester"});
    addPropertyType("Row", PropertyType.STRING);
    addPropertyType("Column", PropertyType.STRING);
    addPropertyType("ComparisonField", PropertyType.STRING);
    addPropertyType("Significance", PropertyType.DOUBLE);
    addPropertyType("Sorting", PropertyType.STRING);
    addPropertyType("ShowStdDev", PropertyType.BOOLEAN);
    addPropertyType("ShowAverage", PropertyType.BOOLEAN);
    addPropertyType("MeanPrecision", PropertyType.INTEGER);
    addPropertyType("StdDevPrecision", PropertyType.INTEGER);
    addPropertyType("OutputFormat", PropertyType.OBJECT_EDITOR);
    setChooser("OutputFormat", new GenericObjectEditorPanel(weka.experiment.ResultMatrix.class, new weka.experiment.ResultMatrixPlainText(), true));
    addPropertyType("RemoveFilterClassnames", PropertyType.BOOLEAN);

    try {
      setPreferences(new Properties(Utils.readProperties(ExperimenterDefaults.PROPERTY_FILE)));
    }
    catch (Exception e) {
      System.err.println("Failed to load WEKA Experimenter properties:");
      e.printStackTrace();
      setPreferences(new Properties());
    }
  }
  
  /**
   * The title of the preferences.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Experimenter";
  }

  /**
   * Returns whether the panel requires a wrapper scrollpane/panel for display.
   * 
   * @return		true if wrapper required
   */
  @Override
  public boolean requiresWrapper() {
    return false;
  }

  /**
   * Activates the settings.
   * 
   * @return		null if successfully activated, otherwise error message
   */
  @Override
  public String activate() {
    String	filename;
    
    if (!WekaPackageManager.PROPERTIES_DIR.exists()) {
      WekaPackageManager.PROPERTIES_DIR.mkdirs();
      if (!WekaPackageManager.PROPERTIES_DIR.exists())
	return "Failed to create WEKA props directory: " + WekaPackageManager.PROPERTIES_DIR;
    }
    
    filename = WekaPackageManager.PROPERTIES_DIR.getAbsolutePath() + File.separator + new File(ExperimenterDefaults.PROPERTY_FILE).getName();
    if (!getPreferences().save(filename))
      return "Failed to save WEKA Experimenter properties: " + filename;
    
    return null;
  }
}
