/************************************************************
 * Universidad de León, EIII
 * Grado en Ingeniería Informática
 * Asignatura de Arquitectura de Sistemas Distribuidos
 * (C) 2012 José María Foces Morán, instructor. 
 * 
 * ASDFileServerFactory.java
 * 
 * SINGLE RESPONSIBILITY OF THIS CLASS:
 * Correctly produce file server instances
 * 
 ************************************************************/

package asdfileservice.servers;

import java.io.*;


public class ASDFileServerFactory {

    static final int BASEPORT = 1024;

    /**
     * Creates a new Server, checks port is not root and that the base
     * storage folder exists and is rw, the new server uses the ST or the
     * MT implementation according to variable MultiThreaded false or true
     * respectively
     * 
     */
    public synchronized static Server getInstance(
            int port,
            String fileStorageBaseDir,
            boolean MultiThreaded) {

        Server s = null;

        /*
         * Make sure the base storage folder exists and is rw
         */
        File f = new File(fileStorageBaseDir);
        
        System.out.println("f.getName() = "+f.getName());
        System.out.println("f.path() = "+f.getPath());        
        System.out.println("f.getAbs: " + f.getAbsolutePath() + "  f.getPath "+ f.getPath());
        System.out.println("exists:"+f.exists()+" abs = "+f.isAbsolute()+" dir = "+f.isDirectory()+" r = "+f.canRead()+"  w = "+f.canWrite());
        System.out.flush();
        
        if (f.isAbsolute() && f.isDirectory() && f.canRead() && f.canWrite()) {

            /*
             * Make sure a root port is not specified, otherwise,
             * return null and finish
             */
            if (port > BASEPORT) {

                if (MultiThreaded) {
                    s = (Server) new MultiThreadedServer(port, f);
                } else {
                    /*
                     * s = (Server) new SingleThreadedServer(port, f);
                     *
                     */
                }

                System.out.println("Server: Welcome socket on port " + port + " created");
                System.out.flush();

            } else {

                s = null;

                System.err.println("Problem on ServerSocket creation");
                System.err.flush();

            }

        }

        return s;

    }

    /** 
     * Two-argument SINGLE THREADED factory method
     */
    public synchronized static Server getInstance(
            int port,
            String fileStorageBaseDir) {

        Server s = null;

        s = getInstance(
                port,
                fileStorageBaseDir,
                false);

        return s;

    }
}
