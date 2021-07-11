package com.example.myapplication.Test.ChatRoomServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class ServerThread extends Thread
{
    private Socket st_sock;
    private DataInputStream st_in;
    private DataOutputStream st_out;
    private StringBuffer st_buffer;
    private WaitRoom st_waitRoom;
    public String st_name;
    public int st_roomNumber;

    private static final String SEPARATOR = "|";
    private static final String DELIMETER = "'";
    private static final int WAITROOM = 0;

    private static final int REQ_LOGON = 1001;
    private static final int REQ_CREATEROOM = 1011;
    private static final int REQ_ENTERROOM = 1021;
    private static final int REQ_QUITROOM = 1031;
    private static final int REQ_LOGOUT = 1041;
    private static final int REQ_SENDWORD = 1051;
    private static final int REQ_SENDIMAGE = 1052;

    private static final int YES_LOGON = 2001;
    private static final int NO_LOGON = 2002;
    private static final int YES_CREATEROOM = 2011;
    private static final int NO_CREATEROOM = 2012;
    private static final int YES_ENTERROOM = 2021;
    private static final int NO_ENTERROOM = 2022;
    private static final int YES_QUITROOM = 2031;
    private static final int YES_LOGOUT = 2041;

    private static final int YES_SENDWORD = 2051;
    private static final int NO_SENDWORD = 2052;
    private static final int YES_SENDIMAGE = 2061;
    private static final int NO_SENDIMAGE = 2062;

    private static final int MDY_WAITUSER = 2003;
    private static final int MDY_WAITINFO = 2013;
    private static final int MDY_ROOMUSER = 2023;

    private static final int ERR_ALREADYUSER = 3001;
    private static final int ERR_SERVERFULL = 3002;
    private static final int ERR_ROOMSFULL = 3011;
    private static final int ERR_ROOMERFULL = 3021;
    private static final int ERR_PASSWORD = 3022;
    private static final int ERR_REJECTION = 3031;
    private static final int ERR_NOUSER = 3032;

    public ServerThread(Socket sock) {
        try {
            st_sock = sock;
            st_in = new DataInputStream(sock.getInputStream());
            st_out = new DataOutputStream(sock.getOutputStream());
            st_buffer = new StringBuffer(2048); // 2048은 버퍼의 사이즈에 해당한다.
            st_waitRoom = new WaitRoom();
        }catch(IOException e) {
            System.out.println(e);
        }
    }

    private void sendErrCode(int message, int errCode) throws IOException{
        st_buffer.setLength(0);
        st_buffer.append(message);
        st_buffer.append(SEPARATOR);
        st_buffer.append(errCode);
        send(st_buffer.toString());
    }
    private void modifyWaitRoom() throws IOException{
        st_buffer.setLength(0);
        st_buffer.append(MDY_WAITINFO);
        st_buffer.append(SEPARATOR);
        st_buffer.append(st_waitRoom.getWaitRoomInfo());
        broadcast(st_buffer.toString(), WAITROOM);
    }

    private void modifyWaitUser() throws IOException{
        String names = st_waitRoom.getUsers();
        st_buffer.setLength(0);
        st_buffer.append(MDY_WAITUSER);
        st_buffer.append(SEPARATOR);
        st_buffer.append(names);
        broadcast(st_buffer.toString(), WAITROOM);
    }

    //채팅방 유저 변경
    private void modifyRoomUser(int roomNumber, String name, int code) throws IOException{
        String names = st_waitRoom.getRoomInfo(roomNumber);
        st_buffer.setLength(0);
        st_buffer.append(MDY_ROOMUSER);
        st_buffer.append(SEPARATOR);
        st_buffer.append(name);
        st_buffer.append(SEPARATOR);
        st_buffer.append(code);
        st_buffer.append(SEPARATOR);
        st_buffer.append(names);
        broadcast(st_buffer.toString(), roomNumber);
    }

    //데이터를 전송하는 메소드
    private void send(String sendData) throws IOException{
        synchronized(st_out) {

            System.out.println(sendData);

            st_out.writeUTF(sendData);
            st_out.flush();
        }
    }

    // 데이터 뿌려주기 (broadcast)
    private synchronized void broadcast(String sendData, int roomNumber) throws IOException{
        ServerThread client;
        Hashtable clients = st_waitRoom.getClients(roomNumber);
        Enumeration enu = clients.keys();
        while(enu.hasMoreElements()) {
            client = (ServerThread) clients.get(enu.nextElement());
            client.send(sendData);
        }
    }

    public void run() {
        try {
            while(true){
                String recvData = st_in.readUTF();

                System.out.println(recvData);

                StringTokenizer st = new StringTokenizer(recvData, SEPARATOR);
                int command = Integer.parseInt(st.nextToken());
                switch(command) {

                    case REQ_LOGON : {
                        st_roomNumber = WAITROOM;
                        int result;
                        st_name = st.nextToken();
                        result = st_waitRoom.addUser(st_name, this);
                        st_buffer.setLength(0);
                        if(result == 0) {
                            st_buffer.append(YES_LOGON);
                            st_buffer.append(SEPARATOR);
                            st_buffer.append(st_waitRoom.getRooms());
                            send(st_buffer.toString());
                            modifyWaitUser();
                            System.out.println(st_name + "님의 연결요청이 승인되었습니다.");
                        } else {
                            sendErrCode(NO_LOGON, result);
                        }
                        break;
                    }

                    case REQ_CREATEROOM : {
                        String name, roomName, password;
                        int roomMaxUser, result;
                        boolean isRock;

                        name = st.nextToken();
                        String roomInfo = st.nextToken();
                        StringTokenizer room = new StringTokenizer(roomInfo, DELIMETER);
                        roomName = room.nextToken();
                        roomMaxUser = Integer.parseInt(room.nextToken());
                        isRock = (Integer.parseInt(room.nextToken()) == 0) ? false : true;
                        password = room.nextToken();

                        ChatRoom chatRoom = new ChatRoom(roomName, roomMaxUser, isRock, password, name);

                        result = st_waitRoom.addRoom(chatRoom);
                        if (result == 0) {

                            st_roomNumber = ChatRoom.getRoomNumber();
                            boolean temp = chatRoom.addUser(st_name,this);
                            st_waitRoom.delUser(st_name);

                            st_buffer.setLength(0);
                            st_buffer.append(YES_CREATEROOM);
                            st_buffer.append(SEPARATOR);
                            st_buffer.append(st_roomNumber);
                            send(st_buffer.toString());
                            modifyWaitRoom();
                            modifyRoomUser(st_roomNumber, name, 1);
                        } else {

                            sendErrCode(NO_CREATEROOM, result);
                        }

                        break;

                    }

                    case REQ_ENTERROOM : {

                        String name, password;
                        int roomNumber, result;
                        name = st.nextToken();
                        roomNumber = Integer.parseInt(st.nextToken());

                        try {
                            password = st.nextToken();
                        }catch(NoSuchElementException e) {
                            password = "0";
                        }

                        result = st_waitRoom.joinRoom(name, this, roomNumber, password);

                        if (result == 0) {
                            st_buffer.setLength(0);
                            st_buffer.append(YES_ENTERROOM);
                            st_buffer.append(SEPARATOR);
                            st_buffer.append(roomNumber);
                            st_buffer.append(SEPARATOR);
                            st_buffer.append(name);
                            st_roomNumber = roomNumber;
                            send(st_buffer.toString());
                            modifyRoomUser(roomNumber, name, 1);
                            modifyWaitRoom();
                        } else {
                            sendErrCode(NO_ENTERROOM, result);
                        }

                        break;

                    }

                    case REQ_QUITROOM : {
                        String name;
                        int roomNumber;
                        boolean updateWaitInfo;
                        name = st.nextToken();
                        roomNumber = Integer.parseInt(st.nextToken());

                        updateWaitInfo = st_waitRoom.quitRoom(name, roomNumber, this);

                        st_buffer.setLength(0);
                        st_buffer.append(YES_QUITROOM);
                        st_buffer.append(SEPARATOR);
                        st_buffer.append(name);
                        send(st_buffer.toString());
                        st_roomNumber = WAITROOM;

                        if(updateWaitInfo) {
                            modifyWaitRoom();
                        } else {
                            modifyWaitRoom();
                            modifyRoomUser(roomNumber, name, 0);
                        }

                        break;
                    }

                    case REQ_LOGOUT : {
                        String name = st.nextToken();
                        st_waitRoom.delUser(name);

                        st_buffer.setLength(0);
                        st_buffer.append(YES_LOGOUT);
                        send(st_buffer.toString());
                        modifyWaitUser();
                        release();
                        break;
                    }

                    case REQ_SENDWORD : {

                        String name = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());

                        st_buffer.setLength(0);
                        st_buffer.append(YES_SENDWORD);
                        st_buffer.append(SEPARATOR);
                        st_buffer.append(name);
                        st_buffer.append(SEPARATOR);
                        st_buffer.append(st_roomNumber);
                        st_buffer.append(SEPARATOR);

                        try {
                            String data = st.nextToken();
                            st_buffer.append(data);
                        }catch(NoSuchElementException e) {}

                        broadcast(st_buffer.toString(), roomNumber);
                        break;

                    }

                    case REQ_SENDIMAGE : {

                        String name = st.nextToken();
                        int roomNumber = Integer.parseInt(st.nextToken());

                        st_buffer.setLength(0);
                        st_buffer.append(YES_SENDIMAGE);
                        st_buffer.append(SEPARATOR);
                        st_buffer.append(name);
                        st_buffer.append(SEPARATOR);
                        st_buffer.append(st_roomNumber);
                        st_buffer.append(SEPARATOR);

                        try {
                            String data = st.nextToken();
                            st_buffer.append(data);
                        }catch(NoSuchElementException e) {}

                        broadcast(st_buffer.toString(), roomNumber);
                        break;
                    }

                }

                Thread.sleep(100);
            }
        }catch(NullPointerException e) {
        }catch(InterruptedException e) {
            System.out.println(e);

            if(st_roomNumber == 0) {
                st_waitRoom.delUser(st_name);
            } else {
                boolean temp = st_waitRoom.quitRoom(st_name, st_roomNumber, this);
                st_waitRoom.delUser(st_name);
            }

            release();
        }catch(IOException e) {
            System.out.println(e);

            if(st_roomNumber == 0) {
                st_waitRoom.delUser(st_name);
            } else {
                boolean temp = st_waitRoom.quitRoom(st_name, st_roomNumber, this);
                st_waitRoom.delUser(st_name);
            }

            release();
        }
    }

    //연결을 종료함
    public void release() {
        try {
            if(st_in != null) st_in.close();
        }catch(IOException e1) {
        }finally {
            st_out = null;
        }

        try {
            if(st_sock != null) st_sock.close();
        }catch(IOException e1) {
        }finally {
            st_sock = null;
        }

        if(st_name != null) {
            System.out.println(st_name+"와 연결을 종료합니다.");
            st_name = null;
        }
    }
}
