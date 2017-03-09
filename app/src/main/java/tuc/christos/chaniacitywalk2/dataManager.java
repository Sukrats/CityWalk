package tuc.christos.chaniacitywalk2;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import tuc.christos.chaniacitywalk2.model.Scene;
import tuc.christos.chaniacitywalk2.model.Poi;
import tuc.christos.chaniacitywalk2.model.PoiList;

/**
 * Created by Christos on 24/1/2017.
 */

public class dataManager {
    private static ArrayList<Poi> allPois = new ArrayList<>();
    private static ArrayList<Scene> Route = new ArrayList<>();
    private static ArrayList<PoiList> mPeriods = new ArrayList<>();
    private static ArrayList<Scene> Scenes = new ArrayList<>();

    private static HashMap<Scene,ArrayList<LatLng>> sceneToPointsMap = new HashMap<>();
    private static HashMap<ArrayList<LatLng>,Scene> pointsToSceneMap = new HashMap<>();

    static private ArrayList<ArrayList<LatLng>> polyList = new ArrayList<>();
    static private ArrayList<LatLng> polyListRocco = new ArrayList<>();
    static private ArrayList<LatLng> polyListByz = new ArrayList<>();
    static private ArrayList<LatLng> polyListKast = new ArrayList<>();
    static private ArrayList<LatLng> polyListGlass = new ArrayList<>();

    private static String TAG = "Data Manager";
    private static Context mContext;
    private static mDBHelper mDBh;

    private static dataManager dm = new dataManager();

    private static boolean instantiated = false;

    private dataManager( ){

    }

    static dataManager getInstance(Context context ) {
        if(instantiated)
            return dm;
        else{
            mDBh = new mDBHelper(context);
            mContext = context;
            initDBhelper();
            instantiate();
            return dm;
        }
    }

    private static void initDBhelper(){
        try {
            mDBh.createDataBase();
        }catch(IOException e){
            Log.i(TAG,"Unable to create database");
        }
        try{
            mDBh.openDataBase();
        }catch(SQLException e){
            Log.i(TAG,e.getMessage());
        }

    }

    private static void instantiate(){

                Cursor c = mDBh.getEntries();
                while (c.moveToNext()) {

                    boolean tvisit;
                    boolean tvisible;
                    boolean thasAR;

                    String TAG = c.getString(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_TAG));
                    String name = c.getString(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_NAME));
                    double lat = c.getDouble(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_LATITUDE));
                    double lon = c.getDouble(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_LONGITUDE));
                    int id = c.getInt(c.getColumnIndexOrThrow(mDBHelper.FeedEntry._ID));
                    int visited = c.getInt(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_VISITED));
                    String descr = c.getString(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_DESCRIPTION));
                    int visible = c.getInt(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_VISIBLE));
                    int hasAR = c.getInt(c.getColumnIndexOrThrow(mDBHelper.FeedEntry.TABLE_COLUMN_HASAR));

                    if (visited == 1)
                        tvisit = true;
                    else
                        tvisit = false;

                    if (visible == 1)
                        tvisible = true;
                    else
                        tvisible = false;

                    if (hasAR == 1)
                        thasAR = true;
                    else
                        thasAR = false;

                    Scene temp = new Scene(lat, lon, id, name, tvisit, tvisible, thasAR, descr, TAG);

                    Scenes.add(temp);
                    if(temp.isHasAR()){
                        Route.add(temp);
                    }
                }
                setPolyPoints();
                instantiated = true;
    }

    ArrayList<Scene> getScenes(){
                   return Scenes;
    }

    public ArrayList<Poi> getAllPois() {
        return allPois;
    }

    public void setAllPois(ArrayList<Poi> pois) {
        allPois = pois;
    }

    public ArrayList<Scene> getRoute() {
        return Route;
    }

    public void setRoute(ArrayList<Scene> route) {
        Route = route;
    }

    public ArrayList<PoiList> getPeriods() {
        return mPeriods;
    }

    public void setPeriods(ArrayList<PoiList> periods) {
        mPeriods = periods;
    }

    void setAllInvisible(){
        for(Scene temp: Scenes)
        {
            temp.setVisible(false);
        }
    }

    void setAllvisible(){
        for(Scene temp: Scenes)
        {
            temp.setVisible(true);
        }
    }

    void setAllvisited(){
        for(Scene temp: Scenes)
        {
            temp.setVisited(true);
        }
    }

    void unsetAllvisited(){
        for(Scene temp: Scenes)
        {
            temp.setVisited(false);
        }
    }

    void setRandVisited(){
        for(Scene temp: Scenes)
        {
            Random rd = new Random();
            if(rd.nextInt(50)%2==1)
                temp.setVisited(true);
        }
    }

    private void updateLocalDB(){
        mDBh.updateLocalDB(Scenes);
        Scenes.clear();
        instantiate();
    }

    ArrayList<LatLng> getPolyPoints(Scene scene){

        return sceneToPointsMap.get(scene);
    }

    private static void  setPolyPoints(){

        polyListRocco.add(new LatLng(35.5164899,24.021208));//stRocco
        polyListRocco.add(new LatLng(35.516470,24.021102));//stRocco
        polyListRocco.add(new LatLng(35.517112,24.020858));//stRocco
        polyListRocco.add(new LatLng(35.517092,24.020705));//stRocco

        polyListByz.add(new LatLng(35.51711,24.020557));//ByzWall
        polyListByz.add(new LatLng(35.517092,24.020705));//ByzWall
        polyListByz.add(new LatLng(35.517253,24.020791));//ByzWall
        polyListByz.add(new LatLng(35.517443,24.020788));//ByzWall
        polyListByz.add(new LatLng(35.517369,24.020397));//ByzWall
        polyListByz.add(new LatLng(35.517076,24.019614));//ByzWall

        polyListKast.add(new LatLng(35.5171461,24.019581));//kasteli
        polyListKast.add(new LatLng(35.517076,24.019614));//kasteli
        polyListKast.add(new LatLng(35.516653,24.018527));//kasteli
        polyListKast.add(new LatLng(35.516522,24.017910));//kasteli
        polyListKast.add(new LatLng(35.516729,24.017768));//kasteli
        polyListKast.add(new LatLng(35.517356,24.017637));//kasteli
        polyListKast.add(new LatLng(35.517398,24.01779));//Glass Mosque

        polyListGlass.add(new LatLng(35.517398,24.01779));//Glass Mosque
        polyListGlass.add(new LatLng(35.517398,24.01779));//Glass Mosque

        for(Scene temp: Route){
            if(temp.getId() == 1 ){
                sceneToPointsMap.put(temp,polyListGlass);
                pointsToSceneMap.put(polyListGlass,temp);
            }
            if(temp.getId() == 2 ){
                sceneToPointsMap.put(temp,polyListByz);
                pointsToSceneMap.put(polyListByz,temp);
            }
            if(temp.getId() == 3 ){
                sceneToPointsMap.put(temp,polyListKast);
                pointsToSceneMap.put(polyListKast,temp);
            }
            if(temp.getId() == 4 ){
                sceneToPointsMap.put(temp,polyListRocco);
                pointsToSceneMap.put(polyListRocco,temp);
            }
        }

    }
    public ArrayList<Poly> getRoutePoly(){
        ArrayList<Poly> polyList = new ArrayList<>();

        ArrayList<LatLng> polyListRocco = new ArrayList<>();
        ArrayList<LatLng> polyListByz = new ArrayList<>();
        ArrayList<LatLng> polyListKast = new ArrayList<>();
        ArrayList<LatLng> polyListGlass = new ArrayList<>();

        polyListRocco.add(new LatLng(35.5164899,24.021208));//stRocco
        polyListRocco.add(new LatLng(35.516470,24.021102));//stRocco
        polyListRocco.add(new LatLng(35.517112,24.020858));//stRocco
        polyListRocco.add(new LatLng(35.517092,24.020705));//stRocco

        polyListByz.add(new LatLng(35.51711,24.020557));//ByzWall
        polyListByz.add(new LatLng(35.517092,24.020705));//ByzWall
        polyListByz.add(new LatLng(35.517253,24.020791));//ByzWall
        polyListByz.add(new LatLng(35.517443,24.020788));//ByzWall
        polyListByz.add(new LatLng(35.517369,24.020397));//ByzWall
        polyListByz.add(new LatLng(35.517076,24.019614));//ByzWall

        polyListKast.add(new LatLng(35.5171461,24.019581));//kasteli
        polyListKast.add(new LatLng(35.517076,24.019614));//kasteli
        polyListKast.add(new LatLng(35.516653,24.018527));//kasteli
        polyListKast.add(new LatLng(35.516522,24.017910));//kasteli
        polyListKast.add(new LatLng(35.516729,24.017768));//kasteli
        polyListKast.add(new LatLng(35.517356,24.017637));//kasteli
        polyListKast.add(new LatLng(35.517398,24.01779));//Glass Mosque

        polyListGlass.add(new LatLng(35.517398,24.01779));//Glass Mosque

        for(Scene temp: Route){
            if(temp.getId() == 4 ){
                polyList.add(new Poly(polyListRocco,temp));
            }else if(temp.getId() == 2 ){
                polyList.add(new Poly(polyListByz,temp));
            }else if(temp.getId() == 3 ){
                polyList.add(new Poly(polyListKast,temp));
            }else if(temp.getId() == 1 ){
                polyList.add(new Poly(polyListGlass,temp));
            }
        }
        return polyList;
    }

    private class Poly {
        private ArrayList<LatLng> points;
        private Scene scene;
        Poly(ArrayList<LatLng> points, Scene scene){
            this.scene=scene;
            this.points = points;
        }

        public Scene getScene(){
            return this.scene;
        }
        public ArrayList<LatLng> getPoints(){
            return this.points;
        }
    }
}
