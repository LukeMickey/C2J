/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import stamboom.domain.Administratie;

public class SerializationMediator implements IStorageMediator {

    private Properties props;

    /**
     * creation of a non configured serialization mediator
     */
    public SerializationMediator() {
        props = null;
    }

    @Override
    public Administratie load() throws IOException {
        if (!isCorrectlyConfigured()) 
        {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }
        
        FileInputStream fsIn = null;
        ObjectInputStream objIn = null;
        Administratie admin = null;
        try
        {
            fsIn = new FileInputStream(((File) props.get("file")).toString());
            objIn = new ObjectInputStream(fsIn);
            admin = (Administratie)objIn.readObject();
            admin.setObservableLists();
        }
        catch(IOException | ClassNotFoundException ioEx)
        {
            throw new IOException("wrong filetype or general failure");
        }
        try
        {
            fsIn.close();
            objIn.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        if (!isCorrectlyConfigured()) 
        {
            throw new RuntimeException("Serialization mediator isn't initialized correctly.");
        }

        FileOutputStream fsOut = null;
        ObjectOutputStream objOut = null;
        try
        {
            fsOut = new FileOutputStream(((File) props.get("file")).toString());
            objOut = new ObjectOutputStream(fsOut);
            objOut.writeObject(admin);
        }       
        catch(IOException ioEx){
            ioEx.printStackTrace();
        }
        try
        {
            fsOut.close();
            objOut.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
    }

    @Override
    public boolean configure(Properties props) {
        this.props = props;
        return isCorrectlyConfigured();
    }

    @Override
    public Properties config() {
        return props;
    }

    /**
     *
     * @return true if config() contains at least a key "file" and the
     * corresponding value is a File-object, otherwise false
     */
    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (props.containsKey("file")) {
            return props.get("file") instanceof File;
        } else {
            return false;
        }
    }
}
