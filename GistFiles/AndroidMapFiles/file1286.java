SqlBrite sqlBrite = new SqlBrite.Builder().build();
BriteDatabase database = sqlBrite.wrapDatabaseHelper(openHelper, Schedulers.io());

// Insert transaction
Transaction transaction = database.newTransaction();
Address.InsertRow insertRow = new Address.InsertRow(database.getWritableDatabase());
try {
    insertRow.bind(1, "Home", "House No. 42", "Foo Lane", null, "Gurgaon", "India", 123456);
    database.executeInsert(Address.TABLE_NAME, insertRow.program);

    insertRow.bind(1, "Office", "A-83", null, "DTDC", "Okhla", "India", 123654);
    database.executeInsert(Address.TABLE_NAME, insertRow.program);
    
    transaction.markSuccessful();
} finally {
    transaction.end();
}

// Query operation
Observable<List<Address>> addressesObservable = database
    .createQuery(Address.TABLE_NAME, Address.FACTORY.selectAll().statement)
    .mapToList(Address.MAPPER);
    .subscribe(addresses -> {
        // Your code here
    }, throwable -> {
        // report error here
    });