/*
To address the first issue from our change, we can make use of any of the blocking operators available to an Observable such as blockingFirst() and blockingNext(). Essentially, both of these operators will block until an item is emitted downstream: blockingFirst() will return the first element emitted and finish, whereas blockingNext() will return an Iterable which allows you to perform a for-each loop on the underlying data (each iteration through the loop will block).

A side-effect of using a blocking operation that is important to be aware of, though, is that exceptions are thrown on the calling thread rather than being passed to an observer’s onError() method.

Using a blocking operator to change the method signature back to a List<User>, our snippet would now look like this:
*/
public List<User> getUsersWithBlogs() {
   return Observable.fromIterable(UserCache.getAllUsers())
           .filter(user -> user.blog != null && !user.blog.isEmpty())
           .sorted((user1, user2) -> user1.name.compareTo(user2.name))
           .toList()
           .blockingGet();
}