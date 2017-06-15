package ru.kfu.itis.mca.screens.consultant_profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.kfu.itis.mca.R;
import ru.kfu.itis.mca.base.view.LoadingView;
import ru.kfu.itis.mca.dialogs.LoadingDialog;
import ru.kfu.itis.mca.models.Consultant;
import ru.kfu.itis.mca.models.ConsultantCertificate;
import ru.kfu.itis.mca.models.ConsultantDetailsInfo;
import ru.kfu.itis.mca.models.ConsultantStatus;
import ru.kfu.itis.mca.models.Specialties;
import ru.kfu.itis.mca.rx.RxError;
import ru.kfu.itis.mca.rx.loader.LifecycleHandler;
import ru.kfu.itis.mca.rx.loader.LoaderLifecycleHandler;
import ru.kfu.itis.mca.screens.chat.ChatActivity;
import ru.kfu.itis.mca.screens.consultant_certificates.ConsultantCertificatesActivity;
import ru.kfu.itis.mca.screens.stub.StubActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConsultantProfileActivity extends AppCompatActivity implements ConsultantProfileView {

    private static final String EXTRA_CONSULTANT_ID = "extra_consultant_id";

    private static final String EXTRA_CONSULTANT_IS_SHOW_PANEL = "extra_consultant_is_show_panel";

    @BindView(R.id.relative_layout) RelativeLayout mRelativeLayout;

    @BindView(R.id.cv_specialisation) CardView mSpecialisationCardView;

    @BindView(R.id.iv_avatar) ImageView mAvatarImageView;

    @BindView(R.id.tv_status) TextView mStatusTextView;

    @BindView(R.id.tv_consultant_name) TextView mNameTextView;

    @BindView(R.id.tv_about) TextView mAboutTextView;

    @BindView(R.id.tv_rating) TextView mRatingTextView;

    @BindView(R.id.rl_age_container) View mAgeContainer;

    @BindView(R.id.tv_age) TextView mAgeTextView;

    @BindView(R.id.tv_age_postfix) TextView mAgePostfixTextView;

    @BindView(R.id.tv_experience) TextView mExperienceTextView;

    @BindView(R.id.tv_specialisation) TextView mSpecialisationTextView;

    @BindView(R.id.tv_marks_count) TextView mMarksCountTextView;

    @BindView(R.id.tv_certificates_count) TextView mCertificatesCountTextView;

    @BindView(R.id.cv_work_districts) View mWorkDistrictsContainer;

    @BindView(R.id.tv_work_districts) TextView mWorkDistrictsTextView;

    @BindView(R.id.tv_experience_postfix) TextView mExperiencePostfixTextView;

    @BindView(R.id.tv_marks_count_postfix) TextView mMarksCountPostfixTextView;

    @BindView(R.id.tv_certificates_count_postfix) TextView mCertificatesCountPostfixTextView;

    @BindView(R.id.tl_control_panel) View mControlPanel;

    private ConsultantDetailsInfo mConsultant;

    private LoadingView mLoadingView;

    private ConsultantProfilePresenter mPresenter;

    private boolean mIsShowControlPanel;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @NonNull
    public static Intent makeIntent(@NonNull Context context,
                                    long consultantId,
                                    boolean isShowControlPanel) {
        return new Intent(context, ConsultantProfileActivity.class)
                .putExtra(EXTRA_CONSULTANT_ID, consultantId)
                .putExtra(EXTRA_CONSULTANT_IS_SHOW_PANEL, isShowControlPanel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_consultant_profile);
        ButterKnife.bind(this);

        long consultantId = getIntent().getLongExtra(EXTRA_CONSULTANT_ID, 0);
        mIsShowControlPanel = getIntent().getBooleanExtra(EXTRA_CONSULTANT_IS_SHOW_PANEL, true);

        mLoadingView = LoadingDialog.view(getSupportFragmentManager());

        LifecycleHandler lifecycleHandler = LoaderLifecycleHandler.create(this, getSupportLoaderManager());
        mPresenter = new ConsultantProfilePresenter(this, lifecycleHandler, RxError.view(this));

        mPresenter.loadConsultantById(consultantId);
    }

    public void showProfileData(@NonNull ConsultantDetailsInfo consultant) {
        mConsultant = consultant;

        if (!mIsShowControlPanel) {
            mControlPanel.setVisibility(View.INVISIBLE);
        }

        Picasso.with(this)
                .load(consultant.getPhoto())
                .into(mAvatarImageView);

        mStatusTextView.setText(consultant.getStatus().toString());
        mStatusTextView.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(this, consultant.getStatus() == ConsultantStatus.ONLINE ? R.drawable.icon_status_on : R.drawable.icon_status_off),
                null, null, null);
        mRatingTextView.setText(String.format(Locale.getDefault(), "%.1f", Double.parseDouble(consultant.getRating())));
        if (consultant.getAge() != null) {
            mAgeTextView.setText(String.valueOf(consultant.getAge()));
            mAgePostfixTextView.setText(getYearWordForm(consultant.getAge()));
        } else {
            mAgeContainer.setVisibility(View.GONE);
        }
        mExperienceTextView.setText(String.valueOf(consultant.getExperience()));
        mExperiencePostfixTextView.setText(getYearWordForm(consultant.getExperience()));
        mNameTextView.setText(consultant.getName().replace(" ", System.lineSeparator()));
        mAboutTextView.setText(consultant.getAbout());
        mSpecialisationCardView.setCardBackgroundColor(ContextCompat.getColor(this, getConsultantSpecialityColor(consultant)));
        mSpecialisationTextView.setText(consultant.getSpecialisations().get(0).getLabel());
        mMarksCountTextView.setText(String.valueOf(consultant.getMarks().size()));
        mMarksCountPostfixTextView.setText(getMarkWordForm(consultant.getMarks().size()));
        mCertificatesCountTextView.setText(String.valueOf(consultant.getCertificates().size()));
        mCertificatesCountPostfixTextView.setText(getCertificateWordForm(consultant.getCertificates().size()));

        if (consultant.getRegions().isEmpty()) {
            mWorkDistrictsContainer.setVisibility(View.GONE);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < consultant.getRegions().size(); i++) {
                if (i != 0) {
                    stringBuilder.append(System.lineSeparator());
                }
                stringBuilder.append(consultant.getRegions().get(i).getRegionName());
            }
            mWorkDistrictsTextView.setText(stringBuilder.toString());
        }
    }

    @OnClick(R.id.iv_close)
    public void closeClick() {
        finish();
    }

    @OnClick(R.id.btn_message)
    public void messageClick() {
        startActivity(ChatActivity.makeIntent(this,
                new Consultant(mConsultant.getId(), mConsultant.getJid(),
                        mConsultant.getName(), mConsultant.getStatus(),
                        getConsultantSpecialityBorder(mConsultant))));
    }

    @OnClick(R.id.btn_phone)
    public void phoneClick() {
//        startActivity(StubActivity.makeIntent(this, R.string.stub_text_phone));
//        openStub(R.string.stub_text_phone);
    }

    @OnClick(R.id.btn_camera)
    public void cameraClick() {
//        startActivity(StubActivity.makeIntent(this, R.string.stub_text_camera));
//        openStub(R.string.stub_text_camera);
    }

    private void openStub(@StringRes int textId){
        mRelativeLayout.setDrawingCacheEnabled(true);
        Bitmap bm = mRelativeLayout.getDrawingCache();
        startActivity(StubActivity.makeIntent(this, textId, bm));
    }

    @OnClick(R.id.cv_certificates)
    public void onCertificatesClick() {
        startActivity(ConsultantCertificatesActivity.makeIntent(this, (ArrayList<ConsultantCertificate>) mConsultant.getCertificates()));
    }

    @StringRes
    private int getYearWordForm(int years) {
        if (years >= 1 && years <= 4) {
            return R.string.profile_text_year_3;
        }
        return R.string.profile_text_year_1;
    }

    @StringRes
    private int getMarkWordForm(int recallsCount) {
        if (recallsCount % 100 / 10 == 1) return R.string.profile_text_recall_v2;
        int d = recallsCount % 10;
        if (d == 0 || d >= 5) return R.string.profile_text_recall_v2;
        if (d == 2 || d == 3 || d == 4) return R.string.profile_text_recall_v1;
        return R.string.profile_text_recall_v3;
    }

    @StringRes
    private int getCertificateWordForm(int cerifactesCount) {
        if (cerifactesCount % 100 / 10 == 1) return R.string.profile_text_certificate_v2;
        int d = cerifactesCount % 10;
        if (d == 0 || d >= 5) return R.string.profile_text_certificate_v2;
        if (d == 2 || d == 3 || d == 4) return R.string.profile_text_certificate_v1;
        return R.string.profile_text_certificate_v3;
    }

    @DrawableRes
    private int getConsultantSpecialityBorder(@NonNull ConsultantDetailsInfo consultant) {
        return Specialties.getEnum(consultant.getSpecialisations().get(0).getId()).getChatPhotoBorder();
    }

    @ColorRes
    private int getConsultantSpecialityColor(@NonNull ConsultantDetailsInfo consultant) {
        return Specialties.getEnum(consultant.getSpecialisations().get(0).getId()).getColor();
    }

    @Override
    public void showLoadingIndicator() {
        mLoadingView.showLoadingIndicator();
    }

    @Override
    public void hideLoadingIndicator() {
        mLoadingView.hideLoadingIndicator();
    }
}
