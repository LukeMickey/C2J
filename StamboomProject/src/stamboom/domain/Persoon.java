package stamboom.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stamboom.util.StringUtilities;

public class Persoon implements java.io.Serializable {

    // ********datavelden**************************************
    private final int nr;
    private final String[] voornamen;
    private final String achternaam;
    private final String tussenvoegsel;
    private final Calendar gebDat;
    private final String gebPlaats;
    private Gezin ouderlijkGezin;
    private final List<Gezin> alsOuderBetrokkenIn;
    private transient ObservableList<Gezin> alsOuderBetrokkenInObserve;
    private final Geslacht geslacht;

    // ********constructoren***********************************
    /**
     * er wordt een persoon gecreeerd met persoonsnummer persNr en met als
     * voornamen vnamen, achternaam anaam, tussenvoegsel tvoegsel, geboortedatum
     * gebdat, gebplaats, geslacht g en een gegeven ouderlijk gezin (mag null
     * (=onbekend) zijn); NB. de eerste letter van een voornaam, achternaam en
     * gebplaats wordt naar een hoofdletter omgezet, alle andere letters zijn
     * kleine letters; het tussenvoegsel is zo nodig in zijn geheel
     * geconverteerd naar kleine letters.
     *
     */
    Persoon(int persNr, String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats, Geslacht g, Gezin ouderlijkGezin) {
        this.nr = persNr;
        this.voornamen = vnamen;
        this.achternaam = StringUtilities.withFirstCapital(anaam);
        this.tussenvoegsel = tvoegsel;
        this.gebDat = gebdat;
        this.gebPlaats = StringUtilities.withFirstCapital(gebplaats);    
        this.alsOuderBetrokkenIn = new ArrayList<>();
        this.alsOuderBetrokkenInObserve = FXCollections.observableList(alsOuderBetrokkenIn);
        this.geslacht = g;
        
        this.ouderlijkGezin = ouderlijkGezin;
        if(this.ouderlijkGezin != null) {
            this.ouderlijkGezin.breidUitMet(this);
        }
    }

    // ********methoden****************************************
    /**
     * @return de achternaam van deze persoon
     */
    public String getAchternaam() {
        return achternaam;
    }

    /**
     * @return de geboortedatum van deze persoon
     */
    public Calendar getGebDat() {
        return gebDat;
    }

    /**
     *
     * @return de geboorteplaats van deze persoon
     */
    public String getGebPlaats() {
        return gebPlaats;
    }

    /**
     *
     * @return het geslacht van deze persoon
     */
    public Geslacht getGeslacht() {
        return geslacht;
    }

    /**
     *
     * @return de voorletters van de voornamen; elke voorletter wordt gevolgd
     * door een punt
     */
    public String getInitialen() {
        //todo opgave 1
        String initialen = "";
        for (String s : voornamen) {
            initialen += s.substring(0, 1).trim().toUpperCase() + ".";
        }
        return initialen;
    }

    /**
     *
     * @return de initialen gevolgd door een eventueel tussenvoegsel en
     * afgesloten door de achternaam; initialen, voorzetsel en achternaam zijn
     * gescheiden door een spatie
     */
    public String getNaam() {
        //todo opgave 1
        if(this.tussenvoegsel.equals("")) {
            return String.format("%s %s", this.getInitialen(), this.achternaam);
        } else {
            return String.format("%s %s %s", this.getInitialen(), this.tussenvoegsel, this.achternaam);
        }
    }

    /**
     * @return het nummer van deze persoon
     */
    public int getNr() {
        return nr;
    }

    /**
     * @return het ouderlijk gezin van deze persoon, indien bekend, anders null
     */
    public Gezin getOuderlijkGezin() {
        return ouderlijkGezin;
    }

    /**
     * @return het tussenvoegsel van de naam van deze persoon (kan een lege
     * string zijn)
     */
    public String getTussenvoegsel() {
        return tussenvoegsel.toLowerCase();
    }

    /**
     * @return alle voornamen onderling gescheiden door een spatie
     */
    public String getVoornamen() {
        StringBuilder init = new StringBuilder();
        for (String s : voornamen) {
            init.append(StringUtilities.withFirstCapital(s.trim())).append(' ');            
        }
        return init.toString().trim();
    }

    /**
     * @return de standaardgegevens van deze mens: naam (geslacht) geboortedatum
     */
    public String standaardgegevens() {
        return getNaam() + " (" + getGeslacht() + ") " + StringUtilities.datumString(gebDat);
    }

    @Override
    public String toString() {
        return standaardgegevens();
    }

    /**
     * @return de gezinnen waar deze persoon bij betrokken is
     */
    public ObservableList<Gezin> getAlsOuderBetrokkenIn() {
        return (ObservableList<Gezin>)FXCollections.unmodifiableObservableList(alsOuderBetrokkenInObserve);
    }

    /**
     * Als het ouderlijk gezin van deze persoon nog onbekend is dan wordt deze
     * persoon een kind van ouderlijkGezin en tevens wordt deze persoon als kind
     * in dat gezin geregistreerd Als de ouders bij aanroep al bekend zijn,
     * verandert er niets
     *
     * @param ouderlijkGezin
     */
    void setOuders(Gezin ouderlijkGezin) {
        if(this.ouderlijkGezin == null) {
            this.ouderlijkGezin = ouderlijkGezin;
            ouderlijkGezin.breidUitMet(this);
        }
    }

    /**
     * @return voornamen, eventueel tussenvoegsel en achternaam, geslacht,
     * geboortedatum, namen van de ouders, mits bekend, en nummers van de
     * gezinnen waarbij deze persoon betrokken is (geweest)
     */
    public String beschrijving() {
        StringBuilder sb = new StringBuilder();

        sb.append(standaardgegevens());

        if (ouderlijkGezin != null) {
            sb.append("; 1e ouder: ").append(ouderlijkGezin.getOuder1().getNaam());
            if (ouderlijkGezin.getOuder2() != null) {
                sb.append("; 2e ouder: ").append(ouderlijkGezin.getOuder2().getNaam());
            }
        }
        if (!alsOuderBetrokkenIn.isEmpty()) {
            sb.append("; is ouder in gezin ");

            for (Gezin g : alsOuderBetrokkenIn) {
                sb.append(g.getNr()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * als g nog niet bij deze persoon staat geregistreerd wordt g bij deze
     * persoon geregistreerd en anders verandert er niets
     *
     * @param g een nieuw gezin waarin deze persoon een ouder is
     *
     */
    void wordtOuderIn(Gezin g) {
        if (!alsOuderBetrokkenIn.contains(g)) {
            alsOuderBetrokkenIn.add(g);
        }
    }

    /**
     *
     *
     * @param andereOuder mag null zijn
     * @return het ongehuwde gezin met de andere ouder ; mits bestaand anders
     * null
     */
    public Gezin heeftOngehuwdGezinMet(Persoon andereOuder) {
        //todo opgave 1
        for (Gezin g : alsOuderBetrokkenIn) {
            if ((g.getOuder1() == andereOuder || g.getOuder2() == andereOuder) && (g.getOuder1() == this || g.getOuder2() == this) && andereOuder != this) {
                return g;
            }
        }
        return null;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum getrouwd is, anders false
     */
    public boolean isGetrouwdOp(Calendar datum) {
                
        for(Gezin g : this.alsOuderBetrokkenIn) {
            if(g.getHuwelijksdatum() != null && g.getHuwelijksdatum().before(datum)) {                 
                return true;
            } 
        }
        return false;
    }

    /**
     *
     * @param datum
     * @return true als de persoon kan trouwen op datum, hierbij wordt rekening
     * gehouden met huwelijken in het verleden en in de toekomst
     */
    public boolean kanTrouwenOp(Calendar datum) {
        for (Gezin gezin : alsOuderBetrokkenIn) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return false;
            } else {
                Calendar huwdatum = gezin.getHuwelijksdatum();
                if (huwdatum != null && huwdatum.after(datum)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum gescheiden is, anders false
     */
    public boolean isGescheidenOp(Calendar datum) {
        //todo opgave 1
        for (Gezin gezin : alsOuderBetrokkenIn) {
            if (gezin.heeftGescheidenOudersOp(datum) && (gezin.getOuder1() == this || gezin.getOuder2() == this)) {
                return true;
            }
        }
        return false;
    }
    
    public Gezin mapIndex(HashMap<Gezin, Integer> hMap, int index){
        return (Gezin) hMap.keySet().toArray()[index];
     }

    /**
     * ********* de rest wordt in opgave 2 verwerkt ****************
     */
    /**
     *
     * @return het aantal personen in de stamboom van deze persoon (ouders,
     * grootouders etc); de persoon zelf telt ook mee
     */
    public int afmetingStamboom() {
        
        int aantalMensen = 0;
        
        HashMap<Gezin, Integer> gezinnen = new HashMap<Gezin, Integer>();
        gezinnen.put(this.ouderlijkGezin, 0);
        
        while(gezinnen.size() > 0) {
            
            int i = gezinnen.size() - 1;
            Gezin g = mapIndex(gezinnen, i);
            
            if(gezinnen.get(mapIndex(gezinnen, i)) == 0) {
                gezinnen.put(g, 1);
                
                if(g.getOuder1().getOuderlijkGezin() != null) {  
                    gezinnen.put(g.getOuder1().getOuderlijkGezin(), 0);                         
                } 
                aantalMensen += 1;

            } else if(gezinnen.get(mapIndex(gezinnen, i)) == 1) {
                if(g.getOuder2() != null && g.getOuder2().getOuderlijkGezin() != null) {                    
                    gezinnen.put(g.getOuder2().getOuderlijkGezin(), 0);     
                } 
                aantalMensen += 1;

                gezinnen.remove(g);
            }
        }
        
        return aantalMensen;
    }

    /**
     * de lijst met de items uit de stamboom van deze persoon wordt toegevoegd
     * aan lijst, dat wil zeggen dat begint met de toevoeging van de
     * standaardgegevens van deze persoon behorende bij generatie g gevolgd door
     * de items uit de lijst met de stamboom van de eerste ouder (mits bekend)
     * en gevolgd door de items uit de lijst met de stamboom van de tweede ouder
     * (mits bekend) (het generatienummer van de ouders is steeds 1 hoger)
     *
     * @param lijst
     * @param g >=0, het nummer van de generatie waaraan deze persoon is
     * toegewezen;
     */
    public void voegJouwStamboomToe(ArrayList<PersoonMetGeneratie> lijst, int g) {
        
        lijst.add(new PersoonMetGeneratie(this.standaardgegevens(), g));
        Persoon ouder1 = null;
        Persoon ouder2 = null;
        if (this.getOuderlijkGezin() != null) {
            ouder1 = this.getOuderlijkGezin().getOuder1();
            ouder2 = this.getOuderlijkGezin().getOuder2();
        }
        if (ouder1 != null) {
            ouder1.voegJouwStamboomToe(lijst, g + 1);
        }
        if (ouder2 != null) {
            ouder2.voegJouwStamboomToe(lijst, g + 1);
        }
    }

    /**
     *
     * @return de stamboomgegevens van deze persoon in de vorm van een String:
     * op de eerste regel de standaardgegevens van deze persoon, gevolgd door de
     * stamboomgegevens van de eerste ouder (mits bekend) en gevolgd door de
     * stamboomgegevens van de tweede ouder (mits bekend); formattering: iedere
     * persoon staat op een aparte regel en afhankelijk van het
     * generatieverschil worden er per persoon 2*generatieverschil spaties
     * ingesprongen;
     *
     * bijv voor M.G. Pieterse met ouders, grootouders en overgrootouders,
     * inspringen is in dit geval met underscore gemarkeerd: <br>
     *
     * M.G. Pieterse (VROUW) 5-5-2004<br>
     * __L. van Maestricht (MAN) 27-6-1953<br>
     * ____A.G. von Bisterfeld (VROUW) 13-4-1911<br>
     * ______I.T.M.A. Goldsmid (VROUW) 22-12-1876<br>
     * ______F.A.I. von Bisterfeld (MAN) 27-6-1874<br>
     * ____H.C. van Maestricht (MAN) 17-2-1909<br>
     * __J.A. Pieterse (MAN) 23-6-1964<br>
     * ____M.A.C. Hagel (VROUW) 12-0-1943<br>
     * ____J.A. Pieterse (MAN) 4-8-1923<br>
     */
    public String stamboomAlsString() {
        StringBuilder builder = new StringBuilder();
        ArrayList<PersoonMetGeneratie> lijst = new ArrayList<>();
        voegJouwStamboomToe(lijst, 0);
        for (PersoonMetGeneratie p : lijst) {
            for (int i = 0; i < p.getGeneratie(); i++) {
                builder.append("  ");
            }
            builder.append(p.getPersoonsgegevens() + "\r\n");
        }
        return builder.toString();
    }
}
