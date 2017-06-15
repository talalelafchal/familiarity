// Input: byte[] data, int width, int height, File jpegFile
YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
FileOutputStream jepgFileOS = new FileOutputStream(jpegFile);
yuvimage.compressToJpeg(new Rect(0, 0, width, height), 95, jpegFileOS);
jpegFileOS.close();