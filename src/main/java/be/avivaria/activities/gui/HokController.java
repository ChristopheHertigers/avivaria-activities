package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.EventDao;
import be.avivaria.activities.dao.HokDao;
import be.avivaria.activities.dao.InschrijvingDao;
import be.avivaria.activities.dao.InschrijvingLijnDao;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.InschrijvingHeader;
import be.avivaria.activities.model.InschrijvingLijn;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.PersistenceController;
import be.indigosolutions.framework.util.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.util.*;

/**
 * User: christophe
 * Date: 29/10/13
 */
public class HokController extends PersistenceController {
    private final MainController parent;

    public HokController(AbstractController parentController) {
        super(parentController);
        this.parent = (MainController) parentController;
    }

    public void assignHokNummers() {
        Session session = getPersistenceContext();
        Event event = getSelectedEvent(session);
        InschrijvingLijnDao inschrijvingLijnDao = new InschrijvingLijnDao(session);
        HokDao hokDao = new HokDao(session);

        List<InschrijvingLijn> inschrijvingLijnen = inschrijvingLijnDao.findByEventId(event.getId());
        List<Hok> hokken = new ArrayList<>();
        Map<InschrijvingLijn,List<Hok>> fixRemarkFor = new HashMap<>();
        for (InschrijvingLijn lijn : inschrijvingLijnen) {
            List<Hok> lijnHokken = lijn.getHokList();
            if (lijnHokken.size() > 1 && lijn.getPrijs() != null && lijn.getPrijs() > 0) {
                fixRemarkFor.put(lijn, lijnHokken);
            }
            hokken.addAll(lijnHokken);
        }
        // sort
        Collections.sort(hokken, event.getType().getHokComparator());
        // number hokken
        long i = 1L;
        long id = hokDao.getNextId();
        for (Hok hok : hokken) {
            hok.setId(id++);
            hok.setHoknummer(i++);
        }
        // fix remarks
        if (CollectionUtils.isNotEmpty(fixRemarkFor)) {
            for (InschrijvingLijn lijn : fixRemarkFor.keySet()) {
                List<Hok> lijnHokken = fixRemarkFor.get(lijn);
                String opm = "(samen met " + (lijnHokken.size() > 2 ? "hokken " : "hok ") + "%s)";
                for (Hok hok : lijnHokken) {
                    hok.setOpmerking(String.format(opm, getHoknummersExcept(hok.getHoknummer(), lijnHokken)));
                }
            }
        }

        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            hokDao.deleteByEventId(event.getId());
            for (Hok hok : hokken) {
                hokDao.saveOrUpdate(hok);
            }

            // renumber inschrijvingen
            InschrijvingDao inschrijvingDao = new InschrijvingDao(session);
            List<InschrijvingHeader> inschrijvingen = inschrijvingDao.findByEventId(event.getId());
            i = 1;
            for (InschrijvingHeader inschrijving : inschrijvingen) {
                inschrijving.setVolgnummer(i++);
                inschrijvingDao.saveOrUpdate(inschrijving);
            }
            inschrijvingDao.flush();
            transaction.commit();
        } catch (Exception e1) {
            transaction.rollback();
            throw new RuntimeException(e1);
        } finally {
            SwingUtilities.invokeLater(parent::loadData);
            JOptionPane.showMessageDialog(parent.getView(), "De hokken zijn succesvol aangemaakt.", "Succes", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public void deleteHokNummers() {
        Session session = getPersistenceContext();
        Event event = getSelectedEvent(session);
        HokDao hokDao = new HokDao(session);
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            hokDao.deleteByEventId(event.getId());
            transaction.commit();
        } catch (Exception e1) {
            transaction.rollback();
            throw new RuntimeException(e1);
        } finally {
            SwingUtilities.invokeLater(parent::loadData);
            JOptionPane.showMessageDialog(parent.getView(), "De hokken zijn verwijderd.", "Succes", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private String getHoknummersExcept(long hoknummer, List<Hok> hokken) {
        if (CollectionUtils.isEmpty(hokken)) return "";
        StringBuilder value = new StringBuilder();
        hokken.stream().filter(hok -> hok.getHoknummer() != hoknummer).forEach(hok -> {
            if (value.length() > 0) value.append(", ");
            value.append(hok.getHoknummer());
        });
        return value.toString();
    }

    private Event getSelectedEvent(Session session) {
        EventDao eventDao = new EventDao(session);
        return eventDao.findSelected();
    }

}
