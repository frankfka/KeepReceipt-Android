package com.jiafrank.receipttracker.data;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Receipt extends RealmObject {

    @PrimaryKey
    private int receiptId;
    private String vendor;
    private double amount;
    @LinkingObjects("receipts")
    private final RealmResults<Category> parentCategories;

}
