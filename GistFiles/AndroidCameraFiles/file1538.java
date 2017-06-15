// This shell command returns all packages that contain the word camera
String command = "pm list packages -f > "
	+ Environment.getExternalStorageDirectory() + "/packages.txt\n"
	+ "grep camera " + Environment.getExternalStorageDirectory()
	+ "/packages.txt\n";
List<String> cameras = new ArrayList<String>();

try {
	String line;
	Process process = Runtime.getRuntime().exec("sh");
	OutputStream stdin = process.getOutputStream();
	InputStream stderr = process.getErrorStream();
	InputStream stdout = process.getInputStream();

	// execute the command
	stdin.write((command).getBytes());
	stdin.write("exit\n".getBytes());
	stdin.flush();
	stdin.close();

    // read output
	BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
	while ((line = br.readLine()) != null) {
	// add each found package to our List
		cameras.add(line);
		// log the output for ease of debugging/bug reports
		Log.d("[Output]", line);
	}
	br.close();

	// read & log errors if any
	br = new BufferedReader(new InputStreamReader(stderr));
	while ((line = br.readLine()) != null) {
		Log.e("[Error]", line);
	}
	br.close();
	process.waitFor();
	process.destroy();

	} catch (Exception ignored) {}

	// Do something with your List values, such as starting your intent
	// Make sure you have declared the WRITE_EXTERNAL_STORAGE permission in
	// your manifest