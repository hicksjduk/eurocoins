package uk.org.thehickses.coins;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.stream.Stream;

public class Commemoratives
{

    public static void main(String[] args)
    {
        try (var pw = new PrintWriter(new FileWriter("output/comm.csv")))
        {
            Stream.concat(new CommemorativeCoinParserCoinDB().parse(),
                    new CommemorativeCoinParserECB().parse())
                    .map(CommemorativeCoinData::toCsv)
                    .peek(System.out::println)
                    .forEach(pw::println);
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }

}
