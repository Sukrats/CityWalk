package tuc.christos.chaniacitywalk2.model;

import android.location.Location;

/**
 * Created by Christos on 24/1/2017.
 */

public class Poi {
    private int id;
    private double latitude;
    private double longitude;
    private boolean visited;
    private boolean visible;
    private boolean unlocked;
    private String briefDesc;
    private String title;
    private String TAG;

    public Poi() {
    }
    public Poi(double latitude, double longitude, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    public Poi(int id, double latitude, double longitude, Boolean visited, Boolean visible, Boolean unlocked, String briefDesc, String TAG) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
        this.visible = visible;
        this.unlocked = unlocked;
        this.briefDesc = briefDesc;
        this.TAG = TAG;
    }

    public Poi(double latitude, double longitude, Boolean visited, Boolean visible, Boolean unlocked, String briefDesc, String TAG) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.visited = visited;
        this.visible = visible;
        this.unlocked = unlocked;
        this.briefDesc = briefDesc;
        this.TAG = TAG;
    }




    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(Boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
