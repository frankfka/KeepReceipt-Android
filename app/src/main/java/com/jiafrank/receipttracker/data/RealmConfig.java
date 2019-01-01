package com.jiafrank.receipttracker.data;

import io.realm.RealmConfiguration;

public class RealmConfig {

    public static RealmConfiguration getDefaultDatabaseConfig() {
        return new RealmConfiguration.Builder()
                .name("CoreData.realm").build();
    }

}
