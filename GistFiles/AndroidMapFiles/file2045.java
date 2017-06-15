public class Constants {
	
	public static final int SOUND_SELECT = R.raw.select;
	public static final int SOUND_LOCKED = R.raw.locked;
	public static final int SOUND_OPEN = R.raw.open;
	public static final int SOUND_CLOSE = R.raw.close;
    
	public static final void initSoundManager(Context context, SoundManager soundManager){
	    soundManager.addSound(context, SOUND_SELECT);
	    soundManager.addSound(context, SOUND_LOCKED);
	    soundManager.addSound(context, SOUND_OPEN);
	    soundManager.addSound(context, SOUND_CLOSE);
	
	}
}