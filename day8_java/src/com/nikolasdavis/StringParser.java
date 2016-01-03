package com.nikolasdavis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nikolasdavis.Constants;

/**
 * Created by ndavis on 1/1/16.
 */
public class StringParser {

    public static String[] lineParser(String fileName, boolean decode) {
        int debugDiff = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(decode) {
                    debugDiff = debugDiff + (line.length() - decodeString(line).length());
                } else {
                    debugDiff = debugDiff + (encodeString(line).length() - line.length());
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("StringParser: File not found.");
        } catch (IOException ex) {
            System.out.println("StringParser: Encountered IO error.");
        }
        if(Constants.DEBUG) {
            System.out.println("StringParser: Difference of " + debugDiff + " chars detected.");
        }
        return new String[] {"haha", "notreadyyet"};
    }

    // Limited to 256 characters of output
    public static String decodeString(String string) {
        String stringOut;
        // I've since learned I could use a StringBuilder to build the output string. This is harder.
        char[] buffer = new char[256];
        int index = 0;
        // Ignore the first and last quote, should probably test for and strip them in real life
        for(int i = 1; i < string.length() - 1; i++) {
            char newChar = 0;
            if(Constants.DEBUG) {
                System.out.println("StringParser.parseString: Backslash found at: " + i);
            }
            if(string.charAt(i) == '\\') {
                char type = string.charAt(i+1);
                if(type == '"') {
                    newChar = '"';
                    i++;
                } else if(type == '\\') {
                    newChar = '\\';
                    i++;
                } else if(type == 'x') {
                    // Parse hexadecimal character
                    newChar = (char)Integer.parseInt(string.substring(i+2, i+3), 16);
                    i += 3;
                }
            } else {
                newChar = string.charAt(i);
            }
            buffer[index] = newChar;
            index++;
        }

        stringOut = new String(buffer, 0, index);
        if(Constants.DEBUG) {
            System.out.println("StringParser.parseString: Input string (" + string + ") length: " + string.length());
            System.out.println("StringParser.parseString: Output string (" + stringOut + ") length: " + stringOut.length());
        }
        return stringOut;
    }

    // Uses StringBuilder .. max value of Integer.MAX_VALUE (openjdk)
    public static String encodeString(String string) {
        StringBuilder stringOut = new StringBuilder();
        stringOut.append('"'); // First quote
        for(int i = 0; i < string.length(); i++) {
            if(Constants.DEBUG) {
                System.out.println("StringParser.parseString: Backslash found at: " + i);
            }
            if(string.charAt(i) == '\\') {
                stringOut.append(new String("\\\\"));
            } else if (string.charAt(i) == '"') {
                stringOut.append(new String("\\\""));
            } else {
                stringOut.append(string.charAt(i));
            }
        }
        stringOut.append('"'); // Second quote

        if(Constants.DEBUG) {
            System.out.println("StringParser.parseString: Input string (" + string + ") length: " + string.length());
            System.out.println("StringParser.parseString: Output string (" + stringOut + ") length: " + stringOut.length());
        }
        return stringOut.toString();
    }
}
