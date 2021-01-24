/**
 * **************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2012 José María Foces Morán,
 * instructor.
 *
 * DownloadClient.java
 *
 * REFERENCE SOLUTION to a simple download client to File Server ServerType0
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

public class DownloadClient {

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


            Transfers.protoSendString(oos, COMMAND_DOWNLOAD);
            
            String remoteFileName = "prueba.txt";            
            String localFileName = "/home/pablominix/tmp";
            
            Transfers.protoSendString(oos, remoteFileName);

            String resp = Transfers.protoReceiveString(ios);


            if (resp.compareToIgnoreCase(COMMAND_DOWNLOAD_ACK) == 0) {
                System.out.println("Download CLIENT: File download of "+remoteFileName+" accepted");
                System.out.flush();
                
                long size = Transfers.protoReceiveLong(ios);
                byte b[] = new byte[(int)size];
                
                Transfers.protoReceiveBytes(ios, b);
                
                resp = Transfers.protoReceiveString(ios);
                
                if (resp.compareToIgnoreCase(COMMAND_SUCCESSFUL) == 0){
                    if( Transfers.handleLocalFileWrite(localFileName, b) ){
                        System.out.print("Download CLIENT: File download of remote file "+remoteFileName);
                        System.out.println(" to local file "+localFileName+ " finished successfully");
                        System.out.flush();
                    }
                    
                }else{
                    System.err.append("Download CLIENT: File download finished with errors");
                    System.err.flush();
                }
                
  
            } else {
                System.out.println("Download CLIENT: File download NOT accepted");
                System.out.flush();
            }

            ios.close();
            oos.close();
            s.close();

        } catch (UnknownHostException ex) {
            Logger.getLogger(DownloadClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DownloadClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {

        new DownloadClient().start();

    }
}
