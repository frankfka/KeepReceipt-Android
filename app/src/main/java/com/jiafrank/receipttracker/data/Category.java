package com.jiafrank.receipttracker.data;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends RealmObject {

    @PrimaryKey
    private String name;
    private RealmList<Receipt> receipts;

}
