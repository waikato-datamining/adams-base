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
 * Renderer.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandler;
import adams.flow.core.ActorUtils;
import adams.gui.core.ColorHelper;
import adams.gui.core.GUIHelper;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Hashtable;

/**
 * A specialized renderer for the tree elements.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Renderer
  extends DefaultTreeCellRenderer {

  /** for serialization. */
  private static final long serialVersionUID = -2445498431457837008L;

  /** the color for debugging background (editing). */
  public final static Color BACKGROUND_EDITING_DEBUG = ColorHelper.valueOf("#FFDDEE");

  /** the color for debugging background (running). */
  public final static Color BACKGROUND_RUNNING_DEBUG = ColorHelper.valueOf("#CC99CC");

  /** the color for background (running). */
  public final static Color BACKGROUND_RUNNING = Color.LIGHT_GRAY;

  /** the color for deprecated actors. */
  public final static Color COLOR_DEPRECATED = Color.RED;

  /** the color for visual cues. */
  public final static Color COLOR_VISUALCUES = Color.BLACK;

  /**
   * Basic class for drawing icons for actors in the flow.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ActorIcon
    implements Icon {

    /** the actual icon. */
    protected ImageIcon m_Icon;

    /** the scale factor. */
    protected double m_ScaleFactor;

    /** how to paint visual cues. */
    protected ActorExecution m_Execution;

    /** whether debugging is enabled. */
    protected boolean m_DebugOn;

    /** whether actor accepts input. */
    protected boolean m_HasInput;

    /** whether the actor generates output. */
    protected boolean m_HasOutput;

    /** whether the actor has sub-actors. */
    protected boolean m_HasSubActors;

    /** whether the input is forwarded to the sub-actors. */
    protected boolean m_ForwardsInput;

    /** whether the actor is deprecated. */
    protected boolean m_Deprecated;

    /**
     * Initializes the icon.
     *
     * @param icon		the actual icon
     * @param scaleFactor	the scale factor (1.0 = default size)
     * @param execution		how to paint the visual cues
     * @param debugOn		whether debugging is on
     * @param hasInput		whether the actor accepts input
     * @param hasOutput		whether the actor generates output
     * @param hasSubActors	whether the actor has sub actors
     * @param forwardsInput	whether the input is forwarded to the sub-actors
     * @param deprecated	whether the actor is deprecated
     */
    public ActorIcon(ImageIcon icon, double scaleFactor, ActorExecution execution, boolean debugOn, boolean hasInput, boolean hasOutput, boolean hasSubActors, boolean forwardsInput, boolean deprecated) {
      super();

      m_Icon          = icon;
      m_ScaleFactor   = scaleFactor;
      m_Execution     = execution;
      m_DebugOn       = debugOn;
      m_HasInput      = hasInput;
      m_HasOutput     = hasOutput;
      m_HasSubActors  = hasSubActors;
      m_ForwardsInput = forwardsInput;
      m_Deprecated    = deprecated;
    }

    /**
     * Pains the background of the icon.
     *
     * @param g		the graphics context
     * @param x		the x position
     * @param y		the y position
     * @param w		the width of the icon
     * @param h		the height of the icon
     */
    protected void paintBackground(Graphics g, int x, int y, int w, int h) {
      if (m_DebugOn) {
	g.setColor(BACKGROUND_EDITING_DEBUG);
	g.fillRect(x + 1, y + 1, w - 2, h - 2);
      }
    }

    /**
     * Draws the icon.
     *
     * @param c		the component to draw it on
     * @param g		the graphics context
     * @param x		the x position
     * @param y		the y position
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
      int		h;
      int		w;
      float		width;
      int		margin;
      Graphics2D 	g2d;

      if (m_Icon == null)
	return;

      w      = getIconWidth();
      h      = getIconHeight();
      g2d    = null;
      width  = 1.0f;
      margin = (int) (BORDER_MARGIN * m_ScaleFactor);

      paintBackground(g, x, y, w, h);

      // set width
      if (g instanceof Graphics2D) {
	g2d = (Graphics2D) g;
	if (g2d.getStroke() instanceof BasicStroke)
	  width = ((BasicStroke) g2d.getStroke()).getLineWidth();
	g2d.setStroke(new BasicStroke(1.0f * (float) m_ScaleFactor));
      }

      // visual cues
      g.setColor(COLOR_VISUALCUES);

      if (m_HasSubActors && m_HasInput && m_ForwardsInput)
	g.drawLine(x + w, y + h / 2, x + w + margin, y + h / 2);  // right

      if (m_Execution != ActorExecution.UNDEFINED) {
	switch (m_Execution) {
	  case SEQUENTIAL:
	    if (m_HasInput)
	      g.drawLine(x + w / 2, y, x + w / 2, 0);                                    // top
	    if (m_HasOutput)
	      g.drawLine(x + w / 2, y + h, x + w / 2, c.getPreferredSize().height - 1);  // bottom
	    break;

	  case PARALLEL:
	    if (m_HasInput)
	      g.drawLine(x - margin, y + h / 2, x, y + h / 2);          // left
	    if (m_HasOutput)
	      g.drawLine(x + w, y + h / 2, x + w + margin, y + h / 2);  // right
	    break;
	}
      }

      m_Icon.paintIcon(c, g, x, y);

      if (m_Deprecated) {
	g.setColor(COLOR_DEPRECATED);
	g.drawLine(x, y,     x + w, y + h);
	g.drawLine(x, y + h, x + w, y);
      }

      // restore width
      if (g2d != null)
	g2d.setStroke(new BasicStroke(width));
    }

    /**
     * Returns the underlying icon.
     *
     * @return		the actual icon
     */
    public ImageIcon getIcon() {
      return m_Icon;
    }

    /**
     * Returns the scale factor in use.
     *
     * @return		the scale factor
     */
    public double getScaleFactor() {
      return m_ScaleFactor;
    }

    /**
     * Sets how the visual cues are painted.
     *
     * @param value	how to paint the cues
     */
    public void setExecution(ActorExecution value) {
      m_Execution = value;
    }

    /**
     * Returns how to paint the visual cues.
     *
     * @return		how to paint the visual cues
     */
    public ActorExecution getExecution() {
      return m_Execution;
    }

    /**
     * Returns whether the actor has debugging enabled.
     *
     * @return		true if the actor has debugging enabled
     */
    public boolean getDebugOn() {
      return m_DebugOn;
    }

    /**
     * Returns whether the actor accepts input.
     *
     * @return		true if the actor accepts input
     */
    public boolean hasInput() {
      return m_HasInput;
    }

    /**
     * Returns whether the actor generates output.
     *
     * @return		true if the actor generates output
     */
    public boolean hasOutput() {
      return m_HasOutput;
    }

    /**
     * Returns whether the actor has sub-actors.
     *
     * @return		true if the actor manages actors
     */
    public boolean hasSubActors() {
      return m_HasSubActors;
    }

    /**
     * Returns whether the actor forwards the input to its sub-actors.
     *
     * @return		true if the actor forwards the data to its sub-actors
     */
    public boolean getForwardsInput() {
      return m_ForwardsInput;
    }

    /**
     * Returns whether the actor is deprecated.
     *
     * @return		true if the actor is deprecated
     */
    public boolean isDeprecated() {
      return m_Deprecated;
    }

    /**
     * The icon width.
     *
     * @return		the width
     */
    public int getIconWidth() {
      if (m_Icon == null)
	return 0;
      else
	return m_Icon.getIconWidth();
    }

    /**
     * The icon height.
     *
     * @return		the height
     */
    public int getIconHeight() {
      if (m_Icon == null)
	return 0;
      else
	return m_Icon.getIconHeight();
    }

    /**
     * Returns a short string description of the icon.
     *
     * @return		the description
     */
    @Override
    public String toString() {
      return "execution=" + m_Execution + ", input=" + m_HasInput + ", output=" + m_HasOutput;
    }
  }

  /**
   * A wrapper for icons in the tree to make them look disabled (light gray
   * background).
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class DisabledIcon
    extends ActorIcon {

    /**
     * Initializes the icon.
     *
     * @param icon		the actual icon
     */
    public DisabledIcon(ActorIcon icon) {
      this(icon.getIcon(), icon.getScaleFactor(), icon.getExecution(), icon.getDebugOn(), icon.hasInput(), icon.hasOutput(), icon.hasSubActors(), icon.getForwardsInput(), icon.isDeprecated());
    }

    /**
     * Initializes the icon.
     *
     * @param icon		the actual icon
     * @param scaleFactor	the scale factor (1.0 = default size)
     * @param execution		how to paint the visual cues
     * @param debugOn		whether the actor has debugging enabled
     * @param hasInput		whether the actor accepts input
     * @param hasOutput		whether the actor generates output
     * @param hasSubActors	whether the actor manages actors
     * @param forwardsInput	whether the actor forwards the input to its sub-actors
     * @param deprecated	whether the actor is deprecated
     */
    public DisabledIcon(ImageIcon icon, double scaleFactor, ActorExecution execution, boolean debugOn, boolean hasInput, boolean hasOutput, boolean hasSubActors, boolean forwardsInput, boolean deprecated) {
      super(icon, scaleFactor, execution, debugOn, hasInput, hasOutput, hasSubActors, forwardsInput, deprecated);
    }

    /**
     * Pains the background of the icon.
     *
     * @param g		the graphics context
     * @param x		the x position
     * @param y		the y position
     * @param w		the width of the icon
     * @param h		the height of the icon
     */
    @Override
    protected void paintBackground(Graphics g, int x, int y, int w, int h) {
      if (m_DebugOn)
	g.setColor(BACKGROUND_RUNNING_DEBUG);
      else
	g.setColor(BACKGROUND_RUNNING);
      g.fillRect(x + 1, y + 1, w - 2, h - 2);
    }
  }

  /** the prefix for the icons. */
  public final static String ICON_PREFIX = "adams.flow.";

  /** the border margin. */
  public final static int BORDER_MARGIN = 3;

  /** stores the classname/icon relationship. */
  protected Hashtable<String,ImageIcon> m_Icons;

  /** the scaling factor for the icons. */
  protected double m_ScaleFactor;

  /**
   * Initializes the renderer with scale factor of "1.0".
   */
  public Renderer() {
    this(1.0);
  }

  /**
   * Initializes the renderer.
   *
   * @param scaleFactor	the scale factor for text/icon
   */
  public Renderer(double scaleFactor) {
    super();

    m_Icons       = new Hashtable<>();
    m_ScaleFactor = scaleFactor;
  }

  /**
   * Returns the current scale factor for text/icon.
   *
   * @return		the scale factor
   */
  public double getScaleFactor() {
    return m_ScaleFactor;
  }

  /**
   * Tries to obtain the icon for the given actor.
   *
   * @param parent	the parent actor, null if root
   * @param actor	the actor get the icon for
   * @param collapsed	whether the node is collapsed
   * @return		the associated icon or null if not found
   */
  protected ActorIcon getIcon(AbstractActor parent, AbstractActor actor, boolean collapsed) {
    ActorIcon		result;
    ImageIcon		icon;
    String		classname;
    ActorExecution	execution;

    result     = null;
    icon       = null;
    classname  = actor.getClass().getName();
    execution  = ActorExecution.UNDEFINED;
    if ((parent != null) && (parent instanceof ActorHandler))
      execution = ((ActorHandler) parent).getActorHandlerInfo().getActorExecution();

    if (m_Icons.containsKey(classname)) {
      icon = m_Icons.get(classname);
    }
    else {
      try {
	icon = GUIHelper.getIcon(actor.getClass());
      }
      catch (Exception e) {
	if (ActorUtils.isControlActor(actor))
	  icon = GUIHelper.getIcon(ICON_PREFIX + "control.Unknown.gif");
	else if (ActorUtils.isStandalone(actor))
	  icon = GUIHelper.getIcon(ICON_PREFIX + "standalone.Unknown.gif");
	else if (ActorUtils.isSource(actor))
	  icon = GUIHelper.getIcon(ICON_PREFIX + "source.Unknown.gif");
	else if (ActorUtils.isTransformer(actor))
	  icon = GUIHelper.getIcon(ICON_PREFIX + "transformer.Unknown.gif");
	else if (ActorUtils.isSink(actor))
	  icon = GUIHelper.getIcon(ICON_PREFIX + "sink.Unknown.gif");
      }

      if (icon != null) {
	if (m_ScaleFactor != 1.0) {
	  icon = new ImageIcon(icon.getImage().getScaledInstance(
	      (int) (icon.getIconWidth()  * m_ScaleFactor),
	      (int) (icon.getIconHeight() * m_ScaleFactor),
	      Image.SCALE_SMOOTH));
	}
	m_Icons.put(classname, icon);
      }
    }

    if (icon != null) {
      result = new ActorIcon(
	icon,
	m_ScaleFactor,
	execution,
	actor.isLoggingEnabled(),
	ActorUtils.isSink(actor) || ActorUtils.isTransformer(actor),
	ActorUtils.isSource(actor) || ActorUtils.isTransformer(actor),
	ActorUtils.isActorHandler(actor),
	ActorUtils.isActorHandler(actor) && ((ActorHandler) actor).getActorHandlerInfo().getForwardsInput(),
	(actor.getClass().getAnnotation(Deprecated.class) != null));
    }

    return result;
  }

  /**
   * For rendering the cell.
   *
   * @param tree		the tree
   * @param value		the node
   * @param sel		whether the element is selected
   * @param expanded	whether the node is expanded
   * @param leaf		whether the node is a leaf
   * @param row		the row in the tree
   * @param hasFocus	whether the node is focused
   * @return		the rendering component
   */
  @Override
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    setFont(getFont().deriveFont((int) (12 * m_ScaleFactor)));

    // icon available?
    ActorIcon icon = null;
    if (value instanceof Node) {
      Node cNode = (Node) value;
      Node pNode = (Node) cNode.getParent();
      AbstractActor parent = null;
      if (pNode != null)
	parent = pNode.getActor();
      icon = getIcon(parent, ((Node) value).getActor(), (!expanded && !leaf));
      if (icon != null) {
	if (tree.isEditable() && tree.isEnabled() && cNode.isEditable()) {
	  setIcon(icon);
	}
	else if (!tree.isEditable() || !cNode.isEditable()) {
	  icon = new DisabledIcon(icon);
	  setIcon(icon);
	}
	else if (!tree.isEnabled()) {
	  icon = new DisabledIcon(icon);
	  setDisabledIcon(icon);
	}
        int margin = (int) (BORDER_MARGIN * m_ScaleFactor);
        setBorder(BorderFactory.createEmptyBorder(margin, margin, margin, margin));
      }
    }

    return this;
  }
}