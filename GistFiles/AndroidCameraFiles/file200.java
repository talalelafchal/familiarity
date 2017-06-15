public class FriendsExpandableRecyclerViewAdapter extends RecyclerView.Adapter<FriendsExpandableRecyclerViewAdapter.FriendViewHolder> {

    private final List<Friend> friends;
    private final Context context;
    private final ErrorHandlingCallback errorHandlingCallback;
    private final StopRefreshCallback stopRefreshCallback;
    private IApiManager apiManager = NudgeApp.getApplication().getAppComponent().provideApiManager();

    public FriendsExpandableRecyclerViewAdapter(Context context, List<Friend> friends, @NonNull ErrorHandlingCallback errorHandlingCallback, @NonNull StopRefreshCallback stopRefreshCallback) {
        this.context = context;
        this.friends = friends;
        this.errorHandlingCallback = errorHandlingCallback;
        this.stopRefreshCallback = stopRefreshCallback;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expanded_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        final Friend currentFriend = friends.get(position);

        holder.nickname_textView.setText(currentFriend.getNickname());
        holder.refresh_imageView.setOnClickListener(v -> {
            startRotationAnimation(holder.refresh_imageView);

            Observable.timer(15, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe((ignored) -> {
                        if (holder.refresh_imageView.getAnimation() != null) {
                            stopNudgeRotation(holder.refresh_imageView);
                            Toast.makeText(context, "Time is up. You'll get update later", Toast.LENGTH_SHORT).show();
                        }
                    }, errorHandlingCallback::onError);

            apiManager.sendInfoRequest(new InfoRequest(currentFriend.getEmail()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(simpleResponse -> {
                    })
                    .doOnError(errorHandlingCallback::onError)
                    .subscribe();
        });

        Nudge lastNudge = currentFriend.getLastNudge();
        if (lastNudge != null) {
            String dateUpdate = new SimpleDateFormat("MMM d, HH:mm:ss", Locale.getDefault()).format(lastNudge.getTime());
            holder.lastNudge_textView.setText(context.getString(R.string.last_nudge, dateUpdate, lastNudge.getStatus()));
        } else {
            holder.lastNudge_textView.setText(context.getString(R.string.last_nudge, "N/A", "N/A"));
        }

        holder.nudge_floatingActionButton.setOnClickListener(v -> {
            startRotationAnimation(holder.animationNudge_imageView);
            apiManager.sendNudgeRequest(new NudgeRequest(1, currentFriend.getEmail()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(nudge -> {
                        stopNudgeRotation(holder.animationNudge_imageView);
                        String dateUpdate = new SimpleDateFormat("MMM d, HH:mm:ss").format(nudge.getTime());
                        friends.get(position).setLastNudge(nudge);
                        holder.lastNudge_textView.setText(context.getString(R.string.last_nudge, dateUpdate, nudge.getStatus()));
                    }, throwable -> {
                        errorHandlingCallback.onError(throwable);
                        stopNudgeRotation(holder.animationNudge_imageView);
                    });
        });

        if (holder.map_view != null) {
            holder.map_view.onCreate(null);
            holder.map_view.onResume();
            holder.map_view.getMapAsync(googleMap -> {
                MapsInitializer.initialize(context.getApplicationContext());
                holder.googleMap = googleMap;
                if (currentFriend.getLatitude() != null && currentFriend.getLongitude() != null) {
                    LatLng coordinates = new LatLng(currentFriend.getLatitude(), currentFriend.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(coordinates)
                            .title(currentFriend.getNickname()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                }
            });
        }

        CheckNull.check(currentFriend.getLastUpdate(), lastUpdate -> {
            if (lastUpdate != null) {
                String dateUpdate = new SimpleDateFormat("MMM d, HH:mm").format(new Date(currentFriend.getLastUpdate()));
                holder.lastUpdate_textView.setText(context.getString(R.string.last_update_s, dateUpdate));
            } else {
                holder.lastUpdate_textView.setText(context.getString(R.string.last_update_s, "N/A"));
            }
        });

        CheckNull.check(currentFriend.getAmbientLight(), ambientLight -> holder.light_textView.setText(ambientLight));
        CheckNull.check(currentFriend.getMovements(), movements -> holder.movements_textView.setText(movements));
        CheckNull.check(currentFriend.getTemperature(), temperature -> holder.temperature_textView.setText(temperature));

        if (currentFriend.getLatitude() != null && currentFriend.getLongitude() != null) {
            ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(context);
            locationProvider.getReverseGeocodeObservable(currentFriend.getLatitude(), currentFriend.getLongitude(), 1)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(errorHandlingCallback::onError)
                    .subscribe(addresses -> {
                        if (!addresses.isEmpty()) {
                            holder.currentLocation_textView.setText(addresses.get(0).getLocality().toUpperCase() + ", " + addresses.get(0).getAddressLine(0));
                        } else {
                            holder.currentLocation_textView.setText(context.getString(R.string.current_location_s, "N/A"));
                        }
                    }, throwable -> {
                        holder.currentLocation_textView.setText(context.getString(R.string.current_location_s, "N/A"));
                    });
        }
    }

    @Override
    public void onViewRecycled(FriendViewHolder holder) {
        if (holder.googleMap != null) {
            holder.googleMap.clear();
            holder.googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public void updateInfo(Friend friend) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getEmail().equals(friend.getEmail())) {
                friend.setLastNudge(friends.get(i).getLastNudge());
                friends.set(i, friend);
                notifyItemChanged(i);
            }
        }
    }

    public void updateNudge(Nudge nudge) {
        for (int i = 0; i < friends.size(); i++) {
            Friend currentFriend = friends.get(i);
            Nudge lastNudge = currentFriend.getLastNudge();
            if (lastNudge != null && nudge.getId().equals(lastNudge.getId())) {
                currentFriend.getLastNudge().setStatus(nudge.getStatus());
                currentFriend.getLastNudge().setTime(nudge.getTime());
                notifyItemChanged(i);
            } else {
                if (nudge.getTo() != null && currentFriend.getEmail().equals(nudge.getTo())) {
                    currentFriend.getLastNudge().setStatus(nudge.getStatus());
                    currentFriend.getLastNudge().setTime(nudge.getTime());
                    currentFriend.getLastNudge().setId(nudge.getId());
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void startRotationAnimation(ImageView imageView) {
        if (imageView != null) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.nudge_rotating);
            anim.setRepeatCount(Animation.INFINITE);
            imageView.startAnimation(anim);
        }
    }

    public void stopNudgeRotation(ImageView imageView) {
        if (imageView != null) {
            imageView.clearAnimation();
        }
        stopRefreshCallback.stopRefresh();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        private GoogleMap googleMap;
        private TextView nickname_textView;
        private TextView currentLocation_textView;
        private TextView lastUpdate_textView;
        private TextView movements_textView;
        private TextView temperature_textView;
        private TextView light_textView;
        private TextView lastNudge_textView;
        private MapView map_view;
        private ImageView refresh_imageView;
        private ImageView animationNudge_imageView;
        private View nudge_floatingActionButton;

        public FriendViewHolder(View itemView) {
            super(itemView);
            nickname_textView = (TextView) itemView.findViewById(R.id.nickname_textView);
            currentLocation_textView = (TextView) itemView.findViewById(R.id.currentLocation_textView);
            lastUpdate_textView = (TextView) itemView.findViewById(R.id.lastUpdate_textView);
            movements_textView = (TextView) itemView.findViewById(R.id.movements_textView);
            temperature_textView = (TextView) itemView.findViewById(R.id.temperature_textView);
            light_textView = (TextView) itemView.findViewById(R.id.light_textView);
            lastNudge_textView = (TextView) itemView.findViewById(R.id.lastNudge_textView);
            map_view = (MapView) itemView.findViewById(R.id.map_view);
            refresh_imageView = (ImageView) itemView.findViewById(R.id.refresh_imageView);
            animationNudge_imageView = (ImageView) itemView.findViewById(R.id.animationNudge_imageView);
            nudge_floatingActionButton = itemView.findViewById(R.id.nudge_floatingActionButton);
        }
    }

}