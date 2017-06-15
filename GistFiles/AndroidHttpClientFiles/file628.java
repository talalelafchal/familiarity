@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
	try {
		InputStream stream = req.getInputStream();

    // do whatever you want with the stream now
	} catch (Exception ex) {
		throw new ServletException(ex);
	}
}