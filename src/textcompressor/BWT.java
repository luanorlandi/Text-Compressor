 /**
 * Copyright (C) 2016 Luan Gustavo Orlandi
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 * @see https://github.com/luanorlandi/Text-Compressor for further details
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
    private final String eof;   /* delimiter for blocks */
    
    public BWT() {
        blockSize = 8;          /* default */
        eof = "\u001a";         /* character EOF */
    }
    
    /* rotate to left a string s for r times */
    public String rotate(String s, int r) {
        String tmp = s+s;
        
        return tmp.substring(r, r+s.length());
    }
    
    /* permute a given block for BWT */
    private String encodeBlock(String original) {
        original += eof;
        
        String block;
        String rotation[] = new String[original.length()];
        
        /* build table (square matrix of char) */
        for(int i = 0; i < rotation.length; i++) {
            rotation[i] = rotate(original, i);
        }
        
        Arrays.sort(rotation);
        
        block = "";
        
        /* get last column as string */
        for(String rot : rotation) {
            block += rot.substring(rot.length() - 1); 
        }
        
        return block;
    }
    
    /* undo a given permutated block */
    private String decodeBlock(String code) {
        String rotation[] = new String[code.length()];
        
        /* init table */
        for(int i = 0; i < rotation.length; i++) {
            rotation[i] = "";
        }
        
        /* build table */
        for(int i = 0; i < rotation.length; i++) {
            /* build a column (always the fist one) */
            for(int j = 0; j < rotation.length; j++) {
                rotation[j] = code.charAt(j) + rotation[j];
            }
            Arrays.sort(rotation);
        }
        
        /* search for the original string which ends with delimiter */
        for(String rot : rotation) {
            if(rot.endsWith(eof)) {
                return rot.substring(0, rot.length() - 1);
            }
        }
        
        /* something wrong, probably not a given BWT encoded file */
        System.err.println("Did not detected delimiter in block: " + code);
        System.exit(-1);
        return null;
    }

    /* encode file input to output */
    @Override
    public void encode(String input, String output) throws IOException {
        FileInputStream fileInput;
        BitOutputStream fileOutput;
        
        byte[] fileText;
        
        int i;
        String block;
        String fileTextString;
        
        /* read input */
        fileInput = new FileInputStream(input);
        
        fileText = new byte[fileInput.available()];
        fileInput.read(fileText);
        
        fileInput.close();
        
        /* write output */
        fileOutput = new BitOutputStream(output);
        
        /* header */
        fileOutput.write(Byte.SIZE, headerBWT);
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
        
        fileOutput.close();
    }

    /* decode file input to output */
    @Override
    public void decode(String input, String output) throws IOException {
        BitInputStream fileBitInput;
        FileInputStream fileInput;
        FileOutputStream fileOutput;
        
        byte[] fileText;
        
        int i;
        String block;
        String fileTextString;
        
        /* read input */
        fileBitInput = new BitInputStream(input);
        fileInput = new FileInputStream(input);
        
        /* header */
        fileBitInput.skip(Byte.BYTES);
        blockSize = fileBitInput.readBits(Integer.SIZE);
        fileBitInput.close();
        
        /* skip header */
        fileInput.skip(Byte.BYTES);
        fileInput.skip(Integer.BYTES);
        
        fileText = new byte[fileInput.available()];
        fileInput.read(fileText);
        
        /* convert byte[] to String */
        fileTextString = new String(fileText, StandardCharsets.UTF_8);
        
        fileInput.close();
        
        /* write output */
        fileOutput = new FileOutputStream(output);
        
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
        
        fileOutput.close();
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
}
