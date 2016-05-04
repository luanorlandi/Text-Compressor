/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textcompressor;

import java.io.IOException;

/**
 *
 * @author Orlandi
 */
public class TextCompressor {
    
    public static void main(String[] args) {
        RunLength rl = new RunLength();
        Huffman h = new Huffman();
        BWT b = new BWT();
        
        try {
//            rl.encode("source.txt", "encode.txt");
//            rl.decode("encode.txt", "decode.txt");
            
//            h.encode("source.txt", "encode.txt");
//            h.decode("encode.txt", "decode.txt");
            
            b.encode("source.txt", "encode.txt");
            b.decode("encode.txt", "decode.txt");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
