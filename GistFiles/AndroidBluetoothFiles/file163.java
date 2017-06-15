//From:

public List<User> getUsersWithBlogs() {
   final List<User> allUsers = UserCache.getAllUsers();
   final List<User> usersWithBlogs = new ArrayList<>();
   for (User user : allUsers) {
       if (user.blog != null && !user.blog.isEmpty()) {
           usersWithBlogs.add(user);
       }
   }
   Collections.sort(usersWithBlogs, (user1, user2) -> user1.name.compareTo(user2.name));
   return usersWithBlogs;
}
//To:

public Observable<User> getUsersWithBlogs() {
   return Observable.fromIterable(UserCache.getAllUsers())
                    .filter(user -> user.blog != null && !user.blog.isEmpty())
                    .sorted((user1, user2) -> user1.name.compareTo(user2.name));
}