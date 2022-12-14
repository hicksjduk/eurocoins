package uk.org.thehickses.coins.euro;

import java.util.Comparator;

public record CommemorativeCoinData(String country, int year, int seq, String description,
        String imageUri) implements Comparable<CommemorativeCoinData>
{

    private static final Comparator<CommemorativeCoinData> comp = Comparator
            .comparing(CommemorativeCoinData::country)
            .thenComparing(CommemorativeCoinData::year)
            .thenComparing(CommemorativeCoinData::seq);

    @Override
    public int compareTo(CommemorativeCoinData o)
    {
        return comp.compare(this, o);
    }

    public String toCsv()
    {
        return "%s,%d,\"%s\", %s".formatted(country, year, description, imageUri);
    }
}