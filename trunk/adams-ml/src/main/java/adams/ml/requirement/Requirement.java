package adams.ml.requirement;

import adams.ml.BaseData;

public interface Requirement {
  /**
   * Checks if cell is ok.
   * If ok, return null 
   * else return error String
   * 
   * Sets cell if possible (i.e string representation of int->int
   * 
   * @param bd	cell
   * @return	error string, or null
   */
  public String checkRequirement(BaseData bd);
 
}
