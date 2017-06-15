MapTemplate map;

double latitude = -23.5703255; 
double longitude = -45.6921585; 

Point point = new Point(latitude, longitude); 
Pin pin = new Pin(bitmap, point); 

map.addPin(pin);