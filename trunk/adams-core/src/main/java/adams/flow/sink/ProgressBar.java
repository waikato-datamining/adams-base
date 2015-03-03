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
 * ProgressBar.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.DecimalFormatString;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;

/**
 <!-- globalinfo-start -->
 * Displays a progress bar. The incoming token is used as 'current' value to be displayed.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Number<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ProgressBar
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 200
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -3
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-writer &lt;adams.gui.print.JComponentWriter&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for generating the graphics output.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.print.NullWriter
 * </pre>
 * 
 * <pre>-min &lt;double&gt; (property: minimum)
 * &nbsp;&nbsp;&nbsp;The minimum to use.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 * 
 * <pre>-max &lt;double&gt; (property: maximum)
 * &nbsp;&nbsp;&nbsp;The maximum to use.
 * &nbsp;&nbsp;&nbsp;default: 100.0
 * </pre>
 * 
 * <pre>-bar &lt;java.awt.Color&gt; (property: bar)
 * &nbsp;&nbsp;&nbsp;The bar color to use.
 * &nbsp;&nbsp;&nbsp;default: #0000ff
 * </pre>
 * 
 * <pre>-background &lt;java.awt.Color&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The background color to use.
 * &nbsp;&nbsp;&nbsp;default: #c0c0c0
 * </pre>
 * 
 * <pre>-foreground &lt;java.awt.Color&gt; (property: foreground)
 * &nbsp;&nbsp;&nbsp;The foreground color to use.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DecimalFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The format string to use for outputting the current value, use empty string 
 * &nbsp;&nbsp;&nbsp;to suppress output.
 * &nbsp;&nbsp;&nbsp;default: #.#%
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the current value.
 * &nbsp;&nbsp;&nbsp;default: helvetica-BOLD-16
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ProgressBar
  extends AbstractGraphicalDisplay {

  /** for serialization. */
  private static final long serialVersionUID = -4075776040257181463L;

  /**
   * Panel for displaying a progress bar.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ProgressBarPanel
    extends BasePanel {
    
    /** for serialization. */
    private static final long serialVersionUID = -8123047909195552460L;

    /** the owner. */
    protected ProgressBar m_Owner;
    
    /** the current value. */
    protected double m_Current;
    
    /** the decimal format. */
    protected transient DecimalFormat m_Format;
    
    /**
     * Initializes the panel with the specified owner.
     * 
     * @param owner	the owning actor
     */
    public ProgressBarPanel(ProgressBar owner) {
      m_Owner   = owner;
      m_Current = owner.getMinimum();
    }

    /**
     * Resets the progress bar.
     */
    public void reset() {
      m_Current = m_Owner.getMinimum();
      repaint();
    }
    
    /**
     * Updates the progress with the new current value.
     * 
     * @param current	the current value of the progress bar
     */
    public void update(double current) {
      m_Current = current;
      setToolTipText(Utils.doubleToString(m_Current, 3) + " of " + Utils.doubleToString(m_Owner.getMaximum(), 3));
      repaint();
    }
    
    /**
     * Paints the component.
     * 
     * @param g		the graphics context
     */
    @Override
    public void paint(Graphics g) {
      double		width;
      double		perc;
      String		curr;
      Graphics2D	g2d;
      TextLayout	layout;
      Rectangle2D	bounds;

      perc = (m_Current - m_Owner.getMinimum()) / (m_Owner.getMaximum()- m_Owner.getMinimum());
      g2d  = (Graphics2D) g;

      // background
      g.setColor(m_Owner.getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
      
      // bar
      width = perc * getWidth();
      g.setColor(m_Owner.getBar());
      g.fillRect(0, 0, (int) width, getHeight());
      
      // current value
      if (m_Format == null)
	m_Format = m_Owner.getFormat().toDecimalFormat();
      curr   = m_Format.format(perc);
      layout = new TextLayout(curr, g.getFont(), g2d.getFontRenderContext());
      bounds = layout.getBounds();
      g.setColor(m_Owner.getForeground());
      g.setFont(m_Owner.getFont());
      g.drawString(curr, (int) ((getWidth() - bounds.getWidth()) / 2), (int) ((getHeight() / 2 + bounds.getHeight() / 2)));
    }
  }
  
  /** the minimum of the progress bar. */
  protected double m_Minimum;

  /** the maximum of the progress bar. */
  protected double m_Maximum;

  /** the color of the bar. */
  protected Color m_Bar;

  /** the background color. */
  protected Color m_Background;

  /** the foreground color. */
  protected Color m_Foreground;

  /** the font to use. */
  protected Font m_Font;

  /** the format of the current value. */
  protected DecimalFormatString m_Format;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays a progress bar. The incoming token is used as 'current' value to be displayed.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min", "minimum",
	    0.0);

    m_OptionManager.add(
	    "max", "maximum",
	    100.0);

    m_OptionManager.add(
	    "bar", "bar",
	    Color.BLUE);

    m_OptionManager.add(
	    "background", "background",
	    Color.LIGHT_GRAY);

    m_OptionManager.add(
	    "foreground", "foreground",
	    Color.WHITE);

    m_OptionManager.add(
	    "format", "format",
	    new DecimalFormatString("#.#%"));

    m_OptionManager.add(
	    "font", "font",
	    new Font("helvetica", Font.BOLD, 16));
  }

  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  @Override
  protected int getDefaultX() {
    return -3;
  }
  
  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  @Override
  protected int getDefaultWidth() {
    return 200;
  }
  
  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  @Override
  protected int getDefaultHeight() {
    return 100;
  }
  
  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(double value) {
    m_Minimum = value;
    reset();
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public double getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum to use.";
  }

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(double value) {
    m_Maximum = value;
    reset();
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public double getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum to use.";
  }

  /**
   * Sets the bar color.
   *
   * @param value	the color
   */
  public void setBar(Color value) {
    m_Bar = value;
    reset();
  }

  /**
   * Returns the bar color.
   *
   * @return		the color
   */
  public Color getBar() {
    return m_Bar;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String barTipText() {
    return "The bar color to use.";
  }

  /**
   * Sets the background color.
   *
   * @param value	the color
   */
  public void setBackground(Color value) {
    m_Background = value;
    reset();
  }

  /**
   * Returns the background color.
   *
   * @return		the color
   */
  public Color getBackground() {
    return m_Background;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundTipText() {
    return "The background color to use.";
  }

  /**
   * Sets the foreground color.
   *
   * @param value	the color
   */
  public void setForeground(Color value) {
    m_Foreground = value;
    reset();
  }

  /**
   * Returns the foreground color.
   *
   * @return		the color
   */
  public Color getForeground() {
    return m_Foreground;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String foregroundTipText() {
    return "The foreground color to use.";
  }

  /**
   * Sets the format string for the current value.
   *
   * @param value	the format, empty string to suppress output
   */
  public void setFormat(DecimalFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format string for the current value.
   *
   * @return		the format, empty string is suppressing output
   */
  public DecimalFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The format string to use for outputting the current value, use empty string to suppress output.";
  }

  /**
   * Sets the font for the time display.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font for the time display.
   *
   * @return		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the current value.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result  = QuickInfoHelper.toString(this, "minimum", m_Minimum, "min: ");
    result += QuickInfoHelper.toString(this, "maximum", m_Maximum, ", max: ");
    result += QuickInfoHelper.toString(this, "format", m_Format, ", format: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Number.class};
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    return new ProgressBarPanel(this);
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    ((ProgressBarPanel) m_Panel).reset();
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    ((ProgressBarPanel) m_Panel).update(((Number) token.getPayload()).doubleValue());
  }
}
