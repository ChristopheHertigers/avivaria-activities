package be.indigosolutions.framework.cellrenderer;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * User: christophe
 * Date: 09/10/13
 * Time: 21:37
 */
public class BooleanCenteredRenderer extends DefaultTableCellRenderer {
    public BooleanCenteredRenderer() {
        setHorizontalAlignment(CENTER);
    }

    @Override
    protected void setValue(Object value) {
        Object v = value;
        if (value instanceof Boolean) {
            Boolean b = (Boolean) value;
            v = b ? "Ja" : "Nee";
        }
        super.setValue(v);
    }
}
