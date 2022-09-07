package uk.org.thehickses.coins;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class CommemorativeCoinParser
{
    public static void main(String[] args) throws Exception
    {
        new CommemorativeCoinParser().parse()
                .sorted()
                .forEach(System.out::println);
    }

    static final Map<String, Integer> accessions = Stream
            .of("Slovenia 2007", "Cyprus 2008", "Malta 2008", "Slovakia 2009", "Estonia 2011",
                    "Latvia 2014", "Lithuania 2015", "Croatia 2023", "Andorra 2014")
            .map(Pattern.compile("(\\S+) (\\d+)")::matcher)
            .filter(Matcher::matches)
            .collect(Collectors.toMap(m -> m.group(1), m -> Integer.parseInt(m.group(2))));
    static final String baseUri = "https://www.coin-database.com";
    static final String dataPage = "/series/eurozone-commemorative-2-euro-coins-2-euro.html?";

    public Stream<CommemorativeCoinData> parse()
    {
        var url = baseUri + dataPage;
        try
        {
            var doc = Jsoup.connect(url)
                    .get();
            var byCountry = doc.select("img")
                    .stream()
                    .map(parser())
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(CommemorativeCoinData::country));
            return byCountry.entrySet()
                    .stream()
                    .filter(e -> !e.getKey()
                            .equals("Eurozone"))
                    .flatMap(e -> Stream.concat(e.getValue()
                            .stream(),
                            byCountry.get("Eurozone")
                                    .stream()
                                    .filter(cd -> cd.year() >= accessions.getOrDefault(e.getKey(),
                                            2001))
                                    .map(cd -> new CommemorativeCoinData(e.getKey(), cd.year(), cd.seq(),
                                            cd.description(), cd.imageUri()))));
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    Function<Element, CommemorativeCoinData> parser()
    {
        Pattern titlePattern = Pattern.compile("2 euro coin (.+) \\| (.+) (\\d+)");
        AtomicInteger seq = new AtomicInteger();
        return e ->
            {
                var title = e.attr("title");
                var matcher = titlePattern.matcher(title);
                if (!matcher.matches())
                    return null;
                var imageUri = baseUri + e.attr("src");
                return new CommemorativeCoinData(matcher.group(2), Integer.parseInt(matcher.group(3)),
                        seq.getAndIncrement(), matcher.group(1)
                                .trim(),
                        imageUri);
            };
    }

}
