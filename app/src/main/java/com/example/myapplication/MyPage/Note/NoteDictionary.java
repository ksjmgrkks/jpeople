package com.example.myapplication.MyPage.Note;

public class NoteDictionary {
    private String noteTitle;
    private String noteDate;
    private String noteContents;
    private String noteNumber;

    public String getNoteNumber() {
        return noteNumber;
    }
    public void setNoteNumber(String noteNumber) {
        this.noteNumber = noteNumber;
    }
    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteContents() {
        return noteContents;
    }

    public void setNoteContents(String noteContents) {
        this.noteContents = noteContents;
    }
    public NoteDictionary(String noteTitle, String noteDate, String noteContents, String noteNumber) {
        this.noteTitle = noteTitle;
        this.noteDate = noteDate;
        this.noteContents = noteContents;
        this.noteNumber = noteNumber;
    }
}

