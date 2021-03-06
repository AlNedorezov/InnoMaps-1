package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EventCreatorAppointmentDAO implements Crud {

    private DatabaseHelper helper;

    public EventCreatorAppointmentDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;
        try {
            index = helper.getEventCreatorAppointmentDao().create(eventCreatorAppointment);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;

        try {
            helper.getEventCreatorAppointmentDao().update(eventCreatorAppointment);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;

        try {
            DeleteBuilder<EventCreatorAppointment, Integer> db = helper.getEventCreatorAppointmentDao().deleteBuilder();
            db.where().eq(Constants.EVENT_ID, eventCreatorAppointment.getEvent_id()).and().eq(Constants.EVENT_CREATOR_ID, eventCreatorAppointment.getEvent_creator_id());
            PreparedDelete<EventCreatorAppointment> preparedDelete = db.prepare();
            helper.getEventCreatorAppointmentDao().delete(preparedDelete);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int eventId, int eventCreatorId) {

        EventCreatorAppointment eventCreatorAppointment = null;
        try {
            QueryBuilder<EventCreatorAppointment, Integer> qb = helper.getEventCreatorAppointmentDao().queryBuilder();
            qb.where().eq(Constants.EVENT_ID, eventId).and().eq(Constants.EVENT_CREATOR_ID, eventCreatorId);
            PreparedQuery<EventCreatorAppointment> pc = qb.prepare();
            eventCreatorAppointment = helper.getEventCreatorAppointmentDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }
        return eventCreatorAppointment;
    }

    @Override
    public List<?> findAll() {

        List<EventCreatorAppointment> items = new ArrayList<>();

        try {
            items = helper.getEventCreatorAppointmentDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;
        try {
            QueryBuilder<EventCreatorAppointment, Integer> qb = helper.getEventCreatorAppointmentDao().queryBuilder();
            qb.where().eq(Constants.EVENT_ID, eventCreatorAppointment.getEvent_id()).and().eq(Constants.EVENT_CREATOR_ID, eventCreatorAppointment.getEvent_creator_id());
            PreparedQuery<EventCreatorAppointment> pc = qb.prepare();
            if (helper.getEventCreatorAppointmentDao().query(pc).size() > 0) {
                if (helper.getEventCreatorAppointmentDao().query(pc).get(0).equals(eventCreatorAppointment))
                    index = 0;
                else {
                    UpdateBuilder<EventCreatorAppointment, Integer> ub = helper.getEventCreatorAppointmentDao().updateBuilder();
                    Date newCreated = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(eventCreatorAppointment.getCreated());
                    ub.updateColumnValue(Constants.CREATED, newCreated);
                    PreparedUpdate<EventCreatorAppointment> preparedUpdate = ub.prepare();
                    index = helper.getEventCreatorAppointmentDao().update(preparedUpdate);
                }
            } else
                index = helper.getEventCreatorAppointmentDao().create(eventCreatorAppointment);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        } catch (ParseException e) {
            Log.e(Constants.DAO_ERROR, e.getMessage(), e.fillInStackTrace());
        }

        return index;
    }

    public List<EventCreatorAppointment> findByEventId(int eventId) {

        List<EventCreatorAppointment> eventCreatorAppointments = new ArrayList<>();

        try {
            QueryBuilder<EventCreatorAppointment, Integer> qb = helper.getEventCreatorAppointmentDao().queryBuilder();
            qb.where().eq(Constants.EVENT_ID, eventId);
            PreparedQuery<EventCreatorAppointment> pc = qb.prepare();
            eventCreatorAppointments = helper.getEventCreatorAppointmentDao().query(pc);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return eventCreatorAppointments;
    }
}

