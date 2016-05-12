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

/**
 *
 * @author Orlandi
 */
public class RunLength extends Compressor {
    public RunLength() {
        
    }
    
    /* compress the file input to output */
    @Override
    public void encode(String input, String output) throws IOException {
        FileInputStream fileInput;
        BitOutputStream fileOutput;
        
        byte[] fileText;
        
        int length, i;
        int bitLength;      /* number of bits for each length */
        
        /* read input */
        fileInput = new FileInputStream(input);
        
        fileText = new byte[fileInput.available()];
        fileInput.read(fileText);
        
        bitLength = getHighestLength(fileText, fileText.length);
        bitLength = Integer.toBinaryString(bitLength).length();
        
        fileInput.close();
        
        /* write output */
        fileOutput = new BitOutputStream(output);
        
        /* header */
        fileOutput.write(Byte.SIZE, headerRunLength);
        fileOutput.write(Integer.SIZE, bitLength);
        
        length = 1;

        for(i = 0; i < fileText.length - 1; i++) {
            if(fileText[i] == fileText[i+1]) {
                length++;
            } else {
                fileOutput.write(bitLength, length);
                fileOutput.write(Byte.SIZE, fileText[i]);

                length = 1;
            }
        }
        
        fileOutput.write(bitLength, length);
        fileOutput.write(Byte.SIZE, fileText[i]);
        
        fileOutput.close();
    }
    
    /* decompress the file input to output */
    @Override
    public void decode(String input, String output) throws IOException {
        BitInputStream fileInput;
        FileOutputStream fileOutput;
        
        int character;
        int length;
        int bitLength;      /* number of bits for each length */
        
        fileInput = new BitInputStream(input);
        
        /* header */
        fileInput.skip(Byte.BYTES);
        bitLength = fileInput.readBits(Integer.SIZE);
        
        length = fileInput.readBits(bitLength);
        character = fileInput.readBits(Byte.SIZE);
        
        fileOutput = this.getTemporaryFile();
        
        while(length > 0) {
            for(int i = 0; i < length; i++) {
                fileOutput.write(character);
            }
            
            length = fileInput.readBits(bitLength);
            character = fileInput.readBits(Byte.SIZE);
        }
        
        fileInput.close();
        fileOutput.close();
        
        /* apply decode on output */
        this.removeTemporaryFile(output);
    }
    
    /* calculate the maximum length */
    private int getHighestLength(byte[] fileText, int fileSize) {
        int length;
        int maxLength;
        
        if(fileSize > 0) {
            length = 1;
            maxLength = 1;
            
            for(int i = 0; i < fileSize - 1; i++) {
                if(fileText[i] == fileText[i+1]) {
                    length++;
                } else {
                    if(length > maxLength) {
                        maxLength = length;
                    }
                    
                    length = 1;
                }
            }
            
            if(length > maxLength) {
                maxLength = length;
            }
        } else {
            maxLength = 0;
        }
        
        return maxLength;
    }
}
