package fr.cnrs.iees.twcore.constants;

/**
 * Labels of Nodes, Edges and Properties permitted in the 3worlds dsl file, as
 * specified in 3wArchetype.dsl CAUTION: this file must always match the
 * archetype - ideally it should be generated from it.
 * 
 * @author J.Gignoux - 8 févr. 2017
 *
 */
public enum ThreeWorldsGraphReference {
	/*-constant             name in archetype       class associated to node (for casting*/
	N_GRAPHROOT/*-			*/("3worlds",/*-		*/null), //

	N_ECOLOGY/*-            */("ecology",//
			"fr.ens.biologie.threeWorlds.core.ecology.ecosystem.World"), //
	N_TIMELINE/*-           */("timeLine",//
			"fr.ens.biologie.threeWorlds.core.ecology.timer.TimeLine"), //
	P_TIMEUNIT/*-          */("timeUnit",/*-        */null), //
	P_NTIMEUNITS/*-        */("nTimeUnits",/*-      */null), //
	P_TIMEGRAIN/*-         */("timeGrain",/*-       */null), //
	P_CALENDAR/*-          */("calendar",/*-        */null), //
	N_TIMEMODEL/*-         */("timeModel",/*-       */null), //
	N_STOPPINGCONDITION/*- */("stoppingCondition",/*-*/null), //
	E_CONDITION/*-         */("condition",/*-       */null), //
	N_EVENTQUEUE/*-        */("eventQueue",/*-      */null), //
	N_PARTITION/*-         */("partition",/*-       */null), //
	N_CATEGORY/*-          */("category",//
			"fr.ens.biologie.threeWorlds.core.ecology.category.Category"), //
	N_SYSTEM/*-            */("system",//
			"fr.ens.biologie.threeWorlds.core.ecology.ecosystem.SystemFactory"), //
	N_SYSTEMCOMPONENT		 ("System", 			"fr.ens.biologie.threeWorlds.core.ecology.ecosystem.SystemComponent"),
	E_FROM/*-              */("from",/*-            */null), // )
	E_TO/*-                */("to",/*-              */null), // )
	N_RELATION/*-          */("relation",//
			"fr.ens.biologie.threeWorlds.core.ecology.relation.Relation"), //
	N_PROCESS/*-           */("process",/*-              */
			"fr.ens.biologie.threeWorlds.core.ecology.process.ProcessNode"), //
	E_PROCESS/*-           */("process",/*-          */null),//
	E_APPLIESTO/*-         */("appliesTo",/*-        */null), //
	E_DEPENDSON/*-         */("dependsOn",/*-        */null), //
	N_LIFECYCLE/*-         */("lifeCycle",//
			"fr.ens.biologie.threeWorlds.core.ecology.ecosystem.LifeCycle"), //
	N_RECRUIT/*-           */("recruit",/*-          */null), //
	N_PRODUCE/*-           */("produce",/*-          */null), //
	N_INITIALISER/*-       */("initialiser",//
			"fr.ens.biologie.threeWorlds.core.ecology.init.SecondaryParametersInitialiser"), //
	E_INITIALISEDBY/*-     */("initialisedBy",/*-    */null), //
	N_FUNCTION/*-          */("function",/*-         */null), //
	N_CONSEQUENCE/*-       */("consequence",/*-      */null), //
//	P_JARFILE/*-           */("jarFile",/*-         */"java.lang.String"), // now obsolete
	N_ENGINE/*-            */("engine",//
			"fr.ens.biologie.threeWorlds.core.ecology.simulator.TimerModelSimulator"), //
	P_PARAMETERCLASS/*-    */("parameterClass",/*-  */"java.lang.String"), //
	P_DRIVERCLASS/*-       */("driverClass",/*-     */"java.lang.String"), //
	P_DECORATORCLASS/*-    */("decoratorClass",/*-  */"java.lang.String"), //
	P_LIFESPAN/*-          */("lifespan",/*-        */"fr.ens.biologie.threeWorlds.core.ecology.ecosystem.LifespanType"), //
	P_VARIABLENUMBERS/*-   */("variableNumbers",/*- */"java.lang.Boolean"), //
	P_MEMORY/*-            */("memory",/*-          */"java.lang.Integer"), //
	P_TYPE/*-              */("type",/*-            */"java.lang.String"), //
	P_CLASS/*-             */("class",/*-           */"java.lang.String"), //
	P_GENERATEDCLASSNAME/*-*/("generatedClassName",/*-*/"java.lang.String"), //
	P_USERCLASSNAME/*-     */("userClassName",/*-    */"java.lang.String"), //
	//P_CLASSNAME/*-         */("className",/*-       */"java.lang.String"), //
	P_DYNAMIC/*-           */("dynamic",/*-         */"java.lang.String"), //
	
	E_ENGINE/*-            */("engine",/*-          */null), //
	E_PARAMETERS/*-        */("parameters",/*       */null), //
	E_DRIVERS/*-           */("drivers",/*-         */null), //
	E_DECORATORS/*-        */("decorators",/*-      */null), //
	E_MEMBEROF/*-          */("memberOf",/*-        */null), //
	E_DRIVENBY/*-          */("drivenBy",/*-       */null), //
	N_SPECIES/*-           */("species",/*-         */null), //
	E_SPECIESTYPE/*-       */("speciesType",/*-     */null), //
	N_STAGE/*-             */("stage",/*-           */null), //
	N_PARAMETERVALUES/*-   */("parameterValues",/*- */null), //
	N_DRIVERVALUES/*-      */("driverValues",/*-    */null), //
	N_METADATAVALUES/*-    */("metadataValues",/*-  */null), //
	E_SYSTEMTYPE/*-        */("systemType",/*-      */null), //
	E_LOADFROM/*-          */("loadFrom",/*-        */null), //
	N_INDIVIDUAL/*-        */("individual",/*-      */null), //
	N_LOAD/*-              */("load",/*-            */null), //
	N_DATATRACKER/*-       */("dataTracker",		"fr.ens.biologie.threeWorlds.core.ecology.process.tracking.DataTracker"), //
	N_INITIALSTATE/*-      */("initialState",/*-    */null), //
	E_SPECIFIEDBY/*        */("specifiedBy",/*-     */null), //
	E_FROMCATEGORY/*-      */("fromCategory",/*-    */null), //
	E_TOCATEGORY/*-        */("toCategory",/*-      */null), //

	N_EXPERIMENT/*-        */("experiment",/*-      */"fr.ens.biologie.threeWorlds.core.ecology.experiment.Experiment"), //
	N_DESIGN/*-            */("design",/*-          */null), //
	N_TREATMENT/*-         */("treatment",/*-       */null), //
	N_TIMEPERIOD/*-        */("timePeriod",/*-      */null), //
	N_MODELCHANGE/*-       */("modelChange",/*-     */null), //
	E_BASELINE/*-          */("baseline",/*-        */null), //
	E_MODELSETUP/*-        */("modelSetup",/*-      */null), //
	E_MODELCHANGE/*-        */("modelChange",/*-    */null), //
	P_REPLICATES/*-        */("replicates",/*-      */null), //
	E_TIMERUNNER/*-        */("timeRunner",/*-      */null), //
	E_STOPON/*-            */("stopOn",/*-          */null), //

	N_CODESOURCE/*-        */("codeSource",/*-      */null), //
	N_FUNCTIONSPEC/*-      */("functionSpec",/*-    */null), //
	N_INITIALISERSPEC/*-   */("initialiserSpec",/*- */null), //
	N_DIMENSIONER/*-       */("dimensioner",/*-     */null), //
	P_DIM/*-               */("dim",/*-             */null), //
	E_SIZEDBY/*-           */("sizedBy",/*-         */null), //
	N_SNIPPET/*-           */("snippet",/*-         */null), //
	N_TABLE/*-             */("table",/*-           */null), //
	N_RECORD/*-            */("record",/*-          */null), //
	N_FIELD/*-             */("field",/*-           */null), //
	P_DATAELEMENTTYPE/*-   */("dataElementType",/*- */null), //
	E_DEPLOYON/*-          */("deployOn",/*-        */null), //

	N_UI/*-                */("userInterface",/*-   */null), //
	N_TAB/*-               */("tab",/*-             */null), //
	N_WIDGET/*-            */("widget",/*-          */null), //
	P_LAYOUT/*-            */("layout",/*-          */null), //

	N_HARDWARE/*-         */("hardware",/*-         */null), //
	N_COMPUTETHREAD/*-    */("computeThread",/*-    */null), //
	N_HOST/*-             */("host",/*-             */null), //
	P_HOSTADDRESS/*-      */("address",/*-          */null), //
	P_HOSTUSERNAME/*-     */("userName",/*-         */null), //
	P_HOSTSUPPLYRANK/*-   */("supplyRank",/*-       */null), //

	N_DATAIO/*-           */("dataIO",/*-           */null), //
	N_DATASOURCE/*-       */("dataSource",/*-       */null), //
	N_DATASINK/*-         */("dataSink",/*-         */null), //
	P_FILE/*-             */("file",/*-             */"java.lang.String"), //
	E_CHANNELLISTENER/*-  */("channelListener",/*- */null), //
	E_TIMELISTENER/*-     */("timeListener",/*-    */null), // 
	E_STATEMACHINELISTENER/*-*/("stateMachineListener",/*-*/null), 
	E_PERIODFOR/*-        */("periodFor",/*-        */null),
	N_READ/*-             */("read",/*-             */null), //
	N_DIM/*-              */("dim",/*-              */null), //
	P_COL/*-              */("col",/*-              */null), //
	P_PARAMFILE/*         */("paramFile",/*         */null),//
	P_INITFILE/*          */("initFile",/*          */null),//
	
	N_TOOLBARTOP/*        */("ToolbarTop",/*        */null),//
	N_TOOLBARBOTTOM/*     */("ToolbarBottom",/*     */null),//
	N_TOPLEFTPANEL/*      */("TopLeftPanel",/*      */null),//
	N_TOPRIGHTPANEL/*     */("TopRightPanel",/*     */null),//
	N_BOTTOMLEFTPANEL/*   */("BottomLeftPanel",/*   */null),//
	N_BOTTOMRIGHTPANEL/*  */("BottomRightPanel",/*  */null),//

	;

	private final String name;
	private final String className;

	private ThreeWorldsGraphReference(String name, String className) {
		this.name = name;
		this.className = className;
	}

	public String toString() {
		return name;
	}

	public String className() {
		return className;
	}

	public static String findClass(String name) {
		for (ThreeWorldsGraphReference c : ThreeWorldsGraphReference.values())
			if (c.toString().equals(name))
				return c.className();
		return null;
	}


}
