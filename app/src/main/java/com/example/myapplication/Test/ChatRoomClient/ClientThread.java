package com.example.myapplication.Test.ChatRoomClient;

public class ClientThread extends Thread
{
//    private WaitRoomDisplay ct_waitRoom;
//    private ChatRoomDisplay ct_chatRoom;
//    private Socket ct_sock;
//    private DataInputStream ct_in;
//    private DataOutputStream ct_out;
//    private StringBuffer ct_buffer;
//    private Thread thisThread;
//    private String ct_logonName;
//    private int ct_roomNumber;
//
//    private static final String SEPARATOR = "|";
//    private static final String DELIMETER = "'";
//    private static final int WAITROOM = 0;
//
//    private static final int REQ_LOGON = 1001;
//    private static final int REQ_CREATEROOM = 1011;
//    private static final int REQ_ENTERROOM = 1021;
//    private static final int REQ_QUITROOM = 1031;
//    private static final int REQ_LOGOUT = 1041;
//    private static final int REQ_SENDWORD = 1051;
//    private static final int REQ_SENDIMAGE = 1052;
//
//    private static final int YES_LOGON = 2001;
//    private static final int NO_LOGON = 2002;
//    private static final int YES_CREATEROOM = 2011;
//    private static final int NO_CREATEROOM = 2012;
//    private static final int YES_ENTERROOM = 2021;
//    private static final int NO_ENTERROOM = 2022;
//    private static final int YES_QUITROOM = 2031;
//    private static final int YES_LOGOUT = 2041;
//
//    private static final int YES_SENDWORD = 2051;
//    private static final int NO_SENDWORD = 2052;
//    private static final int YES_SENDIMAGE = 2061;
//    private static final int NO_SENDIMAGE = 2062;
//
//    private static final int MDY_WAITUSER = 2003;
//    private static final int MDY_WAITINFO = 2013;
//    private static final int MDY_ROOMUSER = 2023;
//
//    private static final int ERR_ALREADYUSER = 3001;
//    private static final int ERR_SERVERFULL = 3002;
//    private static final int ERR_ROOMSFULL = 3011;
//    private static final int ERR_ROOMERFULL = 3021;
//    private static final int ERR_PASSWORD = 3022;
//    private static final int ERR_REJECTION = 3031;
//    private static final int ERR_NOUSER = 3032;
//
//    public ClientThread(){
//        ct_waitRoom = new WaitRoomDisplay(this);
//        ct_chatRoom = null;
//        try{
//          ct_sock = new Socket(InetAddress.getLocalHost(), 8080);
//          ct_in = new DataInputStream(ct_sock.getInputStream());
//          ct_out = new DataOutputStream(ct_sock.getOutputStream());
//          ct_buffer = new StringBuffer(2048);
//          thisThread = this;
//        }catch (IOException e){
//                    //서버에 접속할 수 없습니다.
//        }
//    }
//
//    public ClientThread(String hostaddr){
//        ct_waitRoom = new WaitRoomDisplay(this);
//        ct_waitRoom = null;
//        try{
//          ct_sock = new Socket(hostaddr, 8080);
//          ct_in = new DataInputStream(ct_sock.getInputStream());
//          ct_out = new DataOutputStream(ct_sock.getOutputStream());
//          ct_buffer = new StringBuffer(2048);
//          thisThread = this;
//        }catch (IOException e){
//            //서버에 접속할 수 없습니다.
//        }
//    }
//
//    public void run(){
//        try{
//          Thread currThread = Thread.currentThread();
//          while(currThread == thisThread){
//              String recvData = ct_in.readUTF();
//              StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
//              int command = Integer.parseInt(st.nextToken());
//              switch(command){
//                  case YES_LOGON : {
//                      -
//
//                      break;
//                  }
//
//
//              }
//
//          }
//        }catch (Exception e){
//
//        }
//    }
}
