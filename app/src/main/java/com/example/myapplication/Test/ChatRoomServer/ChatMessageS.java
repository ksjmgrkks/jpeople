package com.example.myapplication.Test.ChatRoomServer;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ChatMessageS{
    String clientdata = "";
    String serverdata = "";
    List<ServerThread> list;
    public ServerThread SThread;

    public void runServer() {
        ServerSocket server;
        Socket sock;
        ServerThread SThread;
        try {

            list = new ArrayList<ServerThread>();
            server = new ServerSocket(5000);
            try {
                while(true){
                    sock = server.accept();
                    SThread = new ServerThread(this, sock);
                    SThread.start();
                    System.out.println(sock.getInetAddress().getHostName() + "서버는 클라이언트와 연결됨");
                }
            } catch(IOException ioe) {
                server.close();
                ioe.printStackTrace();
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatMessageS s = new ChatMessageS();
        s.runServer();
    }

    class ServerThread extends Thread {
        Socket sock;
        BufferedWriter output;
        BufferedReader input;
        String clientdata;
        String serverdata = "";
        ChatMessageS cs;
        JSONArray userArray;

        private static final String SEPARATOR = "|";
        private static final int REQ_LOGOUT = 1000;
        private static final int REQ_LOGON = 1001;
        private static final int REQ_SENDIMAGE = 1002;
        private static final int REQ_SENDWORDS = 1021;
        private static final int REQ_SENDMEMBERS = 1022;

        public ServerThread(ChatMessageS c, Socket s) {
            sock = s;
            cs = c;
            try {
                input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                output = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public void run() {
            cs.list.add(this);
            try {
                // 순별 이름 구분, 들어온 사람 표시

                while((clientdata = input.readLine()) != null) {
                    StringTokenizer st = new StringTokenizer(clientdata, SEPARATOR);
                    int command = Integer.parseInt(st.nextToken());
                    int cnt = cs.list.size();
                    switch(command){
                        case REQ_LOGON : { // "1001|순이름|이름"를 수신한 경우
                            String GroupName = st.nextToken();
                            String userName = st.nextToken();
                            System.out.println(GroupName+"채팅방에"+userName+"님이 입장하셨습니다.");
                            for(int i=0; i<cnt; i++){
                                ServerThread SThread = (ServerThread)cs.list.get(i);
                                SThread.output.write("1001|"+GroupName+"|"+userName+"님이 입장하셨습니다.\r\n");
                                SThread.output.flush();
                            }
                            break;
                        }

                        case REQ_LOGOUT : { // "1000|순이름|이름"를 수신한 경우
                            String GroupName = st.nextToken();
                            String userName = st.nextToken();
                            System.out.println(GroupName+"채팅방에서"+userName+"님이 퇴장하셨습니다.");
                            for(int i=0; i<cnt; i++){
                                ServerThread SThread = (ServerThread)cs.list.get(i);
                                SThread.output.write("1000|"+GroupName+"|"+userName+"님이 퇴장하셨습니다.\r\n");
                                SThread.output.flush();
                            }
                            break;
                        }

                        case REQ_SENDWORDS : { // "1021|순이름|이름|대화말"를 수신
                            String GroupName = st.nextToken();
                            String userName = st.nextToken();
                            String message = st.nextToken();
                            System.out.println(GroupName+"채팅방의 대화 ->"+userName+":"+message);
                            for(int i=0; i<cnt; i++){
                                ServerThread SThread = (ServerThread)cs.list.get(i);
                                SThread.output.write("1021|"+GroupName+"|"+userName+"|"+message+"\r\n");
                                SThread.output.flush();

                            }

                            break;
                        }

                        case REQ_SENDIMAGE : { // "1002|순이름|이름|파일경로"를 수신
                            String GroupName = st.nextToken();
                            String userName = st.nextToken();
                            String imagePath = st.nextToken();
                            System.out.println(GroupName+"채팅방에서 "+userName+"님이 보낸 이미지의 경로:"+imagePath);
                            for(int i=0; i<cnt; i++){
                                ServerThread SThread = (ServerThread)cs.list.get(i);
                                SThread.output.write("1002|"+GroupName+"|"+userName+"|"+imagePath+"\r\n");
                                SThread.output.flush();
                            }

                            break;
                        }

                        case REQ_SENDMEMBERS : { // "1022|순이름|이름"를 수신
                            String GroupName = st.nextToken();
                            String name = st.nextToken();
                            userArray.put(name);
                            System.out.println(GroupName+"채팅방에서 명단 변경 "+userArray);
                            for(int i=0; i<cnt; i++){
                                ServerThread SThread = (ServerThread)cs.list.get(i);
                                SThread.output.write("1022|"+GroupName+"|"+userArray+"\r\n");
                                SThread.output.flush();
                            }

                            break;
                        }



                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            cs.list.remove(this);
            try {
                sock.close();
            }catch(IOException ea){
                ea.printStackTrace();
            }
        }

    }

}
