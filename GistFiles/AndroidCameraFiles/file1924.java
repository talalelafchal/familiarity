package com.socket9.fleet.ViewHolders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.socket9.fleet.Models.ActivityDetail;
import com.socket9.fleet.R;
import com.socket9.fleet.Utils.Singleton;
import com.socket9.fleet.Widgets.Photo;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import timber.log.Timber;

/**
 * Created by visit on 9/22/15 AD.
 */
public class CustomFormRecyclerAdapter extends RecyclerView.Adapter<CustomFormRecyclerAdapter.CustomFormHolder> {

    public static final int SHORT_TEXT = 100;
    public static final int LONG_TEXT = 101;
    public static final int SINGLE_CHOICE = 102;
    public static final int MULTIPLE_CHOICE = 103;
    public static final int PHOTO = 104;
    public static final int NUMERIC = 105;
    DecimalFormat formatter = new DecimalFormat("#,###,###");

    private List<ActivityDetail> activityDetails;
    CustomFormViewHolderListener customFormViewHolderListener;

    public CustomFormRecyclerAdapter(List<ActivityDetail> activityDetails) {
        this.activityDetails = activityDetails;
    }

    public void setItems(List<ActivityDetail> activityDetails) {
        this.activityDetails = activityDetails;
    }

    @Override
    public CustomFormHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_custom_form, parent, false);
        CustomFormHolder customFormHolder = new CustomFormHolder(itemView);
        setSubFields(viewType, customFormHolder);
        return customFormHolder;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        //Log.d("CustomForm Recycle",activityDetails.get(position).valueType.name);
        switch (activityDetails.get(position).valueType.name) {
            case "Short text":
                viewType = SHORT_TEXT;
                break;
            case "Long text":
                viewType = LONG_TEXT;
                break;
            case "Photo":
                viewType = PHOTO;
                break;
            case "Multiple Choice":
                viewType = MULTIPLE_CHOICE;
                break;
            case "Single Choice":
                viewType = SINGLE_CHOICE;
                break;
            case "Numeric":
                viewType = NUMERIC;
                break;
            default:
                viewType = 0;
        }
        return viewType;
    }

    @Override
    public void onBindViewHolder(final CustomFormHolder holder, int position) {
        final ActivityDetail activityDetail = activityDetails.get(position);

        String titleText;
        // No Problem
//        if (activityDetail.getIsMandatory() == 1)
//            titleText = activityDetail.getTitle() + "*";
//        else
            titleText = activityDetail.title;
        holder.tvCustomFormTitle.setText(titleText);

        if (Singleton.getInstance().getActivityMode() == Singleton.ACTIVITY_WRITE) {
//            setSubFields(activityDetail.getValueType().getName(), holder);
            if (getItemViewType(position) == PHOTO && activityDetail.value != null && !activityDetail.value.equals("")) {
                Glide.with(holder.ivCustomForm.getContext()).load(activityDetail.value).placeholder(R.mipmap.ic_default_photo).into(holder.ivCustomForm);
                holder.ivCustomForm.setVisibility(View.VISIBLE);

            } else {
                if(getItemViewType(position) == NUMERIC){
                    try {
                        if(!activityDetail.value.equals(""))
                            holder.etCustomForm.setText(formatter.format(Double.parseDouble(activityDetail.value.replace(",", ""))));
                    }catch(NullPointerException e){
                    }
                }else{
                    holder.etCustomForm.setText(activityDetail.value);
                }
                holder.etCustomForm.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        activityDetail.value = (editable.toString());
                    }
                });
            }
        } else {
            if (getItemViewType(position) == PHOTO) {
                if (activityDetail.value != null) {
                    Glide.with(holder.ivCustomForm.getContext()).load(activityDetail.value).placeholder(R.mipmap.ic_default_photo).into(holder.ivCustomForm);
                    holder.ivCustomForm.setVisibility(View.VISIBLE);
                    holder.tvCustomForm.setVisibility(View.GONE);
                }else{
                    holder.tvCustomForm.setText("Take a photo");
                    holder.tvCustomForm.setTextColor(ContextCompat.getColor(holder.tvCustomForm.getContext(), R.color.colorTextSubtitle));
                }
            } else {
                holder.etCustomForm.setVisibility(View.GONE);
                holder.tvCustomForm.setVisibility(View.VISIBLE);
                if(getItemViewType(position) == NUMERIC){
                    holder.tvCustomForm.setText(activityDetail.value == null ? "Enter here" : formatter.format(Double.parseDouble(activityDetail.value.replace(",", ""))));
                } else {
                    holder.tvCustomForm.setText(activityDetail.value == null ? "Enter here" : activityDetail.value);
                }
                holder.tvCustomForm.setTextColor(ContextCompat.getColor(holder.tvCustomForm.getContext(), R.color.colorTextSubtitle));
            }
        }

        setTextFromSelectedChoice(activityDetail, holder);
        holder.itemView.setTag(activityDetail);
    }

    public void setTextFromSelectedChoice(ActivityDetail dataEntity, CustomFormHolder holder) {
        if (dataEntity.getMaterialDialog() != null) {
            boolean isSingleChoice = dataEntity.valueType.name.equals("Single Choice");
            boolean isMultipleChoice = dataEntity.valueType.name.equals("Multiple Choice");

            if (isSingleChoice && dataEntity.getMaterialDialog().getSelectedIndex() != -1) {
                holder.tvCustomForm.setText(dataEntity.lists.get(dataEntity.getMaterialDialog().getSelectedIndex()).title);
            } else if (isMultipleChoice && dataEntity.getMaterialDialog().getSelectedIndices().length > 0) {
                String list = "";
                for (Integer i : dataEntity.getMaterialDialog().getSelectedIndices()) {
                    list += dataEntity.lists.get(i).title + " ";
                }
                holder.tvCustomForm.setText(list);
            } else {
                holder.tvCustomForm.setText("");
            }
           // Log.d("CustomFromRecycle","call from recycleadapter:");
        }
    }

    public void setCustomFormViewHolderListener(CustomFormViewHolderListener customFormViewHolderListener) {
        this.customFormViewHolderListener = customFormViewHolderListener;
    }

    public void setSubFields(int type, CustomFormHolder holder) {
        switch (type) {
            case SHORT_TEXT:
                holder.etCustomForm.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case LONG_TEXT:
                holder.etCustomForm.setGravity(Gravity.TOP);
                holder.etCustomForm.setMinLines(3);
                holder.etCustomForm.setMaxLines(3);
                break;
            case PHOTO:
                holder.tvCustomForm.setHint("Take a photo");
                holder.etCustomForm.setVisibility(View.GONE);
                holder.tvCustomForm.setVisibility(View.VISIBLE);

                break;
            case MULTIPLE_CHOICE:
            case SINGLE_CHOICE:
                holder.etCustomForm.setVisibility(View.GONE);
                holder.tvCustomForm.setVisibility(View.VISIBLE);
                break;
            case NUMERIC:
                holder.etCustomForm.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                holder.etCustomForm.setKeyListener(DigitsKeyListener.getInstance("0123456789,"));
                holder.etCustomForm.setFilters(new InputFilter[] {new InputFilter.LengthFilter(15)});
                break;
            default:
//                return status == 0 ? R.mipmap.ic_photo_camera_grey_500_24dp : R.mipmap.ic_photo_camera_green_500_24dp;
        }

    }

    @Override
    public int getItemCount() {
        return activityDetails.size();
    }

    public class CustomFormHolder extends RecyclerView.ViewHolder {
        TextView tvCustomFormTitle;
        EditText etCustomForm;
        TextView tvCustomForm;
        Photo ivCustomForm;

        public CustomFormHolder(View itemView) {
            super(itemView);
            this.tvCustomFormTitle = (TextView) itemView.findViewById(R.id.tvCustomFormTitle);
            this.tvCustomForm = (TextView) itemView.findViewById(R.id.tvCustomForm);
            this.etCustomForm = (EditText) itemView.findViewById(R.id.etCustomForm);
            this.ivCustomForm = (Photo) itemView.findViewById(R.id.ivCustomForm);


            tvCustomForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customFormViewHolderListener != null) {
                        if (CustomFormRecyclerAdapter.this.getItemViewType(getAdapterPosition()) == PHOTO) {
                            customFormViewHolderListener.onPhotoClicked(ivCustomForm, getAdapterPosition());
                        } else {
                            customFormViewHolderListener.onBtnClicked(activityDetails.get(getAdapterPosition()),getAdapterPosition());
                        }
                    }
                }
            });

            etCustomForm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (getItemViewType() == NUMERIC) {
                        customFormViewHolderListener.onTextChange(etCustomForm, getAdapterPosition(), editable);
                    }

                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (customFormViewHolderListener != null) {
                        customFormViewHolderListener.onViewClicked(activityDetails.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface CustomFormViewHolderListener {
        void onViewClicked(ActivityDetail optionsList);

        void onBtnClicked(ActivityDetail optionsList,int index);

        void onPhotoClicked(ImageView imageView, int index);

        void onTextChange(EditText etCustomForm, int index, Editable editable);
    }
}
