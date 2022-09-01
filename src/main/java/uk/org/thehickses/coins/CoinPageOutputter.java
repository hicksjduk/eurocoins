package uk.org.thehickses.coins;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CoinPageOutputter
{
    private static int coinsPerRow = 6;

    public void output(List<CoinData> coins)
    {
        var country = coins.get(0)
                .country();
        try (PrintWriter pw = new PrintWriter(new FileWriter("output/%s.html".formatted(country))))
        {
            pw.println("<h1>%s</h1>".formatted(country));
            pw.println("<table>");
            IntStream.range(0, Math.ceilDiv(coins.size(), coinsPerRow))
                    .forEach(rowOutputter(pw, coins));
            pw.println("</table>");
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private IntConsumer rowOutputter(PrintWriter pw, List<CoinData> coins)
    {
        return row ->
            {
                var rowData = coins.stream()
                        .skip(row * coinsPerRow)
                        .limit(coinsPerRow)
                        .toArray(CoinData[]::new);
                pw.println("<tr><td>&nbsp;</td></tr>");
                pw.println("<tr>");
                Stream.of(rowData)
                        .forEach(coinPictureOutputter(pw));
                pw.println("</tr>");
                pw.println("<tr>");
                Stream.of(rowData)
                        .forEach(coinDescrOutputter(pw));
                pw.println("</tr>");
            };
    }

    private Consumer<CoinData> coinPictureOutputter(PrintWriter pw)
    {
        return cd ->
            {
                pw.print("<td align='center'>");
                pw.print("<img src='%s' height='200'>".formatted(cd.imageUri()));
                pw.println("</td>");
            };
    }

    private Consumer<CoinData> coinDescrOutputter(PrintWriter pw)
    {
        return cd ->
            {
                pw.print("<td align='center'>");
                pw.print("%s (%d)".formatted(cd.description(), cd.year()));
                pw.println("</td>");
            };
    }
}
