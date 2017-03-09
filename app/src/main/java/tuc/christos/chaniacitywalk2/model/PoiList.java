package tuc.christos.chaniacitywalk2.model;

import java.util.ArrayList;

/**
 * Created by Christos on 24/1/2017.
 */

public class PoiList {
    private int id;
    private String name;
    private boolean visible;
    private String tag;
    private ArrayList<Poi> Pois;

    public PoiList(int id, String name, Boolean visible, String tag, ArrayList<Poi> pois) {
        this.id = id;
        this.name = name;
        this.visible = visible;
        this.tag = tag;
        this.Pois = pois;
    }
}
