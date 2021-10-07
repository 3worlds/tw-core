package fr.cnrs.iees.twcore.generators.odd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static au.edu.anu.rscs.aot.util.StringUtils.*;

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
	
	private void writeInitBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> flow) {
		// here, we ALWAYS have level=init
		for (TwConfigurationAnalyser.ExecutionStep step:flow) {
			switch (step.looping) {
			case parallel:
				umlText.add(indent+"while(for each **"+step.applyTo+"**)");
				umlText.add(indent+indent+":"+step.applyTo+"."+uncap(step.node.id())+"();");
				umlText.add(indent+"endwhile");
				break;
			case sequential:
				umlText.add(indent+"while(for each **"+step.applyTo+"**)");
				umlText.add(indent+indent+":"+step.applyTo+"."+uncap(step.node.id())+"();");
				umlText.add(indent+"endwhile");
				break;
			case unique:
				umlText.add(indent+":"+step.applyTo+"."+uncap(step.node.id())+"();");
				break;
			default:
				break;
			}
		}
	}
	
	private void writeSimulBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> flow) {
		umlText.add(indent+":doSomething;");
	}

}
