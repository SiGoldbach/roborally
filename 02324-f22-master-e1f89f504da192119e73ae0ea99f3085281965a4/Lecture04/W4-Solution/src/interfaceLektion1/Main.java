package interfaceLektion1;
// Lektion 1: Interface

import java.io.*;
import java.util.*;

import interfaceLektion1.IStak.StakEmptyException;

public class Main {

    public static void main(String[] args) throws StakEmptyException {


        // TODO Auto-generated method stub
        int valg = 0;
        Scanner scan = new Scanner(System.in);
        try {
            while (valg != 3) {
                System.out.println("MENU TEST STACK");
                System.out.println("Test stack implemented as array: 1");
                System.out.println("Test stack implemented as list: 2");
                System.out.println("EXIT: 3");
                System.out.print("Enter option:");
                valg = scan.nextInt();
                try {
                    switch (valg) {
                        case 1: {

                            System.out.println("hvor mange elementer?");
                            int num = scan.nextInt();
                            IStak s = new ArrayStak<String>(num);
                            s.push("Dette");
                            s.push(" er");
                            s.push(" en");
                            // s.show();
                            s.push(" mærekelig");
                            s.push(" sætning");
                            s.show();
                            for (int i = 0; i < 5; i++)
                                System.out.println(s.pop());
                        }

                        break;
                        case 2: {
                            IStak s = new LinkedStak<String>();
                            s.push("Dette");
                            s.push(" er");
                            s.push(" en");
                            // s.show();
                            s.push(" mærekelig");
                            s.push(" sætning");
                            s.show();
                        }
                        break;
                        case 3:
                            System.out.println("Bye");
                            break;
                        default:
                            System.out.println("Ugyldigt tal");
                            break;
                    }
                } catch (Exception e) {
                    System.out.println("There was an exception:" + e.getMessage());
                    System.out.println("The program will try again, enter 3 if you want to stop program!" );
                }

            }
        } finally {
            scan.close();
        }

    }
}