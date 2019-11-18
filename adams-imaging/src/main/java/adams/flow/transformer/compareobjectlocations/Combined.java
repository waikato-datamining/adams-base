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
 * Combined.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.compareobjectlocations;

import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.report.Report;
import adams.flow.transformer.CompareObjectLocations;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.MultiImageOverlay;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

/**
 * Displays the annotations and predictions with different colors.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Combined
  extends AbstractComparison {

  private static final long serialVersionUID = -6214679316931952392L;

  /**
   * Displays the annotations/predictions in a single .
   */
  public static class CombinedPanel
    extends AbstractComparisonPanel {

    private static final long serialVersionUID = 385817392712324238L;

    public final static String SUFFIX_TYPE = "type";

    public static final String PREFIX_ANNOTATION = "Annotation.";

    public static final String PREFIX_PREDICTION = "Prediction.";

    /** the image panel. */
    protected ImagePanel m_PanelImage;

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

    /** the combined report. */
    protected Report m_CombinedReport;

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_CombinedReport = new Report();
    }

    /**
     * For initializing the GUI.
     */
    @Override
    protected void initGUI() {
      super.initGUI();

      m_PanelImage = new ImagePanel();
      add(m_PanelImage, BorderLayout.CENTER);
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
     * Sets the overlay to use for the objects.
     *
     * @param value 	the overlay
     */
    public void setOverlay(ImageOverlay value) {
      m_PanelImage.addImageOverlay((ImageOverlay) OptionUtils.shallowCopy(value));
    }

    /**
     * Clears the content of the panel.
     */
    @Override
    public void clearPanel() {
      if (m_PanelImage != null)
	m_PanelImage.clear();
    }

    /**
     * Filters the objects using the specified label and updates the GUI.
     *
     * @param label	the label to restrict display to, empty/null for all
     */
    @Override
    protected void filterObjects(String label) {
      Report		combined;

      combined = new Report();
      combined.mergeWith(filterObjects(label, m_AnnotationsLocatedObjects, SUFFIX_TYPE, PREFIX_ANNOTATION));
      combined.mergeWith(filterObjects(label, m_PredictionsLocatedObjects, SUFFIX_TYPE, PREFIX_PREDICTION));
      m_PanelImage.setAdditionalProperties(combined);
    }

    /**
     * Updates the label type.
     *
     * @param objects	the objects to update
     * @param suffix	the label suffix used
     * @return		the new objects using {@link #SUFFIX_TYPE} for the label
     */
    protected LocatedObjects updateLabelType(LocatedObjects objects, String suffix) {
      LocatedObjects 	result;

      result = new LocatedObjects();
      for (LocatedObject obj: objects)
	result.add(obj.getClone());
      result.renameMetaDataKey(suffix, SUFFIX_TYPE);

      return result;
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
      m_AnnotationsLocatedObjects = updateLabelType(objAnn, m_AnnotationsLabelSuffix);

      m_PredictionsReport         = repPred;
      m_PredictionsLocatedObjects = updateLabelType(objPred, m_PredictionsLabelSuffix);

      m_CombinedReport = new Report();
      m_CombinedReport.mergeWith(m_AnnotationsLocatedObjects.toReport(PREFIX_ANNOTATION));
      m_CombinedReport.mergeWith(m_PredictionsLocatedObjects.toReport(PREFIX_PREDICTION));

      m_PanelImage.setCurrentImage(cont);
      m_PanelImage.setAdditionalProperties(m_CombinedReport);
      m_PanelImage.setScale(zoom);

      updateButtons(labels);
    }
  }

  /** the color for the annotations. */
  protected Color m_AnnotationsColor;

  /** the color for the predictions. */
  protected Color m_PredictionsColor;

  /** the zoom level. */
  protected double m_Zoom;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the annotations and predictions with different colors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "annotations-color", "annotationsColor",
      Color.BLUE);

    m_OptionManager.add(
      "predictions-color", "predictionsColor",
      Color.RED);

    m_OptionManager.add(
      "zoom", "zoom",
      100.0, -1.0, 1600.0);
  }

  /**
   * Sets the color to use for the annotations.
   *
   * @param value 	the color
   */
  public void setAnnotationsColor(Color value) {
    m_AnnotationsColor = value;
    reset();
  }

  /**
   * Returns the color to use for the annotations.
   *
   * @return 		the color
   */
  public Color getAnnotationsColor() {
    return m_AnnotationsColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String annotationsColorTipText() {
    return "The color to use for the annotations.";
  }

  /**
   * Sets the color to use for the predictions.
   *
   * @param value 	the color
   */
  public void setPredictionsColor(Color value) {
    m_PredictionsColor = value;
    reset();
  }

  /**
   * Returns the color to use for the predictions.
   *
   * @return 		the color
   */
  public Color getPredictionsColor() {
    return m_PredictionsColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predictionsColorTipText() {
    return "The color to use for the predictions.";
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
    CombinedPanel			result;
    ObjectLocationsOverlayFromReport	annotations;
    ObjectLocationsOverlayFromReport	predictions;
    MultiImageOverlay			multi;

    annotations = new ObjectLocationsOverlayFromReport();
    annotations.setColor(m_AnnotationsColor);
    annotations.setPrefix(CombinedPanel.PREFIX_ANNOTATION);

    predictions = new ObjectLocationsOverlayFromReport();
    predictions.setColor(m_PredictionsColor);
    predictions.setPrefix(CombinedPanel.PREFIX_PREDICTION);

    multi = new MultiImageOverlay();
    multi.setOverlays(new ImageOverlay[]{annotations, predictions});

    result = new CombinedPanel();
    result.setZoom(m_Zoom);
    result.setOverlay(multi);

    return result;
  }
}
