#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include<bits/stdc++.h>
using namespace std;

int sockfd;


char buf[1024];
char buffer[256];

string mail_address, port,subject,filename, username, hostname, serverhost,date;



void gettime()
{
    time_t t = time(0);   // get time now
    struct tm * now = localtime( & t );


    //date = (now->tm_year + 1900) + '-'+ (now->tm_mon + 1) + '-'+  now->tm_mday;
    int a = (now->tm_year + 1900);
    int b = (now->tm_mon + 1);
    int c = now->tm_mday;

    date= to_string(a) +"-"+ to_string(b)+"-" + to_string(c);//+ "-" + b + "-" + c;
    //cout <<date <<endl;

}


void splitAddress(char ara[])
{
    int pos=0;


    // printf("->%s\n",ara);

    while(ara[pos]!='@')
        mail_address+=ara[pos],pos++;

    mail_address+=ara[pos]; //+'@';
    pos++;

    while(ara[pos]!=':')
        mail_address+=ara[pos], serverhost+=ara[pos], pos++;



    pos++;

    while(pos<strlen(ara))
        port+=ara[pos],pos++;


}

void error(const char *msg)
{
    perror(msg);
    exit(0);
}

void errorcode(char code1, char code3)
{
    if(code1 == '3')
    {
        error("Moved Permanently, Requested object moved");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else if(code1 == '4' && code3 == '0')
    {
        error("Bad Request, Request message not understood by server");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else if(code1 == '4' && code3 == '1')
    {
        error("Unauthorized, Request lacks proper authorization");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else if(code1 == '4' && code3 == '3')
    {
        error("Forbidden, The request was valid, but the server is refusing action");
        printf("Error Code: %c", code1);
        close(sockfd);
    }


    else if(code1 == '4' && code3 == '4')
    {
        error("Not Found, Requested document not found on this server");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else if(code1 == '5' && code3 == '1')
    {
        error("HTTP Version Not Supported");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else if(code1 == '5' && code3 == '2')
    {
        error("Bad Gateway, The server was acting as a gateway or proxy and received an invalid response from the upstream server");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else if(code1 == '5' && code3 == '3')
    {
        error("Request is out of sequence");
        printf("Error Code: %c", code1);
        close(sockfd);
    }

    else
    {
        error("Undefined problem occured");
        close(sockfd);
    }

}



void Read_File()
{
    FILE *file;
    size_t nread;


    printf("!%s!\n",filename.c_str());

    file = fopen(filename.c_str(), "r");
    if (file)
    {
        while ((nread = fread(buf, 1, sizeof buf, file)) > 0)
            //fwrite(buf, 1, nread, stdout);
            fclose(file);
    }

//    for(int i=0; buf[i]; i++)
//        printf("%c",buf[i]);

//    puts("");

}


void checkmailformet()
{
    if((strstr(buffer, "@") == NULL) || (strstr(buffer, "mail_client") == NULL) || (strstr(buffer, ".txt") == NULL))
    {
        error("Mail formet not supported ");

        close(sockfd);
    }
}


int main(int argc, char *argv[])
{


    gettime();


    username= getenv("USER");
    char temp[1024];
    gethostname(temp, 1024);
    string temp2(temp);
    hostname=temp2;



    int  portno, n;
    struct sockaddr_in serv_addr;
    struct hostent *server;



    if (argc < 4)
    {
        fprintf(stderr,"usage %s hostname port\n", argv[0]);
        exit(0);
    }

    //printf("---------%s\n",argv[1]);
    splitAddress(argv[1]);
    subject=argv[2];

    //printf("->%s\n",argv[3]);
    string tempx(argv[3]);
    filename=tempx;
    Read_File();

    portno = atoi(port.c_str());

    //  cout<<mail_address<<" "<<serverhost<<" "<<port<<" "<<subject<<" "<<filename<<endl;


    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0)
        error("ERROR opening socket");
    server = gethostbyname(serverhost.c_str());
    if (server == NULL)
    {
        fprintf(stderr,"ERROR, no such host\n");
        exit(0);
    }
    bzero((char *) &serv_addr, sizeof(serv_addr));
    serv_addr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,
          (char *)&serv_addr.sin_addr.s_addr,
          server->h_length);
    serv_addr.sin_port = htons(portno);
    if (connect(sockfd, (struct sockaddr *) &serv_addr, sizeof(serv_addr)) < 0)
        error("ERROR connecting");


///mail_client mob:sadeenmahbub@gmail.com:20001 "This is a test" mail.txt


    printf("Start from here:\n");
//    bzero(buffer,256);
//    fgets(buffer,255,stdin);
//    n = write(sockfd, buffer, strlen(buffer));
//    if (n < 0)
//        error("ERROR writing to socket");
    bzero(buffer,256);
    n = read(sockfd, buffer, 255);
    printf("Connection Established: %s\n", buffer);
    if(buffer[0]!='2')
    {
        errorcode(buffer[0], buffer[2]);
        return 0;
    }



    string input;
    cin>>input;
    string heloreq=input+hostname; //HELO Request
    write(sockfd, heloreq.c_str(), heloreq.size());
    bzero(buffer,256);
    n = read(sockfd, buffer, 255);
    printf("Received in response of HELO: %s\n", buffer);
    if(buffer[0]!='2')
    {
        errorcode(buffer[0], buffer[2]);
        return 0;
    }



    // char mailform[] = "MAIL_FROM mob:sadeenmahbub@gmail.com:mob-X556UQK";
     cin>>input;
   // string mailform="MAIL_FROM:"+username+" "+hostname;
    string mailform=input+username+" "+hostname;
    write(sockfd, mailform.c_str(), mailform.size());
    bzero(buffer, 256);
    read(sockfd, buffer, 255);
    printf("Received in response of MAIL_FROM: %s\n", buffer);
    if(buffer[0]!='2')
    {
        errorcode(buffer[0], buffer[2]);
        return 0;
    }

     cin>>input;
  //  string rcptto = "RCPT_TO:"+mail_address;
    string rcptto = input+mail_address;
    write(sockfd, rcptto.c_str(), rcptto.size());
    bzero(buffer, 256);
    read(sockfd, buffer, 255);
    printf("Received in response of RCPT_TO: %s\n", buffer);
    if(buffer[0]!='2')
    {
        errorcode(buffer[0], buffer[2]);
        return 0;
    }


    char datarequest[100]; // = "DATA_request";
    scanf("%s", &datarequest);
    write(sockfd, datarequest, strlen(datarequest));
    bzero(buffer, 256);
    read(sockfd, buffer, 255);
    printf("Received in response of DATA REQUEST: %s\n", buffer);
    if(buffer[0]!='2')
    {
        errorcode(buffer[0], buffer[2]);
        return 0;
    }


    gettime();
    string tempp(buf);
    tempp=mail_address+'#'+username+" "+hostname+'#'+subject+'#'+date+'#'+tempp+'#'+"a.";
    strcpy(buf,tempp.c_str());
    write(sockfd, buf, strlen(buf));
    bzero(buffer, 256);
    read(sockfd, buffer, 255);
    printf("Received in response of message body: %s\n", buffer);
    if(buffer[0]!='2')
    {
        errorcode(buffer[0], buffer[2]);
        return 0;
    }


    char exitrequest[] = "Request to EXIT";
    write(sockfd, exitrequest, strlen(exitrequest));
    bzero(buffer, 256);
    read(sockfd, buffer, 255);
    printf("Quiting server ...\n\n\n");
    close(sockfd);

    //close(sockfd);
    return 0;
}
