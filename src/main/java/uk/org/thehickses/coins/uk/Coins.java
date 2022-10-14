package uk.org.thehickses.coins.uk;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Coins
{
    public static void main(String[] args)
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter("output/uk/50p.html")))
        {
            new CoinDataWriter50p().write(pw);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
