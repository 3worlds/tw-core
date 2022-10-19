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
import java.util.*;

import au.edu.anu.omhtk.Language;
import au.edu.anu.omugi.collections.tables.Table;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.twcore.constants.*;
import fr.cnrs.iees.omhtk.utils.Interval;
import au.edu.anu.twcore.archetype.tw.*;
import au.edu.anu.twcore.errorMessaging.*;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * Natural language translations for any message in this project (twcore).
 * <p>
 * Currently, only French and English are supported. These messages are all
 * related to the query system through this need not be the case. By convention,
 * query translation methods are named after the relevant query. Query messages
 * have two parts:
 * <li>am: the Action message - action to take to address the problem e.g Do
 * this...</li>
 * <li>cm: the Compliance message - the condition that caused the message e.g
 * Expected this but found that...</li>
 * </p>
 * <p>
 * Ensure default response is English otherwise unhandled languages will not
 * produce a message.
 * 
 * @author Ian Davies - 14 Mar. 2021
 * 
 */
public class TextTranslations {
	private TextTranslations() {
	};

	// -------------------- ModelBuildErrorMsg ----
	/**
	 * Message on {@link ModelBuildErrors#MODEL_FILE_BACKUP}.
	 * <p>
	 * This is really a warning rather than an error. Note that, if
	 * {@link ConfgurationPropertyNames#P_FUNCTIONSNIPPET} are kept up-to-date, the
	 * newly generated file will contain any snippet contents.
	 * 
	 * @return action and compliance message.
	 */
	public static String[] getMODEL_FILE_BACKUP() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Mettre à jour le code dans le fichier Java nouvellement généré.";
			cm = "La configuration a changé et nécessite la création d'un nouveau fichier Java. Le fichier précédent a été sauvegardé et renommé en fichier texte numéroté (* .txt)";
		} else {
			am = "Update code in newly generated java file.";
			cm = "Configuration has changed requiring creation of a new Java file. The previous file has been backed up and renamed to a numbered text file (*.txt)";
		}
		String[] result = { am, cm };
		return result;
	};

	/**
	 * Errors reported on {@link ModelBuildErrors#COMPILER_ERROR}.
	 * 
	 * @param compileResult The error string returned by javac. This string is
	 *                      returned to the compliance item in the error message.
	 * @param snippets      List of code snippet function names.
	 * 
	 * @return action and compliance message.
	 */
	public static String[] getCOMPILER_ERROR(String compileResult, List<String> snippets) {
		// Note: is it possible to identify the list of snippets containing the error?
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
				compileResult = "inconnu.";
			cm = compileResult;
		} else {
			if (UserProjectLink.haveUserProject()) {
				am = "Correct coding errors in java project '" + UserProjectLink.projectRoot().getName() + "'";
				if (!snippets.isEmpty())
					am += " and/or code snippet properties of: " + snippets;
				am += ".";
			} else {
				am = "Correct coding errors in code snippet properties of: " + snippets + ".";
			}
			if (compileResult == null)
				compileResult = "unknown.";
			cm = compileResult;
		}

		String[] result = { am, cm };
		return result;
	}

	/**
	 * Message on {@link ModelBuildErrors#COMPILER_MISSING}.
	 * 
	 * @return action and compliance message.
	 */
	public static String[] getCOMPILER_MISSING() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Installer le 'Java Development Kit' (JDK).";
			cm = "Aucun compilateur Java n'a été trouvé.";
		} else {
			am = "Install Java Development Kit (JDK).";
			cm = "Expected Java compiler but none found.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Message on {@link ModelBuildErrors#DEPLOY_CLASS_MISSING}.
	 * 
	 * @param srcName The java source file
	 * @return action and compliance message.
	 */
	public static String[] getDEPLOY_CLASS_MISSING(String srcName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Un fichier compilé Java (.class) est manquant.";
			cm = "Fichier compilé attendu pour " + Language.oq + srcName + Language.cq + " mais aucun n'a été trouvé.";
		} else {
			am = "A Java class file is missing.";
			cm = "Expected class file for '" + srcName + "' but none found.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Message on {@link ModelBuildErrors#DEPLOY_CLASS_OUTOFDATE}.
	 * 
	 * @return action and compliance message.
	 */
	public static String[] getDEPLOY_CLASS_OUTOFDATE() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Actualiser le projet Java.";
			cm = "Un fichier compilé java (.class) est plus ancien que le fichier source Java.";
		} else {
			am = "Refresh Java project.";
			cm = "Compiled class file is older than Java source file.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Message on {@link ModelBuildErrors#DEPLOY_PROJECT_UNSAVED}.
	 * 
	 * @return action and compliance message.
	 */
	public static String[] getDEPLOY_PROJECT_UNSAVED() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Appuyez sur [Ctrl+s] pour enregistrer la configuration.";
			cm = "La configuration doit être enregistrée pour permettre le lancement des simulations.";
		} else {
			am = "Press [Ctrl+s] to save configuration.";
			cm = "Configuration must be saved to allowed deployment.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Message on {@link ModelBuildErrors#DEPLOY_RESOURCE_MISSING}.
	 * 
	 * @param resourceName The resource name.
	 * @param location     The expected location of the resource.
	 * @return action and compliance message.
	 */
	public static String[] getDEPLOY_RESOURCE_MISSING(String resourceName, String location) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajoutez <<" + Language.oq + resourceName + Language.cq + " à " + Language.oq + location + Language.cq
					+ ".";
			cm = "La ressource doit être présente pour le lancement des simulations.";
		} else {
			am = "Add '" + resourceName + "' to '" + location + "'.";
			cm = "Resource must be present for deployment";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Message on {@link ModelBuildErrors#DEPLOY_EXCEPTION}. The exception message
	 * is returned in the compliance part of the return message.
	 * 
	 * @param e The exception return from ProcessBuilder.
	 * @return action and compliance message.
	 */
	public static String[] getDEPLOY_EXCEPTION(Exception e) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Échec du lancement de ModelRunner.";
			cm = e.getMessage();
		} else {
			am = "Failed to launch ModelRunner.";
			cm = e.getMessage();
		}
		String[] result = { am, cm };
		return result;
	}

	// -------------------- Twcore queries ----

	// NB: name static function exactly after the relevant query
	/**
	 * Fail message for {@link EdgeToOneChildOfQuery}.
	 * 
	 * @param nodeRef Reference to the end node.
	 * @return action and compliance messages.
	 */
	public static String[] getEdgeToOneChildOfQuery(String nodeRef) {
		String am;// action message
		String cm;// constraint message
		if (Language.French()) {
			am = "Ajouter un lien vers un noeud fils de " + Language.oq + nodeRef + Language.cq + ".";
			cm = "Lien attendu vers un noeud fils de " + Language.oq + nodeRef + Language.cq + ", mais aucun trouvé.";
		} else {
			am = "Add edge to one child of '" + nodeRef + "'";
			cm = "Expected edge to one child of '" + nodeRef + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	};

	/**
	 * Fail message for {@link BaselineQuery}.
	 * 
	 * @param expRef    Reference to the experiment node.
	 * @param systems   References to all systems in the configuration
	 * @param edgeLabel Baseline edge label.
	 * @return action and compliance messages.
	 */
	public static String[] getBaselineQuery(String expRef, String[] systems, String edgeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter un lien de type '" + edgeLabel + "' vers un noeud parmi " + Arrays.deepToString(systems)
					+ ".";
			cm = "Lien de type '" + edgeLabel + "' vers un noeud parmi " + Arrays.deepToString(systems)
					+ " attendu, mais aucun trouvé.";
		} else {
			am = "Add edge labelled '" + edgeLabel + "' to one of " + Arrays.deepToString(systems) + ".";
			cm = "Expected an edge labelled '" + edgeLabel + "' to one of " + Arrays.deepToString(systems)
					+ " but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Crazy
	/**
	 * Fail message for {@link BorderTypeValidityQuery}.
	 * 
	 * @param stype    The type of space.
	 * @param nDim     Number of dimensions in the space definition.
	 * @param foundDim Number of dimensions that have been defined.
	 * @return action and compliance messages.
	 */
	public static String[] getBorderTypeValidityQuery1(SpaceType stype, int nDim, int foundDim) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Define " + (nDim * 2) + " borders for space type '" + stype + "'.";
			cm = "Expected Space of type " + stype + "' to define " + (nDim * 2) + " borders but found " + foundDim
					+ ".";
		} else {
			am = "Define " + (nDim * 2) + " borders for space type '" + stype + "'.";
			cm = "Expected Space of type " + stype + "' to define " + (nDim * 2) + " borders but found " + foundDim
					+ ".";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Crazy
	/**
	 * Fail message for {@link BorderTypeValidityQuery}.
	 * 
	 * @param i The dimension causing the query fail.
	 * @return action and compliance messages.
	 */
	public static String[] getBorderTypeValidityQuery2(int i) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit borders to pair Wrap-around in dimension " + i + ".";
			cm = "Expected Wrap-around in dimension " + i + " to be paired but was found unpaired.";
		} else {
			am = "Edit borders to pair Wrap-around in dimension " + i + ".";
			cm = "Expected Wrap-around in dimension " + i + " to be paired but was found unpaired.";
		}
		String[] result = { am, cm };
		return result;
	}

	// TODO Crazy
	/**
	 * Fail message for {@link BorderTypeValidityQuery}.
	 * 
	 * @return action and compliance messages.
	 */
	public static String[] getBorderTypeValidityQuery3() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit border property to wrap-around in the x-dimension.";
			cm = "Tubular wrap-around is only supported in the x-dimension.";
		} else {
			am = "Edit border property to wrap-around in the x-dimension.";
			cm = "Tubular wrap-around is only supported in the x-dimension.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link BoxInSpaceDimensionQuery}.
	 * 
	 * @param key          The property with incorrect dimensions.
	 * @param spaceId      The id of the space node.
	 * @param expectedDims The expected number of dimensions of the property.
	 * @param foundDims    The number of dimensions found.
	 * @return action and compliance messages.
	 */
	public static String[] getBoxInSpaceDimensionQuery(String key, String spaceId, Integer expectedDims,
			Integer foundDims) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Changer le nombre de dimensions de '" + key + "' pour " + expectedDims
					+ ", nombre de dimensions de l'espace '" + spaceId + "' qui le contient.";
			cm = "'" + key + "' devrait avoir " + expectedDims + " dimension(s) au lieu de " + foundDims
					+ " trouvée(s).";
		} else {
			am = "Edit dimensions of '" + key + "' to have " + expectedDims
					+ " dimensions, the same dimensions as its containing space '" + spaceId + "'.";
			cm = "Expected '" + key + "' to have " + expectedDims + " dimensions but found " + foundDims + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link CategoryConsistencyQuery}.
	 * 
	 * @param label    Label of the required edge.
	 * @param fromCat  Reference of the relevant category node.
	 * @param procName Reference of the relevant process node.
	 * @return action and compliance messages.
	 */
	public static String[] getCategoryConsistencyQuery(String label, String fromCat, String procName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter un lien de type '" + label + "': de la catégorie '" + fromCat + "' vers un noeud 'Record:'.";
			cm = "La catégorie '" + fromCat + "' (référencée par un lien 'appliesTo' du noeud '" + procName
					+ "') doit avoir un lien vers un noeud 'Record:'; aucun n'a été trouvé."
					+ " Toutes les catégories référencées par un noeud 'Process' doivent avoir un lien vers au moins un noeud 'Record:'.";
		} else {
			am = "Add edge '" + label + "': from '" + fromCat + "' to a 'Record:' node.";
			cm = "Expected '" + fromCat + "' (that is 'appliesTo' by '" + procName
					+ "') to have a link to a 'Record:' node but found none."
					+ " All categories that a process applies to must reference at least one record.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link CheckSubArchetypeQuery}.
	 * 
	 * @param fileName File name of the sub-archetype file.
	 * 
	 * @return action and compliance messages.
	 */
	public static String[] getCheckSubArchetypeQuery(String fileName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Corriger les erreurs dans le sous-archétype.";
			cm = "Le sous archétype '" + fileName + "' contient des erreurs.";
		} else {
			am = "Fix errors in sub-archetype.";
			cm = "Errors found in sub-archetype '" + fileName + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ChildAtLeastOneOfOneOrTwoOfTwoQuery}.
	 * 
	 * @param parentContainer The parent container [widget|container|tab]
	 * @param widgetLabel     A widget label
	 * @param containerLabel  A container label
	 * @return action and compliance messages.
	 */
	public static String[] getChildAtLeastOneOfOneOrTwoOfTwoQuery1(String parentContainer, String widgetLabel,
			String containerLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = " Ajouter un noeud fils de type '" + widgetLabel + "' ou '" + containerLabel + "'.";
			cm = "Un noeud de type '" + parentContainer + "' doit avoir un noeud fils de type '" + widgetLabel
					+ "' ou '" + containerLabel + "'.";
		} else {
			am = " Add child node '" + widgetLabel + "' or '" + containerLabel + "'.";
			cm = "Expected '" + parentContainer + "' must have a child node of '" + widgetLabel + "' or '"
					+ containerLabel + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ChildAtLeastOneOfOneOrTwoOfTwoQuery}.
	 * 
	 * @param parentContainer The parent container [widget|container|tab]
	 * @param widgetLabel     A widget label
	 * @param containerLabel  A container label
	 * @return action and compliance messages.
	 */
	public static String[] getChildAtLeastOneOfOneOrTwoOfTwoQuery2(String parentContainer, String widgetLabel,
			String containerLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit un noeud fils de type '" + widgetLabel + "', soit encore un noeud fils de type '"
					+ containerLabel + "'.";
			cm = "Un noeud de type '" + parentContainer + "' doit avoir soit un noeud fils de type '" + widgetLabel
					+ "', soit un noeud fils de type '" + containerLabel + "' de plus.";
		} else {
			am = "Add either a '" + widgetLabel + "' child or an additional '" + containerLabel + "' child.";
			cm = "Expected '" + parentContainer + "' to have a '" + widgetLabel + "' child or an additional '"
					+ containerLabel + "' child.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ChildXorPropertyQuery}.
	 * 
	 * @param propertyName Key of optional property.
	 * @param nodeLabel    Label of optional node
	 * @return action and compliance messages.
	 */
	public static String[] getChildXorPropertyQuery(String propertyName, String nodeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit la propriété '" + propertyName + "', soit un noeud fils de type '" + nodeLabel + "'.";
			cm = "Propriété '" + propertyName + "' ou noeud fils de type '" + nodeLabel
					+ "' attendus, mais aucun des deux n'a été trouvé.";
		} else {
			am = "Add either property '" + propertyName + "' or child node '" + nodeLabel + "'.";
			cm = "Expected either the property '" + propertyName + "' or the child '" + nodeLabel
					+ "' but found neither or both.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ConsequenceMatchFunctionTypeQuery}.
	 * 
	 * @param strValidTypes      Valid consequence function types.
	 * @param parentFunctionType The parent function type.
	 * @param foundType          The current function type.
	 * @return action and compliance messages.
	 */
	public static String[] getConsequenceMatchFunctionTypeQuery(String strValidTypes, String parentFunctionType,
			String foundType) {
		String am;
		String cm;
		if (Language.French()) {
			am = "La conséquence de la fonction '" + parentFunctionType + "' doit être redéfinie avec l'un des types "
					+ strValidTypes + ".";
			cm = "Conséquence de type " + strValidTypes + " attendue, mais type '" + foundType + "' trouvé.";
		} else {
			am = "Re-create Consequence function to have type of " + strValidTypes + " for function '"
					+ parentFunctionType + "'.";
			cm = "Expected type of function to be one of " + strValidTypes + " but found " + foundType + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link EdgeToSiblingNodesQuery}.
	 * 
	 * @param edgeLabel The edge label (type)
	 * @return action and compliance messages.
	 */
	public static String[] getEdgeToSiblingNodesQuery(String edgeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Changer les liens de type '" + edgeLabel + "' pour qu'ils pointent vers des noeuds frères.";
			cm = "Les liens de type '" + edgeLabel + "' doivent pointer vers des noeuds frères (ayant le même parent).";
		} else {
			am = "Change '" + edgeLabel + "' edges to connect to sibling nodes.";
			cm = "Expected '" + edgeLabel + "' edges to refer to sibling nodes.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link EdgeToSiblingNodesQuery}.
	 * 
	 * @param propertyKey The property key.
	 * @param edgeLabel   The edge label.
	 * @return action and compliance messages.
	 */
	public static String[] getEdgeXorPropertyQuery(String propertyKey, String edgeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit la propriété '" + propertyKey + "', soit un lien de type '" + edgeLabel
					+ "', mais pas les deux.";
			cm = "Propriété '" + propertyKey + "' ou lien de type '" + edgeLabel
					+ "' attendus, mais aucun des deux ou les deux ont été trouvés.";
		} else {
			am = "Add either property '" + propertyKey + "' or a '" + edgeLabel + "' edge.";
			cm = "Expected either property '" + propertyKey + "' or '" + edgeLabel
					+ "' edge but found neither or both.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ChildXorOutEdgeQuery}.
	 * 
	 * @param childLabel Child node type option.
	 * @param edgeLabel  Edge type option.
	 * @return action and compliance messages.
	 */
	public static String[] getChildXorOutEdgeQuery(String childLabel, String edgeLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter soit un ou plusieurs noeuds-enfants de type '" + childLabel
					+ "', soit un un ou plusieurs liens sortants de type '" + edgeLabel + "', mais pas les deux.";
			cm = "Noeuds-enfants de type '" + childLabel + "' ou liens sortants de type '" + edgeLabel
					+ "' attendus, mais aucun des deux ou les deux ont été trouvés.";
		} else {
			am = "Add either '" + childLabel + "' child nodes or '" + edgeLabel + "' out-edges, but do not have both.";
			cm = "Expected either '" + childLabel + "' child nodes or '" + edgeLabel
					+ "' out-edges but found neither or both.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link EndNodeHasPropertyQuery}.
	 * 
	 * @param nodeReference Reference to a leaf node.
	 * @param propertyKey   The key of the node property.
	 * @return action and compliance messages.
	 */
	public static String[] getEndNodeHasPropertyQuery(String nodeReference, String propertyKey) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter la propriété '" + propertyKey + "' au noeud terminal '" + nodeReference + "'.";
			cm = "Le noeud terminal '" + nodeReference + "' doit avoir la propriété '" + propertyKey + "'.";
		} else {
			am = "Add property '" + propertyKey + "' to leaf node '" + nodeReference + "'.";
			cm = "Expected leaf node '" + nodeReference + "' to have property '" + propertyKey + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ExclusiveCategoryQuery}.
	 * 
	 * @param nodeReference
	 * @param categorySetReference
	 * @return action and compliance messages.
	 */
	public static String[] getExclusiveCategoryQuery(String nodeReference, String categorySetReference) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Enlever un des liens de '" + nodeReference + "' vers une des catégories de '" + categorySetReference
					+ "'.";
			cm = "'" + nodeReference + "' ne peut pointer que vers une seule des catégories de l'ensemble '"
					+ categorySetReference + "'.";
		} else {
			am = "Remove edge from '" + nodeReference + "' to one of the categories of '" + categorySetReference + "'.";
			cm = "Expected '" + nodeReference + "' to have an edge to only one of the categories of '"
					+ categorySetReference + "' but found more.";
		}
		String[] result = { am, cm };
		return result;
	}
	// TODO: French done down to here

	/**
	 * Fail message for {@link ExclusiveChildPropertyValueQuery}.
	 * 
	 * @param target         The reference of target sibling
	 * @param key            Target's property key
	 * @param expectedValues Required property values
	 * @param nDiffSibs      Number of non-compliant siblings
	 * @return action and compliance messages.
	 */
	public static String[] getExclusiveChildPropertyValueQuery(String target, String key, List<Object> expectedValues,
			int nDiffSibs) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Either change property value of '" + key + "' to " + expectedValues + " for all sibling(s) of '"
					+ target + "' OR remove this node.";
			cm = "Expected siblings of '" + target + "' containing the property '" + key + "' to have the value(s) "
					+ expectedValues + "but found " + nDiffSibs + " sibling(s) with other values.";
		} else {
			am = "Either change property value of '" + key + "' to " + expectedValues + " for all sibling(s) of '"
					+ target + "' OR remove this node.";
			cm = "Expected siblings of '" + target + "' containing the property '" + key + "' to have the value(s) "
					+ expectedValues + "but found " + nDiffSibs + " sibling(s) with other values.";
		}
		String[] result = { am, cm };
		return result;
	}

//	/**
//	 * A Query to check that both nodes of an edge have a common parent of a
//	 * specified type
//	 */
	/**
	 * Fail message for {@link FindCommonCategoryQuery}.
	 * 
	 * @param trackName   Subject node reference
	 * @param processName Other node reference.
	 * @return action and compliance messages.
	 */
	public static String[] getFindCommonCategoryQuery(String trackName, String processName) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge from '" + trackName + "' to one of the categories of '" + processName + "'.";
			cm = "Expected '" + trackName + "' to belong to one of its '" + processName
					+ "' categories but found none.";
		} else {
			am = "Add edge from '" + trackName + "' to one of the categories of '" + processName + "'.";
			cm = "Expected '" + trackName + "' to belong to one of its '" + processName
					+ "' categories but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link FunctionMatchProcessTypeQuery}.
	 * 
	 * @param functionType String representation of {@link TwFunctionTypes} used by
	 *                     funcRef.
	 * @param processType  A Component or Relation.
	 * @param validTypes   List of possible {@link TwFunctionTypes}.
	 * @param funcRef      The function reference (Label:Name).
	 * @return action and compliance messages.
	 */
	public static String[] getFunctionMatchProcessTypeQuery(String functionType, String processType,
			List<String> validTypes, String funcRef) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Re-create '" + funcRef + "' using one of the function types " + validTypes + "'.";
			cm = "Expected '" + funcRef + "' to have type +" + validTypes + " but found '" + functionType
					+ "'. This is incompatible with a " + processType + " process.";
		} else {
			am = "Re-create '" + funcRef + "' with one of the function types " + validTypes + "'.";
			cm = "Expected '" + funcRef + "' to have type +" + validTypes + " but found '" + functionType
					+ "'. This is incompatible with a " + processType + " process.";
		}
		String[] result = { am, cm };
		return result;
	}

//	/**
//	 * Fail message for {@link GroupComponentRequirementQuery}.
//	 * @param groupName
//	 * @param instanceOfLabel
//	 * @param componentTypeLabel
//	 * @param groupOfLabel
//	 * @param groupTypeLabel
//	 * @return action and compliance messages.
//	 */
//	public static String[] getGroupComponentRequirementQuery(String groupName, String instanceOfLabel,
//			String componentTypeLabel, String groupOfLabel, String groupTypeLabel) {
//		String am;
//		String cm;
//		if (Language.French()) {
//			am = "Make '" + groupName + "' an '" + instanceOfLabel + "' some '" + componentTypeLabel
//					+ ":' OR make it a  '" + groupOfLabel + "' of some '" + groupTypeLabel + "'.";
//			cm = "Expected inEdge '" + instanceOfLabel + "' from some '" + componentTypeLabel + ":' OR outEdge '"
//					+ groupOfLabel + "' to some '" + groupTypeLabel + "' but found neither case.";
//		} else {
//			am = "Make '" + groupName + "' an '" + instanceOfLabel + "' some '" + componentTypeLabel
//					+ ":' OR make it a  '" + groupOfLabel + "' of some '" + groupTypeLabel + "'.";
//			cm = "Expected inEdge '" + instanceOfLabel + "' from some '" + componentTypeLabel + ":' OR outEdge '"
//					+ groupOfLabel + "' to some '" + groupTypeLabel + "' but found neither case.";
//		}
//		String[] result = { am, cm };
//		return result;
//	}

//	// TODO Improve : NB lifecycle is auto prepended
//	public static String[] getGroupInstanceRequirementQuery() {
//		String am;
//		String cm;
//		if (Language.French()) {
//			am = "Make sure that the GroupTypes define under a LifeCycleType have ComponentTypes which categories match those of the LifeCycleType CategorySet.";
//			cm = "All categories of a LifeCycleType category set must be present in its GroupType's ComponentTypes";
//		} else {
//			am = "Make sure that the GroupTypes define under a LifeCycleType have ComponentTypes which categories match those of the LifeCycleType CategorySet.";
//			cm = "All categories of a LifeCycleType category set must be present in its GroupType's ComponentTypes";
//		}
//		String[] result = { am, cm };
//		return result;
//	}

	/**
	 * Fail message for {@link GuardAreaMaxWidthQuery}.
	 * 
	 * @param width Current guard area width value.
	 * @return action and compliance messages.
	 */
	public static String[] getGuardAreaMaxWidthQuery(double width) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set property value to at most half the length of the shortest side.";
			cm = "Expected guard area width to be smaller than half the length of the shortest side but found " + width;
		} else {
			am = "Set property value to at most half the length of the shortest side.";
			cm = "Expected guard area width to be smaller than half the length of the shortest side but found " + width;
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link IndexDimensionQuery}.
	 * 
	 * @param ixs     Index range definition string.
	 * @param nodeRef Subject node reference (Label:name).
	 * @return action and compliance messages.
	 */
	public static String[] getIndexDimensionQuery(String ixs, String nodeRef) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + ixs + "' to be within range of the dimensions for '" + nodeRef + "'.";
			cm = "Index string '" + ixs + "' out of range for table '" + nodeRef + "'.";
		} else {
			am = "Edit '" + ixs + "' to be within range of the dimensions for '" + nodeRef + "'.";
			cm = "Index string '" + ixs + "' out of range for table '" + nodeRef + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link InputFileExistQuery}.
	 * 
	 * @param file File path to missing file.
	 * @return action and compliance messages.
	 */
	public static String[] getInputFileExistQuery(File file) {
		String am;
		String cm;
		if (Language.French()) {
			if (file != null) {
				am = "Add file '" + file.getName() + "' to the project.";
				cm = "Expected file '" + file.getName() + "-" + file.getParent() + "' but not found.";
			} else {
				am = "Select a file for this property.";
				cm = "Expected file to be defined but found null.";
			}
		} else {
			if (file != null) {
				am = "Add file '" + file.getName() + "' to the project.";
				cm = "Expected file '" + file.getName() + "-" + file.getParent() + "' but not found.";
			} else {
				am = "Select a file for this property.";
				cm = "Expected file to be defined but found null.";
			}
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link IsInIntervalQuery}.
	 * 
	 * @param value    The value which is not within the interval bounds.
	 * @param interval The bounding interval
	 * @return action and compliance messages.
	 */
	public static String[] getIsInIntervalQuery(Object value, Interval interval) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set value within the interval " + interval + ".";
			cm = "Expected value to be within " + interval + " but found '" + value + "'.";
		} else {
			am = "Set value within the interval " + interval + ".";
			cm = "Expected value to be within " + interval + " but found '" + value + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link IsBoundedIntervalQuery}.
	 * 
	 * @param interval The interval lacking bounds.
	 * @return action and compliance messages.
	 */
	public static String[] getIsBoundedIntervalQuery(Interval interval) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set interval to be bounded at both ends.";
			cm = "Expected interval to be bounded but found '" + interval + "'.";
		} else {
			am = "Set interval to be bounded at both ends.";
			cm = "Expected interval to be bounded but found '" + interval + "'.";
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
		} else {
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
		} else {
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
		} else {
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
		} else {
			am = "Remove a '" + toLbl + "' edge to a category.";
			cm = "Expected fewer '" + toLbl + "' for life cycle but found " + nToCats;
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link IsInRangeQuery}.
	 * 
	 * @param key   The property name.
	 * @param value The value that must be within range.
	 * @param min   Range minimum.
	 * @param max   Range maximum.
	 * @return action and compliance messages.
	 */
	public static String[] getIsInRangeQuery(String key, Object value, double min, double max) {
		String am;
		String cm;
		if (Language.French()) {
			if (max > 100_000_000)
				am = "Set '" + key + "' to be greater or equal to '" + min + "'.";
			else
				am = "Set '" + key + "' to be within the range " + min + " to " + max + ".";
			cm = "Expected '" + key + "' to be within the range [" + min + ":" + max + "] but found '" + value + "'.";
		} else {
			if (max > 100_000_000)
				am = "Set '" + key + "' to be greater or equal to '" + min + "'.";
			else
				am = "Set '" + key + "' to be within the range " + min + " to " + max + ".";
			cm = "Expected '" + key + "' to be within the range [" + min + ":" + max + "] but found '" + value + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link IsInValueSetQuery}.
	 * 
	 * @param key        Property name.
	 * @param valueSet   Table of allowed values.
	 * @param foundValue Value found.
	 * @return action and compliance messages.
	 */
	public static String[] getIsInValueSetQuery(String key, Table valueSet, Object foundValue) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph file with a text editor to change the property value of '" + key + " to a valid value.";
			cm = "Expected value of '" + key + "' to be one of " + valueSet.toString() + " but found '" + foundValue
					+ "'.";
		} else {
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
		} else {
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
		} else {
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
		} else {
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
		} else {
			am = "Reconfigure. '" + ctName + "' is not ephemeral but is processed by '" + fnName + "' of '" + pnName
					+ "' that only works on ephemeral ComponentTypes.";
			cm = "Expected '" + ctName + "' to belong to 'Category:*ephemeral*'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link NameStartsWithUpperCaseQuery}.
	 * 
	 * @param item Item name.
	 * @param c    Found first character.
	 * @return action and compliance messages.
	 */
	public static String[] getNameStartsWithUpperCaseQuery(String item, char c) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph with a text editor so '" + item + "' starts with an upper case character.";
			cm = "Expected first character to be upper case but found '" + c + "'.";
		} else {
			am = "Edit graph with a text editor so '" + item + "' starts with an upper case character.";
			cm = "Expected first character to be upper case but found '" + c + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link NodeAtLeastOneChildLabelOfQuery}.
	 * 
	 * @param labels Allowed labels.
	 * @return action and compliance messages.
	 */
	public static String[] getNodeAtLeastOneChildLabelOfQuery(List<String> labels) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter un noeud à l'un des «" + labels.toString() + "».";
			cm = "Attendu au moins un enfant intitulé «" + labels.toString() + "», mais n'en trouve aucun";
		} else {
			am = "Add node to one of '" + labels.toString() + "'.";
			cm = "Expected at least one child labelled '" + labels.toString() + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link NodeHasPropertyValueQuery}.
	 * 
	 * @param propertyName   The subject property key.
	 * @param expectedValues Allowed values.
	 * @param foundValue     Value found.
	 * @return action and compliance messages.
	 */
	public static String[] getNodeHasPropertyValueQuery(String propertyName, List<Object> expectedValues,
			Object foundValue) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph with a text editor to set '" + propertyName + "' value to one of '"
					+ expectedValues.toString() + "'.";
			cm = "Expected property '" + propertyName + "' to  have value '" + expectedValues.toString()
					+ "' but found '" + foundValue + "'.";
		} else {
			am = "Edit graph with a text editor to set '" + propertyName + "' value to one of '"
					+ expectedValues.toString() + "'.";
			cm = "Expected property '" + propertyName + "' to  have value '" + expectedValues.toString()
					+ "' but found '" + foundValue + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * First of two fail message for {@link OutEdgeXNorQuery}.
	 * 
	 * @param edgeLabel1 One allowed edge label.
	 * @param edgeLabel2 Other allowed edge label.
	 * @return action and compliance messages.
	 */
	public static String[] getOutEdgeXNorQuery1(String[] edgeLabel1, String[] edgeLabel2) {
		String am;
		String cm;
		// TODO: msg possibly incorrect
		if (Language.French()) {
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges and one of "
					+ Arrays.toString(edgeLabel2) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		} else {
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges and one of "
					+ Arrays.toString(edgeLabel2) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Second of two fail message for {@link OutEdgeXNorQuery}.
	 * 
	 * @param edgeLabel1 One allowed edge label.
	 * @param edgeLabel2 Other allowed edge label.
	 * @return action and compliance messages.
	 */
	public static String[] getOutEdgeXNorQuery2(String[] edgeLabel1, String[] edgeLabel2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		} else {
			am = "Add at least one of " + Arrays.toString(edgeLabel1) + " edges.";
			cm = "Expected at least one edge labelled from " + Arrays.toString(edgeLabel1) + " and one edge labelled "
					+ Arrays.toString(edgeLabel2) + " but condition not met.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link OutEdgeXorQuery}.
	 * 
	 * @param edgeLabel1 One allowed edge label.
	 * @param edgeLabel2 Other allowed edge label.
	 * @return action and compliance messages.
	 */
	public static String[] getOutEdgeXorQuery(String[] edgeLabel1, String[] edgeLabel2) {
		// TODO: List what was found
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge '" + edgeLabel1 + " or " + Arrays.toString(edgeLabel2) + ".";
			cm = "Expected at least one edge labelled either " + Arrays.toString(edgeLabel1) + " or "
					+ Arrays.toString(edgeLabel2) + " but none found.";
		} else {
			am = "Add edge '" + edgeLabel1 + " or " + Arrays.toString(edgeLabel2) + ".";
			cm = "Expected at least one edge labelled either " + Arrays.toString(edgeLabel1) + " or "
					+ Arrays.toString(edgeLabel2) + " but none found.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link OutEdgeXorQuery}.
	 * 
	 * @param nodeLabel1 One allowed node label.
	 * @param nodeLabel2 Other allowed node label.
	 * @return action and compliance messages.
	 */
	public static String[] getOutNodeXorQuery(String nodeLabel1, String nodeLabel2) {
		// TODO: List what was found
		String am;
		String cm;
		if (Language.French()) {
			am = "Add edge to a node labelled either '" + nodeLabel1 + ":' or '" + nodeLabel2 + ":' but not both.";
			cm = "Expected edge to a node labelled either '" + nodeLabel1 + "' or '" + nodeLabel2 + "'.";
		} else {
			am = "Add edge to a node labelled either '" + nodeLabel1 + ":' or '" + nodeLabel2 + ":' but not both.";
			cm = "Expected edge to a node labelled either '" + nodeLabel1 + "' or '" + nodeLabel2 + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ParentClassQuery}.
	 * 
	 * @param klasses       Allowed subclasses
	 * @param foundSubclass
	 * @return action and compliance messages.
	 */
	public static String[] getParentClassQuery(List<String> klasses, String foundSubclass) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set parent to one of " + klasses + ".";
			cm = "Expected parent subclass to be one of " + klasses + " but found '" + foundSubclass + "'.";
		} else {
			am = "Set parent to one of " + klasses + ".";
			cm = "Expected parent subclass to be one of " + klasses + " but found '" + foundSubclass + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ParentHasPropertyValue}.
	 * 
	 * @param propertyName  Property name (key).
	 * @param foundValue    Current value.
	 * @param allowedValues Allowed values.
	 * @return action and compliance messages.
	 */
	public static String[] getParentHasPropertyValue(String propertyName, Object foundValue,
			List<Object> allowedValues) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change property value of '" + propertyName + "' to one of " + allowedValues + ".";
			cm = "Expected '" + propertyName + "' value to be one of '" + allowedValues.toString() + "' but found '"
					+ foundValue + "'.";
		} else {
			am = "Change property value of '" + propertyName + "' to one of " + allowedValues + ".";
			cm = "Expected '" + propertyName + "' value to be one of '" + allowedValues.toString() + "' but found '"
					+ foundValue + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link ParentLabelQuery}.
	 * 
	 * @param allowedLabels List of allowable values.
	 * @param foundParent   Reference of parent found.
	 * @return action and compliance messages.
	 */
	public static String[] getParentLabelQuery(List<String> allowedLabels, String foundParent) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change parent to be one of '" + allowedLabels + "'.";
			cm = "Expected parent to be on of '" + allowedLabels + "' but found '" + foundParent + "'.";
		} else {
			am = "Change parent to be one of '" + allowedLabels + "'.";
			cm = "Expected parent to be on of '" + allowedLabels + "' but found '" + foundParent + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link PropertyXorQuery}.
	 * 
	 * @param propertyKey1 One property name.
	 * @param propertyKey2 Other property name.
	 * @return action and compliance messages.
	 */
	public static String[] getPropertyXorQuery(String propertyKey1, String propertyKey2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit graph file with text editor to remove one of the properties '" + propertyKey1 + "' or '"
					+ propertyKey2 + "'.";
			cm = "Expected property named either '" + propertyKey1 + "' or '" + propertyKey2 + "' but not both.";
		} else {
			am = "Edit graph file with text editor to remove one of the properties '" + propertyKey1 + "' or '"
					+ propertyKey2 + "'.";
			cm = "Expected property named either '" + propertyKey1 + "' or '" + propertyKey2 + "' but not both.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link RankingPropertyQuery} to test nodes.
	 * 
	 * @param propName      Property name (key).
	 * @param allowedValues List of allowable property values.
	 * @param foundValues   List of values found.
	 * @param nodeReference Reference to the node containing the property.
	 * @return action and compliance messages.
	 */
	public static String[] getRankingPropertyQuery1(String propName, String allowedValues, String foundValues,
			String nodeReference) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + propName + "' values for nodes [" + allowedValues + "] to unique values.";
			cm = "Expected '" + propName + "' values for children of '" + nodeReference
					+ "' to be unique but found values [" + foundValues + ".";
		} else {
			am = "Edit '" + propName + "' values for nodes [" + allowedValues + "] to unique values.";
			cm = "Expected '" + propName + "' values for children of '" + nodeReference
					+ "' to be unique but found values [" + foundValues + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link RankingPropertyQuery} to test edges.
	 * 
	 * @param propName      Property name (key).
	 * @param allowedValues List of allowable property values.
	 * @param foundValues   List of values found.
	 * @param edgeReference Reference to the edge containing the property.
	 * @return action and compliance messages.
	 */
	public static String[] getRankingPropertyQuery2(String propName, String allowedValues, String foundValues,
			String edgeReference) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + propName + "' values for elements [" + allowedValues + "] to unique values.";
			cm = "Expected '" + propName + "' values of '" + edgeReference + "' to be unique but found " + foundValues
					+ ".";
		} else {
			am = "Edit '" + propName + "' values for elements [" + allowedValues + "] to unique values.";
			cm = "Expected '" + propName + "' values of '" + edgeReference + "' to be unique but found " + foundValues
					+ ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link RecordUsedByAtMostOneCategoryQuery}.
	 * 
	 * @param target End node.
	 * @param nEdges Number of edges found.
	 * @return action and compliance messages.
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
		} else {
			if (nEdges == 0) {
				am = "Add an edge from a Category to '" + target + "'.";
			} else
				am = "Remove all but one in edge from a Category to '" + target + "'.";
			cm = "Expected 1 in-edge to '" + target + "' but found " + nEdges + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link RequirePropertyQuery}.
	 * 
	 * @param p1 Required property.
	 * @param p2 Found property.
	 * @return action and compliance messages.
	 */
	public static String[] getRequirePropertyQuery(String p1, String p2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Remove property '" + p1
					+ "' by editing the configuration graph with a text editor. [ModelMaker programming error!].";
			cm = "Presence of property '" + p1 + "' is incompatible with value of property '" + p2 + "'.";
		} else {
			am = "Remove property '" + p1
					+ "' by editing the configuration graph with a text editor. [ModelMaker programming error!].";
			cm = "Presence of property '" + p1 + "' is incompatible with value of property '" + p2 + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link SearchProcessConsistencyQuery}.
	 * 
	 * @param processReference Reference to the Process node.
	 * @param spaceReference   Reference to the Space node.
	 * @param foundReferences  List of references of associated nodes.
	 * @return action and compliance messages.
	 */
	public static String[] getSearchProcessConsistencyQuery(String processReference, String spaceReference,
			String foundReferences) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Reconfigure graph so that all componentTypes processed by '" + processReference
					+ "' have valid coordinates for '" + spaceReference + "'.";
			cm = "Expected all componentTypes processed by '" + processReference + "' to have valid coordinates for '"
					+ spaceReference + "' but found associations with '" + foundReferences + "'.";
		} else {
			am = "Reconfigure graph so that all componentTypes processed by '" + processReference
					+ "' have valid coordinates for '" + spaceReference + "'.";
			cm = "Expected all componentTypes processed by '" + processReference + "' to have valid coordinates for '"
					+ spaceReference + "' but found associations with '" + foundReferences + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link SenderInRangeQuery}.
	 * 
	 * @param propertyName Name (key) of the property.
	 * @param allowedRange Allowable listening range
	 * @param foundRange   Found range of simulators.
	 * @param nReps        Number of replicate simulations.
	 * @param firstSender  Lowest simulator id.
	 * @return action and compliance messages.
	 */
	public static String[] getSenderInRangeQuery(String propertyName, IntegerRange allowedRange,
			IntegerRange foundRange, int nReps, int firstSender) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit property '" + propertyName + "' to receive data in the range " + allowedRange + ".";
			cm = "Expected sufficent simulator(s) to send data in the range " + foundRange + " but found only " + nReps
					+ " simulator(s). [" + propertyName + "=" + firstSender + "]";
		} else {
			am = "Edit property '" + propertyName + "' to receive data in the range " + allowedRange + ".";
			cm = "Expected sufficent simulator(s) to send data in the range " + foundRange + " but found only " + nReps
					+ " simulator(s). [" + propertyName + "=" + firstSender + "]";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link SpaceCoordinateTypeQuery}.
	 * 
	 * @param foundType Field type found.
	 * @return action and compliance messages.
	 */
	public static String[] getSpaceCoordinateTypeQuery(String foundType) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change coordinate fields to be numeric.";
			cm = "Expected coordinate fields to be numeric but found '" + foundType + "'.";
		} else {
			am = "Change coordinate fields to be numeric.";
			cm = "Expected coordinate fields to be numeric but found '" + foundType + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link SpaceDimensionConsistencyQuery}.
	 * 
	 * @param dif       difference in found and expected dimensions.
	 * @param dimension Expected number of dimensions.
	 * @param label     Edge type.
	 * @return action and compliance messages.
	 */
	public static String[] getSpaceDimensionConsistencyQuery(int dif, int dimension, String label) {
		String am;
		String cm;
		if (Language.French()) {
			if (dif > 0)
				am = "Add " + dif + " '" + label + ":' edges.";
			else
				am = "Remove " + dif + " '" + label + ":' edges.";
			cm = "Expected " + dimension + " " + label + " edges but found " + (dimension + dif) + ".";
		} else {
			if (dif > 0)
				am = "Add " + dif + " '" + label + ":' edges.";
			else
				am = "Remove " + dif + " '" + label + ":' edges.";
			cm = "Expected " + dimension + " " + label + " edges but found " + (dimension + dif) + ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link SpaceRecordTypeQuery}.
	 * 
	 * @param fieldNodeReferences Reference of the subject Field nodes.
	 * @return action and compliance messages.
	 */
	public static String[] getSpaceRecordTypeQuery(String fieldNodeReferences) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set coordinate field(s) to belong to a record that is used as either a driver or constant.";
			cm = "Expected coordinate fields '" + fieldNodeReferences
					+ "' to belong to a record used as drivers or constants but found none.";
		} else {
			am = "Set coordinate field(s) to belong to a record that is used as either a driver or constant.";
			cm = "Expected coordinate fields '" + fieldNodeReferences
					+ "' to belong to a record used as drivers or constants but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link TimeUnitValidityQuery}.
	 * 
	 * @param key        Property name.
	 * @param validUnits List of valid time units.
	 * @param foundValue Found time unit.
	 * @return action and compliance messages.
	 */
	public static String[] getTimeUnitValidityQuery(String key, String validUnits, String foundValue) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Change '" + key + "' value to be one of " + validUnits + ".";
			cm = "Expected value of '" + key + "' to be one of " + validUnits + " but found '" + foundValue + "'.";
		} else {
			am = "Change '" + key + "' value to be one of " + validUnits + ".";
			cm = "Expected value of '" + key + "' to be one of " + validUnits + " but found '" + foundValue + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link UICanStopQuery}.
	 * 
	 * @param scLabel  Stopping condition label.
	 * @param dynLabel Dynamics label.
	 * @return action and compliance messages.
	 */
	public static String[] getUICanStopQuery(String scLabel, String dynLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add a " + scLabel + " child to " + dynLabel + ".";
			cm = "Expected at least one" + scLabel + "for unattended simulation but found none.";
		} else {
			am = "Add a " + scLabel + " child to " + dynLabel + ".";
			cm = "Expected at least one" + scLabel + "for unattended simulation but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link UIStateMachineControllerQuery}.
	 * 
	 * @param klass            Base class of controllers.
	 * @param foundControllers List of found controllers
	 * @return action and compliance messages.
	 */
	public static String[] getUIStateMachineControllerQuery(String klass, List<String> foundControllers) {
		String am;
		String cm;
		if (Language.French()) {
			if (foundControllers.isEmpty())
				am = "Add a control widget to either [top,bottom,tab,container].";
			else
				am = "Remove one of " + foundControllers + ".";
			cm = "Expected one widget that descends from '" + klass
					+ "' as child of [top,bottom,tab,container] but found " + foundControllers.size() + ".";
		} else {
			if (foundControllers.isEmpty())
				am = "Add a control widget to either [top,bottom,tab,container].";
			else
				am = "Remove one of " + foundControllers + ".";
			cm = "Expected one widget that descends from '" + klass
					+ "' as child of [top,bottom,tab,container] but found " + foundControllers.size() + ".";
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
		} else {
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
		} else {
			am = "Do something!";
			cm = "Expected '" + requiredFunc + "' function type in process '" + procName + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * First of four fail messages for {@link TimeIntervalValidityQuery}. Reports
	 * when time line type is arbitrary.
	 * 
	 * @param shortestTimeUnitKey Property name of shortest time unit.
	 * @param longestTimeUnitKey  Property name of longest time unit.
	 * @param timeLineRef         Reference to the Timeline node.
	 * @param allowedMin          Allowed minimum time unit.
	 * @param allowedMax          Allowed maximum time unit.
	 * @param foundShortest       Found shortest time unit.
	 * @param foundLongest        Found longest time unit.
	 * @param scaleKey            Current time scale type property.
	 * @return action and compliance messages.
	 */
	public static String[] getTimeIntervalValidityQuery1(String shortestTimeUnitKey, String longestTimeUnitKey,
			String timeLineRef, String allowedMin, String allowedMax, String foundShortest, String foundLongest,
			String scaleKey) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '" + timeLineRef + "' to '"
					+ allowedMax + "'.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + timeLineRef
					+ "' to be '" + allowedMax + "' for '" + scaleKey + "' but found '" + foundShortest + "' and '"
					+ foundLongest + "'.";
		} else {
			am = "Set '" + shortestTimeUnitKey + "' or '" + longestTimeUnitKey + "' of '" + timeLineRef + "' to '"
					+ allowedMax + "'.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + timeLineRef
					+ "' to be '" + allowedMax + "' for '" + scaleKey + "' but found '" + foundShortest + "' and '"
					+ foundLongest + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Second of four fail messages for {@link TimeIntervalValidityQuery}. Reports
	 * when time line type is mono.
	 * 
	 * @param shortestTimeUnitKey Property name of shortest time unit.
	 * @param longestTimeUnitKey  Property name of longest time unit.
	 * @param timelineRef         Reference to the Timeline node.
	 * @param scaleKey            Current time scale type property.
	 * @param foundShortest       Found shortest time unit.
	 * @param foundLongest        Found longest time unit.
	 * @return action and compliance messages.
	 */
	public static String[] getTimeIntervalValidityQuery2(String shortestTimeUnitKey, String longestTimeUnitKey,
			String timelineRef, String scaleKey, String foundShortest, String foundLongest) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + timelineRef
					+ "' to the same value.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + timelineRef
					+ "' must be the same for '" + scaleKey + "' but found '" + foundShortest + "' and '" + foundLongest
					+ "'.";
		} else {
			am = "Set '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + timelineRef
					+ "' to the same value.";
			cm = "Expected '" + shortestTimeUnitKey + "' and '" + longestTimeUnitKey + "' of '" + timelineRef
					+ "' must be the same for '" + scaleKey + "' but found '" + foundShortest + "' and '" + foundLongest
					+ "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Third of four fail messages for {@link TimeIntervalValidityQuery}. Reports
	 * when shortest is greater then longest.
	 * 
	 * @param shortestKey   Property name of shortest time unit.
	 * @param longestKey    Property name of longest time unit.
	 * @param scaleType     Current time scale type property.
	 * @param foundShortest Found shortest time unit.
	 * @param foundLongest  Found longest time unit.
	 * @return action and compliance messages.
	 */
	public static String[] getTimeIntervalValidityQuery3(String shortestKey, String longestKey, String scaleType,
			String foundShortest, String foundLongest) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set '" + shortestKey + "' to be less than or equal to '" + longestKey + "'.";
			cm = "Expected '" + shortestKey + "' to be <= '" + longestKey + "' for time scale type '" + scaleType
					+ "' but found '" + foundShortest + "' and '" + foundLongest + "'.";
		} else {
			am = "Set '" + shortestKey + "' to be less than or equal to '" + longestKey + "'.";
			cm = "Expected '" + shortestKey + "' to be <= '" + longestKey + "' for time scale type '" + scaleType
					+ "' but found '" + foundShortest + "' and '" + foundLongest + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Forth of four fail messages for {@link TimeIntervalValidityQuery}. Reports
	 * when the time line range is unnecessarily wide.
	 * 
	 * @param key      Subject property name.
	 * @param expected Expected value.
	 * @param found    Current value.
	 * @return action and compliance messages.
	 */
	public static String[] getTimeIntervalValidityQuery4(String key, String expected, String found) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set property '" + key + "' value to '" + expected
					+ "' or change the time units of associated Timers.";
			cm = "Expected value of '" + key + " to be " + expected + "' but found '" + found + "'.";
		} else {
			am = "Set property '" + key + "' value to '" + expected
					+ "' or change the time units of associated Timers.";
			cm = "Expected value of '" + key + " to be " + expected + "' but found '" + found + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link DynamicsMustHaveAtLeastOneFunction}.
	 * 
	 * @return action and compliance messages.
	 */
	public static String[] DynamicsMustHaveAtLeastOneFunctionQuery() {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add at least one 'function:' node to a process in the 'dynamics:' sub-tree.";
			cm = "Expected at least one 'function:' node but found none.";
		} else {
			am = "Add at least one 'function:' node to a process in the 'dynamics:' sub-tree.";
			cm = "Expected at least one 'function:' node but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link TreatmentTableQuery} when there is a type mismatch.
	 * 
	 * @param edgeRef      Edge reference
	 * @param key          Property name.
	 * @param foundType    Current data type.
	 * @param expectedType Required data type.
	 * @param i            Index of key
	 * @return action and compliance messages.
	 */
	public static String[] getTreatmentTableQuery2(String edgeRef, String key, String foundType,
			DataElementType expectedType, int i) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Modifier la valeur de '" + edgeRef + "#" + key + "[" + i + "]' pour une valeur de type '"
					+ expectedType + "'.";
			cm = "Valeur de '" + edgeRef + "#" + key + "[" + i + "]' de type , '" + expectedType + "' attendue, type '"
					+ foundType + "' trouvé.";
		} else {
			am = "Edit '" + edgeRef + "#" + key + "[" + i + "]' to a '" + expectedType + "' type value.";
			cm = "Expected '" + edgeRef + "#" + key + "[" + i + "]' to be of type '" + expectedType + "' but found '"
					+ foundType + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link TreatmentTableQuery} when there are no treatments
	 * levels set.
	 * 
	 * @param edgeRef Edge reference.
	 * @param key     Property name.
	 * @return action and compliance messages.
	 */
	public static String[] getTreatmentTableQuery(String edgeRef, String key) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Ajouter au moins une valeur à la variable '" + edgeRef + "#" + key + "'.";
			cm = "La variable '" + edgeRef + "#" + key + "' ne contient aucune valeur utilisable.";
		} else {
			am = "Add at least one value to '" + edgeRef + "#" + key + "'.";
			cm = "Expected '" + edgeRef + "#" + key + "' to contain at least one value, but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link HasValidFileNameChars}.
	 * 
	 * @param filename Current file name.
	 * @return action and compliance messages.
	 */
	public static String[] getHasValidFileNameChars(String filename) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Edit '" + filename + "' to a valid file name";
			cm = "Expected valid file name but found '" + filename + "'.";
		} else {
			am = "Edit '" + filename + "' to a valid file name";
			cm = "Expected valid file name but found '" + filename + "'.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link OutNodeOrQuery}.
	 * 
	 * @param nodeLabel1  One node label option.
	 * @param nodeLabel2  Other node label option.
	 * @param foundNodes1 Current list of nodes for one option.
	 * @param foundNodes2 Current list of nodes for other option.
	 * @return action and compliance messages.
	 */
	public static String[] getOutNodeOrQuery(String nodeLabel1, String nodeLabel2, List<Node> foundNodes1,
			List<Node> foundNodes2) {
		String am;
		String cm;
		List<String> lst = new ArrayList<>();
		for (Node n : foundNodes1)
			lst.add(n.toShortString());
		for (Node n : foundNodes2)
			lst.add(n.toShortString());

		if (Language.French()) {
			am = "Add at least one edge to a node labelled '" + nodeLabel1 + ":' or '" + nodeLabel2 + ":'.";
			cm = "Expected edge to a node labelled either '" + nodeLabel1 + "' or '" + nodeLabel2 + "' but found " + lst
					+ ".";
		} else {
			am = "Add at least one edge to a node labelled '" + nodeLabel1 + ":' or '" + nodeLabel2 + ":'.";
			cm = "Expected edge to a node labelled either '" + nodeLabel1 + "' or '" + nodeLabel2 + "' but found " + lst
					+ ".";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link TreatmentExistsQuery}.
	 * 
	 * @param edt            Experiment type.
	 * @param treatmentLabel The label of the required node.
	 * @return action and compliance messages.
	 */
	public static String[] getTreatmentExistsQuery(String edt, String treatmentLabel) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Add a '" + treatmentLabel + "' node for '" + edt + "' experiments.";
			cm = "Expected '" + edt + "' experiments to have one '" + treatmentLabel + "' node but found none.";
		} else {
			am = "Add a '" + treatmentLabel + "' node for '" + edt + "' experiments.";
			cm = "Expected '" + edt + "' experiments to have one '" + treatmentLabel + "' node but found none.";
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link TableDimsMustMatch}.
	 * 
	 * @param key1 First property name.
	 * @param key2 Second property name.
	 * @return action and compliance messages.
	 */
	public static String[] getTableDimsMustMatch(String key1, String key2) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Set the number of elements of the tables '" + key1 + "' and '" + key2 + "' to be the same.";
			cm = "Expected tables '" + key1 + "' and '" + key2
					+ "' to have identical dimensions and size but found they differ.";
		} else {
			am = "Set the number of elements of the tables '" + key1 + "' and '" + key2 + "' to be the same.";
			cm = "Expected tables '" + key1 + "' and '" + key2
					+ "' to have identical dimensions and size but found they differ.";
// and i can't be bothered telling you how they differ.
		}
		String[] result = { am, cm };
		return result;
	}

	/**
	 * Fail message for {@link CheckConstantTrackingQuery}.
	 * 
	 * @param edgeRef         Reference to the subject edge.
	 * @param fieldOrTableRef Reference to the end node.
	 * @return action and compliance messages.
	 */
	public static String[] getCheckConstantTrackingQuery(String edgeRef, String fieldOrTableRef) {
		String am;
		String cm;
		if (Language.French()) {
			am = "Delete '" + edgeRef + "'. Constants cannot be tracked.";
			cm = "Expected '" + edgeRef + "' to track a driver or decorator but found data '" + fieldOrTableRef
					+ "' to be a constant.";
		} else {// ensure default is English or unhandled languages will produce no message.
			am = "Delete '" + edgeRef + "'. Constants cannot be tracked.";
			cm = "Expected '" + edgeRef + "' to track a driver or decorator but found data '" + fieldOrTableRef
					+ "' to be a constant.";
		}
		String[] result = { am, cm };
		return result;
	}
}

// COPY AND PASTE THIS CONVENIENCE TEMPLATE
//public static String[] getXXX() {
//String am; // Action message
//String cm; // Constraint message
//if (Language.French()) {
//	am = "";
//	cm = "";
//} else { // default
//	am = "";
//	cm = "";
//}
//String[] result = { am, cm };
//return result;
//}
