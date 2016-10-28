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
 * WekaExplorerPreferencesPanel.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.Properties;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.goe.GenericObjectEditorPanel;
import weka.core.Utils;
import weka.core.WekaPackageManager;
import weka.gui.explorer.ExplorerDefaults;

import java.io.File;

/**
 * Preferences for the WEKA Explorer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaExplorerPreferencesPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3895159356677639564L;
  
  @Override
  protected void initGUI() {
    super.initGUI();

    addPropertyType("Tabs", PropertyType.STRING);
    addPropertyType("InitialDirectory", PropertyType.DIRECTORY_ABSOLUTE);
    addPropertyType("enableUndo", PropertyType.BOOLEAN);
    addPropertyType("undoDirectory", PropertyType.DIRECTORY_ABSOLUTE);
    addPropertyType("Filter", PropertyType.OBJECT_EDITOR);
    setChooser("Filter", new GenericObjectEditorPanel(weka.filters.Filter.class, new weka.filters.AllFilter(), true));
    addPropertyType("Classifier", PropertyType.OBJECT_EDITOR);
    setChooser("Classifier", new GenericObjectEditorPanel(weka.classifiers.Classifier.class, new weka.classifiers.rules.ZeroR(), true));
    addPropertyType("ClassifierTestMode", PropertyType.LIST);
    setList("ClassifierTestMode", new String[]{"1", "2", "3", "4"});
    setHelp("ClassifierTestMode", "1 - cross-validation, 2 - percentage split, 3 - use training set, 4 - supplied test set");
    addPropertyType("ClassifierCrossvalidationFolds", PropertyType.INTEGER);
    addPropertyType("ClassifierPercentageSplit", PropertyType.INTEGER);
    addPropertyType("ClassifierOutputModel", PropertyType.BOOLEAN);
    addPropertyType("ClassifierOutputPerClassStats", PropertyType.BOOLEAN);
    addPropertyType("ClassifierOutputEntropyEvalMeasures", PropertyType.BOOLEAN);
    addPropertyType("ClassifierOutputConfusionMatrix", PropertyType.BOOLEAN);
    addPropertyType("ClassifierStorePredictionsForVis", PropertyType.BOOLEAN);
    addPropertyType("ClassifierOutputPredictions", PropertyType.BOOLEAN);
    addPropertyType("ClassifierOutputAdditionalAttributes", PropertyType.STRING);
    addPropertyType("ClassifierCostSensitiveEval", PropertyType.BOOLEAN);
    addPropertyType("ClassifierRandomSeed", PropertyType.INTEGER);
    addPropertyType("ClassifierPreserveOrder", PropertyType.BOOLEAN);
    addPropertyType("ClassifierOutputSourceCode", PropertyType.BOOLEAN);
    addPropertyType("ClassifierSourceCodeClass", PropertyType.STRING);
    addPropertyType("ClassifierErrorsPlotInstances", PropertyType.STRING);
    addPropertyType("ClassifierErrorsMinimumPlotSizeNumeric", PropertyType.INTEGER);
    addPropertyType("ClassifierErrorsMaximumPlotSizeNumeric", PropertyType.INTEGER);
    addPropertyType("Clusterer", PropertyType.OBJECT_EDITOR);
    setChooser("Clusterer", new GenericObjectEditorPanel(weka.clusterers.Clusterer.class, new weka.clusterers.SimpleKMeans(), true));
    addPropertyType("ClustererTestMode", PropertyType.LIST);
    setList("ClustererTestMode", new String[]{"2", "3", "4", "5"});
    setHelp("ClustererTestMode", "2 - percentage split, 3 - use training set, 4 - supplied test set, 5 - classes to clusters evaluation");
    addPropertyType("ClustererStoreClustersForVis", PropertyType.BOOLEAN);
    addPropertyType("ClustererAssignmentsPlotInstances", PropertyType.STRING);
    addPropertyType("Associator", PropertyType.OBJECT_EDITOR);
    setChooser("Associator", new GenericObjectEditorPanel(weka.associations.Associator.class, new weka.associations.Apriori(), true));
    addPropertyType("ASEvaluation", PropertyType.OBJECT_EDITOR);
    setChooser("ASEvaluation", new GenericObjectEditorPanel(weka.attributeSelection.ASEvaluation.class, new weka.attributeSelection.CfsSubsetEval(), true));
    addPropertyType("ASSearch", PropertyType.OBJECT_EDITOR);
    setChooser("ASSearch", new GenericObjectEditorPanel(weka.attributeSelection.ASSearch.class, new weka.attributeSelection.BestFirst(), true));
    addPropertyType("ASTestMode", PropertyType.LIST);
    setList("ASTestMode", new String[]{"0", "1"});
    setHelp("ASTestMode", "0 - use full training set, 1 - cross-validation");
    addPropertyType("ASCrossvalidationFolds", PropertyType.INTEGER);
    addPropertyType("ASRandomSeed", PropertyType.INTEGER);

    try {
      setPreferences(new Properties(Utils.readProperties(ExplorerDefaults.PROPERTY_FILE)));
    }
    catch (Exception e) {
      System.err.println("Failed to load WEKA Explorer properties:");
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
    return "WEKA Explorer";
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
    
    filename = WekaPackageManager.PROPERTIES_DIR.getAbsolutePath() + File.separator + new File(ExplorerDefaults.PROPERTY_FILE).getName();
    if (!getPreferences().save(filename))
      return "Failed to save WEKA Explorer properties: " + filename;
    
    return null;
  }
}
