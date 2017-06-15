private void doSearch() {
    String name = mSearchQueryText.getText().toString();
    Sex sex = getCheckedSex(mSexGroup.getCheckedRadioButtonId());
    Rank rank = Rank.of(mRankText.getText().toString());
    PlayerDao.PlayerQuery playerQuery =
        new PlayerDao.SQLBuilder().name(name).sex(sex).rank(rank).build();
    mSubscription = (PlayerDao.getPlayerByQuery(mDb, playerQuery)
        .mapToList(Player.MAPPER)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(mAdapter));
  }
