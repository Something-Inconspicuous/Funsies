package unit1.test;

import unit1.LinkedQueue;
import unit1.PetStylist;

public class PetStylistTest {
    public static void main(String[] args) {
        PetStylist ps = new PetStylist();
        PetStylist.Data missed = ps.simulate();
        System.out.println("Missed " + missed);
        System.out.println(missed.percentMissed() + "%");
    }
}
