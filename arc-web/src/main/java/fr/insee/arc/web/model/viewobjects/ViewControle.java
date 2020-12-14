package fr.insee.arc.web.model.viewobjects;

import java.util.HashMap;
import java.util.Map;

import fr.insee.arc.web.util.ConstantVObject.ColumnRendering;

public class ViewControle extends AbstractViewRule {
	public ViewControle() {
        super("view.control", "viewControle", 15);
    }
	
	@Override
	protected Map<String, ColumnRendering> extraFields() {
		HashMap<String, ColumnRendering> columnRenderings = new HashMap<>();
        columnRenderings.put("id_classe", new ColumnRendering(true, "label.control.type", "13%", "select",
                "select id, id from arc.ext_type_controle order by ordre", true));
        columnRenderings.put("rubrique_pere", new ColumnRendering(true, "label.element.main", "10%", "text", null, true));
        columnRenderings.put("rubrique_fils", new ColumnRendering(true, "label.element.child", "10%", "text", null, true));
        columnRenderings.put("borne_inf", new ColumnRendering(true, "label.min", "5%", "text", null, true));
        columnRenderings.put("borne_sup", new ColumnRendering(true, "label.max", "5%", "text", null, true));
        columnRenderings.put("condition", new ColumnRendering(true, "label.sql.predicate", "21%", "text", null, true));
        columnRenderings.put("pre_action", new ColumnRendering(true, "label.sql.pretreatment", "21%", "text", null, true));
        columnRenderings.put("commentaire", new ColumnRendering(true, "label.comment", "15%", "text", null, true));
        columnRenderings.put("todo", new ColumnRendering(false, "label.todo", "0%", "text", null, true));

		return columnRenderings;
	}
}