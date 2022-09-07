package uk.org.thehickses.coins;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CoinPageOutputter
{
    private static int coinsPerRow = 6;

    public void output(String country, List<DefinitiveCoinData> definitives,
            List<CommemorativeCoinData> commemoratives)
    {
        try (PrintWriter pw = new PrintWriter(new FileWriter("output/%s.html".formatted(country))))
        {
            pw.println("<h1>%s</h1>".formatted(country));
            pw.println("<table>");
            outputD(pw, definitives);
            outputC(pw, commemoratives);
            pw.println("</table>");
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private void outputD(PrintWriter pw, List<DefinitiveCoinData> coins)
    {
        var bySeries = coins.stream()
                .collect(Collectors.groupingBy(DefinitiveCoinData::series));
        bySeries.entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Entry::getKey))
                .forEach(e ->
                    {
                        var it = e.getValue()
                                .stream()
                                .sorted()
                                .iterator();
                        if (!it.hasNext())
                            return;
                        pw.println("<tr>");
                        for (var i = 0; i < 5; i++)
                        {
                            outputD(pw, it.next());
                            if (!it.hasNext())
                                return;
                        }
                        pw.println("</tr>");
                        pw.println("<tr>");
                        for (var i = 0; i < 3; i++)
                        {
                            outputD(pw, it.next());
                            if (!it.hasNext())
                                return;
                        }
                        pw.println("</tr>");
                    });
    }

    private void outputD(PrintWriter pw, DefinitiveCoinData cd)
    {
        pw.print("<td align='center'>");
        pw.print("<img src='%s' height='200'>".formatted(cd.imageUrl()));
        pw.println("</td>");
    }

    private void outputC(PrintWriter pw, List<CommemorativeCoinData> coins)
    {
        IntStream.range(0, Math.ceilDiv(coins.size(), coinsPerRow))
                .forEach(rowOutputter(pw, coins));
    }

    private IntConsumer rowOutputter(PrintWriter pw, List<CommemorativeCoinData> coins)
    {
        return row ->
            {
                var rowData = coins.stream()
                        .skip(row * coinsPerRow)
                        .limit(coinsPerRow)
                        .toArray(CommemorativeCoinData[]::new);
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

    private Consumer<CommemorativeCoinData> coinPictureOutputter(PrintWriter pw)
    {
        return cd ->
            {
                pw.print("<td align='center'>");
                pw.print("<img src='%s' height='200'>".formatted(cd.imageUri()));
                pw.println("</td>");
            };
    }

    private Consumer<CommemorativeCoinData> coinDescrOutputter(PrintWriter pw)
    {
        return cd ->
            {
                pw.print("<td align='center'>");
                pw.print("%s (%d)".formatted(cd.description(), cd.year()));
                pw.println("</td>");
            };
    }
}
