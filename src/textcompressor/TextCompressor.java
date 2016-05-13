 /**
 * Copyright (C) 2016 Luan Gustavo Orlandi
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 * @see https://github.com/luanorlandi/Text-Compressor for further details
 */

package textcompressor;

import java.io.IOException;
import java.util.Map;
import org.docopt.Docopt;

/**
 *
 * @author Orlandi
 */
public class TextCompressor {
    private static final String doc =
    "Usage: TextCompressor [-h show help] OPERATION (-i FILE_INPUT) (-o FILE_OUTPUT) [--bwt=BOOLEAN] [--txtblck=INTEGER] [--huffman=BOOLEAN] [--runl=BOOLEAN]\n"
    + "\n"
    + "-h --help    show this"
    + "-i FILE_INPUT"
    + "-o FILE_OUTPUT"
    + "--bwt BOOLEAN"
    + "--txtblck INTEGER"
    + "--huffman BOOLEAN"
    + "--runl BOOLEAN"
    + "\n";
    
    public static void main(String[] args) {
        final Map<String, Object> opts = new Docopt(doc).parse(args);
        
        RunLength rl = new RunLength();
        Huffman h = new Huffman();
        BWT b = new BWT();

        String operation = opts.get("OPERATION").toString();
        
        String input = opts.get("FILE_INPUT").toString();
        String output = opts.get("FILE_OUTPUT").toString();
        
        boolean bwt;
        if(opts.get("--bwt") != null) {
            bwt = "true".equals(opts.get("--bwt").toString());
        } else {
            bwt = false;
        }
        
        boolean runl;
        if(opts.get("--runl") != null) {
            runl = "true".equals(opts.get("--runl").toString());
        } else {
            runl = false;
        }
        
        boolean huffman;
        if(opts.get("--huffman") != null) {
            huffman = "true".equals(opts.get("--huffman").toString());
        } else {
            huffman = false;
        }
        
        int blockSize = 0;
        
        /* check for a new text block size for BWT */
        if(opts.get("--txtblck") != null) {
            blockSize = Integer.parseInt(opts.get("--txtblck").toString());
        }
        
        try {
            switch(operation) {
                /* encode operation */
                case "encode":
                    /* BWT */
                    if(bwt) {
                        /* update block size if required */
                        if(blockSize > 0) {
                            b.setBlockSize(blockSize);
                        }
                        
                        System.out.println("Encoding BWT with block size "
                            + b.getBlockSize() + "...");
                        
                        b.encode(input, output);
                        input = output;
                    }
                    
                    /* RunLength */
                    if(runl) {
                        System.out.println("Encoding RunLength...");
                        rl.encode(input, output);
                        input = output;
                    }
                    
                    /* Huffman */
                    if(huffman) {
                        System.out.println("Encoding Huffman...");
                        h.encode(input, output);
                    }
                    
                    System.out.println("Encoding finished.");
                    break;
                    
                /* decode operation */
                case "decode":
                    boolean compressed = true;
                    
                    while(compressed) {
                        switch(b.getCompression(input)) {
                            /* RunLength */
                            case 1:
                                System.out.println("Decoding RunLength...");
                                rl.decode(input, output);
                                input = output;
                                break;
                            /* Huffman */
                            case 2:
                                System.out.println("Decoding Huffman...");
                                h.decode(input, output);
                                input = output;
                                break;
                            /* BWT */
                            case 3:
                                System.out.println("Decoding BWT...");
                                b.decode(input, output);
                                input = output;
                                break;
                            default:
                                compressed = false;
                                break;
                        }
                    }
                    System.out.println("No more encode indentified. "
                            + "Decoding finished.");
                    
                    break;
                default:
                    System.out.println("Operation " + opts.get("OPERATION")
                    + "not detected.");
                break;
            }
        } catch(IOException ex) {
            System.out.println("I/O exception error: " + ex.getMessage());
            System.err.println(ex.getMessage());
        }
        
        /* manual input */
//        try {
//            b.setBlockSize(7);
//            b.encode("source.txt", "encode.txt");
//            h.encode("encode.txt", "encode.txt");
//            rl.encode("encode.txt", "encode.txt");
//            
//            rl.decode("encode.txt", "decode.txt");
//            h.decode("decode.txt", "decode.txt");
//            b.decode("decode.txt", "decode.txt");
//
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//        }
    }
}