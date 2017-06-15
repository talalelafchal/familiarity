/**
 * Abstract class containing common validation functionalitites.
 */
public abstract class UserInputValidator {

    /**
     * Set containing all the EditTexts in error.
     */
    protected Set<EditText> viewsInError;

    /**
     * All the validation rules associated to a field.
     */
    protected Map<EditText, Rule> rulesMap;

    public Set<EditText> getViewsInError() {
        return viewsInError;
    }

    /**
     * Default constructor, building validation rules.
     */
    protected UserInputValidator() {
        viewsInError = new HashSet<>();
        rulesMap = new HashMap<>();

        buildRules();
    }

    /**
     * Build validation rules on the fields.
     */
    protected abstract void buildRules();

    /**
     * Verify that the user input is valid.
     *
     * @return True if the input is correct.
     */
    public boolean validateUserInput() {
        for (Map.Entry<EditText, Rule> rule : rulesMap.entrySet()) {
            EditText editText = rule.getKey();
            Rule validationRule = rule.getValue();

            boolean isInputValid = editText.getText() != null && editText.getText().toString().matches(validationRule.regExFormat);

            ViewParent parent = editText.getParent();
            TextInputLayout parentLayout = null;
            String errorMessage;
            if(parent instanceof TextInputLayout) {
                parentLayout = (TextInputLayout) parent;
            }

            if(isInputValid) {
                viewsInError.remove(editText);
                errorMessage = null;
            } else {
                viewsInError.add(editText);
                errorMessage = validationRule.errorMessage;
            }

            if(parentLayout != null) {
                parentLayout.setError(errorMessage);
            } else {
                editText.setError(errorMessage);
            }
        }

        return viewsInError.isEmpty();
    }

    /**
     * A validation rule (wanted format, error message).
     */
    protected static class Rule {

        /**
         * Wanted format expressed as a RegEx.
         */
        final String regExFormat;

        /**
         * The error message shown if the input doesn't match.
         */
        final String errorMessage;

        public Rule(String regExFormat, String errorMessage) {
            this.regExFormat = regExFormat;
            this.errorMessage = errorMessage;
        }
    }
}