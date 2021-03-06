package ru.hotel.bee.changelog;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "addTestData", author = "aeuge")//, runAlways = true)
    public void insertBasicData(DB db) {
        //db.dropDatabase();
        DBCollection myCollection = db.getCollection("hotels");
        DBObject dbObject = (DBObject) JSON.parse("{'kod':'7730','name':'Murmansk Discovery (Мурманск)'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'3477','name':'Nord Point (Мурманск)'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'11349','name':'SMART BUISNESS HOTEL (Мурманск)'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'7731','name':'Вектор (Мурманск)'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'11882','name':'Компас (Североморск)'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'7732','name':'Североморск (Североморск)'}");
        myCollection.insert(dbObject);
    }

    @ChangeSet(order = "002", id = "addTypeData", author = "aeuge")//, runAlways = true)
    public void insertTypeData(DB db) {
        DBCollection myCollection = db.getCollection("types");
        DBObject dbObject = (DBObject) JSON.parse("{'kod':'1','type':'Картой через терминал'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'2','type':'Наличными через кассу'}");
        myCollection.insert(dbObject);
        dbObject = (DBObject) JSON.parse("{'kod':'3','type':'Безналичный расчет для юр. лиц'}");
        myCollection.insert(dbObject);
    }

    @ChangeSet(order = "003", id = "addTypeData2", author = "aeuge")//, runAlways = true)
    public void insertTypeData2(DB db) {
        DBCollection myCollection = db.getCollection("types");
        DBObject dbObject = (DBObject) JSON.parse("{'kod':'4','type':'Банковский перевод для физ. лиц'}");
        myCollection.insert(dbObject);
    }

}
