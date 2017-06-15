//TextureAtlas for andengine
// Billy Lindeman (billy@zoa.io)
// 2011 Protozoa, LLC

//loads texture atlas from Zwoptex Generic plist file
//you pass it the name of the asset (without extension)
//it will load .plist as the coordinates and .png as the texture
//requires https://github.com/tenaciousRas/android-plist-parser

package org.anddev.andengine.opengl.texture.atlas;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.source.AssetTextureSource;

import com.longevitysoft.android.xml.plist.PListXMLHandler;
import com.longevitysoft.android.xml.plist.PListXMLHandler.PListParserListener;
import com.longevitysoft.android.xml.plist.PListXMLHandler.ParseMode;
import com.longevitysoft.android.xml.plist.PListXMLParser;
import com.longevitysoft.android.xml.plist.domain.Dict;
import com.longevitysoft.android.xml.plist.domain.PList;
import com.longevitysoft.android.xml.plist.domain.PListObject;

import android.content.Context;
import android.content.res.AssetManager;


public class TextureAtlas implements PListParserListener{
	//constructor values
	private Engine mEngine;
	private Context mContext;
	private String mName;
	
	//texture info
	private int sizeX,sizeY;
	public Texture mTexture;
	
	//textureregion container
	private HashMap<String,TextureRegion> mRegions = new HashMap<String,TextureRegion>();

	public TextureAtlas (Context pContext, Engine pEngine, String sAssetName) throws IOException {
		//store constructor values
		this.mEngine = pEngine;
		this.mContext = pContext;
		this.mName = sAssetName;
		
		//create input stream for asset
		AssetManager pAssetManager = pContext.getAssets();
		InputStream asset = pAssetManager.open(sAssetName+".plist");
		
		//create plist handler
		PListXMLHandler plistHandler = new PListXMLHandler();
		plistHandler.setParseListener(this);
		//parse plist
		PListXMLParser plistParser = new PListXMLParser();
		plistParser.setHandler(plistHandler);
		plistParser.parse(asset);
		
	}
	
	//helper function to grab a region from a keyname 
	public TextureRegion regionForKey(String key) {
		//return key, or illegal state
		if(mRegions.containsKey(key)) {
			return mRegions.get(key);
		}else {
			throw new IllegalStateException();
		}
	}
	
	//initialize the texture to hold our atlas
	private void initalizeTexture() {
		AssetTextureSource atlasSource = new AssetTextureSource(mContext, mName+".png");
		this.mTexture = new Texture(sizeX, sizeY, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mTexture.addTextureSource(atlasSource, 0, 0);
		mEngine.getTextureManager().loadTexture(this.mTexture);
	}
	
	//once plist is loaded, we parse the values and init our regions
	public void onPListParseDone(PList pList, ParseMode mode) {
		//grab root element
		Dict root = (Dict) pList.getRootElement();
		//load size from metadata
		Dict metadata = root.getConfigurationObject("metadata");
		String tSize = metadata.getConfiguration("size").getValue();
		String tSizes[] = tSize.substring(1,tSize.length()-1).split(", ");
		this.sizeX = new Integer(tSizes[0]);
		this.sizeY = new Integer(tSizes[1]);
		//now that we've loaded the sizes, init the texturesource
		this.initalizeTexture();
		
		//parse our frame info and store in map
		Dict frames = root.getConfigurationObject("frames");
		Map<String,PListObject> frameMap = frames.getConfigMap();
		
		//loop each frame and create the texture region
		for(String key : frameMap.keySet()) {
			
			//parse rect coords from dictionary
			Dict fDict = (Dict)frameMap.get(key);
			String sRect = fDict.getConfiguration("textureRect").getValue();
			String aRect[] = sRect.substring(2,sRect.length()-2).split(", ");

			int rX = new Integer(aRect[0]);
			int rY = new Integer(aRect[1].substring(0,aRect[1].length()-1));
			int rWidth = new Integer(aRect[2].substring(1));
			int rHeight = new Integer(aRect[3]);
			
			//create new textureRegion and store it in hashmap
			mRegions.put(key, new TextureRegion(this.mTexture, rX, rY, rWidth, rHeight));
			
		}		
	}		
}
