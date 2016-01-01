package com.nikolasdavis;

import com.nikolasdavis.Circuit;

public class Main {

    public static void main(String[] args) {
	    Circuit circuit = new Circuit("./input.txt");
        System.out.println("Wire a has value " + circuit.get("a") + ".");
        System.out.println(0x8000 << 1 & 0xffff);
    }
}
