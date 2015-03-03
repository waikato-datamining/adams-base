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
 * Canvas.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import adams.flow.core.Token;
import adams.flow.sink.canvas.AbstractDataPoolPostProcessor;
import adams.flow.sink.canvas.DataPoolPaintlet;
import adams.flow.sink.canvas.DataPoolPanel;
import adams.flow.sink.canvas.PassThrough;
import adams.flow.sink.canvas.XYPaintlet;
import adams.gui.core.BasePanel;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.BackgroundImagePaintlet;
import adams.gui.visualization.core.Paintlet;

/**
 <!-- globalinfo-start -->
 * General purpose drawing canvas. What kind of data can be drawn depends on the paintlet in use.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.Double[]<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Canvas
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-short-title (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
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
 * <pre>-background-paintlet &lt;adams.gui.visualization.core.Paintlet&gt; (property: backgroundPaintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for plotting the background.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.core.BackgroundImagePaintlet
 * </pre>
 * 
 * <pre>-paintlet &lt;adams.flow.sink.canvas.DataPoolPaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;The paintlet to use for plotting the data.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.canvas.XYPaintlet
 * </pre>
 * 
 * <pre>-post-processor &lt;adams.flow.sink.canvas.AbstractDataPoolPostProcessor&gt; (property: postProcessor)
 * &nbsp;&nbsp;&nbsp;The post-processor to use on the data after a token was received and plotted.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.sink.canvas.PassThrough
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Canvas
  extends AbstractGraphicalDisplay {

  /** for serialization. */
  private static final long serialVersionUID = -5501727620411611401L;

  /** the background paintlet to use. */
  protected Paintlet m_BackgroundPaintlet;

  /** the paintlet to use. */
  protected DataPoolPaintlet m_Paintlet;
  
  /** data pool post-processor. */
  protected AbstractDataPoolPostProcessor m_PostProcessor;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"General purpose drawing canvas. What kind of data can be drawn "
	+ "depends on the paintlet in use.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "background-paintlet", "backgroundPaintlet",
	    new BackgroundImagePaintlet());

    m_OptionManager.add(
	    "paintlet", "paintlet",
	    new XYPaintlet());

    m_OptionManager.add(
	    "post-processor", "postProcessor",
	    new PassThrough());
  }

  /**
   * Sets the paintlet to use for plotting the background.
   *
   * @param value 	the paintlet
   */
  public void setBackgroundPaintlet(Paintlet value) {
    if (value.getPaintMoment() != PaintMoment.BACKGROUND)
      throw new IllegalArgumentException("Paintlet cannot be used for background!");
    m_BackgroundPaintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use for plotting the background.
   *
   * @return 		the paintlet
   */
  public Paintlet getBackgroundPaintlet() {
    return m_BackgroundPaintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String backgroundPaintletTipText() {
    return "The paintlet to use for plotting the background.";
  }

  /**
   * Sets the paintlet to use for plotting the data.
   *
   * @param value 	the paintlet
   */
  public void setPaintlet(DataPoolPaintlet value) {
    m_Paintlet = value;
    reset();
  }

  /**
   * Returns the paintlet to use for plotting the data.
   *
   * @return 		the paintlet
   */
  public DataPoolPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The paintlet to use for plotting the data.";
  }

  /**
   * Sets the post-processor to use on the data, e.g., for pruning.
   *
   * @param value 	the post-processor
   */
  public void setPostProcessor(AbstractDataPoolPostProcessor value) {
    m_PostProcessor = value;
    reset();
  }

  /**
   * Returns the post-processor to use on the data, e.g., for pruning.
   *
   * @return 		the post-processor
   */
  public AbstractDataPoolPostProcessor getPostProcessor() {
    return m_PostProcessor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorTipText() {
    return "The post-processor to use on the data after a token was received and plotted.";
  }

  /**
   * Returns the class that the consumer accepts.
   * 
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_Paintlet.accepts();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    DataPoolPanel	result;
    Paintlet		paintlet;
    
    result = new DataPoolPanel();
    paintlet = m_Paintlet.shallowCopy(true);
    paintlet.setPanel(result);
    result.addPaintlet(paintlet);
    paintlet = m_BackgroundPaintlet.shallowCopy(true);
    paintlet.setPanel(result);
    result.addPaintlet(paintlet);
    result.setPostProcessor(m_PostProcessor.shallowCopy(true));
    
    return result;
  }

  /**
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_Panel != null)
      ((DataPoolPanel) m_Panel).clear();
  }

  /**
   * Displays the token (the panel and dialog have already been created at
   * this stage).
   *
   * @param token	the token to display
   */
  @Override
  protected void display(Token token) {
    ((DataPoolPanel) m_Panel).addData(token.getPayload());
  }
}
