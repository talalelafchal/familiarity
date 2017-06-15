package br.com.stardapio.stardapiomobile;

import java.util.HashSet;
import java.util.Set;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.widget.Toast;
import br.com.stardapio.stardapiomobile.fragment.MapFragment;

public class StarDapioActivity extends FragmentActivity {

	private AccountManager mAccountManager;
	private CharSequence[] names;
	private CharSequence selected;
	private static final String ADD_ACCOUNT = "Adicionar conta";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		MapFragment mapFragment = new MapFragment();

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.map, mapFragment);
		transaction.commit();

		DisplayAccountDialogFragment displayAccountDialogFragment = new DisplayAccountDialogFragment();
		Bundle args = new Bundle();
		args.putCharSequenceArray("accountNames", getAccountNames());
		displayAccountDialogFragment.setArguments(args);
		displayAccountDialogFragment.show(getSupportFragmentManager(), "displayAccount");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stardapio, menu);
		return true;
	}

	private CharSequence[] getAccountNames() {
		Set<CharSequence> aux;
		mAccountManager = AccountManager.get(this);
		Account[] accounts = mAccountManager.getAccounts();
		aux = new HashSet<CharSequence>();
		for (int i = 0; i < accounts.length; i++) {
			aux.add(accounts[i].name);
		}

		names = (CharSequence[]) aux.toArray(new CharSequence[aux.size()]);
		names[names.length - 1] = ADD_ACCOUNT;
		return names;
	}

	public String getBestProvider() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		return ((LocationManager) getSystemService(Context.LOCATION_SERVICE))
				.getBestProvider(criteria, true);
	}

}