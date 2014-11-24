package com.hyperiongray.ranker2.db;

/**
 * Created by mark on 11/20/14.
 */

import com.hyperiongray.ranker2.index.Indexer;
import com.mongodb.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class MongoDbOp {
    private DB db;

    public static void main(String[] args) throws Exception {
        // test the connnection
        MongoDbOp instance = new MongoDbOp();
        instance.testConnection();
        instance.listCollections();
        instance.iterateThroughCollection();
    }

    private void testConnection() throws java.net.UnknownHostException {

// To directly connect to a single MongoDB server (note that this will not auto-discover the primary even
// if it's a member of a replica set:
        MongoClient mongoClient = new MongoClient();
// or
//        MongoClient mongoClient = new MongoClient( "localhost" );
// or
//        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
// or, to connect to a replica set, with auto-discovery of the primary, supply a seed list of members
//        MongoClient mongoClient = new MongoClient(Arrays.asList(new ServerAddress("localhost", 27017),
//                new ServerAddress("localhost", 27018),
//                new ServerAddress("localhost", 27019)));

        db = mongoClient.getDB("MemexHack");
        System.out.println("Connection to MongoDB established");
    }

    private void listCollections() {
        System.out.println("List collections:");
        // get a list of the collections in this database and print them out
        Set<String> collectionNames = db.getCollectionNames();
        for (final String s : collectionNames) {
            System.out.println(s);
        }
    }

    private void iterateThroughCollection() {
        Indexer indexer = new Indexer();
        indexer.setIndexLocation("output/index");
        DBCollection collection = db.getCollection("urlinfo");
        DBObject myDoc = collection.findOne();
        System.out.println(myDoc);

        DBCursor cursor = collection.find();
        int count = 0;
        try {
            indexer.openIndexForWrite();
            while (cursor.hasNext()) {
                ++count;
                DBObject object = cursor.next();
                Set<String> keys = object.keySet();
                String html = (String) object.get("html");
                if (html != null) {
                    Document doc = Jsoup.parse(html);
                    String text = doc.text();
                    indexer.addTextToIndex(text);
                }
            }
            indexer.closeIndex();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            cursor.close();
        }
        System.out.println(count + " entries in MongoDB indexed");
    }
}