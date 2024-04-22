import java.io.*;

public class LZ77 {
    int dictionary = 65530;


    public StringBuilder compress(StringBuilder str) {

        int size = str.length();
        StringBuilder strCompress = new StringBuilder();
        StringBuilder memory = new StringBuilder();
        int charCoded = 0;
        int type = 0;
        for (int i = 0; i < size; i++) {
            int dictionaryStart = 0;
            int dictionaryLeng = 0;
            for (int j = Math.max(0, i - dictionary); j < i; j++) {
                if (str.charAt(j) == str.charAt(i)) {//есть вхождение
                    int start = j;
                    int tmp = j + 1;
                    int length = 1;
                    while ((i + length) < size && str.charAt(tmp) == str.charAt(i + length)) {
                        tmp++;
                        length++;
                    }
                    if (dictionaryLeng < length) {
                        dictionaryStart = i - start;
                        dictionaryLeng = length;

                    }
                }
            }
            type = type << 1;
            if (dictionaryLeng > 1) {//запись из словаря
                memory.append((char) dictionaryStart);
                memory.append((char) dictionaryLeng);
                type++;
                i += dictionaryLeng - 1;


            } else {//обычная запись
                memory.append(str.charAt(i));
            }
            charCoded++;

            if (charCoded == 7) {
                strCompress.append((char) type);//запишим типы кодов
                charCoded = 0;
                type = 0;
                strCompress.append(memory);
                memory = new StringBuilder();

            }
        }
        if (charCoded != 0) {
            type = type << 7 - charCoded;//чтобы при дешифрации не было особого случая
            strCompress.append((char) type);//запишим типы кодов

            strCompress.append(memory);

        }
        return strCompress;
    }

    StringBuilder getBit(int a) {
        StringBuilder bytes = new StringBuilder();
        if (a < 128) {
            bytes.append(String.format("%8s", Integer.toBinaryString(a)).replace(' ', '0'));
        } else if (a < 16384) {
            bytes.append("10");
            bytes.append(String.format("%14s", Integer.toBinaryString(a)).replace(' ', '0'));
        } else {
            bytes.append("11");
            bytes.append(String.format("%16s", Integer.toBinaryString(a)).replace(' ', '0'));

        }
        return bytes;
    }

    StringBuilder getDecompressBit(String path) {
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
        return deCompressBits(bytes);
    }

    void toFileByte(StringBuilder str, String path) {
        byte[] strBit = getBytes(compressByte(str));
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(path));
            dos.write(strBit, 0, strBit.length);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getBytes(StringBuilder strBit) {
        byte[] bytes = new byte[strBit.length() / 8 + (strBit.length() % 8 == 0 ? 0 : 1)];
        int i;
        for (i = 0; i < bytes.length - (strBit.length() % 8 == 0 ? 0 : 1); i++) {
            int k = 0;
            for (int j = 1; j < 8; j++) {
                k <<= 1;
                k += Integer.parseInt("" + strBit.charAt(i * 8 + j));
            }
            if (strBit.charAt(i * 8) == '1') {
                k *= -1;
                k--;
            }
            bytes[i] = (byte) k;
        }
        if (strBit.length() % 8 != 0) {

            int k = 0;
            for (int j = i * 8; j < strBit.length(); j++) {
                k <<= 1;
                k += Integer.parseInt("" + strBit.charAt(j));
            }

            bytes[i] = (byte) k;
        }

        return bytes;
    }

    public StringBuilder compressByte(StringBuilder str) {

        int size = str.length();
        StringBuilder strBit = new StringBuilder();
        StringBuilder memory = new StringBuilder();
        int charCoded = 0;
        int type = 0;
        for (int i = 0; i < size; i++) {
            int dictionaryStart = 0;
            int dictionaryLeng = 0;
            for (int j = Math.max(0, i - dictionary); j < i; j++) {
                if (str.charAt(j) == str.charAt(i)) {//есть вхождение
                    int start = j;
                    int tmp = j + 1;
                    int length = 1;
                    while ((i + length) < size && str.charAt(tmp) == str.charAt(i + length)) {
                        tmp++;
                        length++;
                    }
                    if (dictionaryLeng < length) {
                        dictionaryStart = i - start;
                        dictionaryLeng = length;

                    }
                }
            }
            type = type << 1;
            if (dictionaryLeng > 1) {//запись из словаря

                memory.append(getBit(dictionaryStart));
                memory.append(getBit(dictionaryLeng));
                type++;
                i += dictionaryLeng - 1;


            } else {//обычная запись
                memory.append(getBit(str.charAt(i)));
            }
            charCoded++;

            if (charCoded == 8) {
                strBit.append(String.format("%8s", Integer.toBinaryString(type)).replace(' ', '0'));//запишим типы кодов
                charCoded = 0;
                type = 0;
                strBit.append(memory);
                memory = new StringBuilder();

            }
        }
        if (charCoded != 0) {
            type = type << 8 - charCoded;//чтобы при дешифрации не было особого случая
            strBit.append(String.format("%8s", Integer.toBinaryString(type)).replace(' ', '0'));//запишим типы кодов
            strBit.append(memory);

        }
        return strBit;
    }

    public StringBuilder deCompress(StringBuilder str) {
        StringBuilder strOut = new StringBuilder();
        int size = str.length();
        boolean strEnd = false;
        int j = 0;
        while (!strEnd && j < size) {
            int types = str.charAt(j);
            j++;
            for (int i = 6; i >= 0 && !strEnd; i--, j++) {


                if (j < size) {
                    if ((types & (int) Math.pow(2, i)) == 0) {//обычный символ
                        strOut.append(str.charAt(j));
                    } else {
                        int start = str.charAt(j);
                        j++;

                        int length = str.charAt(j);

                        for (int k = 0; k < length; k++) {
                            strOut.append(strOut.charAt(strOut.length() - start));
                        }
                    }

                } else {
                    strEnd = true;
                }
            }
        }
        return strOut;
    }


    int getIntForByte(int start, StringBuilder str) {

        int n = 0;
        int len = 0;
        if (str.charAt(start) == '0') {
            len = 7;
            start++;
        } else if (str.charAt(start + 1) == '0') {
            len = 14;
            start += 2;
        } else {
            len = 16;
            start += 2;
        }
        for (int i = 0; i < len; i++) {
            if (i + start < str.length()) {
                n <<= 1;
                n += Integer.parseInt("" + str.charAt(i + start));
            }
        }
        return n;
    }

    int getSize(int n) {
        if (n < 128) {
            return 8;
        } else if (n < 16384) {
            return 16;
        } else {
            return 18;
        }
    }

    public StringBuilder deCompressBits(StringBuilder str) {
        StringBuilder strOut = new StringBuilder();
        int size = str.length();

        int j = 0;
        while (j < size) {
            int shift = 0;
            for (int i = 0; i < 8; i++) {
                if (j + 8 + shift < str.length()) {
                    if (str.charAt(j + i) == '1') {//запись из словаря
                        int ret = getIntForByte(j + 8 + shift, str);
                        shift += getSize(ret);
                        int step = getIntForByte(j + 8 + shift, str);
                        shift += getSize(step);
                        for (int k = 0; k < step; k++) {
                            strOut.append(strOut.charAt(strOut.length() - ret));
                        }
                    } else {
                        int c = getIntForByte(j + 8 + shift, str);

                        shift += getSize(c);
                        strOut.append((char) c);
                    }
                } else if (j + shift < str.length()) {

                }
            }
            j += shift + 8;
        }
        return strOut;
    }
}
