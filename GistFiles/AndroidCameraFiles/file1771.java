package com.sincress.entitymap;


import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sincress.entitymap.framework.Entity;
import com.sincress.entitymap.framework.EntityCanvas;
import com.sincress.entitymap.framework.EntityCanvas.Connector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityManager {

    public static boolean eraserActive = false;
    private static float clickX, clickY;
    public static EntityCanvas entityCanvas;
    private static int editEntityXPos = -1, editEntityYPos = -1;
    public static View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        long starttime;
        float oldCameraX, oldCameraY, oldClickX, oldClickY;
        boolean entityIsSelected = false;
        Entity dragPoint1;

        /**
         * Handle entity selection and movement, view movement, scaling,
         * adding new entities, connecting entities and etc.
         * @param v
         * @param event
         * @return
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            float cameraX = entityCanvas.getCameraCoords().x;
            float cameraY = entityCanvas.getCameraCoords().y;
            ArrayList<Entity> entities = entityCanvas.getEntities();
            ArrayList<Connector> connectors = entityCanvas.getConnectors();
            // ********************************* ACTION DOWN *********************************************
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                clickX = event.getX();
                clickY = event.getY();
                // Used to check if the long touch is static (for adding entities)
                oldClickX = clickX;
                oldClickY = clickY;
                oldCameraX = cameraX;
                oldCameraY = cameraY;

                // If we started a drag, mark it here
                dragPoint1 = EntityManager.isInEntity(entities,
                        new Point((int) (clickX / entityCanvas.ZOOM_LEVEL + cameraX), (int) (clickY / entityCanvas.ZOOM_LEVEL + cameraY)));

                //Start timer
                starttime = System.currentTimeMillis();
                return true;
            }
            // ********************************* ACTION UP *********************************************
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Entity clickedEntity = isInEntity(entities,
                        new Point((int) (event.getX() / entityCanvas.ZOOM_LEVEL + cameraX), (int) (event.getY() / entityCanvas.ZOOM_LEVEL + cameraY)));
                if (eraserActive) {
                    entities.remove(clickedEntity);
                    for (int i = 0; i < connectors.size(); i++) {
                        Connector conn = connectors.get(i);
                        if (conn.start.equals(clickedEntity) || conn.end.equals(clickedEntity)) {
                            connectors.remove(conn);
                            i--;
                        }
                    }
                    entityCanvas.postInvalidate();
                    return true;
                }

                // Add new connector if the drag start entity isnt the same as drag end entity
                if (clickedEntity != null && dragPoint1 != null) {
                    if (!clickedEntity.equals(dragPoint1)) {
                        connectors.add(new Connector(dragPoint1, clickedEntity));
                        entityCanvas.postInvalidate();
                        return true;
                    } else { // Editing
                        if (System.currentTimeMillis() - starttime > 500) {
                            entityCanvas.getActivity().toggleFragment(entityCanvas, (DateEventEntity) clickedEntity);
                            editEntityXPos = clickedEntity.getPosition().x;
                            editEntityYPos = clickedEntity.getPosition().y;
                            for (int i = 0; i < connectors.size(); i++) {
                                Connector conn = connectors.get(i);
                                if (conn.start.equals(clickedEntity) || conn.end.equals(clickedEntity)) {
                                    connectors.remove(conn);
                                    i--;
                                }
                            }
                            entities.remove(clickedEntity);
                            return true;
                        }
                    }

                }

                // Toggle entity selection
                if (clickedEntity != null) {
                    entityIsSelected = false;
                    clickedEntity.setSelected(!clickedEntity.isSelected()); // Invert selection
                    for (Entity entity : entities)
                        if (entity.isSelected())
                            entityIsSelected = true;
                    entityCanvas.postInvalidate(); // Redraw
                } else {   // Reset all entities' "selected" value
                    for (Entity entity : entities)
                        entity.setSelected(false);
                    entityCanvas.postInvalidate();
                    entityIsSelected = false;
                }
                // Add a new entity if touch has been held for at least 500ms
                // and if the camera did not move and if no entity has been clicked
                if (!cameraMoved(oldCameraX, oldCameraY, cameraX, cameraY) && !entityIsSelected &&
                        !cameraMoved(oldClickX, oldClickY, event.getX(), event.getY())) {
                    // To prevent unwanted popups
                    if (System.currentTimeMillis() - starttime > 500) {
                        // Now add the entity - call the prompt fragment
                        entityCanvas.getActivity().toggleFragment(entityCanvas, null);
                    }
                }
            }
            // ********************************* ACTION MOVE *********************************************
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                // If one of the entities is selected, then MOVE entities, not canvas
                for (Entity entity : entities) {
                    if (entity.isSelected()) {
                        entityIsSelected = true;
                        entity.setPosition(new Point((int) (cameraX + event.getX() / entityCanvas.ZOOM_LEVEL),
                                (int) (cameraY + event.getY() / entityCanvas.ZOOM_LEVEL)));
                        entityCanvas.postInvalidate();
                    }
                }
                // If no entity is selected, we want to move the canvas
                if (!entityIsSelected) {
                    // If user has clicked on an entity he wants to drag a connector, so dont move the camera
                    if (dragPoint1 != null)
                        return true;
                    cameraX += (clickX - event.getX());
                    cameraY += (clickY - event.getY());
                    entityCanvas.setCameraCoords(cameraX, cameraY);
                    entityCanvas.postInvalidate();
                    clickX = event.getX();
                    clickY = event.getY();
                }
            }
            return false;
        }
    };

    /**
     * This method saves the entities and connectors to the save file with the name provided
     *
     * @param filename the name of the file to which we save
     */
    public static void saveEntities(String filename) {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //Save
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "HistoryMap");
            if (!file.mkdirs()) {
                Log.e("FILE ERROR", "Directory not created");
            }
            file = new File(file, filename);

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                Log.e("FILE IO", "Writing to file");

                // FileOutputStream fos = new FileOutputStream(file);
                // ObjectOutputStream oos = new ObjectOutputStream(fos);

                BufferedWriter br = new BufferedWriter(new FileWriter(file));


                String eString = null;
                for (Entity e : entityCanvas.getEntities()) {
                    DateEventEntity dee = (DateEventEntity) e;
                    eString += dee.date + "&" + dee.description + "&" + dee.getPosition().x + "&" + dee.getPosition().y + "&" + dee.cardColor;
                    br.write(eString + "\n");

                }

                br.write("");//empty


                String c = null;
                for (Connector con : entityCanvas.getConnectors()) {
                    DateEventEntity s = (DateEventEntity) con.start;
                    DateEventEntity e = (DateEventEntity) con.end;
                    c += s.date + "&" + s.description + "&" + s.getPosition().x + "&" + s.getPosition().y + "&" + s.cardColor;
                    c += e.date + "&" + e.description + "&" + e.getPosition().x + "&" + e.getPosition().y + "&" + e.cardColor;
                    br.write(c + "\n");
                }

                br.close();

                //fos.close(); tood
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads the entities and connectors from the file at the given filepath and initialises the
     * entities and connectors arrays with the data.
     *
     * @param filepath the full path to the file that is being loaded
     */
    public static void loadEntities(String filepath) throws IOException {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {


                ArrayList<Entity> entities = new ArrayList<>();
                ArrayList<Connector> connectors = new ArrayList<>();

                //FileInputStream fis = new FileInputStream(filepath); todo
                //ObjectInputStream ois = new ObjectInputStream(fis);todo
                BufferedReader br = new BufferedReader(new FileReader(filepath));

                Gson gsonEntity = new GsonBuilder().registerTypeAdapter(DateEventEntity.class, new InterfaceAdapter<Entity>()).create();

                String line;
                boolean k = false;
                while ((line = br.readLine()) != null) {

                    if (line.isEmpty()) {
                        k = true;
                    }

                    if (!k) {
                        String[] e = line.split("&");
                        DateEventEntity entity = new DateEventEntity(
                                e[0],
                                e[1],
                                Integer.parseInt(e[4]),
                                new Point(Integer.parseInt(e[2]), Integer.parseInt(e[3]))
                        );
                        entities.add(entity);

                    } else {
                        String[] c = line.split("&");
                        DateEventEntity start = new DateEventEntity(
                                c[0],
                                c[1],
                                Integer.parseInt(c[4]),
                                new Point(Integer.parseInt(c[2]), Integer.parseInt(c[3]))
                        );
                        DateEventEntity end = new DateEventEntity(
                                c[5],
                                c[6],
                                Integer.parseInt(c[9]),
                                new Point(Integer.parseInt(c[7]), Integer.parseInt(c[8]))
                        );
                        Connector connector = new Connector(start, end);
                        connectors.add(connector);

                        Log.d("SLON", "Stavljam " + entities.size() + " entiteta i " + connectors.size() + " konektora.");
                        entityCanvas.setEntities(entities, connectors);
                    }

                    br.close();
//                ois.close();todo
//                fis.close();todo
                }
            } catch (FileNotFoundException e) {
                Log.d("SLON", e.toString());
            }

        }
    }

    /**
     * Used by EntityCanvas to push its instance to the EntityManager. This is done upon creation
     * of the EntityCanvas and its instance reference is widely used in this class.
     *
     * @param instance
     */

    public static void setCanvasInstance(EntityCanvas instance) {
        EntityManager.entityCanvas = instance;
    }

    /**
     * Iterates through the given array of entities and returns true if the given
     * coordinate is inside one of the entity rectangles.
     *
     * @param array      Array of available entities
     * @param coordinate Coordinate to test
     * @return True if inside any entity, false otherwise
     */
    private static Entity isInEntity(ArrayList<Entity> array, Point coordinate) {
        float zoomLvl = entityCanvas.ZOOM_LEVEL;
        Point position, dimension;
        for (Entity entity : array) {
            position = entity.getPosition();
            dimension = entity.getEntityDimens();
            if (coordinate.x > position.x && coordinate.x < position.x + dimension.x * zoomLvl)
                if (coordinate.y > position.y && coordinate.y < position.y + dimension.y * zoomLvl)
                    return entity;
        }
        return null;
    }

    /**
     * Callback from fragment's OK button, handle the implementation here
     *
     * @param entityCanvas canvas which is related to the activity on which we do our drawing
     */
    public static void doFragmentAction(EntityCanvas entityCanvas) {
        EntityManager.entityCanvas = entityCanvas;
        View fragmentview = entityCanvas.getActivity().fragment.getView();
        EditText dateView = (EditText) fragmentview.findViewById(R.id.dateField);
        EditText descView = (EditText) fragmentview.findViewById(R.id.descField);
        String color = ((Spinner) fragmentview.findViewById(R.id.colorSpinner)).getSelectedItem().toString();

        int intColor = 0;
        switch (color) {
            case "Red":
                intColor = Color.argb(255, 231, 76, 60);
                break;
            case "Blue":
                intColor = Color.argb(255, 41, 128, 185);
                break;
            case "Yellow":
                intColor = Color.argb(255, 243, 156, 18);
                break;
            case "Green":
                intColor = Color.argb(255, 39, 174, 96);
                break;
            case "Magenta":
                intColor = Color.argb(255, 142, 68, 173);
                break;
            case "Gray":
                intColor = Color.argb(255, 127, 140, 141);
                break;
            case "Cyan":
                intColor = Color.argb(255, 26, 188, 156);
                break;
        }
        Entity entity = new DateEventEntity(dateView.getText().toString(),
                descView.getText().toString(),
                intColor,
                new Point((int) (clickX + entityCanvas.getCameraCoords().x),
                        (int) (clickY + entityCanvas.getCameraCoords().y)));
        entityCanvas.getEntities().add(entity);
        // Call postInvalidate to redraw the scene
        entityCanvas.postInvalidate();
        // Reset the fields
        dateView.setText("");
        dateView.setHint("Date");
        descView.setText("");
        descView.setHint("Description");

        if (editEntityXPos != -1 && editEntityYPos != -1) {
            entity.setPosition(new Point(editEntityXPos, editEntityYPos));
            editEntityYPos = editEntityXPos = -1;
        }
    }

    /**
     * Checks if the camera moved significantly to verify if the user is trying to long click in order to add
     * a new entity to the canvas.
     *
     * @param oldCameraX
     * @param oldCameraY
     * @param cameraX
     * @param cameraY
     * @return
     */
    private static boolean cameraMoved(float oldCameraX, float oldCameraY, float cameraX,
                                       float cameraY) {
        float tolerance = entityCanvas.getWidth() / 15;
        if (cameraX > oldCameraX - tolerance && cameraX < oldCameraX + tolerance &&
                cameraY > oldCameraY - tolerance && cameraY < oldCameraY + tolerance)
            return false;
        return true;
    }

    /**
     * Method that sorts the existing entities by year and arranges them by Y axis value.
     */
    public static void smartSort() {
        int elementsNo = entityCanvas.getEntities().size(), year = -1;
        HashMap<Integer, Entity> hmap = new HashMap<>();

        // Obtain first and last date (year)
        for (Entity entity : entityCanvas.getEntities()) {
            String date = ((DateEventEntity) entity).date;
            final Matcher m = Pattern.compile("(\\d{4})").matcher(date);
            try {
                if (m.find())
                    year = Integer.parseInt(m.group(0));
            } catch (NumberFormatException e) {
                year = -1;
            }
            if (year > 0) {
                hmap.put(year, entity);
            }
        }

        ArrayList<Integer> keys = new ArrayList<>(hmap.keySet());
        Collections.sort(keys);
        int ypos = -1 * (elementsNo / 2 * 90);
        for (Integer yr : keys) {
            Entity ent = hmap.get(yr);
            ent.setPosition(new Point(-1 * ent.getEntityDimens().x / 2, ypos));
            ypos += 90;
        }
    }

//    // Deserialize to single object.
//    public MyClass deserializeFromJson(String jsonString) {
//        Gson gson = new Gson();
//        MyClass myClass = gson.fromJson(jsonString, MyClass.class);
//        return myClass;
//    }
}
