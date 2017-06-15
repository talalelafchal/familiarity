package codepath.com.recyclerviewfun;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Contact> allContacts;
    ContactsAdapter adapter;
    RecyclerView rvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvItems = (RecyclerView) findViewById(R.id.rvContacts);
        allContacts = Contact.createContactsList(10, 0);
        adapter = new ContactsAdapter(allContacts);
        rvItems.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvItems.setLayoutManager(linearLayoutManager);
    }

    public void clearItems(View view) {
        allContacts.clear();
        Contact.resetContactId();
        allContacts.addAll(Contact.createContactsList(10, 0));
        adapter.notifyDataSetChanged();
    }
}
