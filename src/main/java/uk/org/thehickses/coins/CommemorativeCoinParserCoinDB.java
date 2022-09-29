package uk.org.thehickses.coins;

import java.io.IOException;
import java.net.URI;
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

public class CommemorativeCoinParserCoinDB
{
    public static void main(String[] args) throws Exception
    {
        new CommemorativeCoinParserCoinDB().parse()
                .filter(c -> c.country()
                        .equals("Greece"))
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
            var national = doc.select("img")
                    .stream()
                    .map(parser())
                    .filter(Objects::nonNull)
                    .filter(c -> !c.country()
                            .equals("Eurozone"));
            var erasmus = parseErasmus();
            return Stream.concat(national, erasmus);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    Function<Element, CommemorativeCoinData> parser()
    {
        Pattern titlePattern = Pattern.compile("2 euro coin (.+)\\| (.+) (\\d+)", Pattern.DOTALL);
        AtomicInteger seq = new AtomicInteger();
        return e ->
            {
                var title = e.attr("title");
                var matcher = titlePattern.matcher(title);
                if (!matcher.matches())
                    return null;
                var imageUri = baseUri + e.attr("src");
                return new CommemorativeCoinData(matcher.group(2),
                        Integer.parseInt(matcher.group(3)), seq.getAndIncrement(), matcher.group(1)
                                .trim(),
                        imageUri);
            };
    }

    Stream<CommemorativeCoinData> parseErasmus()
    {
        try
        {
            var index = baseUri
                    + "/coins/2-euro-coin-35th-anniversary-of-the-erasmus-programme-eurozone-2022.html";
            return Jsoup.connect(index)
                    .get()
                    .select("a[href^=/coins/]")
                    .stream()
                    .map(e -> e.attr("href"))
                    .map(baseUri::concat)
                    .map(this::parseUri)
                    .map(uri -> new CommemorativeCoinData(getCountry(uri), 2022, 0,
                            "35th Anniversary of the Erasmus Programme", uri));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    String parseUri(String addr)
    {
        try
        {
            return baseUri + Jsoup.connect(addr)
                    .get()
                    .selectFirst(".detailCoinImg").attr("src");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    String getCountry(String uri)
    {
        var m = Pattern.compile(".*/images/(.+)/.*")
                .matcher(uri);
        if (!m.matches())
            throw new RuntimeException("Could not parse a country from " + uri);
        return m.group(1);
    }
}
