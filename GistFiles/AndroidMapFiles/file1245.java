/*
* modified by: cprakashagr
* date: Dec 01, 2015
* comments: registerWithGoogle method added for Google Plus Login in Android DDP Android  Client
*           Need to override the LoginHandler at calling the remote method login.
*           For details, check the "socialAccountsLoginHandler.js" at server package.
*           
*           registerWithLinkedIn method added for Linked In Login in Android DDP Android Client
*           Need to override the LoginHandler at calling the  remote method login.
*           For details, check the "socialAccountsLoginHandler.js" at server package.
*	    
*	    Repository: https://github.com/delight-im/Android-DDP
*	    Working Example: https://github.com/cprakashagr/Android-DDP
*/

/**
 * Registers a new user with the Google oAuth API
 *
 * This method will automatically login as the new user on success
 *
 * Please note that this requires `accounts-base` package
 *
 * @param email the email to register with. Must be fetched from the Google oAuth Android API
 * @param userId the unique google plus userId. Must be fetched from the  Google oAuth Android API
 * @param idToken the idToken from Google oAuth Android API
 * @param oAuthToken the oAuthToken from Google oAuth Android API for server side validation
 * @param listener the listener to call on success/error
 */
public void registerWithGoogle(final String email, final String userId, final String idToken, final String oAuthToken,final ResultListener listener) {
	final boolean googleLoginPlugin = true;
	
	Map<String, Object> accountData = new HashMap<String, Object>();
	accountData.put("email", email);
	accountData.put("userId", userId);
	accountData.put("idToken", idToken);
	accountData.put("oAuthToken", oAuthToken);
	accountData.put("googleLoginPlugin", googleLoginPlugin);
	
	call("login", new Object[] { accountData }, listener);
}

/**
 * Registers a new user with the Linked In 
 *
 * This method will automatically login as the new user on success
 *
 * Please note that this requires `accounts-base` package
 *
 * @param email the email to register with. Must be fetched from the LinkedIn Android API
 * @param userId the unique LinkedIn id. Must be fetched from the LinkedIn Android API
 * @param firstName the firstName from LinkedIn Android API
 * @param lastName the lastName from LinkedIn Android API
 * @param listener the listener to call on success/error
 * 
 * @param more parameters could be added as per your requirements.
 */
public void registerWithLinkedIn(final String email, final String userId, final String firstName, final String lastName,final ResultListener listener) {
	final boolean linkedInLoginPlugin = true;
	
	Map<String, Object> accountData = new HashMap<String, Object>();
	accountData.put("email", email);
	accountData.put("userId", userId);
	accountData.put("idToken", firstName);
	accountData.put("oAuthToken", lastName);
	accountData.put("googleLoginPlugin", linkedInLoginPlugin);
	
	call("login", new Object[] { accountData }, listener);
}