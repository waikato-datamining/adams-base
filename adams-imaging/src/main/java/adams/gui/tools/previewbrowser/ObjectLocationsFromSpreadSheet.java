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
 * ObjectLocationsFromSpreadSheet.java
 * Copyright (C) 2019-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.image.ImageAnchor;
import adams.data.io.input.JAIImageReader;
import adams.data.io.input.ObjectLocationsSpreadSheetReader;
import adams.data.objectfinder.AllFinder;
import adams.data.objectfinder.ObjectFinder;
import adams.data.objectoverlap.AreaRatio;
import adams.data.objectoverlap.ObjectOverlap;
import adams.data.overlappingobjectremoval.AbstractOverlappingObjectRemoval;
import adams.data.overlappingobjectremoval.OverlappingObjectRemoval;
import adams.data.overlappingobjectremoval.PassThrough;
import adams.data.report.Report;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BasePanel;
import adams.gui.core.Fonts;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.TranslucentColorProvider;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ObjectLocationsOverlayFromReport;
import adams.gui.visualization.image.leftclick.ViewObjects;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Displays the following image types with an overlay for the objects stored in the spreadsheet with the same name (using the spreadsheet reader's default extension) or with the specified alternative file suffix to the name (eg '-rois'): tif,jpg,tiff,bmp,gif,png,jpeg,wbmp
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.ObjectLocationsSpreadSheetReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader setup to use for reading the object locations from the spreadsheet.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.ObjectLocationsSpreadSheetReader -reader \"adams.data.io.input.CsvSpreadSheetReader -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.DefaultSpreadSheet\" -row-finder adams.data.spreadsheet.rowfinder.AllFinder -col-left x0 -col-top y0 -col-right x1 -col-bottom y1 -col-type label_str
 * </pre>
 *
 * <pre>-alternative-file-suffix &lt;java.lang.String&gt; (property: alternativeFileSuffix)
 * &nbsp;&nbsp;&nbsp;The alternative file suffix to use for locating the associated spreadsheet
 * &nbsp;&nbsp;&nbsp;(eg '-rois').
 * &nbsp;&nbsp;&nbsp;default: -rois
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the objects.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 * <pre>-use-colors-per-type &lt;boolean&gt; (property: useColorsPerType)
 * &nbsp;&nbsp;&nbsp;If enabled, individual colors per type are used.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-type-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: typeColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use for the various types.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-type-suffix &lt;java.lang.String&gt; (property: typeSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix of fields in the report to identify the type.
 * &nbsp;&nbsp;&nbsp;default: .type
 * </pre>
 *
 * <pre>-type-regexp &lt;adams.core.base.BaseRegExp&gt; (property: typeRegExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the types must match in order to get drawn (
 * &nbsp;&nbsp;&nbsp;eg only plotting a subset).
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-filled &lt;boolean&gt; (property: filled)
 * &nbsp;&nbsp;&nbsp;If enabled, the shape is drawn filled.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-label-format &lt;java.lang.String&gt; (property: labelFormat)
 * &nbsp;&nbsp;&nbsp;The label format string to use for the rectangles; '#' for index, '&#64;' for
 * &nbsp;&nbsp;&nbsp;type and '$' for short type (type suffix must be defined for '&#64;' and '$'
 * &nbsp;&nbsp;&nbsp;), '{BLAH}' gets replaced with the value associated with the meta-data key
 * &nbsp;&nbsp;&nbsp;'BLAH'; for instance: '# &#64;' or '# {BLAH}'; in case of numeric values, use
 * &nbsp;&nbsp;&nbsp;'|.X' to limit the number of decimals, eg '{BLAH|.2}' for a maximum of decimals
 * &nbsp;&nbsp;&nbsp;after the decimal point.
 * &nbsp;&nbsp;&nbsp;default: #. $
 * </pre>
 *
 * <pre>-label-font &lt;java.awt.Font&gt; (property: labelFont)
 * &nbsp;&nbsp;&nbsp;The font to use for the labels.
 * &nbsp;&nbsp;&nbsp;default: Display-PLAIN-14
 * </pre>
 *
 * <pre>-label-anchor &lt;TOP_LEFT|TOP_CENTER|TOP_RIGHT|MIDDLE_LEFT|MIDDLE_CENTER|MIDDLE_RIGHT|BOTTOM_LEFT|BOTTOM_CENTER|BOTTOM_RIGHT&gt; (property: labelAnchor)
 * &nbsp;&nbsp;&nbsp;The anchor for the label.
 * &nbsp;&nbsp;&nbsp;default: TOP_RIGHT
 * </pre>
 *
 * <pre>-label-offset-x &lt;int&gt; (property: labelOffsetX)
 * &nbsp;&nbsp;&nbsp;The X offset for the label; values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses left as anchor, -2 the center and -3 the right.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-label-offset-y &lt;int&gt; (property: labelOffsetY)
 * &nbsp;&nbsp;&nbsp;The Y offset for the label values of 0 or greater are interpreted as absolute
 * &nbsp;&nbsp;&nbsp;pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 *
 * <pre>-predefined-labels &lt;adams.core.base.BaseString&gt; [-predefined-labels ...] (property: predefinedLabels)
 * &nbsp;&nbsp;&nbsp;The predefined labels to use for setting up the colors; avoids constants
 * &nbsp;&nbsp;&nbsp;changing in color pallet.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-vary-shape-color &lt;boolean&gt; (property: varyShapeColor)
 * &nbsp;&nbsp;&nbsp;If enabled, the shape colors get varied.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-shape-color-provider &lt;adams.gui.visualization.core.ColorProvider&gt; (property: shapeColorProvider)
 * &nbsp;&nbsp;&nbsp;The color provider to use when varying the shape colors.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.TranslucentColorProvider -provider adams.gui.visualization.core.DefaultColorProvider
 * </pre>
 *
 * <pre>-finder &lt;adams.data.objectfinder.ObjectFinder&gt; (property: finder)
 * &nbsp;&nbsp;&nbsp;The object finder to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectfinder.AllFinder
 * </pre>
 *
 * <pre>-overlap-detection &lt;adams.data.objectoverlap.ObjectOverlap&gt; (property: overlapDetection)
 * &nbsp;&nbsp;&nbsp;The algorithm to use for determining the overlapping objects.
 * &nbsp;&nbsp;&nbsp;default: adams.data.objectoverlap.AreaRatio
 * </pre>
 *
 * <pre>-overlap-removal &lt;adams.data.overlappingobjectremoval.OverlappingObjectRemoval&gt; (property: overlapRemoval)
 * &nbsp;&nbsp;&nbsp;The algorithm to use for removing the overlapping objects.
 * &nbsp;&nbsp;&nbsp;default: adams.data.overlappingobjectremoval.PassThrough
 * </pre>
 *
 * <pre>-use-alternative-location &lt;boolean&gt; (property: useAlternativeLocation)
 * &nbsp;&nbsp;&nbsp;If enabled, the alternative location is used to locate the associated report
 * &nbsp;&nbsp;&nbsp;rather than the directory with the image.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-alternative-location &lt;adams.core.io.PlaceholderDirectory&gt; (property: alternativeLocation)
 * &nbsp;&nbsp;&nbsp;The alternative location to use look for associated reports.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ObjectLocationsFromSpreadSheet
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -3962259305718630395L;

  /**
   * The panel for displaying the image.
   */
  public class CombinedPanel
    extends BasePanel {

    private static final long serialVersionUID = 236378741683380463L;

    /** the image panel. */
    protected ImagePanel m_PanelImage;

    /** whether to use an alternative location for reports. */
    protected BaseCheckBox m_CheckBoxAlternative;

    /** the alternative location. */
    protected DirectoryChooserPanel m_ChooserAlternative;

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      ObjectLocationsOverlayFromReport	overlay;
      JPanel panelBottom;

      super.initGUI();

      setLayout(new BorderLayout());

      m_PanelImage = new ImagePanel();
      overlay = new ObjectLocationsOverlayFromReport();
      overlay.setPrefix(m_Reader.getPrefix());
      overlay.setColor(m_Color);
      overlay.setUseColorsPerType(m_UseColorsPerType);
      overlay.setTypeColorProvider(m_TypeColorProvider.shallowCopy());
      overlay.setTypeSuffix(m_TypeSuffix);
      overlay.setTypeRegExp((BaseRegExp) m_TypeRegExp.getClone());
      overlay.setLabelFormat(m_LabelFormat);
      overlay.setLabelFont(m_LabelFont);
      overlay.setLabelFont(m_LabelFont);
      overlay.setLabelAnchor(m_LabelAnchor);
      overlay.setLabelOffsetX(m_LabelOffsetX);
      overlay.setLabelOffsetY(m_LabelOffsetY);
      overlay.setPredefinedLabels(m_PredefinedLabels);
      overlay.setFilled(m_Filled);
      overlay.setVaryShapeColor(m_VaryShapeColor);
      overlay.setShapeColorProvider(m_ShapeColorProvider.shallowCopy());
      overlay.setBoundingBoxFallbackRatio(m_BoundingBoxFallbackRatio);
      m_PanelImage.addImageOverlay(overlay);
      m_PanelImage.addLeftClickListener(new ViewObjects());

      add(m_PanelImage, BorderLayout.CENTER);

      panelBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(panelBottom, BorderLayout.SOUTH);

      m_CheckBoxAlternative = new BaseCheckBox("Alternative spreadsheet location");
      m_CheckBoxAlternative.addActionListener((ActionEvent e) -> toggleAlternative());
      panelBottom.add(m_CheckBoxAlternative);

      m_ChooserAlternative = new DirectoryChooserPanel();
      m_ChooserAlternative.setEnabled(false);
      m_ChooserAlternative.addChangeListener((ChangeEvent e) -> updateReport());
      panelBottom.add(m_ChooserAlternative);
    }

    /**
     * Updates the report display if possible.
     */
    protected void updateReport() {
      if (getImagePanel().getCurrentFile() != null)
        getImagePanel().setAdditionalProperties(loadReport(this, getImagePanel().getCurrentFile()));
    }

    /**
     * Toggles whether to use an alternative location.
     */
    protected void toggleAlternative() {
      m_ChooserAlternative.setEnabled(m_CheckBoxAlternative.isSelected());
      updateReport();
    }

    /**
     * Sets whether the alternative location should be used.
     *
     * @param value	true if to use
     */
    public void setUseAlternativeLocation(boolean value) {
      m_CheckBoxAlternative.setSelected(value);
      m_ChooserAlternative.setEnabled(value);
      updateReport();
    }

    /**
     * Returns whether the alternative location should be used.
     *
     * @return		true if to use
     */
    public boolean getUseAlternativeLocation() {
      return m_CheckBoxAlternative.isSelected();
    }

    /**
     * Sets the alternative location.
     *
     * @param value	the location
     */
    public void setAlternativeLocation(File value) {
      m_ChooserAlternative.setCurrent(value);
      updateReport();
    }

    /**
     * Returns the alternative location.
     *
     * @return		the location
     */
    public File getAlternativeLocation() {
      return m_ChooserAlternative.getCurrent();
    }

    /**
     * Returns the underlying image panel.
     *
     * @return		the panel
     */
    public ImagePanel getImagePanel() {
      return m_PanelImage;
    }
  }

  /** the reader to use. */
  protected ObjectLocationsSpreadSheetReader m_Reader;

  /** the alternative file suffix to use. */
  protected String m_AlternativeFileSuffix;

  /** the color for the objects. */
  protected Color m_Color;

  /** whether to use colors per type. */
  protected boolean m_UseColorsPerType;

  /** the color provider to use. */
  protected ColorProvider m_TypeColorProvider;

  /** the suffix for the type. */
  protected String m_TypeSuffix;

  /** the regular expression for the types to draw. */
  protected BaseRegExp m_TypeRegExp;

  /** whether to draw the shape filled. */
  protected boolean m_Filled;

  /** the label for the rectangles. */
  protected String m_LabelFormat;

  /** the label font. */
  protected Font m_LabelFont;

  /** the label anchor. */
  protected ImageAnchor m_LabelAnchor;

  /** the x offset for the label. */
  protected int m_LabelOffsetX;

  /** the y offset for the label. */
  protected int m_LabelOffsetY;

  /** the predefined labels. */
  protected BaseString[] m_PredefinedLabels;

  /** whether to vary the shape color. */
  protected boolean m_VaryShapeColor;

  /** the color provider to use when varying the shape colors. */
  protected ColorProvider m_ShapeColorProvider;

  /** the object finder to use. */
  protected ObjectFinder m_Finder;

  /** the ratio used for determining whether to fall back from polygon on bbox. */
  protected double m_BoundingBoxFallbackRatio;

  /** the object overlap calculation to use. */
  protected ObjectOverlap m_OverlapDetection;

  /** the object removal algorithm. */
  protected OverlappingObjectRemoval m_OverlapRemoval;

  /** whether to use an alternative location for the reports. */
  protected boolean m_UseAlternativeLocation;

  /** the alternative location. */
  protected PlaceholderDirectory m_AlternativeLocation;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Displays the following image types with an overlay for the objects "
	+ "stored in the spreadsheet with the same name (using the spreadsheet "
	+ "reader's default extension) or with the specified alternative file "
        + "suffix to the name (eg '-rois'): "
	+ Utils.arrayToString(getExtensions());
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      getDefaultReader());

    m_OptionManager.add(
      "alternative-file-suffix", "alternativeFileSuffix",
      "-rois");

    m_OptionManager.add(
      "color", "color",
      Color.RED);

    m_OptionManager.add(
      "use-colors-per-type", "useColorsPerType",
      true);

    m_OptionManager.add(
      "type-color-provider", "typeColorProvider",
      new DefaultColorProvider());

    m_OptionManager.add(
      "type-suffix", "typeSuffix",
      ".type");

    m_OptionManager.add(
      "type-regexp", "typeRegExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "filled", "filled",
      false);

    m_OptionManager.add(
      "label-format", "labelFormat",
      "#. $");

    m_OptionManager.add(
      "label-font", "labelFont",
      Fonts.getSansFont(14));

    m_OptionManager.add(
      "label-anchor", "labelAnchor",
      getDefaultLabelAnchor());

    m_OptionManager.add(
      "label-offset-x", "labelOffsetX",
      getDefaultLabelOffsetX());

    m_OptionManager.add(
      "label-offset-y", "labelOffsetY",
      getDefaultLabelOffsetY());

    m_OptionManager.add(
      "predefined-labels", "predefinedLabels",
      new BaseString[0]);

    m_OptionManager.add(
      "vary-shape-color", "varyShapeColor",
      false);

    m_OptionManager.add(
      "shape-color-provider", "shapeColorProvider",
      new TranslucentColorProvider());

    m_OptionManager.add(
      "finder", "finder",
      new AllFinder());

    m_OptionManager.add(
      "bounding-box-fallback-ratio", "boundingBoxFallbackRatio",
      0.0, 0.0, 1.0);

    m_OptionManager.add(
      "overlap-detection", "overlapDetection",
      new AreaRatio());

    m_OptionManager.add(
      "overlap-removal", "overlapRemoval",
      new PassThrough());

    m_OptionManager.add(
      "use-alternative-location", "useAlternativeLocation",
      false);

    m_OptionManager.add(
      "alternative-location", "alternativeLocation",
      new PlaceholderDirectory());
  }

  /**
   * Returns the default reader.
   *
   * @return		the reader
   */
  protected ObjectLocationsSpreadSheetReader getDefaultReader() {
    ObjectLocationsSpreadSheetReader  result;

    result = new ObjectLocationsSpreadSheetReader();
    result.setColLeft(new SpreadSheetColumnIndex("x0"));
    result.setColTop(new SpreadSheetColumnIndex("y0"));
    result.setColRight(new SpreadSheetColumnIndex("x1"));
    result.setColBottom(new SpreadSheetColumnIndex("y1"));
    result.setColType(new SpreadSheetColumnIndex("label_str"));

    return result;
  }

  /**
   * Sets the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @param value 	the reader
   */
  public void setReader(ObjectLocationsSpreadSheetReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader setup to use for reading the object locations from the spreadsheet.
   *
   * @return 		the reader
   */
  public ObjectLocationsSpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader setup to use for reading the object locations from the spreadsheet.";
  }

  /**
   * Sets the alternative file suffix to use for locating the associated spreadsheet (eg '-rois').
   *
   * @param value 	the suffix
   */
  public void setAlternativeFileSuffix(String value) {
    m_AlternativeFileSuffix = value;
    reset();
  }

  /**
   * Returns the alternative file suffix to use for locating the associated spreadsheet (eg '-rois').
   *
   * @return 		the suffix
   */
  public String getAlternativeFileSuffix() {
    return m_AlternativeFileSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alternativeFileSuffixTipText() {
    return "The alternative file suffix to use for locating the associated spreadsheet (eg '-rois').";
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Color = value;
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color to use for the objects.";
  }

  /**
   * Sets whether to use colors per type.
   *
   * @param value 	true if to use colors per type
   */
  public void setUseColorsPerType(boolean value) {
    m_UseColorsPerType = value;
    reset();
  }

  /**
   * Returns whether to use colors per type.
   *
   * @return 		true if to use colors per type
   */
  public boolean getUseColorsPerType() {
    return m_UseColorsPerType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColorsPerTypeTipText() {
    return "If enabled, individual colors per type are used.";
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setTypeColorProvider(ColorProvider value) {
    m_TypeColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use for the types.
   *
   * @return 		the provider
   */
  public ColorProvider getTypeColorProvider() {
    return m_TypeColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColorProviderTipText() {
    return "The color provider to use for the various types.";
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_TypeSuffix = value;
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_TypeSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return "The suffix of fields in the report to identify the type.";
  }

  /**
   * Sets the regular expression that the types must match in order to get
   * drawn.
   *
   * @param value 	the expression
   */
  public void setTypeRegExp(BaseRegExp value) {
    m_TypeRegExp = value;
    reset();
  }

  /**
   * Returns the regular expression that the types must match in order to get
   * drawn.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeRegExp() {
    return m_TypeRegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeRegExpTipText() {
    return "The regular expression that the types must match in order to get drawn (eg only plotting a subset).";
  }

  /**
   * Sets whether to draw the shape filled.
   *
   * @param value 	true if to fill
   */
  public void setFilled(boolean value) {
    m_Filled = value;
    reset();
  }

  /**
   * Returns whether to draw the shape filled.
   *
   * @return 		true if to fill
   */
  public boolean getFilled() {
    return m_Filled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filledTipText() {
    return "If enabled, the shape is drawn filled.";
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
    return "The label format string to use for the rectangles; "
      + "'#' for index, '@' for type and '$' for short type (type suffix "
      + "must be defined for '@' and '$'), '{BLAH}' gets replaced with the "
      + "value associated with the meta-data key 'BLAH'; "
      + "for instance: '# @' or '# {BLAH}'; in case of numeric values, use '|.X' "
      + "to limit the number of decimals, eg '{BLAH|.2}' for a maximum of decimals "
      + "after the decimal point.";
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
    return "The font to use for the labels.";
  }

  /**
   * Returns the default label anchor.
   *
   * @return		the default
   */
  protected ImageAnchor getDefaultLabelAnchor() {
    return ImageAnchor.TOP_RIGHT;
  }

  /**
   * Sets the anchor for the label.
   *
   * @param value 	the anchor
   */
  public void setLabelAnchor(ImageAnchor value) {
    m_LabelAnchor = value;
    reset();
  }

  /**
   * Returns the anchor for the label.
   *
   * @return 		the anchor
   */
  public ImageAnchor getLabelAnchor() {
    return m_LabelAnchor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelAnchorTipText() {
    return "The anchor for the label.";
  }

  /**
   * Returns the default label offset for X.
   *
   * @return		the default
   */
  protected int getDefaultLabelOffsetX() {
    return 0;
  }

  /**
   * Sets the X offset for the label.
   *
   * @param value 	the X offset
   */
  public void setLabelOffsetX(int value) {
    m_LabelOffsetX = value;
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getLabelOffsetX() {
    return m_LabelOffsetX;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetXTipText() {
    return "The X offset for the label; values of 0 or greater are interpreted as absolute pixels, -1 uses left as anchor, -2 the center and -3 the right.";
  }

  /**
   * Returns the default label offset for Y.
   *
   * @return		the default
   */
  protected int getDefaultLabelOffsetY() {
    return 0;
  }

  /**
   * Sets the Y offset for the label.
   *
   * @param value 	the Y offset
   */
  public void setLabelOffsetY(int value) {
    m_LabelOffsetY = value;
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getLabelOffsetY() {
    return m_LabelOffsetY;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetYTipText() {
    return "The Y offset for the label values of 0 or greater are interpreted as absolute pixels, -1 uses top as anchor, -2 the middle and -3 the bottom.";
  }

  /**
   * Sets the predefined labels.
   *
   * @param value	the labels
   */
  public void setPredefinedLabels(BaseString[] value) {
    m_PredefinedLabels = value;
    reset();
  }

  /**
   * Returns the predefined labels.
   *
   * @return		the labels
   */
  public BaseString[] getPredefinedLabels() {
    return m_PredefinedLabels;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predefinedLabelsTipText() {
    return "The predefined labels to use for setting up the colors; avoids constants changing in color pallet.";
  }

  /**
   * Sets whether to vary the colors of the shapes.
   *
   * @param value 	true if to vary
   */
  public void setVaryShapeColor(boolean value) {
    m_VaryShapeColor = value;
    reset();
  }

  /**
   * Returns whether to vary the colors of the shapes.
   *
   * @return 		true if to vary
   */
  public boolean getVaryShapeColor() {
    return m_VaryShapeColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String varyShapeColorTipText() {
    return "If enabled, the shape colors get varied.";
  }

  /**
   * Sets the color provider to use when varying the shape colors.
   *
   * @param value 	the provider
   */
  public void setShapeColorProvider(ColorProvider value) {
    m_ShapeColorProvider = value;
    reset();
  }

  /**
   * Returns the color provider to use when varying the shape colors.
   *
   * @return 		the provider
   */
  public ColorProvider getShapeColorProvider() {
    return m_ShapeColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shapeColorProviderTipText() {
    return "The color provider to use when varying the shape colors.";
  }

  /**
   * Sets the finder to use for locating the objects.
   *
   * @param value	the finder
   */
  public void setFinder(ObjectFinder value) {
    m_Finder = value;
    reset();
  }

  /**
   * Returns the finder to use for locating the objects.
   *
   * @return		the finder
   */
  public ObjectFinder getFinder() {
    return m_Finder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finderTipText() {
    return "The object finder to use.";
  }

  /**
   * Sets the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @param value 	the ratio
   */
  public void setBoundingBoxFallbackRatio(double value) {
    if (getOptionManager().isValid("boundingBoxFallbackRatio", value)) {
      m_BoundingBoxFallbackRatio = value;
      reset();
    }
  }

  /**
   * Returns the ratio between shape area over bbox area. If below the bbox is used
   * instead of the polygon.
   *
   * @return 		the ratio
   */
  public double getBoundingBoxFallbackRatio() {
    return m_BoundingBoxFallbackRatio;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String boundingBoxFallbackRatioTipText() {
    return "The threshold for the ratio between the areas (shape / bbox), below which the bounding box is used over the polygon (ie bad masks/shapes).";
  }

  /**
   * Sets the algorithm for determining the overlapping objects
   *
   * @param value 	the algorithm
   */
  public void setOverlapDetection(ObjectOverlap value) {
    m_OverlapDetection = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public ObjectOverlap getOverlapDetection() {
    return m_OverlapDetection;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapDetectionTipText() {
    return "The algorithm to use for determining the overlapping objects.";
  }

  /**
   * Sets the algorithm for determining the overlapping objects
   *
   * @param value 	the algorithm
   */
  public void setOverlapRemoval(OverlappingObjectRemoval value) {
    m_OverlapRemoval = value;
    reset();
  }

  /**
   * Returns the algorithm for determining the overlapping objects.
   *
   * @return 		the algorithm
   */
  public OverlappingObjectRemoval getOverlapRemoval() {
    return m_OverlapRemoval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String overlapRemovalTipText() {
    return "The algorithm to use for removing the overlapping objects.";
  }

  /**
   * Sets whether to use an alternative location for the reports.
   *
   * @param value 	true if to use
   */
  public void setUseAlternativeLocation(boolean value) {
    m_UseAlternativeLocation = value;
    reset();
  }

  /**
   * Returns whether to use an alternative location for the reports.
   *
   * @return 		true if to use
   */
  public boolean getUseAlternativeLocation() {
    return m_UseAlternativeLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useAlternativeLocationTipText() {
    return "If enabled, the alternative location is used to locate the associated report rather than the directory with the image.";
  }

  /**
   * Sets the alternative location to use for the reports.
   *
   * @param value 	the location
   */
  public void setAlternativeLocation(PlaceholderDirectory value) {
    m_AlternativeLocation = value;
    reset();
  }

  /**
   * Returns the alternative location to use for the reports.
   *
   * @return 		the location
   */
  public PlaceholderDirectory getAlternativeLocation() {
    return m_AlternativeLocation;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alternativeLocationTipText() {
    return "The alternative location to use look for associated reports.";
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new JAIImageReader().getFormatExtensions();
  }

  /**
   * Filters the objects in the report, if necessary.
   *
   * @param report	the report to filter
   * @return		the filtered report (copy, in case filtering occurred)
   */
  protected Report filterReport(Report report) {
    Report		result;
    LocatedObjects	objs;

    if ((m_Finder instanceof AllFinder) && (m_OverlapRemoval instanceof PassThrough))
      return report;

    if (m_OverlapRemoval instanceof PassThrough) {
      objs   = m_Finder.findObjects(report);
      result = report.getClone();
      result.removeValuesStartingWith(m_Finder.getPrefix());
      result.mergeWith(objs.toReport(m_Finder.getPrefix()));
    }
    else {
      try {
        result = AbstractOverlappingObjectRemoval.remove(report, report, m_Finder, m_OverlapDetection, m_OverlapRemoval);
      }
      catch (Exception e) {
        getLogger().log(Level.SEVERE, "Failed to remove objects!", e);
        result = report;
      }
    }

    return result;
  }

  /**
   * Loads the report associated with the image.
   *
   * @param panel 	the context panel
   * @param file	the image file
   * @return		the report, null if failed to load report data or none available
   */
  protected Report loadReport(CombinedPanel panel, File file) {
    Report 		result;
    File		baseFile;
    File 		locFile;
    File 		locFile2;
    List<Report> 	reports;

    result   = null;
    if (panel.getUseAlternativeLocation())
      baseFile = new PlaceholderFile(panel.getAlternativeLocation().getAbsolutePath() + File.separator + file.getName());
    else
      baseFile = file;
    locFile = FileUtils.replaceExtension(baseFile, "." + m_Reader.getReader().getDefaultFormatExtension());
    locFile2 = FileUtils.replaceExtension(baseFile, m_AlternativeFileSuffix + "." + m_Reader.getReader().getDefaultFormatExtension());
    if (locFile2.exists() && locFile2.isFile())
      locFile = locFile2;
    if (locFile.exists() && locFile.isFile()) {
      m_Reader.setInput(new PlaceholderFile(locFile));
      reports = m_Reader.read();
      if (reports.size() > 0)
        result = filterReport(reports.get(0));
    }

    return result;
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    CombinedPanel 	panel;
    Report		report;

    panel  = new CombinedPanel();
    panel.setAlternativeLocation(m_AlternativeLocation);
    panel.setUseAlternativeLocation(m_UseAlternativeLocation);
    report = loadReport(panel, file);
    panel.getImagePanel().load(file, new JAIImageReader(), -1.0);
    panel.getImagePanel().setAdditionalProperties(report);

    return new PreviewPanel(panel, panel.getImagePanel().getPaintPanel());
  }

  /**
   * Reuses the last preview, if possible.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel reusePreview(File file, PreviewPanel previewPanel) {
    CombinedPanel 	panel;
    Report		report;

    panel  = (CombinedPanel) previewPanel.getComponent();
    report = loadReport(panel, file);
    panel.getImagePanel().load(file, new JAIImageReader(), panel.getImagePanel().getScale());
    panel.getImagePanel().setAdditionalProperties(report);

    return previewPanel;
  }
}
