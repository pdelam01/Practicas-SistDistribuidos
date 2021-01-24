/**
 * **************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2012 José María Foces Morán,
 * instructor.
 *
 * ListFilesCLient.java
 *
 * REFERENCE SOLUTION to a simple List Files client to the File Server type 0
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

public class ListFilesClient {

    private final static String COMMAND_DOWNLOAD = "Download byte array REQ";
    private final static String COMMAND_DOWNLOAD_ACK = "Download ACK";
    private final static String COMMAND_DOWNLOAD_NACK = "Download NACK";
    private final static String COMMAND_UPLOAD = "Upload byte array REQ";
    private final static String COMMAND_UPLOAD_ACK = "Upload ACK";
    private final static String COMMAND_UPLOAD_NACK = "Upload NACK";
    private final static String COMMAND_LISTFILES = "Fetch File List REQ";
    private final static String COMMAND_LISTFILES_ACK = "Fetch File List ACK";
    private final static String COMMAND_LISTFILES_NACK = "Fetch File List NACK";
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


            Transfers.protoSendString(oos, COMMAND_LISTFILES);

            String resp = Transfers.protoReceiveString(ios);

            if (resp.compareTo(COMMAND_LISTFILES_ACK) == 0) {

                System.out.println("Response from server: " + resp);

                long nFileNames = Transfers.protoReceiveLong(ios);

                String names[] = new String[(int) nFileNames];

                System.out.println("NAME LISTING");
                System.out.println("------------");

                for (int i = 0; i < nFileNames; i++) {
                    names[i] = Transfers.protoReceiveString(ios);

                    System.out.println(names[i]);
                }
            }

            String consume = Transfers.protoReceiveString(ios);
            System.out.println("Response from server: " + resp + ". " + consume);

            ios.close();
            oos.close();
            s.close();

        } catch (UnknownHostException ex) {
            Logger.getLogger(ListFilesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ListFilesClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

        new ListFilesClient().start();

    }
}
