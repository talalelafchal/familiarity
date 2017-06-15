import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DownloadProductPictures implements UseCase<Uri> {

    private static final String PICTURES_PATH = "/catalog_pictures/";

    private final LocalCatalogRepository repo;
    private final OkHttpClient httpClient;
    private final File location;

    public DownloadProductPictures(Context app, LocalCatalogRepository repository, OkHttpClient okHttpClient) {
        this.repo = repository;
        this.httpClient = okHttpClient;

        this.location = new File(app.getFilesDir(), PICTURES_PATH);
        if (!location.exists()) {
            location.mkdir();
        }
    }

    @Override
    public Subscription execute(Observer<Uri> observer) {
        return getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    @RxLogObservable()
    private Observable<Uri> getObservable() {
        return getEachProduct()
                .flatMap(new Func1<Product, Observable<ProductImage>>() {
                    @Override
                    public Observable<ProductImage> call(Product product) {
                        return saveImageForProduct(product);
                    }
                })
                .flatMap(new Func1<ProductImage, Observable<Uri>>() {
                    @Override
                    public Observable<Uri> call(ProductImage productImage) {
                        if (productImage.isEmpty()) {
                            return Observable.just(Uri.EMPTY);
                        } else {
                            return repo.saveImageUriForProduct(productImage.product, productImage.uri);
                        }
                    }
                });
    }

    private Observable<Product> getEachProduct() {
        return repo.getProducts()
                .flatMapIterable(new Func1<List<Product>, Iterable<? extends Product>>() {
                    @Override
                    public Iterable<? extends Product> call(List<Product> products) {
                        return products;
                    }
                }).filter(new Func1<Product, Boolean>() {
                    @Override
                    public Boolean call(Product product) {
                        // only parent products, not product childs (business logic)
                        return product.getParentId() == 0;
                    }
                });
    }

    private Observable<ProductImage> saveImageForProduct(final Product product) {
        return Observable.create(new Observable.OnSubscribe<ProductImage>() {
            @Override
            public void call(Subscriber<? super ProductImage> subscriber) {
                BufferedSource source = null;
                BufferedSink sink = null;

                try {
                    final File file = getImageFileInput(location, product);

                    source = downloadImage(httpClient, product);
                    sink = Okio.buffer(Okio.sink(file));
                    sink.writeAll(source);

                    final Uri uri = Uri.fromFile(file);
                    product.setLocalImageUri(uri.toString());
                    subscriber.onNext(new ProductImage(product, uri));
                } catch (IOException e) {
                    Log.e("IMAGE_DOWNLOAD", "IOexception", e);
                    subscriber.onError(e);
                } finally {
                    closeSource(source);
                    closeSink(sink);
                }
            }
        }).onErrorResumeNext(Observable.just(ProductImage.empty()));
    }

    private static BufferedSource downloadImage(OkHttpClient httpClient, Product product) throws IOException {
        BufferedSource source;
        final Request request = new Request.Builder().url(product.getImageUrl()).build();
        Response response = httpClient.newCall(request).execute();
        source = response.body().source();
        return source;
    }

    private static File getImageFileInput(File directory, Product product) throws IOException {
        final String fileName = getFileNameFromUrl(product.getImageUrl());
        File imageFile = new File(directory, fileName);
        return imageFile;
    }

    private void closeSource(BufferedSource source) {
        if (source == null) return;
        try {
            source.close();
        } catch (IOException e) {
            // do nothing really
        }
    }

    private void closeSink(BufferedSink sink) {
        if (sink == null) return;
        try {
            sink.close();
        } catch (IOException e) {
            // do nothing really
        }
    }


    private static String getFileNameFromUrl(String url) {
        try {
            return url.substring(url.lastIndexOf("/") + 1, url.length());
        } catch (Exception e) {
            return url;
        }
    }

    private static final class ProductImage {

        public static ProductImage empty() {
            return new ProductImage();
        }

        private final Product product;
        private final Uri uri;

        public ProductImage(Product product, Uri uri) {
            this.product = product;
            this.uri = uri;
        }

        private ProductImage() {
            this.product = null;
            this.uri = null;
        }

        private boolean isEmpty() {
            return product == null && uri == null;
        }
    }
}
