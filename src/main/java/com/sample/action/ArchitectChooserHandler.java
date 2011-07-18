package com.sample.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import sun.security.action.GetLongAction;

public class ArchitectChooserHandler implements ActionHandler {

	private static final long serialVersionUID = 1L;
	
	/**
	 * The message member gets its value from the configuration in the 
	 * processdefinition. The value is injected directly by the engine. 
	 */
	String message;

	/**
	 * A message process variable is assigned the value of the message
	 * member. The process variable is created if it doesn't exist yet.
	 */
	public void execute(ExecutionContext context) throws Exception {
		System.out.println("Handler ok");
		String product = (String) context.getContextInstance().getVariable("product");
		if("portal".equals(product)) {
			context.getContextInstance().setVariable("architect", "rasgabucho");
		} else {
			context.getContextInstance().setVariable("architect", "poucastrancas");
		}
	}

}
