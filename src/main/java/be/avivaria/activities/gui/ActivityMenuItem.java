package be.avivaria.activities.gui;

import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.ControllerRegistry;
import be.indigosolutions.framework.DefaultAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:40
 */
public enum ActivityMenuItem {
    Activiteit(null, "Activiteit", null),
    Activiteiten(Activiteit, "Activiteiten", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showActiviteitDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventController controller = ControllerRegistry.get(EventController.class);
                    controller.show();
                }
            };
        }
    },
    Separator1(Activiteit, "---", null),
    Inschrijvingen(Activiteit, "Inschrijvingen", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showInschrijvingDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).closeAllReports();
                    InschrijvingController controller = ControllerRegistry.get(InschrijvingController.class);
                    controller.show();
                }
            };
        }
    },
    Hoknummers(Activiteit, "Hoknummers", null),
    HoknummersToekennen(Hoknummers, "Toekennen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("calculateHoknummers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).closeAllReports();
                    HokController controller = ControllerRegistry.get(HokController.class);
                    controller.assignHokNummers();
                }
            };
        }
    },
    InschrijvingenAfdrukken(Hoknummers, "Afdrukken inschrijvingen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printInschrijvingen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showInschrijvingenReport();
                }
            };
        }
    },
    HoknummersVerwijderen(Hoknummers, "Verwijderen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("deleteHoknummers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).closeAllReports();
                    HokController controller = ControllerRegistry.get(HokController.class);
                    controller.deleteHokNummers();
                }
            };
        }
    },
    Palmares(Activiteit, "Palmares", null),
    Keurmeesters(Palmares, "Keurmeesters aanduiden", KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("keurmeesters") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).closeAllReports();
                    KeurmeesterController controller = ControllerRegistry.get(KeurmeesterController.class);
                    controller.show();
                }
            };
        }
    },
    PredicatenToekennen(Palmares, "Predicaten toekennen", KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("predicaten") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).closeAllReports();
                    PredicaatController controller = ControllerRegistry.get(PredicaatController.class);
                    controller.show();
                }
            };
        }
    },
    DeelnemersAfdrukken(Palmares, "Afdrukken deelnemers", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printDeelnemers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showDeelnemerReport();
                }
            };
        }
    },
    JeugdDeelnemersAfdrukken(Palmares, "Afdrukken jeugddeelnemers", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printJeugdDeelnemers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showJeugdDeelnemerReport();
                }
            };
        }
    },
    PalmaresAfdrukken(Palmares, "Afdrukken palmares", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printPalmares") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showPalmares();
                }
            };
        }
    },
    VerenigingenAfdrukken(Palmares, "Afdrukken verenigingen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printVerenigingen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showVerenigingenReport();
                }
            };
        }
    },
    KampioenenAfdrukken(Palmares, "Afdrukken kampioenen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printKampioenen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showKampioenen();
                }
            };
        }
    },
    AantalDierenLedenAfdrukken(Palmares, "Afdrukken aantal dieren leden", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printAantalDierenLeden") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showAantalDierenLidAvivaria();
                }
            };
        }
    },
    AantalDierenNietLedenAfdrukken(Palmares, "Afdrukken aantal dieren niet-leden", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printAantalDierenNietLeden") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showAantalDierenNietLidAvivaria();
                }
            };
        }
    },
    Labels(Activiteit, "Labels", null),
    HokLabelsAfdrukken(Labels, "Afdrukken hoklabels", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printHokLabels") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showHokLabels();
                }
            };
        }
    },
    PrijsLabelsAfdrukken(Labels, "Afdrukken prijslabels", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("printPrijsLabels") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).showPrijsLabels();
                }
            };
        }
    },
    EditPrijsLabels(Labels, "Wijzig prijslabels", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("editPrijsLabels") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ControllerRegistry.get(ReportController.class).closeAllReports();
                    PrijsLabelController controller = ControllerRegistry.get(PrijsLabelController.class);
                    controller.show();
                }
            };
        }
    },
    Export(Activiteit, "Export", null),
    ExportInschrijvingen(Export, "Exporteer inschrijvingen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("exportInschrijvingen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ExportController controller = ControllerRegistry.get(ExportController.class);
                    controller.export();
                }
            };
        }
    },
    Onderhoud(null, "Onderhoud data", null),
    Deelnemers(Onderhoud, "Deelnemers", KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showDeelnemersDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DeelnemerController controller = ControllerRegistry.get(DeelnemerController.class);
                    controller.show();
                }
            };
        }
    },
    Verenigingen(Onderhoud, "Organizaties", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showVerenigingDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    VerenigingController controller = ControllerRegistry.get(VerenigingController.class);
                    controller.show();
                }
            };
        }
    },
    Separator2(Onderhoud, "---", null),
    Soorten(Onderhoud, "Soorten", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showSoortDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SoortController controller = ControllerRegistry.get(SoortController.class);
                    controller.show();
                }
            };
        }
    },
    Rassen(Onderhoud, "Rassen", KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showRasDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RasController controller = ControllerRegistry.get(RasController.class);
                    controller.show();
                }
            };
        }
    },
    Kleuren(Onderhoud, "Kleurslagen", KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showKleurDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    KleurController controller = ControllerRegistry.get(KleurController.class);
                    controller.show();
                }
            };
        }
    },
    Aantallen(Onderhoud, "Aantallen", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent) {
            return new DefaultAction("showAantalDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AantalController aantalController = ControllerRegistry.get(AantalController.class);
                    aantalController.show();
                }
            };
        }
    },
    Info(null, "Info", null),
    Over(Info, "Over", null) {
        @Override
        public DefaultAction getAction(AbstractController parent) {
            return new DefaultAction("showAbout") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AboutController controller = ControllerRegistry.get(AboutController.class);
                    controller.show();
                }
            };
        }
    };

    public static final String SEPARATOR_LABEL = "---";

    private final ActivityMenuItem parent;
    private final String label;
    private final KeyStroke accelerator;

    ActivityMenuItem(ActivityMenuItem parent, String label, KeyStroke accelerator) {
        this.parent = parent;
        this.label = label;
        this.accelerator = accelerator;
    }

    public ActivityMenuItem getParent() {
        return parent;
    }

    public String getLabel() {
        return label;
    }

    public KeyStroke getAccelerator() {
        return accelerator;
    }

    public DefaultAction getAction(final AbstractController parent) {
        return null;
    }

    public static JMenuBar createMenu(final AbstractController parentController) {
        Map<ActivityMenuItem, JMenuItem> mapping = new HashMap<>();
        JMenuBar menuBar = new JMenuBar();
        for (ActivityMenuItem menuItem : ActivityMenuItem.values()) {
            DefaultAction action = menuItem.getAction(parentController);
            JMenuItem item = action == null ? new JMenu(menuItem.getLabel()) : new JMenuItem(menuItem.getLabel());
            if (SEPARATOR_LABEL.equals(menuItem.getLabel())) {
                JMenu parent = (JMenu) mapping.get(menuItem.getParent());
                if (parent != null) {
                    parent.addSeparator();
                }
            } else if (menuItem.getParent() == null) {
                menuBar.add(item);
            } else {
                JMenuItem parent = mapping.get(menuItem.getParent());
                if (parent != null) {
                    parent.add(item);
                }
            }
            mapping.put(menuItem, item);
            if (action != null) {
                parentController.registerAction(item, action);
            }
            if (menuItem.getAccelerator() != null) {
                item.setAccelerator(menuItem.getAccelerator());
            }
        }
        return menuBar;
    }


}