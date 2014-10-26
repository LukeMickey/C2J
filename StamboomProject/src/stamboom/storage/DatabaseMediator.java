/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;
import stamboom.domain.*;
import stamboom.util.StringUtilities;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    
    @Override
    public Administratie load() throws IOException {
        
        Administratie admin = null;
        
        try
        {
            admin = new Administratie();
            initConnection();
            
            // Retrieves all personen
            Statement stat = conn.createStatement();
            String query = "SELECT * FROM personen ORDER BY persoonsnummer ASC";
            ResultSet r = stat.executeQuery(query);
            while (r.next())
            {
                String[] vnamen = r.getString("voornamen").split(" ");
                String tussenvoegsel = r.getString("tussenvoegsel");
                String achternaam = r.getString("achternaam");
                Calendar geboortedatum = StringUtilities.datum(r.getString("geboortedatum"));
                String geboorteplaats = r.getString("geboorteplaats");
                Geslacht geslacht = Geslacht.valueOf(r.getString("geslacht"));
                
                admin.addPersoon(geslacht, vnamen, achternaam, tussenvoegsel, geboortedatum, geboorteplaats, null);
            }
            stat.close();
            
            // Retrieves all gezinnen
            stat = conn.createStatement();
            query = "SELECT * FROM gezinnen ORDER BY gezinsnummer ASC";
            r = stat.executeQuery(query);
            while(r.next())
            {
                String huwDatum = r.getString("huwelijksdatum");
                String scheiDatum = r.getString("scheidingsdatum");
                Calendar huwelijksdatum = null;
                Calendar scheidingsdatum = null;
                if(huwDatum != null)
                {
                    huwelijksdatum = StringUtilities.datum(huwDatum);
                    if(!scheiDatum.isEmpty())
                    {
                        scheidingsdatum = StringUtilities.datum(scheiDatum);
                    }
                }
            
                Persoon ouder1 = admin.getPersoon(r.getInt("ouder1"));
                Persoon ouder2 = admin.getPersoon(r.getInt("ouder2"));
                
                if(huwelijksdatum == null)
                {
                    admin.addOngehuwdGezin(ouder1, ouder2);
                }
                if(huwelijksdatum != null)
                {
                    Gezin g = admin.addHuwelijk(ouder1, ouder2, huwelijksdatum);
                    if(scheidingsdatum != null)
                    {
                        admin.setScheiding(g, scheidingsdatum);
                    }
                }
            }
            stat.close();
            
            // Sets ouders for each person with ouders
            stat = conn.createStatement();
            query = "SELECT persoonsnummer, ouders FROM personen WHERE ouders IS NOT NULL";
            r = stat.executeQuery(query);
            while(r.next())
            {
                Persoon p = admin.getPersoon(r.getInt("persoonsnummer"));
                Gezin ouders = admin.getGezin(r.getInt("ouders"));
                admin.setOuders(p, ouders);
            }
            stat.close();
        } 
        catch (SQLException ex)
        {
            throw new IOException(ex.getMessage());
        }
        
        this.closeConnection();
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        
        if(!this.isCorrectlyConfigured())
        {
            throw new IOException("Not configured correctly");
        }
        try
        {
            // Clears personen and gezinnen Tables
            this.initConnection();
            //Turn off foreign key constraint checks to clear table.
            String query = "SET FOREIGN_KEY_CHECKS = 0";
            Statement reset = conn.createStatement();
            reset.execute(query);
            query = "DELETE FROM personen";
            reset.execute(query);
            query = "DELETE FROM gezinnen";
            reset.execute(query);
            //Turns foreign key constraint check back on.
            query = "SET FOREIGN_KEY_CHECKS = 1";
            reset.execute(query);
            reset.close();
            
            PreparedStatement stat = null;  
            // Inserts all persons from Administratie into Database
            query = "INSERT INTO personen (persoonsnummer, voornamen, tussenvoegsel, achternaam, geboortedatum, geboorteplaats, geslacht) VALUES (?, ?, ?, ?, ?, ?, ?)";
                                 
            for(Persoon p : admin.getPersonen())
            {
                stat = conn.prepareStatement(query);
                stat.setInt(1, p.getNr());
                stat.setString(2, p.getVoornamen());
                stat.setString(3, p.getTussenvoegsel());
                stat.setString(4, p.getAchternaam());
                stat.setString(5, StringUtilities.datumString(p.getGebDat()));
                stat.setString(6, p.getGebPlaats());
                stat.setString(7, p.getGeslacht().toString());
                stat.executeUpdate();
            }
            
            // Inserts all gezinnen from Administratie into Database
            query = "INSERT INTO gezinnen (gezinsnummer, huwelijksdatum, scheidingsdatum, ouder1, ouder2) VALUES (?, ?, ?, ?, ?)";
            for(Gezin g : admin.getGezinnen())
            {
                stat = conn.prepareStatement(query);
                stat.setInt(1, g.getNr());
                if(g.getHuwelijksdatum() != null)
                {
                    stat.setString(2, StringUtilities.datumString(g.getHuwelijksdatum()));
                    if(g.getScheidingsdatum() != null)
                    {
                        stat.setString(3, StringUtilities.datumString(g.getScheidingsdatum()));
                    }
                    else
                    {
                        stat.setNull(3, java.sql.Types.VARCHAR);
                    }                   
                }
                else
                {
                    // Gezin isn't married so also not divorced
                    stat.setNull(2, java.sql.Types.VARCHAR);
                    stat.setNull(3, java.sql.Types.VARCHAR);
                }
                // ouder1
                stat.setInt(4, g.getOuder1().getNr());
                // ouder2
                if(g.getOuder2() != null)
                {
                    stat.setInt(5, g.getOuder2().getNr());
                }
                else
                {
                    stat.setNull(5, java.sql.Types.INTEGER);
                }
                stat.executeUpdate();                          
            }
            
            // Updates personen, sets ouders if they have any
            query = "UPDATE personen SET ouders = ? WHERE persoonsnummer = ?";
            for(Persoon p : admin.getPersonen())
            {
                if(p.getOuderlijkGezin() != null)
                {
                    stat = conn.prepareStatement(query);
                    stat.setInt(1, p.getOuderlijkGezin().getNr());
                    stat.setInt(2, p.getNr());
                    stat.execute();
                }            
            }
            stat.close();
        } catch (SQLException ex)
        {
            throw new IOException(ex.getMessage());          
        }
        closeConnection();
    }

    @Override
    public final boolean configure(Properties props) {
        this.props = props;

        try {
            initConnection();
            return isCorrectlyConfigured();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        String url = (String)props.get("url");
        String username = (String)props.get("username");
        String password = (String)props.get("password");

        conn = DriverManager.getConnection(url, username, password);
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
