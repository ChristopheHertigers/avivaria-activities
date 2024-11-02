package be.indigosolutions.framework.cellrenderer;

import javax.swing.table.TableCellRenderer;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 09/10/13
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public enum CellRenderers {
    StringCentered(StringCenteredRenderer.class),
    BooleanCentered(BooleanCenteredRenderer.class)
    ;

    private Class<? extends TableCellRenderer> cellRendererClass;

    private CellRenderers(Class<? extends TableCellRenderer> cellRendererClass) {
        this.cellRendererClass = cellRendererClass;
    }

    public TableCellRenderer getRenderer() {
        try {
            return cellRendererClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
