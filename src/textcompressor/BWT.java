/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textcompressor;

import bit.BitInputStream;
import bit.BitOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author Orlandi
 */
public class BWT extends Compressor {
    private int blockSize;      /* characters count for each block */
    
    public BWT() {
        blockSize = 8;
    }
    
    /* rotate to left a string s for r times */
    public String rotate(String s, int r) {
        String tmp = s+s;
        
        return tmp.substring(r, r+s.length());
    }
    
    private String encodeBlock(String original) {
        String block;
        String rotation[] = new String[original.length()];
        
        for(int i = 0; i < rotation.length; i++) {
            rotation[i] = rotate(original, i);
        }
        
        Arrays.sort(rotation);
        
        block = "";
        
        for(String rot : rotation) {
            block += rot.substring(rot.length() - 1); 
        }
        
        return block;
    }

    @Override
    public void encode(String input, String output) throws IOException {
        FileInputStream fileInput;
        BitOutputStream fileOutput;
        
        byte[] fileText;
        
        int i;
        String block;
        String fileTextString;
        
        fileInput = new FileInputStream(input);
        fileOutput = new BitOutputStream(output);
        
        fileText = new byte[fileInput.available()];
        fileInput.read(fileText);
        
        /* header */
        fileOutput.write(Integer.SIZE, blockSize);
        
        fileTextString = new String(fileText, StandardCharsets.UTF_8);
        
        /* write blocks */
        for(i = 0; i+blockSize <= fileTextString.length(); i += blockSize) {
            block = encodeBlock(fileTextString.substring(i, i+blockSize));
            
            for(int j = 0; j < block.length(); j++) {
                fileOutput.write(block.charAt(j));
            }
            System.err.println(block);
        }
        
        /* rest of the text */
        if(i < fileTextString.length()) {
            block = encodeBlock(
                    fileTextString.substring(i, fileTextString.length()));
            
            for(int j = 0; j < block.length(); j++) {
                fileOutput.write(block.charAt(j));
            }
            System.err.println(block);
        }
        
        fileInput.close();
        fileOutput.close();
    }

    @Override
    public void decode(String input, String output) throws IOException {
        BitInputStream fileInput;
        FileOutputStream fileOutput;
        
        fileInput = new BitInputStream(input);
        fileOutput = new FileOutputStream(output);
        
        /* header */
        blockSize = fileInput.readBits(Integer.SIZE);
        
        
        fileInput.close();
        fileOutput.close();
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
}
