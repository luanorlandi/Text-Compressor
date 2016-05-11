/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package textcompressor;

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
        
        System.err.println(opts);
        System.err.println(opts.get("FILE_INPUT"));
        System.err.println(opts.get("FILE_OUTPUT"));
        System.err.println(opts.get("--bwt"));
        System.err.println(opts.get("--txtblck"));
        System.err.println(opts.get("--huffman"));
        System.err.println(opts.get("--runl"));
//        RunLength rl = new RunLength();
//        Huffman h = new Huffman();
//        BWT b = new BWT();
//        try {
//    //            rl.encode("source.txt", "encode.txt");
//    //            rl.decode("encode.txt", "decode.txt");
//
//    //            h.encode("source.txt", "encode.txt");
//    //            h.decode("encode.txt", "decode.txt");
//
//            b.setBlockSize(7);
//            b.encode("source.txt", "encode.txt");
//            b.decode("encode.txt", "decode.txt");
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//        }
    }
}
