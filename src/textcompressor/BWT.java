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
    private final String eof;
    
    public BWT() {
        blockSize = 8;
        eof = "\u001a";
    }
    
    /* rotate to left a string s for r times */
    public String rotate(String s, int r) {
        String tmp = s+s;
        
        return tmp.substring(r, r+s.length());
    }
    
    private String encodeBlock(String original) {
        original += eof;
        
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
    
    private String decodeBlock(String code) {
        String rotation[] = new String[code.length()];
        
        for(int i = 0; i < rotation.length; i++) {
            rotation[i] = "";
        }
        
        for(int i = 0; i < rotation.length; i++) {
            for(int j = 0; j < rotation.length; j++) {
                rotation[j] = code.charAt(j) + rotation[j];
            }
            Arrays.sort(rotation);
        }
        
        for(String rot : rotation) {
            if(rot.endsWith(eof)) {
                return rot.substring(0, rot.length() - 1);
            }
        }
        
        System.err.println("Did not detected delimiter in block: " + code);
        System.exit(-1);
        return null;
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
        
        /* convert byte[] to String */
        fileTextString = new String(fileText, StandardCharsets.UTF_8);
        
        /* write blocks */
        for(i = 0; i+blockSize <= fileTextString.length(); i += blockSize) {
            block = encodeBlock(fileTextString.substring(i, i+blockSize));
            
            for(int j = 0; j < block.length(); j++) {
                fileOutput.write(block.charAt(j));
            }
        }
        
        /* rest of the text */
        if(i < fileTextString.length()) {
            block = encodeBlock(
                    fileTextString.substring(i, fileTextString.length()));
            
            for(int j = 0; j < block.length(); j++) {
                fileOutput.write(block.charAt(j));
            }
        }
        
        fileInput.close();
        fileOutput.close();
    }

    @Override
    public void decode(String input, String output) throws IOException {
        BitInputStream fileBitInput;
        FileInputStream fileInput;
        FileOutputStream fileOutput;
        
        byte[] fileText;
        
        int i;
        String block;
        String fileTextString;
        
        fileBitInput = new BitInputStream(input);
        fileInput = new FileInputStream(input);
        fileOutput = new FileOutputStream(output);
        
        /* header */
        blockSize = fileBitInput.readBits(Integer.SIZE);
        fileBitInput.close();
        
        /* skip header */
        fileInput.skip(Integer.BYTES);
        fileText = new byte[fileInput.available()];
        fileInput.read(fileText);
        
        /* convert byte[] to String */
        fileTextString = new String(fileText, StandardCharsets.UTF_8);
        
        /* read blocks */
        for(i = 0; i+blockSize+1 <= fileTextString.length(); i += blockSize+1) {
            block = decodeBlock(fileTextString.substring(i, i+blockSize+1));
            
            fileOutput.write(block.getBytes(StandardCharsets.UTF_8));
        }
        
        /* rest of the text */
        if(i < fileTextString.length()) {
            block = decodeBlock(
                    fileTextString.substring(i, fileTextString.length()));
            
            fileOutput.write(block.getBytes(StandardCharsets.UTF_8));
        }
        
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
