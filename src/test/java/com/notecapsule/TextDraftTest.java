package com.notecapsule;

import com.notecapsule.memory.MemoryDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class TextDraftTest {

    @Test
    public void nullMemory(){
      assertThrows(IllegalArgumentException.class,
        () -> new MemoryDTO(null));

    }

    @Test
    public void whiteSpaceMemory() {
       assertThrows(IllegalArgumentException.class,
               () -> new MemoryDTO("      "));
    }

    @Test
    public void goodMemory(){
        MemoryDTO memoryDTO = new MemoryDTO("QuentinAureliaAreTheBest");

        assertEquals("QuentinAureliaAreTheBest", memoryDTO.getNoteText());
    }

}
