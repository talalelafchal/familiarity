public class Product {
    int id;
    String title;
    ArrayList children;

    public Product(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.title = object.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Product> fromJson(JSONArray jsonObjects) {
        ArrayList<Product> products = new ArrayList<Product>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                products.add(new Product(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    public ArrayList getChildren() {
        return children;
    }

    public void setChildren(ArrayList children) {
        this.children = children;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return id + " - " + title;
    }
}
