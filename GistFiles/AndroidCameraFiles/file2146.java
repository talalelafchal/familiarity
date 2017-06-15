package com.yifanhao;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.yifanhao.utils.ToolNetwork;

public class MApplication extends Application {

	/** �����ṩ���Ӧ���������ڵ�Context **/
	private static Context instance;
	/** ���Ӧ��ȫ�ֿɷ�����ݼ��� **/
	private static Map<String, Object> gloableData = new HashMap<String, Object>();

	/*** �Ĵ����Ӧ��Activity **/
	private final Stack<WeakReference<Activity>> activitys = new Stack<WeakReference<Activity>>();

	/**
	 * �����ṩApplication Context
	 * 
	 * @return
	 */
	public static Context gainContext() {
		return instance;
	}

	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	/**
	 * ��ȡ�����Ƿ�������
	 * 
	 * @return
	 */
	public static boolean isNetworkReady() {
		return ToolNetwork.getInstance().init(instance).isConnected();
	}

	/******************************************************* Application��ݲ���API����ʼ�� ********************************************************/

	/**
	 * ��Application������ݣ�������?��5����
	 * 
	 * @param strKey
	 *            �������Key
	 * @param strValue
	 *            ��ݶ���
	 */
	public static void assignData(String strKey, Object strValue) {
		if (gloableData.size() > 5) {
			throw new RuntimeException("�������������");
		}
		gloableData.put(strKey, strValue);
	}

	/**
	 * ��Applcaiton��ȡ���
	 * 
	 * @param strKey
	 *            ������Key
	 * @return ��ӦKey����ݶ���
	 */
	public static Object gainData(String strKey) {
		return gloableData.get(strKey);
	}

	/*
	 * ��Application���Ƴ����
	 */
	public static void removeData(String key) {
		if (gloableData.containsKey(key))
			gloableData.remove(key);
	}

	/******************************************************* Application��ݲ���API������ ********************************************************/

	/******************************************* Application�д�ŵ�Activity������ѹջ/��ջ��API����ʼ�� *****************************************/

	/**
	 * ��Activityѹ��Applicationջ
	 * 
	 * @param task
	 *            ��Ҫѹ��ջ��Activity����
	 */
	public void pushTask(WeakReference<Activity> task) {
		activitys.push(task);
	}

	/**
	 * �������Activity�����ջ���Ƴ�
	 * 
	 * @param task
	 */
	public void removeTask(WeakReference<Activity> task) {
		activitys.remove(task);
	}

	/**
	 * ���ָ��λ�ô�ջ���Ƴ�Activity
	 * 
	 * @param taskIndex
	 *            Activityջ����
	 */
	public void removeTask(int taskIndex) {
		if (activitys.size() > taskIndex)
			activitys.remove(taskIndex);
	}

	/**
	 * ��ջ��Activity�Ƴ���ջ��
	 */
	public void removeToTop() {
		int end = activitys.size();
		int start = 1;
		for (int i = end - 1; i >= start; i--) {
			if (!activitys.get(i).get().isFinishing()) {
				activitys.get(i).get().finish();
			}
		}
	}

	/**
	 * �Ƴ�ȫ�����������Ӧ���˳���
	 */
	public void removeAll() {
		// finish���е�Activity

		for (WeakReference<Activity> task : activitys) {
			if (!task.get().isFinishing()) {
				task.get().finish();
			}
		}
	}

	/******************************************* Application�д�ŵ�Activity������ѹջ/��ջ��API������ *****************************************/
}
