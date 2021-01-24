/**
 * **************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2012 José María Foces Morán,
 * instructor.
 *
 * FileExistsClient.java
 *
 * REFERENCE SOLUTION to a simple File Exists client to the File Server type 0
 *
 * This program is just a quick-and-dirty proof of concept, it should be
 * properly designed by using a a design process similar to that used for the
 * Server implementations belonging to this project
 *
 **************************************************************************
 */
package asdfileservice.clients;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import asdfileservice.transfers.Transfers;

public class FileExistsClient {

    private final static String COMMAND_DOWNLOAD = "Download byte array REQ";
    private final static String COMMAND_DOWNLOAD_ACK = "Download ACK";
    private final static String COMMAND_DOWNLOAD_NACK = "Download NACK";
    private final static String COMMAND_UPLOAD = "Upload byte array REQ";
    private final static String COMMAND_UPLOAD_ACK = "Upload ACK";
    private final static String COMMAND_UPLOAD_NACK = "Upload NACK";
    private final static String COMMAND_FILELIST = "Fetch File List REQ";
    private final static String COMMAND_FILELIST_ACK = "Fetch File List ACK";
    private final static String COMMAND_FILELIST_NACK = "Fetch File List NACK";
    private final static String COMMAND_FILEXISTS = "File Exists REQ";
    private final static String COMMAND_FILEXISTS_ACK = "File Exists ACK";
    private final static String COMMAND_FILEXISTS_NACK = "File Exists NACK";
    private final static String COMMAND_SUCCESSFUL = "Command successful";
    private final static String COMMAND_NOT_SUCCESSFUL = "Command not successful";

    void start() {

        Socket s = null;

        try {

            s = new Socket("127.0.0.1", 60001);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ios = new ObjectInputStream(s.getInputStream());


            Transfers.protoSendString(oos, COMMAND_FILEXISTS);
            
            String localFn = "/tmp/prueba.txt";
            Transfers.protoSendString(oos, localFn);

            String resp = Transfers.protoReceiveString(ios);


            if (resp.compareToIgnoreCase(COMMAND_FILEXISTS_ACK) == 0) {
                System.out.println("FileExists CLIENT: File " + localFn + " does exist");
                System.out.flush();
            } else {
                System.out.println("FileExists CLIENT: File " + localFn + " does NOT exist");
                System.out.flush();
            }

            ios.close();
            oos.close();
            s.close();


        } catch (UnknownHostException ex) {
            Logger.getLogger(FileExistsClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileExistsClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

        new FileExistsClient().start();

    }
}
