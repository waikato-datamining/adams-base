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
 * ZScoreDisplay.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JComponent;

import weka.core.Attribute;
import weka.core.Instances;
import adams.core.Index;
import adams.core.base.BaseRegExp;
import adams.flow.core.Token;
import adams.gui.core.BasePanel;
import adams.gui.visualization.stats.paintlet.AbstractZScorePaintlet;
import adams.gui.visualization.stats.paintlet.ZScoreCircle;
import adams.gui.visualization.stats.zscore.AbstractZScoreOverlay;
import adams.gui.visualization.stats.zscore.Mean;
import adams.gui.visualization.stats.zscore.StdDev;
import adams.gui.visualization.stats.zscore.ZScore;

/**
 <!-- globalinfo-start -->
 * Actor for displaying a z score plot
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: ZScoreDisplay
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
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 700
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 500
 * &nbsp;&nbsp;&nbsp;minimum: 1
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
 * <pre>-overlay &lt;adams.gui.visualization.stats.zscore.AbstractZScoreOverlay&gt; [-overlay ...] (property: overlays)
 * &nbsp;&nbsp;&nbsp;Add overlays to the z score plot
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.zscore.Mean, adams.gui.visualization.stats.zscore.StdDev -color #ffc800 -standard-deviations 2.0, adams.gui.visualization.stats.zscore.StdDev -color #ff0000 -standard-deviations 3.0
 * </pre>
 *
 * <pre>-paintlet &lt;adams.gui.visualization.stats.paintlet.AbstractZScorePaintlet&gt; (property: paintlet)
 * &nbsp;&nbsp;&nbsp;Choose paintlet for plotting data
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.stats.paintlet.ZScoreCircle
 * </pre>
 *
 * <pre>-attribute-name &lt;adams.core.base.BaseRegExp&gt; (property: attributeName)
 * &nbsp;&nbsp;&nbsp;Name of attribute to display, used if set,otherwise the index is used
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-attribute &lt;java.lang.String&gt; (property: attribute)
 * &nbsp;&nbsp;&nbsp;Set the attribute to display using an index, used only if regular expression
 * &nbsp;&nbsp;&nbsp;not set
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class ZScoreDisplay
  extends AbstractGraphicalDisplay
  implements DisplayPanelProvider {

  /** for serialization */
  private static final long serialVersionUID = -909689793225143043L;

  /** zscore to display using the actor */
  protected ZScore m_ZScore;

  /**Array containing initial z score overlays*/
  protected AbstractZScoreOverlay[] m_Overlays;

  /**Paintlet to draw initial data with */
  protected AbstractZScorePaintlet m_Paintlet;

  /**String for regular expression for attribute */
  protected BaseRegExp m_Att;

  /** String for index to set attribute*/
  protected String m_AttIndex;

  /** whether to show the options in the display or not. */
  protected boolean m_ShowOptions;

  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  @Override
  public void defineOptions() {
    super.defineOptions();

    //need to set the default overlays, mean, 2 std and 3std
    Mean mean = new Mean();
    StdDev twoStd = new StdDev();
    StdDev threeStd = new StdDev();
    twoStd.setColor(Color.ORANGE);
    threeStd.setColor(Color.RED);
    twoStd.setStandardDeviations(2.0);
    threeStd.setStandardDeviations(3.0);

    //z-score overlays
    m_OptionManager.add(
	"overlay", "overlays",
	new AbstractZScoreOverlay[]{mean, twoStd, threeStd});

    //Paintlet used
    m_OptionManager.add(
	"paintlet", "paintlet",
	new ZScoreCircle());

    //Name of attributes
    m_OptionManager.add(
	"attribute-name", "attributeName", new BaseRegExp(""));

    //Index of attribute
    m_OptionManager.add(
	"attribute", "attribute", "1");

    m_OptionManager.add(
	"show-options", "showOptions",
	false);
  }

  /**
   * Set the attribute to use with a regular expression
   * @param val		String for regular expression
   */
  public void setAttributeName(BaseRegExp val) {
    m_Att = val;
    reset();
  }

  /**
   * Get the string to set the attribute using a regular expression
   * @return				String for regular expression
   */
  public BaseRegExp getAttributeName() {
    return m_Att;
  }

  /**
   * Return a tip text for the attribute name property
   * @return			tip text for the property
   */
  public String attributeNameTipText() {
    return "Name of attribute to display, used if set," +
    "otherwise the index is used";
  }

  /**
   * Sets whether to show the options panel or not.
   *
   * @param value	true if to show the options
   */
  public void setShowOptions(boolean value) {
    m_ShowOptions = value;
    reset();
  }

  /**
   * Returns whether to show the options or not.
   *
   * @return		true if the options are shown
   */
  public boolean getShowOptions() {
    return m_ShowOptions;
  }

  /**
   * Return a tip text for the attribute name property
   * @return			tip text for the property
   */
  public String showOptionsTipText() {
    return "If enabled, the options are shown in the display, allowing the user to modify the visual appearance.";
  }

  /**
   * Set the string to use for setting the attribute using
   * an index
   * @param val			String for the attribute index
   */
  public void setAttribute(String val) {
    m_AttIndex = val;
    reset();
  }

  /**
   * Get the string used to set the attribute using an index
   * @return			String for the index
   */
  public String getAttribute() {
    return m_AttIndex;
  }

  /**
   * return a tip text for the attribute index property
   * @return			tip text for the property
   */
  public String attributeTipText() {
    return "Set the attribute to display using an index, used only if " +
    "regular expression not set";
  }


  /**
   * set the overlays to be drawn on the z score plot
   * @param over			Array containing the overlays
   */
  public void setOverlays(AbstractZScoreOverlay[] over) {
    m_Overlays = over;
    reset();
  }

  /**
   * get the overlays to be drawn on the z score plot
   * @return				Array containing the overlays
   */
  public AbstractZScoreOverlay[] getOverlays() {
    return m_Overlays;
  }

  /**
   * Returns a tip text for the overlays property
   * @return				Tip text for the property
   */
  public String overlaysTipText() {
    return "Add overlays to the z score plot";
  }

  /**
   * Setbefore regular expression if this is provide the paintlet to be used for displaying the data
   * @param paintlet			Paintlet to be used
   */
  public void setPaintlet(AbstractZScorePaintlet paintlet) {
    m_Paintlet = paintlet;
    reset();
  }

  /**
   * Get the paintlet to be used for plotting the data
   * @return				Paintlet to be used
   */
  public AbstractZScorePaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns a tip text for the paintlet property
   * @return				Tip text for this property
   */
  public String paintletTipText() {
    return "Choose paintlet for plotting data";
  }

  @Override
  protected int getDefaultWidth() {
    return 1200;
  }

  @Override
  protected int getDefaultHeight() {
    return 500;
  }

  @Override
  public void clearPanel() {
    if (m_ZScore != null) {
      Instances temp = new Instances("Empty", new ArrayList<Attribute>(), 0);
      m_ZScore.setInstances(temp);
    }
  }

  @Override
  protected BasePanel newPanel() {
    m_ZScore = new ZScore();
    m_ZScore.setOptionsVisible(getShowOptions());
    return m_ZScore;
  }

  @Override
  protected void display(Token token) {
    m_ZScore.setAttReg(m_Att);
    m_ZScore.setAttindex(new Index(m_AttIndex));
    m_ZScore.setInstances((Instances) token.getPayload());
    m_ZScore.setOverlays(m_Overlays);
    m_ZScore.setPaintlet(m_Paintlet);
    m_ZScore.reset();
  }

  /**
   * Creates a new panel for the token.
   *
   * @param token	the token to display in a new panel, can be null
   * @return		the generated panel
   */
  public AbstractDisplayPanel createDisplayPanel(Token token) {
    AbstractDisplayPanel	result;
    String			name;

    name = "ZScore Display (" + ((Instances) token.getPayload()).relationName() + ")";

    result = new AbstractComponentDisplayPanel(name) {
      private static final long serialVersionUID = 3272038733338355773L;
      protected ZScore m_ZScore;
      @Override
      protected void initGUI() {
	super.initGUI();
	setLayout(new BorderLayout());
	m_ZScore = new ZScore();
	m_ZScore.setOptionsVisible(false);
	add(m_ZScore, BorderLayout.CENTER);
      }
      @Override
      public void display(Token token) {
	try {
	  m_ZScore.setAttReg(m_Att);
	  m_ZScore.setAttindex(new Index(m_AttIndex));
	  m_ZScore.setInstances((Instances) token.getPayload());
	  m_ZScore.setOverlays(m_Overlays);
	  m_ZScore.setPaintlet(m_Paintlet);
	  m_ZScore.reset();
	}
	catch (Exception e) {
	  handleException("Failed to display token", e);
	}
      }
      @Override
      public JComponent supplyComponent() {
	return m_ZScore;
      }
      @Override
      public void clearPanel() {
	m_ZScore.setInstances(null);
      }
      public void cleanUp() {
	m_ZScore.setInstances(null);
      }
    };
    
    if (token != null)
      result.display(token);

    return result;
  }

  /**
   * Returns whether the created display panel requires a scroll pane or not.
   *
   * @return		true if the display panel requires a scroll pane
   */
  public boolean displayPanelRequiresScrollPane() {
    return false;
  }

  @Override
  public String globalInfo() {
    return "Actor for displaying a z score plot";
  }
}