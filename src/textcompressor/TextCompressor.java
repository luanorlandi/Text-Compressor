/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
//        final Map<String, Object> opts = new Docopt(doc).parse(args);
//        
//        RunLength rl = new RunLength();
//        Huffman h = new Huffman();
//        BWT b = new BWT();
//
//        System.err.println(opts.get("OPERATION"));
//        System.err.println(opts.get("FILE_INPUT"));
//        System.err.println(opts.get("FILE_OUTPUT"));
//        System.err.println(opts.get("--bwt"));
//        System.err.println(opts.get("--txtblck"));
//        System.err.println(opts.get("--huffman"));
//        System.err.println(opts.get("--runl"));
//        
//        String operation = opts.get("OPERATION").toString();
//        
//        String input = opts.get("FILE_INPUT").toString();
//        String output = opts.get("FILE_OUTPUT").toString();
//        
//        boolean bwt = "true".equals(opts.get("--bwt").toString());
//        boolean runl = "true".equals(opts.get("--runl").toString());
//        boolean huffman = "true".equals(opts.get("--huffman").toString());
//        
//        int blockSize = 0;
//        
//        /* check for a new text block size for BWT */
//        if(opts.get("--txtblck") != null) {
//            blockSize = Integer.getInteger(opts.get("--txtblck").toString());
//        }
//        
//        try {
//            switch(operation) {
//                /* encode operation */
//                case "encode":
//                    /* BWT */
//                    if(bwt) {
//                        /* update block size if required */
//                        if(blockSize > 0) {
//                            b.setBlockSize(blockSize);
//                        }
//                        
//                        System.out.println("Encoding BWT with block size "
//                            + b.getBlockSize() + "...");
//                        
//                        b.encode(input, output);
//                    }
//                    
//                    /* RunLength */
//                    if(runl) {
//                        System.out.println("Encoding RunLength...");
//                        rl.encode(input, output);
//                    }
//                    
//                    /* Huffman */
//                    if(huffman) {
//                        System.out.println("Encoding Huffman...");
//                        rl.encode(input, output);
//                    }
//                    
//                    System.out.println("Encoding finished.");
//                    break;
//                    
//                /* decode operation */
//                case "decode":
//                    boolean compressed = true;
//                    
//                    while(compressed) {
//                        switch(b.getCompression(input)) {
//                            /* RunLength */
//                            case 1:
//                                System.out.println("Decoding RunLength...");
//                                rl.decode(input, output);
//                                break;
//                            /* Huffman */
//                            case 2:
//                                System.out.println("Decoding Huffman...");
//                                rl.decode(input, output);
//                                break;
//                            /* BWT */
//                            case 3:
//                                System.out.println("Decoding BWT...");
//                                rl.decode(input, output);
//                                break;
//                        }
//                    }
//                        Files.copy
//                    System.out.println("Decoding finished.");
//                    
//                    break;
//                default:
//                    System.out.println("Operation " + opts.get("OPERATION")
//                    + "not detected.");
//            }
//        } catch(IOException ex) {
//            System.out.println("I/O exception error: " + ex.getMessage());
//            System.err.println(ex.getMessage());
//        }
        
        RunLength rl = new RunLength();
        Huffman h = new Huffman();
        BWT b = new BWT();
        try {
                rl.encode("source.txt", "encode.txt");
                rl.decode("encode.txt", "decode.txt");

//                h.encode("source.txt", "encode.txt");
//                h.decode("encode.txt", "decode.txt");

//            b.setBlockSize(7);
//            b.encode("source.txt", "encode.txt");
//            b.decode("encode.txt", "decode.txt");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}