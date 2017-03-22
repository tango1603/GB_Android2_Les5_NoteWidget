package com.example.ilya.gb_android2_les5_notewidget.models.tables;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.QueryModel;

/**
 * Created by Ilya on 21.03.2017.
 */
@QueryModel(database = AppDataBase.class)
public class TblNotes_List_Model {
    @Column
    public int listPosition;
    @Column
    public String noteName;
    @Column
    public String noteText;
}
