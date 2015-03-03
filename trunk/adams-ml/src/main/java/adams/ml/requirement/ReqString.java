package adams.ml.requirement;

import adams.ml.BaseData;

public class ReqString implements Requirement {

  
  @Override
  /**
   * Does not need to be present.
   * 
   */
  public String checkRequirement(BaseData bd) {
    if (bd == null || bd.getData() == null){
      return(null);
    }
    bd.setData(bd.getData().toString());
    return(null);
  }
  
  

}
