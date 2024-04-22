
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Huffman {
    Long start;

    public HashMap<Character, String> getCodeTree(String str) {

        HashMap<Character, String> charCode;

        ArrayList<HuffmanTreeNode> charRate = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            boolean flag = true;
            for (int i1 = 0; i1 < charRate.size(); i1++) {

                if (charRate.get(i1).getStr().charAt(0) == (str.charAt(i))) {
                    flag = false;
                    charRate.get(i1).addAmount();
                }
            }
            if (flag) {
                charRate.add(new HuffmanTreeNode("" + str.charAt(i), 1));
            }
        }
        Collections.sort(charRate);
        charCode = getCharCode(charRate);
        //  canonicalHuffmanCodesForCharCode(charCode);
        return charCode;

    }

    public HashMap<Character, Integer> getCodeLengForTree(String str) {

        HashMap<Character, Integer> charCode;

        HashMap<Character, Integer> charRate = new HashMap<>();

        for (int i = 0; i < str.length(); i++) {
            if (charRate.get(str.charAt(i)) == null) {
                charRate.put(str.charAt(i), 1);
            } else {
                charRate.put(str.charAt(i), charRate.get(str.charAt(i)) + 1);
            }
        }
        ArrayList<HuffmanTreeNode> charRateTreeNode = new ArrayList<>();

        for (Character character : charRate.keySet()) {
            charRateTreeNode.add(new HuffmanTreeNode(character + "", charRate.get(character)));
        }
        Collections.sort(charRateTreeNode);
        charCode = getCharLeng(charRateTreeNode);
        //  canonicalHuffmanCodesForCharCode(charCode);
        return charCode;

    }

    public HashMap<Character, Integer> getCharLeng(ArrayList<HuffmanTreeNode> charRate) {
        while (charRate.size() > 1) {
            HuffmanTreeNode newNode = new HuffmanTreeNode(charRate.get(0), charRate.get(1));
            charRate.remove(0);
            charRate.remove(0);

            charRate.add(newNode);
            Collections.sort(charRate);

        }
        HashMap<Character, Integer> charCode = new HashMap<>();
        lngFromTree(charRate.get(0), charCode, 0);
        return charCode;
    }

    public HashMap<Character, String> getCharCode(ArrayList<HuffmanTreeNode> charRate) {
        while (charRate.size() > 1) {
            HuffmanTreeNode newNode = new HuffmanTreeNode(charRate.get(0), charRate.get(1));
            charRate.remove(0);
            charRate.remove(0);
            for (int i = 0; i < charRate.size(); i++) {
                if (charRate.get(i).getAmount() > newNode.getAmount()) {
                    charRate.add(i, newNode);
                    break;
                }
                if (charRate.size() - 1 == i) {
                    charRate.add(newNode);
                    break;
                }
            }
            if (charRate.size() == 0) {
                charRate.add(newNode);
            }
        }
        HashMap<Character, String> charCode = new HashMap<>();
        codeFromTree(charRate.get(0), charCode, "");
        return charCode;
    }

    public void lngFromTree(HuffmanTreeNode tree, HashMap<Character, Integer> charLeng, int deep) {
        if (tree != null) {
            if (tree.left == null && tree.right == null) {
                charLeng.put(tree.getStr().charAt(0), deep);
            } else {
                lngFromTree(tree.left, charLeng, deep + 1);
                lngFromTree(tree.right, charLeng, deep + 1);
            }
        }
    }

    public void codeFromTree(HuffmanTreeNode tree, HashMap<Character, String> charCode, String str) {
        if (tree != null) {
            if (tree.left == null && tree.right == null) {
                charCode.put(tree.getStr().charAt(0), str);
            } else {
                codeFromTree(tree.left, charCode, str + "0");
                codeFromTree(tree.right, charCode, str + "1");
            }
        }
    }

    public HashMap<String, Character> canonicalHuffmanCodesForCharCode(HashMap<Character, String> charCode) {
        ArrayList<HuffmanTreeNode> CodesLengthArray = new ArrayList<>();
        for (char value : charCode.keySet()) {
            CodesLengthArray.add(new HuffmanTreeNode(String.valueOf(value), (charCode.get(value).length())));
        }
        return canonicalHuffman(CodesLengthArray);
    }

    public HashMap<String, Character> canonicalHuffman(ArrayList<HuffmanTreeNode> charLength) {
        Collections.sort(charLength);
        HashMap<String, Character> charCode = new HashMap<>();
        StringBuilder code = new StringBuilder("0");
        for (HuffmanTreeNode huffmanTreeNode : charLength) {
            while (code.length() < huffmanTreeNode.getAmount()) {
                code.append("0");
            }
            charCode.put(code.toString(), huffmanTreeNode.getStr().charAt(0));

            add(code);
        }
        //  System.out.println(charCode);
        return charCode;

    }

    public void add(StringBuilder a) {
        int n = a.length() - 1;
        while (a.charAt(n) == '1' && n > 0) {
            a.setCharAt(n, '0');
            n--;
        }
        a.setCharAt(n, '1');

    }


    public void codingInFileToBit(StringBuilder str, String path) {


        HashMap<Character, String> charCode = getCodeCanonicForStr(String.valueOf(str));
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            output.append(charCode.get(str.charAt(i)));
        }
        // запись в файл
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(path))) {
            // записываем значения

            dos.writeShort(charCode.size());//для алфовита мение 65536, но больший не поддерживает java

            dos.flush();
            for (Map.Entry<Character, String> entry : charCode.entrySet()) {
                dos.writeChar(entry.getKey());

                dos.flush();
                dos.writeByte(entry.getValue().length());//если код больше 256, будут проблему, но там и с алфовитом будут проблемы
                dos.flush();
            }
            int i = 0;
            while (i + 8 <= output.length()) {
                int myChar = 0;
                for (int j = 0; j < 8; j++, i++) {
                    myChar <<= 1;
                    myChar += output.charAt(i) == '1' ? 1 : 0;
                }

                dos.writeByte(myChar);

                dos.flush();
            }
            int myChar = 0;

            for (; i < output.length(); i++) {
                myChar <<= 1;
                myChar += output.charAt(i) == '1' ? 1 : 0;
            }
            if ((i % 8) > 0) {
                dos.writeByte(myChar);
            }
            dos.writeByte(8 - i % 8);
            dos.flush();
            dos.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public StringBuilder decodingBit(String path) {

        try {
            DataInputStream dos = new DataInputStream(new FileInputStream(path));
            int c = dos.readShort();
            if (c<0){
                c+=65536;
            }
            ArrayList<HuffmanTreeNode> charLength = new ArrayList<>();
            for (int i = 0; i < c; i++) {
                int ch = dos.readChar();
                int num = dos.readByte();
                if (num < 0) {
                    num+= 256;
                }
                charLength.add(new HuffmanTreeNode("" + (char) ch, num));
            }
            HashMap<String, Character> charCode = canonicalHuffman(charLength);

            StringBuilder strBinarDeCompress = new StringBuilder();
            StringBuilder strDeCompress = new StringBuilder();
            int last = 0;
            while (dos.available() > 0) {
                //while (c!=-2) {
                c = dos.readByte();
                if (c < 0) {
                    c += 256;
                }
                strBinarDeCompress.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
                last = c;
            }
            strBinarDeCompress.delete(strBinarDeCompress.length() - 8, strBinarDeCompress.length());
            if (last != 8) {
                strBinarDeCompress.delete(strBinarDeCompress.length() - 8, strBinarDeCompress.length() - 8 + last);
            }
            StringBuilder code = new StringBuilder();

            for (int i = 0; i < strBinarDeCompress.length(); i++) {



                code.append(strBinarDeCompress.charAt(i));

                if (charCode.containsKey(code.toString())) {

                    strDeCompress.append(charCode.get(code.toString()));
                    code = new StringBuilder();
                }
            }

            return strDeCompress;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<Character, String> getCodeCanonicForStr(String str) {

        HashMap<Character, Integer> CharLng = getCodeLengForTree(str);

        ArrayList<HuffmanTreeNode> charLength = new ArrayList<>();
        for (Character character : CharLng.keySet()) {
            int ch = character;
            int num = CharLng.get(character);
            charLength.add(new HuffmanTreeNode("" + (char) ch, num));
        }
        HashMap<String, Character> charCodeCanon = canonicalHuffman(charLength);
        HashMap<Character, String> charCode = new HashMap<>();
        for (String s : charCodeCanon.keySet()) {
            charCode.put(charCodeCanon.get(s), s);
        }
        return (charCode);
    }

    void time() {
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
    }

}
