import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import agency.mimo.R;
import agency.mimo.model.entities.LanguageEntity;
import agency.mimo.model.entities.UserEntity;
import butterknife.Bind;
import horses.camera.util.StringUtils;
import horses.commons.Methods;

public class DetailInfoFragment extends BaseFragment {

    private static UserEntity userEntity;

    @Bind(R.id.fullName)
    protected TextView fullName;

    @Bind(R.id.sex)
    protected TextView sex;

    @Bind(R.id.birth)
    protected TextView birth;

    @Bind(R.id.country)
    protected TextView country;

    @Bind(R.id.language)
    protected TextView language;

    private static TextView fullNameStatic;
    private static TextView sexStatic;
    private static TextView birthStatic;
    private static TextView countryStatic;
    private static TextView languageStatic;

    public static DetailInfoFragment init(UserEntity entity) {

        userEntity = entity;

        return new DetailInfoFragment();
    }

    @Override
    protected int getFragmentView() {
        return R.layout.fragment_detail_info;
    }

    @SuppressWarnings("Convert2streamapi")
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate() {

        fullNameStatic = fullName;
        sexStatic = sex;
        birthStatic = birth;
        countryStatic = country;
        languageStatic = language;

        updateData(userEntity, activity);
    }

    @SuppressWarnings("Convert2streamapi")
    @SuppressLint("SetTextI18n")
    public static void updateData(UserEntity userEntity, Activity activity){

        //region Personal Info
        fullNameStatic.setText(userEntity.getFirstName() + " " + userEntity.getLastName());

        sexStatic.setText(userEntity.getPerson().getSexString().equals("M") ?
                activity.getResources().getString(R.string.title_male) :
                activity.getResources().getString(R.string.title_female));

        if(userEntity.getPerson().getBirdDateShow() == 0)
            birthStatic.setVisibility(View.GONE);

        birthStatic.setText(userEntity.getPerson().getBirdDate());

        if(userEntity.getPerson().getCountryShow() == 0)
            countryStatic.setVisibility(View.GONE);

        int countryId = userEntity.getPerson().getIdCountry() - 1;

        List<String> counties = Arrays.asList(activity.getResources().getStringArray(R.array.countries));

        countryStatic.setText(counties.get(countryId));

        List<String> langArray = Arrays.asList(activity.getResources().getStringArray(R.array.languages));
        List<String> lang = new ArrayList<>();

        for(LanguageEntity entity : userEntity.getPerson().getLanguages())
            lang.add(langArray.get(entity.getId() - 1));

        if(userEntity.getPerson().getLanguages().size() == 0)
            languageStatic.setVisibility(View.GONE);

        languageStatic.setText(StringUtils.join(lang.iterator(), ", "));
        //endregion
    }
}