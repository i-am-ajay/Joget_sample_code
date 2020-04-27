package org.joget.geowatch.api.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlaceResponse {

    @SerializedName("predictions")
    private List<Prediction> predictions;

    public List<Prediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }

    @Override
    public String toString() {
        return "GooglePlaceResponse{" +
                "predictions=" + predictions +
                '}';
    }

    public static class Prediction {

        @SerializedName("id")
        private String id;

        @SerializedName("place_id")
        private String placeId;

        @SerializedName("description")
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "Prediction{" +
                    "id='" + id + '\'' +
                    ", placeId='" + placeId + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }
}
