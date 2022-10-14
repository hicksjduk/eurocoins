package uk.org.thehickses.coins.uk;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CoinDataWriter
{
    public void write(Stream<CoinData> coins, PrintWriter pw)
    {
        pw.println("<table>");
        var it = coins.sorted().iterator();
        while (it.hasNext())
        {
            List<String> descriptions = new ArrayList<>();
            pw.println("<tbody style='page-break-inside: avoid; page-break-before: auto'>");
            pw.println("<tr>");
            for (var i = 0; i < 6 && it.hasNext(); i++)
            {
                var cd = it.next();
                pw.println("<td align='center'>");
                pw.println("<img src='%s' height=100>".formatted(cd.image()
                        .toString()));
                pw.println("</td>");
                descriptions.add("%s (%d)".formatted(cd.description(), cd.year()));
            }
            pw.println("</tr>");
            pw.println("<tr>");
            descriptions.stream()
                    .map("<td>%s</td>"::formatted)
                    .forEach(pw::println);
            pw.println("</tr>");
            pw.println("</tbody>");
        }
        pw.println("</table>");
    }

}
