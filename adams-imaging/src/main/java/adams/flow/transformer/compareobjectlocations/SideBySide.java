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
 * SideBySide.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.compareobjectlocations;

import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import adams.flow.transformer.CompareObjectLocations;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.BaseSplitPane;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.leftclick.ViewObjects;

import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Generates a side-by-side comparison.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SideBySide
  extends AbstractComparison {

  private static final long serialVersionUID = 1595413312168655696L;

  /**
   * Panel for displaying the annotations/predictions side-by-side.
   */
  public static class SideBySidePanel
    extends AbstractComparisonPanel {

    private static final long serialVersionUID = -4668850944632082746L;

    /** the split pane. */
    protected BaseSplitPane m_SplitPane;

    /** the image panel with the annotations. */
    protected ImagePanel m_PanelImageAnnotations;

    /** the image panel with the predictions. */
    protected ImagePanel m_PanelImagePredictions;

    /** the annotations report. */
    protected Report m_AnnotationsReport;

    /** the located objects / annotations. */
    protected LocatedObjects m_AnnotationsLocatedObjects;

    /** the annotations label suffix. */
    protected String m_AnnotationsLabelSuffix;

    /** the predictions report. */
    protected Report m_PredictionsReport;

    /** the located objects / predictions. */
    protected LocatedObjects m_PredictionsLocatedObjects;

    /** the predictions label suffix. */
    protected String m_PredictionsLabelSuffix;

    /** the zoom level. */
    protected double m_Zoom;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_AnnotationsReport         = new Report();
      m_AnnotationsLocatedObjects = new LocatedObjects();
      m_PredictionsReport         = new Report();
      m_PredictionsLocatedObjects = new LocatedObjects();
    }

    /**
     * For initializing the GUI.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
      m_SplitPane.setResizeWeight(0.5);
      add(m_SplitPane, BorderLayout.CENTER);

      m_PanelImageAnnotations = new ImagePanel();
      m_PanelImageAnnotations.setBorder(BorderFactory.createTitledBorder("Annotations"));
      m_PanelImageAnnotations.addLeftClickListener(new ViewObjects());
      m_SplitPane.setLeftComponent(m_PanelImageAnnotations);

      m_PanelImagePredictions = new ImagePanel();
      m_PanelImagePredictions.setBorder(BorderFactory.createTitledBorder("Predictions"));
      m_PanelImagePredictions.addLeftClickListener(new ViewObjects());
      m_SplitPane.setRightComponent(m_PanelImagePredictions);
    }

    /**
     * Sets the zoom level in percent (0-1600).
     *
     * @param value 	the zoom, -1 to fit window, or 0-1600
     */
    public void setZoom(double value) {
      m_Zoom = value;
    }

    /**
     * Sets the overlay to use for the annotations.
     *
     * @param value 	the overlay
     */
    public void setAnnotationsOverlay(ImageOverlay value) {
      m_PanelImageAnnotations.addImageOverlay((ImageOverlay) OptionUtils.shallowCopy(value));
    }

    /**
     * Sets the report suffix that the annotations use for storing the label.
     *
     * @param value 	the suffix
     */
    public void setAnnotationsLabelSuffix(String value) {
      m_AnnotationsLabelSuffix = value;
    }

    /**
     * Sets the overlay to use for the predictions.
     *
     * @param value 	the overlay
     */
    public void setPredictionsOverlay(ImageOverlay value) {
      m_PanelImagePredictions.addImageOverlay((ImageOverlay) OptionUtils.shallowCopy(value));
    }

    /**
     * Sets the report suffix that the predictions use for storing the label.
     *
     * @param value 	the suffix
     */
    public void setPredictionsLabelSuffix(String value) {
      m_PredictionsLabelSuffix = value;
    }

    /**
     * Filters the objects using the specified label and updates the GUI.
     *
     * @param label	the label to restrict display to, empty/null for all
     */
    @Override
    protected void filterObjects(String label) {
      m_PanelImageAnnotations.setAdditionalProperties(filterObjects(label, m_AnnotationsLocatedObjects, m_AnnotationsLabelSuffix, m_AnnotationsPrefix));
      m_PanelImagePredictions.setAdditionalProperties(filterObjects(label, m_PredictionsLocatedObjects, m_PredictionsLabelSuffix, m_PredictionsPrefix));
    }

    /**
     * Clears the content of the panel.
     */
    @Override
    public void clearPanel() {
      m_PanelImageAnnotations.clear();
      m_PanelImagePredictions.clear();
    }

    /**
     * Displays the new image.
     *
     * @param cont	the image to display
     * @param labels 	the object labels
     * @param repAnn	the report with the annotations (ground truth)
     * @param objAnn	the object locations (ground truth from report)
     * @param repPred	the report with the predictions
     * @param objPred	the object locations (predictions)
     */
    @Override
    public void display(AbstractImageContainer cont, List<String> labels, Report repAnn, LocatedObjects objAnn, Report repPred, LocatedObjects objPred) {
      double		zoom;

      if (m_Zoom == -1)
	zoom = m_Zoom;
      else
	zoom = m_Zoom / 100.0;

      m_AnnotationsReport         = repAnn;
      m_AnnotationsLocatedObjects = objAnn;
      m_PanelImageAnnotations.setCurrentImage(cont);
      m_PanelImageAnnotations.setAdditionalProperties(m_AnnotationsReport);
      m_PanelImageAnnotations.setScale(zoom);

      m_PredictionsReport         = repPred;
      m_PredictionsLocatedObjects = objPred;
      m_PanelImagePredictions.setCurrentImage(cont);
      m_PanelImagePredictions.setAdditionalProperties(m_PredictionsReport);
      m_PanelImagePredictions.setScale(zoom);

      updateButtons(labels);
    }
  }

  /** the image overlays for the annotations. */
  protected ImageOverlay m_AnnotationsOverlay;

  /** the image overlays for the predictions. */
  protected ImageOverlay m_PredictionsOverlay;

  /** the zoom level. */
  protected double m_Zoom;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a side-by-side comparison.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "annotations-overlay", "annotationsOverlay",
      new ObjectLocationsOverlayFromReport());

    m_OptionManager.add(
      "predictions-overlay", "predictionsOverlay",
      new ObjectLocationsOverlayFromReport());

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, -1.0, 1600.0);
  }

  /**
   * Sets the overlay to use for the annotations.
   *
   * @param value 	the overlay
   */
  public void setAnnotationsOverlay(ImageOverlay value) {
    m_AnnotationsOverlay = value;
    reset();
  }

  /**
   * Returns the overlay to use for the annotations.
   *
   * @return 		the overlay
   */
  public ImageOverlay getAnnotationsOverlay() {
    return m_AnnotationsOverlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsOverlayTipText() {
    return "The overlay to apply to the annotations.";
  }

  /**
   * Sets the overlay to use for the predictions.
   *
   * @param value 	the overlay
   */
  public void setPredictionsOverlay(ImageOverlay value) {
    m_PredictionsOverlay = value;
    reset();
  }

  /**
   * Returns the overlay to use for the predictions.
   *
   * @return 		the overlay
   */
  public ImageOverlay getPredictionsOverlay() {
    return m_PredictionsOverlay;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsOverlayTipText() {
    return "The overlay to apply to the predictions.";
  }

  /**
   * Sets the zoom level in percent (0-1600).
   *
   * @param value 	the zoom, -1 to fit window, or 0-1600
   */
  public void setZoom(double value) {
    if ((value == -1) || ((value > 0) && (value <= 1600))) {
      m_Zoom = value;
      reset();
    }
    else {
      getLogger().warning("Zoom must -1 to fit window or 0 < x < 1600, provided: " + value);
    }
  }

  /**
   * Returns the zoom level in percent.
   *
   * @return 		the zoom
   */
  public double getZoom() {
    return m_Zoom;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String zoomTipText() {
    return "The zoom level in percent.";
  }

  /**
   * Generates the comparison panel.
   *
   * @param owner 	the owning panel
   * @return 		the panel
   */
  @Override
  public AbstractComparisonPanel generate(CompareObjectLocations owner) {
    SideBySidePanel	result;

    result = new SideBySidePanel();
    result.setZoom(m_Zoom);

    result.setAnnotationsOverlay(m_AnnotationsOverlay);
    result.setAnnotationsPrefix(owner.getAnnotationsPrefix());
    result.setAnnotationsLabelSuffix(owner.getAnnotationsLabelSuffix());

    result.setPredictionsOverlay(m_PredictionsOverlay);
    result.setPredictionsPrefix(owner.getPredictionsPrefix());
    result.setPredictionsLabelSuffix(owner.getPredictionsLabelSuffix());

    return result;
  }
}
