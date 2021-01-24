
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

#define OURPORT 50009
#define REQLENGTH 127

char DATEREQUEST[REQLENGTH + 1];

int MAXITERATIONS = 5;


int main(int argc, char** argv) {

    /*
     * Crear la dirección de la socket UDP
     */
    struct sockaddr_in server;
    // Socket del tipo 'internet'
    server.sin_family = AF_INET;
    // Elegimos un puerto no reservado
    server.sin_port = htons(OURPORT);
    /* Esta socket (En este server) va a a escuchar en
     * cualquiera de las IPs, reales o virtuales
     */
    server.sin_addr.s_addr = INADDR_ANY; 

    /*
     * Se crea una socket internet, del tipo datagrama
     */
    int s;
    s = socket(AF_INET, SOCK_DGRAM, 0); //Último arg, siempre 0


    /*
     * Unimos a la socket s la dirección que hemos creado en los
     * párrafos anteriores:
     */
    bind(s, (struct sockaddr *) &server, sizeof (server));


    /*
     * Dirección del cliente que nos va a contactar
     */
    struct sockaddr_in client;
    unsigned int addr_length;
    //addr_length = sizeof (client);

    printf("Servidor esperando a recibir una solicitud\n");
    fflush(stdout);


    /*
     * Usar la socket s para recibir un mensaje que se almacenará en DATEREQUEST y
     * que no será de longitud efectiva superior a REQLENGTH (20 bytes), 
     * la dirección y el puerto del cliente quedarán registrados en client
     */
    addr_length = sizeof (client);
    int i = 0;

    while (i < MAXITERATIONS) {
        int nbytes = recvfrom(s, DATEREQUEST, REQLENGTH, 0, (struct sockaddr *) &client, &addr_length);

        printf("Solicitud recibida con un tamaño de %u bytes, procedentes de %s@ puerto %u:\n",
                nbytes, inet_ntoa(client.sin_addr), client.sin_port);

        printf("'%s'\n", DATEREQUEST);

        char *response = "Respuesta de prueba";

        /*
         * Usar la socket s para enviar un DATAGRAMA de respuesta al cliente desde
         * el que hemos recibido la solicitud
         */
        nbytes = sendto(s, response, strlen(response) + 1, 0, (struct sockaddr *) &client, sizeof (client));

        printf("Enviados %u bytes:\n\n", nbytes);

        i++;
    }

    return 0;
}
