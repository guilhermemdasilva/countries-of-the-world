package com.guilherme_silva.countriesoftheworld.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CountryCodeHelperTest {
    @Test
    public void getFlagImageResource() throws Exception {
        String input = "";
        int output;
        int expected = CountryCodeHelper.getDrawableResource("un");

        output = CountryCodeHelper.getFlagImageResource(input);

        assertEquals(expected, output);
    }

}