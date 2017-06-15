String listId = String.valueOf(getListId());
subscriptions.add(db.createQuery(TodoItem.TABLE, LIST_QUERY, listId)
        .mapToList(TodoItem.MAPPER)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter));
