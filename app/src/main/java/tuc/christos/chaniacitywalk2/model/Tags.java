package tuc.christos.chaniacitywalk2.model;

import java.util.ArrayList;

/**
 * Created by Christos on 24/1/2017.
 */

public class Tags {
    private ArrayList<String> tags = new ArrayList<String>();

    public Tags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
