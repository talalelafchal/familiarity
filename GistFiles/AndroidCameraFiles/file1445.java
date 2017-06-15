//Subclasses of this should act as individual game states
//ex: MainMenuActivity, GameActivity, InventoryActivity
//Not to be confused with an Android Activity, because the EngineActivity simply switches between SubActivities

package com.github.daltonks.engine.activities;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.github.daltonks.engine.gl.EngineGLScene;
import com.github.daltonks.engine.util.Pools;
import com.github.daltonks.engine.util.SortedList;
import com.github.daltonks.engine.util.Util;
import com.github.daltonks.engine.util.Vec3d;
import com.github.daltonks.engine.util.interfaces.Drawable;
import com.github.daltonks.engine.world.Camera;
import com.github.daltonks.engine.world.EngineWorld;
import com.github.daltonks.engine.world.events.EventSystemContainer;
import com.github.daltonks.engine.world.ui.UIBoundsComponent;
import com.github.daltonks.engine.world.ui.UIEntity;

import java.util.ArrayList;

public abstract class SubActivity implements Drawable {
    private short id;
    private int currentEntityID = 0;
    private String name;
    private Camera uiCamera;
    private EventSystemContainer eventSystemContainer = new EventSystemContainer();
	private TouchEventHandler touchHandler = new TouchEventHandler();
    private EngineWorld engineWorld;

    private SortedList<UIEntity> uiEntities = new SortedList<>();
    private ArrayList<Integer> deletedEntityIDs = new ArrayList<>();

    public SubActivity() {
        uiCamera = new Camera(this);
        uiCamera.getUp().set(0, 1, 0);
        uiCamera.getTransformComponent().setLocation(getStartingUICameraOffset());
        uiCamera.updateViewMatrix();
    }

    public abstract void beforeModelGeneration();
    public abstract void onSurfaceCreated();
	public abstract void onTouchEvent(EngineTouchEvent event);
    public abstract boolean onBackPressed();
    public abstract void onActivityPause();
    protected abstract Vec3d getStartingUICameraOffset();

    public void update(double delta) {
		touchHandler.update();
		
        ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
        for(int i = 0; i < underlying.size(); i++) {
            underlying.get(i).update(this, delta);
        }
        engineWorld.update(delta);
    }

    public void draw(Camera camera) {
        Vec3d cameraLoc = camera.getTransformComponent().getLocation();
        Vec3d sunShaderLocInView = cameraLoc.clone().mult(-1).multMatrix(camera.getViewMatrix());

        GLES20.glUniform3f(
                EngineGLScene.LIGHT_POSITION_IN_VIEW_3F_UNIFORM,
                (float) sunShaderLocInView.getX(),
                (float) sunShaderLocInView.getY(),
                (float) sunShaderLocInView.getZ()
        );
        Pools.recycle(sunShaderLocInView);

        engineWorld.draw(camera);

        GLES20.glUniform3f(EngineGLScene.LIGHT_POSITION_IN_VIEW_3F_UNIFORM, 0, 0, 4000);

        ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
        for(int i = 0; i < underlying.size(); i++) {
            underlying.get(i).draw(uiCamera);
        }
    }

    public void onTouchEvent(MotionEvent e) {
        int index = e.getActionIndex();
        int id = e.getPointerId(index);
        int pointerCount = e.getPointerCount();
        float x = 0, y = 0;
        FingerTracker currentTracker = getFingerTracker(id);

        if(currentTracker != null) {
            x = e.getX(index);
            y = e.getY(index);
        }

        switch(e.getActionMasked()) {
            case MotionEvent.ACTION_MOVE: {
                if(touchHandler.fingerTrackers.isEmpty()) {
                    return;
                }
				
				EngineTouchEvent touchEvent = Pools.get(EngineTouchEvent.class);
				touchEvent.setMotionType(MotionType.MOVE);
                for(int i = 0; i < pointerCount; i++) {
                    FingerTracker t = getFingerTracker(e.getPointerId(i));
                    if(t == null)
                        continue;
                    t.x = e.getX(i);
                    t.y = e.getY(i);
                    t.previousX = t.prevX;
                    t.previousY = t.prevY;
					t.prevX = t.x;
                    t.prevY = t.y;
					touchEvent.fingers.add(t.clone());
					/*
                    boolean captured = false;
                    if(t.focusedUIEntity != null) {
                        synchronized(EngineGLScene.LOOP_LOCK) {
                            captured = t.focusedUIEntity.getUIBoundsComponent().onSwipe(this, t.x - t.previousX, t.y - t.previousY, Util.toOpenGLX(t.x), Util.toOpenGLY(t.y));
                        }
                    }*/
                }
				
				touchHandler.addEvent(touchEvent);
                return;
            }

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                FingerTracker newTracker = Pools.get(FingerTracker.class);
                newTracker.id = id;
                touchHandler.fingerTrackers.add(newTracker);
                currentTracker = newTracker;
                x = e.getX(index);
                y = e.getY(index);
				
				EngineTouchEvent touchEvent = Pools.get(EngineTouchEvent.class);
				touchEvent.setMotionType(MotionType.DOWN);
				touchEvent.fingers.add(newTracker.clone());
				touchHandler.addEvent(touchEvent);
                /*synchronized(EngineGLScene.LOOP_LOCK) {
					
                    Vec3d worldLoc = getUICamera().screenToWorldZPlaneNew(Util.toOpenGLX(x), Util.toOpenGLY(y));
                    ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
                    for(int i = 0; i < underlying.size(); i++) {
                        UIEntity entity = underlying.get(i);
                        UIBoundsComponent uiBounds = entity.getUIBoundsComponent();
                        if(uiBounds != null && uiBounds.isInBounds(worldLoc)) {
                            newTracker.focusedUIEntity = entity;
                            uiBounds.onDown(this, Util.toOpenGLX(x), Util.toOpenGLY(y));
                            break;
                        }
                    }
                    Pools.recycle(worldLoc);*/
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
                if(currentTracker == null) {
                    return;
                }
				
				EngineTouchEvent touchEvent = Pools.get(EngineTouchEvent.class);
				touchEvent.setMotionType(MotionType.UP);
				touchEvent.fingers.add(currentTracker.clone());
				touchHandler.addEvent(touchEvent);
				
                touchHandler.fingerTrackers.remove(currentTracker);
				Pools.recycle(currentTracker);

				/*
                synchronized(EngineGLScene.LOOP_LOCK) {
                    if(currentTracker.focusedUIEntity != null && currentTracker.focusedUIEntity.getUIBoundsComponent() != null) {
                        float openGLX = Util.toOpenGLX(x);
                        float openGLY = Util.toOpenGLY(y);
                        if(currentTracker.focusedUIEntity.getUIBoundsComponent().isInBounds(openGLX, openGLY)) {
                            currentTracker.focusedUIEntity.getUIBoundsComponent().onClick(this, openGLX, openGLY);
                        }
                    }
                }*/

                currentTracker.focusedUIEntity = null;
                break;
            }
        }

        currentTracker.prevX = x;
        currentTracker.prevY = y;
    }

    public void onNewSurfaceDimensions(int width, int height) {
        uiCamera.updateProjectionMatrix(width, height);
        ArrayList<UIEntity> underlying = uiEntities.getUnderlyingList();
        for(UIEntity uiEntity : underlying) {
            uiEntity.onSurfaceChanged(this, width, height);
        }
        if(engineWorld != null) {
            engineWorld.onNewSurfaceDimensions(width, height);
        }
    }

    private FingerTracker getFingerTracker(int id) {
        for(int i = 0; i < fingerTrackers.size(); i++) {
            FingerTracker tracker = fingerTrackers.get(i);
            if(tracker.id == id) {
                return tracker;
            }
        }
        return null;
    }

    public void onEnterSubActivity() {
		touchHandler.recycleLists();
		
    }

    public void onLeaveSubActivity() {

    }

    public void addUIEntity(UIEntity ent) {
        uiEntities.add(ent);
    }

    public void removeUIEntity(UIEntity ent) {
        uiEntities.remove(ent);
        recycleEntityID(ent.getID());
    }

    public int generateEntityID() {
        int id;
        if(deletedEntityIDs.isEmpty()) {
            id = currentEntityID;
            currentEntityID++;
        } else {
            id = deletedEntityIDs.remove(deletedEntityIDs.size() - 1);
        }
        return id;
    }

    public void recycleEntityID(int id) {
        deletedEntityIDs.add(id);
    }

    public void setEngineWorld(EngineWorld engineWorld) {
        this.engineWorld = engineWorld;
    }

    public EngineWorld getEngineWorld() {
        return engineWorld;
    }

    public void setID(short id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public short getID() {
        return id;
    }

    public Camera getUICamera() {
        return uiCamera;
    }

    public EventSystemContainer getEventSystemContainer() {
        return eventSystemContainer;
    }
	
	private class TouchEventHandler {
		private ArrayList<FingerTracker> fingerTrackers = new ArrayList<>(5);
		private LinkedList<EngineTouchEvent> touchEvents = new LinkedList<>();
		
		private void update() {
			while(!touchEvents.isEmpty()) {
				EngineTouchEvent touchEvent = touchHandler.pop();
				switch(touchEvent.motionType) {
					case DOWN: {
						
						break;
					}
				}
				onTouchEvent(touchEvent);
				touchEvent.recycle();
			}
		}
		
		private void addEvent(EngineTouchEvent event) {
			synchronized(EngineGLScene.LOOP_LOCK) {
				touchEvents.add(touchEvent);
			}
		}
		
		private recycleLists() {
			for(FingerTracker tracker : fingerTrackers)
				Pools.recycle(tracker);
			fingerTrackers.clear();
			
			for(EngineTouchEvent touchEvent : touchEvents)
				touchEvent.recycle();
			touchEvents.clear();
		}
	}
	
	public static class EngineTouchEvent {
		private MotionType motionType;
		private LinkedList<FingerTracker> fingers = new LinkedList();
		
		private void recycle() {
			while(!fingers.isEmpty())
				Pools.recycle(fingers.pop());
			Pools.recycle(this);
		}
		
		public void setMotionType(MotionType type) {
			this.motionType = type;
		}
		
		public MotionType getMotionType() {
			return motionType;
		}
	}

    public static class FingerTracker {
        public int id;
        private float prevX, prevY;
        public float previousX, previousY;
        public float x, y;
        public UIEntity focusedUIEntity;
		
		private FingerTracker clone() {
			FingerTracker newTracker = Pools.get(FingerTracker.class);
			newTracker.id = id;
			newTracker.x = x;
			newTracker.y = y;
			newTracker.focusedUIEntity = focusedUIEntity;
			return newTracker;
		}
    }
	
	public enum MotionType {
		DOWN, MOVE, UP
	}
}