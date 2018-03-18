package metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import ast.ClassObject;
import ast.ConstructorObject;
import ast.MethodObject;
import ast.SystemObject;
import ast.decomposition.CompositeStatementObject;
import ast.decomposition.MethodBodyObject;


public class WMC {

	private int wmcSystem;
	private HashMap<String, Integer> wmcMap = new HashMap<String, Integer>();
	public WMC(SystemObject system) {
		wmcSystem=0;
		Set<ClassObject> classes = system.getClassObjects();
		
		for(ClassObject classObject : classes) {
			if(classObject !=null){
				int  wmcClass = computeWMC(classObject);
				wmcSystem+= wmcClass;
				wmcMap.put(classObject.getName(), wmcClass);
			}
		}
	}

	private int computeWMC(ClassObject classObject){
		int noOfPredicateStatements = 0;
		List<MethodObject> methods = classObject.getMethodList();
		List<ConstructorObject> constructors = classObject.getConstructorList();

		if(methods.isEmpty())
			return 0;

		for(MethodObject methodObject: methods){
			MethodBodyObject methodBodyObject = methodObject.getMethodBody();
			
			if(methodBodyObject != null){
				CompositeStatementObject compStatementObject = methodBodyObject.getCompositeStatement();
				noOfPredicateStatements = noOfPredicateStatements + compStatementObject.getIfStatements().size()+ 
						compStatementObject.getSwitchStatements().size()+ 
						compStatementObject.getWhileStatements().size()+
						compStatementObject.getSWITCH_CASEStatement().size()+ 
						compStatementObject.getEnhancedForStatement().size()+
						compStatementObject.getForStatement().size()+
						compStatementObject.getDoStatements().size()+
						compStatementObject.getTernaryStatements().size()+
						compStatementObject.getSuperConstructorInvocationStatements().size()+
						compStatementObject.getTryStatements().size()+
						1;
	
			}
		
		}

		for(ConstructorObject constructorObject: constructors){
			MethodBodyObject constructorBodyObject = constructorObject.getMethodBody();
			
			if(constructorBodyObject != null){
				CompositeStatementObject compStatementObject = constructorBodyObject.getCompositeStatement();
				noOfPredicateStatements = noOfPredicateStatements + compStatementObject.getIfStatements().size()+ 
						compStatementObject.getSwitchStatements().size()+ 
						compStatementObject.getWhileStatements().size()+
						compStatementObject.getSWITCH_CASEStatement().size()+ 
						compStatementObject.getEnhancedForStatement().size()+
						compStatementObject.getForStatement().size()+
						compStatementObject.getDoStatements().size()+
						compStatementObject.getTernaryStatements().size()+
						compStatementObject.getSuperConstructorInvocationStatements().size()+
						compStatementObject.getTryStatements().size()+
						1;
			}
			
		}
		return noOfPredicateStatements;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String key : wmcMap.keySet()) {
			sb.append(key).append("\t").append("[WMC: ").append(wmcMap.get(key)).append("]").append("\n");
		}
		sb.append("\n ********System WMC value is: ["+wmcSystem+"] ********").append("\n");
		return sb.toString();
	}


}
