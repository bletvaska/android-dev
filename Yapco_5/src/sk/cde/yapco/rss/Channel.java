package sk.cde.yapco.rss;

import java.util.Iterator;
import java.util.List;

/**
 * Class represents RSS Channel
 */
public class Channel implements Iterable<Item> {
    public final String title;
    public final String link;
    public final String description;
    public final String podcastUrl;
    private List<Item> episodes;

    public Channel(String podcastUrl, String title, String description, String link, List<Item> episodes) {
        this.podcastUrl = podcastUrl;
        this.title = title;
        this.description = description;
        this.link = link;
        this.episodes = episodes;
    }

    @Override
    public String toString() {
        String result = "";
        for (Item episode : episodes)
            result += episode;

        return String.format("rss feed: %s\n%s\n%s\nURL: %s\nNr. of episodes: %d\n===============\n%s\n",
                this.podcastUrl, this.title, this.description, this.link, this.episodes.size(), result);
    }

    public Item get(int position) {
        return this.episodes.get(position);
    }

    @Override
    public Iterator<Item> iterator() {
        return this.episodes.iterator();
    }
}
