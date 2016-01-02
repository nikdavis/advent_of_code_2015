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
    public static String[] lineParser(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line.length());
                break;
            }
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("Encountered IO error.");
        }
        return new String[] {"haha", "notreadyyet"};
    }

    public static String parseString(String string) {
        String stringOut;
        // I've since learned I could use a StringBuilder to build the output string. This is harder.
        char[] buffer = new char[256];
        int index = 0;
        // Ignore the first and last quote, should probably test for and strip them in real life
        for(int i = 1; i < string.length() - 1; i++) {
            char newChar = 0;
            if(Constants.DEBUG) {
                System.out.println("Backslash found at: " + i);
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
                    newChar = (char)Integer.parseInt(string.substring(i+2, i+3));
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
            System.out.println("Input string (" + string + ") length: " + string.length());
            System.out.println("Output string (" + stringOut + ") length: " + stringOut.length());
        }
        return stringOut;
    }
}
