package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.HokRepository;
import be.avivaria.activities.dao.InschrijvingLijnRepository;
import be.avivaria.activities.dao.InschrijvingRepository;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.InschrijvingHeader;
import be.avivaria.activities.model.InschrijvingLijn;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: christophe
 * Date: 29/10/13
 */
@Controller
public class HokController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(HokController.class);
    private final MainController mainController;

    private final HokRepository hokRepository;
    private final InschrijvingRepository inschrijvingRepository;
    private final InschrijvingLijnRepository inschrijvingLijnRepository;

    @Autowired
    public HokController(
            MainController mainController,
            HokRepository hokRepository,
            InschrijvingRepository inschrijvingRepository,
            InschrijvingLijnRepository inschrijvingLijnRepository
    ) {
        this.mainController = mainController;
        this.hokRepository = hokRepository;
        this.inschrijvingRepository = inschrijvingRepository;
        this.inschrijvingLijnRepository = inschrijvingLijnRepository;
    }

    public void assignHokNummers() {
        Event event = getSelectedEvent();

        List<InschrijvingLijn> inschrijvingLijnen = inschrijvingLijnRepository.findAllByInschrijving_EventOrderById(event);
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
        hokken.sort(event.getType().getHokComparator());
        // number hokken
        long i = event.getHokStartNummer().longValue();
        for (Hok hok : hokken) {
            hok.setId(null);
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

        try {
            hokRepository.deleteByInschrijvingLijn_Inschrijving_Event(event);
            hokRepository.saveAll(hokken);

            // renumber inschrijvingen
            List<InschrijvingHeader> inschrijvingen = inschrijvingRepository.findAllByEventOrderByDeelnemer_Naam(event);
            i = 1;
            for (InschrijvingHeader inschrijving : inschrijvingen) {
                inschrijving.setVolgnummer(i++);
            }
            inschrijvingRepository.saveAll(inschrijvingen);
        } catch (Exception e1) {
            logger.error("Error assigning hoknummers", e1);
            throw new RuntimeException(e1);
        } finally {
            SwingUtilities.invokeLater(mainController::loadData);
            JOptionPane.showMessageDialog(mainController.getView(), "De hokken zijn succesvol aangemaakt.", "Succes", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    public void deleteHokNummers() {
        Event event = getSelectedEvent();
        try {
            hokRepository.deleteByInschrijvingLijn_Inschrijving_Event(event);
        } catch (Exception e1) {
            logger.error("Error deleting hokken", e1);
            throw new RuntimeException(e1);
        } finally {
            SwingUtilities.invokeLater(mainController::loadData);
            JOptionPane.showMessageDialog(mainController.getView(), "De hokken zijn verwijderd.", "Succes", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private String getHoknummersExcept(long hoknummer, List<Hok> hokken) {
        if (CollectionUtils.isEmpty(hokken)) return "";
        StringBuilder value = new StringBuilder();
        hokken.stream().filter(hok -> hok.getHoknummer() != hoknummer).forEach(hok -> {
            if (!value.isEmpty()) value.append(", ");
            value.append(hok.getHoknummer());
        });
        return value.toString();
    }

    private Event getSelectedEvent() {
        return mainController.getSelectedEvent();
    }

}
