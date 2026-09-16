package com.notecapsule.memory;

import java.time.Instant;

public class MemoryDTO {
    private Instant instant;
    private String noteText;

    public MemoryDTO(String noteText){
        if (noteText == null || noteText.isBlank()){
            throw new IllegalArgumentException("Note cannot be blank");
        }

        this.noteText = noteText;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}
