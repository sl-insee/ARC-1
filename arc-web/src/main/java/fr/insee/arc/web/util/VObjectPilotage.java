package fr.insee.arc.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import fr.insee.arc.core.model.TraitementEtat;
import fr.insee.arc.core.model.TraitementPhase;
import fr.insee.arc.utils.structure.GenericBean;
import fr.insee.arc.utils.utils.ManipString;

@Service
public class VObjectPilotage extends VObjectService {

	@Override
    public ArrayList<ArrayList<String>> reworkContent(ArrayList<ArrayList<String>> content) {
		GenericBean g = new GenericBean(content);
		HashMap<String, ArrayList<String>> mapContent = g.mapContent();
		HashMap<String, String> mt = g.mapTypes();

		ArrayList<String> newHeaders = new ArrayList<>();

		ArrayList<ArrayList<String>> newContent = new ArrayList<>();

		// ne garder les colonnes avec au moins un enregistrements dedans

		newHeaders.add("date_entree");

		for (Map.Entry<String, ArrayList<String>> entry : mapContent.entrySet()) {
			boolean toKeep = false;
			int i = 0;

			while (i < entry.getValue().size() && !entry.getKey().equals("date_entree")) {

				if (Integer.parseInt(entry.getValue().get(i)) > 0) {
					toKeep = true;
				}
				i++;
			}

			if (toKeep) {
				newHeaders.add(entry.getKey());
			}
		}

		// ordonner les colonnes selon la phase et l'etat
		Collections.sort(newHeaders, new Comparator<String>() {
			public int compare(String a, String b) {
				String phaseA = ManipString.substringBeforeLast(a, "_").toUpperCase();
				String etatA = ManipString.substringAfterLast(a, "_").toUpperCase();

				String phaseB = ManipString.substringBeforeLast(b, "_").toUpperCase();
				String etatB = ManipString.substringAfterLast(b, "_").toUpperCase();

				//Check if one of the two headers is date_entree. Because date_entree is not a TypeTraitementPhase
				try {
					TraitementPhase.valueOf(phaseA);
				} catch (Exception e) {
					return -1;
				}

				try {
					TraitementPhase.valueOf(phaseB);
				} catch (Exception e) {
					return 1;
				}

				if (TraitementPhase.valueOf(phaseA).getOrdre() > TraitementPhase.valueOf(phaseB).getOrdre()) {
					return 1;
				}
				if (TraitementPhase.valueOf(phaseA).getOrdre() < TraitementPhase.valueOf(phaseB).getOrdre()) {
					return -1;
				}
				if (TraitementEtat.valueOf(etatA).getOrdre() > TraitementEtat.valueOf(etatB).getOrdre()) {
					return 1;
				}
				if (TraitementEtat.valueOf(etatA).getOrdre() < TraitementEtat.valueOf(etatB).getOrdre()) {
					return -1;
				}
				return 0;

			}
		});

		// ajouter les coloonnes
		newContent.add(newHeaders);

		// ajout des types des colonnes
		ArrayList<String> newTypes = new ArrayList<String>();

		for (int j = 0; j < newHeaders.size(); j++) {
			newTypes.add(mt.get(newHeaders.get(j)));
		}

		newContent.add(newTypes);

		// ajout du contenu relatifs aux colonnes
		if (!mapContent.isEmpty()) {
			for (int k = 0; k < mapContent.get(newHeaders.get(0)).size(); k++) {
				ArrayList<String> newLine = new ArrayList<String>();
				for (int j = 0; j < newHeaders.size(); j++) {
					newLine.add(mapContent.get(newHeaders.get(j)).get(k));
				}

				newContent.add(newLine);
			}
		}

		return newContent;
	}

}
