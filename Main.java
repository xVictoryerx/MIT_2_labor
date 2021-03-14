package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		ArrayList<VariableDefinition> varDefs = new ArrayList<>();
		ArrayList<EventDefinition> evDefs = new ArrayList<>();
		while (iterator.hasNext()) {
			EObject content = iterator.next();

			if(content instanceof EventDefinition) {
				EventDefinition evdef = (EventDefinition) content;
				evDefs.add(evdef);
				/*String monogram = evdef.getName().toUpperCase().substring(0, 2);
				System.out.println("System.out.println(\"" + monogram +
						" = \" + s.getSCInterface().get" + evdef.getName() + "());");*/
			}
			if(content instanceof VariableDefinition) {
				VariableDefinition vardef = (VariableDefinition) content;
				varDefs.add(vardef);
			}
		}
		System.out.println("public class RunStatechart {\n");
		System.out.println("public static void main(String[] args) throws IOException {");
		System.out.println("BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));");
		System.out.println("ExampleStatemachine s = new ExampleStatemachine();");
		System.out.println("s.setTimer(new TimerService());");
		System.out.println("RuntimeService.getInstance().registerStatemachine(s, 200);");
		System.out.println("s.init();");
		System.out.println("s.enter();");
		System.out.println("s.runCycle();");
		System.out.println("boolean run = true;");
		System.out.println("while(run) {");
		System.out.println("String command = reader.readLine();");
		System.out.println("switch(command) {");
		for(int i = 0; i < evDefs.size(); ++i) {
			String evName = evDefs.get(i).getName();
			String mono = evName.substring(0,1).toUpperCase();
			System.out.println("case \"" + evName + "\":");
			evName = evName.substring(1);
			System.out.println("s.raise" + mono + evName + "();");
			System.out.println("s.runCycle();");
			System.out.println("break;");
		}
		System.out.println("case \"exit\":");
		System.out.println("run = false;");
		System.out.println("break;");
		System.out.println("default:");
		System.out.println("System.out.println(\"Unrecognised command.\");");
		System.out.println("break;");
		System.out.println("}");
		System.out.println("print(s);");
		System.out.println("}");
		System.out.println("reader.close();");
		System.out.println("System.exit(0);");
		System.out.println("}");
		System.out.println("public static void print(IExampleStatemachine s) {");
		for(int i = 0; i < varDefs.size(); ++i) {
			String varName = varDefs.get(i).getName();
			String mono = varName.toUpperCase().substring(0,1);
			System.out.println("System.out.println(\"" + mono +
					" = \" + s.getSCInterface().get" + varName + "());");
		}
		System.out.println("}");
		System.out.println("}");

		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
