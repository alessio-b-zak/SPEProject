package com.bitbusters.android.speproject;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cp153 on 06/12/2016.
 */

public class InputStreamToImageLocation {
    public List<ImageLocation> readImageLocStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List<ImageLocation> readMessagesArray(JsonReader reader) throws IOException {
        List<ImageLocation> messages = new ArrayList<ImageLocation>();
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                messages.add(readMessage(reader));
            }
            reader.endArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public ImageLocation readMessage(JsonReader reader) throws IOException {
        String id = null;
        ImageTag tag = ImageTag.NA;
        double latitude = 0.0, longitude = 0.0;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("_id")) {
                    id = reader.nextString();
                } else if (name.equals("tag")) {
                    tag = ImageTag.fromString(reader.nextString());
                } else if (name.equals("loc")) {
                    reader.beginArray();
                    longitude = reader.nextDouble();
                    latitude = reader.nextDouble();
                    reader.endArray();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ImageLocation(id, latitude, longitude,tag);
    }

}
