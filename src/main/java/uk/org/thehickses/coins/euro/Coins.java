package uk.org.thehickses.coins.euro;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Coins
{

    public static void main(String[] args)
    {
        var defsByCountry = new DefinitiveCoinParser().parse()
                .collect(Collectors.groupingBy(DefinitiveCoinData::country));
        var commemsByCountry = Stream
                .concat(new CommemorativeCoinParserCoinDB().parse(),
                        new CommemorativeCoinParserECB().parse()
                                .filter(c -> c.imageUri()
                                        .contains("joint_comm")))
                .sorted()
                .collect(Collectors.groupingBy(CommemorativeCoinData::country));
        var outputter = new CoinPageOutputter();
        defsByCountry.keySet()
                .stream()
                .forEach(c -> outputter.output(c, defsByCountry.get(c), commemsByCountry.get(c)));
    }

}
