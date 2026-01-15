import TaskClasses.Transaction;
import com.viko.parser.GenericParser;

import java.nio.file.Path;
import java.util.List;

public class MainTest {

    //usual AAA testing setup -> Arrange, Act, Assert
    public static void main(String[] args) {
        try {
            GenericParser parser = new GenericParser();

            Path path = Path.of(
                    MainTest.class.getClassLoader().getResource("transactions.txt").toURI());

            List<Transaction> transactions =
                    parser.parse(path.toString(), Transaction.class, GenericParser.ParsingMode.LENIENT);

            transactions.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
