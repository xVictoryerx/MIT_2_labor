package hu.bme.mit.yakindu.analysis.workhere;

import java.util.ArrayList;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import hu.bme.mit.model2gml.Model2GML;
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
		ArrayList<State> trapNodes = new ArrayList<State>();
		ArrayList<State> anonymNodes = new ArrayList<State>();
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				if(state.getOutgoingTransitions().size() == 0)trapNodes.add(state);
				if(state.getName() == "")anonymNodes.add(state);
				System.out.println(state.getName());
			}
			if(content instanceof Transition) {
				Transition transition = (Transition) content;
				System.out.println(transition.getSource().getName() + " -> " + transition.getTarget().getName());
			}
		}
		if(trapNodes.size() > 0) {
			System.out.println("The following states do not have outgoing transitions:");
			for(int i = 0; i < trapNodes.size(); ++i) {
				System.out.println(trapNodes.get(i).getName());
			}
		}
		else System.out.println("All states have outgoing transitions.");
		if(anonymNodes.size() > 0) {
			System.out.println( "There are " + anonymNodes.size() + " states that lack names. Proposal:");
			for(int i = 0; i < anonymNodes.size(); ++i) {
				System.out.println("Anonymus_" + i);
			}
		}
		else System.out.println("All states have names.");
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
}
