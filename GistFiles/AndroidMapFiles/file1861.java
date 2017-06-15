public class AddressRepository implements BaseRepo<Address> {

    private final RestApi mRestApi;
    private final BriteDatabase mDatabase;

    public AddressRepository(RestApi restApi, BriteDatabase database) {
        mRestApi = restApi;
        mDatabase = database;
    }

    private void bindAddress(Address.InsertRow insertRow, Address address) {
        insertRow.bind(
                address.id(),
                address.name(),
                address.line1(),
                address.line2(),
                address.landmark(),
                address.city(),
                address.country(),
                address.pincode());
    }

    private Observable<Address> addToDb(Address address) {
        Address.InsertRow insertRow = new AddressModel.InsertRow(mDatabase.getWritableDatabase());
        bindAddress(insertRow, address);
        long id = mDatabase.executeInsert(Address.TABLE_NAME, insertRow.program);
        return id > 0 ? Observable.just(address) : Observable.error(new SQLException("Failed to insert address"));
    }

    private Observable<List<Address>> addToDb(List<Address> addresses) {
        Address.InsertRow insertRow = new AddressModel.InsertRow(mDatabase.getWritableDatabase());
        BriteDatabase.Transaction transaction = mDatabase.newTransaction();
        try {
            for (Address address : addresses) {
                bindAddress(insertRow, address);
                long id = mDatabase.executeInsert(Address.TABLE_NAME, insertRow.program);
                if (id == -1) return Observable.error(new SQLException("Failed to insert address"));
            }
        } finally {
            transaction.end();
        }
        return Observable.just(addresses);
    }

    private Observable<Address> queryFromDb(long id) {
        return mDatabase.createQuery(Address.TABLE_NAME, Address.FACTORY.selectById(id).statement)
                .mapToOne(Address.MAPPER);
    }

    private Observable<List<Address>> queryFromDb() {
        return mDatabase.createQuery(Address.TABLE_NAME, Address.FACTORY.selectAll().statement)
                .mapToList(Address.MAPPER);
    }

    private Observable<Integer> removeFromDb(Address address) {
        Address.DeleteById deleteById = new AddressModel.DeleteById(mDatabase.getWritableDatabase());
        deleteById.bind(address.id());
        return Observable.just(mDatabase.executeUpdateDelete(Address.TABLE_NAME, deleteById.program));
    }

    @Override
    public Observable<Address> add(Address address) {
        return mRestApi.addAddress(address).flatMap(this::addToDb);
    }

    @Override
    public Observable<List<Address>> add(List<Address> addresses) {
        return Observable.from(addresses)
                .flatMap(mRestApi::addAddress)
                .toList()
                .flatMap(this::addToDb);
    }

    @Override
    public Observable<Address> query(long id) {
        Observable<Address> db = queryFromDb(id);
        Observable<Address> server = mRestApi.getAddress(id).doOnNext(this::addToDb);
        return Observable.merge(db, server).distinct();
    }

    @Override
    public Observable<List<Address>> query() {
        Observable<List<Address>> db = queryFromDb();
        Observable<List<Address>> server = mRestApi.getAddresses().doOnNext(this::addToDb);
        return Observable.combineLatest(db, Observable.concat(null, server),
                (dbAddress, serverAddress) -> dbAddress.size() == 0 && serverAddress == null ? null : dbAddress)
                .filter(addresses -> addresses != null)
                .distinct();
    }


    @Override
    public Observable<Address> update(Address address) {
        return mRestApi.updateAddress(address.id(), address)
                .flatMap(this::addToDb);
    }

    @Override
    public Observable<Integer> remove(Address address) {
        return mRestApi.removeAddress(address.id())
                .flatMap(aBoolean -> removeFromDb(address));
    }

}
