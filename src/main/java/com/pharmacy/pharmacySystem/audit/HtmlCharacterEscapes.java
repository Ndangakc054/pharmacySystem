package com.pharmacy.pharmacySystem.audit;

import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

import java.io.Serializable;

/**public class HtmlCharacterEscapes extends CharacterEscapes {
    private final int[] asciiEscapes;

    public HtmlCharacterEscapes() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        // Désactive l'échappement pour les caractères HTML
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_NONE;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_NONE;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_NONE;
        asciiEscapes['"'] = CharacterEscapes.ESCAPE_NONE;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_NONE;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializedString getEscapeSequence(int ch) {
        return null;
    }
}**/
