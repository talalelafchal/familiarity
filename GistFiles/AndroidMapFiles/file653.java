package com.test.customWidgets;

import android.widget.EditText;

public class Utility {

	private final String TW_SEMI_BOLD = "10TitilliumWeb-SemiBold.ttf";
	private final String TW_SEMI_BOLD_ITALIC = "11TitilliumWeb-SemiBoldItalic.ttf";
	private final String TW_BLACK = "1TitilliumWeb-Black.ttf";
	private final String TW_BOLD = "2TitilliumWeb-Bold.ttf";
	private final String TW_BOLD_ITALIC = "3TitilliumWeb-BoldItalic.ttf";
	private final String TW_EXTRA_LIGHT = "4TitilliumWeb-ExtraLight.ttf";
	private final String TW_EXTRA_LIGHT_ITALIC = "5TitilliumWeb-ExtraLightItalic.ttf";
	private final String TW_ITALIC = "6TitilliumWeb-Italic.ttf";
	private final String TW_LIGHT = "7TitilliumWeb-Light.ttf";
	private final String TW_LIGHT_ITALIC = "8TitilliumWeb-LightItalic.ttf";
	private final String TW_REGULAR = "9TitilliumWeb-Regular.ttf";

	public  String getFontName(int fontNos) {
		String font = null;
		switch (Integer.valueOf(fontNos)) {
		case 1:font = TW_BLACK;break;
		case 2:font = TW_BOLD;break;
		case 3:font = TW_BOLD_ITALIC;break;
		case 4:font = TW_EXTRA_LIGHT;break;
		case 5:font = TW_EXTRA_LIGHT_ITALIC;break;
		case 6:font = TW_ITALIC;break;
		case 7:font = TW_LIGHT;break;
		case 8:font = TW_LIGHT_ITALIC;break;
		case 9:font = TW_REGULAR;break;
		case 10:font = TW_SEMI_BOLD;break;
		case 11:font = TW_SEMI_BOLD_ITALIC;break;

		default:
			break;
		}
		return font;
	}

}
