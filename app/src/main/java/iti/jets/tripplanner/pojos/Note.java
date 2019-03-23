package iti.jets.tripplanner.pojos;

public class Note {
    private String noteId;
    private String noteName;
    private String noteDescription;
    private boolean noteStatus;
    private String tripId;

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

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public boolean isNoteStatus() {
        return noteStatus;
    }

    public void setNoteStatus(boolean noteStatus) {
        this.noteStatus = noteStatus;
    }
}
