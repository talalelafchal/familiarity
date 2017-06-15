        // inflate MapView from layout
        mMapView = (MapView) findViewById(R.id.mapView);
        // create an ArcGISMap with BasemapType topo
        final ArcGISMap map = new ArcGISMap(Basemap.Type.DARK_GRAY_CANVAS_VECTOR, 65.783988, -83.447820, 6);


        pointCollectionOne = new PointCollection(SpatialReference.create(102113));
        pointCollectionOne.add(-9080995.6424, 9910570.681900002);
        pointCollectionOne.add(-9349175.207599999, 9361330.556400001);
        pointCollectionOne.add(-9236116.532600001, 10398421.5554);

        pointCollectionPolyOne = new PointCollection(SpatialReference.create(102113));
        pointCollectionPolyOne.add(-11718058.9064, 11923317.4762);
        pointCollectionPolyOne.add(-11673874.3463, 11823798.2926);
        pointCollectionPolyOne.add(-11663379.0265, 11800374.9416);
        pointCollectionPolyOne.add(-11624640.365, 11714620.1008);
        pointCollectionPolyOne.add(-11688098.9073, 11419718.659);
        pointCollectionPolyOne.add(-11780562.7897, 11620949.2129);
        pointCollectionPolyOne.add(-11718058.9064, 11923317.4762);

        Part partOne = new Part(pointCollectionOne);

        PartCollection polylineParts = new PartCollection(partOne);
        Polyline polyline = new Polyline(polylineParts);

        for (ImmutablePart immutablePart : polyline.getParts()) {
            Log.d(TAG, "No. Of Segments: " + immutablePart.size());

            Point p1 = immutablePart.getPoint(0);
            Log.d(TAG, "| Point 1 x: " + p1.getX() + "| Point 1 y: " + p1.getY() + "|");
            Point p2 = immutablePart.getPoint(1);
            Log.d(TAG, "| Point 2 x: " + p2.getX() + "| Point 2 y: " + p2.getY() + "|");

            Point ipPnt =  immutablePart.getStartPoint();
            Point endPnt = immutablePart.getEndPoint();

            Log.d(TAG, "| StartPoint X: " + ipPnt.getX() + "| StartPoint Y: " + ipPnt.getY() + "|");
            Log.d(TAG, "| EndPoint X: " + endPnt.getX() + "| EndPoint Y: " + endPnt.getY() + "|");

            for (Segment segment : immutablePart) {
                Log.d(TAG, "| Seg-StartPoint X: " + segment.getStartPoint().getX() + "| Seg-StartPoint Y: " + segment.getStartPoint().getY() + "|");
                Log.d(TAG, "| Seg-EndPoint X: " + segment.getEndPoint().getX() + "| Seg-EndPoint Y: " + segment.getEndPoint().getY() + "|");
            }
        }

        SimpleLineSymbol sls = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.YELLOW, 4);
        Graphic graphic = new Graphic(polyline, sls);
        GraphicsOverlay gOverlay = new GraphicsOverlay();

        gOverlay.getGraphics().add(graphic);
        mMapView.getGraphicsOverlays().add(gOverlay);

        mMapView.setMap(map);