/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;
import static stamboom.util.StringUtilities.datum;
import static stamboom.util.StringUtilities.datumString;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;
    @FXML TextArea taStamboom;
    
    //GEZIN
    @FXML ComboBox cbKiesGezin;
    @FXML TextField tfGezinNr;
    @FXML TextField tfOuder1;
    @FXML TextField tfOuder2;
    @FXML TextField tfHuwelijk;
    @FXML TextField tfScheiding;
    @FXML TextArea taKinderen;
    @FXML Button btnSetHuwelijk, btnSetScheiding;
    @FXML TextField tfNewDate;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //INVOER PERSOON
    @FXML TextField tfNieuwVoornamen;
    @FXML TextField tfNieuwTussenvoegsel;
    @FXML TextField tfNieuwAchternaam;
    @FXML ComboBox cbNieuwGeslacht;
    @FXML TextField tfNieuwGeboortedatum;
    @FXML TextField tfNieuwGeboorteplaats;
    @FXML ComboBox cbNieuwOuderlijkGezin;

    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initComboboxes();
        withDatabase = false;
    }

    private void initComboboxes() {        
        this.cbNieuwGeslacht.setItems(FXCollections.observableArrayList(Geslacht.values()));
        this.cbNieuwOuderlijkGezin.setItems(this.getAdministratie().getGezinnen());
        this.cbOuder1Invoer.setItems(this.getAdministratie().getPersonen());
        this.cbOuder2Invoer.setItems(this.getAdministratie().getPersonen());
        this.cbPersonen.setItems(this.getAdministratie().getPersonen()); 
        this.cbKiesGezin.setItems(this.getAdministratie().getGezinnen());
        this.cbOuderlijkGezin.setItems(this.getAdministratie().getGezinnen());
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        getAdministratie().setOuders(p, ouderlijkGezin);
    }

    public void selectGezin(Event evt) {
        Gezin gezin = (Gezin)this.cbKiesGezin.getSelectionModel().getSelectedItem();
        if(gezin != null)
        {
            this.showGezin(gezin);
        }
    }

    private void showGezin(Gezin gezin) {
        this.clearTabGezin();
        if(gezin == null)
        {          
            return;
        }
        this.tfGezinNr.setText(String.valueOf(gezin.getNr()));
        this.tfOuder1.setText(gezin.getOuder1().getNaam());
        if(gezin.getOuder2() != null)
        {
            this.tfOuder2.setText(gezin.getOuder2().getNaam());
        }
        
        if(gezin.getHuwelijksdatum() != null)
        {
            this.tfHuwelijk.setText(datumString(gezin.getHuwelijksdatum()));
            
            if(gezin.getScheidingsdatum() != null)
            {
                this.tfScheiding.setText(datumString(gezin.getScheidingsdatum()));
            }
        }
    }

    public void setHuwdatum(Event evt) {
        Calendar datum;
        Gezin gezin;
        try
        {
             gezin = this.getAdministratie().getGezin(Integer.parseInt(this.tfGezinNr.getText()));
             datum = datum(this.tfNewDate.getText());
        }
        catch (NumberFormatException exc)
        {
            this.showDialog("Invalid Input", "Please give a valid family index");
            return;
        }
        catch (IllegalArgumentException exc)
        {
            this.showDialog("Invalid Input", "Given date could not be converted");
            return;
        }
        if(gezin != null || datum != null)
        {
            if(!this.getAdministratie().setHuwelijk(gezin, datum))
            {
                this.showDialog("Error", "The system was not able to process this marriage");
            }
            else
            {
                this.showDialog("Family Married", gezin.toString() + " has been married");
                this.showGezin(gezin);
            }
        }   
        else
        {
            this.showDialog("Invalid Input", "Family not found or date is invalid");
        }
        this.tfNewDate.clear();
    }

    public void setScheidingsdatum(Event evt) {
        Calendar datum;
        Gezin gezin;
        try
        {
             gezin = this.getAdministratie().getGezin(Integer.parseInt(this.tfGezinNr.getText()));
             datum = datum(this.tfNewDate.getText());
        }
        catch (NumberFormatException exc)
        {
            this.showDialog("Invalid Input", "Please give a valid family index");
            return;
        }
        catch (IllegalArgumentException exc)
        {
            this.showDialog("Invalid Input", "Given date could not be converted");
            return;
        }
        if(gezin != null || datum != null)
        {
            if(!this.getAdministratie().setScheiding(gezin, datum))
            {
                this.showDialog("Error", "The system was not able to divorce this family");
            }
            else
            {
                this.showDialog("Family Divorced", gezin.toString() + " has been divorced");
                this.showGezin(gezin);
            }
        }   
        else
        {
            this.showDialog("Invalid Input", "Family not found or date is invalid");
        }
        this.tfNewDate.clear();

    }

    public void cancelPersoonInvoer(Event evt) {
        this.clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        String voornaamS = this.tfNieuwVoornamen.getText();
        ArrayList<String> voornaamA = new ArrayList<>();
        String tussenV = this.tfNieuwTussenvoegsel.getText();
        String achterN = this.tfNieuwAchternaam.getText();
        String gebPlaats = this.tfNieuwGeboorteplaats.getText();
        Geslacht geslacht = (Geslacht)this.cbNieuwGeslacht.getSelectionModel().getSelectedItem();
        Gezin ouderlijkGezin = (Gezin)this.cbNieuwOuderlijkGezin.getSelectionModel().getSelectedItem();
        Calendar gebDatum = null;
        try
        {
           gebDatum = datum(this.tfNieuwGeboortedatum.getText()); 
        }
        catch (IllegalArgumentException ex)
        {
            this.showDialog("Invalid Input", "Wrong date format, adhere to dd-mm-yyyy");
            return;
        }
        
        if(voornaamS.isEmpty() || 
                achterN.isEmpty() || 
                gebDatum == null ||
                gebPlaats.isEmpty() ||
                this.cbNieuwGeslacht.getSelectionModel().getSelectedItem() == null) {
            this.showDialog("Incomplete Input", "Please provide additional details");
            return;
        }
        
        while(voornaamS.contains(" "))
        {
            voornaamS = voornaamS.trim();
            if(voornaamS.contains(" "))
            {
                voornaamA.add(voornaamS.substring(0, voornaamS.indexOf(" ")));
                voornaamS = voornaamS.substring(voornaamS.indexOf(" "));
            }
        }
        voornaamA.add(voornaamS);
        
        Persoon persoon = this.getAdministratie().addPersoon(geslacht, 
                voornaamA.toArray(new String[voornaamA.size()]), achterN, 
                tussenV, gebDatum, gebPlaats, ouderlijkGezin);
        
        if(persoon != null)
        {
            this.showDialog("Person added", persoon.toString() + " has been added to the system");
        }
        else
        {
            this.showDialog("Person not added", "Specified person has not been added to the system");
        }
        this.clearTabPersoonInvoer();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    getAdministratie().setScheiding(g, scheidingsdatum);
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        this.clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        this.taStamboom.clear();
        
        int persoonNr = -1;
        
        try
        {
           persoonNr = Integer.parseInt(this.tfPersoonNr.getText());
        }
        catch (NumberFormatException ex)
        {
            this.showDialog("Input Error", "Input does not correlate with a valid person: " + ex.getMessage());
            return;
        }
        
        Persoon persoon = getAdministratie().getPersoon(persoonNr);
        if(persoon == null)
        {
            this.showDialog("Invalid Person", "Please give a valid index for Person");
            return;
        }
        
        this.taStamboom.setText(persoon.stamboomAlsString());        
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        // todo opgave 3
       
    }

    
    public void saveStamboom(Event evt) {
        // todo opgave 3
       
    }

    
    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        this.clearTabPersoon();
        this.clearTabPersoonInvoer();
        this.clearTabGezin();
        this.clearTabGezinInvoer();
    }

    
    private void clearTabPersoonInvoer() {
        this.cbNieuwOuderlijkGezin.getSelectionModel().clearSelection();  
        this.tfNieuwGeboortedatum.clear();
        this.tfNieuwGeboorteplaats.clear();
        this.cbNieuwGeslacht.getSelectionModel().clearSelection();
        this.tfNieuwAchternaam.clear();
        this.tfNieuwVoornamen.clear();
        this.tfNieuwTussenvoegsel.clear();        
    }

    
    private void clearTabGezinInvoer() {
        this.cbOuder1Invoer.getSelectionModel().clearSelection();
        this.cbOuder2Invoer.getSelectionModel().clearSelection();
        this.tfHuwelijkInvoer.clear();
        this.tfScheidingInvoer.clear();     
    }

    private void clearTabPersoon() {
        this.cbPersonen.getSelectionModel().clearSelection();
        this.tfPersoonNr.clear();
        this.tfVoornamen.clear();
        this.tfTussenvoegsel.clear();
        this.tfAchternaam.clear();
        this.tfGeslacht.clear();
        this.tfGebDatum.clear();
        this.tfGebPlaats.clear();
        this.cbOuderlijkGezin.getSelectionModel().clearSelection();
        this.lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    
    private void clearTabGezin() {
        this.tfNewDate.clear();this.tfScheiding.clear();
        this.tfOuder1.clear();
        this.tfOuder2.clear();
        this.taKinderen.clear(); 
        this.tfHuwelijk.clear();
        this.tfGezinNr.clear();
        this.cbKiesGezin.getSelectionModel().clearSelection();
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
