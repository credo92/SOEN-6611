package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ast.ClassObject;
import ast.ConstructorObject;
import ast.FieldInstructionObject;
import ast.MethodObject;
import ast.SystemObject;
import ast.TypeObject;

public class CAMC {

	 private List<ConstructorObject> allDeclConst;
	 private List<MethodObject> allMethods;
	 private HashSet<String> allParams=new HashSet<String>();
	 private HashSet<String> ParamsinMethod=new HashSet<String>();
	 private HashMap<String, Integer> POMatrix= new HashMap<>();
	 private HashMap<String, List<String>> POMatrix2= new HashMap<>();

	 private Map<String, Double[]> camcMap;

		public CAMC(SystemObject system) {
			camcMap = new HashMap<String, Double[]>();
			
			Set<ClassObject> classes = system.getClassObjects();
			
			for(ClassObject classObject : classes) {
				allParams=new HashSet<String>();
				POMatrix= new HashMap<>();
				POMatrix2= new HashMap<>();
				double camc = computeCAMC(classObject);
				double commmonAttrSize= computeCommonAttr(classObject);
				Double[] values= new Double[]{camc, commmonAttrSize};

				camcMap.put(classObject.getName(), values);
				
			}
			
		}
		
		private double computeCAMC(ClassObject classObject) {
			computeMethodPO(classObject);
			computeConstructorsPO(classObject);
			
			return calculateClassCAMC();
		}
	 
	 public void computeMethodPO(ClassObject c) {
	        allMethods = c.getMethodList();
	        for (MethodObject m : allMethods) {
	        	ParamsinMethod=new HashSet<String>();
	        	
	        	List<TypeObject> params = m.getParameterTypeList();
	            for (int i = 0; i < params.size(); i++) {
	            	if(params.get(i).getClassType()!=c.getClass().getTypeName())
	            	{
		            	ParamsinMethod.add(params.get(i).getClassType());
		            	allParams.add(params.get(i).getClassType());
	            	}
	            }

	        	POMatrix.put(m.getName(), (int) ParamsinMethod.stream().distinct().count()+1);
	            POMatrix2.put(m.getName(), ParamsinMethod.stream().distinct().collect(Collectors.toList()));
	        }        
	    }
	
	
	public void computeConstructorsPO(ClassObject c) {
      
        allDeclConst = c.getConstructorList();
        for (ConstructorObject currentDeclConst : allDeclConst) {
        	ParamsinMethod=new HashSet<String>();
        	
        	List<TypeObject> params = currentDeclConst.getParameterTypeList();
            for (int i = 0; i < params.size(); i++) {
            	if(params.get(i).getClassType()!=c.getClass().getTypeName())
            	{
	            	ParamsinMethod.add(params.get(i).getClassType());
	            	allParams.add(params.get(i).getClassType());
            	}
            }

            POMatrix.put(currentDeclConst.getName(), (int) ParamsinMethod.stream().distinct().count()+1);
            POMatrix2.put(currentDeclConst.getName(), ParamsinMethod.stream().distinct().collect(Collectors.toList()));
            
        }          
    }
	
	
	private int computeCommonAttr(ClassObject classObject) {
		
		List<MethodObject> methods = classObject.getMethodList();
		List<ConstructorObject> constructors = classObject.getConstructorList();
		
		Set<Object> commAttr = new HashSet<Object>();
		if((methods.size()+constructors.size()) < 2) {
			return 0;
		}
		
		for(int i=0; i<methods.size()-1; i++) {
			MethodObject mI = methods.get(i);
			List<Object> attributesI = mI.getParameterTypeList().stream().distinct().collect(Collectors.toList());
			for(int j=i+1; j<methods.size(); j++) {
				MethodObject mJ = methods.get(j);
				List<Object> attributesJ = mJ.getParameterTypeList().stream().distinct().collect(Collectors.toList());
				
				commAttr.addAll(commonAttributes(attributesI, attributesJ, classObject.getName()));
			}
		}
		for(int i=0; i<constructors.size()-1; i++) {
			ConstructorObject mI = constructors.get(i);
			List<Object> attributesI = mI.getParameterTypeList().stream().distinct().collect(Collectors.toList());
			for(int j=i+1; j<constructors.size(); j++) {
				ConstructorObject mJ = constructors.get(j);
				List<Object> attributesJ = mJ.getParameterTypeList().stream().distinct().collect(Collectors.toList());
				
				commAttr.addAll(commonAttributes(attributesI, attributesJ, classObject.getName()));
			}
		}
		
		return (int) commAttr.stream().distinct().count();
	}
		
		private Set<Object> commonAttributes(List<Object> attributesI,
				List<Object> attributesJ, String className) {
			
			Set<Object> commonAttributes = new HashSet<Object>();
			for (Object instructionI : attributesI) {
				if(attributesJ.contains(instructionI)) {
					commonAttributes.add(instructionI);
				}
			}
			return commonAttributes;
		}
    
	
	public int calcK(){
		int k = allDeclConst.size() + allMethods.size();
		return k;
	}
	
	public int calcL(){
		int l = (int) allParams.stream().distinct().count();

		return l+1;
	}
	
	public double calculateClassCAMC()
	{
		double denom= calcK() * calcL();
		double num=0;
		for(int i : POMatrix.values())
		{
			num+=i;
		}
		if(denom<=0)
			return 0;
		double camc= num/denom;
		return camc;
	}
	
	public double calculateSystemCAMC()
	{
		double sum=0;
		int totalClasses=camcMap.keySet().size();
		
		for(String key : camcMap.keySet()) {
			sum+= camcMap.get(key)[0];
		}
		
		double sysCAMC= sum /totalClasses;
		return sysCAMC;
	}
		
	public double calculateSystemNoOfCommonParams()
	{
		double sum=0;
		int totalClasses=camcMap.keySet().size();
		
		for(String key : camcMap.keySet()) {
			sum+= camcMap.get(key)[1];
		}
		
		double sysCommAttr= sum /totalClasses;
		return sysCommAttr;
	}
	
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String key : camcMap.keySet()) {
			sb.append(key).append("\t").append("[CAMC: ").append(camcMap.get(key)[0]).append(", No of Common Attr: {").append(camcMap.get(key)[1]).append("}]").append("\n");
		}
		sb.append("\n ********System Avg CAMC value is: ["+calculateSystemCAMC()+"] ********").append("\n");
		sb.append("\n ********System Avg No of Common Parameters is: ["+calculateSystemNoOfCommonParams()+"] ********").append("\n");
		sb.append("\n Total Classes:" + camcMap.keySet().size());
		return sb.toString();
	}
	
}
