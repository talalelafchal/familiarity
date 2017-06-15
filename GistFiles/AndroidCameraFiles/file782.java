public static String convertFileSize(long sizeInB) {//将文件的大小从字节转为常用单位
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (sizeInB >= gb) {
			return String.format("%.1f GB", (float) sizeInB / gb);
		} else if (sizeInB >= mb) {
			float f = (float) sizeInB / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (sizeInB >= kb) {
			float f = (float) sizeInB / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", sizeInB);
	}