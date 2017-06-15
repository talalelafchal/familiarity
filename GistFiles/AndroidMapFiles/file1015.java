SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);

// Setted to true for compatibility with what seems to be the default encoding for .Net-Services
envelope.dotNet = true;

// Setted to true to not add type definitions in the XML-Request
envelope.implicitTypes = true;

// Setted to false to not add and ID and ROOT label to the envelope
envelope.setAddAdornments(false);

String WSDL_TARGET_NAMESPACE = "http://www.webserviceX.NET/";
String OPERATION_NAME = "ConvertWeight";
SoapObject body = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

// HINT: Use a map (Map<ParamName, ParamValue>) when you have several parameters
body.addProperty("Weight", 23);
body.addProperty("FromUnit", "Kilograms");
body.addProperty("ToUnit", "Grams");

envelope.setOutputSoapObject(body);