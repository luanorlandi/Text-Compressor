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
public abstract class Compressor {
    public Compressor() {
        
    }
    
    /* encode file input to output */
    public abstract void encode(String input, String output) throws IOException;
    
    /* decode file input to output */
    public abstract void decode(String input, String output) throws IOException;
}
