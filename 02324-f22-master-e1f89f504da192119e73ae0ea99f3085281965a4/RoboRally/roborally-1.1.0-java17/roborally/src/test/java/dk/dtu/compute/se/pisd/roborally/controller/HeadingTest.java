package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HeadingTest {
    /**
     * Test for heading method just to be sure it works as intend.
     */
    @Test
    void opposite(){
        Heading h1=Heading.SOUTH;

        Heading h2=h1.opposite();

        Assertions.assertEquals(h2,Heading.NORTH);

    }

}
