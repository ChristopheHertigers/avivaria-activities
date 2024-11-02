package be.indigosolutions.framework;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 06/10/13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
public class VetoableListSelectionModel extends DefaultListSelectionModel {
    public VetoableListSelectionModel() {
        super();
        setSelectionMode(SINGLE_SELECTION);
    }

    public boolean vetoSelection() {
        return false;
    }

    @Override
    public void clearSelection() {
        if (!vetoSelection()) {
            super.clearSelection();
        }
    }

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (!vetoSelection()) {
            super.setSelectionInterval(index0, index1);
        }
    }

    @Override
    public void setLeadSelectionIndex(int leadIndex) {
        if (!vetoSelection()) {
            super.setLeadSelectionIndex(leadIndex);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
