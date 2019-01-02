package com.jiafrank.keepreceipt.data;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Receipt extends RealmObject {

    @PrimaryKey
    private String receiptId;
    private String vendor;
    private double amount;
    private Date time;
    @LinkingObjects("receipts")
    private final RealmResults<Category> parentCategories = null;

}
