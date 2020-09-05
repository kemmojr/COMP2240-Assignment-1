import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Scheduler {
    public static void main(String args[]){

        Scanner reader = null;
        try {
            reader = new Scanner(new FileInputStream(args[0]));//Scanner reader object to use for stepping through the data in the file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Failure to find file");
            return;
        }

        Algorithms a = new Algorithms(reader);

        /*while (reader.hasNext())
            System.out.println(reader.next());*/
    }
}
