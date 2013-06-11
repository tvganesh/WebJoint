package com.tvganesh.webjoint;
/*
 * Web Joint
 *  Designed and developed by Tinniam V Ganesh, 11 Jun 2013
 *  Uses AndEngine 
 *  Uses Box2D physics with Distance Joints
 */

import java.util.ArrayList;
import java.util.Iterator;
import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;


import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;


public class WebJoint extends SimpleBaseGameActivity implements IAccelerationListener, IOnAreaTouchListener {

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	public static final float PIXEL_TO_METER_RATIO_DEFAULT = 32.0f;
	
	private BitmapTextureAtlas mBitmapTextureAtlas;
	private TiledTextureRegion mCircleFaceTextureRegion;
    private Scene mScene;
    
    private PhysicsWorld mPhysicsWorld;
   
    
	private TextureRegion mBallTextureRegion;
	Line connectionLine;
	Line connectionLine1;
	Line connectionLine2;
	Line connectionLine3;
	Line connectionLine4;
	Line connectionLine5;
	Line connectionLine6;
	Line connectionLine7;
	Line connectionLine8;
	
	float lineWidth = 5.0f;
	
	AnimatedSprite face1,face2,face3,face4;
	Body body1,body2,body3,body4;
	final FixtureDef gameFixtureDef = PhysicsFactory.createFixtureDef(1f, 0.0f, 0.1f);
	
	Sprite circle1;
	Sprite circle2;
	Sprite circle3;
	Sprite circle4;
	Body circleBody1;
	Body circleBody2;
	Body circleBody3;
	Body circleBody4;
	
	private Font mFont;
    static Text bText;
    
	public EngineOptions onCreateEngineOptions() {
		
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}
	
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");	
		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 74, 42, TextureOptions.BILINEAR);		
		
		this.mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "ball.png", 0, 0);
		
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 10, 10, 2, 1); // 64x32
		
		this.mBitmapTextureAtlas.load();
		
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL), 30);
		this.mFont.load();
		
		this.enableAccelerationSensor(this);
	

	}
	
	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_JUPITER), false);	
		// Create a Web 
		this.initWeb(mScene);
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.setOnAreaTouchListener(this);

		return mScene;		
		
	}
	
	public void initWeb(Scene mScene){
		
	
		
		int p1[] = {130,100};
		int p2[] = {130,300};
		int p3[] = {470,300};
		int p4[] = {470,100};
		
		int q1[] = {50,20};
		int q2[] = {50,380};
		int q3[] = {550,380};
		int q4[] = {550,30};
		
		 
		//Create the floor,ceiling and walls
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.0f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mScene.attachChild(ground);
		this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		
		// Create Animated Sprites
		face1 = new AnimatedSprite(p1[0], p1[1], this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
		body1 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face1, BodyType.DynamicBody, gameFixtureDef);
		
		face2 = new AnimatedSprite(p2[0], p2[1], this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
		body2 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face2, BodyType.DynamicBody, gameFixtureDef);
		
		face3 = new AnimatedSprite(p3[0], p3[1], this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
		body3 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face3, BodyType.DynamicBody, gameFixtureDef);
		
		face4 = new AnimatedSprite(p4[0], p4[1], this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
		body4 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face4, BodyType.DynamicBody, gameFixtureDef);

		// Create lines
		connectionLine1 = new Line(p1[0],p1[1],p2[0],p2[1],lineWidth,this.getVertexBufferObjectManager());
		connectionLine2 = new Line(p2[0],p2[1],p3[0],p3[1],lineWidth,this.getVertexBufferObjectManager());
		connectionLine3 = new Line(p3[0],p3[1],p4[0],p4[1],lineWidth,this.getVertexBufferObjectManager());
		connectionLine4 = new Line(p4[0],p4[1],p1[0],p1[1],lineWidth,this.getVertexBufferObjectManager());

		
		bText = new Text(200, 30, this.mFont, "Web Joint", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		this.mScene.attachChild(bText);
		final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(5f, 0.0f, 0.1f);
		
		//Create static bodies
		circle1 = new Sprite(q1[0], q1[1], this.mBallTextureRegion, this.getVertexBufferObjectManager());
		circleBody1 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, circle1, BodyType.StaticBody, FIXTURE_DEF);

		circle2 = new Sprite(q2[0], q2[1], this.mBallTextureRegion, this.getVertexBufferObjectManager());
		circleBody2 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, circle2, BodyType.StaticBody, FIXTURE_DEF);

		circle3 = new Sprite(q3[0], q3[1], this.mBallTextureRegion, this.getVertexBufferObjectManager());
		circleBody3 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, circle3, BodyType.StaticBody, FIXTURE_DEF);

		circle4 = new Sprite(q4[0], q4[1], this.mBallTextureRegion, this.getVertexBufferObjectManager());
		circleBody4 = PhysicsFactory.createCircleBody(this.mPhysicsWorld, circle4, BodyType.StaticBody, FIXTURE_DEF);
		
		connectionLine5 = new Line(q1[0],q1[1],p1[0],p1[1],lineWidth,this.getVertexBufferObjectManager());
	    connectionLine6 = new Line(q2[0],q2[1],p2[0],p2[1],lineWidth,this.getVertexBufferObjectManager());
	    connectionLine7 = new Line(q3[0],q3[1],p3[0],p3[1],lineWidth,this.getVertexBufferObjectManager());
	    connectionLine8 = new Line(q4[0],q4[1],p4[0],p4[1],lineWidth,this.getVertexBufferObjectManager());
		
	    //Add Animated Sprites
		addFace(p1,face1,body1);
		addFace(p2,face2,body2);
		addFace(p3,face3,body3);
		addFace(p4,face4,body4);
	
		// Connect the Animated Sprites
	    addConnectionLine(connectionLine1,face1,body1,face2);
	    addConnectionLine(connectionLine2,face2,body2,face3);
	    addConnectionLine(connectionLine3,face3,body3,face4);
	    addConnectionLine(connectionLine4,face4,body4,face1);
	   
	    // Add distance joints between Animated Sprites
	    addJoint(p1,p2,body1,body2);
	    addJoint(p2,p3,body2,body3);
	    addJoint(p3,p4,body3,body4);
	    addJoint(p4,p1,body4,body1);

	    //Add static points
	    addStaticPoint(q1,circle1,circleBody1);
	    addStaticPoint(q2,circle2,circleBody2);
	    addStaticPoint(q3,circle3,circleBody3);
	    addStaticPoint(q4,circle4,circleBody4);;
	     
	    // Add connecting lines from static points to Animated Sprites
	    addConnector(connectionLine5,circle1,circleBody1,face1);
	    addConnector(connectionLine6,circle2,circleBody2,face2);
	    addConnector(connectionLine7,circle3,circleBody3,face3);
	    addConnector(connectionLine8,circle4,circleBody4,face4);
	
		//Add distance joints from static point to face 
	    addJoint(q1,p1,circleBody1,body1);
	    addJoint(q2,p2,circleBody2,body2);
	    addJoint(q3,p3,circleBody3,body3);
	    addJoint(q4,p4,circleBody4,body4);
                     
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		
	}

	// Method to add an animated sprite
	public void addFace(int[] v, AnimatedSprite face,Body body){
		// Add face1
		
		face.animate(200);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));			
		this.mScene.attachChild(face);
		this.mScene.registerTouchArea(face);
		
		
	}
	
	// Method to connect sprites and update in real time
	public void addConnectionLine(final Line connectionLine,final AnimatedSprite faceA,Body bodyA,  final AnimatedSprite faceB){
		 // Add Line1
	     
	     connectionLine.setColor(0.0f,0.0f,1.0f);
	     this.mScene.attachChild(connectionLine);	
	     //Update connection line so that the line moves along with the body
	     	
	    	 this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(faceA, bodyA, true, true) {
			  @Override
				public void onUpdate(final float pSecondsElapsed) {
					super.onUpdate(pSecondsElapsed);
					if(connectionLine != null) 			
						connectionLine.setPosition(faceA.getX(),faceA.getY(),faceB.getX(),faceB.getY());
		        }
	    	 }
	      );	
	    
	}
	
	// Method to add distance joints
	public void addJoint(int[] p1, int[] p2,Body body1,Body body2){
		   DistanceJointDef distanceJoint = new DistanceJointDef();
		
		   Vector2 v1 = new Vector2(p1[0]/PIXEL_TO_METER_RATIO_DEFAULT,p1[1]/PIXEL_TO_METER_RATIO_DEFAULT);
		   Vector2 v2 = new Vector2(p2[0]/PIXEL_TO_METER_RATIO_DEFAULT,p2[1]/PIXEL_TO_METER_RATIO_DEFAULT);
		   distanceJoint.initialize(body1, body2, v1, v2);		   
		   distanceJoint.collideConnected = true;
		   distanceJoint.dampingRatio = 1.0f;
		   distanceJoint.frequencyHz = 10.0f;
		   this.mPhysicsWorld.createJoint(distanceJoint);
	}

	public void addStaticPoint(int[] q, Sprite circle, Body circleBody){
	     
		this.mScene.attachChild(circle);
		
	}
	
	// Method to add connecting lines between static points and animated sprites
	public void addConnector(final Line connectionLine,final Sprite faceX,Body bodyX, final AnimatedSprite faceY){
		 // Add Line1
	     
	     connectionLine.setColor(0.0f,0.0f,1.0f);
	     this.mScene.attachChild(connectionLine);	
	     //Update connection line so that the line moves along with the body
	     	
	    	 this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(faceX, bodyX, true, true) {
			  @Override
				public void onUpdate(final float pSecondsElapsed) {
					super.onUpdate(pSecondsElapsed);
					if(connectionLine != null) 			
						connectionLine.setPosition(faceX.getX(),faceX.getY(),faceY.getX(),faceY.getY());
		        }
	    	 }
	      );		
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
		
	}


	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);

	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,ITouchArea pTouchArea, float pTouchAreaLocalX,	float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			this.removeFace((AnimatedSprite)pTouchArea);
			return true;
		}

		return false;
	}

	private void removeFace(AnimatedSprite face) {
		if(face == null) {
			return;
		}
		final EngineLock engineLock = this.mEngine.getEngineLock();
		engineLock.lock();
		
		//Check which Animated Sprite is touched. Remove all lines connecting to it.
		if(face.equals(face1)){
			Log.d("Yes","Yes");
			destroyLine(connectionLine1);
			destroyLine(connectionLine4);
			destroyLine(connectionLine5);
			
			
		}
		if(face.equals(face2)){
			Log.d("Yes2","Yes2");
			destroyLine(connectionLine1);
			destroyLine(connectionLine2);
			destroyLine(connectionLine6);
			
			
		}
		if(face.equals(face3)){
			Log.d("Yes3","Yes3");
			destroyLine(connectionLine3);
			destroyLine(connectionLine2);
			destroyLine(connectionLine7);
			
			
		}
		if(face.equals(face4)){
			Log.d("Yes4","Yes4");
			destroyLine(connectionLine4);
			destroyLine(connectionLine3);
			destroyLine(connectionLine8);
			
			
		}

		final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);

		// Determine all joints connected to Animated Sprite & destroy
		ArrayList<JointEdge> jointsEdge = facePhysicsConnector.getBody().getJointList();
		for(int i=0; i < jointsEdge.size(); i++){
			Joint j = jointsEdge.get(i).joint;
			this.mPhysicsWorld.destroyJoint(j);
			j=null;
		}
		
		
		this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
	
		
		this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());
		

		this.mScene.unregisterTouchArea(face);
		this.mScene.detachChild(face);
		engineLock.unlock();
		System.gc();
		
	}
	
	// Method to destroy the connecting line
	public void destroyLine(Line line){
		if(line.isDisposed() == true)
			return;
		this.mScene.detachChild(line);
		final PhysicsConnector linePhysicsConnector= this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(line);
		this.mPhysicsWorld.unregisterPhysicsConnector(linePhysicsConnector);
		line.dispose();
		line = null;
	}
	
	
}