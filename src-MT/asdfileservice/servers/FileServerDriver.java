/**
 * ****************************************************************************
 * Universidad de León, EIII Grado en Ingeniería Informática Asignatura de
 * Arquitectura de Sistemas Distribuidos (C) 2014 José María Foces Morán,
 * instructor.
 *
 * FileServerDriver.java
 *
 * SINGLE RESPONSIBILITY OF THIS CLASS:
 * Start execution of a file server corresponding to interface Server which
 * only implementation, for the time being is SingleThreadedServer.java
 * 
 * Obtains an instance of Server according to arguments received via stdin and
 * calls the new server's tight loop
 * 
 ******************************************************************************
 */
package asdfileservice.servers;

public class FileServerDriver {

    public static void main(String[] args) {

        if(args.length < 2 || args.length > 3){
            System.err.print("Usage: fsd <tcp port> <base dir> [mt true/false]");
            System.exit(-1);
        }
        
        int port = Integer.parseInt(args[0]);
        String baseDir = args[1];
        boolean mt = Boolean.parseBoolean(args[2]);

        Server s = null;

        if (args.length == 2) {
            s = ASDFileServerFactory.getInstance(port, baseDir);
        } else if (args.length == 3) {
            s = ASDFileServerFactory.getInstance(port, baseDir, mt);
        } else {
            System.err.print("Usage: fsd <tcp port> <base dir> [mt true/false]");
            System.exit(-1);
        }

        if (s != null) {
            System.out.println("\t· Invoking SERVER with args: "+ port+" "+baseDir+" "+mt);
            System.out.flush();
            s.serverMainLoop();
        } else {
            System.err.print("Server null, could not be started");
            System.exit(-1);
        }
    }
}
