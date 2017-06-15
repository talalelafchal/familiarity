package net.vvakame.polkodotter;

import java.util.List;

import com.google.inject.Module;

import roboguice.application.GuiceApplication;

public class MyApplication extends GuiceApplication {
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new MyModule());
	}
}
