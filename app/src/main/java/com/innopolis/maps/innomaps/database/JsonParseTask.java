package com.innopolis.maps.innomaps.database;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static com.innopolis.maps.innomaps.database.TableFields.CREATOR;
import static com.innopolis.maps.innomaps.database.TableFields.DATETIME;
import static com.innopolis.maps.innomaps.database.TableFields.DESCRIPTION;
import static com.innopolis.maps.innomaps.database.TableFields.DISPLAY_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.EMAIL;
import static com.innopolis.maps.innomaps.database.TableFields.END;
import static com.innopolis.maps.innomaps.database.TableFields.EVENTS;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_POI;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.HASH;
import static com.innopolis.maps.innomaps.database.TableFields.ID;
import static com.innopolis.maps.innomaps.database.TableFields.LAST_UPDATE;
import static com.innopolis.maps.innomaps.database.TableFields.LINK;
import static com.innopolis.maps.innomaps.database.TableFields.LOCATION;
import static com.innopolis.maps.innomaps.database.TableFields.NULL;
import static com.innopolis.maps.innomaps.database.TableFields.RECURRENCE;
import static com.innopolis.maps.innomaps.database.TableFields.RRULE;
import static com.innopolis.maps.innomaps.database.TableFields.START;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;

public class JsonParseTask extends AsyncTask<Void, Void, String> {

    private SQLiteDatabase database;
    private SharedPreferences sPref;
    private final String resultJson = "";
    private final static String DELETE = "delete from ";

    public JsonParseTask(SQLiteDatabase database, SharedPreferences sPref) {
        this.database = database;
        this.sPref = sPref;
    }

    @Override
    protected String doInBackground(Void... params) {
        return Utils.getGoogleApi();
    }

    /**
     * Checks whether the JSON file was updated or not
     *
     * @param hashKey - md5 hash
     * @return true in case the JSON was updated
     */
    protected boolean jsonUpdated(String hashKey) {
        String savedText = sPref.getString(HASH, NULL);
        if (savedText.equals(hashKey)) {
            return false;
        } else {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(HASH, hashKey);
            removeEventTables();
            ed.apply();
            return true;
        }
    }

    private void removeEventTables() {
        removeTable(EVENTS);
        removeTable(EVENT_TYPE);
        removeTable(EVENT_POI);
    }

    protected boolean weekUpdated() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek()); //the starting date of current week
        Date updatedDate = null;
        try {
            updatedDate = Utils.googleTimeFormat.parse(sPref.getString(LAST_UPDATE, NULL));
        } catch (ParseException e) {
            e.printStackTrace();
        }
            /*If it's null or old - update the database*/
        if (updatedDate == null || !updatedDate.equals(cal.getTime())) {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(LAST_UPDATE, Utils.googleTimeFormat.format(cal.getTime()));
            removeEventTables();
            ed.apply();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPostExecute(String strJson) {
        JSONObject dataJsonObj;
        String md5 = new String(Hex.encodeHex(DigestUtils.md5(resultJson)));
        try {
            dataJsonObj = new JSONObject(strJson);
            if (jsonUpdated(md5) | weekUpdated()) {
                populateDB(dataJsonObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void removeTable(String tableName) {
        database.execSQL(DELETE + tableName);
    }


    public int populateDB(JSONObject dataJsonObj) throws JSONException {
        int eventsInserted = 0;
        JSONArray events = dataJsonObj.getJSONArray("items");
        for (int i = 0; i < events.length(); i++) {
            JSONObject jsonEvent = events.getJSONObject(i);
            String summary = NULL, htmlLink = NULL, start = NULL, end = NULL,
                    location = NULL, eventID = NULL, description = NULL,
                    creator_name = NULL, creator_email = NULL, checked = "0",
                    recurrence = NULL;
            Iterator<String> iter = jsonEvent.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                switch (key) {
                    case SUMMARY:
                        summary = jsonEvent.getString(SUMMARY);
                        break;
                    case LINK:
                        htmlLink = jsonEvent.getString(LINK);
                        break;
                    case START:
                        start = jsonEvent.getJSONObject(START).getString(DATETIME);
                        break;
                    case END:
                        end = jsonEvent.getJSONObject(END).getString(DATETIME);
                        break;
                    case LOCATION:
                        location = jsonEvent.getString(LOCATION);
                        break;
                    case "id":
                        eventID = jsonEvent.getString("id");
                        break;
                    case DESCRIPTION:
                        description = jsonEvent.getString(DESCRIPTION);
                        break;
                    case CREATOR:
                        creator_name = jsonEvent.getJSONObject(CREATOR).getString(DISPLAY_NAME);
                        creator_email = jsonEvent.getJSONObject(CREATOR).getString(EMAIL);
                        break;
                    case RECURRENCE:
                        recurrence = jsonEvent.getJSONArray(RECURRENCE).getString(0).replace(RRULE, NULL);
                        break;
                }
            }

            DateTime currentDate = new DateTime(new Date().getTime());
            RecurrenceRule rule;
            try {
                rule = new RecurrenceRule(recurrence);
            } catch (InvalidRecurrenceRuleException e) {
                e.printStackTrace();
                continue;
            }

            DateTime startDate;
            DateTime endDate;
            Long durationTime;

            try {
                startDate = new DateTime(Utils.googleTimeFormat.parse(start).getTime());
                endDate = new DateTime(Utils.googleTimeFormat.parse(end).getTime());
                durationTime = TimeUnit.MILLISECONDS.toMinutes(endDate.getTimestamp() - startDate.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }


            RecurrenceRuleIterator it = rule.iterator(startDate);
            it.fastForward(currentDate);
            int maxInstances = 3; // limit instances for 3 times

            while (it.hasNext() && (maxInstances-- > 0)) {
                DateTime nextInstance = it.nextDateTime();
                if (nextInstance.after(currentDate)) {
                    String finalStartDate = Utils.googleTimeFormat.format(new Date(nextInstance.getTimestamp()));
                    String finalEndDate = Utils.googleTimeFormat.format(new Date(nextInstance.addDuration(new Duration(1, 0, 0, durationTime.intValue(), 0)).getTimestamp()));
                    String locationArray[] = location.split("/");
                    Cursor poiCursor = null;
                    switch (locationArray.length) {
                        case 1:
                            poiCursor = database.rawQuery("SELECT * FROM POI WHERE building LIKE '%" + locationArray[0] + "%'", null);
                            break;
                        case 2:
                            poiCursor = database.rawQuery("SELECT * FROM POI WHERE building LIKE '%" + locationArray[0] + "%' AND floor LIKE '%" + locationArray[1] + "%'", null);
                            break;
                        case 3:
                            poiCursor = database.rawQuery("SELECT * FROM POI WHERE building LIKE '%" + locationArray[0] + "%' AND floor LIKE '%" + locationArray[1] + "%' AND room LIKE '%" + locationArray[2] + "%'", null);
                    }
                    if (poiCursor.moveToFirst()) {
                        String poiID = poiCursor.getString(poiCursor.getColumnIndex(ID));
                        DBHelper.insertEventPoi(database, eventID + "_" + maxInstances, poiID);
                        DBHelper.insertEvent(database, summary, htmlLink, finalStartDate, finalEndDate, eventID + "_" + maxInstances, checked);
                        DBHelper.insertEventType(database, summary, description, creator_name, creator_email);
                        ++eventsInserted;
                    }
                    poiCursor.close();
                }
            }
        }
        return eventsInserted;
    }
}