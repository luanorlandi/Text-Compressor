package textcompressor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 *
 * @author Orlandi
 */


public abstract class Compressor {
    public static int headerRunLength;
    public static int headerHuffman;
    public static int headerBWT;
    
    private FileInputStream fileInput;
    
    private final String fileTmp = "tmpFile.tmp";
    
    public Compressor() {
        /* char = 1, 2 and 3 are ot used in texts,
        using here as "reserved" numbers to
        express a compressed file */
        headerRunLength = 1;
        headerHuffman = 2;
        headerBWT = 3;
    }
    /* encode file input to output */
    public abstract void encode(String input, String output) throws IOException;
    
    /* decode file input to output */
    public abstract void decode(String input, String output) throws IOException;
    
    /* identifies the compression in a file */
    public int getCompression(String input) throws IOException {
        fileInput = new FileInputStream(input);

        int c = fileInput.read();
        fileInput.close();

        return c;
    }
    
    /* if the input is the same as output, it will
    use a tmp file used in huffman and run length */
    public FileOutputStream getTemporaryFile() throws IOException {
        FileOutputStream fileOutput;
        
        fileOutput = new FileOutputStream(fileTmp);
        
        return fileOutput;
    }
    
    /* remove tmp file and apply changes to a file output */
    public void removeTemporaryFile(String fileOutput) throws IOException {
        Files.copy(Paths.get(fileTmp), Paths.get(fileOutput), REPLACE_EXISTING);
        
        Files.delete(Paths.get(fileTmp));
    }
}