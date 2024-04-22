
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;


public class Main {


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("введите путь к сжимаемому файлу");
        String path ;
        path= scanner.nextLine();

        StringBuilder strIn = readFile( path);
        System.out.println("введите путь к  каталогу, где будут созданы новые файлы");
        path = scanner.nextLine();

        Huffman huffman = new Huffman();
        LZ77 lz77 = new LZ77();

       lz77.toFileByte(strIn, "lz2");

        System.out.println("кодирование lz77 завершено");
        huffman.codingInFileToBit(bitLzToChar("lz2", 16), path+"\\lzha.txt" );

        System.out.println("кодирование HA завершено");

        System.out.println("начало раскодирования");

        StringBuilder ha = huffman.decodingBit(path+"\\lzha.txt" );
        ha = charToLzBit(ha);
        System.out.println("раскодирование HA завершено");
        toFile(lz77.deCompressBits(ha), path+"\\lzHaDe.txt");
        System.out.println("раскодирование lz77 завершено");
    }

    static StringBuilder charToLzBit(StringBuilder in) {
        StringBuilder out = new StringBuilder();
        int last = 0;
        for (int i = 0; i < in.length(); i++) {
            int c = in.charAt(i);
            out.append(String.format("%16s", Integer.toBinaryString(c)).replace(' ', '0'));
            last = c;
        }
        out.delete(out.length() - 16, out.length());
        out.delete(out.length() - 16 + last, out.length());

        return out;
    }

    static StringBuilder bitLzToChar(String path, int sizeStep) {
        StringBuilder bytes = new StringBuilder();
        try {
            DataInputStream dos = new DataInputStream(new FileInputStream(path));
            while (dos.available() > 0) {
                int k = dos.readByte();
                if (k < 0) {
                    k = 127 - k;
                }
                bytes.append(String.format("%8s", Integer.toBinaryString(k)).replace(' ', '0'));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder out = new StringBuilder();

        int last = 0;
        for (int i = 0; i < bytes.length(); ) {

            int n = 0;

            for (int j = 0; j < sizeStep; j++, i++) {
                n <<= 1;
                if (i < bytes.length()) {
                    n += bytes.charAt(i) == '1' ? 1 : 0;

                } else {
                    last++;
                }
            }

            out.append((char) n);
        }
        out.append((char) (sizeStep - last));

        return out;
    }

    public static StringBuilder readFile(String path){
        StringBuilder strIn = new StringBuilder();

        try {
            FileReader reader = new FileReader(new File(path));
            int c;
            while ((c = reader.read()) != -1) {
                strIn.append((char) c);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return strIn;
    }
    static void toFile(StringBuilder str, String path) {
        try {
            FileWriter writer = new FileWriter(path, false);
            writer.write(str.toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}