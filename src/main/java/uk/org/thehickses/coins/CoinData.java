package uk.org.thehickses.coins;

import java.util.Comparator;

public record CoinData(String country, int year, int seq, String description,
        String imageUri) implements Comparable<CoinData>
{

    private static final Comparator<CoinData> comp = Comparator.comparing(CoinData::country)
            .thenComparing(CoinData::year)
            .thenComparing(CoinData::seq);

    @Override
    public int compareTo(CoinData o)
    {
        return comp.compare(this, o);
    }
}