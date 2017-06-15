mDebtorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Debtor d = mDebtorsList.get(position);
                Log.d("CLICKED", d.getName());
                Intent i = new Intent(getApplicationContext(), DebtorDetailActivity.class);
                i.putExtra("NAME", d.getName());
                i.putExtra("AMOUNT", d.getAmount());
                startActivity(i);
            }
        });