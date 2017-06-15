package com.vinaysshenoy.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by vinaysshenoy on 11/05/16.
 */
public final class EventBus {

    private final Map<Type, Subject<Object, Object>> subjectsMap;

    public EventBus() {
        //Assuming around 15 unique event classes.
        subjectsMap = new HashMap<>((int) (15 * 1.33F));
    }

    private Subject<Object, Object> subject(@NonNull Type type) {

        if (!subjectsMap.containsKey(type)) {
            synchronized (this) {
                if (!subjectsMap.containsKey(type)) {
                    subjectsMap.put(type, new SerializedSubject<>(PublishSubject.create()));
                }
            }
        }

        return subjectsMap.get(type);
    }

    @SuppressWarnings("unchecked")
    public <T> Subscription registerSingle(@NonNull final Class<T> type, @NonNull final Action1<T> eventReceiver) {
        return subject(type).observeOn(AndroidSchedulers.mainThread()).subscribe((Action1) eventReceiver);
    }

    public RegistrationBuilder registerMultiple() {
        return new RegistrationBuilder(this);
    }

    public void post(@NonNull Object event) {
        subject(event.getClass()).onNext(event);
    }

    public final class RegistrationBuilder {

        @NonNull
        private final EventBus bus;

        private final List<Pair<Type, Action1<?>>> typeActionList;

        private RegistrationBuilder(@NonNull final EventBus bus) {
            this.bus = bus;
            this.typeActionList = new ArrayList<>(5);
        }

        public <T> RegistrationBuilder addReceiver(@NonNull final Class<T> type, @NonNull final Action1<T> eventReceiver) {
            typeActionList.add(new Pair<Type, Action1<?>>(type, eventReceiver));
            return this;
        }

        @SuppressWarnings("unchecked")
        public Subscription registerAll() {

            final SubscriptionList subscriptionList = new SubscriptionList();

            final int numItems = typeActionList.size();
            Pair<Type, Action1<?>> item;
            for (int i = 0; i < numItems; i++) {
                item = typeActionList.get(i);
                subscriptionList.add(bus.subject(item.first).observeOn(AndroidSchedulers.mainThread()).subscribe((Action1<? super Object>) item.second));
            }
            return subscriptionList;
        }
    }
}
