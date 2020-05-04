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
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.compareobjectlocations;

import adams.core.base.BaseString;
import adams.core.option.OptionUtils;
import adams.data.image.AbstractImageContainer;
import adams.data.objectoverlap.LabelAwareObjectOverlap;
import adams.data.objectoverlap.Null;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.report.Report;
import adams.flow.transformer.CompareObjectLocations;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.ColorHelper;
import adams.gui.core.Fonts;
import adams.gui.visualization.image.ImageOverlay;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.MultiImageOverlay;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.ReportObjectOverlay;
import adams.gui.visualization.image.leftclick.ViewObjects;
import com.github.fracpete.javautils.struct.Struct2;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
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

    public static final String PREFIX_OVERLAP_CORRECT = "OverlapCorrect.";

    public static final String PREFIX_OVERLAP_INCORRECT = "OverlapIncorrect.";

    /** the image panel. */
    protected ImagePanel m_PanelImage;

    /** the panel with the colors. */
    protected JPanel m_PanelColors;

    /** the label for the annotation color. */
    protected JLabel m_LabelAnnotationsColorBox;

    /** the label for the prediction color. */
    protected JLabel m_LabelPredictionsColorBox;

    /** the label for the overlap color text (matching labels). */
    protected JLabel m_LabelOverlapColorCorrectText;

    /** the label for the overlap color (matching labels). */
    protected JLabel m_LabelOverlapColorCorrectBox;

    /** the label for the overlap color text (mismatching labels). */
    protected JLabel m_LabelOverlapColorIncorrectText;

    /** the label for the overlap color (mismatching labels). */
    protected JLabel m_LabelOverlapColorIncorrectBox;

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

    /** the algorithm for calculating the overlapping objects. */
    protected ObjectOverlap m_ObjectOverlap;

    /** the located objects / overlaps (matching labels). */
    protected LocatedObjects m_OverlapLocatedObjectsCorrect;

    /** the located objects / overlaps (mismatching labels). */
    protected LocatedObjects m_OverlapLocatedObjectsIncorrect;

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
      ViewObjects	viewObjects;

      super.initGUI();

      m_PanelImage = new ImagePanel();
      add(m_PanelImage, BorderLayout.CENTER);

      m_PanelColors = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(m_PanelColors, BorderLayout.SOUTH);

      m_LabelAnnotationsColorBox = new JLabel("         ");
      m_LabelAnnotationsColorBox.setOpaque(true);
      m_LabelPredictionsColorBox = new JLabel("         ");
      m_LabelPredictionsColorBox.setOpaque(true);
      m_LabelOverlapColorCorrectText = new JLabel("Overlaps (match)");
      m_LabelOverlapColorCorrectBox = new JLabel("         ");
      m_LabelOverlapColorCorrectBox.setOpaque(true);
      m_LabelOverlapColorIncorrectText = new JLabel("Overlaps (mismatch)");
      m_LabelOverlapColorIncorrectBox = new JLabel("         ");
      m_LabelOverlapColorIncorrectBox.setOpaque(true);

      m_PanelColors.add(new JLabel("Annotations"));
      m_PanelColors.add(m_LabelAnnotationsColorBox);
      m_PanelColors.add(new JLabel("Predictions"));
      m_PanelColors.add(m_LabelPredictionsColorBox);
      m_PanelColors.add(m_LabelOverlapColorCorrectText);
      m_PanelColors.add(m_LabelOverlapColorCorrectBox);
      m_PanelColors.add(m_LabelOverlapColorIncorrectText);
      m_PanelColors.add(m_LabelOverlapColorIncorrectBox);

      viewObjects = new ViewObjects();
      viewObjects.setPrefixes(new BaseString[]{
        new BaseString(PREFIX_ANNOTATION),
        new BaseString(PREFIX_PREDICTION),
      });
      m_PanelImage.addLeftClickListener(viewObjects);
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
     * Sets the color to use for the annotations.
     *
     * @param value 	the color
     */
    public void setAnnotationsColor(Color value) {
      m_LabelAnnotationsColorBox.setBackground(value);
    }

    /**
     * Sets the color to use for the predictions.
     *
     * @param value 	the color
     */
    public void setPredictionsColor(Color value) {
      m_LabelPredictionsColorBox.setBackground(value);
    }

    /**
     * Sets the algorithm to use for determining the overlapping object.
     *
     * @param value 	the algorithm
     */
    public void setObjectOverlap(ObjectOverlap value) {
      m_ObjectOverlap = value;
      if (m_ObjectOverlap instanceof Null) {
        m_LabelOverlapColorCorrectText.setVisible(false);
        m_LabelOverlapColorCorrectBox.setVisible(false);
      }
    }

    /**
     * Sets the color to use for the overlaps (matching labels).
     *
     * @param value 	the color
     */
    public void setOverlapColorCorrect(Color value) {
      m_LabelOverlapColorCorrectBox.setBackground(value);
    }

    /**
     * Sets the color to use for the overlaps (mismatching labels).
     *
     * @param value 	the color
     */
    public void setOverlapColorIncorrect(Color value) {
      m_LabelOverlapColorIncorrectBox.setBackground(value);
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
      combined.mergeWith(filterObjects(label, m_AnnotationsLocatedObjects,      SUFFIX_TYPE, PREFIX_ANNOTATION));
      combined.mergeWith(filterObjects(label, m_PredictionsLocatedObjects,      SUFFIX_TYPE, PREFIX_PREDICTION));
      combined.mergeWith(filterObjects(label, m_OverlapLocatedObjectsCorrect,   SUFFIX_TYPE, PREFIX_OVERLAP_CORRECT));
      combined.mergeWith(filterObjects(label, m_OverlapLocatedObjectsIncorrect, SUFFIX_TYPE, PREFIX_OVERLAP_INCORRECT));
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
      double					zoom;
      LocatedObjects				overlaps;
      Struct2<LocatedObjects,LocatedObjects>	split;

      if (m_Zoom == -1)
	zoom = m_Zoom;
      else
	zoom = m_Zoom / 100.0;

      m_AnnotationsReport         = repAnn;
      m_AnnotationsLocatedObjects = updateLabelType(objAnn, m_AnnotationsLabelSuffix);

      m_PredictionsReport         = repPred;
      m_PredictionsLocatedObjects = updateLabelType(objPred, m_PredictionsLabelSuffix);

      if (m_ObjectOverlap instanceof Null) {
	m_OverlapLocatedObjectsCorrect   = new LocatedObjects();
	m_OverlapLocatedObjectsIncorrect = new LocatedObjects();
      }
      else {
        overlaps = m_ObjectOverlap.calculate(m_AnnotationsLocatedObjects, m_PredictionsLocatedObjects);
	if (m_ObjectOverlap instanceof LabelAwareObjectOverlap) {
	  split = ((LabelAwareObjectOverlap) m_ObjectOverlap).splitOverlaps(overlaps);
	  m_OverlapLocatedObjectsCorrect   = split.value1;
	  m_OverlapLocatedObjectsIncorrect = split.value2;
	}
	else {
	  m_OverlapLocatedObjectsCorrect   = overlaps;
	  m_OverlapLocatedObjectsIncorrect = new LocatedObjects();
	}
      }

      m_CombinedReport = new Report();
      m_CombinedReport.mergeWith(m_AnnotationsLocatedObjects.toReport(PREFIX_ANNOTATION));
      m_CombinedReport.mergeWith(m_PredictionsLocatedObjects.toReport(PREFIX_PREDICTION));
      m_CombinedReport.mergeWith(m_OverlapLocatedObjectsCorrect.toReport(PREFIX_OVERLAP_CORRECT));
      m_CombinedReport.mergeWith(m_OverlapLocatedObjectsIncorrect.toReport(PREFIX_OVERLAP_INCORRECT));

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

  /** the algorithm for calculating the overlapping objects. */
  protected ObjectOverlap m_ObjectOverlap;

  /** the color for the correct overlaps. */
  protected Color m_OverlapColorCorrect;

  /** the color for the incorrect overlaps. */
  protected Color m_OverlapColorIncorrect;

  /** the label format. */
  protected String m_LabelFormat;

  /** the label font. */
  protected Font m_LabelFont;

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
      "object-overlap", "objectOverlap",
      new Null());

    m_OptionManager.add(
      "overlap-color-correct", "overlapColorCorrect",
      ColorHelper.addAlpha(Color.GREEN, 64));

    m_OptionManager.add(
      "overlap-color-incorrect", "overlapColorIncorrect",
      ColorHelper.addAlpha(ColorHelper.invert(Color.GREEN), 64));

    m_OptionManager.add(
      "label-format", "labelFormat",
      "#");

    m_OptionManager.add(
      "label-font", "labelFont",
      Fonts.getSansFont(14));

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
   * Sets the algorithm to use for determining overlapping objects.
   *
   * @param value 	the algorithm
   */
  public void setObjectOverlap(ObjectOverlap value) {
    m_ObjectOverlap = value;
    reset();
  }

  /**
   * Returns the algorithm to use for determining overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getObjectOverlap() {
    return m_ObjectOverlap;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String objectOverlapTipText() {
    return "The algorithm to use for determining overlapping objects.";
  }

  /**
   * Sets the color to use for the overlapping objects with matching labels.
   *
   * @param value 	the color
   */
  public void setOverlapColorCorrect(Color value) {
    m_OverlapColorCorrect = value;
    reset();
  }

  /**
   * Returns the color to use for the overlapping objects with matching labels.
   *
   * @return 		the color
   */
  public Color getOverlapColorCorrect() {
    return m_OverlapColorCorrect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapColorCorrectTipText() {
    return "The color to use for the overlapping objects with matching labels.";
  }

  /**
   * Sets the color to use for the overlapping objects with matching labels.
   *
   * @param value 	the color
   */
  public void setOverlapColorIncorrect(Color value) {
    m_OverlapColorIncorrect = value;
    reset();
  }

  /**
   * Returns the color to use for the overlapping objects with matching labels.
   *
   * @return 		the color
   */
  public Color getOverlapColorIncorrect() {
    return m_OverlapColorIncorrect;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapColorIncorrectTipText() {
    return "The color to use for the overlapping objects with matching labels.";
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setLabelFormat(String value) {
    m_LabelFormat = value;
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getLabelFormat() {
    return m_LabelFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFormatTipText() {
    return new ReportObjectOverlay().labelFormatTipText();
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setLabelFont(Font value) {
    m_LabelFont = value;
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_LabelFont;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return new ReportObjectOverlay().labelFontTipText();
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
    ObjectLocationsOverlayFromReport 	overlapsCorrect;
    ObjectLocationsOverlayFromReport 	overlapsIncorrect;
    MultiImageOverlay			multi;

    annotations = new ObjectLocationsOverlayFromReport();
    annotations.setColor(m_AnnotationsColor);
    annotations.setPrefix(CombinedPanel.PREFIX_ANNOTATION);
    annotations.setLabelFont(m_LabelFont);
    annotations.setLabelFormat(m_LabelFormat);

    predictions = new ObjectLocationsOverlayFromReport();
    predictions.setColor(m_PredictionsColor);
    predictions.setPrefix(CombinedPanel.PREFIX_PREDICTION);
    predictions.setLabelFont(m_LabelFont);
    predictions.setLabelFormat(m_LabelFormat);

    overlapsCorrect = new ObjectLocationsOverlayFromReport();
    overlapsCorrect.setColor(m_OverlapColorCorrect);
    overlapsCorrect.setFilled(true);
    overlapsCorrect.setPrefix(CombinedPanel.PREFIX_OVERLAP_CORRECT);
    overlapsCorrect.setLabelFont(m_LabelFont);
    overlapsCorrect.setLabelFormat(m_LabelFormat);

    overlapsIncorrect = new ObjectLocationsOverlayFromReport();
    overlapsIncorrect.setColor(m_OverlapColorIncorrect);
    overlapsIncorrect.setFilled(true);
    overlapsIncorrect.setPrefix(CombinedPanel.PREFIX_OVERLAP_INCORRECT);
    overlapsIncorrect.setLabelFont(m_LabelFont);
    overlapsIncorrect.setLabelFormat(m_LabelFormat);

    multi = new MultiImageOverlay();
    multi.setOverlays(new ImageOverlay[]{annotations, predictions, overlapsCorrect, overlapsIncorrect});

    result = new CombinedPanel();
    result.setOverlay(multi);
    result.setAnnotationsColor(m_AnnotationsColor);
    result.setPredictionsColor(m_PredictionsColor);
    result.setObjectOverlap((ObjectOverlap) OptionUtils.shallowCopy(m_ObjectOverlap, true));
    result.setOverlapColorCorrect(m_OverlapColorCorrect);
    result.setOverlapColorIncorrect(m_OverlapColorIncorrect);
    result.setZoom(m_Zoom);

    return result;
  }
}
