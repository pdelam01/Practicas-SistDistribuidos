/**
 * **************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2012 José María Foces Morán,
 * instructor.
 *
 * CommandDispatcher.java
 *
 * SINGLE RESPONSIBILITY OF THIS CLASS:
 * Receive commands (file server protocol commands) and invoke the specific
 * protocol-implementation method (upload, download, list, etc).
 * 
 * Receives the upstream and downstream objects and implements a simple file
 * upload and download protocol. Commands (High level protocol functions) are
 * encapsulated into class methods, they should be moved into Command-pattern
 * command classes, thus, they can more flexibly be reused and their functions
 * more conceptually organized
 *
 *
 ***************************************************************************
 */
package asdfileservice.servers;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import asdfileservice.transfers.Transfers;

public class Controller {

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

    
    /**
     * Receives the first string in a C/S interaction and interprets it as a
     * protocol command and calls a command handler method which will proceed
     * with the sequence of RR that will ultimately complete the command
     */
    void dispatch(ObjectInputStream input,
            ObjectOutputStream output,
            File fileStorageBaseDir) {

        String command = Transfers.protoReceiveString(input);

        /*
         * Determine which command has been received
         */
        if (command.equalsIgnoreCase(COMMAND_UPLOAD)) {

            doUpload(input, output, fileStorageBaseDir);

        } else if (command.equalsIgnoreCase(COMMAND_DOWNLOAD)) {

            doDonwload(input, output, fileStorageBaseDir);

        } else if (command.equalsIgnoreCase(COMMAND_LISTFILES)) {

            doListFiles(input, output, fileStorageBaseDir);

        } else if (command.equalsIgnoreCase(COMMAND_FILEXISTS)) {

            doFileExists(input, output, fileStorageBaseDir);

        } else {

            Transfers.handleProtocolError(input, output);

        }

    }

    
    /** 
     * UPLOAD COMMAND HANDLING METHOD
     * 
     * Performs the server part of a file upload
     */
    private void doUpload(ObjectInputStream input,
            ObjectOutputStream output,
            File fileStorageBaseDir) {

        byte b[] = null;

        String fileName = Transfers.protoReceiveString(input);

        File file = new File(fileName);

        if (!file.isAbsolute()) {

            long fileSize = Transfers.protoReceiveLong(input);

            Transfers.protoSendString(output, COMMAND_UPLOAD_ACK);

            if (fileSize > 0) {

                b = new byte[(int) fileSize];
                Transfers.protoReceiveBytes(input, b);

            }

            String localFileName = fileStorageBaseDir.toString() +
                    File.separator + fileName;
            
            if (Transfers.handleLocalFileWrite(localFileName, b) == true) {

                Transfers.protoSendString(output, COMMAND_SUCCESSFUL);

            } else {

                Transfers.protoSendString(output, COMMAND_NOT_SUCCESSFUL);

            }

        }else{
            Transfers.protoSendString(output, COMMAND_UPLOAD_NACK);
        }
        
    }

    /** 
     * DOWNLOAD COMMAND HANDLING METHOD
     * 
     * Performs the server part of a file donwload
     */
    private void doDonwload(ObjectInputStream input,
            ObjectOutputStream output,
            File fileStorageBaseDir) {

        String fName = Transfers.protoReceiveString(input);

        File file = new File(fileStorageBaseDir.toString() + File.separator + fName);

        System.err.println("SERVER: Download file name = " + file.getPath());

        if (file.exists() && file.length() > 0) {

            byte b[];

            Transfers.protoSendString(output, COMMAND_DOWNLOAD_ACK);
            Transfers.protoSendLong(output, file.length());

            b = Transfers.handleLocalFileRead(file.getPath());

            if (Transfers.protoSendBytes(output, b)) {
                Transfers.protoSendString(output, COMMAND_SUCCESSFUL);
            } else {
                Transfers.protoSendString(output, COMMAND_NOT_SUCCESSFUL);
            }

        } else {
            Transfers.protoSendString(output, COMMAND_DOWNLOAD_NACK);
        }


    }

    /** 
     * FILE EXISTS COMMAND HANDLING METHOD
     * 
     * Performs the server part of a file exists command
     */
    private void doFileExists(ObjectInputStream input,
            ObjectOutputStream output,
            File fileStorageBaseDir) {

        String fileName = Transfers.protoReceiveString(input);

        File testFile = new File(fileName);

        if (testFile.exists()) {
            Transfers.protoSendString(output, COMMAND_FILEXISTS_ACK);
        } else {
            Transfers.protoSendString(output, COMMAND_FILEXISTS_NACK);
        }

    }

    /** 
     * LIST FILES COMMAND HANDLING METHOD
     * 
     * Performs the server part of a file listing, it lists the names
     * of the files present in the storage base folder
     */
    private void doListFiles(ObjectInputStream input,
            ObjectOutputStream output,
            File fileStorageBaseDir) {

        File files[] = fileStorageBaseDir.listFiles();

        long nfiles = (long) files.length;

        if (nfiles > 0) {

            Transfers.protoSendString(output, COMMAND_LISTFILES_ACK);
            Transfers.protoSendLong(output, nfiles);

            for (int i = 0; i < files.length; i++) {
                Transfers.protoSendString(output, files[i].getName());
            }

            Transfers.protoSendString(output, COMMAND_SUCCESSFUL);

        } else {
            Transfers.protoSendString(output, COMMAND_LISTFILES_NACK);
            Transfers.protoSendString(output, COMMAND_NOT_SUCCESSFUL);
        }
    }
}
