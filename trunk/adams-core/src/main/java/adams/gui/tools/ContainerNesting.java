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
 * ContainerNesting.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import java.awt.Component;
import java.awt.Container;

import adams.env.Environment;
import adams.gui.core.BaseTree;
import adams.gui.core.BaseTreeNode;

/**
 * Generates a tree structure from a Swing container.
 * <p/>
 * A class name can be provided on the command-line. The provided class 
 * requires a default constructor in order to be analyzed.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ContainerNesting {

  /**
   * Generates the label for the component.
   * 
   * @param comp	the component to generate a label for
   * @return		the label
   */
  protected static String getLabel(Component comp) {
    String	result;
    Container	cont;
    
    result = comp.getClass().getName();
    if (comp instanceof Container) {
      cont = (Container) comp;
      if (cont.getLayout() != null)
	result += " [" + cont.getLayout().getClass().getName() + "]";
      else
	result += " [no layout manager]";
    }
    result += ": " + (comp.isVisible() ? "visible" : "hidden");
    
    return result;
  }
  
  /**
   * Analyzes the given Swing container.
   * 
   * @param parent	the parent in the tree, use null for root
   * @param comp	the component to analyze
   * @return		the parent
   */
  protected static BaseTreeNode analyze(BaseTreeNode parent, Component comp) {
    BaseTreeNode	result;
    Container		cont;
    int			i;

    result = new BaseTreeNode(getLabel(comp));
    if (parent != null)
      parent.add(result);
    
    if (comp instanceof Container) {
      cont = (Container) comp;
      for (i = 0; i < cont.getComponentCount(); i++)
	analyze(result, cont.getComponent(i));
    }
    
    return result;
  }

  /**
   * Analyzes the given Swing container.
   * 
   * @param cont	the container to analyze
   * @return		the generated tree of the nested elements
   */
  public static BaseTree analyze(Container cont) {
    BaseTree		result;
    BaseTreeNode	root;
    
    root   = analyze(null, cont);
    result = new BaseTree(root);
    
    return result;
  }
  
  /**
   * Analyzes the class defined by its classname as second parameter. The first
   * parameter is the environment class, e.g., adams.env.Environment.
   * 
   * @param args	the command-line parameters (environment class + container class)
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.err.println("\nUsage: " + ContainerNesting.class.getName() + " <environment class> <class-name>\n");
      System.exit(1);
    }
    
    // set up environment
    System.err.println("Setting up environment...");
    Environment.setEnvironmentClass(Class.forName(args[0]));
    
    // analyzing
    System.err.println("Analyzing container...");
    Container cont = (Container) Class.forName(args[1]).newInstance();
    BaseTree tree = analyze(cont);
    System.out.println(tree);
    
    System.exit(0);
  }
}
