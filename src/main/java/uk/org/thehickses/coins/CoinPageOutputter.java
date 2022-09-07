package uk.org.thehickses.coins;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CoinPageOutputter
{
    private static int coinsPerRow = 6;
    private static Map<Integer, Integer> sizes = Stream
            .of("200, 2575", "100, 2325", "50, 2425", "20, 2225", "10, 1975", "5, 2125", "2, 1875",
                    "1, 1625")
            .map(s -> s.split("\\D+"))
            .collect(Collectors.toMap(a -> Integer.parseInt(a[0]),
                    a -> Integer.parseInt(a[1]) * 150 / 2575));

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
        pw.print("<img src='%s' height='%d'>".formatted(cd.imageUrl(), sizes.get(cd.centValue())));
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
                var rowData = coins.subList(row * coinsPerRow,
                        Math.min(coins.size(), (row + 1) * coinsPerRow));
                pw.println("<tr><td>&nbsp;</td></tr>");
                pw.println("<tr>");
                rowData.forEach(coinPictureOutputter(pw));
                pw.println("</tr>");
                pw.println("<tr>");
                rowData.forEach(coinDescrOutputter(pw));
                pw.println("</tr>");
            };
    }

    private Consumer<CommemorativeCoinData> coinPictureOutputter(PrintWriter pw)
    {
        return cd ->
            {
                pw.print("<td align='center'>");
                pw.print("<img src='%s' height='%d'>".formatted(cd.imageUri(), sizes.get(200)));
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
