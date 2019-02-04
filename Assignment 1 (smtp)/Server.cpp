/* The port number is passed as an argument */
///http://www.bogotobogo.com/cplusplus/sockets_server_client.php
/// compile with g++ in terminal and then run it.

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include<arpa/inet.h>
#include<bits/stdc++.h>
#define ms(a,b) memset(a,b,sizeof a);
using namespace std;
vector<int>vec;
string user,mail,port,subject,filename;
char nextPar[100],status[5];
char buffer[256];
struct sockaddr_in serv_addr, cli_addr;
int sockfd, newsockfd, portno;
int n,flag, flaghelo, flagMailfrom, flagrpto, flagdatareq ;





vector<char*> v;


void clearr()
{
    flag=flaghelo=flagMailfrom=flagrpto=flagdatareq=0;
    user="", mail="", port="", subject="", filename="";
    ms(nextPar,0);
    ms(status,0);
    ms(buffer,0);
//    vec.clear();
//    v.clear();

}


void error(const char *msg)
{
    perror(msg);
    exit(1);
}



void Write_File(char str[])   ///writing in the file
{

    user+=".txt";   ///opening a file of name of user
    char text[100];
    strcpy(text,user.c_str());

    FILE * fp, *fp2;
    fp = fopen (text,"a+");

    char input[100];
    string sent;

    int cnt=0;

    for(int i=0; i<strlen(str)-1; i++)
    {
        if(str[i]=='#' || (str[i]=='a' && str[i+1]=='.'))
        {
            if(cnt==0) sent="\n\nTO: "+sent;
            else if(cnt==1) sent="FROM: "+sent;
            else if(cnt==2) sent="SUBJECT: "+sent;
            else if(cnt==3) sent="DATE:"+sent+"\nMessage body:";

            cnt++;

            strcpy(input,sent.c_str());
            // printf("->->%s\n",input);
            strcat(input,"\n");
            fprintf (fp, input);
            sent="";

            if(str[i]=='a' && str[i+1]=='.')
                flag=1;

        }
        else
            sent+=str[i];
    }


    fclose (fp);
    if(flag==0)
        send(newsockfd, "404",3, 0);
    else
        send(newsockfd, "200",3, 0);

}



int check_rpto(char str[])
{

    // printf("Here is the message: %s\n",buffer);
    int pos=0;
    string mail_from;
    while(buffer[pos]!=':' && pos<strlen(buffer)) pos++ ;

//    cout<<"+>"<<mail_from<<endl;
//    if(mail_from=="RCPT_TO") flagrpto=1;
//    if(flagrpto==0)  send(newsockfd, "404",3, 0);



    pos++;
    while(buffer[pos]!='@' && pos<strlen(buffer))
        user+=buffer[pos], pos++;



//    if(pos<strlen(buffer))
//        flagrpto=1;
//
//    if(flagrpto==0)  send(newsockfd, "404",3, 0);


    // cout<<"Is there any "<<user<<" account?"<<endl;
    string text;
    text=user+".txt";


    if(strstr(buffer,str)!=NULL)
    {
        FILE *fp2;
        if(fp2= fopen(text.c_str(),"r"))
        {
            status[0]='2',status[1]='0', status[2]='0';
        }
        else  status[0]='4',status[1]='0', status[2]='4';
    }
    else   status[0]='4',status[1]='0', status[2]='4';



    send(newsockfd, status,3, 0);    ///sending ok to client
    if(status[0]=='4')
     return 0;
    return 1;

}

int check_connection(char str[])
{
    if(strstr(buffer,str)!=NULL)
        status[0]='2',status[1]='0', status[2]='0';
    else   status[0]='5',status[1]='0', status[2]='3';

    send(newsockfd, status,3, 0);    ///sending ok to client
    if(status[0]=='5')
        return 0;
    else return 1;
}



int main(int argc, char *argv[])
{

    socklen_t clilen;
    if (argc < 2)
    {
        fprintf(stderr,"ERROR, no port provided\n");
        exit(1);
    }

    // create a socket
    // socket(int domain, int type, int protocol)
    sockfd =  socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0)
        error("ERROR opening socket");

    // clear address structure
    bzero((char *) &serv_addr, sizeof(serv_addr));

    portno = atoi(argv[1]);

    printf("Server is on at port:%d\n",portno);

    /* setup the host_addr structure for use in bind call */
    // server byte order
    serv_addr.sin_family = AF_INET;

    // automatically be filled with current host's IP address
    serv_addr.sin_addr.s_addr = INADDR_ANY;

    // convert short integer value for port must be converted into network byte order
    serv_addr.sin_port = htons(portno);

    // bind(int fd, struct sockaddr *local_addr, socklen_t addr_length)
    // bind() passes file descriptor, the address structure,
    // and the length of the address structure
    // This bind() call will bind  the socket to the current IP address on port, portno
    if (bind(sockfd, (struct sockaddr *) &serv_addr,
             sizeof(serv_addr)) < 0)
        error("ERROR on binding");

    // This listen() call tells the socket to listen to the incoming connections.
    // The listen() function places all incoming connection into a backlog queue
    // until accept() call accepts the connection.
    // Here, we set the maximum size for the backlog queue to 5.


    while(1)
    {

        listen(sockfd,5);

        // The accept() call actually accepts an incoming connection
        clilen = sizeof(cli_addr);

        // This accept() function will write the connecting client's address info
        // into the the address structure and the size of that structure is clilen.
        // The accept() returns a new socket file descriptor for the accepted connection.
        // So, the original socket file descriptor can continue to be used
        // for accepting new connections while the new socker file descriptor is used for
        // communicating with the connected client.
        newsockfd = accept(sockfd,
                           (struct sockaddr *) &cli_addr, &clilen);
        if (newsockfd < 0)
            error("ERROR on accept");

        printf("server: got connection from %s port %d\n",
               inet_ntoa(cli_addr.sin_addr), ntohs(cli_addr.sin_port));

        // This send() function sends the 13 bytes of the string to the new socket
        send(newsockfd, "202:Welcome\n", 11, 0);   ///sending ok to client


        bzero(buffer,256);
        n = read(newsockfd,buffer,255);    /// Mail from
        if(check_connection("HELO")==0) { printf("Sequence out of order \n"); continue;   }
        printf("Received:%s\n",buffer);


        bzero(buffer,256);
        n = read(newsockfd,buffer,255);    /// Mail from
        printf("Received:%s\n",buffer);
        if(check_connection("MAIL_FROM")==0) { printf("Sequence out of order \n"); continue;   }
       // check_connection("MAIL_FROM");

        bzero(buffer,256);
        n = read(newsockfd,buffer,255);    /// Mail from
        printf("Received:%s\n",buffer);
        if(check_connection("RCPT_TO")==0){ printf("Sequence out of order \n"); continue;   }
       // check_connection("RCPT_TO");
        if(check_rpto("RCPT_TO")==0)  {  printf("file not found\n"); continue;  }

//        if(flagrpto==0)
//            continue;



        bzero(buffer,256);
        n = read(newsockfd,buffer,255);    /// Mail from
        printf("Received:%s\n",buffer);
        if(check_connection("DATA_REQUEST")==0) { printf("Sequence out of order \n"); continue;   }
       // check_connection("DATA_request");

        bzero(buffer,256);
        n = read(newsockfd,buffer,255);    /// Mail from
        printf("Received:%s\n",buffer);

        Write_File(buffer);

//        if(flag==0)
//            continue;


        bzero(buffer,256);
        n = read(newsockfd,buffer,255);    /// Mail from
        printf("Received:%s\n",buffer);
        send(newsockfd, "200",3, 0);

        clearr();

    }

    close(newsockfd);
    close(sockfd);


    return 0;
}

