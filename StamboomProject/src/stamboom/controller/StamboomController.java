/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.storage.DatabaseMediator;
import stamboom.storage.IStorageMediator;
import stamboom.storage.SerializationMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }
    
    public void setAdmin(Administratie admin){
        this.admin = admin;
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */

    public void serialize(File bestand) throws IOException 
    {
        if (!(storageMediator instanceof SerializationMediator)) 
        {
            Properties props = new Properties();
            props.put("file", bestand);
            storageMediator = new SerializationMediator();
            storageMediator.configure(props);
        }
        storageMediator.save(admin);    
    }
    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException 
    {
        if (!(storageMediator instanceof SerializationMediator))
        {
            Properties props = new Properties();
            props.put("file", bestand);
            storageMediator = new SerializationMediator();
            storageMediator.configure(props);
            
        }
        Administratie output = storageMediator.load();
        if(output != null)
        {
            admin = storageMediator.load();
        }
        else
        {
            throw new IOException("failed to load correctly");
        }
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {
        if (!(storageMediator instanceof DatabaseMediator)) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("database.properties")) {
                props.load(in);
            }
            storageMediator = new DatabaseMediator();
            storageMediator.configure(props); 
        }
    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        this.initDatabaseMedium();
        admin = storageMediator.load();
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        this.initDatabaseMedium();
        storageMediator.save(admin);
    }

}
