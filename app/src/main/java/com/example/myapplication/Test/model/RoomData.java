package com.example.myapplication.Test.model;

public class RoomData {
    private String username;
    private String roomNumber;

    public RoomData(String username, String roomNumber) {
        this.username = username;
        this.roomNumber = roomNumber;
    }
  public int SumTool(int sum1, int sum2) {
      return sum1 + sum2;
  }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}

