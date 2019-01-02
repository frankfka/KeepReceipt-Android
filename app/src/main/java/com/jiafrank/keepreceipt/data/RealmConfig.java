package com.jiafrank.keepreceipt.data;

import io.realm.RealmConfiguration;

public class RealmConfig {

    public static RealmConfiguration getDefaultDatabaseConfig() {
        return new RealmConfiguration.Builder()
                .name("CoreData.realm").build();
    }

}
