package fr.cnrs.iees.twcore.generators.odd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Component;
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LifeCycle;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.ComponentType;
import au.edu.anu.twcore.ecosystem.structure.GroupType;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

import static au.edu.anu.rscs.aot.util.StringUtils.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Utility class to generate various UML diagrams from the analysis of the configuration graph.
 * 
 * 
 * @author Jacques Gignoux - 7 oct. 2021
 *
 */
public class UMLGenerator {
	
	private static String umlIndent = "  ";
	private static String umlBlockOpen = "@startuml";
	private static String umlBlockClose = "@enduml";
	private static String flowChartStart = "start";
	private static String flowChartStop = "stop";
	
	private List<String> umlText = new ArrayList<>();
	
	public UMLGenerator() {
		super();
	}

	/**
	 * Generate an activity (=flow) diagram.
	 * 
	 * @param flow a sequence of steps produced by {@link TwConfigurationAnalyser}.
	 */
	public void activityDiagram(List<TwConfigurationAnalyser.ExecutionStep> flow) {
		// split flow in two parts, initialisation and simulation
		List<TwConfigurationAnalyser.ExecutionStep> initflow = new ArrayList<>();
		List<TwConfigurationAnalyser.ExecutionStep> simflow = new ArrayList<>();
		for (TwConfigurationAnalyser.ExecutionStep step:flow) {
			if (step.level==TwConfigurationAnalyser.ExecutionLevel.init)
				initflow.add(step);
			else
				simflow.add(step);
		}
		umlText.add(umlBlockOpen);
		umlText.add(flowChartStart);
		umlText.add("repeat");
		umlText.add(umlIndent+":initialisation;");
		writeInitBlock(umlIndent,initflow);
		umlText.add(umlIndent+":simulation;");
		umlText.add(umlIndent+"repeat"); // time loop
		writeSimulBlock(umlIndent+umlIndent,simflow);
		umlText.add(umlIndent+"repeatwhile (stoppingCondition?)"); // end time loop. todo: extract the stopping cd?
		umlText.add("repeatwhile (more runs?)");
		umlText.add(flowChartStop);
		umlText.add(umlBlockClose);
		// debug
		for (String s:umlText)
			System.out.println(s);
	}
	
	
	// get the category signature (without predefs) of an ElementType
	private String getMeaningfulCategory(TreeGraphDataNode node) {		
		Collection<Category> cats = Categorized.getSuperCategories(node);
		SortedSet<Category> cset = new TreeSet<>();
		cset.addAll(cats);
		String result = "";
		for (Category c:cset) {
			switch (c.id()) {
				case Category.assemblage:
				case Category.atomic:
				case Category.permanent:
				case Category.ephemeral:
				break;
				default:
					result = result+c.id()+Categorized.CATEGORY_SEPARATOR;
				break;
			}
		}
		result = result.substring(0,result.length()-1);
		return result;
	}

	// NB we only enter here if the collection of items is of size 1
	@SuppressWarnings("unchecked")
	private String getElementId(TreeGraphDataNode eltype) {
		Collection<TreeGraphDataNode> children = (Collection<TreeGraphDataNode>) eltype.getChildren();
		String result = null;
		for (TreeGraphDataNode theChild:children) {
			if (eltype instanceof ArenaType)
				result = eltype.id();
			if ( ((eltype instanceof LifeCycleType)&&(theChild instanceof LifeCycle)) ||
				((eltype instanceof GroupType)&&(theChild instanceof Group)) ||
				((eltype instanceof ComponentType)&&(theChild instanceof Component)) )
				result = theChild.id();
		}
		return result;
	}
	
	private void writeInitBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> flow) {
		// here, we ALWAYS have level=init
		for (TwConfigurationAnalyser.ExecutionStep step:flow) {
			switch (step.looping) {
			case parallel:
				umlText.add(indent+"while(for each **"+getMeaningfulCategory(step.applyTo)+"**)");
				umlText.add(indent+indent+":"+uncap(step.node.id())+"();");
				umlText.add(indent+"endwhile");
				break;
			case sequential:
				umlText.add(indent+"while(for each **"+getMeaningfulCategory(step.applyTo)+"**)");
				umlText.add(indent+indent+":"+uncap(step.node.id())+"();");
				umlText.add(indent+"endwhile");
				break;
			case unique:
				String id = getElementId(step.applyTo);
				umlText.add(indent+":"+id+"."+uncap(step.node.id())+"();");
				break;
			default:
				break;
			}
		}
	}
	
	private void writeSimulBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> flow) {
		List<TwConfigurationAnalyser.ExecutionStep> timers = new ArrayList<>();
		List<TwConfigurationAnalyser.ExecutionStep> steps = new ArrayList<>();
		for (TwConfigurationAnalyser.ExecutionStep step:flow) {
			if (step.node!=null) {
				if (step.node.classId().equals(N_TIMER.label()))
					timers.add(step);
				else 
					steps.add(step);
			}
		}
		if (timers.size()==1) {
			TwConfigurationAnalyser.ExecutionStep step = timers.get(0);
			// get the time unit from the timer timeline
			umlText.add(indent+"while(//t//<sub>i+1</sub> = "+ step.node.id()+".nextTime(//t//<sub>i</sub>))");			
			writeProcessBlock(indent+indent,steps);
			umlText.add(indent+"endwhile");
		}
		else {
			umlText.add(indent+"://t//<sub>i+1</sub> = +∞;");
			umlText.add(indent+"while(for each **timer**)");
			umlText.add(indent+indent+"://t//<sub>i+1</sub> = min(//t//<sub>i+1</sub>,**timer**.nextTime(//t//<sub>i</sub>));");
			umlText.add(indent+"endwhile");
			umlText.add(indent+"while(for each **timer**)");
			umlText.add(indent+indent+":if (**timer**.nextTime(//t//<sub>i</sub>) == //t//<sub>i+1</sub>)\nactivate **timer**;");
			umlText.add(indent+"endwhile");
			umlText.add(indent+"while(for each //active// **timer**)");
			writeProcessBlock(indent+indent,steps);
			umlText.add(indent+"endwhile");
		}
	}
	
	private void writeProcessBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> procs) {
		umlText.add(indent+":doSomething;");	
	}

}
