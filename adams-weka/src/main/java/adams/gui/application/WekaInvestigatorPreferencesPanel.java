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
 * WekaInvestigatorPreferencesPanel.java
 * Copyright (C) 2016-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.application;

import adams.core.io.FileUtils;
import adams.data.io.input.AbstractIndexedSplitsRunsReader;
import adams.data.io.input.JsonIndexedSplitsRunsReader;
import adams.data.weka.classattribute.LastAttribute;
import adams.data.weka.relationname.NoChange;
import adams.env.Environment;
import adams.env.WekaInvestigatorDefinition;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.goe.GenericArrayEditorPanel;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.finalmodel.AbstractFinalModelGenerator;
import adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.finalmodel.Simple;
import adams.multiprocess.JobRunner;
import adams.multiprocess.LocalJobRunner;

/**
 * Preferences for the WEKA Investigator.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaInvestigatorPreferencesPanel
  extends AbstractPropertiesPreferencesPanel {

  /** for serialization. */
  private static final long serialVersionUID = 3895159356677639564L;
  
  @Override
  protected void initGUI() {
    super.initGUI();

    // general
    addPropertyType("General.DefaultTabs", PropertyType.COMMA_SEPARATED_LIST);
    addPropertyType("General.MaxTabUndo", PropertyType.INTEGER);
    addPropertyType("General.PromptCloseTab", PropertyType.BOOLEAN);
    addPropertyType("General.DefaultDataTableHeight", PropertyType.INTEGER);
    addPropertyType("General.ClassAttributeHeuristic", PropertyType.OBJECT_EDITOR);
    setChooser("General.ClassAttributeHeuristic", new GenericObjectEditorPanel(
      adams.data.weka.classattribute.AbstractClassAttributeHeuristic.class,
      new LastAttribute(), true));
    addPropertyType("General.RelationNameHeuristic", PropertyType.OBJECT_EDITOR);
    setChooser("General.RelationNameHeuristic", new GenericObjectEditorPanel(
      adams.data.weka.relationname.AbstractRelationNameHeuristic.class,
      new NoChange(), true));
    addPropertyType("General.UndoEnabled", PropertyType.BOOLEAN);
    addPropertyType("General.CalculateModelSize", PropertyType.BOOLEAN);
    addPropertyType("General.TestingUpdateInterval", PropertyType.INTEGER);
    addPropertyType("General.SortAttributeNames", PropertyType.BOOLEAN);
    addPropertyType("General.ResultHistoryToolTips", PropertyType.BOOLEAN);

    // general
    addPropertyType("Data.MaxColWidth", PropertyType.INTEGER);

    // preprocessing
    addPropertyType("Preprocess.Filter", PropertyType.OBJECT_EDITOR);
    setChooser("Preprocess.Filter", new GenericObjectEditorPanel(
      weka.filters.Filter.class, new weka.filters.AllFilter(), true));
    addPropertyType("Preprocess.ReplaceDatasets", PropertyType.BOOLEAN);
    addPropertyType("Preprocess.KeepName", PropertyType.BOOLEAN);
    addPropertyType("Preprocess.BatchFilter", PropertyType.BOOLEAN);
    addPropertyType("Preprocess.Serialize", PropertyType.BOOLEAN);
    addPropertyType("Preprocess.SerializeFile", PropertyType.FILE_ABSOLUTE);
    addPropertyType("Preprocess.MaxAttributesToVisualize", PropertyType.INTEGER);

    // associate
    addPropertyType("Associate.Associator", PropertyType.OBJECT_EDITOR);
    setChooser("Associate.Associator", new GenericObjectEditorPanel(
      weka.associations.Associator.class, new weka.associations.Apriori(), true));
    addPropertyType("Associate.Evaluation", PropertyType.OBJECT_EDITOR);
    setChooser("Associate.Evaluation", new GenericObjectEditorPanel(
      adams.gui.tools.wekainvestigator.tab.associatetab.evaluation.AbstractAssociatorEvaluation.class,
      new adams.gui.tools.wekainvestigator.tab.associatetab.evaluation.Train(), true));
    addPropertyType("Associate.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("Associate.OutputGenerators", PropertyType.ARRAY_EDITOR);
    setChooser("Associate.OutputGenerators", new GenericArrayEditorPanel(
      new adams.gui.tools.wekainvestigator.tab.associatetab.output.AbstractOutputGenerator[0]));
    setArrayClass("Associate.OutputGenerators",
      adams.gui.tools.wekainvestigator.tab.associatetab.output.AbstractOutputGenerator.class);
    setArraySeparator("Associate.OutputGenerators", " ");

    // classify
    addPropertyType("Classify.Classifier", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.Classifier", new GenericObjectEditorPanel(
      weka.classifiers.Classifier.class, new weka.classifiers.rules.ZeroR(), true));
    addPropertyType("Classify.Evaluation", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.Evaluation", new GenericObjectEditorPanel(
      adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.AbstractClassifierEvaluation.class,
      new adams.gui.tools.wekainvestigator.tab.classifytab.evaluation.CrossValidation(), true));
    addPropertyType("Classify.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("Classify.NumFolds", PropertyType.INTEGER);
    addPropertyType("Classify.PerFoldOutput", PropertyType.BOOLEAN);
    addPropertyType("Classify.CrossValidationFoldGenerator", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.CrossValidationFoldGenerator", new GenericObjectEditorPanel(
      weka.classifiers.CrossValidationFoldGenerator.class, new weka.classifiers.DefaultCrossValidationFoldGenerator(), true));
    addPropertyType("Classify.JobRunner", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.JobRunner", new GenericObjectEditorPanel(
      JobRunner.class, new LocalJobRunner(), true));
    addPropertyType("Classify.UseViews", PropertyType.BOOLEAN);
    addPropertyType("Classify.CrossValidationFinalModel", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.CrossValidationFinalModel", new GenericObjectEditorPanel(
      AbstractFinalModelGenerator.class, new Simple(), true));
    addPropertyType("Classify.Seed", PropertyType.INTEGER);
    addPropertyType("Classify.TrainPercentage", PropertyType.DOUBLE);
    addPropertyType("Classify.TrainTestSplitGenerator", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.TrainTestSplitGenerator", new GenericObjectEditorPanel(
      weka.classifiers.RandomSplitGenerator.class, new weka.classifiers.DefaultRandomSplitGenerator(), true));
    addPropertyType("Classify.PreserveOrder", PropertyType.BOOLEAN);
    addPropertyType("Classify.DiscardPredictions", PropertyType.BOOLEAN);
    addPropertyType("Classify.BuildModelPreserveOrder", PropertyType.BOOLEAN);
    addPropertyType("Classify.BuildModelSeed", PropertyType.INTEGER);
    addPropertyType("Classify.ModelDirectory", PropertyType.DIRECTORY_ABSOLUTE);
    addPropertyType("Classify.IndexedSplitsRunsFile", PropertyType.DIRECTORY_ABSOLUTE);
    addPropertyType("Classify.IndexedSplitsRunsReader", PropertyType.OBJECT_EDITOR);
    setChooser("Classify.IndexedSplitsRunsReader", new GenericObjectEditorPanel(
      AbstractIndexedSplitsRunsReader.class, new JsonIndexedSplitsRunsReader(), true));
    addPropertyType("Classify.IndexedSplitsRunsTrainSplitName", PropertyType.STRING);
    addPropertyType("Classify.IndexedSplitsRunsTestSplitName", PropertyType.STRING);
    addPropertyType("Classify.IndexedSplitsRunsLenient", PropertyType.BOOLEAN);
    addPropertyType("Classify.OutputGenerators", PropertyType.ARRAY_EDITOR);
    setChooser("Classify.OutputGenerators", new GenericArrayEditorPanel(
      new adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator[0]));
    setArrayClass("Classify.OutputGenerators",
      adams.gui.tools.wekainvestigator.tab.classifytab.output.AbstractOutputGenerator.class);
    setArraySeparator("Classify.OutputGenerators", " ");

    // clusterer
    addPropertyType("Cluster.Clusterer", PropertyType.OBJECT_EDITOR);
    setChooser("Cluster.Clusterer", new GenericObjectEditorPanel(
      weka.clusterers.Clusterer.class, new weka.clusterers.SimpleKMeans(), true));
    addPropertyType("Cluster.Evaluation", PropertyType.OBJECT_EDITOR);
    setChooser("Cluster.Evaluation", new GenericObjectEditorPanel(
      adams.gui.tools.wekainvestigator.tab.clustertab.evaluation.AbstractClustererEvaluation.class,
      new adams.gui.tools.wekainvestigator.tab.clustertab.evaluation.TrainTestSet(), true));
    addPropertyType("Cluster.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("Cluster.NumFolds", PropertyType.INTEGER);
    addPropertyType("Cluster.CrossValidationFinalModel", PropertyType.BOOLEAN);
    addPropertyType("Cluster.Seed", PropertyType.INTEGER);
    addPropertyType("Cluster.UseViews", PropertyType.BOOLEAN);
    addPropertyType("Cluster.TrainPercentage", PropertyType.DOUBLE);
    addPropertyType("Cluster.PreserveOrder", PropertyType.BOOLEAN);
    addPropertyType("Cluster.BuildModelPreserveOrder", PropertyType.BOOLEAN);
    addPropertyType("Cluster.BuildModelSeed", PropertyType.INTEGER);
    addPropertyType("Cluster.ModelDirectory", PropertyType.DIRECTORY_ABSOLUTE);
    addPropertyType("Cluster.OutputGenerators", PropertyType.ARRAY_EDITOR);
    setChooser("Cluster.OutputGenerators", new GenericArrayEditorPanel(
      new adams.gui.tools.wekainvestigator.tab.clustertab.output.AbstractOutputGenerator[0]));
    setArrayClass("Cluster.OutputGenerators",
      adams.gui.tools.wekainvestigator.tab.clustertab.output.AbstractOutputGenerator.class);
    setArraySeparator("Cluster.OutputGenerators", " ");

    // attribute selection
    addPropertyType("AttributeSelection.Evaluator", PropertyType.OBJECT_EDITOR);
    setChooser("AttributeSelection.Evaluator", new GenericObjectEditorPanel(
      weka.attributeSelection.ASEvaluation.class, new weka.attributeSelection.CfsSubsetEval(), true));
    addPropertyType("AttributeSelection.Search", PropertyType.OBJECT_EDITOR);
    setChooser("AttributeSelection.Search", new GenericObjectEditorPanel(
      weka.attributeSelection.ASSearch.class, new weka.attributeSelection.BestFirst(), true));
    addPropertyType("AttributeSelection.Evaluation", PropertyType.OBJECT_EDITOR);
    setChooser("AttributeSelection.Evaluation", new GenericObjectEditorPanel(
      adams.gui.tools.wekainvestigator.tab.attseltab.evaluation.AbstractAttributeSelectionEvaluation.class,
      new adams.gui.tools.wekainvestigator.tab.attseltab.evaluation.Train(), true));
    addPropertyType("AttributeSelection.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("AttributeSelection.NumFolds", PropertyType.INTEGER);
    addPropertyType("AttributeSelection.Seed", PropertyType.INTEGER);
    setChooser("AttributeSelection.OutputGenerators", new GenericArrayEditorPanel(
      new adams.gui.tools.wekainvestigator.tab.attseltab.output.AbstractOutputGenerator[0]));
    setArrayClass("AttributeSelection.OutputGenerators",
      adams.gui.tools.wekainvestigator.tab.attseltab.output.AbstractOutputGenerator.class);
    setArraySeparator("AttributeSelection.OutputGenerators", " ");

    // instance
    addPropertyType("Instance.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("Instance.AntiAliasing", PropertyType.BOOLEAN);
    addPropertyType("Instance.Markers", PropertyType.BOOLEAN);

    // ica
    addPropertyType("IndependentComponents.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("IndependentComponents.FastICA", PropertyType.OBJECT_EDITOR);
    setChooser("IndependentComponents.FastICA", new GenericObjectEditorPanel(
      com.github.waikatodatamining.matrix.algorithm.ica.FastICA.class, new com.github.waikatodatamining.matrix.algorithm.ica.FastICA(), false));

    // pca
    addPropertyType("PrincipalComponents.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("PrincipalComponents.Variance", PropertyType.DOUBLE);
    addPropertyType("PrincipalComponents.MaxAttributes", PropertyType.INTEGER);
    addPropertyType("PrincipalComponents.MaxAttributeNames", PropertyType.INTEGER);
    addPropertyType("PrincipalComponents.SkipNominal", PropertyType.BOOLEAN);

    // pls
    addPropertyType("PartialLeastSquares.LeftPanelWidth", PropertyType.INTEGER);
    addPropertyType("PartialLeastSquares.Algorithm", PropertyType.STRING);
    addPropertyType("PartialLeastSquares.NumComponents", PropertyType.INTEGER);

    setPreferences(Environment.getInstance().read(WekaInvestigatorDefinition.KEY));
  }
  
  /**
   * The title of the preferences.
   * 
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "WEKA Investigator";
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
    if (Environment.getInstance().write(WekaInvestigatorDefinition.KEY, getPreferences()))
      return null;
    else
      return "Failed to save Weka Investigator setup!";
  }

  /**
   * Returns whether the panel supports resetting the options.
   *
   * @return		true if supported
   */
  public boolean canReset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(WekaInvestigatorDefinition.KEY);
    return (props != null) && FileUtils.fileExists(props);
  }

  /**
   * Resets the settings to their default.
   *
   * @return		null if successfully reset, otherwise error message
   */
  public String reset() {
    String	props;

    props = Environment.getInstance().getCustomPropertiesFilename(WekaInvestigatorDefinition.KEY);
    if ((props != null) && FileUtils.fileExists(props)) {
      if (!FileUtils.delete(props))
	return "Failed to remove custom Weka Investigator properties: " + props;
    }

    return null;
  }
}
