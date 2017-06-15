    private void showPopUp(View v) {
        ListPopupWindow listPopupWindow = new ListPopupWindow(
                getContext());
        listPopupWindow.setAdapter(new PopUpAdapter());
        listPopupWindow.setAnchorView(v);
        listPopupWindow.setWidth(getContext().getResources().getDimensionPixelSize(R.dimen.popup_width));
        listPopupWindow.setHeight(getContext().getResources().getDimensionPixelSize(R.dimen.popup_height));
        listPopupWindow.setModal(true);
        listPopupWindow.show();
    }


    private static final class PopUpAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.pop_up_menu_example,
                    parent, false);
        }
    }