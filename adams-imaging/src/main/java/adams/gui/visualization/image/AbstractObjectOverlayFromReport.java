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
 * ObjectCentersOverlayFromReport.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image;

import adams.core.base.BaseRegExp;
import adams.core.base.BaseString;
import adams.data.image.ImageAnchor;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.PopupMenuCustomizer;
import adams.gui.visualization.core.ColorProvider;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for overlays that use object locations from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractObjectOverlayFromReport
  extends AbstractImageOverlay
  implements PopupMenuCustomizer<PaintPanel>, TypeColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6356419097401574024L;

  /** the default prefix. */
  public final static String PREFIX_DEFAULT = ReportObjectOverlay.PREFIX_DEFAULT;

  /** the overlay handler. */
  protected ReportObjectOverlay m_Overlays;

  /** the listeners for locations updates. */
  protected Set<ChangeListener> m_LocationsUpdatedListeners;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"prefix", "prefix",
	PREFIX_DEFAULT);

    m_OptionManager.add(
	"color", "color",
	Color.RED);

    m_OptionManager.add(
	"use-colors-per-type", "useColorsPerType",
	false);

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
	"label-format", "labelFormat",
	"#");

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
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Overlays                  = new ReportObjectOverlay();
    m_LocationsUpdatedListeners = new HashSet<>();
  }

  /**
   * Sets the prefix to use for the objects in the report.
   *
   * @param value 	the prefix
   */
  public void setPrefix(String value) {
    m_Overlays.setPrefix(value);
    reset();
  }

  /**
   * Returns the prefix to use for the objects in the report.
   *
   * @return 		the prefix
   */
  public String getPrefix() {
    return m_Overlays.getPrefix();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return m_Overlays.prefixTipText();
  }

  /**
   * Sets the color to use for the objects.
   *
   * @param value 	the color
   */
  public void setColor(Color value) {
    m_Overlays.setColor(value);
    reset();
  }

  /**
   * Returns the color to use for the objects.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_Overlays.getColor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return m_Overlays.colorTipText();
  }

  /**
   * Sets whether to use colors per type.
   *
   * @param value 	true if to use colors per type
   */
  public void setUseColorsPerType(boolean value) {
    m_Overlays.setUseColorsPerType(value);
    reset();
  }

  /**
   * Returns whether to use colors per type.
   *
   * @return 		true if to use colors per type
   */
  public boolean getUseColorsPerType() {
    return m_Overlays.getUseColorsPerType();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useColorsPerTypeTipText() {
    return m_Overlays.useColorsPerTypeTipText();
  }

  /**
   * Sets the color provider to use for the types.
   *
   * @param value 	the provider
   */
  public void setTypeColorProvider(ColorProvider value) {
    m_Overlays.setTypeColorProvider(value);
    reset();
  }

  /**
   * Returns the color provider to use for the types.
   *
   * @return 		the provider
   */
  public ColorProvider getTypeColorProvider() {
    return m_Overlays.getTypeColorProvider();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeColorProviderTipText() {
    return m_Overlays.typeColorProviderTipText();
  }

  /**
   * Sets the suffix to use for the types.
   *
   * @param value 	the suffix
   */
  public void setTypeSuffix(String value) {
    m_Overlays.setTypeSuffix(value);
    reset();
  }

  /**
   * Returns the suffix to use for the types.
   *
   * @return 		the suffix
   */
  public String getTypeSuffix() {
    return m_Overlays.getTypeSuffix();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeSuffixTipText() {
    return m_Overlays.typeSuffixTipText();
  }

  /**
   * Sets the regular expression that the types must match in order to get
   * drawn.
   *
   * @param value 	the expression
   */
  public void setTypeRegExp(BaseRegExp value) {
    m_Overlays.setTypeRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression that the types must match in order to get
   * drawn.
   *
   * @return 		the expression
   */
  public BaseRegExp getTypeRegExp() {
    return m_Overlays.getTypeRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeRegExpTipText() {
    return m_Overlays.typeRegExpTipText();
  }

  /**
   * Sets the label format.
   *
   * @param value 	the label format
   */
  public void setLabelFormat(String value) {
    m_Overlays.setLabelFormat(value);
    reset();
  }

  /**
   * Returns the label format.
   *
   * @return 		the label format
   */
  public String getLabelFormat() {
    return m_Overlays.getLabelFormat();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFormatTipText() {
    return m_Overlays.labelFormatTipText();
  }

  /**
   * Sets the label font.
   *
   * @param value 	the label font
   */
  public void setLabelFont(Font value) {
    m_Overlays.setLabelFont(value);
    reset();
  }

  /**
   * Returns the label font.
   *
   * @return 		the label font
   */
  public Font getLabelFont() {
    return m_Overlays.getLabelFont();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelFontTipText() {
    return m_Overlays.labelFontTipText();
  }

  /**
   * Sets the anchor for the label.
   *
   * @param value 	the anchor
   */
  public void setLabelAnchor(ImageAnchor value) {
    m_Overlays.setLabelAnchor(value);
    reset();
  }

  /**
   * Returns the default label anchor.
   *
   * @return		the anchor
   */
  protected ImageAnchor getDefaultLabelAnchor() {
    return ImageAnchor.TOP_RIGHT;
  }

  /**
   * Returns the anchor for the label.
   *
   * @return 		the anchor
   */
  public ImageAnchor getLabelAnchor() {
    return m_Overlays.getLabelAnchor();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelAnchorTipText() {
    return m_Overlays.labelAnchorTipText();
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
    m_Overlays.setLabelOffsetX(value);
    reset();
  }

  /**
   * Returns the X offset for the label.
   *
   * @return 		the X offset
   */
  public int getLabelOffsetX() {
    return m_Overlays.getLabelOffsetX();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetXTipText() {
    return m_Overlays.labelOffsetXTipText();
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
    m_Overlays.setLabelOffsetY(value);
    reset();
  }

  /**
   * Returns the Y offset for the label.
   *
   * @return 		the Y offset
   */
  public int getLabelOffsetY() {
    return m_Overlays.getLabelOffsetY();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String labelOffsetYTipText() {
    return m_Overlays.labelOffsetYTipText();
  }

  /**
   * Sets the predefined labels.
   *
   * @param value	the labels
   */
  public void setPredefinedLabels(BaseString[] value) {
    m_Overlays.setPredefinedLabels(value);
    reset();
  }

  /**
   * Returns the predefined labels.
   *
   * @return		the labels
   */
  public BaseString[] getPredefinedLabels() {
    return m_Overlays.getPredefinedLabels();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String predefinedLabelsTipText() {
    return m_Overlays.predefinedLabelsTipText();
  }

  /**
   * Checks whether a color has been stored for the given object type.
   *
   * @param type	the type to check
   * @return		true if custom color available
   */
  @Override
  public boolean hasTypeColor(String type) {
    return m_Overlays.hasTypeColor(type);
  }

  /**
   * Returns the color for the object type.
   *
   * @param type	the type to get the color for
   * @return		the color, null if none available
   */
  @Override
  public Color getTypeColor(String type) {
    return m_Overlays.getTypeColor(type);
  }

  /**
   * Adds the listener for location updates.
   *
   * @param l		the listener to add
   */
  public void addLocationsUpdatedListeners(ChangeListener l) {
    m_LocationsUpdatedListeners.add(l);
  }

  /**
   * Removes the listener for location updates.
   *
   * @param l		the listener to remove
   */
  public void removeLocationsUpdatedListeners(ChangeListener l) {
    m_LocationsUpdatedListeners.remove(l);
  }

  /**
   * Notifies all the listeners that the notifications have been updated.
   */
  protected void notifyLocationsUpdatedListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_LocationsUpdatedListeners)
      l.stateChanged(e);
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected synchronized void doImageChanged(PaintPanel panel) {
    m_Overlays.reset();
  }

  /**
   * Calculates the string dimensions in pixels.
   *
   * @param g		the graphics context
   * @param f		the font to use
   * @param s		the string to measure
   * @return		the dimensions in pixels
   */
  protected Dimension calcStringDimenions(Graphics g, Font f, String s) {
    FontMetrics metrics;

    metrics = g.getFontMetrics(f);
    return new Dimension(metrics.stringWidth(s), metrics.getHeight());
  }

  /**
   * Draws the string at the specified position.
   *
   * @param g		the graphics context
   * @param rect	the bounding box
   * @param label	the label to draw
   */
  protected void drawString(Graphics g, Rectangle rect, String label) {
    int		offsetX;
    int		offsetY;
    Dimension	dims;

    if (label.isEmpty())
      return;

    offsetX = getLabelOffsetX();
    offsetY = getLabelOffsetY();
    dims    = calcStringDimenions(g, getLabelFont(), label);

    switch (offsetX) {
      case -1:
        offsetX = 0;
        break;
      case -2:
        offsetX = -dims.width / 2;
        break;
      case -3:
        offsetX = -dims.width;
        break;
      default:
	if (offsetX < 0)
	  offsetX = 0;
    }

    switch (offsetY) {
      case -1:
        offsetY = 0;
        break;
      case -2:
        offsetY = dims.height / 2;
        break;
      case -3:
        offsetY = dims.height;
        break;
      default:
	if (offsetY < 0)
	  offsetY = 0;
    }

    switch (getLabelAnchor()) {
      case TOP_LEFT:
	g.drawString(
	  label,
	  (int) (rect.getX() + offsetX),
	  (int) (rect.getY() + offsetY));
	break;
      case TOP_CENTER:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width / 2 - dims.width / 2 + offsetX),
	  (int) (rect.getY() + offsetY));
	break;
      case TOP_RIGHT:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width + offsetX),
	  (int) (rect.getY() + offsetY));
	break;
      case MIDDLE_LEFT:
	g.drawString(
	  label,
	  (int) (rect.getX() + offsetX),
	  (int) (rect.getY() + rect.height / 2 - dims.height / 2 + offsetY));
	break;
      case MIDDLE_CENTER:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width / 2 - dims.width / 2 + offsetX),
	  (int) (rect.getY() + rect.height / 2 - dims.height / 2 + offsetY));
	break;
      case MIDDLE_RIGHT:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width + offsetX),
	  (int) (rect.getY() + rect.height / 2 - dims.height / 2 + offsetY));
	break;
      case BOTTOM_LEFT:
	g.drawString(
	  label,
	  (int) (rect.getX() + offsetX),
	  (int) (rect.getY() + rect.height + offsetY));
	break;
      case BOTTOM_CENTER:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width / 2 - dims.width / 2 + offsetX),
	  (int) (rect.getY() + rect.height + offsetY));
	break;
      case BOTTOM_RIGHT:
	g.drawString(
	  label,
	  (int) (rect.getX() + rect.width + offsetX),
	  (int) (rect.getY() + rect.height + offsetY));
	break;
      default:
        throw new IllegalStateException("Unhandled label anchor: " + getLabelAnchor());
    }
  }

  /**
   * Performs the actual painting of the objects.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   * @param locations	the locations to paint
   */
  protected abstract void doPaintObjects(PaintPanel panel, Graphics g, List<Polygon> locations);

  /**
   * Performs the actual painting of the overlay.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected synchronized void doPaintOverlay(PaintPanel panel, Graphics g) {
    boolean	updated;

    updated = m_Overlays.determineLocations(panel.getOwner().getAdditionalProperties());
    if (m_Overlays.hasLocations())
      doPaintObjects(panel, g, m_Overlays.getLocations());
    if (updated)
      notifyLocationsUpdatedListeners();
  }

  /**
   * For customizing the popup menu.
   *
   * @param source	the source, e.g., event
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(PaintPanel source, JPopupMenu menu) {
    JMenuItem		menuitem;

    if (!getTypeSuffix().isEmpty()) {
      menuitem = new JMenuItem("Displayed types", GUIHelper.getIcon("objecttypes.gif"));
      menuitem.addActionListener((ActionEvent e) -> {
        String type = GUIHelper.showInputDialog(source, "Regular expression for type", getTypeRegExp().getValue());
        if (type == null)
          return;
        if (!getTypeRegExp().isValid(type)) {
          GUIHelper.showErrorMessage(source, "Invalid regular expression: " + type);
          return;
	}
	setTypeRegExp(new BaseRegExp(type));
        source.update();
      });
      menu.add(menuitem);
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_LocationsUpdatedListeners != null)
      m_LocationsUpdatedListeners.clear();
    super.cleanUp();
  }
}
