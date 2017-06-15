package dm.com.imagepixelator;

import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import dm.com.dmlib.MyClass;
import dm.com.dmlib.dimageview.DZoomableImageView;

public class GalleryActivity extends AppCompatActivity {

    DZoomableImageView img_gallery_picture;
    RecyclerView gallery_recycler;
    ArrayList<String> imagesArray = new ArrayList<>();
    GalleryAdapter galleryAdapter;
    TextView txt_no_images_found;
    LinearLayout lin_gallery_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        img_gallery_picture = (DZoomableImageView) findViewById(R.id.img_gallery_picture);
        txt_no_images_found = (TextView) findViewById(R.id.txt_no_images_found);
        lin_gallery_back = (LinearLayout) findViewById(R.id.lin_gallery_back);
        lin_gallery_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery_recycler = (RecyclerView) findViewById(R.id.gallery_recycler);
        gallery_recycler.setLayoutManager(layoutManager);
        gallery_recycler.setItemAnimator(new DefaultItemAnimator());

        for (File f : Global.galleryDir.listFiles()) {
            if (f.isFile()) {
                imagesArray.add(f.getPath());
            }
        }

        if (imagesArray.size() > 0) {
            try {
                txt_no_images_found.setVisibility(View.GONE);
                new MyClass().loadImageFromFileUri(GalleryActivity.this, imagesArray.get(0), img_gallery_picture);
                galleryAdapter = new GalleryAdapter(imagesArray);
                gallery_recycler.setAdapter(galleryAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            txt_no_images_found.setVisibility(View.VISIBLE);
        }
    }

    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

        private ArrayList<String> arrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView img_gallery_list_item;

            public MyViewHolder(View view) {
                super(view);
                img_gallery_list_item = (ImageView) view.findViewById(R.id.img_gallery_list_item);
            }
        }

        public GalleryAdapter(ArrayList<String> arrayList) {
            this.arrayList = arrayList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
//            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), arrayList.get(position), MediaStore.Images.Thumbnails.MINI_KIND, (BitmapFactory.Options) null);
            holder.img_gallery_list_item.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(arrayList.get(position)), (int) (100 * Global.scale + 0.5f), (int) (100 * Global.scale + 0.5f)));
            holder.img_gallery_list_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new MyClass().loadImageFromFileUri(GalleryActivity.this, arrayList.get(position), img_gallery_picture);
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
}
