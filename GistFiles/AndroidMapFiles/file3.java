/**
 * The activity containing your EditTexts.
 */
public class ProfileActivity extends AppCompatActivity {
  
    /**
     * Input field "your name".
     */
    @Bind(R.id.edittext_name)
    EditText nameEditText;

    /**
     * Input field "mobile number".
     */
    @Bind(R.id.edittext_mobile_number)
    EditText mobileNumberEditText;
    
    /**
     * Activity-specific user input validator.
     */
    ProfileInputValidator inputValidator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
    }
    
    /**
     * Action when the "validate" button is pressed.
     */
    @OnClick(R.id.button_profile_validate)
    public void validateButtonClick() {
        if (getInputValidator().validateUserInput()) {
            // The EditTexts are filled as wanted
        }
    }
    
    
    // --------------------------- VALIDATOR PART ---------------------------
    
    /**
     * Custom validator definition.
     */
    public class ProfileInputValidator extends UserInputValidator {
        @Override
        protected void buildRules() {
            // Build a rule for each EditText, specifying the wanted format and error message
            rulesMap.put(nameEditText, new Rule(Consts.REGEX_USERNAME, getString(R.string.profile_error_name)));
            rulesMap.put(mobileNumberEditText, new Rule(Consts.REGEX_MOBILE, getString(R.string.profile_error_mobile)));
        }
    }

    public ProfileInputValidator getInputValidator() {
        if (inputValidator == null) {
            inputValidator = new ProfileInputValidator();
        }

        return inputValidator;
    }
    
}