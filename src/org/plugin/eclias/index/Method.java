package org.plugin.eclias.index;



import java.util.HashMap;



import org.eclipse.jdt.core.IMethod;


public class Method {
	static HashMap<IMethod, Method> methods = new HashMap<IMethod, Method>();
	
	 public Method(IMethod iMethod) {
		// TODO Auto-generated constructor stub
	}

	static Method getMethod(IMethod iMethod) {
		if (!methods.containsKey(iMethod)) {
			
			methods.put(iMethod, new Method(iMethod) );
		}
		return methods.get(iMethod);
	}

}
