package sk.cde.yapco;

import java.util.Date;

/**
 * Class represents RSS Feed Episode
 * Created by mirek on 13.1.2014.
 */
public class Item {
    public final String title;
    public final String link;
    public final String description;
    public final Date published;
    public final String mediaUrl;
    public final String mediaType;
    public final int mediaLength;

    public Item(String title, String description, Date published, String link, String mediaUrl, String mediaType, int mediaLength){
        this.title = title;
        this.description = description;
        this.published = published;
        this.link = link;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.mediaLength = mediaLength;
    }

    @Override
    public String toString(){
        return String.format("%s (%s, %d)\n%s\n%s\n%s\n%s\n",
                this.title, this.mediaType, this.mediaLength, this.description, this.published, this.link, this.mediaUrl);
    }

}
