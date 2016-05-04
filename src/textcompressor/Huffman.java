/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textcompressor;

import bit.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Orlandi
 */
public class Huffman extends Compressor {
    private final int maxChar = 256;
    
    public Huffman() {
        
    }
    
    /* compress the file input to output */
    @Override
    public void encode(String input, String output) throws IOException {
        FileInputStream fileInput;
        BitOutputStream fileOutput;
        
        byte[] fileText;
        Map<Integer, String> code;
        
        fileInput = new FileInputStream(input);
        fileOutput = new BitOutputStream(output);
        
        fileText = new byte[fileInput.available()];
        fileInput.read(fileText);
        
        code = getCode(fileText);
        
        writeHeader(fileOutput, fileText.length, code);
        
        for(int i = 0; i < fileText.length; i++) {
            String value = code.get((int) fileText[i]);
            fileOutput.write(value.length(), Integer.parseInt(value, 2));
        }
        
        fileInput.close();
        fileOutput.close();
    }

    /* decompress the file input to output */
    @Override
    public void decode(String input, String output) throws IOException {
        BitInputStream fileInput;
        FileOutputStream fileOutput;
        
        Map<String, Integer> code;
        
        fileInput = new BitInputStream(input);
        fileOutput = new FileOutputStream(output);
        
        int charCount = fileInput.readBits(Integer.SIZE);
        code = readHeader(fileInput);
        
//        System.err.println("+++++++++");
//        for (Map.Entry<String, Integer> entry : code.entrySet()) {
//            String key = entry.getKey();
//            int value = entry.getValue();
//            
//            System.err.println("key: " + key + " || value: " + value);
//        }
//        System.err.println("+++++++++");
        
        int i = 0;
        String buffer = "";
        while(i < charCount) {
            buffer += fileInput.readBits(1);

            if(code.containsKey(buffer)) {
                fileOutput.write(code.get(buffer));
                buffer = "";
                i++;
            }
        }
        
        fileInput.close();
        fileOutput.close();
    }
    
    /* sort 2 vectors, priority to 2nd vector */
    private void insertionSort(
            ArrayList<ArrayList<Integer>> v1, ArrayList<Long> v2) {
        int i, j;
        ArrayList<Integer> tmp1;
        long tmp2;
        
        for(i = 1; i < v1.size(); i++) {
            tmp1 = v1.get(i);
            tmp2 = v2.get(i);
            j = i - 1;

            while(j >= 0 && v2.get(j) > tmp2) {
                v1.set(j+1, v1.get(j));
                v2.set(j+1, v2.get(j));
                j--;
            }
            
            v1.set(j+1, tmp1);
            v2.set(j+1, tmp2);
        }
    }

    /* calculate frequency of each character in text */
    private ArrayList<Long> getFrequency(byte text[]) {
        int i;
        ArrayList<Long> frequency;
        
        frequency = new ArrayList<>();

        /* count character appearance */
        for(i = 0; i < maxChar; i++) {
            frequency.add((long) 0);
        }
        
        for(i = 0; i < text.length; i++) {
            frequency.set(text[i], 1 + frequency.get(text[i]));
        }

        return frequency;
    }

    private Map<Integer, String> getCode(byte[] fileText) {
        ArrayList<ArrayList<Integer>> tree;
        ArrayList<Long> frequency;
        
        Map<Integer, String> code;
        
        tree = new ArrayList();
        code = new HashMap<>();
        
        /* count all characters appearance */
        frequency = getFrequency(fileText);
        
        /* start tree */
        for(int i = 0; i < maxChar; i++) {
            ArrayList<Integer> integer = new ArrayList();
            integer.add(i);
            tree.add(integer);
        }
        
        /* remove unused characters */
        for(int i = frequency.size() - 1; i >= 0; i--) {
            if(frequency.get(i) == 0) {
                frequency.remove(i);
                tree.remove(i);
            }
        }
        
        /* init hashmap */
        for(int i = 0; i < maxChar; i++) {
            code.put(i, "");
        }
        
        /* build the tree and calculate each code */
        while(frequency.size() > 1) {
            insertionSort(tree, frequency);
            
            /* get lowest frequent */
            ArrayList<Integer> characters = tree.remove(0);
            long freq = frequency.remove(0);
            
            for(int i = 0; i < characters.size(); i++) {
                code.put(characters.get(i),
                        "0" + code.get(characters.get(i)));
            }
            
            for(int i = 0; i < tree.get(0).size(); i++) {
                code.put(tree.get(0).get(i),
                        "1" + code.get(tree.get(0).get(i)));
            }
            
            /* sum */
            tree.get(0).addAll(characters);
            frequency.set(0, freq + frequency.get(0));
            
//            System.err.println("-----");
//            for(int i = 0; i < frequency.size(); i++) {
//                System.err.println(Arrays.toString(tree.get(i).toArray()) + " " + frequency.get(i));
//            }
//            System.err.println("-----");
        }
        
        return code;
    }
    
    /* debug */
    private void showCode(Map<Integer, String> code) {
        System.err.println("=====");
        
        for (Map.Entry<Integer, String> entry : code.entrySet()) {
            int key = entry.getKey();
            String value = entry.getValue();
            
            if(!value.isEmpty()) {
                System.err.println("key: " + key + " || value: " + value);
            }
        }
        
        System.err.println("=====");
    }
    
    private void writeHeader(BitOutputStream fileOutput,
            int charCount, Map<Integer, String> code) {
        
        /* chracters count in file */
        fileOutput.write(Integer.SIZE, charCount);
        
        /* all characters code */
        for(int i = 0; i < maxChar; i++) {
            int l = code.get(i).length();
            fileOutput.write(Byte.SIZE, l);
            
            /* if the character appear in file, will have a code */
            if(l > 0) {
                int c = Integer.parseInt(code.get(i), 2);
                fileOutput.write(l, c);
            }
        }
    }
    
    private Map<String, Integer> readHeader(BitInputStream fileInput)
            throws IOException {
        
        Map<String, Integer> code;
        code = new HashMap<>();
        
        for(int i = 0; i < maxChar; i++) {
            int l = fileInput.readBits(Byte.SIZE);
            
            if(l > 0) {
                /* 01 and 001 will both be "1" */
                String c = Integer.toBinaryString(fileInput.readBits(l));
                
                /* fix conversion */
                while(c.length() < l) {
                    c = "0" + c;
                }
                
                code.put(c, i);
            }
        }
        
        return code;
    }
}
