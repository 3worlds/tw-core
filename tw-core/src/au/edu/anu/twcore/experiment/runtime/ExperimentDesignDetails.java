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
package au.edu.anu.twcore.experiment.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.rscs.aot.graph.property.Property;
import au.edu.anu.twcore.experiment.ExpFactor;
import fr.cnrs.iees.twcore.constants.ExperimentDesignType;

/**
 * @author Ian Davies - 11 July 2022
 */
public final class ExperimentDesignDetails implements IEdd {
	private final List<List<Property>> treatments;
	private final Map<String, ExpFactor> factors;
	private final Map<String, Object> baseline;
	private final String precis;
	private final int nReplicates;
	private final ExperimentDesignType edt;
	private final File designFile;
	private final String expDir;

	private ExperimentDesignDetails(String precis, int nReplicates, ExperimentDesignType edt, File designFile,
			String expDir) {
		this.edt = edt;
		this.precis = precis;
		this.nReplicates = nReplicates;
		this.treatments = new ArrayList<>();
		this.factors = new HashMap<>();
		this.baseline = new HashMap<>();
		this.designFile = designFile;
		this.expDir = expDir;

	}

	@Override
	public List<List<Property>> treatments() {
		return treatments;
	}

	@Override
	public Map<String, ExpFactor> factors() {
		return factors;
	}

	@Override
	public Map<String, Object> baseline() {
		return baseline;
	}

	@Override
	public ExperimentDesignType getType() {
		return edt;
	}

	@Override
	public String getExpDir() {
		return expDir;
	}

	private boolean isCustom() {
		return edt == null ? true : false;
	}

	@Override
	public String toDetailString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type\t");
		if (isCustom())
			sb.append(designFile);
		else {
			sb.append(edt.name()).append("\n");
			sb.append("Type description\t").append(edt.description());
		}
		sb.append("\n");
		sb.append("precis\t").append(precis).append("\n");

		sb.append("Replicates\t").append(nReplicates);
		sb.append("\n");
		if (!isCustom()) {
			if (edt.equals(ExperimentDesignType.crossFactorial)) {

				sb.append(nReplicates);
				factors.forEach((k, v) -> {
					sb.append(" x ").append(k).append("(").append(v.nLevels()).append(")");
				});
				sb.append("=").append(treatments.size() * nReplicates).append("\n");

			} else if (edt.equals(ExperimentDesignType.sensitivityAnalysis)) {
				sb.append(nReplicates).append(" x (");
				factors.forEach((k, v) -> {
					sb.append(" + ").append(k).append("(").append(v.nLevels()).append(")");
				});
				sb.append(") =").append(treatments.size() * nReplicates).append("\n");
			}
		}
		sb.append("\n");

		sb.append("Baseline\n");
		sb.append("Name\tValue\n");
		baseline.forEach((k, v) -> {
			sb.append(k).append("\t").append(v).append("\n");
		});
		sb.append("\n");

		sb.append("Treatments (parallel deployment)\n");
		sb.append("SimId\tTreatment\n");
		int simId = 0;
		for (int i = 0; i < nReplicates; i++) {
			for (int j = 0; j < treatments.size(); j++) {
				sb.append(simId++).append("\t");
				List<Property> properties = treatments.get(j);
				for (Property p : properties) {
					ExpFactor f = factors.get(p.getKey());
					String vn = f.getValueName(p);
					sb.append(f.getName()).append("(").append(vn).append(")\t");
				}
				sb.append("\n");

			}
		}

		sb.append("\n");
		sb.append("Factors\n");
		factors.forEach((k, v) -> {
			sb.append(v).append("\n");

		});

		return sb.toString();
	}

	@Override
	public int getReplicateCount() {
		return nReplicates;
	}

	@Override
	public Map<String, ExpFactor> getFactors() {
		return Collections.unmodifiableMap(factors);
	}

	@Override
	public List<List<Property>> getTreatments() {
		return Collections.unmodifiableList(treatments);
	}

	@Override
	public Map<String, Object> getBaseline() {
		return Collections.unmodifiableMap(baseline);
	}

	public static IEdd makeDetails(String precis, int nReplicates, ExperimentDesignType edt, File designFile,
			String expDir) {
		return new ExperimentDesignDetails(precis, nReplicates, edt, designFile, expDir);

	}

}
