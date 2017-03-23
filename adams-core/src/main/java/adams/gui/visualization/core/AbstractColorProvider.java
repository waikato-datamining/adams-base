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
 * AbstractColorProvider.java
 * Copyright (C) 2011-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.core;

import adams.core.option.AbstractOptionConsumer;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.ArrayConsumer;
import adams.core.option.OptionUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for providing colors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractColorProvider
  extends AbstractOptionHandler
  implements ColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = 1159553726314921425L;

  /** the minimum value for R, G and B before restarting the color provider. */
  public final static int MIN_VALUE = 64;

  /** the current index. */
  protected int m_Index;

  /** whether it is the first iteration. */
  protected boolean m_FirstIteration;

  /** contains the current colors. */
  protected List<Color> m_Colors;

  /** contains the default colors. */
  protected List<Color> m_DefaultColors;

  /** recycled, i.e., returned colors. */
  protected List<Color> m_RecycledColors;

  /** excluded colors (already used). */
  protected List<Color> m_ExcludedColors;

  /** whether darkening of colors is allowed. */
  protected boolean m_AllowDarkening;

  /** whether to ensure that colors aren't too dark. */
  protected boolean m_CheckTooDark;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DefaultColors  = new ArrayList<>();
    m_Colors         = new ArrayList<>();
    m_RecycledColors = new ArrayList<>();
    m_ExcludedColors = new ArrayList<>();
    m_AllowDarkening = true;
    m_CheckTooDark   = true;
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    resetColors();
  }

  /**
   * Returns the next color.
   *
   * @return		the next color
   */
  public synchronized Color next() {
    Color	result;

    if (m_DefaultColors.size() == 0)
      throw new IllegalStateException("No more colors left!");

    result = null;

    while (result == null) {
      if (m_RecycledColors.size() > 0) {
	result = m_RecycledColors.get(0);
	m_RecycledColors.remove(0);
      }
      else {
	result = m_Colors.get(m_Index);

	// create next color
	if (!m_FirstIteration) {
	  result = result.darker();
	  m_Colors.set(m_Index, result);
	}

	// increment index
	m_Index++;
	if (m_Index >= m_Colors.size()) {
	  m_Index = 0;
	  if (m_AllowDarkening)
	    m_FirstIteration = false;
	}
      }

      if (m_ExcludedColors.contains(result))
	result = null;
    }

    // too dark? -> restart
    if (m_CheckTooDark) {
      if (    (result.getRed() < MIN_VALUE)
	  && (result.getGreen() < MIN_VALUE)
	  && (result.getBlue() < MIN_VALUE) ) {
	resetColors();
	result = m_Colors.get(m_Index);
      }
    }

    if (isLoggingEnabled())
      getLogger().info("next color: " + result);

    return result;
  }

  /**
   * Resets the colors.
   */
  public synchronized void resetColors() {
    m_Index          = 0;
    m_FirstIteration = true;

    m_Colors.clear();
    m_Colors.addAll(m_DefaultColors);
    m_RecycledColors.clear();
    m_ExcludedColors.clear();

    if (isLoggingEnabled())
      getLogger().info("reset of colors");
  }

  /**
   * "Recycles" the specified colors, i.e., makes it available for future use.
   *
   * @param c		the color to re-use
   */
  public synchronized void recycle(Color c) {
    m_RecycledColors.add(c);
  }

  /**
   * "Excludes" the specified colors, i.e., makes it unavailable for future use.
   *
   * @param c		the color to exclude
   */
  public synchronized void exclude(Color c) {
    m_ExcludedColors.add(c);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @return		the shallow copy
   */
  public ColorProvider shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public ColorProvider shallowCopy(boolean expand) {
    return (ColorProvider) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Instantiates the provider with the given options.
   *
   * @param classname	the classname of the provider to instantiate
   * @param options	the options for the provider
   * @return		the instantiated provider or null if an error occurred
   */
  public static ColorProvider forName(String classname, String[] options) {
    ColorProvider	result;

    try {
      result = (ColorProvider) OptionUtils.forName(ColorProvider.class, classname, options);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Instantiates the provider from the given commandline
   * (i.e., classname and optional options).
   *
   * @param cmdline	the classname (and optional options) of the
   * 			provider to instantiate
   * @return		the instantiated provider
   * 			or null if an error occurred
   */
  public static ColorProvider forCommandLine(String cmdline) {
    return (ColorProvider) AbstractOptionConsumer.fromString(ArrayConsumer.class, cmdline);
  }
}
