/**
 * MainActivity class
 */
public class StudentsListActivity extends AppCompatActivity {

    private ListView list;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTextView = (TextView) findViewById(R.id.text);
        list = (ListView) findViewById(R.id.custom_list);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AsyncTask<Void, Void, String> asyncTask = new TaskWS(StudentsListActivity.this).execute();
        try {
            Gson gson = new Gson();
            ProductList product = gson.fromJson(asyncTask.get(), ProductList.class);

//            ArrayAdapter<Product> adapter = new ArrayAdapter<Product>(StudentsListActivity.this, android.R.layout.simple_list_item_1, product.getContent());

            productAdapter = new ProductAdapter(product.getContent(), StudentsListActivity.this);
            list.setAdapter(productAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    ...
}