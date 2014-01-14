package sk.cde.yapco;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * class for parsing rss feed
 * code stolen from http://developer.android.com/training/basics/network-ops/xml.html
 * specification: http://validator.w3.org/feed/docs/rss2.html
 */
public class RssFeedParser {
    // We don't use namespaces
    private static final String ns = null;

    private static final String PUB_DATE = "pubDate";
    private static final String PUB_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String DESCRIPTION = "description";
    private static final String CHANNEL = "channel";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    private static final String ITEM = "item";
    private static final String ENCLOSURE = "enclosure";

    public static Channel parse(String rssFeed) throws XmlPullParserException, IOException {
        InputStream in = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            in = new URL(rssFeed).openConnection().getInputStream();
            parser.setInput(in, null);
            parser.nextTag();
            parser.nextTag();

            RssFeedParser feedParser = new RssFeedParser();
            return feedParser.readChannel(parser);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return null;
    }


    private Channel readChannel(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Item> episodes = new ArrayList<Item>();

        parser.require(XmlPullParser.START_TAG, ns, CHANNEL);

        String title = null;
        String description = null;
        String link = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(TITLE)) {
                title = readTitle(parser);
            } else if (name.equals(DESCRIPTION)) {
                description = readDescription(parser);
            } else if (name.equals(LINK)) {
                link = readLink(parser);
            } else if (name.equalsIgnoreCase(ITEM)) {
                episodes.add(readItem(parser));
            } else {
                skip(parser);
            }
        }

        return new Channel(title, description, link, episodes );
    }


    /**
     * Processes item tags in the feed.
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private Item readItem(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ITEM);
        String title = null;
        String description = null;
        String link = null;
        Date pubDate = null;
        String mediaUrl = null;
        String mediaType = null;
        int mediaLength = 0;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equalsIgnoreCase(TITLE)) {
                title = readTitle(parser);
            } else if (name.equalsIgnoreCase(DESCRIPTION)) {
                description = readDescription(parser);
            } else if (name.equalsIgnoreCase(LINK)) {
                link = readLink(parser);
            } else if( name.equalsIgnoreCase(PUB_DATE)){
                pubDate = readPubDate(parser);
            } else if( name.equalsIgnoreCase(ENCLOSURE)){
                parser.require(XmlPullParser.START_TAG, ns, ENCLOSURE);
                mediaUrl = parser.getAttributeValue(null, "url");
                mediaType = parser.getAttributeValue(null, "type");
                mediaLength = Integer.parseInt(parser.getAttributeValue(null, "length"));
                parser.next();
                parser.require(XmlPullParser.END_TAG, ns, ENCLOSURE);
            } else {
                skip(parser);
            }
        }

        parser.require(XmlPullParser.END_TAG, ns, ITEM);

        return new Item(title, description, pubDate, link, mediaUrl, mediaType, mediaLength);
    }


    /**
     * Processes title tags in the feed.
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, TITLE);
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, TITLE);
        return title;
    }


    /**
     * Processes link tags in the feed.
     *
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, LINK);
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, LINK);
        return link;
    }


    /**
     * Processes description tags in the feed.
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, DESCRIPTION);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, DESCRIPTION);
        return summary;
    }


    /**
     * Processes pubDate tags in the feed.
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private Date readPubDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, PUB_DATE);
        DateFormat formatter = new SimpleDateFormat(PUB_DATE_FORMAT);
        Date pubDate = null;
        try {
            pubDate = formatter.parse(readText(parser));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        parser.require(XmlPullParser.END_TAG, ns, PUB_DATE);
        return pubDate;
    }


    /**
     * For the tags title and summary, extracts their text values.
     *
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}