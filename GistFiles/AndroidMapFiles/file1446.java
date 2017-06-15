
Envelope env = new Envelope();
map.getExtent().queryEnvelope(env);

double xMax = env.getXMax();
double xMin = env.getXMin();
double yMax = env.getYMax();
double yMin = env.getYMin();

String msg = "xMax:" + xMax + " xMin:" + xMin + " yMax:"
		+ yMax + " yMin:" + yMin;
Log.d("onSingleTap", msg);
Toast.makeText(PMMA.this, msg, Toast.LENGTH_SHORT);
