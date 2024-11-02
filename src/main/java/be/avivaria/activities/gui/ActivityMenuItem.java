package be.avivaria.activities.gui;

import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.ControllerRegistry;
import be.indigosolutions.framework.DefaultAction;
import org.hibernate.Session;

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
    Activiteiten(Activiteit, "Activiteiten", KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showActiviteitDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventController controller = ControllerRegistry.getInstance().get(EventController.class);
                    if (controller == null) {
                        controller = new EventController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Separator1(Activiteit, "---", null),
    Inschrijvingen(Activiteit, "Inschrijvingen", KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showInschrijvingDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.closeAllReports();
                    InschrijvingController controller = ControllerRegistry.getInstance().get(InschrijvingController.class);
                    if (controller == null) {
                        controller = new InschrijvingController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Hoknummers(Activiteit, "Hoknummers", null),
    HoknummersToekennen(Hoknummers, "Toekennen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("calculateHoknummers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.closeAllReports();
                    HokController controller = ControllerRegistry.getInstance().get(HokController.class);
                    if (controller == null) {
                        controller = new HokController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.assignHokNummers();
                }
            };
        }
    },
    InschrijvingenAfdrukken(Hoknummers, "Afdrukken inschrijvingen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printInschrijvingen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showInschrijvingenReport();
                }
            };
        }
    },
    HoknummersVerwijderen(Hoknummers, "Verwijderen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("deleteHoknummers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.closeAllReports();
                    HokController controller = ControllerRegistry.getInstance().get(HokController.class);
                    if (controller == null) {
                        controller = new HokController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.deleteHokNummers();
                }
            };
        }
    },
    Palmares(Activiteit, "Palmares", null),
    PredicatenToekennen(Palmares, "Predicaten toekennen", KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("predicaten") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.closeAllReports();
                    PredicaatController controller = ControllerRegistry.getInstance().get(PredicaatController.class);
                    if (controller == null) {
                        controller = new PredicaatController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    DeelnemersAfdrukken(Palmares, "Afdrukken deelnemers", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printDeelnemers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showDeelnemerReport();
                }
            };
        }
    },
    JeugdDeelnemersAfdrukken(Palmares, "Afdrukken jeugddeelnemers", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printJeugdDeelnemers") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showJeugdDeelnemerReport();
                }
            };
        }
    },
    PalmaresAfdrukken(Palmares, "Afdrukken palmares", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printPalmares") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showPalmares();
                }
            };
        }
    },
    VerenigingenAfdrukken(Palmares, "Afdrukken verenigingen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printVerenigingen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showVerenigingenReport();
                }
            };
        }
    },
    KampioenenAfdrukken(Palmares, "Afdrukken kampioenen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printKampioenen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showKampioenen();
                }
            };
        }
    },
    Labels(Activiteit, "Labels", null),
    HokLabelsAfdrukken(Labels, "Afdrukken hoklabels", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printHokLabels") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showHokLabels();
                }
            };
        }
    },
    PrijsLabelsAfdrukken(Labels, "Afdrukken prijslabels", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("printPrijsLabels") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.showPrijsLabels();
                }
            };
        }
    },
    EditPrijsLabels(Labels, "Wijzig prijslabels", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("editPrijsLabels") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ReportController.closeAllReports();
                    PrijsLabelController controller = ControllerRegistry.getInstance().get(PrijsLabelController.class);
                    if (controller == null) {
                        controller = new PrijsLabelController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.refreshItemList();
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Export(Activiteit, "Export", null),
    ExportInschrijvingen(Export, "Exporteer inschrijvingen", null) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("exportInschrijvingen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new ExportController(parent);
                }
            };
        }
    },
    Onderhoud(null, "Onderhoud data", null),
    Deelnemers(Onderhoud, "Deelnemers", KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showDeelnemersDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DeelnemerController controller = ControllerRegistry.getInstance().get(DeelnemerController.class);
                    if (controller == null) {
                        controller = new DeelnemerController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Verenigingen(Onderhoud, "Organizaties", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showVerenigingDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    VerenigingController controller = ControllerRegistry.getInstance().get(VerenigingController.class);
                    if (controller == null) {
                        controller = new VerenigingController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Separator2(Onderhoud, "---", null),
    Soorten(Onderhoud, "Soorten", KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showSoortDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SoortController controller = ControllerRegistry.getInstance().get(SoortController.class);
                    if (controller == null) {
                        controller = new SoortController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Rassen(Onderhoud, "Rassen", KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showRasDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RasController controller = ControllerRegistry.getInstance().get(RasController.class);
                    if (controller == null) {
                        controller = new RasController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Kleuren(Onderhoud, "Kleurslagen", KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showKleurDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    KleurController controller = ControllerRegistry.getInstance().get(KleurController.class);
                    if (controller == null) {
                        controller = new KleurController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    },
    Aantallen(Onderhoud, "Aantallen", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK)) {
        @Override
        public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
            return new DefaultAction("showAantalDetail") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AantalController aantalController = ControllerRegistry.getInstance().get(AantalController.class);
                    if (aantalController == null) {
                        aantalController = new AantalController(parent);
                        ControllerRegistry.getInstance().register(aantalController);
                    }
                    aantalController.getView().setVisible(true);
                }
            };
        }
    },
    Info(null, "Info", null),
    Over(Info, "Over", null) {
        @Override
        public DefaultAction getAction(AbstractController parent, Session persistenceContext) {
            return new DefaultAction("showAbout") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AboutController controller = ControllerRegistry.getInstance().get(AboutController.class);
                    if (controller == null) {
                        controller = new AboutController(parent);
                        ControllerRegistry.getInstance().register(controller);
                    }
                    controller.getView().setVisible(true);
                }
            };
        }
    };

    public static final String SEPARATOR_LABEL = "---";

    private ActivityMenuItem parent;
    private String label;
    private KeyStroke accelerator;

    private ActivityMenuItem(ActivityMenuItem parent, String label, KeyStroke accelerator) {
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

    public DefaultAction getAction(final AbstractController parent, final Session persistenceContext) {
        return null;
    }

    public static JMenuBar createMenu(final AbstractController parentController, final Session persistenceContext) {
        Map<ActivityMenuItem, JMenuItem> mapping = new HashMap<>();
        JMenuBar menuBar = new JMenuBar();
        for (ActivityMenuItem menuItem : ActivityMenuItem.values()) {
            DefaultAction action = menuItem.getAction(parentController, persistenceContext);
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