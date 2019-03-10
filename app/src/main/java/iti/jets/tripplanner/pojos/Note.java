package iti.jets.tripplanner.pojos;

public class Note {
    String noteId;
    String noteName;
    String noteDescription;

    public Note(String noteName, String noteDescription) {
        this.noteName = noteName;
        this.noteDescription = noteDescription;
    }

    public Note() {

    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteDescription() {
        return noteDescription;
    }

    public void setNoteDescription(String noteDescription) {
        this.noteDescription = noteDescription;
    }
}
