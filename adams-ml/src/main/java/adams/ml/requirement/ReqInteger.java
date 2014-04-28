package adams.ml.requirement;

import adams.ml.BaseData;

public class ReqInteger implements Requirement {

  
  @Override
  /**
   * Does not need to be present.
   * 
   */
  public String checkRequirement(BaseData bd) {
    if (bd == null || bd.getData() == null){
      return(null);
    }
    Object o=bd.getData();
    if (o instanceof Integer){
      return(null);
    } else if (o instanceof String){
      try{
	Integer i=Integer.parseInt((String)o);
	bd.setData(i);
	return(null);
      } catch (Exception e){
	return("Unable to parse to Integer ("+(String)o+")");	
      }
    } else {
      return("Type Error: Expecting Integer got "+o.getClass().getCanonicalName());
    }
  }
  
  

}
