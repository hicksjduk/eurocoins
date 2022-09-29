package uk.org.thehickses.coins;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class CommemorativeCoinParserECB
{
    public static void main(String[] args)
    {
        new CommemorativeCoinParserECB().parse()
                .sorted()
                .forEach(System.out::println);
    }

    static final String baseUri = "https://www.ecb.europa.eu";

    public Stream<CommemorativeCoinData> parse()
    {
        try
        {
            var indexPage = baseUri + "/euro/coins/comm/html/index.en.html";
            var indexPageURI = URI.create(indexPage);
            return Jsoup.connect(indexPage)
                    .get()
                    .select(".box[href]")
                    .stream()
                    .map(e -> e.attr("href"))
                    .map(indexPageURI::resolve)
                    .map(Object::toString)
                    .flatMap(this::parse);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Stream<CommemorativeCoinData> parse(String yearPageAddr)
    {
        var year = Integer.parseInt(yearPageAddr.replaceAll("\\D+", ""));
        try
        {
            return Jsoup.connect(yearPageAddr)
                    .get()
                    .select(".box")
                    .stream()
                    .flatMap(parser(year));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private Function<Element, Stream<CommemorativeCoinData>> parser(int year)
    {
        AtomicInteger seq = new AtomicInteger();
        return elem ->
            {
                var countryGetter = countryGetter(elem.selectFirst("h3")
                        .ownText());
                var description = elem.selectFirst("strong:contains(feature)")
                        .parent()
                        .ownText();
                return elem.select("img[src$=.jpg]")
                        .stream()
                        .map(e -> e.attr("src"))
                        .map(src ->
                            {
                                return new CommemorativeCoinData(countryGetter.apply(src), year,
                                        seq.getAndIncrement(), description, baseUri + src);
                            })
                        .filter(c -> Objects.nonNull(c.country()));
            };
    }

    private Function<String, String> countryGetter(String country)
    {
        if (!country.startsWith("Euro"))
            return s -> normalise(country);
        var pattern = Pattern.compile(".*\\d+\\_([^_]+)\\.jpg");
        return s ->
            {
                var m = pattern.matcher(s);
                if (!m.matches())
                    return null;
                return normalise(m.group(1));
            };
    }

    private static Map<String, String> mappings = Stream
            .of("Vatican,Vatican City", "Nederland,Netherlands")
            .map(s -> s.split(","))
            .collect(Collectors.toMap(a -> a[0], a -> a[1]));

    private String normalise(String country)
    {
        return mappings.getOrDefault(country, country);
    }
}
