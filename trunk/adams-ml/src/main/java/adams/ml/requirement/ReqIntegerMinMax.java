package adams.ml.requirement;

import adams.ml.BaseData;

/**
 * 
 * @author dale
 *
 */
public class ReqIntegerMinMax extends ReqInteger {
  
  public int m_min,m_max;
  /**
   * 
   * @param min
   * @param max
   */
  public ReqIntegerMinMax(int min, int max){
    m_min=min;
    m_max=max;
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
    if ((Integer)bd.getData() < m_min){
      return("Less than "+m_min);
    }
    if ((Integer)bd.getData() > m_max){
      return("Greater than "+m_max);
    }
    return(null);
  }
}
