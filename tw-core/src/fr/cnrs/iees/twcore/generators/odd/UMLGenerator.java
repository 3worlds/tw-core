/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package fr.cnrs.iees.twcore.generators.odd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static au.edu.anu.omhtk.util.StringUtils.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

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
	// utility across methods
	private String arenaId = ""; 
	
	public UMLGenerator() {
		super();
	}
	
	public List<String> umlText() {
		return umlText;
	}

	public String umlString() {
		StringBuilder sb = new StringBuilder();
		for (String s:umlText)
			sb.append(s).append('\n');
		return sb.toString();
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
		writeInitBlock(umlIndent,initflow);
		umlText.add(umlIndent+"repeat"); // time loop
		writeSimulBlock(umlIndent+umlIndent,simflow);
		umlText.add(umlIndent+"repeatwhile (stoppingCondition?)"); // end time loop. todo: extract the stopping cd?
		umlText.add(flowChartStop);
		umlText.add(umlBlockClose);
//		// debug
//		for (String s:umlText)
//			System.out.println(s);
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
			if (eltype instanceof ArenaType) {
				result = eltype.id();
				arenaId = result;
			}
//			if ( ((eltype instanceof LifeCycleType)&&(theChild instanceof LifeCycle)) ||
//				((eltype instanceof GroupType)&&(theChild instanceof Group)) ||
//				((eltype instanceof ComponentType)&&(theChild instanceof Component)) )
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
			if (step.level==TwConfigurationAnalyser.ExecutionLevel.timer)
				timers.add(step);
			else 
				steps.add(step);
		}
		if (timers.size()==1) {
			TwConfigurationAnalyser.ExecutionStep step = timers.get(0);
			// get the time unit from the timer timeline
			umlText.add(indent+"while(//t//<sub>i+1</sub> = "+ step.node.id()+".nextTime(//t//<sub>i</sub>))");			
			writeProcessBlock(indent+indent,steps);
			umlText.add(indent+"://t//<sub>i</sub> = //t//<sub>i+1</sub>;");
			umlText.add(indent+"endwhile");
		}
		else {
			umlText.add(indent+"://t//<sub>i+1</sub> = +∞;");
			umlText.add(indent+"while(for each **timer**)");
			umlText.add(indent+indent+"://t//<sub>i+1</sub> = min(//t//<sub>i+1</sub>,**timer**.nextTime(//t//<sub>i</sub>));");
			umlText.add(indent+"endwhile");
			umlText.add(indent+"while(for each **timer**)");
			umlText.add(indent+indent+":if (**timer**.nextTime(//t//<sub>i</sub>) == //t//<sub>i+1</sub>)\n"
					   +indent+indent+"activate **timer**;");
			umlText.add(indent+"endwhile");
			umlText.add(indent+"while(for each //active// **timer**)");
			writeProcessBlock(indent+indent,steps);
			umlText.add(indent+"endwhile");
		}
	}
	
	// NB function types are sequential by default
	// data trackers follow functions
	// nb use an iterator rather than a foreach loop.	
	@SuppressWarnings("unchecked")
	private void writeFunctionBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> funcs,
			String arenaName) {
		ListIterator<TwConfigurationAnalyser.ExecutionStep> looper = funcs.listIterator();
		while (looper.hasNext()) {
			TwConfigurationAnalyser.ExecutionStep step = looper.next();
			if (step.level==TwConfigurationAnalyser.ExecutionLevel.functionType) {
			}
			else {
				if (step.level==TwConfigurationAnalyser.ExecutionLevel.function) {
					switch ((TwFunctionTypes)step.node.properties().getPropertyValue(P_FUNCTIONTYPE.key())) {
					case ChangeCategoryDecision:
						umlText.add(indent+"if ("+arenaName+uncap(step.node.id())+"(...)?) then (yes)");
						umlText.add(indent+":change to next category in life cycle;");
						if (looper.hasNext())
							step = looper.next();
						if (step.level==TwConfigurationAnalyser.ExecutionLevel.consequence)
							umlText.add(indent+indent+":newComponent."+uncap(step.node.id())+"(oldComponent,...);");
						umlText.add(indent+"else (no)");
						umlText.add(indent+"endif");
						break;
					case ChangeOtherState:
					case ChangeRelationState:
					case ChangeState:
					case SetInitialState:
					case SetOtherInitialState:
						umlText.add(indent+":"+arenaName+uncap(step.node.id())+"(...);");
						break;
						
					case CreateOtherDecision:
						umlText.add(indent+"while(for i=0 to "+arenaName+uncap(step.node.id())+"(...))");
						umlText.add(indent+indent+":create newBorn;"); // get category of newBorn?
						if (looper.hasNext())
							step = looper.next();
						if (step.level==TwConfigurationAnalyser.ExecutionLevel.consequence)
							umlText.add(indent+indent+":newBorn."+uncap(step.node.id())+"(parent,...);");
						umlText.add(indent+"endwhile");
						break;
					
					case DeleteDecision:
						
						umlText.add(indent+"if ("+arenaName+uncap(step.node.id())+"(...)?) then (yes)");
						umlText.add(indent+":delete self;");
						if (looper.hasNext())
							step = looper.next();
						if (step.level==TwConfigurationAnalyser.ExecutionLevel.consequence)
							umlText.add(indent+indent+":component."+uncap(step.node.id())+"(other,...);");
						umlText.add(indent+"else (no)");
						umlText.add(indent+"endif");
						break;
					
					case MaintainRelationDecision:
						umlText.add(indent+"if ("+arenaName+uncap(step.node.id())+"(...)?) then (yes)");
						// consequence?
						umlText.add(indent+":keep relation;");
						umlText.add(indent+"else (no)");
						umlText.add(indent+":delete relation;");
						umlText.add(indent+"endif");
						break;
					
					case RelateToDecision:
						umlText.add(indent+"if ("+arenaName+uncap(step.node.id())+"(...)?) then (yes)");
						// consequence?
						umlText.add(indent+":establish relation;");
						umlText.add(indent+"else (no)");
						umlText.add(indent+"endif");
						break;
					default:
						break;
					}
				}
				if (step.level==TwConfigurationAnalyser.ExecutionLevel.dataTracker) {
					String s = ":output ";
					List<TreeGraphDataNode> fields = (List<TreeGraphDataNode>) get(step.node.edges(Direction.OUT),
						selectZeroOrMany(orQuery(hasTheLabel(E_TRACKFIELD.label()),hasTheLabel(E_TRACKTABLE.label()))),
						edgeListEndNodes());
					List<TreeGraphDataNode> widgets = (List<TreeGraphDataNode>) get(step.node.edges(Direction.IN),
							selectZeroOrMany(hasTheLabel(E_TRACKSERIES.label())),
							edgeListStartNodes());
					if (fields.size()==0)
						s += "data";
					else {
						for (TreeGraphDataNode f:fields)
							s += "**"+f.id()+"**"+",";
						s = s.substring(0,s.length()-1);
					}
					if ((fields.size()>1)||(widgets.size()>1)) 
						s += indent+"\nto "; // to avoid too long lines
					else
						s += " to ";
					if (widgets.size()==0)
						s += "no one at the moment!";
					else {
						for (TreeGraphDataNode w:widgets)
							s += "**"+w.id()+"**"+",";
						s = s.substring(0,s.length()-1);
					}
					s += ";";
					umlText.add(indent+s);
				}
			}
		}
	}
	
	// argument: a list of steps starting with a process
	@SuppressWarnings("unchecked")
	private void writeProcessLoop(String indent,List<TwConfigurationAnalyser.ExecutionStep> proc) {
		List<TwConfigurationAnalyser.ExecutionStep> funcList = new ArrayList<>();
		String arenaName = "";
		for (TwConfigurationAnalyser.ExecutionStep step:proc) {
			// this only occurs for the first item of this list
			if (step.level==TwConfigurationAnalyser.ExecutionLevel.process) {
				if (!funcList.isEmpty()) {
					writeFunctionBlock(indent+indent,funcList,arenaName);
					funcList.clear();
					if (arenaName.isEmpty())
						umlText.add(indent+"endwhile");
				}
				List<TreeGraphDataNode> apps = (List<TreeGraphDataNode>) get(step.node.edges(Direction.OUT),
					selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
					edgeListEndNodes());
				String s = "";
				String ss = "category member";
				for (TreeGraphDataNode tgdn:apps)
					s += tgdn.id()+Categorized.CATEGORY_SEPARATOR;
				if (!s.isEmpty())
					s = s.substring(0,s.length()-1);
				if (apps.size()==1)
					if (apps.get(0) instanceof RelationType)
						ss = "relation";				
				if (s.contains(Category.arena)) {
					if (arenaId!=null)
						arenaName = arenaId+".";
					else
						arenaName = "arena."; // too tricky to find the real name!
				}
				else {
					arenaName = "";
					umlText.add(indent+":"+proc.get(0).node.id()+";");
					umlText.add(indent+"while (for each **"+s+"** "+ss+")");
				}
			} else
				funcList.add(step);
		}
		if (!funcList.isEmpty()) {
			writeFunctionBlock(indent+indent,funcList,arenaName);
			if (arenaName.isEmpty())
				umlText.add(indent+"endwhile");
		}
	}
	
	// NB dependency ranks are sequential by construct
	private void writeProcessBlock(String indent,List<TwConfigurationAnalyser.ExecutionStep> procs) {
		boolean depRankOpen = false;
		int nproc = 0;
		List<TwConfigurationAnalyser.ExecutionStep> procList = new ArrayList<>();
		for (TwConfigurationAnalyser.ExecutionStep step:procs) {
			if (step.level==TwConfigurationAnalyser.ExecutionLevel.dependencyRank) {
				// this produces the process loop for the last process of the previous dependency rank
				if (!procList.isEmpty())
					writeProcessLoop(indent,procList);
				if (nproc>0)
					umlText.add(indent+"split end");
				if (!procList.isEmpty()) {
					umlText.add(indent+":update drivers\n"+indent+"update graph structure;");
					procList.clear();
				}
				nproc = 0;
			}
			else {
				if (step.level==TwConfigurationAnalyser.ExecutionLevel.process) {
					// this produces the process loop for the previous process
					if (!procList.isEmpty()) {
						writeProcessLoop(indent,procList);
						procList.clear();
						nproc++;
					}
					switch (step.looping) {
					case parallel:
						if (nproc==0) {
							umlText.add(indent+"split");
							depRankOpen = true;
						}
						else
							umlText.add(indent+"split again");
						break;
					case sequential:
					case unique:
							depRankOpen = false;
						break;
					}
				}
				procList.add(step);
			}
		}
		// this happens at the end of the procs list
		if (!procList.isEmpty()) {
			writeProcessLoop(indent,procList);
//			procList.clear();
		}
		if (depRankOpen)
			umlText.add(indent+"split end");
		umlText.add(indent+":update drivers\n"+indent+"update graph structure;");
		umlText.add(indent+":reset decorators;");
	}

}
