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
 * AxisPanel.java
 * Copyright (C) 2008-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import adams.core.Utils;
import adams.core.base.BaseInterval;
import adams.core.logging.LoggingHelper;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.ParameterPanel;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.visualization.core.axis.AbstractAxisModel;
import adams.gui.visualization.core.axis.DefaultAxisModel;
import adams.gui.visualization.core.axis.Direction;
import adams.gui.visualization.core.axis.FlippableAxisModel;
import adams.gui.visualization.core.axis.Orientation;
import adams.gui.visualization.core.axis.Tick;
import adams.gui.visualization.core.axis.TickGenerator;
import adams.gui.visualization.core.axis.Type;
import adams.gui.visualization.core.axis.Visibility;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * Specialized panel for displaying an axis.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class AxisPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = 5811111621680835988L;

  /** the panel itself. */
  protected AxisPanel m_Self;

  /** the direction of the axis. */
  protected Direction m_Direction;

  /** the orientation of the axis. */
  protected Orientation m_Orientation;

  /** the type of axis. */
  protected Type m_Type;

  /** the axis color. */
  protected Color m_AxisColor;

  /** the width of the axis (for HORIZONTAL axes, this is the height, of course). */
  protected int m_AxisWidth;

  /** the name of the axis. */
  protected String m_AxisName;

  /** the font for the axis name. */
  protected Font m_AxisNameFont;

  /** whether the axis name is centered in the axis and not at the border. */
  protected boolean m_AxisNameCentered;

  /** the length of the ticks. */
  protected int m_LengthTicks;

  /** an optional customizer for the right-click popup. */
  protected PopupMenuCustomizer m_PopupMenuCustomizer;

  /** the paint listeners. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** the axis model. */
  protected AbstractAxisModel m_Model;

  /** for overriding the default number formats. */
  protected Hashtable<Type,String> m_NumberFormatOverride;

  /** whether to show the coordinate lines for this axis. */
  protected boolean m_ShowGridLines;

  /** the visibility state of the axis. */
  protected Visibility m_Visibility;

  /**
   * Initializes the axis, with color black and 10 ticks, width 20.
   *
   * @param direction	the direction of the axis
   * @param orientation	the orientation of the axis
   * @param type	the type of axis
   */
  public AxisPanel(Direction direction, Orientation orientation, Type type) {
    this(direction, orientation, type, 10);
  }

  /**
   * Initializes the axis, with color black, width 20.
   *
   * @param direction	the direction of the axis
   * @param orientation	the orientation of the axis
   * @param type	the type of axis
   * @param ticks	the number of ticks on the axis
   */
  public AxisPanel(Direction direction, Orientation orientation, Type type, int ticks) {
    super();

    m_Self                   = this;
    m_Direction              = direction;
    m_Orientation            = orientation;
    m_AxisColor              = Color.BLACK;
    m_AxisWidth              = 20;
    m_AxisName               = null;
    m_AxisNameFont           = new Font("Monospaced", Font.BOLD, 10);
    m_AxisNameCentered       = false;
    m_LengthTicks            = 6;
    m_PopupMenuCustomizer    = null;
    m_ChangeListeners        = new HashSet<>();
    m_NumberFormatOverride   = new Hashtable<>();
    m_ShowGridLines          = false;
    m_Visibility             = Visibility.VISIBLE;

    m_Model = new DefaultAxisModel();
    m_Model.setParent(this);

    setToolTipText("Right-click for menu");
    setType(type);
    setMinimum(0.0);
    setMaximum(1.0);

    addMouseListener(new MouseAdapter() {
      // popup menu
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  BasePopupMenu menu = getPopupMenu(e);
	  menu.showAbsolute(m_Self, e);
	}
      }
    });

    calculateDimensions();
    repaint();
  }

  /**
   * Obtains all the settings from the other panel.
   * Does not re-use the {@link PopupMenuCustomizer}.
   *
   * @param other	the panel to get the settings from
   */
  public void assign(AxisPanel other) {
    m_Direction              = other.getDirection();
    m_Orientation            = other.getOrientation();
    m_AxisColor              = other.getAxisColor();
    m_AxisWidth              = other.getAxisWidth();
    m_AxisName               = other.getAxisName();
    m_AxisNameFont           = other.getAxisNameFont();
    m_AxisNameCentered       = other.m_AxisNameCentered;
    m_LengthTicks            = other.getLengthTicks();
    m_PopupMenuCustomizer    = null;
    m_ShowGridLines          = other.getShowGridLines();
    m_Visibility             = other.getVisibility();
    try {
      m_Model = other.getAxisModel().getClass().getDeclaredConstructor().newInstance();
    }
    catch (Exception e) {
      System.err.println("Failed to create instance of axis model:");
      e.printStackTrace();
      m_Model = new DefaultAxisModel();
    }
    m_Model.assign(other.getAxisModel());
    m_Model.setParent(this);

    setToolTipText(other.getToolTipText());
    setType(other.getType());
    setMinimum(other.getMinimum());
    setMaximum(other.getMaximum());

    calculateDimensions();
    repaint();
  }

  /**
   * Transforms the given value into the display (absolute, percentage, log,
   * etc.) format.
   *
   * @param value	the value to transform
   * @return		the display value
   */
  public String valueToDisplay(double value) {
    return m_Model.valueToDisplay(value);
  }

  /**
   * calculates and sets the preferred size of the axis.
   */
  protected void calculateDimensions() {
    Dimension	size;

    if (m_Direction == Direction.VERTICAL)
      size = new Dimension(getActualAxisWidth(), Integer.MAX_VALUE);
    else
      size = new Dimension(Integer.MAX_VALUE, getActualAxisWidth());

    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);

    if (getParent() != null)
      getParent().repaint();
  }

  /**
   * Sets the direction of the axis.
   *
   * @param value	the direction
   */
  public void setDirection(Direction value) {
    m_Direction = value;
    calculateDimensions();
  }

  /**
   * Returns the direction of the axis.
   *
   * @return		the direction
   */
  public Direction getDirection() {
    return m_Direction;
  }

  /**
   * Sets the orientation of the axis.
   *
   * @param value	the orientation
   */
  public void setOrientation(Orientation value) {
    m_Orientation = value;
    repaint();
  }

  /**
   * Returns the orientation of the axis.
   *
   * @return		the orientation
   */
  public Orientation getOrientation() {
    return m_Orientation;
  }

  /**
   * Sets the type of axis.
   *
   * @param value	the type
   */
  public void setType(Type value) {
    AbstractAxisModel	oldModel;

    m_Type   = value;
    oldModel = m_Model;
    m_Model  = m_Type.getModel();
    m_Model.assign(oldModel);
    if (hasNumberFormatOverride(m_Type))
      m_Model.setNumberFormat(getNumberFormatOverride(m_Type));

    notifyChangeListeners();
  }

  /**
   * Returns the type of axis.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Sets the color of the axis.
   *
   * @param value	the color to use
   */
  public void setAxisColor(Color value) {
    m_AxisColor = value;
    repaint();
  }

  /**
   * Returns the color of the axis.
   *
   * @return		the current color
   */
  public Color getAxisColor() {
    return m_AxisColor;
  }

  /**
   * Sets tick generator to use.
   *
   * @param value	the tick generator
   */
  public void setTickGenerator(TickGenerator value) {
    m_Model.setTickGenerator(value);
  }

  /**
   * Returns the current tick generator in use.
   *
   * @return		the tick generator
   */
  public TickGenerator getTickGenerator() {
    return m_Model.getTickGenerator();
  }

  /**
   * Sets the count of ticks a value is shown, i.e., "3" means every third tick:
   * 1, 4, 7, ...
   *
   * @param value	the count
   */
  public void setNthValueToShow(int value) {
    m_Model.setNthValueToShow(value);
  }

  /**
   * Returns the count of ticks a value is shown, i.e., "3" means every third
   * tick: 1, 4, 7, ...
   *
   * @return		the count
   */
  public int getNthValueToShow() {
    return m_Model.getNthValueToShow();
  }

  /**
   * Sets the length of ticks to display along the axis (at least 4).
   *
   * @param value	the length of ticks
   */
  public void setLengthTicks(int value) {
    if (value >= 4) {
      m_LengthTicks = value;
      repaint();
    }
    else {
      System.err.println(
	"Ticks must be at least 4 pixels long (provided: " + value + ")!");
    }
  }

  /**
   * Returns the length of ticks currently displayed.
   *
   * @return		the legnth of ticks
   */
  public int getLengthTicks() {
    return m_LengthTicks;
  }

  /**
   * Sets the width of the axis (this is height for HORIZONTAL axes, of
   * course), at least 5 pixel.
   *
   * @param value	the new width
   */
  public void setAxisWidth(int value) {
    if (value >= 5) {
      m_AxisWidth = value;
      calculateDimensions();
    }
    else {
      System.err.println(
	"Axis width must be at least 5 pixels (provided: " + value + ")!");
    }
  }

  /**
   * Returns the current width of the axis.
   *
   * @return		the width
   */
  public int getAxisWidth() {
    return m_AxisWidth;
  }

  /**
   * Returns the actual axis width, depending on the visibility.
   *
   * @return		the axis width if visible, otherwise 0
   */
  public int getActualAxisWidth() {
    if (m_Visibility == Visibility.VISIBLE)
      return m_AxisWidth;
    else
      return 0;
  }

  /**
   * Sets the name of the axis, null or empty string for none.
   *
   * @param value	the name of the axis
   */
  public void setAxisName(String value) {
    m_AxisName = value;
    repaint();
  }

  /**
   * Returns the name of the axis, null or empty string if none set.
   *
   * @return		the name of the axis or null
   */
  public String getAxisName() {
    return m_AxisName;
  }

  /**
   * Sets the font to use for the axis name.
   *
   * @param value	the font to use
   */
  public void setAxisNameFont(Font value) {
    m_AxisNameFont = value;
  }

  /**
   * Returns the font being used for the axis name.
   *
   * @return		the font in use
   */
  public Font getAxisNameFont() {
    return m_AxisNameFont;
  }

  /**
   * Sets whether the axis name is centered in the axis or at the border.
   *
   * @param value	if true then the name is centered
   */
  public void setAxisNameCentered(boolean value) {
    m_AxisNameCentered = value;
  }

  /**
   * Returns whether the axis name is centered in the axis or at the border.
   *
   * @return		true if centered
   */
  public boolean isAxisNameCentered() {
    return m_AxisNameCentered;
  }

  /**
   * Sets the minimum to display on the axis.
   *
   * @param value	the minimum value
   */
  public void setMinimum(double value) {
    m_Model.setMinimum(value);
  }

  /**
   * Returns the currently set minimum on the axis.
   *
   * @return		the minimum value
   */
  public double getMinimum() {
    return m_Model.getMinimum();
  }

  /**
   * Sets the manual minimum to display on the axis.
   *
   * @param value	the minimum value, null to unset
   */
  public void setManualMinimum(Double value) {
    m_Model.setManualMinimum(value);
  }

  /**
   * Returns the currently set manual minimum on the axis.
   *
   * @return		the minimum value, null if none set
   */
  public Double getManualMinimum() {
    return m_Model.getManualMinimum();
  }

  /**
   * Returns the actual minimum on the axis (incl zoom/panning).
   *
   * @return		the actual minimum value
   */
  public double getActualMinimum() {
    return m_Model.getActualMinimum();
  }

  /**
   * Returns the actual minimum on the axis (excl margins).
   *
   * @return		the actual minimum value
   */
  public double getActualMinimumNoMargin() {
    return m_Model.getActualMinimumNoMargin();
  }

  /**
   * Sets the maximum to display on the axis.
   *
   * @param value	the maximum value
   */
  public void setMaximum(double value) {
    m_Model.setMaximum(value);
  }

  /**
   * Returns the currently set maximum on the axis.
   *
   * @return		the minimum value
   */
  public double getMaximum() {
    return m_Model.getMaximum();
  }

  /**
   * Sets the manual maximum to display on the axis.
   *
   * @param value	the maximum value, null to unset
   */
  public void setManualMaximum(Double value) {
    m_Model.setManualMaximum(value);
  }

  /**
   * Returns the currently set manual maximum on the axis.
   *
   * @return		the manual maximum value, null if none set
   */
  public Double getManualMaximum() {
    return m_Model.getManualMaximum();
  }

  /**
   * Returns the actual maximum on the axis (incl zoom/panning).
   *
   * @return		the actual maximum value
   */
  public double getActualMaximum() {
    return m_Model.getActualMaximum();
  }

  /**
   * Returns the actual maximum on the axis (excl margins).
   *
   * @return		the actual maximum value
   */
  public double getActualMaximumNoMargin() {
    return m_Model.getActualMaximumNoMargin();
  }

  /**
   * Sets the top margin factor (>= 0.0).
   *
   * @param value	the top margin
   */
  public void setTopMargin(double value) {
    m_Model.setTopMargin(value);
  }

  /**
   * Returns the currently set top margin factor (>= 0.0).
   *
   * @return		the top margin
   */
  public double getTopMargin() {
    return m_Model.getTopMargin();
  }

  /**
   * Sets the bottom margin factor (>= 0.0).
   *
   * @param value	the bottom margin
   */
  public void setBottomMargin(double value) {
    m_Model.setBottomMargin(value);
  }

  /**
   * Returns the currently set bottom margin factor (>= 0.0).
   *
   * @return		the bottom margin
   */
  public double getBottomMargin() {
    return m_Model.getBottomMargin();
  }

  /**
   * Sets the manual top margin factor (>= 0.0 or null).
   *
   * @param value	the top margin
   */
  public void setManualTopMargin(Double value) {
    m_Model.setManualTopMargin(value);
  }

  /**
   * Returns the currently set manual top margin factor (>= 0.0 or null).
   *
   * @return		the top margin
   */
  public Double getManualTopMargin() {
    return m_Model.getManualTopMargin();
  }

  /**
   * Sets the manual bottom margin factor (>= 0.0 or null).
   *
   * @param value	the bottom margin
   */
  public void setManualBottomMargin(Double value) {
    m_Model.setManualBottomMargin(value);
  }

  /**
   * Returns the currently set manual bottom margin factor (>= 0.0 or null).
   *
   * @return		the bottom margin
   */
  public Double getManualBottomMargin() {
    return m_Model.getManualBottomMargin();
  }

  /**
   * Clears the panning.
   */
  public void clearPanning() {
    m_Model.setPixelOffset(0);
  }

  /**
   * Sets the pixel offset due to panning.
   *
   * @param value	the offset
   */
  public void setPixelOffset(int value) {
    m_Model.setPixelOffset(value);
  }

  /**
   * Returns the current pixel offset.
   *
   * @return		the offset
   */
  public int getPixelOffset() {
    return m_Model.getPixelOffset();
  }

  /**
   * Sets the pattern used for displaying the numbers on the axis.
   *
   * @param value	the value to use
   * @see		DecimalFormat#applyPattern(String)
   */
  public void setNumberFormat(String value) {
    m_Model.setNumberFormat(value);
    notifyChangeListeners();
  }

  /**
   * Returns the pattern used for displaying the numbers on the axis.
   *
   * @return		the pattern
   * @see		DecimalFormat#toPattern()
   */
  public String getNumberFormat() {
    return m_Model.getNumberFormat();
  }

  /**
   * Enables/disables the display of grid lines for this axis.
   *
   * @param value	if true then the grid lines will be activated
   */
  public void setShowGridLines(boolean value) {
    m_ShowGridLines = value;
    notifyChangeListeners();
  }

  /**
   * Returns whether the grid lines are displayed.
   *
   * @return		true if the grid lines are displayed
   */
  public boolean getShowGridLines() {
    return m_ShowGridLines;
  }

  /**
   * Returns the underlying axis model.
   *
   * @return		the model
   */
  public AbstractAxisModel getAxisModel() {
    return m_Model;
  }

  /**
   * Sets the visibility of the axis.
   *
   * @param value	the visibility
   */
  public void setVisibility(Visibility value) {
    m_Visibility = value;
    calculateDimensions();
    notifyChangeListeners();
  }

  /**
   * Returns the visibility of the axis.
   *
   * @return		the visibility
   */
  public Visibility getVisibility() {
    return m_Visibility;
  }

  /**
   * Checks whether we can still zoom in.
   *
   * @param min		the minimum of the zoom
   * @param max		the maximum of the zoom
   * @return		true if zoom is possible
   */
  public boolean canZoom(double min, double max) {
    return m_Model.canZoom(min, max);
  }

  /**
   * Adds the zoom to its internal list and updates the axis.
   *
   * @param min		the minimum of the zoom
   * @param max		the maximum of the zoom
   */
  public void pushZoom(double min, double max) {
    m_Model.pushZoom(min, max);
  }

  /**
   * Removes the latest zoom, if available.
   */
  public void popZoom() {
    m_Model.popZoom();
  }

  /**
   * Returns true if the axis is currently zoomed.
   *
   * @return		true if a zoom is in place
   */
  public boolean isZoomed() {
    return m_Model.isZoomed();
  }

  /**
   * Removes all zooms.
   */
  public void clearZoom() {
    m_Model.clearZoom();
  }

  /**
   * Depending on direction, either returns width (= HORIZONTAL) or height
   * (= VERTICAL).
   *
   * @return		the length of the axis
   */
  public int getLength() {
    if (m_Direction == Direction.HORIZONTAL)
      return getWidth();
    else
      return getHeight();
  }

  /**
   * Pops up a dialog letting the user choose the range (min/max) for the
   * axis.
   */
  public void selectRange() {
    ApprovalDialog		dialog;
    ParameterPanel		panel;
    final BaseTextField		textMin;
    final BaseTextField		textMax;
    double			min;
    double			max;

    panel = new ParameterPanel();
    textMin = new BaseTextField(10);
    textMin.setText(Utils.doubleToString(getActualMinimumNoMargin(), 8));
    panel.addParameter("Minimum", textMin);
    textMax = new BaseTextField(10);
    textMax.setText(Utils.doubleToString(getActualMaximumNoMargin(), 8));
    panel.addParameter("Maximum", textMax);

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle("Select range for " + getAxisName());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setCancelVisible(true);
    dialog.setApproveVisible(true);
    dialog.setDiscardVisible(false);
    dialog.pack();
    dialog.setLocationRelativeTo(getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    try {
      min = Double.parseDouble(textMin.getText());
      max = Double.parseDouble(textMax.getText());
      if (!getAxisModel().canHandle(min, max))
	throw new Exception("Cannot handle range!");
      setManualMinimum(min);
      setManualMaximum(max);
      clearZoom();
      clearPanning();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	getParent(),
	"Failed to parse/set range parameters:\n"
	  + textMin.getText() + "\n"
	  + textMax.getText() + "\n"
	  + LoggingHelper.throwableToString(e));
    }
  }

  /**
   * Sets the current range of the axis.
   *
   * @param value	the new range
   */
  public void setRange(BaseInterval value) {
    double		min;
    double		max;

    min = value.getLower();
    max = value.getUpper();
    if (!getAxisModel().canHandle(min, max)) {
      GUIHelper.showErrorMessage(
	getParent(),
	"Failed to parse/set range parameters:\n" + value);
    }
    else {
      setManualMinimum(min);
      setManualMaximum(max);
      clearZoom();
      clearPanning();
    }
  }

  /**
   * Returns the current range of the axis.
   *
   * @return		the current range
   */
  public BaseInterval getRange() {
    return new BaseInterval(
      getActualMinimumNoMargin(),
      getActualMaximumNoMargin());
  }

  /**
   * Copies the range to the clipboard.
   */
  protected void copyRange() {
    ClipboardHelper.copyToClipboard(getRange().getValue());
  }

  /**
   * Pastes the range from the clipboard, if possible.
   */
  protected void pasteRange() {
    BaseInterval 	range;

    if (!ClipboardHelper.canPasteStringFromClipboard())
      return;

    range = new BaseInterval();
    if (range.isValid(ClipboardHelper.pasteStringFromClipboard())) {
      range.setValue(ClipboardHelper.pasteStringFromClipboard());
      setRange(range);
    }
  }

  /**
   * Resets any manually set range for the axis.
   */
  public void resetRange() {
    setManualMinimum(null);
    setManualMaximum(null);
    clearZoom();
    clearPanning();
  }

  /**
   * Pops up a dialog letting the user choose the margins (top/bottom) for the
   * axis.
   */
  public void selectMargins() {
    ApprovalDialog	dialog;
    ParameterPanel	panel;
    final BaseTextField 	textTop;
    final BaseTextField 	textBottom;
    double 		top;
    double 		bottom;

    panel = new ParameterPanel();
    textTop = new BaseTextField(10);
    textTop.setText(Utils.doubleToString(getManualTopMargin() == null ? getTopMargin() : getManualTopMargin(), 8));
    panel.addParameter("Top", textTop);
    textBottom = new BaseTextField(10);
    textBottom.setText(Utils.doubleToString(getManualBottomMargin() == null ? getBottomMargin() : getManualBottomMargin(), 8));
    panel.addParameter("Bottom", textBottom);

    if (getParentDialog() != null)
      dialog = new ApprovalDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(getParentFrame(), true);
    dialog.setTitle("Select margins for " + getAxisName());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setCancelVisible(true);
    dialog.setApproveVisible(true);
    dialog.setDiscardVisible(false);
    dialog.pack();
    dialog.setLocationRelativeTo(getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    try {
      top    = Double.parseDouble(textTop.getText());
      bottom = Double.parseDouble(textBottom.getText());
      setManualTopMargin(top);
      setManualBottomMargin(bottom);
      clearZoom();
      clearPanning();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	getParent(),
	"Failed to parse/set margins:\n"
	  + textTop.getText() + "\n"
	  + textBottom.getText() + "\n"
	  + LoggingHelper.throwableToString(e));
    }
  }

  /**
   * Copies the margins to the clipboard.
   */
  protected void copyMargins() {
    String	range;

    range =
      Utils.doubleToString(getManualTopMargin() == null ? getTopMargin() : getManualTopMargin(), 8)
	+ ";"
	+ Utils.doubleToString(getManualBottomMargin() == null ? getBottomMargin() : getManualBottomMargin(), 8);
    ClipboardHelper.copyToClipboard(range);
  }

  /**
   * Pastes the margins from the clipboard, if possible.
   */
  protected void pasteMargins() {
    String 	range;
    String[]	parts;
    double 	top;
    double 	bottom;

    if (!ClipboardHelper.canPasteStringFromClipboard())
      return;

    range = ClipboardHelper.pasteStringFromClipboard();
    if (range.contains(";") && (range.split(";").length == 2)) {
      parts  = range.split(";");
      if (Utils.isDouble(parts[0]) && Utils.isDouble(parts[1])) {
	top    = Double.parseDouble(parts[0]);
	bottom = Double.parseDouble(parts[1]);
	setManualTopMargin(top);
	setManualBottomMargin(bottom);
	clearZoom();
	clearPanning();
      }
    }
  }

  /**
   * Resets any manually set margins for the axis.
   */
  public void resetMargins() {
    setManualTopMargin(null);
    setManualBottomMargin(null);
    clearZoom();
    clearPanning();
  }

  /**
   * Sets the class to customize the right-click popup menu.
   *
   * @param value	the customizer
   */
  public void setPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_PopupMenuCustomizer = value;
  }

  /**
   * Returns the current customizer, can be null.
   *
   * @return		the customizer
   */
  public PopupMenuCustomizer getPopupMenuCustomizer() {
    return m_PopupMenuCustomizer;
  }

  /**
   * Returns the popup menu, potentially customized.
   *
   * @param e		the mouse event
   * @return		the popup menu
   * @see		#m_PopupMenuCustomizer
   */
  public BasePopupMenu getPopupMenu(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		item;
    JMenu		submenu;
    ButtonGroup 	group;

    result = new BasePopupMenu();

    // type
    group = new ButtonGroup();
    for (Type t: Type.values()) {
      final Type type = t;
      item = new JRadioButtonMenuItem(t.toString());
      if (m_Type == t)
	item.setSelected(true);
      item.setEnabled(t.canHandle(m_Model.getMinimum(), m_Model.getMaximum()));
      item.addActionListener((ActionEvent ae) -> m_Self.setType(type));
      if (item.isSelected() || item.isEnabled()) {
	result.add(item);
	group.add(item);
      }
    }

    result.addSeparator();

    // ticks
    item = new JMenuItem("Ticks...");
    item.addActionListener((ActionEvent ae) -> editTickGenerator());
    result.add(item);

    // format
    item = new JMenuItem("Format...");
    item.addActionListener((ActionEvent ae) -> {
      String pattern = getNumberFormat();
      pattern = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(m_Self),
	"Please enter format (empty format resets to default again):",
	pattern);
      if (pattern != null) {
	if (pattern.length() == 0)
	  removeNumberFormatOverride(getType());
	else
	  addNumberFormatOverride(getType(), pattern);
      }
    });
    result.add(item);

    // grid lines
    item = new JCheckBoxMenuItem("Grid lines");
    item.setSelected(getShowGridLines());
    item.addActionListener((ActionEvent ae) -> setShowGridLines(!getShowGridLines()));
    result.add(item);

    if (m_Model instanceof FlippableAxisModel) {
      item = new JCheckBoxMenuItem("Flip");
      item.setSelected(((FlippableAxisModel) m_Model).isFlipped());
      item.addActionListener((ActionEvent ae) ->
	((FlippableAxisModel) m_Model).setFlipped(!((FlippableAxisModel) m_Model).isFlipped()));
      result.add(item);
    }

    // range menu
    submenu = new JMenu("Range");
    result.add(submenu);

    // edit range
    item = new JMenuItem("Edit...");
    item.addActionListener((ActionEvent ae) -> selectRange());
    submenu.add(item);

    // copy range
    item = new JMenuItem("Copy");
    item.addActionListener((ActionEvent ae) -> copyRange());
    submenu.add(item);

    // paste range
    item = new JMenuItem("Paste");
    item.addActionListener((ActionEvent ae) -> pasteRange());
    submenu.add(item);

    // reset range
    item = new JMenuItem("Reset");
    item.addActionListener((ActionEvent ae) -> resetRange());
    submenu.add(item);

    // margins
    submenu = new JMenu("Margins");
    result.add(submenu);

    // edit margins
    item = new JMenuItem("Edit...");
    item.addActionListener((ActionEvent ae) -> selectMargins());
    submenu.add(item);

    // copy margins
    item = new JMenuItem("Copy");
    item.addActionListener((ActionEvent ae) -> copyMargins());
    submenu.add(item);

    // paste margins
    item = new JMenuItem("Paste");
    item.addActionListener((ActionEvent ae) -> pasteMargins());
    submenu.add(item);

    // reset margins
    item = new JMenuItem("Reset");
    item.addActionListener((ActionEvent ae) -> resetMargins());
    submenu.add(item);

    // customize it?
    if (m_PopupMenuCustomizer != null)
      m_PopupMenuCustomizer.customizePopupMenu(e, result);

    return result;
  }

  /**
   * Allows the user to edit the tick generator.
   */
  protected void editTickGenerator() {
    GenericObjectEditorDialog	dialog;

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Ticks");
    dialog.setUISettingsPrefix(TickGenerator.class);
    dialog.setDefaultCloseOperation(GenericObjectEditorDialog.DISPOSE_ON_CLOSE);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(TickGenerator.class);
    dialog.setCurrent(getTickGenerator());
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    setTickGenerator((TickGenerator) dialog.getCurrent());
  }

  /**
   * Adds the given listener to the internal list of change listeners.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list of change listeners.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Notifies all change listeners.
   */
  public void notifyChangeListeners() {
    Iterator<ChangeListener>	iter;
    final ChangeEvent		e;

    e    = new ChangeEvent(this);
    iter = m_ChangeListeners.iterator();
    while (iter.hasNext()) {
      final ChangeListener l = iter.next();
      SwingUtilities.invokeLater(() -> l.stateChanged(e));
    }
  }

  /**
   * Adds the format override for the specified type. If the current type is
   * the same type as being set, then the format is automatically updated.
   *
   * @param type	the axis model type
   * @param format	the override
   */
  public void addNumberFormatOverride(Type type, String format) {
    m_NumberFormatOverride.put(type, format);

    if (getType() == type)
      setNumberFormat(format);
  }

  /**
   * Removes the format override for the specified type. If the current type
   * is the same type as being removed, then the format is reset to the default
   * one.
   *
   * @param type	the axis model type
   */
  public void removeNumberFormatOverride(Type type) {
    m_NumberFormatOverride.remove(type);

    if (getType() == type)
      setNumberFormat(type.getModel().getNumberFormat());
  }

  /**
   * Returns whether an override format is available for the specified type.
   *
   * @param type	the axis model type
   * @return		true if an override format exists
   */
  public boolean hasNumberFormatOverride(Type type) {
    return m_NumberFormatOverride.containsKey(type);
  }

  /**
   * Returns the format override for the specified type, null if non-existing.
   *
   * @param type	the axis model type
   * @return		the override format, can be null
   */
  public String getNumberFormatOverride(Type type) {
    return m_NumberFormatOverride.get(type);
  }

  /**
   * "corrects" the position for an origin at bottom-left corner instead
   * of top-left corner, in case of a VERTICAL axis.
   *
   * @param pos		the position to correct
   * @return		the corrected position
   */
  public int correctPosition(int pos) {
    int		result;

    result = pos;

    if (m_Direction == Direction.VERTICAL)
      result = (getLength() - 1) - result;

    return result;
  }

  /**
   * Returns the position for the given value according to the settings (min
   * and max).
   *
   * @param value	the value to turn into a position
   * @return		the position
   */
  public int valueToPos(double value) {
    int		result;

    result = m_Model.valueToPos(value);
    result = correctPosition(result);

    return result;
  }

  /**
   * Returns the value for the given position according to the settings
   * (min and max).
   *
   * @param pos		the position on the panel
   * @return		the corresponding value
   */
  public double posToValue(int pos) {
    double	result;

    pos    = correctPosition(pos);
    result = m_Model.posToValue(pos);

    return result;
  }

  /**
   * draws the bar.
   *
   * @param g		the graphics context
   */
  public void drawBar(Graphics g) {
    int 	height;
    int		width;
    Graphics2D	g2d;

    g2d    = (Graphics2D) g;
    height = getSize().height;
    width  = getSize().width;

    g.setColor(m_AxisColor);

    if (m_Direction == Direction.VERTICAL) {
      if (m_Orientation == Orientation.LEFT_TO_RIGHT) {
	/*
	 *    10 -|
	 *        |
	 *  s     |
	 *  i  5 -|
	 *  x     |
	 *  A     |
	 *     0 -|
	 */

	g.drawLine(width - 1, 0, width - 1, height);
      }
      else {
	/*
	 * |- 10
	 * |      A
	 * |      x
	 * |- 5   i
	 * |      s
	 * |
	 * |- 0
	 */

	g.drawLine(0, 0, 0, height - 1);
      }
    }
    else {
      if (m_Orientation == Orientation.LEFT_TO_RIGHT) {
	/*
	 * -----------------
	 * |       |       |
	 * 0       5       10
	 *
	 *       A x i s
	 */

	g.drawLine(0, 0, width - 1, 0);
      }
      else {
	/*
	 *      A x i s
	 *
	 * 0       5       10
	 * |       |       |
	 * -----------------
	 */

	g2d.drawLine(0, height - 1, width - 1, height - 1);
      }
    }
  }

  /**
   * Checks whether the tick is the first one.
   *
   * @param ticks	all the ticks
   * @param i		the current tick's index
   * @param flipped	whether the axis is flipped
   * @param max		the width/height of the panel
   * @return		true if first
   */
  protected boolean isFirst(List<Tick> ticks, int i, boolean flipped, int max) {
    return (ticks.get(i).getPosition() == 0);
  }

  /**
   * Checks whether the tick is the last one.
   *
   * @param ticks	all the ticks
   * @param i		the current tick's index
   * @param flipped	whether the axis is flipped
   * @param max		the width/height of the panel
   * @return		true if last
   */
  protected boolean isLast(List<Tick> ticks, int i, boolean flipped, int max) {
    return (ticks.get(i).getPosition() == max - 1);
  }

  /**
   * Checks whether the tick is to be skipped.
   *
   * @param ticks	all the ticks
   * @param i		the current tick's index
   * @return		true if skip
   */
  protected boolean skipLabel(List<Tick> ticks, int i) {
    return !ticks.get(i).hasLabel() || (m_Model.getNthValueToShow() == 0) || (i % m_Model.getNthValueToShow() != 0);
  }

  /**
   * Checks whether there is an overlap between the labels of the two Ticks.
   *
   * @param tick1	the first tick
   * @param tick2	the second tick
   * @return		true if overlapping
   */
  protected boolean hasOverlap(Tick tick1, Tick tick2) {
    return
      (tick1 != null)
	&& (tick2 != null)
	&& tick1.hasBounds()
	&& tick2.hasBounds()
	&& tick1.getBounds().intersects(tick2.getBounds());
  }

  /**
   * draws the ticks.
   *
   * @param g		the graphics context
   */
  protected void drawTicks(Graphics g) {
    int 		height;
    int			width;
    int			i;
    int			x;
    int			y;
    int			tickPos;
    Graphics2D		g2d;
    TextLayout		layout;
    Rectangle2D		bounds;
    List<Tick>		ticks;
    Tick		tick;
    int			len;
    boolean		flipped;
    Tick		lastTick;
    boolean		skipped;

    g2d      = (Graphics2D) g;
    height   = getSize().height;
    width    = getSize().width;
    ticks    = m_Model.getTicks();
    flipped  = (m_Model instanceof FlippableAxisModel) && (((FlippableAxisModel) m_Model).isFlipped());
    lastTick = null;

    g.setColor(m_AxisColor);
    GUIHelper.configureAntiAliasing(g, true);

    if (m_Direction == Direction.VERTICAL) {
      if (m_Orientation == Orientation.LEFT_TO_RIGHT) {
	/*
	 *    10 -|
	 *        |
	 *  s     |
	 *  i  5 -|
	 *  x     |
	 *  A     |
	 *     0 -|
	 */

	for (i = 0; i < ticks.size(); i++) {
	  tick    = ticks.get(i);
	  tickPos = correctPosition(tick.getPosition());

	  // the value
	  skipped = !tick.hasLabel() || skipLabel(ticks, i);
	  if (!skipped) {
	    layout = new TextLayout(tick.getLabel(), g.getFont(), g2d.getFontRenderContext());
	    bounds = layout.getBounds();
	    y      = tickPos;
	    if (isFirst(ticks, i, flipped, height))
	      y -= bounds.getHeight() / 2;
	    else if (isLast(ticks, i, flipped, height))
	      y += bounds.getHeight() / 2 + 2;
	    y = (int) (y + bounds.getHeight() / 2);
	    x = (int) (width - (bounds.getWidth() + m_LengthTicks + 4));
	    tick.setBounds(x, y, (int) bounds.getWidth(), (int) bounds.getHeight());
	    if (!hasOverlap(tick, lastTick)) {
	      g.drawString(tick.getLabel(), x, y);
	      lastTick = tick;
	    }
	  }

	  // the tick
	  len = (skipped ? m_LengthTicks / 2 : m_LengthTicks);
	  g.drawLine(width - len, tickPos, width, tickPos);
	}
      }
      else {
	/*
	 * |- 10
	 * |      A
	 * |      x
	 * |- 5   i
	 * |      s
	 * |
	 * |- 0
	 */

	for (i = 0; i < ticks.size(); i++) {
	  tick    = ticks.get(i);
	  tickPos = correctPosition(tick.getPosition());

	  // the value
	  skipped = !tick.hasLabel() || skipLabel(ticks, i);
	  if (!skipped) {
	    layout = new TextLayout(tick.getLabel(), g.getFont(), g2d.getFontRenderContext());
	    bounds = layout.getBounds();
	    y      = tickPos;
	    if (isFirst(ticks, i, flipped, height))
	      y -= bounds.getHeight() / 2;
	    else if (isLast(ticks, i, flipped, height))
	      y += bounds.getHeight() / 2 + 2;
	    y = (int) (y + bounds.getHeight() / 2);
	    x = (int) (m_LengthTicks + 4);
	    tick.setBounds(x, y, (int) bounds.getWidth(), (int) bounds.getHeight());
	    if (!hasOverlap(tick, lastTick)) {
	      g.drawString(tick.getLabel(), x, y);
	      lastTick = tick;
	    }
	  }

	  // the tick
	  len = (skipped ? m_LengthTicks / 2 : m_LengthTicks);
	  g.drawLine(0, tickPos, len, tickPos);
	}
      }
    }
    else {
      if (m_Orientation == Orientation.LEFT_TO_RIGHT) {
	/*
	 * -----------------
	 * |       |       |
	 * 0       5       10
	 *
	 *       A x i s
	 */

	for (i = 0; i < ticks.size(); i++) {
	  tick    = ticks.get(i);
	  tickPos = correctPosition(tick.getPosition());

	  // the value
	  skipped = !tick.hasLabel() || skipLabel(ticks, i);
	  if (!skipped) {
	    layout = new TextLayout(tick.getLabel(), g.getFont(), g2d.getFontRenderContext());
	    bounds = layout.getBounds();
	    x      = tickPos;
	    if (isFirst(ticks, i, flipped, width))
	      x += bounds.getWidth() / 2;
	    else if (isLast(ticks, i, flipped, width))
	      x -= bounds.getWidth() / 2;
	    x = (int) (x - bounds.getWidth() / 2);
	    y = (int) (bounds.getHeight() + m_LengthTicks + 4);
	    tick.setBounds(x, y, (int) bounds.getWidth(), (int) bounds.getHeight());
	    if (!hasOverlap(tick, lastTick)) {
	      g.drawString(tick.getLabel(), x, y);
	      lastTick = tick;
	    }
	  }

	  // the tick
	  len = (skipped ? m_LengthTicks / 2 : m_LengthTicks);
	  g.drawLine(tickPos, 0, tickPos, len);
	}
      }
      else {
	/*
	 *      A x i s
	 *
	 * 0       5       10
	 * |       |       |
	 * -----------------
	 */

	for (i = 0; i < ticks.size(); i++) {
	  tick    = ticks.get(i);
	  tickPos = correctPosition(tick.getPosition());

	  // the value
	  skipped = !tick.hasLabel() || skipLabel(ticks, i);
	  if (!skipped) {
	    layout = new TextLayout(tick.getLabel(), g.getFont(), g2d.getFontRenderContext());
	    bounds = layout.getBounds();
	    x      = tickPos;
	    if (isFirst(ticks, i, flipped, width))
	      x += bounds.getWidth() / 2;
	    else if (isLast(ticks, i, flipped, width))
	      x -= bounds.getWidth() / 2;
	    x = (int) (x - bounds.getWidth() / 2);
	    y = (int) (height - (m_LengthTicks + 4));
	    tick.setBounds(x, y, (int) bounds.getWidth(), (int) bounds.getHeight());
	    if (!hasOverlap(tick, lastTick)) {
	      g.drawString(tick.getLabel(), x, y);
	      lastTick = tick;
	    }
	  }

	  // the tick
	  len = (skipped ? m_LengthTicks / 2 : m_LengthTicks);
	  g2d.drawLine(tickPos, height - len - 1, tickPos, height);
	}
      }
    }
  }

  /**
   * draws the axis name.
   *
   * @param g		the graphics context
   */
  protected void drawName(Graphics g) {
    int 		height;
    int			width;
    int			x;
    int			y;
    Graphics2D		g2d;
    TextLayout		textLayoutName;
    Rectangle2D		textBoundsName;
    Font		defaultFont;

    if (m_AxisName.trim().length() == 0)
      return;

    g2d         = (Graphics2D) g;
    height      = getSize().height;
    width       = getSize().width;
    defaultFont = g.getFont();
    if ((m_AxisName != null) && (m_AxisName.length() > 0)) {
      textLayoutName = new TextLayout(m_AxisName, m_AxisNameFont, g2d.getFontRenderContext());
      textBoundsName = textLayoutName.getBounds();
    }
    else {
      textLayoutName = null;
      textBoundsName = null;
    }

    GUIHelper.configureAntiAliasing(g, true);

    if (m_Direction == Direction.VERTICAL) {
      if (m_Orientation == Orientation.LEFT_TO_RIGHT) {
	/*
	 *    10 -|
	 *        |
	 *  s     |
	 *  i  5 -|
	 *  x     |
	 *  A     |
	 *     0 -|
	 */

	g2d.rotate(Math.PI * 1.5);
	x = (int) -((height - textBoundsName.getWidth()) / 2 + textBoundsName.getWidth());
	if (m_AxisNameCentered)
	  y = (int) (textBoundsName.getHeight() / 2 + (width - m_LengthTicks) / 2);
	else
	  y = (int) (textBoundsName.getHeight() + 6);
	g.setColor(getBackground());
	g.setFont(m_AxisNameFont);
	g.fillRect(
	  (int) Math.round(textBoundsName.getX() + x) - 2,
	  (int) Math.round(textBoundsName.getY() + y) - 2,
	  (int) Math.round(textBoundsName.getWidth()) + 4,
	  (int) Math.round(textBoundsName.getHeight()) + 4);
	g.setColor(m_AxisColor);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g.drawString(m_AxisName, x, y);
	g.setFont(defaultFont);
	g2d.rotate(0);
      }
      else {
	/*
	 * |- 10
	 * |      A
	 * |      x
	 * |- 5   i
	 * |      s
	 * |
	 * |- 0
	 */

	g2d.rotate(Math.PI * 0.5);
	x = (int) ((height - textBoundsName.getWidth()) / 2);
	if (m_AxisNameCentered)
	  y = (int) (-(width / 2) + m_LengthTicks);
	else
	  y = (int) -(width - textBoundsName.getHeight() - 4);
	g.setColor(getBackground());
	g.setFont(m_AxisNameFont);
	g.fillRect(
	  (int) Math.round(textBoundsName.getX() + x) - 2,
	  (int) Math.round(textBoundsName.getY() + y) - 2,
	  (int) Math.round(textBoundsName.getWidth()) + 4,
	  (int) Math.round(textBoundsName.getHeight()) + 4);
	g.setColor(m_AxisColor);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g.drawString(m_AxisName, x, y);
	g.setFont(defaultFont);
	g2d.rotate(0);
      }
    }
    else {
      if (m_Orientation == Orientation.LEFT_TO_RIGHT) {
	/*
	 * -----------------
	 * |       |       |
	 * 0       5       10
	 *
	 *       A x i s
	 */

	x = (int) ((width - textBoundsName.getWidth()) / 2);
	if (m_AxisNameCentered)
	  y = (int) ((height / 2) + m_LengthTicks);
	else
	  y = (int) (height - textBoundsName.getHeight());
	g.setColor(getBackground());
	g.setFont(m_AxisNameFont);
	g.fillRect(
	  (int) Math.round(textBoundsName.getX() + x) - 2,
	  (int) Math.round(textBoundsName.getY() + y) - 2,
	  (int) Math.round(textBoundsName.getWidth()) + 4,
	  (int) Math.round(textBoundsName.getHeight()) + 4);
	g.setColor(m_AxisColor);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g.drawString(m_AxisName, x, y);
	g.setFont(defaultFont);
      }
      else {
	/*
	 *      A x i s
	 *
	 * 0       5       10
	 * |       |       |
	 * -----------------
	 */

	x = (int) ((width - textBoundsName.getWidth()) / 2);
	if (m_AxisNameCentered)
	  y = (int) ((height / 2) + m_LengthTicks);
	else
	  y = (int) (textBoundsName.getHeight() + 4);
	g.setColor(getBackground());
	g.setFont(m_AxisNameFont);
	g.fillRect(
	  (int) Math.round(textBoundsName.getX() + x) - 2,
	  (int) Math.round(textBoundsName.getY() + y) - 2,
	  (int) Math.round(textBoundsName.getWidth()) + 4,
	  (int) Math.round(textBoundsName.getHeight()) + 4);
	g.setColor(m_AxisColor);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	g.drawString(m_AxisName, x, y);
	g.setFont(defaultFont);
      }
    }
  }

  /**
   * draws the axis.
   *
   * @param g		the graphics context
   */
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    m_Model.validate();

    drawBar(g);
    drawTicks(g);
    if (m_AxisName != null)
      drawName(g);
  }

  /**
   * Returns a short description of the axis.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    String	result;

    result  = getClass().getName() + ": ";
    result += "name=" + m_AxisName + ", ";
    result += "direction=" + m_Direction + ", ";
    result += "orientation=" + m_Orientation + ", ";
    result += "visibility=" + m_Visibility + ", ";
    result += "act.width=" + getActualAxisWidth() + ", ";
    result += "panel.width=" + (m_Direction == Direction.HORIZONTAL ? getPreferredSize().height : getPreferredSize().width) + ", ";
    result += "type=" + m_Type + ", ";
    result += "model=[" + m_Model + "]";

    return result;
  }
}
