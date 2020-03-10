package com.example.go4lunch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RestaurantPOJO {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * No args constructor for use in serialization
     *
     */
    public RestaurantPOJO() {
    }

    /**
     *
     * @param htmlAttributions
     * @param results
     * @param status
     */
    public RestaurantPOJO(List<Object> htmlAttributions, List<Result> results, String status) {
        super();
        this.htmlAttributions = htmlAttributions;
        this.results = results;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public RestaurantPOJO withHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
        return this;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public RestaurantPOJO withResults(List<Result> results) {
        this.results = results;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RestaurantPOJO withStatus(String status) {
        this.status = status;
        return this;
    }


    public class Geometry {

        @SerializedName("location")
        @Expose
        private Location location;
        @SerializedName("viewport")
        @Expose
        private Viewport viewport;

        /**
         * No args constructor for use in serialization
         *
         */
        public Geometry() { }

        /**
         *
         * @param viewport
         * @param location
         */
        public Geometry(Location location, Viewport viewport) {
            super();
            this.location = location;
            this.viewport = viewport;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Geometry withLocation(Location location) {
            this.location = location;
            return this;
        }

        public Viewport getViewport() {
            return viewport;
        }

        public void setViewport(Viewport viewport) {
            this.viewport = viewport;
        }

        public Geometry withViewport(Viewport viewport) {
            this.viewport = viewport;
            return this;
        }

    }

    public class Location {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        /**
         * No args constructor for use in serialization
         *
         */
        public Location() {
        }

        /**
         *
         * @param lng
         * @param lat
         */
        public Location(Double lat, Double lng) {
            super();
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Location withLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public Location withLng(Double lng) {
            this.lng = lng;
            return this;
        }

    }

    public class Northeast {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        /**
         * No args constructor for use in serialization
         *
         */
        public Northeast() {
        }

        /**
         *
         * @param lng
         * @param lat
         */
        public Northeast(Double lat, Double lng) {
            super();
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Northeast withLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public Northeast withLng(Double lng) {
            this.lng = lng;
            return this;
        }

    }

    public class OpeningHours {

        @SerializedName("open_now")
        @Expose
        private Boolean openNow;

        /**
         * No args constructor for use in serialization
         *
         */
        public OpeningHours() {
        }

        /**
         *
         * @param openNow
         */
        public OpeningHours(Boolean openNow) {
            super();
            this.openNow = openNow;
        }

        public Boolean getOpenNow() {
            return openNow;
        }

        public void setOpenNow(Boolean openNow) {
            this.openNow = openNow;
        }

        public OpeningHours withOpenNow(Boolean openNow) {
            this.openNow = openNow;
            return this;
        }

    }


    public class Photo {

        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("html_attributions")
        @Expose
        private List<String> htmlAttributions = null;
        @SerializedName("photo_reference")
        @Expose
        private String photoReference;
        @SerializedName("width")
        @Expose
        private Integer width;

        /**
         * No args constructor for use in serialization
         *
         */
        public Photo() {
        }

        /**
         *
         * @param htmlAttributions
         * @param photoReference
         * @param width
         * @param height
         */
        public Photo(Integer height, List<String> htmlAttributions, String photoReference, Integer width) {
            super();
            this.height = height;
            this.htmlAttributions = htmlAttributions;
            this.photoReference = photoReference;
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Photo withHeight(Integer height) {
            this.height = height;
            return this;
        }

        public List<String> getHtmlAttributions() {
            return htmlAttributions;
        }

        public void setHtmlAttributions(List<String> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
        }

        public Photo withHtmlAttributions(List<String> htmlAttributions) {
            this.htmlAttributions = htmlAttributions;
            return this;
        }

        public String getPhotoReference() {
            return photoReference;
        }

        public void setPhotoReference(String photoReference) {
            this.photoReference = photoReference;
        }

        public Photo withPhotoReference(String photoReference) {
            this.photoReference = photoReference;
            return this;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Photo withWidth(Integer width) {
            this.width = width;
            return this;
        }

    }

    public class PlusCode {

        @SerializedName("compound_code")
        @Expose
        private String compoundCode;
        @SerializedName("global_code")
        @Expose
        private String globalCode;

        /**
         * No args constructor for use in serialization
         *
         */
        public PlusCode() {
        }

        /**
         *
         * @param globalCode
         * @param compoundCode
         */
        public PlusCode(String compoundCode, String globalCode) {
            super();
            this.compoundCode = compoundCode;
            this.globalCode = globalCode;
        }

        public String getCompoundCode() {
            return compoundCode;
        }

        public void setCompoundCode(String compoundCode) {
            this.compoundCode = compoundCode;
        }

        public PlusCode withCompoundCode(String compoundCode) {
            this.compoundCode = compoundCode;
            return this;
        }

        public String getGlobalCode() {
            return globalCode;
        }

        public void setGlobalCode(String globalCode) {
            this.globalCode = globalCode;
        }

        public PlusCode withGlobalCode(String globalCode) {
            this.globalCode = globalCode;
            return this;
        }

    }

    public class Result {

        @SerializedName("geometry")
        @Expose
        private Geometry geometry;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("opening_hours")
        @Expose
        private OpeningHours openingHours;
        @SerializedName("photos")
        @Expose
        private List<Photo> photos = null;
        @SerializedName("place_id")
        @Expose
        private String placeId;
        @SerializedName("plus_code")
        @Expose
        private PlusCode plusCode;
        @SerializedName("price_level")
        @Expose
        private Integer priceLevel;
        @SerializedName("rating")
        @Expose
        private Double rating;
        @SerializedName("reference")
        @Expose
        private String reference;
        @SerializedName("scope")
        @Expose
        private String scope;
        @SerializedName("types")
        @Expose
        private List<String> types = null;
        @SerializedName("user_ratings_total")
        @Expose
        private Integer userRatingsTotal;
        @SerializedName("vicinity")
        @Expose
        private String vicinity;

        /**
         * No args constructor for use in serialization
         *
         */
        public Result() {
        }

        /**
         *
         * @param types
         * @param plusCode
         * @param icon
         * @param placeId
         * @param rating
         * @param userRatingsTotal
         * @param priceLevel
         * @param photos
         * @param reference
         * @param scope
         * @param name
         * @param geometry
         * @param openingHours
         * @param vicinity
         * @param id
         */
        public Result(Geometry geometry, String icon, String id, String name, OpeningHours openingHours, List<Photo> photos, String placeId, PlusCode plusCode, Integer priceLevel, Double rating, String reference, String scope, List<String> types, Integer userRatingsTotal, String vicinity) {
            super();
            this.geometry = geometry;
            this.icon = icon;
            this.id = id;
            this.name = name;
            this.openingHours = openingHours;
            this.photos = photos;
            this.placeId = placeId;
            this.plusCode = plusCode;
            this.priceLevel = priceLevel;
            this.rating = rating;
            this.reference = reference;
            this.scope = scope;
            this.types = types;
            this.userRatingsTotal = userRatingsTotal;
            this.vicinity = vicinity;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        public void setGeometry(Geometry geometry) {
            this.geometry = geometry;
        }

        public Result withGeometry(Geometry geometry) {
            this.geometry = geometry;
            return this;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Result withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Result withId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Result withName(String name) {
            this.name = name;
            return this;
        }

        public OpeningHours getOpeningHours() {
            return openingHours;
        }

        public void setOpeningHours(OpeningHours openingHours) {
            this.openingHours = openingHours;
        }

        public Result withOpeningHours(OpeningHours openingHours) {
            this.openingHours = openingHours;
            return this;
        }

        public List<Photo> getPhotos() {
            return photos;
        }

        public void setPhotos(List<Photo> photos) {
            this.photos = photos;
        }

        public Result withPhotos(List<Photo> photos) {
            this.photos = photos;
            return this;
        }

        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        public Result withPlaceId(String placeId) {
            this.placeId = placeId;
            return this;
        }

        public PlusCode getPlusCode() {
            return plusCode;
        }

        public void setPlusCode(PlusCode plusCode) {
            this.plusCode = plusCode;
        }

        public Result withPlusCode(PlusCode plusCode) {
            this.plusCode = plusCode;
            return this;
        }

        public Integer getPriceLevel() {
            return priceLevel;
        }

        public void setPriceLevel(Integer priceLevel) {
            this.priceLevel = priceLevel;
        }

        public Result withPriceLevel(Integer priceLevel) {
            this.priceLevel = priceLevel;
            return this;
        }

        public Double getRating() {
            return rating;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public Result withRating(Double rating) {
            this.rating = rating;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public Result withReference(String reference) {
            this.reference = reference;
            return this;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public Result withScope(String scope) {
            this.scope = scope;
            return this;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public Result withTypes(List<String> types) {
            this.types = types;
            return this;
        }

        public Integer getUserRatingsTotal() {
            return userRatingsTotal;
        }

        public void setUserRatingsTotal(Integer userRatingsTotal) {
            this.userRatingsTotal = userRatingsTotal;
        }

        public Result withUserRatingsTotal(Integer userRatingsTotal) {
            this.userRatingsTotal = userRatingsTotal;
            return this;
        }

        public String getVicinity() {
            return vicinity;
        }

        public void setVicinity(String vicinity) {
            this.vicinity = vicinity;
        }

        public Result withVicinity(String vicinity) {
            this.vicinity = vicinity;
            return this;
        }

    }

    public class Southwest {

        @SerializedName("lat")
        @Expose
        private Double lat;
        @SerializedName("lng")
        @Expose
        private Double lng;

        /**
         * No args constructor for use in serialization
         *
         */
        public Southwest() {
        }

        /**
         *
         * @param lng
         * @param lat
         */
        public Southwest(Double lat, Double lng) {
            super();
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Southwest withLat(Double lat) {
            this.lat = lat;
            return this;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public Southwest withLng(Double lng) {
            this.lng = lng;
            return this;
        }

    }

    public class Viewport {

        @SerializedName("northeast")
        @Expose
        private Northeast northeast;
        @SerializedName("southwest")
        @Expose
        private Southwest southwest;

        /**
         * No args constructor for use in serialization
         *
         */
        public Viewport() {
        }

        /**
         *
         * @param southwest
         * @param northeast
         */
        public Viewport(Northeast northeast, Southwest southwest) {
            super();
            this.northeast = northeast;
            this.southwest = southwest;
        }

        public Northeast getNortheast() {
            return northeast;
        }

        public void setNortheast(Northeast northeast) {
            this.northeast = northeast;
        }

        public Viewport withNortheast(Northeast northeast) {
            this.northeast = northeast;
            return this;
        }

        public Southwest getSouthwest() {
            return southwest;
        }

        public void setSouthwest(Southwest southwest) {
            this.southwest = southwest;
        }

        public Viewport withSouthwest(Southwest southwest) {
            this.southwest = southwest;
            return this;
        }

    }
}
