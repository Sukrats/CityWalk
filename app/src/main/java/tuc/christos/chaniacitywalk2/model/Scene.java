package tuc.christos.chaniacitywalk2.model;

/**
 * Created by Christos on 28-Jan-17.
 */

public class Scene {
    private int id;
    private double latitude;
    private double longitude;
    private boolean visited;
    private boolean visible;
    private boolean hasAR;
    private boolean unlocked;
    private String briefDesc;
    private String TAG;
    private String name;

    public boolean isHasAR() {
        return hasAR;
    }

    public void setHasAR(boolean hasAR) {
        this.hasAR = hasAR;
    }

    public String getTAG() {
        return TAG;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scene(){

    }

    public Scene( double latitude, double longitude, int id, String name) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public Scene( double latitude, double longitude, int id, String name, boolean visited, boolean visible, boolean hasAR, String description, String TAG) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.visited = visited;
        this.visible = visible;
        this.briefDesc = description;
        this.hasAR = hasAR;
        this.TAG = TAG;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getBriefDesc() {
        return briefDesc;
    }

    public void setBriefDesc(String briefDesc) {
        this.briefDesc = briefDesc;
    }

    public String getName() {
        return name;
    }
}
