
#include <stdio.h>
#include <stdlib.h>

#include <fcntl.h>
#include <memory.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>

#include <signal.h>
#include <errno.h>
#include <sys/time.h>

int createDatagramSocket() {

    /*
     * Create a sockaddr_in struct that represents the full
     * UDP socket addressing (IP and UDP port number)
     */
    struct sockaddr_in client;
    client.sin_family = AF_INET;
    client.sin_port = INADDR_ANY;
    client.sin_addr.s_addr = INADDR_ANY;

    /*
     * Create a new UDP socket in the AF_INET domain and of
     * type SOCK_DGRAM (UDP)
     */
    int s = socket(AF_INET, SOCK_DGRAM, 0); //Always 0

    /*
     * Bind the socket s to address client
     */
    bind(s, (struct sockaddr *) &client, sizeof (client));

    return s;
}

struct sockaddr_in createServerAddress(int ip, int port) {

    /*
     * Create a sockaddr_in struct that represents the full
     * UDP socket addressing for the server
     */
    struct sockaddr_in server;
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    //Type the server IP right below
    server.sin_addr.s_addr = ip;
    //193.146.101.127
    return server;
}

void sendLoop(int s, struct sockaddr_in server) {

    int i = 0;

    while (i < 5) {

        printf("Send iteration %u\n", ++i);

        /*
         * Send mensaje through socket s to UDP server socket 
         * whose address is server
         * 
         * flags is 0
         */
        char *message = "Hello world! aeiou abcdefeghijklmnopqrstuvxyz";

        int nbytes = sendto(s, message, strlen(message), 0, (struct sockaddr *) &server, sizeof (server));

        printf("%u actually sent\n", nbytes);
        printf("%s\n", message);

        fflush(stdout);

        char response[1025];

        struct sockaddr_in addr;
        unsigned int addr_length;

        addr_length = sizeof (addr);
        nbytes = recvfrom(s, response, 1024, 0, (struct sockaddr *) &addr, &addr_length);

        printf("%u bytes received:\n", nbytes);
        response[nbytes] = '\0';
        printf("%s\n---------------\n", response);
        sleep(3);
    }

}

int main(int argc, char** argv) {

    if(argc<3 || argc>3){
        printf("ERROR!\n");
        return -1;

    }else if (argc==3){

        int ip=inet_addr(argv[1]);
        int port=atoi(argv[2]);

        if(port<0 || port>65535){
            printf("ERROR!\n");
            return -1;

        }else{
            int s = createDatagramSocket();
            struct sockaddr_in server = createServerAddress(ip,port);
            sendLoop(s, server);
            printf("Client exiting\n");
        }
       
    }

}

