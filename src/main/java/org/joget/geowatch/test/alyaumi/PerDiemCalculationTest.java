package org.joget.geowatch.test.alyaumi;

import static org.junit.jupiter.api.Assertions.*;

class PerDiemCalculationTest {
    PerDiemCalculation calc;
    @org.junit.jupiter.api.BeforeAll
    void setUp() {
        calc = new PerDiemCalculation();
    }

    @org.junit.jupiter.api.AfterAll
    void tearDown() {
        calc = null;
    }

    /*@org.junit.jupiter.api.Test
    void getAmount() {
    }

    @org.junit.jupiter.api.Test
    void isInternationalTrip() {
    }

    @org.junit.jupiter.api.Test
    void getPerDiemAmount() {
    }*/

    @org.junit.jupiter.api.Test
    void getTotalPerDiemAmount() {
        double amount = calc.getTotalPerDiemAmount("16",250);
        assertEquals(1650,amount);
    }
}