package marxtseng.demo;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startup();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        startup();
    }

    private void startup() {
        ArrayList&lt;Map&lt;String, String&gt;&gt; items = new ArrayList&lt;&gt;();
        for (int i = 0; i &lt; 10; i++) {
            Map&lt;String, String&gt; item = new HashMap&lt;&gt;();
            item.put(&quot;title&quot;, &quot;Title &quot; + Math.round(Math.random() * 100000000));
            item.put(&quot;subtitle&quot;, &quot;Subtitle &quot; + Math.round(Math.random() * 100000000));

            switch(i % 5) {
                case 0:
                    item.put(&quot;image&quot;, R.drawable.ic_directions_bike_black_24dp+&quot;&quot;);
                    break;
                case 1:
                    item.put(&quot;image&quot;, R.drawable.ic_directions_bus_black_24dp+&quot;&quot;);
                    break;
                case 2:
                    item.put(&quot;image&quot;, R.drawable.ic_directions_car_black_24dp+&quot;&quot;);
                    break;
                case 3:
                    item.put(&quot;image&quot;, R.drawable.ic_directions_railway_black_24dp+&quot;&quot;);
                    break;
                case 4:
                    item.put(&quot;image&quot;, R.drawable.ic_directions_run_black_24dp+&quot;&quot;);
                    break;
            }

            items.add(item);
        }

        mRecyclerView.setAdapter(new SampleRecyclerViewAdapter(items));
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public class SampleRecyclerViewAdapter
            extends RecyclerView.Adapter&lt;SampleRecyclerViewAdapter.ViewHolder&gt; {

        private final ArrayList&lt;Map&lt;String, String&gt;&gt; mValues;
        private ViewHolder mPreviousHolder;

        public SampleRecyclerViewAdapter(ArrayList&lt;Map&lt;String, String&gt;&gt; items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_holer_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(holder.mItem.get(&quot;title&quot;));
            holder.mSubTitleView.setText(holder.mItem.get(&quot;subtitle&quot;));
            holder.mLogoView.setImageResource(Integer.parseInt(holder.mItem.get(&quot;image&quot;)));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPreviousHolder != null &amp;&amp; mPreviousHolder != holder)
                        mPreviousHolder.setCollapse(false);

                    holder.toggleCollapse();
                    mPreviousHolder = holder;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public Map&lt;String, String&gt; mItem;
            public View mView;
            public ImageView mLogoView;
            public TextView mTitleView;
            public TextView mSubTitleView;

            private boolean mCollapse = false;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mLogoView = (ImageView) view.findViewById(R.id.logo);
                mTitleView = (TextView) view.findViewById(R.id.title);
                mSubTitleView = (TextView) view.findViewById(R.id.subtitle);
            }

            public void toggleCollapse() {
                mCollapse = !mCollapse;
                mSubTitleView.setVisibility(mCollapse ? View.VISIBLE : View.GONE);
            }

            public void setCollapse(boolean value) {
                mCollapse = value;
                mSubTitleView.setVisibility(mCollapse ? View.VISIBLE : View.GONE);
            }
        }
    }
}