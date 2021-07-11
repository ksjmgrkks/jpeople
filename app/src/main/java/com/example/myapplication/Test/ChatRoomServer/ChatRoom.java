package com.example.myapplication.Test.ChatRoomServer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class ChatRoom
{
    private static final String DELIMETER = "'";
    private static final String DELIMETER1 = "=";
    public static int roomNumber = 0;
    private Vector userVector;
    private Hashtable userHash;
    private String roomName;
    private int roomMaxUser;
    private int roomUser;
    private boolean isRock;
    private String password;
    private String admin;

    public ChatRoom(String roomName, int roomMaxUser, boolean isRock, String password, String admin) {

        roomNumber++;
        this.roomName = roomName;
        this.roomMaxUser = roomMaxUser;
        this.roomUser = 0;
        this.isRock = isRock;
        this.password = password;
        this.admin = admin;
        this.userVector = new Vector(roomMaxUser);
        this.userHash = new Hashtable(roomMaxUser);
    }

    public boolean addUser(String name, ServerThread client) {

        if (roomUser == roomMaxUser) {
            return false;
        }
        userVector.addElement(name);
        userHash.put(name, client);
        roomUser++;
        return true;
    }

    public boolean checkPassword(String passwd) {
        return password.equals(passwd);
    }

    public boolean checkUserNames(String name) {
        Enumeration names = userVector.elements();
        while(names.hasMoreElements()) {
            String tempName = (String) names.nextElement();
            if (tempName.equals(name)) return true;

        }

        return false;

    }

    public boolean isRocked() {
        return isRock;
    }

    public boolean delUser(String name) {
        userVector.removeElement(name);
        userHash.remove(name);
        roomUser--;

        return userVector.isEmpty();
    }

    public synchronized String getUsers() {
        StringBuffer name = new StringBuffer();
        String names;
        Enumeration enu = userVector.elements();
        while(enu.hasMoreElements()){
            name.append(enu.nextElement());
            name.append(DELIMETER);
        }
        try{
            names = new String(name);
            names = names.substring(0,names.length() - 1);
        }catch (StringIndexOutOfBoundsException e){
                    return "";
        }

        return names;
    }

    public ServerThread getUser(String name){
        ServerThread client = null;
        client = (ServerThread) userHash.get(name);
        return client;
    }

    public Hashtable getClients(){
        return userHash;
    }

    public String toString(){
        StringBuffer room = new StringBuffer();
        room.append(roomName);
        room.append(DELIMETER1);
        room.append(String.valueOf(roomUser));
        room.append(DELIMETER1);
        room.append(String.valueOf(roomMaxUser));
        room.append(DELIMETER1);

        if (isRock) {
            room.append("비공개");
        } else {
            room.append("공개");
        }
        room.append(DELIMETER1);
        room.append(admin);
        return room.toString();
    }

    public static synchronized int getRoomNumber(){
        return roomNumber;
    }
}
