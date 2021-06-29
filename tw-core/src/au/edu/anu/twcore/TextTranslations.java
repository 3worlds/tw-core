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
package au.edu.anu.twcore;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import au.edu.anu.omhtk.Language;
import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.ens.biologie.generic.utils.Interval;

/**
 * @author Ian Davies
 *
 * @date 14 Mar. 2021
 */
public class TextTranslations {

	// -------------------- ModelBuildErrorMsg ----
	public static String[] getMODEL_FILE_BACKUP() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Mettre à jour le code dans le fichier Java nouvellement généré.";
			cm = "La configuration a changé et nécessite la création d'un nouveau fichier Java. Le fichier précédent a été sauvegardé et renommé en fichier texte numéroté (* .txt)";
		} else {// make sure default is English!
			am = "Update code in newly generated java file.";
			cm = "Configuration has changed requiring creation of a new Java file. The previous file has been backed up and renamed to a numbered text file (*.txt)";
		}
		String[] result = { am, cm };
		return result;
	};

	public static String[] getCOMPILER_ERROR(String compileResult, List<String> snippets) {
		String am;
		String cm;
		if (Language.French()) {
			if (UserProjectLink.haveUserProject()) {
				am = "Corriger les erreurs de programmation dans le projet java <<" + Language.oq
						+ UserProjectLink.projectRoot().getName() + Language.cq + ". ";
				if (!snippets.isEmpty())
					am += " et / ou extrait(s) de code: " + snippets;
				am += ".";
			} else {
				am = "Corriger les erreurs de programmation dans les extrait(s) de code: " + snippets + ".";
			}
			if (compileResult == null)
				compileResult = "inconnu";
			cm = compileResult;
		} else {// make sure default is English!
			if (UserProjectLink.haveUserProject()) {
				am = "Correct coding errors in java project '" + UserProjectLink.projectRoot().getName() + "'";
				if (!snippets.isEmpty())
					am += " and/or code snippet(s): " + snippets;
				am += ".";
			} else {
				am = "Correct coding errors in code snippet(s): " + snippets + ".";
			}
			if (compileResult == null)
				compileResult = "unknown.";
			cm = compileResult;
		}

		String[] result = { am, cm };
		return result;
	}

	public static String[] getCOMPILER_MISSING() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Installer le 'Java Development Kit' (JDK).";
			cm = "Aucun compilateur Java n'a été trouvé.";
		} else {// make sure default is English!
			am = "Install Java Development Kit (JDK).";
			cm = "Expected Java compiler but none found.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getDEPLOY_CLASS_MISSING(String srcName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Un fichier compilé Java (.class) est manquant.";
			cm = "Fichier compilé attendu pour " + Language.oq + srcName + Language.cq + " mais aucun n'a été trouvé.";
		} else {// make sure default is English!
			am = "A Java class file is missing.";
			cm = "Expected class file for '" + srcName + "' but none found.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getDEPLOY_CLASS_OUTOFDATE() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Actualiser le projet Java.";
			cm = "Un fichier compilé java (.class) est plus ancien que le fichier source Java.";
		} else {// make sure default is English!
			am = "Refresh Java project.";
			cm = "Compiled class file is older than Java source file.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getDEPLOY_PROJECT_UNSAVED() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Appuyez sur [Ctrl+s] pour enregistrer la configuration.";
			cm = "La configuration doit être enregistrée pour permettre le lancement des simulations.";
		} else {// make sure default is English!
			am = "Press [Ctrl+s] to save configuration.";
			cm = "Configuration must be saved to allowed deployment.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getDEPLOY_RESOURCE_MISSING(String resourceName, String location) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajoutez <<" + Language.oq + resourceName + Language.cq + " à " + Language.oq + location + Language.cq
					+ ".";
			cm = "La ressource doit être présente pour le lancement des simulations.";
		} else {// make sure default is English!
			am = "Add '" + resourceName + "' to '" + location + "'.";
			cm = "Resource must be present for deployment";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getDEPLOY_EXCEPTION(Exception e) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Échec du lancement de ModelRunner.";
			cm = e.getMessage();
		} else {// make sure default is English!
			am = "Failed to launch ModelRunner.";
			cm = e.getMessage();
		}
		String[] result = { am, cm };
		return result;
	}

	// -------------------- Twcore queries ----

	// NB: name static function exactly after the relevant query
	public static String[] getEdgeToOneChildOfQuery(String nodeRef) {
		// NB: the node to which the out edge is added is prepended later. Don't add it
		// here.
		String am;// action message
		String cm;// constraint message
		if (Language.French()) {
			am = "Ajouter un lien vers un noeud fils de " + Language.oq + nodeRef + Language.cq + ".";
			cm = "Lien attendu vers un noeud fils de " + Language.oq + nodeRef + Language.cq + ", mais aucun trouvé.";
		} else {// make sure default is English!
			am = "Add edge to one child of '" + nodeRef + "'";
			cm = "Expected edge to one child of '" + nodeRef + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	};

	public static String[] getBaselineQuery(String shortString, String[] sys, String baselineLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter un lien de type '" + baselineLabel + "' vers un noeud parmi " + Arrays.deepToString(sys)
					+ ".";
			cm = "Lien de type '" + baselineLabel + "' vers un noeud parmi " + Arrays.deepToString(sys)
					+ " attendu, mais aucun trouvé.";
		} else {
			am = "Add edge labelled '" + baselineLabel + "' to one of " + Arrays.deepToString(sys) + ".";
			cm = "Expected an edge labelled '" + baselineLabel + "' to one of " + Arrays.deepToString(sys)
					+ " but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Crazy
	public static String[] getBorderTypeValidityQuery1(SpaceType stype, int requredDim, int foundDim) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Define " + (requredDim * 2) + " borders for space type '" + stype + "'.";
			cm = "Expected Space of type " + stype + "' to define " + (requredDim * 2) + " borders but found "
					+ foundDim + ".";
		} else {// make sure default is English!
			am = "Define " + (requredDim * 2) + " borders for space type '" + stype + "'.";
			cm = "Expected Space of type " + stype + "' to define " + (requredDim * 2) + " borders but found "
					+ foundDim + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Crazy
	public static String[] getBorderTypeValidityQuery2(int i) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit borders to pair Wrap-around in dimension " + i + ".";
			cm = "Expected Wrap-around in dimension " + i + " to be paired but was found unpaired.";
		} else {// make sure default is English!
			am = "Edit borders to pair Wrap-around in dimension " + i + ".";
			cm = "Expected Wrap-around in dimension " + i + " to be paired but was found unpaired.";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Crazy
	public static String[] getBorderTypeValidityQuery3() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit border property to wrap-around in the x-dimension.";
			cm = "Tubular wrap-around is only supported in the x-dimension.";
		} else {// make sure default is English!
			am = "Edit border property to wrap-around in the x-dimension.";
			cm = "Tubular wrap-around is only supported in the x-dimension.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getBoxInSpaceDimensionQuery(String key, String spaceName, Integer expectedDims,
			Integer foundDims) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Changer le nombre de dimensions de '" + key + "' pour " + expectedDims
					+ ", nombre de dimensions de l'espace '" + spaceName + "' qui le contient.";
			cm = "'" + key + "' devrait avoir " + expectedDims + " dimension(s) au lieu de " + foundDims
					+ " trouvée(s).";
		} else {// make sure default is English!
			am = "Edit dimensions of '" + key + "' to have " + expectedDims
					+ " dimensions, the same dimensions as its containing space '" + spaceName + "'.";
			cm = "Expected '" + key + "' to have " + expectedDims + " dimensions but found " + foundDims + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getCategoryConsistencyQuery(String label, String fromCat, String procName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter un lien de type '" + label + "': de la catégorie '" + fromCat + "' vers un noeud 'Record:'.";
			cm = "La catégorie '" + fromCat + "' (référencée par un lien 'appliesTo' du noeud '" + procName
					+ "') doit avoir un lien vers un noeud 'Record:'; aucun n'a été trouvé."
					+ " Toutes les catégories référencées par un noeud 'Process' doivent avoir un lien vers au moins un noeud 'Record:'.";
		} else {// make sure default is English!
			am = "Add edge '" + label + "': from '" + fromCat + "' to a 'Record:' node.";
			cm = "Expected '" + fromCat + "' (that is 'appliesTo' by '" + procName
					+ "') to have a link to a 'Record:' node but found none."
					+ " All categories that a process applies to must reference at least one record.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getCheckSubArchetypeQuery(String fileName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Corriger les erreurs dans le sous-archétype.";
			cm = "Le sous archétype '" + fileName + "' contient des erreurs.";
		} else {// make sure default is English!
			am = "Fix errors in sub-archetype.";
			cm = "Errors found in sub-archetype '" + fileName + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getChildAtLeastOneOfOneOrTwoOfTwoQuery1(String item, String widgetLabel,
			String containerLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = " Ajouter un noeud fils de type '" + widgetLabel + "' ou '" + containerLabel + "'.";
			cm = "Un noeud de type '" + item + "' doit avoir un noeud fils de type '" + widgetLabel + "' ou '"
					+ containerLabel + "'.";
		} else {// make sure default is English!
			am = " Add child node '" + widgetLabel + "' or '" + containerLabel + "'.";
			cm = "Expected '" + item + "' must have a child node of '" + widgetLabel + "' or '" + containerLabel + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getChildAtLeastOneOfOneOrTwoOfTwoQuery2(String item, String widgetLabel,
			String containerLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit un noeud fils de type '" + widgetLabel + "', soit encore un noeud fils de type '"
					+ containerLabel + "'.";
			cm = "Un noeud de type '" + item + "' doit avoir soit un noeud fils de type '" + widgetLabel
					+ "', soit un noeud fils de type '" + containerLabel + "' de plus.";
		} else {// make sure default is English!
			am = "Add either a '" + widgetLabel + "' child or an additional '" + containerLabel + "' child.";
			cm = "Expected '" + item + "' to have a '" + widgetLabel + "' child or an additional '" + containerLabel
					+ "' child.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getChildXorPropertyQuery(String propertyName, String nodeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit la propriété '" + propertyName + "', soit un noeud fils de type '" + nodeLabel + "'.";
			cm = "Propriété '" + propertyName + "' ou noeud fils de type '" + nodeLabel
					+ "' attendus, mais aucun des deux n'a été trouvé.";
		} else {// make sure default is English!
			am = "Add either property '" + propertyName + "' or child node '" + nodeLabel + "'.";
			cm = "Expected either the property '" + propertyName + "' or the child '" + nodeLabel
					+ "' but found neither or both.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getConsequenceMatchFunctionTypeQuery(String strValidTypes, String functionType,
			String foundType) {
		String am;
		String cm;
		if (Language.French()) {
			am = "La conséquence de la fonction '" + functionType + "' doit être redéfinie avec l'un des types "
					+ strValidTypes + ".";
			cm = "Conséquence de type " + strValidTypes + " attendue, mais type '" + foundType + "' trouvé.";
		} else {// make sure default is English!
			am = "Re-create Consequence function to have type of " + strValidTypes + " for function '" + functionType
					+ "'.";
			cm = "Expected type of function to be one of " + strValidTypes + " but found " + foundType + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getEdgeToSiblingNodesQuery(String label) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Changer les liens de type '" + label + "' pour qu'ils pointent vers des noeuds frères.";
			cm = "Les liens de type '" + label + "' doivent pointer vers des noeuds frères (ayant le même parent).";
		} else {// make sure default is English!
			am = "Change '" + label + "' edges to connect to sibling nodes.";
			cm = "Expected '" + label + "' edges to refer to sibling nodes.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getEdgeXorPropertyQuery(String item, String key, String label) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit la propriété '" + key + "', soit un lien vers un noeud de type '" + label + "'.";
			cm = "Propriété '" + key + "' ou lien vers noeud de type '" + label
					+ "' attendus, mais aucun des deux ou les deux ont été trouvés.]";
		} else {// make sure default is English!
			am = "Add either property '" + key + "' or an edge to node '" + label + "'.";
			cm = "Expected either property '" + key + "' or edge to '" + label + "' but found neither or both.]";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getEndNodeHasPropertyQuery(String item, String key) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter la propriété '" + key + "' au noeud terminal '" + item + "'.";
			cm = "Le noeud terminal '" + item + "' doit avoir la propriété '" + key + "'.";
		} else {// make sure default is English!
			am = "Add property '" + key + "' to leaf node '" + item + "'.";
			cm = "Expected leaf node '" + item + "' to have property '" + key + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getExclusiveCategoryQuery(String item, String catSet) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Enlever un des liens de '" + item + "' vers une des catégories de '" + catSet + "'.";
			cm = "'" + item + "' ne peut pointer que vers une seule des catégories de l'ensemble '" + catSet + "'.";
		} else {// make sure default is English!
			am = "Remove edge from '" + item + "' to one of the categories of '" + catSet + "'.";
			cm = "Expected '" + item + "' to have an edge to only one of the categories of '" + catSet
					+ "' but found more.";
		}
		String[] result = { am, cm };
		return result;
	}
	// TODO: French done down to here

	/**
	 * Checks that if a child node with a given property value is present, then no
	 * child with another value in the same property can be present. Can be
	 * instantiated with a single label, or a table of compatible labels.
	 * 
	 * @param target The label:name of target sibling
	 * @param key Target's property key
	 * @param expectedValues Required property values
	 * @param nDiffSibs Number of non-compliant siblings
	 * @return
	 */
	public static String[] getExclusiveChildPropertyValueQuery(String target, String key, List<Object> expectedValues,
			int nDiffSibs) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Either change property value of '" + key + "' to "
					+ expectedValues + " for all sibling(s) of '"+target+"' OR remove this node.";
			cm = "Expected siblings of '" + target + "' containing the property '" + key + "' to have the value(s) "
					+ expectedValues + "but found " + nDiffSibs + " sibling(s) with other values.";
		} else {// make sure default is English!
			am = "Either change property value of '" + key + "' to "
					+ expectedValues + " for all sibling(s) of '"+target+"' OR remove this node.";
			cm = "Expected siblings of '" + target + "' containing the property '" + key + "' to have the value(s) "
					+ expectedValues + "but found " + nDiffSibs + " sibling(s) with other values.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * A Query to check that both nodes of an edge have a common parent of a
	 * specified type
	 */
	public static String[] getFindCommonCategoryQuery(String trackName, String processName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge from '" + trackName + "' to one of the categories of '" + processName + "'.";
			cm = "Expected '" + trackName + "' to belong to one of its '" + processName
					+ "' categories but found none.";
		} else {// make sure default is English!
			am = "Add edge from '" + trackName + "' to one of the categories of '" + processName + "'.";
			cm = "Expected '" + trackName + "' to belong to one of its '" + processName
					+ "' categories but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getFunctionMatchProcessTypeQuery(String functionType, String processType,
			List<String> validTypes, String functionName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Re-create '" + functionName + "' with one of the function types " + validTypes + "'.";
			cm = "Expected '" + functionName + "' to have type +" + validTypes + " but found '" + functionType
					+ "'. This is incompatible with a " + processType + " process.";
		} else {// make sure default is English!
			am = "Re-create '" + functionName + "' with one of the function types " + validTypes + "'.";
			cm = "Expected '" + functionName + "' to have type +" + validTypes + " but found '" + functionType
					+ "'. This is incompatible with a " + processType + " process.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getGroupComponentRequirementQuery(String groupName, String instanceOfLabel,
			String componentTypeLabel, String groupOfLabel, String groupTypeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Make '" + groupName + "' an '" + instanceOfLabel + "' some '" + componentTypeLabel
					+ ":' OR make it a  '" + groupOfLabel + "' of some '" + groupTypeLabel + "'.";
			cm = "Expected inEdge '" + instanceOfLabel + "' from some '" + componentTypeLabel + ":' OR outEdge '"
					+ groupOfLabel + "' to some '" + groupTypeLabel + "' but found neither case.";
		} else {// make sure default is English!
			am = "Make '" + groupName + "' an '" + instanceOfLabel + "' some '" + componentTypeLabel
					+ ":' OR make it a  '" + groupOfLabel + "' of some '" + groupTypeLabel + "'.";
			cm = "Expected inEdge '" + instanceOfLabel + "' from some '" + componentTypeLabel + ":' OR outEdge '"
					+ groupOfLabel + "' to some '" + groupTypeLabel + "' but found neither case.";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Improve : NB lifecycle is auto prepended
	public static String[] getGroupInstanceRequirementQuery() {
		String am;
		String cm;
		if (Language.French()) {
			am = "LifeCycle must have exactly one instance of Group per GroupType of its LifeCycleType.";
			cm = "LifeCycle must have exactly one instance of Group per GroupType of its LifeCycleType.";
		} else {// make sure default is English!
			am = "LifeCycle must have exactly one instance of Group per GroupType of its LifeCycleType.";
			cm = "LifeCycle must have exactly one instance of Group per GroupType of its LifeCycleType.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getGuardAreaMaxWidthQuery(double width) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set property value to at most half the length of the shortest side.";
			cm = "Expected guard area width to be smaller than half the length of the shortest side but found " + width;
		} else {// make sure default is English!
			am = "Set property value to at most half the length of the shortest side.";
			cm = "Expected guard area width to be smaller than half the length of the shortest side but found " + width;
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIndexDimensionQuery(String ixs, String nodeName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + ixs + "' to be within range of the dimensions for '" + nodeName + "'.";
			cm = "Index string '" + ixs + "' out of range for table '" + nodeName + "'.";
		} else {// make sure default is English!
			am = "Edit '" + ixs + "' to be within range of the dimensions for '" + nodeName + "'.";
			cm = "Index string '" + ixs + "' out of range for table '" + nodeName + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getInputFileExistQuery(File file) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add file '" + file.getName() + "' to the project.";
			cm = "Expected file '" + file.getName() + "-" + file.getParent() + "' but not found.";
		} else {// make sure default is English!
			am = "Add file '" + file.getName() + "' to the project.";
			cm = "Expected file '" + file.getName() + "-" + file.getParent() + "' but not found.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInIntervalQuery(Object value, Interval interval) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set value within the interval " + interval + ".";
			cm = "Expected value to be within " + interval + " but found '" + value + "'.";
		} else {// make sure default is English!
			am = "Set value within the interval " + interval + ".";
			cm = "Expected value to be within " + interval + " but found '" + value + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInLifeCycleCategorySetQuery1(int nToCats, int nFromCats, String toLbl, String fromLbl) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge '" + fromLbl + "' to a Category";
			cm = "Expected '" + fromLbl + "' to a life cycle category but found none.";
		} else {// make sure default is English!
			am = "Add edge '" + fromLbl + "' to a Category";
			cm = "Expected '" + fromLbl + "' to a life cycle category but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInLifeCycleCategorySetQuery2(int nToCats, int nFromCats, String toLbl, String fromLbl) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Remove a '" + fromLbl + "' edge from a category.";
			cm = "Expected fewer '" + fromLbl + "' for life cycle but found " + nFromCats;
		} else {// make sure default is English!
			am = "Remove a '" + fromLbl + "' edge from a category.";
			cm = "Expected fewer '" + fromLbl + "' for life cycle but found " + nFromCats;
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInLifeCycleCategorySetQuery3(int nToCats, int nFromCats, String toLbl, String fromLbl) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge '" + toLbl + "' to a Category";
			cm = "Expected '" + toLbl + "' to a life cycle category but found none.";
		} else {// make sure default is English!
			am = "Add edge '" + toLbl + "' to a Category";
			cm = "Expected '" + toLbl + "' to a life cycle category but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInLifeCycleCategorySetQuery4(int nToCats, int nFromCats, String toLbl, String fromLbl) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Remove a '" + toLbl + "' edge to a category.";
			cm = "Expected fewer '" + toLbl + "' for life cycle but found " + nToCats;
		} else {// make sure default is English!
			am = "Remove a '" + toLbl + "' edge to a category.";
			cm = "Expected fewer '" + toLbl + "' for life cycle but found " + nToCats;
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInRangeQuery(String key, Object value, double min, double max) {
		String am;
		String cm;
		if (Language.French()) {
			if (max > 100_000_000)
				am = "Set '" + key + "' to be greater or equal to '" + min + "'.";
			else
				am = "Set '" + key + "' to be within the range " + min + " to " + max + ".";
			cm = "Expected '" + key + "' to be within the range [" + min + ":" + max + "] but found '" + value + "'.";
		} else {// make sure default is English!
			if (max > 100_000_000)
				am = "Set '" + key + "' to be greater or equal to '" + min + "'.";
			else
				am = "Set '" + key + "' to be within the range " + min + " to " + max + ".";
			cm = "Expected '" + key + "' to be within the range [" + min + ":" + max + "] but found '" + value + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getIsInValueSetQuery(String key, Table valueSet, Object foundValue) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph file with a text editor to change the property value of '" + key + " to a valid value.";
			cm = "Expected value of '" + key + "' to be one of " + valueSet.toString() + " but found '" + foundValue
					+ "'.";
		} else {// make sure default is English!
			am = "Edit graph file with a text editor to change the property value of '" + key + " to a valid value.";
			cm = "Expected value of '" + key + "' to be one of " + valueSet.toString() + " but found '" + foundValue
					+ "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getLifeCycleInstanceRequirementQuery1(String edgeLabel, String lifecycleLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add a '" + edgeLabel + "' edge to a '" + lifecycleLabel + "'.";
			cm = "Expected '" + edgeLabel + "' to a '" + lifecycleLabel + "' but found none.";
		} else {// make sure default is English!
			am = "Add a '" + edgeLabel + "' edge to a '" + lifecycleLabel + "'.";
			cm = "Expected '" + edgeLabel + "' to a '" + lifecycleLabel + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getLifeCycleInstanceRequirementQuery2(String edgeLabel, String lcName, String lcParentName,
			String gtName, String gtParentName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Do something";
			cm = "Expected '" + lcName + "'to be linked with '" + edgeLabel + "' to the same lifecyle as '" + gtName
					+ "' but ...";
		} else {// make sure default is English!
			am = "Do something";
			cm = "Expected '" + lcName + "'to be linked with '" + edgeLabel + "' to the same lifecyle as '" + gtName
					+ "' but ...";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getLifeCycleSubGroupsQuery() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Reconfigure GroupType to have at least one child group belonging to each category of it's categorySet.";
			cm = "Expected life cycle group to have at least one child group to belong to each category of it's categorySet but found none.";
		} else {// make sure default is English!
			am = "Reconfigure GroupType to have at least one child group belonging to each category of it's categorySet.";
			cm = "Expected life cycle group to have at least one child group to belong to each category of it's categorySet but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getLifespanFunctionCompatibilityQuery(String ctName, String fnName, String pnName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Reconfigure. '" + ctName + "' is not ephemeral but is processed by '" + fnName + "' of '" + pnName
					+ "' that only works on ephemeral ComponentTypes.";
			cm = "Expected '" + ctName + "' to belong to 'Category:*ephemeral*'.";
		} else {// make sure default is English!
			am = "Reconfigure. '" + ctName + "' is not ephemeral but is processed by '" + fnName + "' of '" + pnName
					+ "' that only works on ephemeral ComponentTypes.";
			cm = "Expected '" + ctName + "' to belong to 'Category:*ephemeral*'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getNameStartsWithUpperCaseQuery(String item, char c) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph with a text editor so '" + item + "' starts with an upper case character.";
			cm = "Expected first character to be upper case but found '" + c + "'.";
		} else {// make sure default is English!
			am = "Edit graph with a text editor so '" + item + "' starts with an upper case character.";
			cm = "Expected first character to be upper case but found '" + c + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getNodeAtLeastOneChildLabelOfQuery(List<String> labels) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter un noeud à l'un des «" + labels.toString() + "».";
			cm = "Attendu au moins un enfant intitulé «" + labels.toString() + "», mais n'en trouve aucun";
		} else {// make sure default is English!
			am = "Add node to one of '" + labels.toString() + "'.";
			cm = "Expected at least one child labelled '" + labels.toString() + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getNodeHasPropertyValueQuery(String propertyName, List<Object> expectedValues,
			Object foundValue) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph with a text editor to set '" + propertyName + "' value to one of '"
					+ expectedValues.toString() + "'.";
			cm = "Expected property '" + propertyName + "' to  have value '" + expectedValues.toString()
					+ "' but found '" + foundValue + "'.";
		} else {// make sure default is English!
			am = "Edit graph with a text editor to set '" + propertyName + "' value to one of '"
					+ expectedValues.toString() + "'.";
			cm = "Expected property '" + propertyName + "' to  have value '" + expectedValues.toString()
					+ "' but found '" + foundValue + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getOutEdgeXNorQuery1(String[] edgeLabel1, String[] edgeLabel2) {
		String am;
		String cm;
		// TODO: msg possibly incorrect
		if (Language.French()) {
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges and one of "
					+ Arrays.toString(edgeLabel2) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		} else {// make sure default is English!
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges and one of "
					+ Arrays.toString(edgeLabel2) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getOutEdgeXNorQuery2(String[] edgeLabel1, String[] edgeLabel2) {
		String am;
		String cm;
		// TODO: msg possibly incorrect
		if (Language.French()) {
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		} else {// make sure default is English!
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getOutEdgeXorQuery(String[] edgeLabel1, String[] edgeLabel2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge '" + edgeLabel1 + " or " + Arrays.toString(edgeLabel2) + ".";
			cm = "Expected at least one edge labelled either " + Arrays.toString(edgeLabel1) + " or "
					+ Arrays.toString(edgeLabel2) + " but none found.";
		} else {// make sure default is English!
			am = "Add edge '" + edgeLabel1 + " or " + Arrays.toString(edgeLabel2) + ".";
			cm = "Expected at least one edge labelled either " + Arrays.toString(edgeLabel1) + " or "
					+ Arrays.toString(edgeLabel2) + " but none found.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getOutNodeXorQuery(String nodeLabel1, String nodeLabel2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge to a node labelled '" + nodeLabel1 + ":' or '" + nodeLabel2 + ":'.";
			cm = "Expected edge to a node labelled either '" + nodeLabel1 + "' or '" + nodeLabel2 + "'.";
		} else {// make sure default is English!
			am = "Add edge to a node labelled '" + nodeLabel1 + ":' or '" + nodeLabel2 + ":'.";
			cm = "Expected edge to a node labelled either '" + nodeLabel1 + "' or '" + nodeLabel2 + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getParentClassQuery(List<String> klasses, String foundParent) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set parent to one of " + klasses + ".";
			cm = "Expected parent to be one of " + klasses + " but found '" + foundParent + "'.";
		} else {// make sure default is English!
			am = "Set parent to one of " + klasses + ".";
			cm = "Expected parent to be one of " + klasses + " but found '" + foundParent + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getParentHasPropertyValue(String propertyName, Object foundValue,
			List<Object> expectedValues) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change property value of '" + propertyName + "' to one of " + expectedValues + ".";
			cm = "Expected '" + propertyName + "' value to be one of '" + expectedValues.toString() + "' but found '"
					+ foundValue + "'.";
		} else {// make sure default is English!
			am = "Change property value of '" + propertyName + "' to one of " + expectedValues + ".";
			cm = "Expected '" + propertyName + "' value to be one of '" + expectedValues.toString() + "' but found '"
					+ foundValue + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getParentLabelQuery(List<String> labels, String foundParent) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change parent to be one of '" + labels + "'.";
			cm = "Expected parent to be on of '" + labels + "' but found '" + foundParent + "'.";
		} else {// make sure default is English!
			am = "Change parent to be one of '" + labels + "'.";
			cm = "Expected parent to be on of '" + labels + "' but found '" + foundParent + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getPropertyXorQuery(String name1, String name2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph file with text editor to remove one of the properties '" + name1 + "' or '" + name2 + "'.";
			cm = "Expected property named either '" + name1 + "' or '" + name2 + "' but not both.";
		} else {// make sure default is English!
			am = "Edit graph file with text editor to remove one of the properties '" + name1 + "' or '" + name2 + "'.";
			cm = "Expected property named either '" + name1 + "' or '" + name2 + "' but not both.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getRankingPropertyQuery1(String propName, String elementList, String numberList,
			String nodeName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + propName + "' values for nodes [" + elementList + "] to unique values.";
			cm = "Expected '" + propName + "' values for children of '" + nodeName + "' to be unique but found values ["
					+ numberList + ".";
		} else {// make sure default is English!
			am = "Edit '" + propName + "' values for nodes [" + elementList + "] to unique values.";
			cm = "Expected '" + propName + "' values for children of '" + nodeName + "' to be unique but found values ["
					+ numberList + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getRankingPropertyQuery2(String propName, String elementList, String numberList,
			String edgeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + propName + "' values for elements [" + elementList + "] to unique values.";
			cm = "Expected '" + propName + "' values of '" + edgeLabel + "' to be unique but found " + numberList + ".";
		} else {// make sure default is English!
			am = "Edit '" + propName + "' values for elements [" + elementList + "] to unique values.";
			cm = "Expected '" + propName + "' values of '" + edgeLabel + "' to be unique but found " + numberList + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Check that a root record is used by one and only one category {autoVar,
	 * decorators, drivers, constants}.
	 */
	public static String[] getRecordUsedByAtMostOneCategoryQuery(String target, int nEdges) {
		String am;
		String cm;
		if (Language.French()) {
			if (nEdges == 0) {
				am = "Add an edge from a Category to '" + target + "'.";
			} else
				am = "Remove all but one in edge from a Category to '" + target + "'.";
			cm = "Expected 1 in-edge to '" + target + "' but found " + nEdges + ".";
		} else {// make sure default is English!
			if (nEdges == 0) {
				am = "Add an edge from a Category to '" + target + "'.";
			} else
				am = "Remove all but one in edge from a Category to '" + target + "'.";
			cm = "Expected 1 in-edge to '" + target + "' but found " + nEdges + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getRequirePropertyQuery(String p1, String p2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Remove property '" + p1
					+ "' by editing the configuration graph with a text editor. [ModelMaker programming error!].";
			cm = "Presence of property '" + p1 + "' is incompatible with value of property '" + p2 + "'.";
		} else {// make sure default is English!
			am = "Remove property '" + p1
					+ "' by editing the configuration graph with a text editor. [ModelMaker programming error!].";
			cm = "Presence of property '" + p1 + "' is incompatible with value of property '" + p2 + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getSearchProcessConsistencyQuery(String procName, String spaceName, String assocList) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Reconfigure graph so that all componentTypes processed by '" + procName
					+ "' have valid coordinates for '" + spaceName + "'.";
			cm = "Expected all componentTypes processed by '" + procName + "' to have valid coordinates for '"
					+ spaceName + "' but found associations with '" + assocList + "'.";
		} else {// make sure default is English!
			am = "Reconfigure graph so that all componentTypes processed by '" + procName
					+ "' have valid coordinates for '" + spaceName + "'.";
			cm = "Expected all componentTypes processed by '" + procName + "' to have valid coordinates for '"
					+ spaceName + "' but found associations with '" + assocList + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getSenderInRangeQuery(String pKey, IntegerRange simRange, IntegerRange listenerRange,
			int nReps, int firstSender) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit property '" + pKey + "' to receive data in the range " + simRange + ".";
			cm = "Expected sufficent simulator(s) to send data in the range " + listenerRange + " but found only "
					+ nReps + " simulator(s). [" + pKey + "=" + firstSender + "]";
		} else {// make sure default is English!
			am = "Edit property '" + pKey + "' to receive data in the range " + simRange + ".";
			cm = "Expected sufficent simulator(s) to send data in the range " + listenerRange + " but found only "
					+ nReps + " simulator(s). [" + pKey + "=" + firstSender + "]";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getSpaceCoordinateTypeQuery(String typeName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change coordinate fields to be numeric.";
			cm = "Expected coordinate fields to be numeric but found '" + typeName + "'.";
		} else {// make sure default is English!
			am = "Change coordinate fields to be numeric.";
			cm = "Expected coordinate fields to be numeric but found '" + typeName + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getSpaceDimensionConsistencyQuery(int dif, int dimension, String label) {
		String am;
		String cm;
		if (Language.French()) {
			if (dif > 0)
				am = "Add " + dif + " '" + label + ":' edges.";
			else
				am = "Remove " + dif + " '" + label + ":' edges.";
			cm = "Expected " + dimension + " " + label + " edges but found " + (dimension + dif) + ".";
		} else {// make sure default is English!
			if (dif > 0)
				am = "Add " + dif + " '" + label + ":' edges.";
			else
				am = "Remove " + dif + " '" + label + ":' edges.";
			cm = "Expected " + dimension + " " + label + " edges but found " + (dimension + dif) + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getSpaceRecordTypeQuery(String fieldNames) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set coordinate field(s) to belong to a record that is used as either a driver or constant.";
			cm = "Expected coordinate fields '" + fieldNames
					+ "' to belong to a record used as drivers or constants but found none.";
		} else {// make sure default is English!
			am = "Set coordinate field(s) to belong to a record that is used as either a driver or constant.";
			cm = "Expected coordinate fields '" + fieldNames
					+ "' to belong to a record used as drivers or constants but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getTimeUnitValidityQuery(String key, String validUnits, String foundValue) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change '" + key + "' value to be one of " + validUnits + ".";
			cm = "Expected value of '" + key + "' to be one of " + validUnits + " but found '" + foundValue + "'.";
		} else {// make sure default is English!
			am = "Change '" + key + "' value to be one of " + validUnits + ".";
			cm = "Expected value of '" + key + "' to be one of " + validUnits + " but found '" + foundValue + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getUICanStopQuery(String scLabel, String dynLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add a " + scLabel + " child to " + dynLabel + ".";
			cm = "Expected at least one" + scLabel + "for unattended simulation but found none.";
		} else {// make sure default is English!
			am = "Add a " + scLabel + " child to " + dynLabel + ".";
			cm = "Expected at least one" + scLabel + "for unattended simulation but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getUIStateMachineControllerQuery(String klass, List<String> ctrlNames) {
		String am;
		String cm;
		if (Language.French()) {
			if (ctrlNames.isEmpty())
				am = "Add a control widget to either [top,bottom,tab,container].";
			else
				am = "Remove one of " + ctrlNames + ".";
			cm = "Expected one widget that descends from '" + klass
					+ "' as child of [top,bottom,tab,container] but found " + ctrlNames.size() + ".";
		} else {// make sure default is English!
			if (ctrlNames.isEmpty())
				am = "Add a control widget to either [top,bottom,tab,container].";
			else
				am = "Remove one of " + ctrlNames + ".";
			cm = "Expected one widget that descends from '" + klass
					+ "' as child of [top,bottom,tab,container] but found " + ctrlNames.size() + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getValidLifeCycleProcessQuery1(String s, String cats, String procName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Do something";
			cm = "'" + s + "' node fromCategories {" + cats + "} not all found in process '" + procName + "'";
		} else {// make sure default is English!
			am = "Do something";
			cm = "'" + s + "' node fromCategories {" + cats + "} not all found in process '" + procName + "'";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getValidLifeCycleProcessQuery2(String requiredFunc, String procName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Do something!";
			cm = "Expected '" + requiredFunc + "' function type in process '" + procName + "' but found none.";
		} else {// make sure default is English!
			am = "Do something!";
			cm = "Expected '" + requiredFunc + "' function type in process '" + procName + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getTimeIntervalValidityQuery1(String shortestTimeUnitKey, String longestTimeUnitKey,
			String tlName, String allowedMin, String allowedMax, String foundShortest, String foundLongest,
			String scaleKey) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '" + tlName + "' to '"
					+ allowedMax + "'.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + tlName + "' to be '"
					+ allowedMax + "' for '" + scaleKey + "' but found '" + foundShortest + "' and '" + foundLongest
					+ "'.";
		} else {// make sure default is English!
			am = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '" + tlName + "' to '"
					+ allowedMax + "'.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + tlName + "' to be '"
					+ allowedMax + "' for '" + scaleKey + "' but found '" + foundShortest + "' and '" + foundLongest
					+ "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getTimeIntervalValidityQuery2(String shortestTimeUnitKey, String longestTimeUnitKey,
			String tlName, String scaleKey, String foundShortest, String foundLongest) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + tlName
					+ "' to the same value.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + tlName
					+ "' must be the same for '" + scaleKey + "' but found '" + foundShortest + "' and '" + foundLongest
					+ "'.";
		} else {// make sure default is English!
			am = "Set '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + tlName
					+ "' to the same value.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + tlName
					+ "' must be the same for '" + scaleKey + "' but found '" + foundShortest + "' and '" + foundLongest
					+ "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getTimeIntervalValidityQuery3(String shortestKey, String longestKey, String scaleType,
			String foundShortest, String foundLongest) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set '" + shortestKey + "' to be less than or equal to '" + longestKey + "'.";
			cm = "Expected '" + shortestKey + "' to be <= '" + longestKey + "' for time scale type '" + scaleType
					+ "' but found '" + foundShortest + "' and '" + foundLongest + "'.";
		} else {// make sure default is English!
			am = "Set '" + shortestKey + "' to be less than or equal to '" + longestKey + "'.";
			cm = "Expected '" + shortestKey + "' to be <= '" + longestKey + "' for time scale type '" + scaleType
					+ "' but found '" + foundShortest + "' and '" + foundLongest + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] getTimeIntervalValidityQuery4(String key, String expected, String found) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set property '" + key + "' value to '" + expected
					+ "' or change the time units of associated Timers.";
			cm = "Expected value of '" + key + " to be " + expected + "' but found '" + found + "'.";
		} else {// make sure default is English!
			am = "Set property '" + key + "' value to '" + expected
					+ "' or change the time units of associated Timers.";
			cm = "Expected value of '" + key + " to be " + expected + "' but found '" + found + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	public static String[] DynamicsMustHaveAtLeastOneFunctionQuery() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add at least one 'function:' node to a process in the 'dynamics:' sub-tree.";
			cm = "Expected at least one 'function:' node but found none.";
		} else {// make sure default is English or Sanskrit users will get no messages!
			am = "Add at least one 'function:' node to a process in the 'dynamics:' sub-tree.";
			cm = "Expected at least one 'function:' node but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

}

//public static String[] getXXX() {
//String am;
//String cm;
//if (Language.French()) {
//	am = "";
//	cm = "";
//} else {// make sure default is English or Sanskrit users will get no messages!
//	am = "";
//	cm = "";
//}
//String[] result = { am, cm };
//return result;
//}
