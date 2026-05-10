package com.demo.game.dungeoncrawl.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameRendererTest {

    @Test
    void constructorShouldRejectMissingCanvas() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new GameRenderer(null, 20, 15)
        );

        assertEquals("canvas", exception.getMessage());
    }
}
