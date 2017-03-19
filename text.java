import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
public class text {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        System.out.println(star("15619cctest"));
        System.out.println(split("ab-c 5 dddd+fsfds'dfsf*fsd"));
    }
    public static String split(String str) {
        String[] arr = str.split("[^(0-9|a-z|A-Z|\\-|\\')]+");
        return Arrays.toString(arr);
     }
    
    
    public static String star(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            if (i == 0 || i == str.length() - 1) {
                result += str.charAt(i);
            } else {
                result += "*";
            }
        }
        return result;
    }
    
    public static HashSet<String> transfer() {
        String FILENAME = "go_flux_yourself.txt";
        HashSet<String> banWords = new HashSet<String>();
        
        String line;
        try {
            FileReader fileReader = new FileReader(FILENAME);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(rot13(line));
                banWords.add(rot13(line));
            }
            bufferedReader.close();  
        } catch (IOException e) {
            e.printStackTrace();
        }
        return banWords;
    }
    public static String rot13(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= 97 && ch <= 122) {
                result += (char)((str.charAt(i) - 97 + 13)%26 + 'a');
            } else {
                result += ch;
            }
        }
        return result;
    }

}
