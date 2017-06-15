
//Metodo para enviar a photo (pode deve ficar em algum serviço,
	public static void sendPhoto(byte[] photodata) throws Exception {
		Hashtable<String, String> params = new Hashtable<String, String>();
		params.put("photo", Base64.encodeBytes(photodata));
		HttpService.post(URL_PHOTO, params);
	}
