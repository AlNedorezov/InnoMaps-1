package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingAuxiliaryCoordinate;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

/**
 * Created by alnedorezov on 7/20/16.
 */
public class GetBuildingAuxiliaryCoordinatesCreatedOnOrAfterDateTask extends AsyncTask<String, Void, List<BuildingAuxiliaryCoordinate>> {
    @Override
    protected List<BuildingAuxiliaryCoordinate> doInBackground(String... params) {
        try {
            String urlString = getURL(params[0]);
            return deserializeCoordinate(urlString);
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage());
        }
        return Collections.emptyList();
    }

    private List<BuildingAuxiliaryCoordinate> deserializeCoordinate(String urlString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String response;
        try {
            response = NetworkController.establishGetConnection(urlString);
            if (response != null)
                response = response.substring(32, response.length() - 1);
            mapper.setDateFormat(Constants.serverDateFormat);
            return mapper.readValue(response,
                    TypeFactory.defaultInstance().constructCollectionType(List.class,
                            BuildingAuxiliaryCoordinate.class));
        } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return Collections.emptyList();
    }

    // parameter id
    protected String getURL(String date) {
        return Constants.CONNECTION_PROTOCOL + Constants.COLON_AND_TWO_SLASHES + Constants.IP + Constants.COLON +
                Constants.PORT + Constants.SLASH_RESOURCES_SLASH + Constants.BUILDING + Constants.AUXILIARY_COORDINATE + Constants.S_LOWERCASE +
                Constants.QUESTION_MARK + Constants.CREATED_AFTER_DATE + Constants.PARAMETER_EQUALS_CHAR + date;
    }
}