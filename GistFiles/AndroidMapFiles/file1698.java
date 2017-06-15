import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class AssembleR {

	public static Map<Integer, String> assemble() {
		Map<Integer, String> rMap = new HashMap<Integer, String>();
		for (Class c : R.class.getClasses()) {
			String innerClassName = c.getName().split("\\$")[1];
			for (Field f : c.getFields()) {
				try {
					Integer k = new Integer(f.getInt(c));
					String v = "R.".concat(innerClassName).concat(".").concat(f.getName());
					System.out.println(v + " = " + k);
					rMap.put(k, v);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return rMap;
	}
	public static void main(String[] args) {
		Map<Integer, String> map = assemble();
		// read the decompiled sources and replace the constant int by using map.
	}

}
