package adams.ml.requirement;

import adams.ml.BaseData;

/**
 * 
 * @author dale
 *
 */
public class ReqIntegerValue extends ReqInteger {
  
  public int m_value;
  /**
   * 
   * @param min
   * @param max
   */
  public ReqIntegerValue(int value){
    m_value=value;
  }
  
  /**
   * 
   */
  @Override
  public String checkRequirement(BaseData bd) {
    if (bd == null || bd.getData() == null){
      return(null);
    }
    String res=super.checkRequirement(bd);
    if (res != null){
      return(res);
    }
    if ((Integer)bd.getData() != m_value){
      return("Not equal "+m_value);
    }
    return(null);
  }
}
