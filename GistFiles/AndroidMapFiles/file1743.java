//图片路径  /assets/map/battletower.png
protected PlayerLayer(ccColor4B color) {
  	super(color);

		this.setIsTouchEnabled(true);
		
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		backGround = CCSprite.sprite("map/battletower.png");

		backGround
				.setScaleX(winSize.width / backGround.getTexture().getWidth());
		backGround.setScaleY(winSize.height
				/ backGround.getTexture().getHeight());
		backGround.setPosition(CGPoint.make(winSize.width / 2,
				winSize.height / 2));
		addChild(backGround);
	}