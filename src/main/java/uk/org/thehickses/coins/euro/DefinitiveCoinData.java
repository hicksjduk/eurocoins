package uk.org.thehickses.coins.euro;

import java.util.Comparator;

public record DefinitiveCoinData(String country, int series, int centValue, String imageUrl)
        implements Comparable<DefinitiveCoinData>
{

    private static final Comparator<DefinitiveCoinData> comp = Comparator
            .comparing(DefinitiveCoinData::country)
            .thenComparing(DefinitiveCoinData::series)
            .thenComparing(DefinitiveCoinData::centValue, Comparator.reverseOrder());

    @Override
    public int compareTo(DefinitiveCoinData o)
    {
        return comp.compare(this, o);
    }

}
